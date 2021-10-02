package de.symeda.sormas.backend.travelentry.transformers;

import java.util.Date;
import java.util.List;

import org.hibernate.transform.ResultTransformer;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.travelentry.TravelEntryListEntryDto;

public class TravelEntryListEntryDtoTransformer implements ResultTransformer {

	@Override
	public Object transformTuple(Object[] objects, String[] strings) {
		return new TravelEntryListEntryDto((String) objects[0], (Date) objects[1], (Disease) objects[2], (String) objects[3], (Boolean) objects[4]);
	}

	@Override
	public List transformList(List list) {
		return list;
	}
}
