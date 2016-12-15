package pl.kasprowski.etcal.optimizer.svr;

import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

import pl.kasprowski.etcal.calibration.CalibratorLibSVM;
import pl.kasprowski.etcal.dataunits.DataUnits;
import pl.kasprowski.etcal.evaluation.Evaluator;

public class FullSvrOptimizer extends SvrOptimizer {
	public void optimize(DataUnits dataUnits) throws Exception {
		this.setDataUnits(dataUnits);

		Evaluator e = new Evaluator();

		
		//calculate errors for all masks
		double minError = -1;
		int minCost = 0;
		int minGamma = 0;
		
		for(int cost = minExpCost;cost<=maxExpCost;cost++)
			for(int gamma = minExpGamma;gamma<=maxExpGamma;gamma += 1)	{
				System.setOut(new PrintStream(new OutputStream() {
				      @Override public void write(int b) throws IOException {}
				    }));
				
//				LibSVM svm = new LibSVM();
//				svm.setSVMType(new SelectedTag(LibSVM.SVMTYPE_NU_SVR,LibSVM.TAGS_SVMTYPE));
//				svm.setKernelType(new SelectedTag(LibSVM.KERNELTYPE_RBF,LibSVM.TAGS_KERNELTYPE));
//				svm.setShrinking(false);
//				svm.setCost(Math.pow(2,cost));
//				svm.setGamma(Math.pow(2,gamma));
//				svm.svm_set_print_string_function(new libsvm.svm_print_interface(){
//				    @Override public void print(String s) {} // Disables svm output
//				});
				CalibratorLibSVM cw = new CalibratorLibSVM();
				cw.setExpCost(cost);
				cw.setExpGamma(gamma);
				//cw.setData(data);

				double error = e.calculate(cw, dataUnits, getCvFolds(), getCvType()).getAbsError();
				System.setOut(new PrintStream(new FileOutputStream(FileDescriptor.out)));

				if(minError==-1 || minError>error) {
					minError = error; 
					minGamma = gamma; 
					minCost = cost;
					setBest(new int[] {cost,gamma});
				}
		}
		setBest(new int[] {minCost,minGamma});
	}

}
