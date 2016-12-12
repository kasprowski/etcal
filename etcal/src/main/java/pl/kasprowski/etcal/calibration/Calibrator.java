package pl.kasprowski.etcal.calibration;

import pl.kasprowski.etcal.dataunits.DataUnits;
import pl.kasprowski.etcal.dataunits.Target;

public interface Calibrator {

	public abstract void calculate(DataUnits data);
	
	public abstract Target getValue(double[] x);
	
}
