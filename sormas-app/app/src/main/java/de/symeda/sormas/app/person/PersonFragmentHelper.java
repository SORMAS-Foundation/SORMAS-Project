package de.symeda.sormas.app.person;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.app.backend.caze.Case;
import de.symeda.sormas.app.backend.common.AbstractDomainObject;
import de.symeda.sormas.app.backend.contact.Contact;
import de.symeda.sormas.app.backend.event.EventParticipant;
import de.symeda.sormas.app.backend.immunization.Immunization;

public abstract class PersonFragmentHelper {

	public static Disease getDiseaseOfAssociatedEntity(AbstractDomainObject associatedEntity) {
		if (associatedEntity instanceof Case) {
			Case caze = (Case) associatedEntity;
			return caze.getDisease();
		} else if (associatedEntity instanceof Contact) {
			Contact contact = (Contact) associatedEntity;
			return contact.getDisease();
		} else if (associatedEntity instanceof EventParticipant) {
			EventParticipant eventParticipant = (EventParticipant) associatedEntity;
			return eventParticipant.getEvent().getDisease();
		} else if (associatedEntity instanceof Immunization) {
			Immunization immunization = (Immunization) associatedEntity;
			return immunization.getDisease();
		} else {
			throw new IllegalArgumentException(associatedEntity.getEntityName() + " is not a supported entity");
		}
	}

}
