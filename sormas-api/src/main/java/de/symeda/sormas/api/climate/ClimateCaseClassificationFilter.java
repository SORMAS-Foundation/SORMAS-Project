/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2026 SORMAS Foundation gGmbH
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

package de.symeda.sormas.api.climate;

import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;

import de.symeda.sormas.api.caze.CaseClassification;
import de.symeda.sormas.api.i18n.I18nProperties;

/**
 * Which cases the Climate dashboard counts.
 */
public enum ClimateCaseClassificationFilter {

	ALL_BUT_NOT_A_CASE,
	CONFIRMED_AND_PROBABLE,
	CONFIRMED;

	/**
	 * @return the classifications counted
	 */
	public Set<CaseClassification> getClassifications() {
		switch (this) {
		case ALL_BUT_NOT_A_CASE:
			return Collections.unmodifiableSet(EnumSet.complementOf(EnumSet.of(CaseClassification.NO_CASE)));
		case CONFIRMED_AND_PROBABLE:
			EnumSet<CaseClassification> classifications = EnumSet.copyOf(CaseClassification.getConfirmedClassifications());
			classifications.add(CaseClassification.PROBABLE);
			return Collections.unmodifiableSet(classifications);
		case CONFIRMED:
			return CaseClassification.getConfirmedClassifications();
		default:
			throw new IllegalArgumentException(name());
		}
	}

	@Override
	public String toString() {
		return I18nProperties.getEnumCaption(this);
	}
}
