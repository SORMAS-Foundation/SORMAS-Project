package de.symeda.sormas.backend.immunization.transformers;

import java.util.Date;
import java.util.List;

import org.hibernate.transform.ResultTransformer;

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
		Integer age = objects[4] != null ? (int) objects[4] : null;
		ApproximateAgeType approximateAgeType = (ApproximateAgeType) objects[5];
		Integer birthdateDD = objects[6] != null ? (int) objects[6] : null;
		Integer birthdateMM = objects[7] != null ? (int) objects[7] : null;
		Integer birthdateYYYY = objects[8] != null ? (int) objects[8] : null;
		return new ImmunizationIndexDto(
			(String) objects[0],
			(String) objects[1],
			(String) objects[2],
			(String) objects[3],
			new AgeAndBirthDateDto(age, approximateAgeType, birthdateDD, birthdateMM, birthdateYYYY),
			(Sex) objects[9],
			(String) objects[10],
			(MeansOfImmunization) objects[11],
			(ImmunizationManagementStatus) objects[12],
			(ImmunizationStatus) objects[13],
			(Date) objects[14],
			(Date) objects[15],
			(String) objects[16],
			(Date) objects[17]);
	}

	@Override
	public List transformList(List list) {
		return list;
	}
}
