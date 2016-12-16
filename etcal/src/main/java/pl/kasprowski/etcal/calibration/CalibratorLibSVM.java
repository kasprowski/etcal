package pl.kasprowski.etcal.calibration;

import pl.kasprowski.etcal.dataunits.DataUnits;
import weka.classifiers.Classifier;
import weka.classifiers.functions.LibSVM;
import weka.core.SelectedTag;

/**
 * Calibrator that uses LibSVM to build a model
 * It has two input parameters: expCost and expGamma
 * @author pawel@kasprowski.pl
 *
 */
public class CalibratorLibSVM extends CalibratorWeka {

	private Integer expCost;
	private Integer expGamma;
	public void setExpCost(Integer cost) {this.expCost = cost;}
	public void setExpGamma(Integer gamma) {this.expGamma = gamma;}

	@Override
	public void calculate(DataUnits dataUnits)  {
		LibSVM svm = new LibSVM();
		svm.setSVMType(new SelectedTag(LibSVM.SVMTYPE_NU_SVR,LibSVM.TAGS_SVMTYPE));
		svm.setKernelType(new SelectedTag(LibSVM.KERNELTYPE_RBF,LibSVM.TAGS_KERNELTYPE));
		//svm.setEps(0.000001);
		svm.setShrinking(false);
		svm.setCost(Math.pow(2, expCost));
		svm.setGamma(Math.pow(2,expGamma));
		this.classifierX = svm;
		try {
			this.classifierY = Classifier.makeCopy(svm);
		} catch (Exception e) {
			e.printStackTrace();
		}
		super.calculate(dataUnits);
	}
	
	@Override
	public String toString() {
		return getClass().getName()+" expCost:"+expCost+" expGamma:"+expGamma;
	}


}
