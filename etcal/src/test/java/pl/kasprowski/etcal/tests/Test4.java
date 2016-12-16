package pl.kasprowski.etcal.tests;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import pl.kasprowski.etcal.ETCal;
import pl.kasprowski.etcal.dataunits.DataUnit;
import pl.kasprowski.etcal.dataunits.DataUnits;
import pl.kasprowski.etcal.dataunits.Target;
import pl.kasprowski.etcal.filters.AoiMedianizeFilter;
import pl.kasprowski.etcal.helpers.ObjDef;

/**
 * Test4 from
 * @unpublished{kasprowski2016etcal,
 * title={ETCAL - a versatile and extendable library for eye tracker calibration},
 * author={Kasprowski, Pawel and Harezlak, Katarzyna}, note={Inpress}
 * }
 * @author pawel@kasprowski.pl
 *
 */
public class Test4 {
	static String dir = "c:/etcal_data/et/";

	public static void main(String[] args) throws Exception{

		Test4 t = new Test4();

		for(String fname:new File(dir).list()) {
			if(!fname.contains("train")) continue;
			for(int i=1;i<=9;i++) {
				t.check(dir+fname,i,"c:/etcal_data/test4");
			}
		}
		System.exit(0);
	}
	public void check(String fname, int no, String outFile) throws Exception{

		DataUnits duTrain = DataUnits.load(fname);

		AoiMedianizeFilter f = new AoiMedianizeFilter();
		duTrain = f.filter(duTrain);

		DataUnits duT9 = (DataUnits)duTrain.clone();
		Collection<Target> targets = getAllTargets(duT9);

		List<Boolean> searched = new ArrayList<Boolean>();
		for(int i=0;i<targets.size();i++) searched.add(false);
		double searchedNo = 0;
		
		//remove from the end
		for(int i=duT9.size()-no;i<duT9.size();i++) {
			fillWithTargets1(duT9, targets,i);
			searched.set(i, true);
			searchedNo++;
		}

		
		
		ETCal calib = new ETCal();
		calib.add(duT9);
		ObjDef mapper = new ObjDef();
		mapper.type = "pl.kasprowski.etcal.mapper.GeneticMapper";
		mapper.params.put("genPopulation", "50");
		mapper.params.put("genIterations", "800");
		mapper.params.put("numIterationsWithNoProgress", "8");
		calib.mapTargets(mapper);

		double correct = 0;
		double sNo = 0;
		for(int i=0;i<duTrain.getDataUnits().size();i++) {
			if(searched.get(i)) {
				if(calib.getMappings().get(i) == i) 
					correct++;
				sNo++;
			}
		}
		try (BufferedWriter out = new BufferedWriter(new FileWriter(new File(outFile),true));) {
			String txt = no + "\t" + correct + "\t"+ 
					new DecimalFormat("#.###").format(correct/(double)searchedNo)+"\t"+
					sNo + "\t"+
					searchedNo + "\t"+
					duTrain.size()+"\t"+
					fname;
			System.out.println(txt);
			out.write(txt+"\n");
		}catch(IOException e) {e.printStackTrace();}

		
	}


	static List<Target> getAllTargets(DataUnits dataUnits) {
		List<Target> targets = new ArrayList<Target>(); 
		for(DataUnit du:dataUnits.getDataUnits()) {
			for(Target t:du.getTargets()) {
				boolean isOnList = false;
				for(Target tt:targets) {
					if(t.equals(tt)) isOnList=true;
				}
				if(!isOnList) targets.add(t);
			}
		}

		return targets;
	}

	static void fillWithTargets1(DataUnits dataUnits, Collection<Target> targets, int no) {
		DataUnit du = dataUnits.getDataUnits().get(no);
		du.getTargets().clear();
		for(Target t:targets)
			du.addTarget(t);

	}
}
