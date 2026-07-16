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

package de.symeda.sormas.api.exposure;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.utils.Diseases;

public enum ExposureType {

	@Diseases(value = {
		Disease.CRYPTOSPORIDIOSIS,
		Disease.GIARDIASIS,
		Disease.YERSINIOSIS }, hide = true)
	WORK(true),
	@Diseases({
		Disease.CRYPTOSPORIDIOSIS,
		Disease.GIARDIASIS,
		Disease.YERSINIOSIS })
	TRAVEL(true),
	@Diseases(value = {
		Disease.CRYPTOSPORIDIOSIS,
		Disease.GIARDIASIS,
		Disease.YERSINIOSIS }, hide = true)
	SPORT(false),
	@Diseases(value = {
		Disease.CRYPTOSPORIDIOSIS,
		Disease.GIARDIASIS,
		Disease.YERSINIOSIS }, hide = true)
	VISIT(false),
	@Diseases(value = {
		Disease.CRYPTOSPORIDIOSIS,
		Disease.GIARDIASIS,
		Disease.YERSINIOSIS }, hide = true)
	GATHERING(true),
	@Diseases(value = {
		Disease.CRYPTOSPORIDIOSIS,
		Disease.GIARDIASIS,
		Disease.YERSINIOSIS }, hide = true)
	HABITATION(false),
	@Diseases(value = {
		Disease.CRYPTOSPORIDIOSIS,
		Disease.GIARDIASIS,
		Disease.YERSINIOSIS }, hide = true)
	PERSONAL_SERVICES(false),
	@Diseases(value = {
		Disease.RESPIRATORY_SYNCYTIAL_VIRUS })
	CHILDCARE_FACILITY(false),
	@Diseases(value = {
		Disease.CORONAVIRUS,
		Disease.GIARDIASIS,
		Disease.CRYPTOSPORIDIOSIS,
		Disease.YERSINIOSIS }, hide = true)
	BURIAL(false),
	@Diseases(value = {
		Disease.CORONAVIRUS,
		Disease.YERSINIOSIS })
	ANIMAL_CONTACT(false),
	@Diseases({
		Disease.GIARDIASIS,
		Disease.CRYPTOSPORIDIOSIS,
		Disease.YERSINIOSIS })
	RECREATIONAL_WATER(false, ExposureCategory.WATER_BORNE),
	@Diseases({
		Disease.GIARDIASIS,
		Disease.CRYPTOSPORIDIOSIS,
		Disease.YERSINIOSIS })
	FOOD(false, ExposureCategory.FOOD_BORNE),
	@Diseases({
		Disease.YERSINIOSIS })
	RAW_PET_FOOD(false, ExposureCategory.FOOD_BORNE),
	@Diseases({
		Disease.GIARDIASIS,
		Disease.CRYPTOSPORIDIOSIS })
	SEXUAL_CONTACT(false, ExposureCategory.DIRECT_CONTACT),
	@Diseases({
		Disease.CRYPTOSPORIDIOSIS,
		Disease.YERSINIOSIS })
	SYMPTOMATIC_CONTACT(false, ExposureCategory.DIRECT_CONTACT),
	@Diseases({
		Disease.CRYPTOSPORIDIOSIS,
		Disease.GIARDIASIS,
		Disease.YERSINIOSIS })
	FLOOD_EXPOSURE(false, ExposureCategory.WATER_BORNE),
	OTHER(true),
	UNKNOWN(true);

	private final boolean defaultType;
	private final Set<ExposureCategory> categories;

	ExposureType(boolean defaultType, ExposureCategory... categories) {
		this.defaultType = defaultType;
		this.categories = categories.length == 0 ? Collections.emptySet() : Collections.unmodifiableSet(EnumSet.copyOf(Arrays.asList(categories)));
	}

	public boolean isDefaultType() {
		return defaultType;
	}

	public Set<ExposureCategory> getCategories() {
		return categories;
	}

	public static List<ExposureType> getValues(Collection<ExposureCategory> diseaseCategories) {
		boolean hasConfig = diseaseCategories != null && !diseaseCategories.isEmpty();
		Set<ExposureCategory> configured = hasConfig ? EnumSet.copyOf(diseaseCategories) : EnumSet.noneOf(ExposureCategory.class);

		return Arrays.stream(values()).filter(type -> {
			if (type.isDefaultType()) {
				return true;
			}
			if (!hasConfig) {
				return false;
			}
			return type.getCategories().stream().anyMatch(configured::contains);
		}).collect(Collectors.toList());
	}

	@Override
	public String toString() {
		return I18nProperties.getEnumCaption(this);
	}
}
