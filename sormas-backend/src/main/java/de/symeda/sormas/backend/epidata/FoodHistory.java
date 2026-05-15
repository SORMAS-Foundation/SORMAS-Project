/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2026 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 */

package de.symeda.sormas.backend.epidata;

import static de.symeda.sormas.api.utils.FieldConstraints.CHARACTER_LIMIT_DEFAULT;

import java.util.EnumMap;
import java.util.Map;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.MapKeyColumn;
import javax.persistence.MapKeyEnumerated;

import de.symeda.sormas.api.epidata.FoodConsumptionItem;
import de.symeda.sormas.backend.common.AbstractDomainObject;

/**
 * Salmonellosis (Luxembourg) food consumption history attached 1:1 to {@link EpiData}.
 * Persisted as parent table {@code foodhistory} + value-collection table
 * {@code foodhistory_consumeditems}. Presence of an entry in {@link #consumedItemsDetails}
 * means the patient consumed the item; the value (possibly null) carries free-text details.
 */
@Entity
public class FoodHistory extends AbstractDomainObject {

	private static final long serialVersionUID = -2148717635193517125L;

	public static final String TABLE_NAME = "foodhistory";

	private Map<FoodConsumptionItem, String> consumedItemsDetails = new EnumMap<>(FoodConsumptionItem.class);
	private String otherFoodDetails;

	@ElementCollection(fetch = FetchType.EAGER)
	@CollectionTable(name = "foodhistory_consumeditems",
		joinColumns = @JoinColumn(name = "foodhistory_id", referencedColumnName = FoodHistory.ID, nullable = false))
	@MapKeyColumn(name = "item", nullable = false, length = 64)
	@MapKeyEnumerated(EnumType.STRING)
	@Column(name = "details", length = CHARACTER_LIMIT_DEFAULT)
	public Map<FoodConsumptionItem, String> getConsumedItemsDetails() {
		return consumedItemsDetails;
	}

	public void setConsumedItemsDetails(Map<FoodConsumptionItem, String> consumedItemsDetails) {
		this.consumedItemsDetails = consumedItemsDetails;
	}

	@Column(length = CHARACTER_LIMIT_DEFAULT)
	public String getOtherFoodDetails() {
		return otherFoodDetails;
	}

	public void setOtherFoodDetails(String otherFoodDetails) {
		this.otherFoodDetails = otherFoodDetails;
	}
}
