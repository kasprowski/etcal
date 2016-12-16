package pl.kasprowski.etcal.filters;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import pl.kasprowski.etcal.calibration.RegressionData;
import pl.kasprowski.etcal.dataunits.DU2RDConverter;
import pl.kasprowski.etcal.dataunits.DataUnits;

/**
 * Takes all subsequent readings with the same target, 
 * calculates median value for each variable and replaces
 * all these readings by one median reading.
 * Only first Target is used!
 * @author pawel@kasprowski.pl
 *
 */

//TODO: maybe check the list of targets and medianize only when
// all targets the same
public class AoiMedianizeFilter implements Filter {

	/**
	 * Takes only sequences longer than minSize points
	 */
	private int minSize;
	public int getMinSize() {return minSize;}
	public void setMinSize(int minSize) {this.minSize = minSize;}

	@Override
	public DataUnits filter(DataUnits dataUnits) {
		RegressionData data = DU2RDConverter.dataUnits2RegressionData(dataUnits);
		RegressionData newdata = new RegressionData();
		Double lastY1 = null;
		Double lastY2 = null;
		List<double[]> points = new ArrayList<double[]>();
		for(int i=0;i<data.size();i++) {
			if(data.getY1(i).equals(lastY1) && data.getY2(i).equals(lastY2)) {
				points.add(data.getX(i));
			}
			else {
				if(points.size()>minSize) {
					double[] p = medianize(points);
					newdata.addPoint(p, lastY1, lastY2, points.size());
				}
				lastY1 = data.getY1(i);
				lastY2 = data.getY2(i);
				points = new ArrayList<double[]>();

			}
		}

		if(points.size()>minSize) {
			double[] p = medianize(points);
			newdata.addPoint(p, lastY1, lastY2, points.size());
		}

		
		return DU2RDConverter.regressionData2dataUnits(newdata);
	}

	double[] medianize(List<double[]> points) {
		int size = points.get(0).length;
		double[] res = new double[size];
		for(int i=0; i<size; i++) {
			List<Double> lista = new ArrayList<Double>();
			for(double[] r:points) {
				lista.add(r[i]);
			}
			res[i] = median(lista);
		}
		return res;
	}

	public static double median(List<Double> x) {
		Collections.sort(x);
		return x.get(x.size()/2);

	}


}
