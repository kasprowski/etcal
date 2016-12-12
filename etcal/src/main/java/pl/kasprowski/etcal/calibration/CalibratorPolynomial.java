package pl.kasprowski.etcal.calibration;



import org.apache.commons.math3.exception.TooManyEvaluationsException;
import org.apache.commons.math3.optimization.PointVectorValuePair;
import org.apache.commons.math3.optimization.general.LevenbergMarquardtOptimizer;
import org.apache.log4j.Logger;

import pl.kasprowski.etcal.calibration.polynomial.MaskedPolynomialProblem;
import pl.kasprowski.etcal.calibration.polynomial.PolynomialProblem;
import pl.kasprowski.etcal.dataunits.DU2RDConverter;
import pl.kasprowski.etcal.dataunits.DataUnits;
import pl.kasprowski.etcal.dataunits.Target;

/**
 * Calibrates using mask
 * @author pawel@kasprowski.pl
 *
 */
@SuppressWarnings("deprecation")
public class CalibratorPolynomial implements Calibrator{
	Logger log = Logger.getLogger(CalibratorPolynomial.class);
	boolean calculated = false;
	int maxEval = 100;
	public void setMaxEval(Integer maxEval) {this.maxEval = maxEval;}

	PolynomialProblem problem;
	double[] variablesX;
	double[] variablesY;

	private String mask;
	public String getMask() {return mask;}
	public void setMask(String mask) {this.mask = mask;}


	/**
	 * Default constructor with all terms
	 * @param data
	 */
	public CalibratorPolynomial() {
	}


	public void calculate(DataUnits dataUnits) {
		RegressionData data = DU2RDConverter.dataUnits2RegressionData(dataUnits);
		if(mask==null) {
			StringBuffer bmask= new StringBuffer("");
			for(int i=0;i<MaskedPolynomialProblem.termsNum(data.xNum());i++) bmask.append("1");
			this.mask = bmask.toString();
		}
		this.problem = new MaskedPolynomialProblem(data,MaskedPolynomialProblem.maskFromString(mask));

		calculated = false;
		try{
			calculate(data, 0);
			calculate(data, 1);
			calculated =  true;
		}catch(TooManyEvaluationsException ex) {
			log.warn("Too many evaluations exception mask="+mask);
		}

	}
	public void calculate(RegressionData data, int axis) {
		calculated = false;

		LevenbergMarquardtOptimizer optimizer = new LevenbergMarquardtOptimizer();
		int solutionSize = problem.termsNum();

		if(problem instanceof MaskedPolynomialProblem)
			solutionSize = ((MaskedPolynomialProblem)problem).maskedNum();
		final double[] initialSolution = new double[solutionSize];

		for(int i=0;i<initialSolution.length;i++) initialSolution[i]=1; //{1, 1, 1, 1, 1};
		
		log.trace("Start");
		PointVectorValuePair optimum = optimizer.optimize(maxEval, //100000,
				problem,
				(axis==0)?
						(data.getY1().stream().mapToDouble(d -> d).toArray()):
							(data.getY2().stream().mapToDouble(d -> d).toArray()),
							(data.getWeights().stream().mapToDouble(d -> d).toArray())
							,
							initialSolution);
		log.trace("Done");
		if(axis==0)
			variablesX = optimum.getPoint();
		else
			variablesY = optimum.getPoint();
		//		showVariables(axis);

	}

	public void showVariables() {
		char var = 'A';
		log.trace("X:");
		for(double v:variablesX)
			log.trace((var++)+": " + v);
		var = 'A';
		log.trace("Y:");
		for(double v:variablesY)
			log.trace((var++)+": " + v);
	}


	public Target getValue(double[] x) {
		if(!calculated) 
			throw new RuntimeException("Model not calculated!");

		if(variablesX ==null || variablesY==null)
			throw new RuntimeException("Parameters not calculated yet!");
		double sx = problem.value(x,variablesX);
		double sy = problem.value(x,variablesY);
		Target dv = new Target(sx,sy,1);
		return dv;
	}

	@Override
	public String toString() {
		return getClass().getName()+" mask="+getMask();
	}
}
