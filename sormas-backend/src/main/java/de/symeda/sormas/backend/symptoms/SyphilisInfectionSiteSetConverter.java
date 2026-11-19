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
package de.symeda.sormas.backend.symptoms;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.persistence.AttributeConverter;

import org.apache.commons.lang3.StringUtils;

import de.symeda.sormas.api.symptoms.syphilis.SyphilisInfectionSite;

/**
 * JPA {@link AttributeConverter} that persists a {@link Set} of {@link SyphilisInfectionSite} values
 * as a single comma-separated {@link String} database column, and reconstructs the set when the
 * entity is loaded.
 *
 * <p>
 * This allows a multi-select infection site field (e.g. rendered as checkboxes in the UI) to be
 * stored in a single text column instead of a separate join table.
 * </p>
 */
public class SyphilisInfectionSiteSetConverter implements AttributeConverter<Set<SyphilisInfectionSite>, String> {

	/** Separates the {@link SyphilisInfectionSite} names in the database column. */
	public static final String SEPARATOR = ",";

	/**
	 * Joins the given infection sites into a comma-separated string for storage.
	 *
	 * @param infectionSites
	 *            the infection sites to persist, or {@code null}
	 * @return a comma-separated string of {@link SyphilisInfectionSite} names, or {@code null} if
	 *         {@code infectionSites} is {@code null}
	 */
	@Override
	public String convertToDatabaseColumn(Set<SyphilisInfectionSite> infectionSites) {
		return infectionSites != null
			? String.join(SEPARATOR, infectionSites.stream().map(SyphilisInfectionSite::name).collect(Collectors.toSet()))
			: null;
	}

	/**
	 * Splits the given comma-separated database value back into a set of infection sites.
	 *
	 * @param infectionSitesText
	 *            a comma-separated string of {@link SyphilisInfectionSite} names, or {@code null}
	 * @return the parsed set of {@link SyphilisInfectionSite} values, or {@code null} if
	 *         {@code infectionSitesText} is {@code null}
	 */
	@Override
	public Set<SyphilisInfectionSite> convertToEntityAttribute(String infectionSitesText) {
		return infectionSitesText != null
			? Stream.of(StringUtils.split(infectionSitesText, SEPARATOR)).map(SyphilisInfectionSite::valueOf).collect(Collectors.toSet())
			: null;
	}
}
