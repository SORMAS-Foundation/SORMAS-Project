package de.symeda.sormas.app.person;

import de.symeda.sormas.app.backend.person.Person;

/**
 * Created by Stefan Szczesny on 08.12.2016.
 */

public class PersonSelectVO {
    private String firstName;
    private String lastName;
    private Person selectedPerson;

    public PersonSelectVO(Person person) {
        if(person!=null) {
            this.firstName = person.getFirstName();
            this.lastName = person.getLastName();
        }
    }

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

    public Person getSelectedPerson() {
        return selectedPerson;
    }

    public void setSelectedPerson(Person selectedPerson) {
        this.selectedPerson = selectedPerson;
    }
}
