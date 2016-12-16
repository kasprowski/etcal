package pl.kasprowski.etcal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.apache.log4j.Logger;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import pl.kasprowski.etcal.calibration.Calibrator;
import pl.kasprowski.etcal.calibration.RegressionData;
import pl.kasprowski.etcal.dataunits.DU2RDConverter;
import pl.kasprowski.etcal.dataunits.DataUnit;
import pl.kasprowski.etcal.dataunits.DataUnits;
import pl.kasprowski.etcal.dataunits.Target;
import pl.kasprowski.etcal.evaluation.Errors;
import pl.kasprowski.etcal.evaluation.Evaluator;
import pl.kasprowski.etcal.filters.Filter;
import pl.kasprowski.etcal.helpers.ObjDef;
import pl.kasprowski.etcal.helpers.ObjectBuilder;
import pl.kasprowski.etcal.mapper.MapTargets;
import pl.kasprowski.etcal.mapper.Mapper;
import pl.kasprowski.etcal.optimizer.AbstractOptimizer;
import pl.kasprowski.etcal.optimizer.Optimizer;

/**
 * The only implementation of IETCal interface
 * @author pawel@kasprowski.pl
 *
 */
public class ETCal implements IETCal{

	Logger log = Logger.getLogger(ETCal.class);

	private DataUnits dataUnits = new DataUnits();
	private List<Filter> filters = new ArrayList<Filter>();

	private Map<String, String> info = new HashMap<String,String>();

	private Calibrator best;
	private Optimizer optimizer;
	private List<Integer> mappings = new ArrayList<Integer>();

	public List<Integer> getMappings() {return mappings;} //TODO: remove

	private final ExecutorService pool = Executors.newFixedThreadPool(10);
	private Future<Void> calibrationProcess;
	private Future<Void> optimizationProcess;
	private Future<Void> mappingProcess;

	@Override
	public void add(DataUnits dataUnits) {
		this.dataUnits.add(dataUnits);
		log.debug("Added "+dataUnits.getDataUnits().size()+" data units");
		log.trace("Total number of data units: "+this.dataUnits.getDataUnits().size());
		info.put("dataUnits", ""+this.dataUnits.getDataUnits().size());
	}

	@Override
	public void reset() {
		dataUnits = new DataUnits();
		log.debug("Removed all data units");		
		info.remove("dataUnits");
	}

	@Override
	public void addFilter(ObjDef params) {
		try{
			Filter filter = (Filter)ObjectBuilder.getObjectFromObjDef(params);
			addFilter(filter);
		}catch(Exception e) {throw new ETCalException(e.getClass().getName()+":"+e.getMessage(),e);}
	}

	@Override
	public void addFilter(Filter calFilter) {
		filters.add(calFilter);
		log.debug("Added "+calFilter+" filter");		
		info.put("filtersNum", ""+filters.size());
		info.put("filters", ""+filters);

	}

	@Override
	public void resetFilters() {
		filters = new ArrayList<Filter>();
		log.debug("All filters removed");		
		info.remove("filtersNum");
		info.remove("filters");

	}


	public void useFilter(ObjDef params) {
		try{
			Filter filter = (Filter)ObjectBuilder.getObjectFromObjDef(params);
			useFilter(filter);
		}catch(Exception e) {throw new ETCalException(e.getClass().getName()+":"+e.getMessage(),e);}
	}
	@Override
	public void useFilter(Filter calFilter) {
		dataUnits = calFilter.filter(dataUnits);
		log.debug("Applied "+calFilter+" filter");		
	}


	@Override
	public void build(ObjDef params) {
		try{
			calibrationProcess = buildAsync(params, null);
			calibrationProcess.get();
		}catch(Exception e) {throw new ETCalException(e.getClass().getName()+":"+e.getMessage(),e);}

	}

	@Override
	public Future<Void> buildAsync(ObjDef params, Callback callback) {
		try{
			Calibrator calibrator = (Calibrator)ObjectBuilder.getObjectFromObjDef(params);
			return buildAsync(calibrator, callback);
		}catch(Exception e) {throw new ETCalException(e.getClass().getName()+":"+e.getMessage(),e);}

	}

	@Override
	public void build(Calibrator calibrator){
		try{
			calibrationProcess = buildAsync(calibrator, null);
			calibrationProcess.get();
		}catch(Exception e) {throw new ETCalException(e.getClass().getName()+":"+e.getMessage(),e);}

	}
	@Override
	public Future<Void> buildAsync(Calibrator calibrator, Callback callback){
		try{
			if(calibrationProcess!=null && !calibrationProcess.isDone()) { 
				log.warn("Terminating the previous calibration process");
				calibrationProcess.cancel(true);
			}

			calibrationProcess = pool.submit(new Callable<Void>() {
				@Override
				public Void call() throws Exception{
					info.put("calibrationPending", "true");

					log.trace("Filtering with "+filters.size()+" filters");
					DataUnits datau = dataUnits;
					for(Filter filter:filters) {  
						log.trace("Applying "+filter.getClass().getName()+" filter");
						datau = filter.filter(datau); 
					}
					datau = MapTargets.mapTargets(datau, mappings);
					log.trace("Building calibrator "+calibrator);
					long startCalibration = System.currentTimeMillis();
					calibrator.calculate(datau);

					best = calibrator;
					info.put("calibrator", best.toString());
					info.put("calibrationTime",""+(System.currentTimeMillis() - startCalibration));

					log.trace("Calibrator "+best.getClass().getName()+" built");
					log.trace("Calibration finished in "+(System.currentTimeMillis() - startCalibration)+" ms");
					if(callback!=null)
						callback.ready();
					info.remove("calibrationPending");
					return null;
				}
			});
			return calibrationProcess;
		}catch(Exception e) {throw new ETCalException(e.getClass().getName()+":"+e.getMessage(),e);}

	}

	@Override
	public Target get(double[] data) {
		try{
			//update calibrator to the best available
			if(optimizationProcess!=null && !optimizationProcess.isDone() 
					&& optimizer.getBest()!=null) {
				best = optimizer.getBest(); 
				info.put("calibrator", best.toString());
				info.remove("calibrationTime");
			}

			if(best!=null) {
				return best.getValue(data); 
			}
		}catch(Exception e) {throw new ETCalException(e.getClass().getName()+":"+e.getMessage(),e);}
		return null;
	}

	@Override
	public DataUnits get(DataUnits dataUnitsToCalibrate) {
		try{
			//update calibrator to the best available
			if(optimizationProcess!=null && !optimizationProcess.isDone() 
					&& optimizer.getBest()!=null) {
				best = optimizer.getBest(); 
				info.put("calibrator", best.toString());
				info.remove("calibrationTime");
			}

			if(best==null) return null;
			RegressionData testData = DU2RDConverter.dataUnits2RegressionData(dataUnitsToCalibrate);
			DataUnits calibrated = new DataUnits();
			for(double[] x:testData.getX()) {
				Target p =  best.getValue(x);
				DataUnit du = new DataUnit();
				du.addVariables(x);
				du.addTarget(p);
				calibrated.add(du);
			}
			return calibrated;
		}catch(Exception e) {throw new ETCalException(e.getClass().getName()+":"+e.getMessage(),e);}

	}


	@Override
	public Errors checkErrors(DataUnits testUnits) {
		try{
			DataUnits datau = null;
			datau = (DataUnits)dataUnits.clone();
			datau = MapTargets.mapTargets(datau, mappings);

			if(optimizationProcess!=null && !optimizationProcess.isDone() 
					&& optimizer.getBest()!=null) {
				best = optimizer.getBest(); 
				info.put("calibrator", best.toString());
				info.remove("calibrationTime");
			}


			Evaluator e = new Evaluator();
			return e.calculate(best, datau, testUnits);
		}catch(Exception e) {throw new ETCalException(e.getClass().getName()+":"+e.getMessage(),e);}

	}

	@Override
	public void optimize(ObjDef params){
		try{
			optimizationProcess = optimizeAsync(params, null);
			optimizationProcess.get();
		}catch(Exception e) {throw new ETCalException(e.getClass().getName()+":"+e.getMessage(),e);}
	}

	@Override
	public Future<Void> optimizeAsync(ObjDef params, Callback callback) {
		try{
			optimizer = (AbstractOptimizer)ObjectBuilder.getObjectFromObjDef(params);
			return optimizeAsync(optimizer, callback);
		}catch(Exception e) {throw new ETCalException(e.getClass().getName()+":"+e.getMessage(),e);}

	}

	@Override
	public void optimize(Optimizer optimizer){
		try{
			optimizationProcess = optimizeAsync(optimizer, null);
			optimizationProcess.get();
		}catch(Exception e) {throw new ETCalException(e.getClass().getName()+":"+e.getMessage(),e);}

	}

	@Override
	public Future<Void> optimizeAsync(Optimizer optimizer, Callback callback) {
		try{
			this.optimizer = optimizer;
			optimizationProcess =  pool.submit(new Callable<Void>() {
				@Override
				public Void call() throws Exception{
					info.put("optimizationPending", "true");
					long startOptimization = System.currentTimeMillis();
					log.trace("Optimization start");

					log.trace("Filtering with "+filters.size()+" filters");
					DataUnits datau = dataUnits;
					for(Filter filter:filters) {  
						log.trace("Applying "+filter.getClass().getName()+" filter");
						datau = filter.filter(datau); 
					}

					try {
						datau = MapTargets.mapTargets(datau, mappings);
						optimizer.optimize(datau);
						best = optimizer.getBest();
						info.put("calibrator", best.toString());
						log.debug("Optimization finished in "+(System.currentTimeMillis() - startOptimization)+" ms");
					} catch (Exception e) {
						//		throw new RuntimeException(e.fillInStackTrace());
						e.printStackTrace();
						log.error("Optimization finished with "+e.getMessage()+" in "+(System.currentTimeMillis() - startOptimization)+" ms");
					}
					info.put("optimizationTime",""+(System.currentTimeMillis() - startOptimization));
					if(callback!=null)
						callback.ready();
					info.remove("optimizationPending");
					return null;
				}
			});
			return optimizationProcess;
		}catch(Exception e) {throw new ETCalException(e.getClass().getName()+":"+e.getMessage(),e);}

	}

	@Override
	public void mapTargets(ObjDef params){
		try{
			mappingProcess = mapTargetsAsync(params, null);
			mappingProcess.get();
		}catch(Exception e) {throw new ETCalException(e.getClass().getName()+":"+e.getMessage(),e);}

	}

	@Override
	public Future<Void> mapTargetsAsync(ObjDef params, Callback callback){
		try{
			Mapper mapper = (Mapper)ObjectBuilder.getObjectFromObjDef(params);
			return mapTargetsAsync(mapper, callback);
		}catch(Exception e) {throw new ETCalException(e.getClass().getName()+":"+e.getMessage(),e);}

	}

	@Override
	public void mapTargets(Mapper mapper){
		try{
			mappingProcess = mapTargetsAsync(mapper, null);
			mappingProcess.get();
		}catch(Exception e) {throw new ETCalException(e.getClass().getName()+":"+e.getMessage(),e);}

	}

	@Override
	public Future<Void> mapTargetsAsync(Mapper mapper, Callback callback) {
		try{
			mappingProcess =  pool.submit(new Callable<Void>() {
				@Override
				public Void call() throws Exception{
					info.put("mappingPending", "true");
					long startMapping = System.currentTimeMillis();
					log.trace("Mapping start");
					try {

						//finds list of best indexes
						mappings = mapper.map(dataUnits);
						log.trace("Mapping finished in "+(System.currentTimeMillis() - startMapping)+" ms");
					} catch (Exception e) {
						//		throw new RuntimeException(e.fillInStackTrace());
						e.printStackTrace();
						log.error("Mapping finished with "+e.getMessage()+" in "+(System.currentTimeMillis() - startMapping)+" ms");
					}
					info.put("mappingTime",""+(System.currentTimeMillis() - startMapping));
					if(callback!=null)
						callback.ready();
					info.remove("mappingPending");
					return null;
				}
			});
			return mappingProcess;
		}catch(Exception e) {throw new ETCalException(e.getClass().getName()+":"+e.getMessage(),e);}

	}

	@Override
	public Map<String, String> getStatusInfo() {
		return info;
	}

	@Override
	public String getStatusInfoJSON() {
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		return gson.toJson(info);
	}

	@Override
	public boolean isCalibrating() {
		return (calibrationProcess!=null && !calibrationProcess.isDone());
	}

	@Override
	public boolean isOptimizing() {
		return (optimizationProcess!=null && !optimizationProcess.isDone());
	}

	@Override
	public boolean isMapping() {
		return (mappingProcess!=null && !mappingProcess.isDone());
	}


}
