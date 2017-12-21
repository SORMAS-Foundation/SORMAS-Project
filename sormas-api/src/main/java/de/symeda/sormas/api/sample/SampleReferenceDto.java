package de.symeda.sormas.api.sample;

import de.symeda.sormas.api.ReferenceDto;
import de.symeda.sormas.api.utils.DataHelper;

public class SampleReferenceDto extends ReferenceDto {

	private static final long serialVersionUID = -6975445672442728938L;

	public SampleReferenceDto() {
		
	}
	
	public SampleReferenceDto(String uuid) {
		setUuid(uuid);
	}
	
	public SampleReferenceDto(String uuid, String caption) {
		setUuid(uuid);
		setCaption(caption);
	}

	public SampleReferenceDto(String uuid, SampleMaterial sampleMaterial, String caseUuid) {
		setUuid(uuid);
		setCaption(buildCaption(sampleMaterial, caseUuid));
	}

	public static String buildCaption(SampleMaterial sampleMaterial, String caseUuid) {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append(DataHelper.toStringNullable(sampleMaterial));
		if (stringBuilder.length() > 0) {
			stringBuilder.append(" sample");
		} else {
			stringBuilder.append("Sample");
		}
		stringBuilder.append(" for case ")
			.append(DataHelper.getShortUuid(caseUuid));
		return stringBuilder.toString();
	}
}
