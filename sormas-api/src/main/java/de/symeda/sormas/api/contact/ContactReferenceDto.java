package de.symeda.sormas.api.contact;

import de.symeda.sormas.api.ReferenceDto;
import de.symeda.sormas.api.utils.DataHelper;

public class ContactReferenceDto extends ReferenceDto {

	private static final long serialVersionUID = -7764607075875188799L;
	
	public ContactReferenceDto() {
		
	}
	
	public ContactReferenceDto(String uuid) {
		setUuid(uuid);
	}
	
	public ContactReferenceDto(String uuid, String caption) {
		setUuid(uuid);
		setCaption(caption);
	}
	
	public ContactReferenceDto(String uuid, String contactFirstName, String contactLastName, String caseFirstName, String caseLastName) {
		setUuid(uuid);
		setCaption(buildCaption(contactFirstName, contactLastName, caseFirstName, caseLastName));
	}

	public static String buildCaption(String contactFirstName, String contactLastName, String caseFirstName, String caseLastName) {
		StringBuilder builder = new StringBuilder();
		builder.append(DataHelper.toStringNullable(contactFirstName))
			.append(" ").append(DataHelper.toStringNullable(contactLastName).toUpperCase())
			.append(" to case ")
			.append(DataHelper.toStringNullable(caseFirstName))
			.append(" ").append(DataHelper.toStringNullable(caseLastName));
		return builder.toString();
	}
}
