package de.symeda.sormas.app.backend.event;

import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.api.utils.jurisdiction.EventJurisdictionHelper;
import de.symeda.sormas.api.utils.jurisdiction.EventParticipantJurisdictionHelper;
import de.symeda.sormas.app.backend.config.ConfigProvider;
import de.symeda.sormas.app.backend.user.User;
import de.symeda.sormas.app.util.JurisdictionHelper;

public class EventEditAuthorization {

	public static boolean isEventEditAllowed(Event event) {
		User user = ConfigProvider.getUser();

		if (event.getSormasToSormasOriginInfo() != null) {
			return event.getSormasToSormasOriginInfo().isOwnershipHandedOver();
		}

		return !event.isOwnershipHandedOver()
			&& EventJurisdictionHelper.isInJurisdictionOrOwned(
				UserRole.getJurisdictionLevel(user.getUserRoles()),
				JurisdictionHelper.createUserJurisdiction(user),
				JurisdictionHelper.createEventJurisdictionDto(event));
	}

	public static boolean isEventParticipantEditAllowed(EventParticipant eventParticipant) {
		User user = ConfigProvider.getUser();

		if (eventParticipant.getSormasToSormasOriginInfo() != null) {
			return eventParticipant.getSormasToSormasOriginInfo().isOwnershipHandedOver();
		}

		return !eventParticipant.isOwnershipHandedOver()
			&& EventParticipantJurisdictionHelper.isInJurisdictionOrOwned(
				UserRole.getJurisdictionLevel(user.getUserRoles()),
				JurisdictionHelper.createUserJurisdiction(user),
				JurisdictionHelper.createEventParticipantJurisdictionDto(eventParticipant));
	}
}
