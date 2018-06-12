package de.symeda.sormas.app.component.dialog;

import de.symeda.sormas.app.backend.person.Person;

/**
 * Created by Orson on 25/03/2018.
 * <p>
 * www.technologyboard.org
 * sampson.orson@gmail.com
 * sampson.orson@technologyboard.org
 */

public class PersonSelectViewModel {

    private Person person;
    private boolean selected;

    public PersonSelectViewModel(Person person) {
        if (person == null)
            return;

        this.person = person;
    }

    public Person getPerson() {
        return person;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }
}
