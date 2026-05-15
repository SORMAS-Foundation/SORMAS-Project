/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2026 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 */

package de.symeda.sormas.api.epidata;

import java.util.EnumMap;
import java.util.Map;

import javax.validation.constraints.Size;

import de.symeda.sormas.api.feature.FeatureType;
import de.symeda.sormas.api.i18n.Validations;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.api.utils.DependingOnFeatureType;
import de.symeda.sormas.api.utils.FieldConstraints;
import de.symeda.sormas.api.utils.pseudonymization.PseudonymizableDto;

/**
 * Salmonellosis (Luxembourg) food consumption history captured during the 5-day window before symptom onset.
 * Source: SALM_enquête_publipostage.docx, Alimentation / Ernährung / Food section.
 * Presence of a key in {@link #consumedItemsDetails} means the item was consumed; the value (possibly empty)
 * carries any free-text details.
 */
@DependingOnFeatureType(featureType = {
	FeatureType.CASE_SURVEILANCE,
	FeatureType.CONTACT_TRACING })
public class FoodHistoryDto extends PseudonymizableDto {

	private static final long serialVersionUID = -2148717635193517125L;

	public static final String I18N_PREFIX = "FoodHistory";

	public static final String CONSUMED_ITEMS_DETAILS = "consumedItemsDetails";
	public static final String OTHER_FOOD_DETAILS = "otherFoodDetails";

	private Map<FoodConsumptionItem, String> consumedItemsDetails = new EnumMap<>(FoodConsumptionItem.class);

	@Size(max = FieldConstraints.CHARACTER_LIMIT_DEFAULT, message = Validations.textTooLong)
	private String otherFoodDetails;

	public static FoodHistoryDto build() {
		FoodHistoryDto dto = new FoodHistoryDto();
		dto.setUuid(DataHelper.createUuid());
		return dto;
	}

	public Map<FoodConsumptionItem, String> getConsumedItemsDetails() {
		return consumedItemsDetails;
	}

	public void setConsumedItemsDetails(Map<FoodConsumptionItem, String> consumedItemsDetails) {
		this.consumedItemsDetails =
			consumedItemsDetails == null ? new EnumMap<>(FoodConsumptionItem.class) : new EnumMap<>(consumedItemsDetails);
	}

	public String getOtherFoodDetails() {
		return otherFoodDetails;
	}

	public void setOtherFoodDetails(String otherFoodDetails) {
		this.otherFoodDetails = otherFoodDetails;
	}

	@Override
	public FoodHistoryDto clone() throws CloneNotSupportedException {
		FoodHistoryDto clone = (FoodHistoryDto) super.clone();
		clone.setConsumedItemsDetails(consumedItemsDetails);
		return clone;
	}
}
