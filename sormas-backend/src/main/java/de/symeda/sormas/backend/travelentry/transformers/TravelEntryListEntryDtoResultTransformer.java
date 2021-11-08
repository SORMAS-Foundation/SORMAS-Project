package de.symeda.sormas.backend.travelentry.transformers;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.transform.ResultTransformer;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.travelentry.TravelEntryListEntryDto;

public class TravelEntryListEntryDtoResultTransformer implements ResultTransformer {

	@Override
	public Object transformTuple(Object[] objects, String[] strings) {
		String pointOfEntryDetails = objects[4] != null ? (String) objects[4] : null;
		String pointOfEntryName = StringUtils.isNotBlank(pointOfEntryDetails) ? pointOfEntryDetails : (String) objects[3];
		return new TravelEntryListEntryDto((String) objects[0], (Date) objects[1], (Disease) objects[2], pointOfEntryName, (Boolean) objects[5]);
	}

	@Override
	public List transformList(List list) {
		return list;
	}
}
