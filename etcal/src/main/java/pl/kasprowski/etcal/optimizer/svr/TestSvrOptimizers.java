package pl.kasprowski.etcal.optimizer.svr;

import pl.kasprowski.etcal.dataunits.DataUnits;

public class TestSvrOptimizers {
	public static void main(String[] args) throws Exception{

		//SvrOptimizer fo = new FullSvrOptimizer();
		SvrOptimizer fo = new GeneticSvrOptimizer();
		//FullOptimizer fo = new FullOptimizer();
		DataUnits du = DataUnits.load("data_long.json");

//		CalibratorWeka cw = new CalibLibSVM(data);
//		int cost = 100;
//		double gamma = 8;
//		LibSVM svm = new LibSVM();
//		svm.setSVMType(new SelectedTag(LibSVM.SVMTYPE_NU_SVR,LibSVM.TAGS_SVMTYPE));
//		svm.setKernelType(new SelectedTag(LibSVM.KERNELTYPE_RBF,LibSVM.TAGS_KERNELTYPE));
//		svm.setShrinking(false);
//		svm.setCost(cost);
//		svm.setGamma(gamma);
//		CalibratorWeka cw = new CalibratorWeka(data, svm);
//		Evaluator e = new Evaluator();
//		double error = e.calculate(cw, data, 1).getAbsError();
//		System.out.println(error);

		fo.setCvFolds(5);
		fo.setCvType(0);
		fo.optimize(du);
		System.out.println(fo);
	}

}
