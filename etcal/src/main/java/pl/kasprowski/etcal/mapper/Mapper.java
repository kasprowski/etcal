package pl.kasprowski.etcal.mapper;

import java.util.List;

import pl.kasprowski.etcal.dataunits.DataUnits;

/**
 * Interface fo target mappers
 * 
 * @author pawel@kasprowski.pl
 *
 */
public abstract interface Mapper {
	
	/**
	 * For given DataUnits which contain DataUnit objects with multiple Targets
	 * finds the best-fitting target mappings.
	 * The returned list has the same size as DataUnits and contains index 
	 * of the chosen target for every DataUnit.
	 * 
	 * @param dataUnits
	 * @return
	 * @throws Exception
	 */
	public abstract List<Integer> map(DataUnits dataUnits) throws Exception;

}
