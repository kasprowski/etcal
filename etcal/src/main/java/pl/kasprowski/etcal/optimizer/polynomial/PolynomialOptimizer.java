package pl.kasprowski.etcal.optimizer.polynomial;

import pl.kasprowski.etcal.calibration.CalibratorPolynomial;
import pl.kasprowski.etcal.calibration.polynomial.MaskedPolynomialProblem;
import pl.kasprowski.etcal.optimizer.AbstractOptimizer;

public abstract class PolynomialOptimizer extends AbstractOptimizer{

//	private boolean[] mask;
//	public boolean[] getMask() {return mask;}
//	public void setMask(boolean[] mask) {this.mask = mask;}


	
	public void setBest(boolean[] mask ) {
		CalibratorPolynomial c = new CalibratorPolynomial();
		c.setMask(MaskedPolynomialProblem.maskAsString(mask));
//		c.getParams().put("mask",MaskedPolynomialProblem.maskAsString(mask));
		setBest(c);
	}
	//public abstract boolean[] findBestMask(RegressionData data, int cvSets) throws Exception;

}