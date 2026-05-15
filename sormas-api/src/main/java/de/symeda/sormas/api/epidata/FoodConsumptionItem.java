/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2026 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 */

package de.symeda.sormas.api.epidata;

import de.symeda.sormas.api.i18n.I18nProperties;

/**
 * Food consumption items captured by the Salmonellosis (Luxembourg) survey, 5-day window before symptom onset.
 * Source: SALM_enquête_publipostage.docx, Alimentation / Ernährung / Food section.
 */
public enum FoodConsumptionItem {

	TAP_WATER,
	ICE_CUBES,
	BOTTLED_WATER,
	OTHER_DRINKS,
	POULTRY,
	BEEF,
	PORK,
	OTHER_MEAT,
	FISH,
	SEAFOOD,
	EGGS,
	CHEESE,
	DAIRY,
	DESSERTS,
	VEGETABLES,
	FRUITS,
	DRIED_FRUITS,
	SPICES,
	PREPARED_MEALS,
	SNACKS;

	@Override
	public String toString() {
		return I18nProperties.getEnumCaption(this);
	}
}
