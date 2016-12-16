package pl.kasprowski.etcal.tests;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;

import pl.kasprowski.etcal.ETCal;
import pl.kasprowski.etcal.IETCal;
import pl.kasprowski.etcal.dataunits.DataUnits;
import pl.kasprowski.etcal.evaluation.Errors;
import pl.kasprowski.etcal.helpers.ObjDef;

/**
 * Test 1 from 
 * @unpublished{kasprowski2016etcal,
 * title={ETCAL - a versatile and extendable library for eye tracker calibration},
 * author={Kasprowski, Pawel and Harezlak, Katarzyna}, note={Inpress}
 * }
 * @author pawel@kasprowski.pl
 *
 */
public class Test1 {
	static String fdir = "c:/etcal_data/vog/";
	static String reportFile = "c:/etcal_data/test1";

	public static void main(String[] args) throws Exception{
		for(String fname:new File(fdir).list()) 
			if(fname.contains("train"))
				new Test1().doExperiment(fname.substring(0,fname.indexOf("_")));

	}

	IETCal etcal;
	String fname;
	DataUnits duTrain; 
	DataUnits duTest;

	public void doExperiment(String fname) throws Exception{
		System.out.println(new Date() + " " + fname);
		this.fname = fname;
		duTrain = DataUnits.load(fdir+"/"+fname+"_train.json");
		duTest = DataUnits.load(fdir+"/"+fname+"_test.json");
		
		etcal = new ETCal();
		etcal.add(duTrain);

		ObjDef calibpar = new ObjDef();
		
		calibpar.type = "pl.kasprowski.etcal.calibration.CalibratorPolynomial";
		etcal.build(calibpar);
		printResults("cubic",calibpar);

		
		String mask = TestHelper.genMask(duTrain.xNum(), 2);
		calibpar.params.put("mask",mask);
		etcal.build(calibpar);
		printResults("quad",calibpar);

		mask = TestHelper.genMask(duTrain.xNum(), 1);
		calibpar.params.put("mask",mask);
		etcal.build(calibpar);
		printResults("linear",calibpar);
		
	}
	


	void printResults(String name,ObjDef opt) {
		try (BufferedWriter table = new BufferedWriter(new FileWriter(new File(reportFile),true));) {
			String folds = opt.params.get("cvFolds");
			String type = opt.params.get("cvType");
			if(folds==null) folds="";
			if(type==null) type="";

			if(opt.params.get("cvFolds")!=null) name+= "_"+opt.params.get("cvFolds");
			if(opt.params.get("cvType")!=null) name+= "_"+opt.params.get("cvType");


			try{
				Errors e1 = etcal.checkErrors(duTrain);
				//			writer.write("TRAIN: "+e1+"\n");
				Errors e2 = etcal.checkErrors(duTest);
				//			writer.write("TEST: "+e2+"\n");

				String txt =
						fname+ "\t"+ name +"\t" +
								etcal.getStatusInfo().toString()+"\t"+
								folds+ "\t"+
								type+"\t"+
								e1.getAbsError()+"\t"+
								e2.getAbsError()+"\t"+
								//								calsoft.getTimes().toString()+"\t"+
								System.currentTimeMillis()+"\t"+
								new Date()+
								"\n"
								;
				table.write(txt);
				System.out.println(txt);
			}catch(Exception e) {
				table.write(fname+ "\t"+
						etcal.getStatusInfo().toString()+"\t"+
						folds+ "\t"+
						type+"\t"+ e.getMessage());
				e.printStackTrace();
			}

		} catch(IOException ex) {
			ex.printStackTrace();
		}



	}

	
}
