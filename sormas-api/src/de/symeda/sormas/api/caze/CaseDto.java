package de.symeda.sormas.api.caze;

import de.symeda.sormas.api.DataTransferObject;
import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.person.PersonDto;

public class CaseDto extends DataTransferObject {

	private static final long serialVersionUID = 5007131477733638086L;
	
	public static final String CASE_STATUS = "caseStatus";
	public static final String DESCRIPTION = "description";
	public static final String PERSON = "personDto";
	public static final String DISEASE = "disease";
	
	private CaseStatus caseStatus;
	private String description;
	private PersonDto personDto;
	private Disease disease;
	
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

	public Disease getDisease() {
		return disease;
	}

	public void setDisease(Disease disease) {
		this.disease = disease;
	}
}
