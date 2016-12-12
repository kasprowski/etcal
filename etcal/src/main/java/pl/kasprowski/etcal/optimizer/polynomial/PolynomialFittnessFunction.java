package pl.kasprowski.etcal.optimizer.polynomial;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.math3.exception.TooManyEvaluationsException;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.jgap.FitnessFunction;
import org.jgap.Gene;
import org.jgap.IChromosome;

import pl.kasprowski.etcal.calibration.CalibratorPolynomial;
import pl.kasprowski.etcal.calibration.polynomial.MaskedPolynomialProblem;
import pl.kasprowski.etcal.dataunits.DataUnits;
import pl.kasprowski.etcal.evaluation.Evaluator;

public class PolynomialFittnessFunction extends FitnessFunction {
	static Logger log = Logger.getLogger(PolynomialFittnessFunction.class);
	static{
		log.setLevel(Level.TRACE);
	}
	private static final long serialVersionUID = 1L;

	Map<IChromosome,Double> alreadyCalculated = new HashMap<IChromosome,Double>();
	DataUnits data;
	int cvSets;
	int cvType; 
	public PolynomialFittnessFunction(DataUnits data, int cvSets, int cvType) {
		this.data = data;
		this.cvSets = cvSets;
		this.cvType = cvType;
	}
	@Override
	protected double evaluate(IChromosome chromosome) {
		if(alreadyCalculated.keySet().contains(chromosome)) {
			log.trace("Chromosome already calculated!");
			return alreadyCalculated.get(chromosome);
		}


		boolean[] mask = new boolean[chromosome.getGenes().length];
		for(int i=0;i<chromosome.getGenes().length;i++) {
			Gene gene = chromosome.getGene(i);
			mask[i] = (boolean)gene.getAllele();
		}

		try{

			CalibratorPolynomial calp = new CalibratorPolynomial();
			calp.setMask(MaskedPolynomialProblem.maskAsString(mask));

			Evaluator e = new Evaluator();
			long ts = System.currentTimeMillis();

			double error = Double.MAX_VALUE;
			error = e.calculate(calp, data, cvSets, cvType).getAbsError();
			//		ex.printStackTrace();

			log.trace("checking: "+MaskedPolynomialProblem.maskAsString(mask)+ 
					" time = "+(System.currentTimeMillis()-ts)+
					" result = "	+ new DecimalFormat("#.###").format(error));
			alreadyCalculated.put(chromosome, 1/error);

			return 1/error;
		}catch(TooManyEvaluationsException ex) {log.trace("Problem too complicated, skipping...");}
		catch(Exception ex) {log.trace(ex);}
		alreadyCalculated.put(chromosome, 0.0);
		return 0.0;

	}
}