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

package de.symeda.sormas.api.caze;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import de.symeda.sormas.api.i18n.I18nProperties;

public enum ReinfectionDetail {

	GENOME_SEQUENCE_PREVIOUS_INFECTION_KNOWN(ReinfectionDetailGroup.GENOME_SEQUENCE),
	GENOME_SEQUENCE_CURRENT_INFECTION_KNOWN(ReinfectionDetailGroup.GENOME_SEQUENCE),
	GENOME_SEQUENCES_NOT_MATCHING(ReinfectionDetailGroup.GENOME_SEQUENCE),
	GENOME_COPY_NUMBER_ABOVE_THRESHOLD(ReinfectionDetailGroup.REINFECTION_EVALUATION),
	GENOME_COPY_NUMBER_BELOW_THRESHOLD(ReinfectionDetailGroup.REINFECTION_EVALUATION),
	ACUTE_RESPIRATORY_ILLNESS_OVERCOME(ReinfectionDetailGroup.PRECEDING_INFECTION),
	PREVIOUS_ASYMPTOMATIC_INFECTION(ReinfectionDetailGroup.PRECEDING_INFECTION),
	TESTED_NEGATIVE_AFTER_PREVIOUS_INFECTION(ReinfectionDetailGroup.PREVIOUS_INFECTION_COMPLETED),
	LAST_PCR_DETECTION_NOT_RECENT(ReinfectionDetailGroup.PREVIOUS_INFECTION_COMPLETED);

	private final ReinfectionDetailGroup group;

	ReinfectionDetail(ReinfectionDetailGroup group) {
		this.group = group;
	}

	public static List<ReinfectionDetail> values(ReinfectionDetailGroup group) {
		return Arrays.stream(values()).filter(e -> e.getGroup() == group).collect(Collectors.toList());
	}

	public ReinfectionDetailGroup getGroup() {
		return group;
	}

	@Override
	public String toString() {
		return I18nProperties.getEnumCaption(this);
	}
}
