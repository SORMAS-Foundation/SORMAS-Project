/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2024 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

package de.symeda.sormas.api.symptoms;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.utils.Diseases;

public enum InfectionSite {

	NOT_APPLICABLE(false, true),
	@Diseases(value = {
		Disease.TUBERCULOSIS })
	BONES_JOINTS_OTHER_THAN_VERTEBRAE(true, true),
	@Diseases(value = {
		Disease.TUBERCULOSIS })
	CNS_EXCEPT_MENINGES(true, true),
	@Diseases(value = {
		Disease.TUBERCULOSIS })
	DISSEMINATED_FORM(true, true),
	@Diseases(value = {
		Disease.TUBERCULOSIS })
	EXTRAPULMONARY_UNKNOWN_SITE(true, true),
	@Diseases(value = {
		Disease.TUBERCULOSIS })
	EXTRA_THORACIC_LYMPH_NODES(true, true),
	@Diseases(value = {
		Disease.TUBERCULOSIS })
	GENITO_URINARY(true, true),
	@Diseases(value = {
		Disease.TUBERCULOSIS })
	INTRATHORACIC_LYMPH_NODES(true, true),
	@Diseases(value = {
		Disease.TUBERCULOSIS })
	LUNG(true, true),
	@Diseases(value = {
		Disease.TUBERCULOSIS })
	MENINGES(true, true),
	@Diseases(value = {
		Disease.TUBERCULOSIS })
	PERITONEUM_DIGESTIVE_TRACT(true, true),
	@Diseases(value = {
		Disease.TUBERCULOSIS })
	PLEURA(true, true),
	@Diseases(value = {
		Disease.TUBERCULOSIS })
	UROGENITAL_SYSTEM(true, true),
	@Diseases(value = {
		Disease.TUBERCULOSIS })
	VERTEBRAE(true, true),
	UNKNOWN(true, true),
	OTHER(true, true);

	private final boolean major;
	private final boolean minor;

	InfectionSite(boolean major, boolean minor) {
		this.major = major;
		this.minor = minor;
	}

	public boolean isMajor() {
		return major;
	}

	public boolean isMinor() {
		return minor;
	}

	@Override
	public String toString() {
		return I18nProperties.getEnumCaption(this);
	}

	public static Set<InfectionSite> filter(Disease disease, boolean includeMajor, boolean includeMinor) {
		return Arrays.stream(values()).filter(site -> {
			// Check if the site is applicable for the disease
			boolean diseaseMatches = isDiseaseApplicable(site, disease);

			// Check if the site matches the major/minor criteria
			boolean criteriaMatches = (includeMajor && site.isMajor()) || (includeMinor && site.isMinor());

			return diseaseMatches && criteriaMatches;
		}).collect(Collectors.toCollection(LinkedHashSet::new));
	}

	private static boolean matchesCriteria(InfectionSite site, boolean includeMajor, boolean includeMinor) {
		if (includeMajor && includeMinor) {
			return site.isMajor() || site.isMinor(); // Include if it's either major or minor
		} else if (includeMajor) {
			return site.isMajor(); // Include only if it's major
		} else if (includeMinor) {
			return site.isMinor(); // Include only if it's minor
		} else {
			return false; // Include nothing if both are false
		}
	}

	private static boolean isDiseaseApplicable(InfectionSite site, Disease disease) {
		try {
			// Get the @Diseases annotation from the enum value
			Diseases diseasesAnnotation = site.getClass().getField(site.name()).getAnnotation(Diseases.class);

			if (diseasesAnnotation == null) {
				// If no annotation, it's applicable to all diseases
				return true;
			}

			// Check if the disease is in the annotation's disease list
			return Arrays.asList(diseasesAnnotation.value()).contains(disease);
		} catch (NoSuchFieldException e) {
			// If we can't access the field, assume it's applicable
			return true;
		}
	}
}
