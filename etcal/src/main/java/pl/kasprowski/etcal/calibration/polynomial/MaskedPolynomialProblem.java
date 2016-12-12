package pl.kasprowski.etcal.calibration.polynomial;

import pl.kasprowski.etcal.calibration.RegressionData;

/**
 * Calculates regression using 3 degree polynomial
 * Mask parameter sets which terms of the polynomial should be used for model calculation
 * 
 * @author pawel@kasprowski.pl
 *
 */
public class MaskedPolynomialProblem extends PolynomialProblem {

	public MaskedPolynomialProblem(RegressionData data, boolean[] mask) {
		super();
		this.data = data;
		this.mask = mask;
	}

	private boolean[] mask;
	public void setMask(boolean[] mask) {this.mask = mask;}
	public boolean[] getMask() {return mask;}

	public int maskedNum() {
		int x = 0;
		for(boolean b:mask) if (b) x++;
		return x;
	}
	/**
	 * Number of terms
	 */
	private int termsNum = -1;
	
	
	/**
	 * returns number of terms - initializes it if necessary
	 */
	@Override
	public int termsNum() {
		if(termsNum==-1)
			termsNum = termsNum(data.xNum()); //komb(data.xNum(),3) + komb(data.xNum(),2) + data.xNum() +1; 
		return termsNum;
	}

	/**
	 * Calculates the number of terms for given number of variables
	 */
	public static int termsNum(int xNum) {
		int termsNum = komb(xNum,3) + komb(xNum,2) + xNum +1; 
		return termsNum;
	}
	
	/**
	 * Calculates value using parameters and variables
	 * @param x 
	 * @param variables
	 * @return
	 */
	public double value(double[] x, double[] variables) {
		String[] v = fillV(termsNum,data.xNum());

		double retValue = 0;
		int vn = 0;
		for(int i=0;i<v.length-1;i++) {
			if(!mask[i]) continue;
			double term = variables[vn];
			for(int k=0;k<v[i].length();k++) {
				int ind = Integer.valueOf(v[i].substring(k, k+1));
				term *= x[ind];
			}
			retValue += term;
			vn++;
		}
		retValue += variables[maskedNum()-1];
		return retValue;
	}
	
	/**
	 * Calculates jacobian
	 * @param current coefficients
	 */
	double[][] jacobian(double[] variables) {
		double[][] jacobian = new double[data.size()][maskedNum()];
		String[] v = fillV(termsNum,data.xNum());
		
		for (int i = 0; i < jacobian.length; ++i) {
			int m=0;
			double[] x = data.getX().get(i);
			
			for(int j=0;j<v.length-1;j++) {
				if(!mask[j]) continue;
				double term = 1;
				for(int k=0;k<v[j].length();k++) {
					int ind = Integer.valueOf(v[j].substring(k, k+1));
					term *= x[ind];
				}
				jacobian[i][m] = term;//data.getX().get(i)[j]*data.getX().get(i)[j];
				m++;
				
			}

			jacobian[i][maskedNum()-1] = 1.0;
		}
		return jacobian;

	}

	/**
	 * Builds a map of all terms for a given number of variables
	 * @param varsNum number of terms
	 * @param xnum number of variables
	 * @return
	 */
	static String[] fillV(int varsNum, int xnum) {

		String[] v = new String[varsNum];
		int j = 0;

		for(int i=0;i<xnum;i++) 
			for(int k=i;k<xnum;k++)
				for(int m=k;m<xnum;m++)
					v[j++] = i+""+k+""+m;

		for(int i=0;i<xnum;i++) 
			for(int k=i;k<xnum;k++)
				v[j++] = i+""+k;

		for(int i=0;i<xnum;i++) 
			v[j++] = ""+i;

		v[j++] = "W";
		
//		int i=0;
//		for(String t:v) {
//			System.out.println((i++)+"> "+t);
//		}
		return v;

	}

	/**
	 * Combination with repetitions of k from n
	 * @param n
	 * @param k
	 * @return
	 */
	static int komb(int n,int k) {
		return factorial(n+k-1)/(factorial(k)*factorial(n-1));
	}


	static int factorial(int n) {
		int fact = 1; // this  will be the result
		for (int i = 1; i <= n; i++) {
			fact *= i;
		}
		return fact;
	}

	
	/**
	 * Converts mask table to String
	 * @param mask
	 * @return
	 */
	public static String maskAsString(boolean[] mask) {
		StringBuffer sb = new StringBuffer();
		for(int i=0;i<mask.length;i++) {
			sb.append((mask[i])?"1":"0");
		}
		return sb.toString();
	}

	/**
	 * Converts String to mask table
	 * @param txt
	 * @return table 
	 */
	public static boolean[] maskFromString(String txt) {
		boolean[] mask = new boolean[txt.length()];
		for(int i=0;i<txt.length();i++)
			mask[i] = txt.substring(i,i+1).equals("1");
		return mask;
	}

}