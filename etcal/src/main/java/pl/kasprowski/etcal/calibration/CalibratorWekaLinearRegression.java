package pl.kasprowski.etcal.calibration;

import weka.classifiers.Classifier;
import weka.classifiers.functions.LinearRegression;

/**
 * Classifier that uses WEKA's LinearRegression classifier
 * 
 * @author pawel@kasprowski.pl
 *
 */
public class CalibratorWekaLinearRegression extends CalibratorWeka {

	public CalibratorWekaLinearRegression() throws Exception {
		super();
		this.classifierX = new LinearRegression();
		try {
			this.classifierY = Classifier.makeCopy(classifierX);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}


}
