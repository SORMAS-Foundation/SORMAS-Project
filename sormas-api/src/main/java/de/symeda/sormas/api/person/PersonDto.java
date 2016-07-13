package de.symeda.sormas.api.person;

import de.symeda.sormas.api.DataTransferObject;

public class PersonDto extends DataTransferObject {

	private static final long serialVersionUID = -8558187171374254398L;

	public static final String I18N_PREFIX = "CasePerson";

	public static final String FIRST_NAME = "firstName";
	public static final String LAST_NAME = "lastName";
	public static final String CASE_UUID = "caseUuid";

	private String firstName;
	private String lastName;
	
	private String caseUuid;

		
	
	public String getFirstName() {
		return firstName;
	}
	
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	
	public String getLastName() {
		return lastName;
	}
	
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	
	public String getCaseUuid() {
		return caseUuid;
	}

	public void setCaseUuid(String caseUuid) {
		this.caseUuid = caseUuid;
	}
	
	@Override
	public String toString() {
		return firstName + " " + lastName;
	}
	
}
