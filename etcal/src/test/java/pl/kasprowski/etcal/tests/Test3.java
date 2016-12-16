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
import pl.kasprowski.etcal.filters.AoiMedianizeFilter;
import pl.kasprowski.etcal.filters.MedianFilter;
import pl.kasprowski.etcal.helpers.ObjDef;

/**
 * Test3 from
 * @unpublished{kasprowski2016etcal,
 * title={ETCAL - a versatile and extendable library for eye tracker calibration},
 * author={Kasprowski, Pawel and Harezlak, Katarzyna}, note={Inpress}
 * }

 * @author pawel@kasprowski.pl
 *
 */
public class Test3 {
	//Logger log = Logger.getLogger(Test3.class);
	static String fdir = "c:/etcal_data/vog/";
	static String reportFile = "c:/etcal_data/test3";

	public static void main(String[] args) throws Exception{
		for(String fname:new File(fdir).list())
			if(fname.contains("train"))
				new Test3().doExperiment(fname.substring(0,fname.indexOf("_")));

		System.exit(0);
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


		duTrain = TestHelper.cutVariables(duTrain, 2);
		duTest = TestHelper.cutVariables(duTest, 2);

		etcal = new ETCal();
		etcal.add(duTrain);



		System.out.println(new Date() + " POLY");

		ObjDef calibpar = new ObjDef();
		calibpar.type = "pl.kasprowski.etcal.calibration.CalibratorPolynomial";
		calibpar.params.put("mask","0000111111");
		etcal.build(calibpar);
		printResults("notfiltered",calibpar);

		AoiMedianizeFilter filter = new AoiMedianizeFilter();
		filter.setMinSize(10);
		etcal.useFilter(new AoiMedianizeFilter());
		System.out.println(etcal.getStatusInfo().get("dataUnits"));
		etcal.build(calibpar);
		printResults("AOIfiltered",calibpar);	

		for(Integer size:new int[] {5,7,9,13,15,19,21}) {
			duTrain = DataUnits.load(fdir+"/"+fname+"_train.json");
			etcal.reset();
			etcal.add(duTrain);
			MedianFilter f = new MedianFilter();
			f.setLen(size);
			etcal.useFilter(f);
			etcal.build(calibpar);
			printResults("filtered"+size,calibpar);
		}

	}
	void printResults(String name,ObjDef opt) {
		try (BufferedWriter table = new BufferedWriter(new FileWriter(new File(reportFile),true));
				) {
			String folds = opt.params.get("cvFolds");
			String type = opt.params.get("cvType");
			if(folds==null) folds="";
			if(type==null) type="";

			if(opt.params.get("cvFolds")!=null) name+= "_"+opt.params.get("cvFolds");
			if(opt.params.get("cvType")!=null) name+= "_"+opt.params.get("cvType");


			try{
				Errors e1 = etcal.checkErrors(duTrain);
				DataUnits duTestNew = (DataUnits)duTest.clone(); 
				if(name.startsWith("filtered")) {
					MedianFilter f = new MedianFilter();
					f.setLen(Integer.parseInt(name.substring(8)));
					duTestNew = f.filter(duTest);
				} else if(name.startsWith("AOI")) {
					AoiMedianizeFilter f = new AoiMedianizeFilter();
					duTestNew = f.filter(duTest);
					
				}
				Errors e2 = etcal.checkErrors(duTestNew);

				String txt =
						fname+ "\t"+ name +"\t" +
								folds+ "\t"+
								type+"\t"+
								e1.getMseX()+"\t"+
								e1.getMseY()+"\t"+
								e2.getMseX()+"\t"+
								e2.getMseY()+"\t"+
								etcal.getStatusInfo().toString()+"\t"+
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
