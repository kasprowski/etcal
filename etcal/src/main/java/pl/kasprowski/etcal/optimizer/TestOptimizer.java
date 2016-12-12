package pl.kasprowski.etcal.optimizer;

import javax.swing.JFrame;
import javax.swing.JLabel;

import pl.kasprowski.etcal.dataunits.DataUnits;
import pl.kasprowski.etcal.dataunits.Target;
import pl.kasprowski.etcal.optimizer.polynomial.GeneticPolynomialOptimizer;

public class TestOptimizer {
	public static void main(String[] args) throws Exception{
		new TestOptimizer();
	}
	public TestOptimizer() {
		GeneticPolynomialOptimizer fo = new GeneticPolynomialOptimizer();
		//FullOptimizer fo = new FullOptimizer();
		//DataUnits du = DataUnits.load("data_long.json");
		DataUnits du = DataUnits.load("c:/_oko/_calsoft/data/js/20131030171913.cal_train.json");
		
//		CalPar p = new CalPar();
//		p.type = "pl.kasprowski.calsoft.calibration.CalibratorPolynomial";
//		p.params.put("mask", "0101111110");
//		Calibrator cal = Calibrator.getCalibrator(p);
//		cal.setData(data);
//		cal.calculate();
//		Target t = cal.getValue(new double[] {0.5,0.5});
//		System.out.println(t);
//
//		System.exit(0);
		
		Thread calcThread = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					fo.setCvFolds(1);
					fo.setGenIterations(20);
					fo.setGenPopulation(40);

					fo.optimize(du);
					System.out.println(fo);

				} catch (Exception e) {
					//		throw new RuntimeException(e.fillInStackTrace());
					e.printStackTrace();
				}
			}
		});
		calcThread.start();



		double screenX = 800;
		double screenY = 600;
		JFrame frame = new JFrame("PP");
		frame.setSize((int)screenX+10, (int)screenY+10);
		JLabel label = new JLabel("TEST");
		frame.add(label);

		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);

		Thread dispThread = new Thread(new Runnable() {
			@Override
			public void run() {
				while(true) {
					try {
						//PointXY p = fo.getValue(new double[] {100,100,0,50});
						if(fo.getBest()!=null) {
							//Target p = fo.getBest().getValue(new double[] {100,100});
							Target p = fo.getBest().getValue(new double[] {0.5,0.5});
							if(p!=null) {
								label.setText(p.getX()+" "+p.getY());
								//System.out.println("XXXXXXXXXXXXXXXXXXXX");
								frame.repaint();
							}
						}
						Thread.sleep(100);
					} catch (Exception e) {e.printStackTrace();}
				}
			}
		});
		dispThread.start();

		//System.out.println(PolynomialOptimizer.maskAsString(mask));

	}
}
