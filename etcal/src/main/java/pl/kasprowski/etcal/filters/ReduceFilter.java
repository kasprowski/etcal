package pl.kasprowski.etcal.filters;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import pl.kasprowski.etcal.dataunits.DataUnit;
import pl.kasprowski.etcal.dataunits.DataUnits;
import pl.kasprowski.etcal.dataunits.Target;

/**
 * For every <em>len</em> DataUnit objects calculates median of all variables
 * and reduces it  to one medianized DataUnit
 * 
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
				y1.add(oldDu.getTargets().get(0).getX()); //TODO: only the first target is taken!
				y2.add(oldDu.getTargets().get(0).getY());

			}
			double[] p = medianize(points);
			double ny1 = median(y1);
			double ny2 = median(y2);
			newDu = new DataUnit();
			
			newDu.addVariables(p);
			newDu.addTarget(new Target(ny1,ny2,1)); //TODO: what about weight?
			newDataUnits.add(newDu);

		}
	return newDataUnits; 
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
