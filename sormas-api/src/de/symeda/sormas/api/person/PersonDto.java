package de.symeda.sormas.api.person;

import de.symeda.sormas.api.caze.DataTransferObject;

public class PersonDto extends DataTransferObject {

	private static final long serialVersionUID = -8558187171374254398L;

	public static final String FIRST_NAME = "firstName";
	public static final String LAST_NAME = "lastName";
	
	private String firstName;
	private String lastName;
	
	
	
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
	
	
	@Override
	public String toString() {
		return firstName + " " + lastName;
	}
	
	
}
