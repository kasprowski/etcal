package pl.kasprowski.etcal.mapper;

import java.util.List;

import pl.kasprowski.etcal.dataunits.DataUnits;

public abstract interface Mapper {
	public abstract List<Integer> map(DataUnits dataUnits) throws Exception;

}
