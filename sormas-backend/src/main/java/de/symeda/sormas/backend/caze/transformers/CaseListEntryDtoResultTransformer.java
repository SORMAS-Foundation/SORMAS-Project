package de.symeda.sormas.backend.caze.transformers;

import java.util.Date;
import java.util.List;

import org.hibernate.transform.ResultTransformer;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.caze.CaseClassification;
import de.symeda.sormas.api.caze.CaseListEntryDto;

public class CaseListEntryDtoResultTransformer implements ResultTransformer {

	private static final long serialVersionUID = -1076376082289833418L;

	@Override
	public Object transformTuple(Object[] objects, String[] strings) {
		return new CaseListEntryDto(
			(String) objects[0],
			(Date) objects[1],
			(Disease) objects[2],
			(CaseClassification) objects[3],
			(Date) objects[4],
			(boolean) objects[5]);
	}

	@Override
	public List transformList(List list) {
		return list;
	}
}
