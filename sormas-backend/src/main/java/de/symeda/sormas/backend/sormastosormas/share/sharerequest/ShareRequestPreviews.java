/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2021 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

package de.symeda.sormas.backend.sormastosormas.share.sharerequest;

import java.util.ArrayList;
import java.util.List;

import de.symeda.sormas.api.sormastosormas.sharerequest.SormasToSormasCasePreview;
import de.symeda.sormas.api.sormastosormas.sharerequest.SormasToSormasContactPreview;
import de.symeda.sormas.api.sormastosormas.sharerequest.SormasToSormasEventParticipantPreview;
import de.symeda.sormas.api.sormastosormas.sharerequest.SormasToSormasEventPreview;

public class ShareRequestPreviews {

	private final List<SormasToSormasCasePreview> cases;
	private final List<SormasToSormasContactPreview> contacts;
	private final List<SormasToSormasEventPreview> events;
	private final List<SormasToSormasEventParticipantPreview> eventParticipants;

	public ShareRequestPreviews() {
		cases = new ArrayList<>(0);
		contacts = new ArrayList<>(0);
		events = new ArrayList<>(0);
		eventParticipants = new ArrayList<>(0);
	}

	public ShareRequestPreviews(
		List<SormasToSormasCasePreview> cases,
		List<SormasToSormasContactPreview> contacts,
		List<SormasToSormasEventPreview> events,
		List<SormasToSormasEventParticipantPreview> eventParticipants) {
		this.cases = cases;
		this.contacts = contacts;
		this.events = events;
		this.eventParticipants = eventParticipants;
	}

	public List<SormasToSormasCasePreview> getCases() {
		return cases;
	}

	public List<SormasToSormasContactPreview> getContacts() {
		return contacts;
	}

	public List<SormasToSormasEventPreview> getEvents() {
		return events;
	}

	public List<SormasToSormasEventParticipantPreview> getEventParticipants() {
		return eventParticipants;
	}
}
