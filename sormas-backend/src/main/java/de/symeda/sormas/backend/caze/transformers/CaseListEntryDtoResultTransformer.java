package de.symeda.sormas.backend.caze.transformers;

import java.util.Date;
import java.util.List;

import org.hibernate.transform.ResultTransformer;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.caze.CaseClassification;
import de.symeda.sormas.api.caze.CaseListEntryDto;

public class CaseListEntryDtoResultTransformer implements ResultTransformer {

	@Override
	public Object transformTuple(Object[] objects, String[] strings) {
		return new CaseListEntryDto(
			(String) objects[0],
			(Date) objects[1],
			(Disease) objects[2],
			(CaseClassification) objects[3],
			(boolean) objects[4]);
	}

	@Override
	public List transformList(List list) {
		return list;
	}
}
