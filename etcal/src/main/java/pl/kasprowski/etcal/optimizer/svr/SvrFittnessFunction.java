package pl.kasprowski.etcal.optimizer.svr;

import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.jgap.FitnessFunction;
import org.jgap.IChromosome;

import pl.kasprowski.etcal.calibration.CalibratorLibSVM;
import pl.kasprowski.etcal.dataunits.DataUnits;
import pl.kasprowski.etcal.evaluation.Evaluator;

public class SvrFittnessFunction extends FitnessFunction {
	Logger log = Logger.getLogger(SvrFittnessFunction.class);
	
	private static final long serialVersionUID = 1L;

	Map<int[],Double> alreadyCalculated = new HashMap<int[],Double>();

	DataUnits data;
	int cvSets;
	int cvType;
	public SvrFittnessFunction(DataUnits data, int cvSets, int cvType) {
		this.data = data;
		this.cvSets = cvSets;
		this.cvType = cvType;
	}
	@Override
	protected double evaluate(IChromosome chromosome) {

		Integer cost = (Integer)chromosome.getGene(0).getAllele();
		Integer gamma = (Integer)chromosome.getGene(1).getAllele();
	
		for(int[] a:alreadyCalculated.keySet())
		if(Arrays.equals(a, new int[]{cost, gamma})) {
			log.trace("Chromosome already calculated! cost="+cost+" gamma="+gamma);
			return alreadyCalculated.get(a);
		}


		try {
			CalibratorLibSVM cw = new CalibratorLibSVM();
			cw.setExpCost(cost);
			cw.setExpGamma(gamma);

			long ts = System.currentTimeMillis();
			Evaluator e = new Evaluator();
			double error = e.calculate(cw, data, cvSets, cvType).getAbsError();
			alreadyCalculated.put(new int[]{cost, gamma}, 1/error);

			log.trace("checked: cost="+cost+" gamma="+gamma+ 
					" time = "+(System.currentTimeMillis()-ts)+
					" result = "	+ new DecimalFormat("#.####").format(error));

			return 1/error;
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		alreadyCalculated.put(new int[]{cost, gamma}, 0.0);
		return 0;
	}
}

