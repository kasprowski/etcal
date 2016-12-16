package pl.kasprowski.etcal.optimizer.polynomial;

import pl.kasprowski.etcal.calibration.CalibratorPolynomial;
import pl.kasprowski.etcal.calibration.polynomial.MaskedPolynomialProblem;
import pl.kasprowski.etcal.optimizer.AbstractOptimizer;

/**
 * Optimizer that works with polynomial models
 * 
 * @author pawel@kasprowski.pl
 *
 */
public abstract class PolynomialOptimizer extends AbstractOptimizer{
	/**
	 * Creates the best calibrator for a given mask
	 * @param mask
	 */
	public void setBest(boolean[] mask ) {
		CalibratorPolynomial c = new CalibratorPolynomial();
		c.setMask(MaskedPolynomialProblem.maskAsString(mask));
		setBest(c);
	}

}