package pl.kasprowski.etcal.optimizer;

import pl.kasprowski.etcal.calibration.Calibrator;
import pl.kasprowski.etcal.dataunits.DataUnits;

public interface Optimizer {
	public abstract void optimize(DataUnits data) throws Exception;
	public abstract Calibrator getBest();

}
