package pl.kasprowski.etcal.mapper;

import java.util.ArrayList;
import java.util.List;

import pl.kasprowski.etcal.dataunits.DataUnits;

/**
 * Mapper that maps always first Target in every DataUnit 
 * @author pawel@kasprowski.pl
 *
 */
public class NullMapper implements Mapper{

	@Override
	public List<Integer> map(DataUnits dataUnits) {
		int size = dataUnits.getDataUnits().size();
		List<Integer> mappings = new ArrayList<Integer>();
		for(int i=0;i<size;i++) mappings.add(0);

//		for(DataUnit du:dataUnits.getDataUnits()) {
//			//here should be a code that finds the best mapping
//			mappings.add(0);
//		}

		
		return mappings;
	}
	

}
