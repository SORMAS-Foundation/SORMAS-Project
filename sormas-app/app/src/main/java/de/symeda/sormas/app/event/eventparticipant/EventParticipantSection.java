/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2020 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package de.symeda.sormas.app.event.eventparticipant;

import android.content.Context;

import de.symeda.sormas.app.R;
import de.symeda.sormas.app.core.enumeration.StatusElaborator;

public enum EventParticipantSection
	implements
	StatusElaborator {

	EVENT_PARTICIPANT_INFO(R.string.heading_event_participant, R.drawable.ic_alert_24dp),
	PERSON_INFO(R.string.caption_person_information, R.drawable.ic_person_black_24dp),
	IMMUNIZATIONS(R.string.caption_event_participant_immunizations, R.drawable.ic_drawer_immunization_24dp),
	VACCINATIONS(R.string.caption_event_participant_vaccinations, R.drawable.ic_drawer_vaccines_24);

	private int friendlyNameResourceId;
	private int iconResourceId;

	EventParticipantSection(int friendlyNameResourceId, int iconResourceId) {
		this.friendlyNameResourceId = friendlyNameResourceId;
		this.iconResourceId = iconResourceId;
	}

	public static EventParticipantSection fromOrdinal(int ordinal) {
		return EventParticipantSection.values()[ordinal];
	}

	@Override
	public String getFriendlyName(Context context) {
		return context.getResources().getString(friendlyNameResourceId);
	}

	@Override
	public int getColorIndicatorResource() {
		return 0;
	}

	@Override
	public Enum getValue() {
		return this;
	}

	@Override
	public int getIconResourceId() {
		return iconResourceId;
	}
}
