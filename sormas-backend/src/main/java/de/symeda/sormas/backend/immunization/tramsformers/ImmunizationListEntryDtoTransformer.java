package de.symeda.sormas.backend.immunization.tramsformers;

import java.util.Date;
import java.util.List;

import org.hibernate.transform.ResultTransformer;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.immunization.ImmunizationListEntryDto;
import de.symeda.sormas.api.immunization.ImmunizationManagementStatus;
import de.symeda.sormas.api.immunization.ImmunizationStatus;
import de.symeda.sormas.api.immunization.MeansOfImmunization;

public class ImmunizationListEntryDtoTransformer implements ResultTransformer {

	@Override
	public Object transformTuple(Object[] objects, String[] strings) {
		return new ImmunizationListEntryDto(
			(String) objects[0],
			(Disease) objects[1],
			(MeansOfImmunization) objects[2],
			(ImmunizationStatus) objects[3],
			(ImmunizationManagementStatus) objects[4],
			(Date) objects[5],
			(Date) objects[6]);
	}

	@Override
	public List transformList(List list) {
		return list;
	}
}
