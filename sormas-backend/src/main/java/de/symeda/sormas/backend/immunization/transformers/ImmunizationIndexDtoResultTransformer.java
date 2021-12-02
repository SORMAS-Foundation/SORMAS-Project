package de.symeda.sormas.backend.immunization.transformers;

import java.util.Date;
import java.util.List;

import org.hibernate.transform.ResultTransformer;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.caze.AgeAndBirthDateDto;
import de.symeda.sormas.api.immunization.ImmunizationIndexDto;
import de.symeda.sormas.api.immunization.ImmunizationManagementStatus;
import de.symeda.sormas.api.immunization.ImmunizationStatus;
import de.symeda.sormas.api.immunization.MeansOfImmunization;
import de.symeda.sormas.api.person.ApproximateAgeType;
import de.symeda.sormas.api.person.Sex;

public class ImmunizationIndexDtoResultTransformer implements ResultTransformer {

	@Override
	public Object transformTuple(Object[] objects, String[] strings) {
		Integer age = objects[5] != null ? (int) objects[5] : null;
		ApproximateAgeType approximateAgeType = (ApproximateAgeType) objects[6];
		Integer birthdateDD = objects[7] != null ? (int) objects[7] : null;
		Integer birthdateMM = objects[8] != null ? (int) objects[8] : null;
		Integer birthdateYYYY = objects[9] != null ? (int) objects[9] : null;
		return new ImmunizationIndexDto(
			(String) objects[0],
			(String) objects[1],
			(String) objects[2],
			(String) objects[3],
			(Disease) objects[4],
			new AgeAndBirthDateDto(age, approximateAgeType, birthdateDD, birthdateMM, birthdateYYYY),
			(Sex) objects[10],
			(String) objects[11],
			(MeansOfImmunization) objects[12],
			(ImmunizationManagementStatus) objects[13],
			(ImmunizationStatus) objects[14],
			(Date) objects[15],
			(Date) objects[16],
			(String) objects[17],
			(Date) objects[18],
			(Boolean) objects[19]);
	}

	@Override
	public List transformList(List list) {
		return list;
	}
}
