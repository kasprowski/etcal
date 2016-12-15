package pl.kasprowski.etcal;

import java.util.Map;
import java.util.concurrent.Future;

import pl.kasprowski.etcal.calibration.Calibrator;
import pl.kasprowski.etcal.dataunits.DataUnits;
import pl.kasprowski.etcal.dataunits.Target;
import pl.kasprowski.etcal.evaluation.Errors;
import pl.kasprowski.etcal.filters.Filter;
import pl.kasprowski.etcal.helpers.ObjDef;
import pl.kasprowski.etcal.mapper.Mapper;
import pl.kasprowski.etcal.optimizer.Optimizer;

/**
 * Main interface containing all methods necessary to use the library 
 * @author pawel@kasprowski.pl
 *
 */
public interface IETCal {

	/**
	 * Adds data
	 * @param dataUnits
	 */
	public void add(DataUnits dataUnits);
	/**
	 * Removes all data
	 */
	public void reset();
	
	/**
	 * Adds a generic filter defined by CalPar object
	 * Filter must implement pl.kasprowski.calsoft.filters.CalFilter interface
	 * @param param object with all information necessary to build a filter
	 * @throws Exception
	 */
	public void addFilter(ObjDef param) throws Exception;
	/**
	 * Adds filter implementing pl.kasprowski.calsoft.filters.CalFilter interface
	 * @param calFilter
	 */
	public void addFilter(Filter calFilter);
	/**
	 * Removes all filters
	 */
	public void resetFilters();
	

	public void useFilter(ObjDef param) throws Exception;
	public void useFilter(Filter calFilter);
	/**
	 * Builds calibration model using a generic calibrator and data added by add() method
	 * Calibrator must extend pl.kasprowski.calibration.Calibrator class 
	 * @param params object with all information necessary to build a calibrator
	 */
	public void build(ObjDef params) throws Exception;
	public Future<Void> buildAsync(ObjDef params, Callback callback) throws Exception;
	
	/**
	 * Builds calibration model using calibrator and data added by add() method
	 * Calibrator must extend pl.kasprowski.calibration.Calibrator class
	 * @param calibrator
	 */
	public void build(Calibrator calibrator) throws Exception;
	public Future<Void> buildAsync(Calibrator calibrator, Callback callback) throws Exception;


	/**
	 * Calculates target (gaze point on screen) for given set of parameters 
	 * @param x list of variables to calculate target
	 * @return Target - gaze point coordinates
	 */
	public Target get(double[] x);
	
	/**
	 * Calculates targets (gaze points on screen) for every DataUnit object from dataUnitsToCalibrate
	 * Attention! Targets in dataUnitsToCalibrate are not taken into account
	 * @param dataUnitsToCalibrate set of points to calibrate
	 * @return DataUnits - every DataUnit contains one Target - gaze point coordinates
	 */
	public DataUnits get(DataUnits dataUnitsToCalibrate);
	
	/**
	 * Calculates Errors for given dataUnits using current calibration model
	 * @param dataUnits
	 * @return error object
	 */
	public Errors checkErrors(DataUnits dataUnits);
	
	/**
	 * Builds optimizer using CalPar object and runs it for current data
	 * @param params object with all information necessary to build an optimizer
	 */
	public void optimize(ObjDef params) throws Exception;

	/**
	 * Builds optimizer using CalPar object and runs anynchronously it for current data
	 * @param params object with all information necessary to build an optimizer 
	 * @param callback 
	 */
	public Future<Void> optimizeAsync(ObjDef params, Callback callback) throws Exception;

	public void optimize(Optimizer optimizer) throws Exception;
	public Future<Void> optimizeAsync(Optimizer optimizer, Callback callback) throws Exception;

	
	
	/**
	 * Builds mapper 
	 * @param mapperName
	 * @param params
	 */
	public void mapTargets(ObjDef params) throws Exception;
	public Future<Void> mapTargetsAsync(ObjDef params, Callback callback) throws Exception;
	
	public void mapTargets(Mapper mapper) throws Exception;
	public Future<Void> mapTargetsAsync(Mapper mapper, Callback callback) throws Exception;
	
	/**
	 * Returns information zbout the current state of the object
	 * @return map with all information
	 */
	public Map<String,String> getStatusInfo();
	public String getStatusInfoJSON();

	
	/**
	 * Flag checked when object is building calibration model 
	 * @return if is calibrating
	 */
	public boolean isCalibrating();

	/**
	 * Flag checked when object is optimizing calibrator 
	 * @return if is calibrating
	 */
	public boolean isOptimizing();

	/**
	 * Flag checked when object is building a mapper
	 * @return if is calibrating
	 */
	public boolean isMapping();
	
	//?? 
}
