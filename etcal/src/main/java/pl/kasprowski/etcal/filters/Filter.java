package pl.kasprowski.etcal.filters;

import pl.kasprowski.etcal.dataunits.DataUnits;

public abstract interface Filter {


	public abstract DataUnits filter(DataUnits data);

}
