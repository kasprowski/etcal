package pl.kasprowski.etcal.optimizer;

import org.apache.log4j.Logger;

import pl.kasprowski.etcal.calibration.Calibrator;
import pl.kasprowski.etcal.dataunits.DataUnits;

/**
 * Abstract class for optimizers
 * Contains all useful parameters
 * 
 * @author pawel@kasprowski.pl
 *
 */
public abstract class AbstractOptimizer implements Optimizer{
	Logger log = Logger.getLogger(AbstractOptimizer.class);
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
		log.debug("Calculated "+best.toString());
		this.best = best;
	}

	@Override
	public String toString() {
		return getClass().getName()+" cvFolds="+getCvFolds()+" cvType="+getCvType();
	}

}
