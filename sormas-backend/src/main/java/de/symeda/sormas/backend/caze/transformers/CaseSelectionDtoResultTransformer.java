package de.symeda.sormas.backend.caze.transformers;

import java.util.Date;
import java.util.List;

import org.hibernate.transform.ResultTransformer;

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
		Integer age = objects[5] != null ? (int) objects[5] : null;
		ApproximateAgeType approximateAgeType = (ApproximateAgeType) objects[6];
		Integer birthdateDD = objects[7] != null ? (int) objects[7] : null;
		Integer birthdateMM = objects[8] != null ? (int) objects[8] : null;
		Integer birthdateYYYY = objects[9] != null ? (int) objects[9] : null;
		String healthFacilityName = FacilityHelper.buildFacilityString((String) objects[11], (String) objects[12], (String) objects[13]);
		return new CaseSelectionDto(
			(String) objects[0],
			(String) objects[1],
			(String) objects[2],
			(String) objects[3],
			(String) objects[4],
			new AgeAndBirthDateDto(age, approximateAgeType, birthdateDD, birthdateMM, birthdateYYYY),
			(String) objects[10],
			healthFacilityName,
			(Date) objects[14],
			(Sex) objects[15],
			(CaseClassification) objects[16],
			(CaseOutcome) objects[17],
			(Boolean) objects[18]);
	}

	@Override
	public List transformList(List list) {
		return list;
	}
}
