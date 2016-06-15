package de.symeda.sormas.api.caze;

import de.symeda.sormas.api.person.PersonDto;

public class CaseDto extends DataTransferObject {

	private static final long serialVersionUID = 5007131477733638086L;
	
	public static final String CASE_STATUS = "caseStatus";
	public static final String DESCRIPTION = "description";
	public static final String PERSON = "personDto";
	
	private CaseStatus caseStatus;
	private String description;
	private PersonDto personDto;
	
	public CaseStatus getCaseStatus() {
		return caseStatus;
	}

	public void setCaseStatus(CaseStatus caseStatus) {
		this.caseStatus = caseStatus;
	}
	
	public String getDescription() {
		return description;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}

	public PersonDto getPerson() {
		return personDto;
	}
	
	public void setPersonDto(PersonDto personDto) {
		this.personDto = personDto;
	}
}
