package pl.kasprowski.etcal.optimizer.polynomial;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import pl.kasprowski.etcal.calibration.CalibratorPolynomial;
import pl.kasprowski.etcal.calibration.RegressionData;
import pl.kasprowski.etcal.calibration.polynomial.MaskedPolynomialProblem;
import pl.kasprowski.etcal.dataunits.DU2RDConverter;
import pl.kasprowski.etcal.dataunits.DataUnits;
import pl.kasprowski.etcal.evaluation.Evaluator;

public class FullPolynomialOptimizer extends PolynomialOptimizer {
	Logger log = Logger.getLogger(FullPolynomialOptimizer.class);
	

	public void optimize(DataUnits dataUnits) throws Exception {
		this.setDataUnits(dataUnits);

		RegressionData data = DU2RDConverter.dataUnits2RegressionData(dataUnits);

		Evaluator e = new Evaluator();
		System.out.println("size="+MaskedPolynomialProblem.termsNum(data.xNum()));
		List<boolean[]> masks = generateAllMasks(MaskedPolynomialProblem.termsNum(data.xNum()));
		System.out.println(masks.size());
		int i=0;
		
		//calculate errors for all masks
		double minError = -1;
		boolean[] minMask = new boolean[1];
		for(boolean[] mask:masks) {
			CalibratorPolynomial calp = new CalibratorPolynomial();
			calp.setMask(MaskedPolynomialProblem.maskAsString(mask));
			double error = e.calculate(calp, dataUnits, getCvFolds(), getCvType()).getAbsError();
			if(minError==-1 || minError>error) {
				minError = error; 
				minMask = mask;
				
				setBest(minMask);

			}
			log.trace(
					(i++)+"\t"+
							MaskedPolynomialProblem.maskAsString(mask)+ "\t"+
							new DecimalFormat("#.####").format(error));
		}
		setBest(minMask);
	}

	static List<boolean[]> generateAllMasks(int bits) {
		int size = 1 << bits;
		ArrayList<boolean[]> masks = new ArrayList<boolean[]>();
		for (int val = 1; val < size; val++) {
			//BitSet bs = new BitSet(bits);
			boolean[] m = new boolean[bits];
			for (int i = bits-1; i >= 0; i--) {
				m[i] = (val & (1 << i)) != 0;
			}
			masks.add(m);
			//			System.out.println(val+" > "+Arrays.toString(m));
		}
		return masks;
	}

}
