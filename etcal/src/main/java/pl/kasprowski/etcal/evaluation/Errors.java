package pl.kasprowski.etcal.evaluation;

import java.text.DecimalFormat;
import java.util.List;

public class Errors {
	private double rSquareX;
	private double rSquareY;
	private double mseX;
	private double mseY;
	private double absError;
	private double absErrorX;
	private double absErrorY;
	public Errors() {}
	
	public Errors(double[] errors) {
		this.rSquareX = errors[0];
		this.rSquareY = errors[1];
		this.mseX = errors[2];
		this.mseY = errors[3];
		this.absError = errors[4];
		this.absErrorX = errors[5];
		this.absErrorY = errors[6];
		
	}
	public Errors(double rSquareX, double rSquareY, double mseX, double mseY, double absError, double absErrorX,
			double absErrorY) {
		this.rSquareX = rSquareX;
		this.rSquareY = rSquareY;
		this.mseX = mseX;
		this.mseY = mseY;
		this.absError = absError;
		this.absErrorX = absErrorX;
		this.absErrorY = absErrorY;
	}
	public Double getrSquareX() {
		return rSquareX;
	}
	public Double getrSquareY() {
		return rSquareY;
	}
	public Double getMseX() {
		return mseX;
	}
	public Double getMseY() {
		return mseY;
	}
	public Double getAbsError() {
		return absError;
	}
	public Double getAbsErrorX() {
		return absErrorX;
	}
	public Double getAbsErrorY() {
		return absErrorY;
	}
	DecimalFormat df = new DecimalFormat("#.#####");
	@Override
	public String toString() {
//		return "Errors [rSquareX=" + rSquareX + ", rSquareY=" + rSquareY + ", mseX=" + mseX + ", mseY=" + mseY
//				+ ", absError=" + absError + ", absErrorX=" + absErrorX + ", absErrorY=" + absErrorY + "]";
		return "Errors [mseX=" + df.format(mseX) + 
				"; mseY=" + df.format(mseY) +
				"; absError=" + df.format(absError) + 
				"; absErrorX=" + df.format(absErrorX) + 
				"; absErrorY=" + df.format(absErrorY) + "]";

	}

	
	public static Errors average(List<Errors> le) {
		Errors res = new Errors();
		for(Errors e:le) {
			res.rSquareX += e.rSquareX;
			res.rSquareY += e.rSquareY;
			res.mseX += e.mseX;
			res.mseY += e.mseY;
			res.absError += e.absError;
			res.absErrorX += e.absErrorX;
			res.absErrorY += e.absErrorY;
		}
		double n = le.size();
		res.rSquareX /= n;
		res.rSquareY /= n;
		res.mseX /= n;
		res.mseY /= n;
		res.absError /= n;
		res.absErrorX /= n;
		res.absErrorY /= n;
		return res;
	}

}
