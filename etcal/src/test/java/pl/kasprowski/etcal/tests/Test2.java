package pl.kasprowski.etcal.tests;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;

import pl.kasprowski.etcal.ETCal;
import pl.kasprowski.etcal.IETCal;
import pl.kasprowski.etcal.dataunits.DataUnit;
import pl.kasprowski.etcal.dataunits.DataUnits;
import pl.kasprowski.etcal.dataunits.Target;
import pl.kasprowski.etcal.evaluation.Errors;
import pl.kasprowski.etcal.helpers.ObjDef;

/**
 * Test2 from
 * @unpublished{kasprowski2016etcal,
 * title={ETCAL - a versatile and extendable library for eye tracker calibration},
 * author={Kasprowski, Pawel and Harezlak, Katarzyna}, note={Inpress}
 * }

 * @author pawel@kasprowski.pl
 *
 */
public class Test2 {
	static String fdir = "c:/etcal_data/vog/";
	static String reportFile = "c:/etcal_data/test2";

	public static void main(String[] args) throws Exception{
		for(String fname:new File(fdir).list())
			if(fname.contains("train"))
				new Test2().doExperiment(fname.substring(0,fname.indexOf("_")));

		System.exit(0);
	}

	IETCal calsoft;	
	String fname;
	DataUnits duTrain; 
	DataUnits duTest;

	public void doExperiment(String fname) throws Exception{
		System.out.println(new Date() + " " + fname);
		this.fname = fname;
		duTrain = DataUnits.load(fdir+"/"+fname+"_train.json");
		duTest = DataUnits.load(fdir+"/"+fname+"_test.json");
		
		duTrain = cutVariables(duTrain, 2);
		duTest = cutVariables(duTest, 2);
		
		calsoft = new ETCal();
		calsoft.add(duTrain);

		duTrain = TestHelper.cutVariables(duTrain, 2);
		duTest = TestHelper.cutVariables(duTest, 2);

		System.out.println(new Date() + " POLY");

		ObjDef opt = new ObjDef();
		opt.type = "pl.kasprowski.etcal.optimizer.polynomial.GeneticPolynomialOptimizer";
		opt.params.put("cvFolds", "0");
		opt.params.put("cvType", "0");
		opt.params.put("genIterations", "30");
		opt.params.put("genPopulation", "30");
		opt.params.put("numIterationsWithNoProgress", "4");
		

		for(Integer folds:new int[] {1,2,5}) {
			opt.params.put("cvFolds", ""+folds);
			calsoft.optimize(opt);
			printResults("poly",opt);
		}
		opt.params.put("cvType", "1");
		for(Integer folds:new int[] {1,2,5}) {
			opt.params.put("cvFolds", ""+folds);
			calsoft.optimize(opt);
			printResults("poly",opt);
		}

		opt.type = "pl.kasprowski.etcal.optimizer.svr.GeneticSvrOptimizer";
		opt.params.put("cvFolds", "0");
		opt.params.put("cvType", "0");
		opt.params.put("minExpCost","-5");
		opt.params.put("maxExpCost","2");;
		opt.params.put("minExpGamma","-10");
		opt.params.put("maxExpGamma","5");
		
//		opt.params.put("genIterations", "20");
//		opt.params.put("genPopulation", "20");

		for(Integer folds:new int[] {1,2,5}) {
			opt.params.put("cvFolds", ""+folds);
			calsoft.optimize(opt);
			printResults("svr",opt);
		}

		opt.params.put("cvType", "1");

		for(Integer folds:new int[] {1,2,5}) {
			opt.params.put("cvFolds", ""+folds);
			calsoft.optimize(opt);
			printResults("svr",opt);
		}
		

		
	}
	
	DataUnits cutVariables(DataUnits dataUnits,int howMuch) {
		DataUnits newDu = new DataUnits();
		for(DataUnit u:dataUnits.getDataUnits()) {
			DataUnit newd = new DataUnit();
			for(int i=0;i<howMuch;i++)
				newd.addVariable(u.getVariables().get(i));
			for(Target t:u.getTargets())
				newd.addTarget(new Target(t.getX(),t.getY(),t.getW()));
			newDu.add(newd);
		}
		return newDu;

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
				Errors e1 = calsoft.checkErrors(duTrain);
				//			writer.write("TRAIN: "+e1+"\n");
				Errors e2 = calsoft.checkErrors(duTest);
				//			writer.write("TEST: "+e2+"\n");

				String txt =
						fname+ "\t"+ name +"\t" +
								calsoft.getStatusInfo().toString()+"\t"+
								folds+ "\t"+
								type+"\t"+
								e1.getMseX()+"\t"+e1.getMseY()+"\t"+
								e2.getMseX()+"\t"+e2.getMseY()+"\t"+
								//								calsoft.getTimes().toString()+"\t"+
								System.currentTimeMillis()+"\t"+
								new Date()+
								"\n"
								;
				table.write(txt);
				System.out.println(txt);
			}catch(Exception e) {
				table.write(fname+ "\t"+
						calsoft.getStatusInfo().toString()+"\t"+
						folds+ "\t"+
						type+"\t"+ e.getMessage());
				e.printStackTrace();
			}

		} catch(IOException ex) {
			ex.printStackTrace();
		}



	}

	
}
