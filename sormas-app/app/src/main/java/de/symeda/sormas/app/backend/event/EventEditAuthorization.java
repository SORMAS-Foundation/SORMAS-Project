package de.symeda.sormas.app.backend.event;

import de.symeda.sormas.app.backend.config.ConfigProvider;
import de.symeda.sormas.app.backend.user.User;
import de.symeda.sormas.app.util.JurisdictionHelper;

public class EventEditAuthorization {

	public static boolean isEventEditAllowed(Event event) {

		if (event.getSormasToSormasOriginInfo() != null) {
			return event.getSormasToSormasOriginInfo().isOwnershipHandedOver();
		}

		final User user = ConfigProvider.getUser();
		final EventJurisdictionBooleanValidator eventJurisdictionBooleanValidator =
				EventJurisdictionBooleanValidator.of(JurisdictionHelper.createEventJurisdictionDto(event), JurisdictionHelper.createUserJurisdiction(user));
		return !event.isOwnershipHandedOver() && eventJurisdictionBooleanValidator.inJurisdictionOrOwned();
	}

	public static boolean isEventParticipantEditAllowed(EventParticipant eventParticipant) {

		if (eventParticipant.getSormasToSormasOriginInfo() != null) {
			return eventParticipant.getSormasToSormasOriginInfo().isOwnershipHandedOver();
		}

		final User user = ConfigProvider.getUser();
		final EventParticipantJurisdictionBooleanValidator validator =
				EventParticipantJurisdictionBooleanValidator.of(JurisdictionHelper.createEventParticipantJurisdictionDto(eventParticipant), JurisdictionHelper.createUserJurisdiction(user));
		return !eventParticipant.isOwnershipHandedOver() && validator.inJurisdictionOrOwned();
	}
}
