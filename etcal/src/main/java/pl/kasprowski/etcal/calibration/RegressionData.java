package pl.kasprowski.etcal.calibration;

import java.util.ArrayList;
import java.util.List;

/**
 * Used to store data needed for calibration
 * Created from DataUnits object but contrary to DataUnits there is only one Target taken for each reading
 * Target in DataUnits corresponds to y1 and y2 lists
 * The class is created just for convenience - no need to use it in your own calibrators
 * 
 * @author pawel@kasprowski.pl
 *
 */
public class RegressionData {
	List<double[]> x = new ArrayList<double[]>();
	List<Double> y1;
	List<Double> y2;
	List<Double> weights;

	public RegressionData() {
		reset();
	}

	public List<double[]> getX() {
		return x;
	}
	public List<Double> getY1() {
		return y1;
	}
	public List<Double> getY2() {
		return y2;
	}
	public List<Double> getWeights() {
		return weights;
	}

	public double[] getX(int i) {
		return x.get(i);
	}
	public Double getY1(int i) {
		return y1.get(i);
	}
	public Double getY2(int i) {
		return y2.get(i);
	}
	public Double getWeights(int i) {
		return weights.get(i);
	}

	public void reset() {
		x = new ArrayList<double[]>();
		y1 = new ArrayList<Double>();
		y2 = new ArrayList<Double>();
		weights = new ArrayList<Double>();
	}
	public int size() { return x.size(); }

	public int xNum() {return x.get(0).length; }

	public void addPoint(double[] x, double y1, double y2,double weight) {
		this.x.add(x);
		this.y1.add(y1);
		this.y2.add(y2);
		this.weights.add(weight);
	}

	/**
	 * Merges list RegressionData objects into one object
	 * @param list
	 * @return
	 */
	public static RegressionData merge(List<RegressionData> list) {
		RegressionData data = new RegressionData();
		for(RegressionData r:list) {
			for(int i=0;i<r.size();i++) {
				data.addPoint(r.getX(i), r.getY1(i), r.getY2(i), r.getWeights(i));
			}
		}
		return data;
	}
}
