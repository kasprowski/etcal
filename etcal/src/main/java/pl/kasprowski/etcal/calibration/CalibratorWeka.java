package pl.kasprowski.etcal.calibration;

import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

import pl.kasprowski.etcal.dataunits.DU2RDConverter;
import pl.kasprowski.etcal.dataunits.DataUnits;
import pl.kasprowski.etcal.dataunits.Target;
import weka.classifiers.Classifier;
import weka.core.Attribute;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;

/**
 * Calibrator that may use any classifier implemented in WEKA that works
 * for regression.
 * Classifiers should be created in subclasses (see CalibratorLibSVM for example)
 * 
 * @author pawel@kasprowski.pl
 *
 */
public class CalibratorWeka implements Calibrator{
	boolean calculated = false;

	
	protected Classifier classifierX; //= new SMOreg();
	Instances instancesX;
	protected Classifier classifierY; //= new SMOreg();
	Instances instancesY;


	@Override
	public void calculate(DataUnits dataUnits) {
		RegressionData data = DU2RDConverter.dataUnits2RegressionData(dataUnits);
		calculated = false;
		System.setOut(new PrintStream(new OutputStream() {
			@Override public void write(int b) throws IOException {}
		}));
		try{
			FastVector attributes = new FastVector();
			attributes.addElement(new Attribute("y"));
			for(int i=0;i<data.xNum();i++) {
				attributes.addElement(new Attribute("a"+i));
			}
			instancesX = new Instances("EyeX", attributes, 100);
			instancesX.setClassIndex(0);
			instancesY = new Instances("EyeY", attributes, 100);
			instancesY.setClassIndex(0);


			//training sets preparation
			for(int i=0;i<data.size();i++) {
				double[] x = data.getX().get(i);
				double y = data.getY1().get(i);
				Instance instance = new Instance(data.xNum()+1);
				instance.setValue((Attribute)attributes.elementAt(0), y);
				for(int j=0;j<data.xNum();j++)
					instance.setValue((Attribute)attributes.elementAt(j+1), x[j]);
				instancesX.add(instance);	

			}


			for(int i=0;i<data.size();i++) {
				double[] x = data.getX().get(i);
				double y = data.getY2().get(i);
				Instance instance = new Instance(data.xNum()+1);
				instance.setValue((Attribute)attributes.elementAt(0), y);
				for(int j=0;j<data.xNum();j++)
					instance.setValue((Attribute)attributes.elementAt(j+1), x[j]);
				instancesY.add(instance);	

			}


			try{
				classifierX.buildClassifier(instancesX);
				classifierY.buildClassifier(instancesY);
			}catch(Exception e) {e.printStackTrace();
			System.out.println(instancesX);
			}

			calculated = true;
		}
		finally{
			System.setOut(new PrintStream(new FileOutputStream(FileDescriptor.out)));
		}

	}

	@Override
	public Target getValue(double[] x) {
		
		if(!calculated) 
			throw new RuntimeException("Model not calculated!");

		double sx = getResult(x, classifierX);
		double sy = getResult(x, classifierY);
		Target dv = new Target(sx,sy,1);
		return dv;
	}

	public double getResult(double[] x,Classifier classifier) {
		try{

			FastVector attributes = new FastVector();
			attributes.addElement(new Attribute("y"));
			for(int i=0;i<x.length;i++) {
				attributes.addElement(new Attribute("a"+i));
			}
			Instances instances = new Instances("EyeDB", attributes, 1);       
			instances.setClassIndex(0);

			Instance instance = new Instance(x.length+1);
			for(int i=0;i<x.length;i++)
				instance.setValue((Attribute)attributes.elementAt(i+1), x[i]);
			instance.setDataset(instances);
			return classifier.classifyInstance(instance);
		}catch(Exception e) {e.printStackTrace();}
		return 0;
	}

}
