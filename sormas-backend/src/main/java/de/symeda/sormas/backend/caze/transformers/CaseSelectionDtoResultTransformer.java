package de.symeda.sormas.backend.caze.transformers;

import java.util.Date;
import java.util.List;

import org.hibernate.transform.ResultTransformer;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.caze.AgeAndBirthDateDto;
import de.symeda.sormas.api.caze.CaseClassification;
import de.symeda.sormas.api.caze.CaseOutcome;
import de.symeda.sormas.api.caze.CaseSelectionDto;
import de.symeda.sormas.api.infrastructure.facility.FacilityHelper;
import de.symeda.sormas.api.person.ApproximateAgeType;
import de.symeda.sormas.api.person.Sex;

public class CaseSelectionDtoResultTransformer implements ResultTransformer {

	@Override
	public Object transformTuple(Object[] objects, String[] strings) {
		Integer age = objects[6] != null ? (int) objects[6] : null;
		ApproximateAgeType approximateAgeType = (ApproximateAgeType) objects[7];
		Integer birthdateDD = objects[8] != null ? (int) objects[8] : null;
		Integer birthdateMM = objects[9] != null ? (int) objects[9] : null;
		Integer birthdateYYYY = objects[10] != null ? (int) objects[10] : null;
		String healthFacilityName = FacilityHelper.buildFacilityString((String) objects[12], (String) objects[13], (String) objects[14]);
		return new CaseSelectionDto(
			(String) objects[0],
			(String) objects[1],
			(String) objects[2],
			(Disease) objects[3],
			(String) objects[4],
			(String) objects[5],
			new AgeAndBirthDateDto(age, approximateAgeType, birthdateDD, birthdateMM, birthdateYYYY),
			(String) objects[11],
			healthFacilityName,
			(Date) objects[15],
			(Sex) objects[16],
			(CaseClassification) objects[17],
			(CaseOutcome) objects[18],
			(Boolean) objects[19]);
	}

	@Override
	public List transformList(List list) {
		return list;
	}
}
