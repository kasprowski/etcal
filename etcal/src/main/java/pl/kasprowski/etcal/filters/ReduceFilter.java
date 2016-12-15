package pl.kasprowski.etcal.filters;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import pl.kasprowski.etcal.dataunits.DataUnit;
import pl.kasprowski.etcal.dataunits.DataUnits;
import pl.kasprowski.etcal.dataunits.Target;

/**
 * For every reading calculates median of all variables 
 * taking into account <code>len</code> neighboring readings
 * @author pawel@kasprowski.pl
 *
 */
public class ReduceFilter implements Filter {

	private int len;
	
	public int getLen() {
		return len;
	}

	public void setLen(int len) {
		this.len = len;
	}

	@Override
	public DataUnits filter(DataUnits dataUnits) {
		
//		RegressionData data = DU2RDConverter.dataUnits2RegressionData(dataUnits);
//		RegressionData newdata = new RegressionData();

		int size = dataUnits.getDataUnits().size();
		DataUnits newDataUnits = new DataUnits();
		DataUnit newDu = null;
		
		for(int i=0;i<size-len-1;i+=len) {
			
			List<double[]> points = new ArrayList<double[]>();
			List<Double> y1 = new ArrayList<Double>();
			List<Double> y2 = new ArrayList<Double>();
			for(int j=i;j<i+len;j++) {
				DataUnit oldDu = dataUnits.getDataUnits().get(j);
				points.add(oldDu.getVariablesAsTable());
//				y1.add(data.getY1(j));
//				y2.add(data.getY2(j));
				y1.add(oldDu.getTargets().get(0).getX()); //TODO: only the first target is taken!
				y2.add(oldDu.getTargets().get(0).getY());

			}
			double[] p = medianize(points);
			double ny1 = median(y1);
			double ny2 = median(y2);
//			newdata.addPoint(p, ny1, ny2, 1);
			newDu = new DataUnit();
			
			newDu.addVariables(p);
			newDu.addTarget(new Target(ny1,ny2,1)); //TODO: what about weight?
	//		for(int j=0;j<len;j++) //to be able to compare
				newDataUnits.add(newDu);

		}
//		for(int j=0;j<len;j++)	newDataUnits.add(newDu); //tail
	return newDataUnits; //DU2RDConverter.regressionData2dataUnits(newdata);
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
