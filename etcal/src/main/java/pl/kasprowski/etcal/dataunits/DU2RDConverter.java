package pl.kasprowski.etcal.dataunits;

import java.util.ArrayList;
import java.util.List;

import pl.kasprowski.etcal.calibration.RegressionData;

public class DU2RDConverter {
	public static RegressionData dataUnits2RegressionData(DataUnits dataUnits) {
		RegressionData data = new RegressionData();
		for(DataUnit unit:dataUnits.getDataUnits()) {
			List<Double> indata = new ArrayList<Double>();
			for(Double ind: unit.getVariables()) {
				indata.add(ind);
			}
			//TODO: wyb�r najbardziej prawdopodobnego?
			Target dep = unit.getTargets().get(0);
			double[] arr = indata.stream().mapToDouble(d -> d).toArray();
			data.addPoint(arr, dep.getX(),dep.getY(), 1/*waga!*/);
			
		}
		return data;

	}

	public static DataUnits regressionData2dataUnits(RegressionData data) {
		DataUnits dataUnits = new DataUnits();
		for(int i=0;i<data.size();i++) {
			double[] x = data.getX(i);
			Target target = new Target(data.getY1(i),data.getY2(i),1);
			DataUnit du = new DataUnit();
			du.addVariables(x);
			du.addTarget(target);
			dataUnits.add(du);
		}
		return dataUnits;
	}


}
