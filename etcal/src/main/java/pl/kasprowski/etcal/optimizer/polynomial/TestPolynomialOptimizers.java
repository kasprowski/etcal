package pl.kasprowski.etcal.optimizer.polynomial;

import pl.kasprowski.etcal.dataunits.DataUnits;

public class TestPolynomialOptimizers {
	public static void main(String[] args) throws Exception{

		PolynomialOptimizer fo = new GeneticPolynomialOptimizer();
		//FullOptimizer fo = new FullOptimizer();
		DataUnits du = DataUnits.load("data_long.json");
		fo.setCvFolds(5);
		fo.optimize(du);
		//boolean[] mask = fo.getMask(); 
		
		System.out.println(fo);
		//System.out.println(PolynomialOptimizer.maskAsString(mask));
	}

}
