package pl.kasprowski.etcal.filters;

import pl.kasprowski.etcal.dataunits.DataUnits;

/**
 * Interface for all filters
 * 
 * @author pawel@kasprowski.pl
 *
 */
public abstract interface Filter {

	/**
	 * The method converts one DataUnits object to another
	 * @param data
	 * @return
	 */
	public abstract DataUnits filter(DataUnits data);

}
