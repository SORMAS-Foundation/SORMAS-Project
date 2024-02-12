/*******************************************************************************
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2018 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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
 *******************************************************************************/
package de.symeda.sormas.api.caze;

import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;

import de.symeda.sormas.api.audit.AuditedClass;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.statistics.StatisticsGroupingKey;

@AuditedClass
public enum CaseClassification
	implements
	StatisticsGroupingKey {

	NOT_CLASSIFIED(1),
	SUSPECT(2),
	PROBABLE(3),
	CONFIRMED(6),
	CONFIRMED_NO_SYMPTOMS(4),
	CONFIRMED_UNKNOWN_SYMPTOMS(5),
	NO_CASE(0);

	/**
	 * Severity of the case classification; confirmed has the highest severity in terms of the classification process
	 * while no_case has the lowest.
	 */
	private final int severity;

	CaseClassification(int severity) {
		this.severity = severity;
	}

	public String getName() {
		return this.name();
	}

	@Override
	public String toString() {
		return I18nProperties.getEnumCaption(this);
	}

	public String toShortString() {
		return I18nProperties.getEnumCaptionShort(this);
	}

	@Override
	public int keyCompareTo(StatisticsGroupingKey o) {

		if (o == null) {
			throw new NullPointerException("Can't compare to null.");
		}
		if (o.getClass() != this.getClass()) {
			throw new UnsupportedOperationException(
				"Can't compare to class " + o.getClass().getName() + " that differs from " + this.getClass().getName());
		}

		return this.toString().compareTo(o.toString());
	}

	public int getSeverity() {
		return severity;
	}

	public static Set<CaseClassification> getConfirmedClassifications() {
		return Collections.unmodifiableSet(
			EnumSet.of(CaseClassification.CONFIRMED, CaseClassification.CONFIRMED_NO_SYMPTOMS, CaseClassification.CONFIRMED_UNKNOWN_SYMPTOMS));
	}
}
