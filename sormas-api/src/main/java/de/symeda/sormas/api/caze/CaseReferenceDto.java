package de.symeda.sormas.api.caze;

import java.util.Date;

import de.symeda.sormas.api.ReferenceDto;

public class CaseReferenceDto extends ReferenceDto {

	public CaseReferenceDto() { }
	
	public CaseReferenceDto(Date creationDate, Date changeDate, String uuid) {
		super(creationDate, changeDate, uuid);
	}

	private static final long serialVersionUID = 5612778605780498593L;

	
}
