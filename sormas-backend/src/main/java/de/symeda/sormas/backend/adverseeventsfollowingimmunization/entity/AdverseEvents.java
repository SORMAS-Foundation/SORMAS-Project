/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2024 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package de.symeda.sormas.backend.adverseeventsfollowingimmunization.entity;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;

import de.symeda.sormas.api.adverseeventsfollowingimmunization.AdverseEventState;
import de.symeda.sormas.api.adverseeventsfollowingimmunization.SeizureType;
import de.symeda.sormas.backend.common.AbstractDomainObject;

@Entity
@Table(name = "adverseevents")
public class AdverseEvents extends AbstractDomainObject {

	private static final long serialVersionUID = 5407524640930885029L;

	public static final String TABLE_NAME = "adverseevents";

	public static final String SEVERE_LOCAL_REACTION = "severeLocalReaction";
	public static final String SEVERE_LOCAL_REACTION_MORE_THAN_THREE_DAYS = "severeLocalReactionMoreThanThreeDays";
	public static final String SEVERE_LOCAL_REACTION_BEYOND_NEAREST_JOINT = "severeLocalReactionBeyondNearestJoint";
	public static final String SEIZURES = "seizures";
	public static final String SEIZURE_TYPE = "seizureType";
	public static final String ABSCESS = "abscess";
	public static final String SEPSIS = "sepsis";
	public static final String ENCEPHALOPATHY = "encephalopathy";
	public static final String TOXIC_SHOCK_SYNDROME = "toxicShockSyndrome";
	public static final String THROMBOCYTOPENIA = "thrombocytopenia";
	public static final String ANAPHYLAXIS = "anaphylaxis";
	public static final String FEVERISH_FEELING = "feverishFeeling";
	public static final String OTHER_ADVERSE_EVENT_DETAILS = "otherAdverseEventDetails";

	private AdverseEventState severeLocalReaction;
	private boolean severeLocalReactionMoreThanThreeDays;
	private boolean severeLocalReactionBeyondNearestJoint;
	private AdverseEventState seizures;
	private SeizureType seizureType;
	private AdverseEventState abscess;
	private AdverseEventState sepsis;
	private AdverseEventState encephalopathy;
	private AdverseEventState toxicShockSyndrome;
	private AdverseEventState thrombocytopenia;
	private AdverseEventState anaphylaxis;
	private AdverseEventState feverishFeeling;
	private String otherAdverseEventDetails;

	@Enumerated(EnumType.STRING)
	public AdverseEventState getSevereLocalReaction() {
		return severeLocalReaction;
	}

	public void setSevereLocalReaction(AdverseEventState severeLocalReaction) {
		this.severeLocalReaction = severeLocalReaction;
	}

	public boolean isSevereLocalReactionMoreThanThreeDays() {
		return severeLocalReactionMoreThanThreeDays;
	}

	public void setSevereLocalReactionMoreThanThreeDays(boolean severeLocalReactionMoreThanThreeDays) {
		this.severeLocalReactionMoreThanThreeDays = severeLocalReactionMoreThanThreeDays;
	}

	public boolean isSevereLocalReactionBeyondNearestJoint() {
		return severeLocalReactionBeyondNearestJoint;
	}

	public void setSevereLocalReactionBeyondNearestJoint(boolean severeLocalReactionBeyondNearestJoint) {
		this.severeLocalReactionBeyondNearestJoint = severeLocalReactionBeyondNearestJoint;
	}

	@Enumerated(EnumType.STRING)
	public AdverseEventState getSeizures() {
		return seizures;
	}

	public void setSeizures(AdverseEventState seizures) {
		this.seizures = seizures;
	}

	@Enumerated(EnumType.STRING)
	public SeizureType getSeizureType() {
		return seizureType;
	}

	public void setSeizureType(SeizureType seizureType) {
		this.seizureType = seizureType;
	}

	@Enumerated(EnumType.STRING)
	public AdverseEventState getAbscess() {
		return abscess;
	}

	public void setAbscess(AdverseEventState abscess) {
		this.abscess = abscess;
	}

	@Enumerated(EnumType.STRING)
	public AdverseEventState getSepsis() {
		return sepsis;
	}

	public void setSepsis(AdverseEventState sepsis) {
		this.sepsis = sepsis;
	}

	@Enumerated(EnumType.STRING)
	public AdverseEventState getEncephalopathy() {
		return encephalopathy;
	}

	public void setEncephalopathy(AdverseEventState encephalopathy) {
		this.encephalopathy = encephalopathy;
	}

	@Enumerated(EnumType.STRING)
	public AdverseEventState getToxicShockSyndrome() {
		return toxicShockSyndrome;
	}

	public void setToxicShockSyndrome(AdverseEventState toxicShockSyndrome) {
		this.toxicShockSyndrome = toxicShockSyndrome;
	}

	@Enumerated(EnumType.STRING)
	public AdverseEventState getThrombocytopenia() {
		return thrombocytopenia;
	}

	public void setThrombocytopenia(AdverseEventState thrombocytopenia) {
		this.thrombocytopenia = thrombocytopenia;
	}

	@Enumerated(EnumType.STRING)
	public AdverseEventState getAnaphylaxis() {
		return anaphylaxis;
	}

	public void setAnaphylaxis(AdverseEventState anaphylaxis) {
		this.anaphylaxis = anaphylaxis;
	}

	@Enumerated(EnumType.STRING)
	public AdverseEventState getFeverishFeeling() {
		return feverishFeeling;
	}

	public void setFeverishFeeling(AdverseEventState feverishFeeling) {
		this.feverishFeeling = feverishFeeling;
	}

	public String getOtherAdverseEventDetails() {
		return otherAdverseEventDetails;
	}

	public void setOtherAdverseEventDetails(String otherAdverseEventDetails) {
		this.otherAdverseEventDetails = otherAdverseEventDetails;
	}
}
