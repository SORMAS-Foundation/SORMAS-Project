/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2026 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 */

package de.symeda.sormas.api.exposure;

import de.symeda.sormas.api.i18n.I18nProperties;

public enum EatingOutVenue {

	CANTEENS,
	FOOD_SERVED_ON_BOARD,
	GAS_STATIONS,
	STREET_VENDORS,
	FOOD_COURTS,
	CAFES_OR_TEA_ROOMS,
	SANDWICH_SHOPS_BAKERIES_DELICATESSENS,
	CAFETERIAS_OR_BARS,
	KEBAB_FALAFEL_SNACK_SHOPS,
	BURGER_RESTAURANTS,
	FAST_FOOD_SNACK_BARS,
	PIZZERIAS,
	ASIAN_RESTAURANTS,
	HOTELS,
	TAKEAWAY_FOOD,
	BUFFET_STYLE_RESTAURANTS,
	FOOD_AT_EVENTS,
	OTHER;

	@Override
	public String toString() {
		return I18nProperties.getEnumCaption(this);
	}
}
