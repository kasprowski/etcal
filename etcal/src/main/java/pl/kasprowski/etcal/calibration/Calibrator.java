package pl.kasprowski.etcal.calibration;

import pl.kasprowski.etcal.dataunits.DataUnits;
import pl.kasprowski.etcal.dataunits.Target;

/**
 * Interface to be implemented by all calibrators
 * @author pawel@kasprowski.pl
 *
 */
public interface Calibrator {
	/**
	 * Builds calibration model based on training data
	 * @param data
	 */
	public abstract void calculate(DataUnits data);
	
	/**
	 * Returns gaze coordinates (Target) based on input variables
	 * Works only after calibrator has been learnt (with calibrate method)
	 * @param x input variables from an eye tracker
	 * @return gaze coordinates
	 */
	public abstract Target getValue(double[] x);
	
}
