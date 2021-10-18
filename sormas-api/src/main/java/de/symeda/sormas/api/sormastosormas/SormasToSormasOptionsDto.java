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

package de.symeda.sormas.api.sormastosormas;

import static de.symeda.sormas.api.utils.FieldConstraints.CHARACTER_LIMIT_BIG;

import java.io.Serializable;

import javax.validation.constraints.Size;

import de.symeda.sormas.api.i18n.Validations;

public class SormasToSormasOptionsDto implements Serializable {

	public static final String I18N_PREFIX = "SormasToSormasOptions";

	// Fixme this should be renamed
	public static final String ORGANIZATION = "organization";
	public static final String HAND_OVER_OWNERSHIP = "handOverOwnership";
	public static final String PSEUDONYMIZE_PERSONAL_DATA = "pseudonymizePersonalData";
	public static final String PSEUDONYMIZE_SENSITIVE_DATA = "pseudonymizeSensitiveData";
	public static final String COMMENT = "comment";

	public static final String WITH_ASSOCIATED_CONTACTS = "withAssociatedContacts";
	public static final String WITH_SAMPLES = "withSamples";
	public static final String WITH_EVENT_PARTICIPANTS = "withEventParticipants";
	public static final String WITH_IMMUNIZATIONS = "withImmunizations";

	// Fixme this should be renamed but it has strange side effects with the UI
	private SormasServerDescriptor organization;

	private boolean handOverOwnership;

	private boolean pseudonymizePersonalData;

	private boolean pseudonymizeSensitiveData;

	@Size(max = CHARACTER_LIMIT_BIG, message = Validations.textTooLong)
	private String comment;

	private boolean withAssociatedContacts;

	private boolean withSamples;

	private boolean withEventParticipants;

	private boolean withImmunizations;

	// FIXME(#6101): This should be renamed as it is the target of the operation
	public SormasServerDescriptor getOrganization() {
		return organization;
	}

	public void setOrganization(SormasServerDescriptor organization) {
		this.organization = organization;
	}

	public boolean isHandOverOwnership() {
		return handOverOwnership;
	}

	public void setHandOverOwnership(boolean handOverOwnership) {
		this.handOverOwnership = handOverOwnership;
	}

	public boolean isPseudonymizePersonalData() {
		return pseudonymizePersonalData;
	}

	public void setPseudonymizePersonalData(boolean pseudonymizePersonalData) {
		this.pseudonymizePersonalData = pseudonymizePersonalData;
	}

	public boolean isPseudonymizeSensitiveData() {
		return pseudonymizeSensitiveData;
	}

	public void setPseudonymizeSensitiveData(boolean pseudonymizeSensitiveData) {
		this.pseudonymizeSensitiveData = pseudonymizeSensitiveData;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public boolean isWithAssociatedContacts() {
		return withAssociatedContacts;
	}

	public void setWithAssociatedContacts(boolean withAssociatedContacts) {
		this.withAssociatedContacts = withAssociatedContacts;
	}

	public boolean isWithSamples() {
		return withSamples;
	}

	public void setWithSamples(boolean withSamples) {
		this.withSamples = withSamples;
	}

	public boolean isWithEventParticipants() {
		return withEventParticipants;
	}

	public void setWithEventParticipants(boolean withEventParticipants) {
		this.withEventParticipants = withEventParticipants;
	}

	public boolean isWithImmunizations() {
		return withImmunizations;
	}

	public void setWithImmunizations(boolean withImmunizations) {
		this.withImmunizations = withImmunizations;
	}
}
