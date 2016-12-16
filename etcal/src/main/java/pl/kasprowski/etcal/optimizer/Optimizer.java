package pl.kasprowski.etcal.optimizer;

import pl.kasprowski.etcal.calibration.Calibrator;
import pl.kasprowski.etcal.dataunits.DataUnits;

/**
 * Interface for all optimizers
 * 
 * @author pawel@kasprowski.pl
 *
 */
public interface Optimizer {
	
	/**
	 * Tries to find the best calibrator for given training data
	 * @param data
	 * @throws Exception
	 */
	public abstract void optimize(DataUnits data) throws Exception;
	
	/**
	 * Returns the best calibrator.
	 * If the optimize method still works it should return the best calibrator found so far!
	 * 
	 * @return
	 */
	public abstract Calibrator getBest();

}
