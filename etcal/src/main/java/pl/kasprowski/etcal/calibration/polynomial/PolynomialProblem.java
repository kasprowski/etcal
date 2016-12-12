package pl.kasprowski.etcal.calibration.polynomial;

import java.util.List;

import org.apache.commons.math3.analysis.DifferentiableMultivariateVectorFunction;
import org.apache.commons.math3.analysis.MultivariateMatrixFunction;

import pl.kasprowski.etcal.calibration.RegressionData;

/**
 * Based on 
 * http://commons.apache.org/proper/commons-math/userguide/optimization.html
 * @author pawel@kasprowski.pl
 *
 */
@SuppressWarnings("deprecation")
public abstract class PolynomialProblem implements DifferentiableMultivariateVectorFunction {
	
	RegressionData data;
	
	public void setData(RegressionData data) {
		this.data = data;
	}
	public abstract int termsNum();
	public abstract double value(double[] x, double[] variables);
	abstract double[][] jacobian(double[] variables);
	
	/**
	 * Recalculates coeficients
	 * @param indata
	 * @param variables
	 * @return
	 */
	public double[] value(List<double[]> x, double[] variables) {
		double[] values = new double[x.size()];
		for (int i = 0; i < values.length; ++i) {
			values[i] = value(x.get(i),variables);
		}
		return values;
	}

	@Override
	public double[] value(double[] variables) throws IllegalArgumentException {
		return value(data.getX(),variables);
	}

	@Override
	public MultivariateMatrixFunction jacobian() {
		 return new MultivariateMatrixFunction() {
			 public double[][] value(double[] point) {
				 return jacobian(point);
			 }
		 };

	}

}
