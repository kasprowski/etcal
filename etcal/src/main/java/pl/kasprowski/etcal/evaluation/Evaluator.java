package pl.kasprowski.etcal.evaluation;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import pl.kasprowski.etcal.calibration.Calibrator;
import pl.kasprowski.etcal.calibration.RegressionData;
import pl.kasprowski.etcal.dataunits.DU2RDConverter;
import pl.kasprowski.etcal.dataunits.DataUnits;
import pl.kasprowski.etcal.dataunits.Target;

/**
 * Calculates errors for DataUnit objects in given DataUnits object and calibrator
 * 
 * @author pawel@kasprowski.pl
 *
 */
public class Evaluator {
	static Logger log = Logger.getLogger(Evaluator.class);
	static{
		log.setLevel(Level.ERROR);
	}
	/**
	 * Validation on data itself
	 * @param c
	 * @param data
	 * @return
	 */
	public Errors calculate(Calibrator c,DataUnits data) {
		return calculate(c, data , data);
	}

	/**
	 * Calculates errors using cross validation
	 * @param c
	 * @param data
	 * @param foldsNo
	 * @param cvType
	 * 
	 * @return
	 */
	public Errors calculate(Calibrator c,DataUnits dataUnits, int foldsNo, int cvType) {
		if(foldsNo==0) {
			log.error("foldsNo = "+foldsNo);
			throw new RuntimeException("foldsNo = "+foldsNo);
		}
		if(foldsNo==1) {
			return calculate(c, dataUnits , dataUnits);
		}
		
		RegressionData data = DU2RDConverter.dataUnits2RegressionData(dataUnits);
		List<RegressionData> folds = new ArrayList<RegressionData>();
		for(int i=0;i<foldsNo;i++) folds.add(new RegressionData());

		if(cvType==0) {
			//striped
			int currentFold = 0;
			for(int i=0;i<data.size();i++) {
				folds.get(currentFold).addPoint(data.getX(i), data.getY1(i), data.getY2(i), data.getWeights(i));
				currentFold = (currentFold+1)%foldsNo;
			}
		} else {
			//block
			int singleFoldSize = data.size()/folds.size();
			for(int i=0;i<folds.size();i++) {
				for(int j=0;j<singleFoldSize;j++) {
					int offset = singleFoldSize*i;
					folds.get(i).addPoint(
							data.getX(offset+j), data.getY1(offset+j), data.getY2(offset+j), data.getWeights(offset+j));
				}
			}
		}

		for(int i=0;i<foldsNo;i++) {
			log.trace(i+ " > "+folds.get(i).size());
		}
		List<Errors> le = new ArrayList<Errors>();
		for(int i=0;i<foldsNo;i++) {
			List<RegressionData> trainingFolds = new ArrayList<RegressionData>(folds);
			trainingFolds.remove(i);
			RegressionData t = RegressionData.merge(trainingFolds);
			DataUnits duNoFold = DU2RDConverter.regressionData2dataUnits(t);
			DataUnits duFold = DU2RDConverter.regressionData2dataUnits(folds.get(i));
			
			Errors se = calculate(c, duNoFold , duFold);
			le.add(se);
		}
		return Errors.average(le);


	}
	/**
	 * Method builds calibration model using a given calibrator and trainData
	 * Then it checks error for this model on testData
	 * @param c
	 * @param trainData
	 * @param testData
	 * @return
	 */
	public Errors calculate(Calibrator c,DataUnits trainData, DataUnits testData) {
		log.trace("trainSize: "+trainData.size()+ " testSize: "+testData.size());

		//c.setData(trainData);
		c.calculate(trainData);
		RegressionData rTestData = DU2RDConverter.dataUnits2RegressionData(testData);
		List<Target> realPts = new ArrayList<Target>();
		List<Target> calibPts = new ArrayList<Target>();
		for(int i=0;i<testData.size();i++) {
			Target calPoint = c.getValue(rTestData.getX().get(i));
			Target realPoint = new Target(rTestData.getY1().get(i),rTestData.getY2().get(i),1);
			realPts.add(realPoint);
			calibPts.add(calPoint);
		}
		CalcErrors ce = new CalcErrors();
		double[] e = ce.calcErrors(realPts, calibPts);
		Errors er = new Errors(e);
		return er;
	}
}
