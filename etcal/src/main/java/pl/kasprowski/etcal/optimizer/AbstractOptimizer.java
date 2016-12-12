package pl.kasprowski.etcal.optimizer;

import pl.kasprowski.etcal.calibration.Calibrator;
import pl.kasprowski.etcal.dataunits.DataUnits;

public abstract class AbstractOptimizer implements Optimizer{

	public abstract void optimize(DataUnits data) throws Exception;

	private DataUnits dataUnits;
	public DataUnits getDataUnits() {return dataUnits;}
	public void setDataUnits(DataUnits dataUnits) {this.dataUnits = dataUnits;}
	private int cvFolds = 1; 
	private int cvType = 0;
	public int getCvFolds() {return cvFolds;}
	public void setCvFolds(Integer cvSets) {this.cvFolds = cvSets;}
	public int getCvType() {return cvType;}
	public void setCvType(Integer cvType) {this.cvType = cvType;}

	private Calibrator best;
	public Calibrator getBest() {return best;}


	protected void setBest(Calibrator best) {
		best.calculate(dataUnits);
		this.best = best;
	}

	@Override
	public String toString() {
		return getClass().getName()+" cvFolds="+getCvFolds()+" cvType="+getCvType();
	}

}
