package pl.kasprowski.etcal.evaluation;

import java.util.List;

import pl.kasprowski.etcal.dataunits.Target;

public class CalcErrors {
//	private static double coefX = 40;
//	private static double coefY = 32;
	private static double coefX = 1;
	private static double coefY = 1;

	public double[] calcErrors(List<Target> real,List<Target> calib) {
		double[] realX = new double[real.size()];
		double[] realY = new double[real.size()];
		for(int i=0;i<real.size();i++) {
			realX[i] = real.get(i).getX();
			realY[i] = real.get(i).getY();
		}
		double[] calibX = new double[calib.size()];
		double[] calibY = new double[calib.size()];
		for(int i=0;i<calib.size();i++) {
			calibX[i] = calib.get(i).getX();
			calibY[i] = calib.get(i).getY();
		}
		
		
		return calcErrors(realX, calibX, realY, calibY);
	}
	/**
	 * Liczy b��dy dla punkt�w z x, y przy dopasowaniu do xc,yc
	 * x,y - warto�ci zmierzone, xc,yc - warto�ci wyliczone 
	 * @return dwuargumentowa tablica: r[0] = R2 dla X, r[1] = R2 dla Y, r[2] = MSE dla X, r[3] = MSE dla Y,... 
	 */
//	@Override
	public double[] calcErrors(double[] sx,double[] xc,double[] sy, double[] yc) {
		double[] r = new double[8];
		r[0] = calcRSquare(sx, xc);
		r[1] = calcRSquare(sy, yc);
		r[2] = calcMSE(sx, xc);
		r[3] = calcMSE(sy, yc);
		r[4] = calcAbsError(sx,xc,sy,yc,1,1); // b��d bez wsp�czynnik�w
		r[5] = calcError(sx, xc, coefX);
		r[6] = calcError(sy, yc, coefY);
		r[7] = calcAbsError(sx,xc,sy,yc,coefX,coefY);
		return r;
	}
	/**
	 * liczy rSquare
	 * http://en.wikipedia.org/wiki/Coefficient_of_determination
	 * @param sx warto�ci zmierzone
	 * @param xc warto�ci wyliczone
	 * @return
	 */
	public double calcRSquare(double[] sx, double[] xc) {
		//		System.out.println("\txsize="+x.length+" xc="+xc.length);
		double sumx = 0;

		//		System.out.println("=====");
		//		for(int i=0;i<x.length;i++) System.out.println(x[i]+"\t"+xc[i]);
		//		System.out.println("=====");


		for(int i=0;i<sx.length;i++) sumx+=sx[i];
		double avgx = sumx/sx.length;

		double res = 0;
		double tot = 0;
		for(int i=0;i<sx.length;i++) {
			res += (sx[i]-xc[i])*(sx[i]-xc[i]);
			tot += (sx[i]-avgx)*(sx[i]-avgx);
		}
		double rsq = 1- res/tot;
		return rsq;
	}


	/**
	 * Wylicza Mean Square Error
	 * @param sx - zmierzone
	 * @param xc - wyliczone
	 * @return
	 */
	private double calcMSE(double[] sx, double[] xc) {
		double sse = 0;
		for(int i=0;i<sx.length;i++) {
//			System.out.println(i+ " "+ sx[i] + " > "+xc[i]);
			sse+= (sx[i]-xc[i])*(sx[i]-xc[i]);
		}
		double mse = sse/(sx.length);
		//TODO czy liczy� pierwiastek?
		return Math.sqrt(mse);
		//return mse;
	}

	public static double calcError(double[] x,double[] xc,double coef) {
		double sumErr = 0;
		for(int i=0;i<x.length;i++) {
			double dx = (x[i]-xc[i])*coef;
			sumErr += Math.abs(dx);
		}
		return sumErr/x.length;
	}

	public static double calcAbsError(double[] x,double[] xc,double[] y, double[] yc,double coefX,double coefY) {
		double sumErr = 0;
		//		double coefX = 40; //przelicznik na k�ty dla osi X
		//		double coefY = 32; //przelicznik na k�ty dla osi Y
		for(int i=0;i<x.length;i++) {
			double dx = (x[i]-xc[i])*coefX;
			double dy = (y[i]-yc[i])*coefY;
			sumErr += Math.sqrt(dx*dx + dy*dy);
		}
		return sumErr/x.length;
	}

	

}
