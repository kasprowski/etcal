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
public class MedianFilter implements CalFilter {

	int len;
	public MedianFilter setLen(int len) {
		this.len = len;
		return this;
	}
	@Override
	public DataUnits filter(DataUnits dataUnits) {
		//RegressionData data = DU2RDConverter.dataUnits2RegressionData(dataUnits);
		//RegressionData newdata = new RegressionData();
		int size = dataUnits.getDataUnits().size();
		
		DataUnits newDataUnits = new DataUnits();
		
		for(int i=0;i<len/2;i++) { 
			//newdata.addPoint(data.getX(i), data.getY1(i), data.getY2(i), 1);
			DataUnit oldDu = dataUnits.getDataUnits().get(i);
			DataUnit newDu = new DataUnit();
			newDu.addVariables(oldDu.getVariables());
			for(Target t:oldDu.getTargets())
				newDu.addTarget(new Target(t.getX(),t.getY(),t.getW()));
			newDataUnits.add(newDu);
		}

		for(int i=len/2;i<size-len/2;i++) {
			DataUnit oldDu = dataUnits.getDataUnits().get(i);
			List<double[]> points = new ArrayList<double[]>();
			for(int j=-len/2;j<=len/2;j++) {
				DataUnit oldDuX = dataUnits.getDataUnits().get(i+j);
					
				//points.add(data.getX(i+j));
				points.add(oldDuX.getVariablesAsTable());
			}
			double[] p = medianize(points);
			
			//newdata.addPoint(p, data.getY1(i), data.getY2(i), 1);
			DataUnit newDu = new DataUnit();
			
			newDu.addVariables(p);
			for(Target t:oldDu.getTargets())
				newDu.addTarget(new Target(t.getX(),t.getY(),t.getW()));
			newDataUnits.add(newDu);

		
		}

		for(int i=size-len/2;i<size;i++) { 
//			newdata.addPoint(data.getX(i), data.getY1(i), data.getY2(i), 1);
			DataUnit oldDu = dataUnits.getDataUnits().get(i);
			DataUnit newDu = new DataUnit();
			newDu.addVariables(oldDu.getVariables());
			for(Target t:oldDu.getTargets())
				newDu.addTarget(new Target(t.getX(),t.getY(),t.getW()));
			newDataUnits.add(newDu);

		}

		
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
