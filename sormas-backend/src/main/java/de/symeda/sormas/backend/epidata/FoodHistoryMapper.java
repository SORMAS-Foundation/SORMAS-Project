/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2026 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 */

package de.symeda.sormas.backend.epidata;

import java.util.EnumMap;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import de.symeda.sormas.api.epidata.FoodConsumptionItem;
import de.symeda.sormas.api.epidata.FoodHistoryDto;
import de.symeda.sormas.backend.util.DtoHelper;

@LocalBean
@Stateless(name = "FoodHistoryMapper")
public class FoodHistoryMapper {

	public static FoodHistoryDto toDto(FoodHistory source) {
		if (source == null) {
			return null;
		}

		FoodHistoryDto target = new FoodHistoryDto();
		DtoHelper.fillDto(target, source);

		EnumMap<FoodConsumptionItem, String> copy = new EnumMap<>(FoodConsumptionItem.class);
		if (source.getConsumedItemsDetails() != null) {
			copy.putAll(source.getConsumedItemsDetails());
		}
		target.setConsumedItemsDetails(copy);
		target.setOtherFoodDetails(source.getOtherFoodDetails());

		return target;
	}

	public FoodHistory fillOrBuildEntity(FoodHistoryDto source, FoodHistory target, boolean checkChangeDate) {
		if (source == null) {
			return null;
		}

		target = DtoHelper.fillOrBuildEntity(source, target, FoodHistory::new, checkChangeDate);

		// Replace contents of the persistent collection rather than reassign, so JPA tracks orphans correctly
		target.getConsumedItemsDetails().clear();
		if (source.getConsumedItemsDetails() != null) {
			target.getConsumedItemsDetails().putAll(source.getConsumedItemsDetails());
		}
		target.setOtherFoodDetails(source.getOtherFoodDetails());

		return target;
	}
}
