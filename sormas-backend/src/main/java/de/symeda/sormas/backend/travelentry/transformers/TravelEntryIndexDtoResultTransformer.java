package de.symeda.sormas.backend.travelentry.transformers;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.transform.ResultTransformer;

import de.symeda.sormas.api.travelentry.TravelEntryIndexDto;

public class TravelEntryIndexDtoResultTransformer implements ResultTransformer {

	@Override
	public Object transformTuple(Object[] objects, String[] strings) {
		String pointOfEntryDetails = objects[6] != null ? (String) objects[6] : null;
		String pointOfEntryName = StringUtils.isNotBlank(pointOfEntryDetails) ? pointOfEntryDetails : (String) objects[5];
		return new TravelEntryIndexDto(
			(String) objects[0],
			(String) objects[1],
			(String) objects[2],
			(String) objects[3],
			(String) objects[4],
			pointOfEntryName,
			(boolean) objects[7],
			(boolean) objects[8],
			(boolean) objects[9],
			(Date) objects[10],
			(boolean) objects[11]);
	}

	@Override
	public List transformList(List list) {
		return list;
	}
}
