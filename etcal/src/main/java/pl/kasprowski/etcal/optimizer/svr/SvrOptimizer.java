package pl.kasprowski.etcal.optimizer.svr;

import pl.kasprowski.etcal.calibration.CalibratorLibSVM;
import pl.kasprowski.etcal.optimizer.AbstractOptimizer;

public abstract class SvrOptimizer extends AbstractOptimizer{

	int minExpCost = -1;
	int maxExpCost = 5;
	int minExpGamma = -5;
	int maxExpGamma = 5;
	
	public int getMinExpCost() {return minExpCost;}
	public void setMinExpCost(int minExpCost) {this.minExpCost = minExpCost;}
	public int getMaxExpCost() {return maxExpCost;}
	public void setMaxExpCost(int maxExpCost) {this.maxExpCost = maxExpCost;}
	public int getMinExpGamma() {return minExpGamma;}
	public void setMinExpGamma(int minExpGamma) {this.minExpGamma = minExpGamma;}
	public int getMaxExpGamma() {return maxExpGamma;}
	public void setMaxExpGamma(int maxExpGamma) {this.maxExpGamma = maxExpGamma;}


	protected void setBest(int[] p) {
		CalibratorLibSVM cal = new CalibratorLibSVM();
		cal.setExpCost(p[0]);
		cal.setExpGamma(p[1]);
//		cal.getParams().put("expCost",""+p[0]);
//		cal.getParams().put("expGamma",""+p[1]);
		super.setBest(cal);
	}

	@Override
	public String toString() {
		return super.toString() + " expCostRange=["+minExpCost+"-"+maxExpCost+"] expGammaRange=["+getMaxExpGamma()+"-"+getMaxExpGamma()+"]";
	}
}