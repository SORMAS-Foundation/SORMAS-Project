package de.symeda.sormas.api.caze;

import de.symeda.sormas.api.ReferenceDto;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.utils.DataHelper;

public class CaseReferenceDto extends ReferenceDto {

	private static final long serialVersionUID = 5007131477733638086L;
	
	public CaseReferenceDto() {
		
	}
	
	public CaseReferenceDto(String uuid) {
		setUuid(uuid);
	}
	
	public CaseReferenceDto(String uuid, String caption) {
		setUuid(uuid);
		setCaption(caption);
	}

	public CaseReferenceDto(String uuid, String firstName, String lastName) {
		setUuid(uuid);
		setCaption(buildCaption(uuid, firstName, lastName));
	}
	
	
	public static String buildCaption(String uuid, String personName) {
		return personName + " (" + DataHelper.getShortUuid(uuid) + ")";
	}

	public static String buildCaption(String uuid, String firstName, String lastName) {
		return buildCaption(uuid, PersonDto.buildCaption(firstName, lastName));
	}

}
