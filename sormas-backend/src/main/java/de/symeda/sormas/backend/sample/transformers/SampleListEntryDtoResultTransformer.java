package de.symeda.sormas.backend.sample.transformers;

import java.util.Date;
import java.util.List;

import org.hibernate.transform.ResultTransformer;

import de.symeda.sormas.api.infrastructure.facility.FacilityHelper;
import de.symeda.sormas.api.infrastructure.facility.FacilityReferenceDto;
import de.symeda.sormas.api.sample.AdditionalTestingStatus;
import de.symeda.sormas.api.sample.PathogenTestResultType;
import de.symeda.sormas.api.sample.SampleListEntryDto;
import de.symeda.sormas.api.sample.SampleMaterial;
import de.symeda.sormas.api.sample.SamplePurpose;
import de.symeda.sormas.api.sample.SamplingReason;
import de.symeda.sormas.api.sample.SpecimenCondition;

public class SampleListEntryDtoResultTransformer implements ResultTransformer {

	@Override
	public Object transformTuple(Object[] objects, String[] strings) {
		boolean referred = objects[5] != null;

		String labName = (String) objects[11];
		String labUuid = (String) objects[12];
		FacilityReferenceDto lab = new FacilityReferenceDto(labUuid, FacilityHelper.buildFacilityString(labUuid, labName), null);

		AdditionalTestingStatus additionalTestingStatus = Boolean.TRUE.equals(objects[16])
			? AdditionalTestingStatus.PERFORMED
			: (Boolean.TRUE.equals(objects[15]) ? AdditionalTestingStatus.REQUESTED : AdditionalTestingStatus.NOT_REQUESTED);

		return new SampleListEntryDto(
			(String) objects[0],
			(SampleMaterial) objects[1],
			(PathogenTestResultType) objects[2],
			(SpecimenCondition) objects[3],
			(SamplePurpose) objects[4],
			referred,
			(boolean) objects[6],
			(Date) objects[7],
			(boolean) objects[8],
			(Date) objects[9],
			(Date) objects[10],
			lab,
			(SamplingReason) objects[13],
			(String) objects[14],
			additionalTestingStatus,
			(long) objects[17]);
	}

	@Override
	public List transformList(List list) {
		return list;
	}
}
