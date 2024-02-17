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

package de.symeda.sormas.api.adverseeventsfollowingimmunization;

import java.util.ArrayList;
import java.util.List;

import de.symeda.sormas.api.i18n.I18nProperties;

public final class AefiHelper {

	private AefiHelper() {

	}

	public static String buildAdverseEventsString(
		AdverseEventState severeLocalReaction,
		boolean severeLocalReactionMoreThanThreeDays,
		boolean severeLocalReactionBeyondNearestJoint,
		AdverseEventState seizures,
		SeizureType seizureType,
		AdverseEventState abscess,
		AdverseEventState sepsis,
		AdverseEventState encephalopathy,
		AdverseEventState toxicShockSyndrome,
		AdverseEventState thrombocytopenia,
		AdverseEventState anaphylaxis,
		AdverseEventState feverishFeeling,
		String otherAdverseEventDetails) {

		List<String> adverseEventsList = new ArrayList<>();

		if (severeLocalReaction == AdverseEventState.YES) {
			adverseEventsList.add(I18nProperties.getPrefixCaption(AdverseEventsDto.I18N_PREFIX, AdverseEventsDto.SEVERE_LOCAL_REACTION));
		}

		if (seizures == AdverseEventState.YES) {
			adverseEventsList.add(I18nProperties.getPrefixCaption(AdverseEventsDto.I18N_PREFIX, AdverseEventsDto.SEIZURES));
		}

		if (abscess == AdverseEventState.YES) {
			adverseEventsList.add(I18nProperties.getPrefixCaption(AdverseEventsDto.I18N_PREFIX, AdverseEventsDto.ABSCESS));
		}

		if (sepsis == AdverseEventState.YES) {
			adverseEventsList.add(I18nProperties.getPrefixCaption(AdverseEventsDto.I18N_PREFIX, AdverseEventsDto.SEPSIS));
		}

		if (encephalopathy == AdverseEventState.YES) {
			adverseEventsList.add(I18nProperties.getPrefixCaption(AdverseEventsDto.I18N_PREFIX, AdverseEventsDto.ENCEPHALOPATHY));
		}

		if (toxicShockSyndrome == AdverseEventState.YES) {
			adverseEventsList.add(I18nProperties.getPrefixCaption(AdverseEventsDto.I18N_PREFIX, AdverseEventsDto.TOXIC_SHOCK_SYNDROME));
		}

		if (thrombocytopenia == AdverseEventState.YES) {
			adverseEventsList.add(I18nProperties.getPrefixCaption(AdverseEventsDto.I18N_PREFIX, AdverseEventsDto.THROMBOCYTOPENIA));
		}

		if (anaphylaxis == AdverseEventState.YES) {
			adverseEventsList.add(I18nProperties.getPrefixCaption(AdverseEventsDto.I18N_PREFIX, AdverseEventsDto.ANAPHYLAXIS));
		}

		if (feverishFeeling == AdverseEventState.YES) {
			adverseEventsList.add(I18nProperties.getPrefixCaption(AdverseEventsDto.I18N_PREFIX, AdverseEventsDto.FEVERISH_FEELING));
		}

		return String.join(", ", adverseEventsList);
	}
}
