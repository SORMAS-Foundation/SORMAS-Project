package de.symeda.sormas.backend.travelentry.transformers;

import java.util.Date;
import java.util.List;

import org.hibernate.transform.ResultTransformer;

import de.symeda.sormas.api.travelentry.TravelEntryIndexDto;

public class TravelEntryIndexDtoResultTransformer implements ResultTransformer {

	@Override
	public Object transformTuple(Object[] objects, String[] strings) {
		return new TravelEntryIndexDto(
			(String) objects[0],
			(String) objects[1],
			(String) objects[2],
			(String) objects[3],
			(String) objects[4],
			(String) objects[5],
			(String) objects[6],
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
