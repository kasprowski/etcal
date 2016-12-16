package pl.kasprowski.etcal.tests;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;

import pl.kasprowski.etcal.ETCal;
import pl.kasprowski.etcal.calibration.polynomial.MaskedPolynomialProblem;
import pl.kasprowski.etcal.dataunits.DataUnit;
import pl.kasprowski.etcal.dataunits.DataUnits;
import pl.kasprowski.etcal.dataunits.Target;
import pl.kasprowski.etcal.evaluation.Errors;
import pl.kasprowski.etcal.helpers.ObjDef;

public class TestHelper {
	/**
	 * Generates mask for polynomial with the given degree
	 * @param xnum
	 * @param deg
	 * @return
	 */
	public static String genMask(int xnum, int deg) {
		int len = 0;


		StringBuffer bmask = new StringBuffer("");
		for(int i=0;i<MaskedPolynomialProblem.termsNum(xnum);i++) bmask.append("0");

		if(deg==3)
			len = MaskedPolynomialProblem.komb(xnum, 3) +
			MaskedPolynomialProblem.komb(xnum, 2) + MaskedPolynomialProblem.komb(xnum, 1);
		if(deg==2)
			len = MaskedPolynomialProblem.komb(xnum, 2) + MaskedPolynomialProblem.komb(xnum, 1);
		if(deg==1)
			len = MaskedPolynomialProblem.komb(xnum, 1);

		System.out.println("TERMS="+bmask.length());
		System.out.println("set terms: "+len);
		for(int i=bmask.length()-len-1;i<bmask.length();i++) bmask.replace(i, i+1, "1");
		return bmask.toString();

	}

	/**
	 * Creates a new DataUnits object containing only first howMuch variables of the original one
	 * @param dataUnits
	 * @param howMuch
	 * @return
	 */
	public static DataUnits cutVariables(DataUnits dataUnits,int howMuch) {
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

	
	void printResults(String fname, String name, ETCal etcal, DataUnits duTrain, DataUnits duTest, ObjDef opt, String reportFile) {
		try (BufferedWriter table = new BufferedWriter(new FileWriter(new File(reportFile),true));
				//	BufferedWriter table = new BufferedWriter(new FileWriter(new File("c:/calsoft/table2"),true));
				) {
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
