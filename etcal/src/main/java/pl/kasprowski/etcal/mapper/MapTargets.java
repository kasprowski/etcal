package pl.kasprowski.etcal.mapper;

import java.util.List;

import org.apache.log4j.Logger;

import pl.kasprowski.etcal.dataunits.DataUnit;
import pl.kasprowski.etcal.dataunits.DataUnits;

public class MapTargets {
	static Logger log = Logger.getLogger(Mapper.class);

	/**
	 * Creates a new DataUnits object containing only the chosen Targets
	 * @param oldDu
	 * @param mappings
	 * @return
	 */
	public static DataUnits mapTargets(DataUnits oldDu,List<Integer> mappings) {
		try{
			if(mappings==null || mappings.size()==0)
				return (DataUnits)oldDu.clone();

			if(mappings.size()!=oldDu.getDataUnits().size()) {
				log.error("Mappings size ("+mappings.size()+") != dataUnits size ("+oldDu.getDataUnits().size()+")");
				return (DataUnits)oldDu.clone();
			}
		}catch(CloneNotSupportedException e) {e.printStackTrace();}

		DataUnits newDu = new DataUnits();
		int i=0;
		for(DataUnit du:oldDu.getDataUnits()) {
			DataUnit ndu = new DataUnit();
			ndu.addVariables(du.getVariables());
			ndu.addTarget(du.getTargets().get(mappings.get(i))); //add given target
			newDu.add(ndu);
			i++;
		}
		return newDu;
	}


}
