package pl.kasprowski.etcal.mapper;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.math3.exception.TooManyEvaluationsException;
import org.apache.log4j.Logger;
import org.jgap.FitnessFunction;
import org.jgap.IChromosome;

import pl.kasprowski.etcal.calibration.CalibratorPolynomial;
import pl.kasprowski.etcal.calibration.RegressionData;
import pl.kasprowski.etcal.calibration.polynomial.MaskedPolynomialProblem;
import pl.kasprowski.etcal.dataunits.DU2RDConverter;
import pl.kasprowski.etcal.dataunits.DataUnits;
import pl.kasprowski.etcal.evaluation.Evaluator;

/**
 * Class used by GeneticMapper to evaluate different mappings
 * 
 * @author pawel@kasprowski.pl
 *
 */
public class MappingFittnessFunction extends FitnessFunction {
	static Logger log = Logger.getLogger(MappingFittnessFunction.class);
	
	private static final long serialVersionUID = 1L;
	Map<List<Integer>,Double> alreadyCalculated = new HashMap<List<Integer>,Double>();
	DataUnits data;
	public MappingFittnessFunction(DataUnits data) {
		this.data = data;
		log.trace("Fittness function created");
	}
	@Override
	protected double evaluate(IChromosome chromosome) {
		List<Integer> mapping = GeneticMapper.chromosomeToMapping(chromosome); 
		for(List<Integer> ch: alreadyCalculated.keySet())
			if(ch.equals(mapping)) {
			log.trace("Chromosome "+mapping+" already calculated with value "+1/alreadyCalculated.get(mapping)+"!");
			return 1/alreadyCalculated.get(mapping);
		}


		//add to avoid duplicates
//		Set<Integer> v = new HashSet<Integer>();
//		for(Integer i:mapping) v.add(i);
//		if(v.size()!=mapping.size()) return 0;
		
		try{

			DataUnits datau = MapTargets.mapTargets(data, mapping);
			RegressionData rData = DU2RDConverter.dataUnits2RegressionData(datau);

			StringBuffer bmask = new StringBuffer("");
			for(int i=0;i<MaskedPolynomialProblem.termsNum(rData.xNum());i++) bmask.append("0");
			for(int i=bmask.length()-rData.xNum()-1;i<bmask.length();i++) bmask.replace(i, i+1, "1");
			String mask = bmask.toString();

			CalibratorPolynomial calp = new CalibratorPolynomial();
			calp.setMask(mask);

			Evaluator e = new Evaluator();
			long ts = System.currentTimeMillis();

			double error = Double.MAX_VALUE;
			error = e.calculate(calp, datau, 1, 0).getAbsError();

			log.trace("checking: "+mapping+ 
					" time = "+(System.currentTimeMillis()-ts)+
					" result = "	+ new DecimalFormat("#.###").format(1/error));
			alreadyCalculated.put(mapping, error);
			return 1/error;
		}catch(TooManyEvaluationsException ex) {log.trace("Problem too complicated, skipping...");}
		catch(Exception ex) {log.trace(ex);}
		alreadyCalculated.put(mapping, 0.0);
		return 0.0;

	}
}