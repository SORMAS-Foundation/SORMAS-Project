/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2026 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 */

package de.symeda.sormas.ui.epidata;

import static de.symeda.sormas.ui.utils.LayoutUtil.fluidRowLocs;
import static de.symeda.sormas.ui.utils.LayoutUtil.loc;

import com.vaadin.ui.Label;
import com.vaadin.v7.ui.TextArea;

import de.symeda.sormas.api.epidata.FoodHistoryDto;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.utils.fieldaccess.UiFieldAccessCheckers;
import de.symeda.sormas.api.utils.fieldvisibility.FieldVisibilityCheckers;
import de.symeda.sormas.ui.utils.AbstractEditForm;
import de.symeda.sormas.ui.utils.CssStyles;

/**
 * Embedded sub-form for {@link FoodHistoryDto}, attached 1:1 to {@link de.symeda.sormas.api.epidata.EpiDataDto}.
 * Shipped for Salmonellosis (Luxembourg) — captures the 5-day food consumption history per the InSa survey.
 */
public class FoodHistoryForm extends AbstractEditForm<FoodHistoryDto> {

	private static final long serialVersionUID = 1L;

	private static final String FOOD_HISTORY_HEADING_LOC = "foodHistoryHeadingLoc";

	//@formatter:off
	private static final String HTML_LAYOUT =
		loc(FOOD_HISTORY_HEADING_LOC) +
		fluidRowLocs(FoodHistoryDto.CONSUMED_ITEMS_DETAILS) +
		fluidRowLocs(FoodHistoryDto.OTHER_FOOD_DETAILS);
	//@formatter:on

	public FoodHistoryForm(FieldVisibilityCheckers fieldVisibilityCheckers, UiFieldAccessCheckers fieldAccessCheckers) {
		super(FoodHistoryDto.class, FoodHistoryDto.I18N_PREFIX, true, fieldVisibilityCheckers, fieldAccessCheckers);
	}

	@Override
	protected String createHtmlLayout() {
		return HTML_LAYOUT;
	}

	@Override
	protected void addFields() {

		Label headingLabel = new Label(I18nProperties.getCaption(Captions.EpiData_foodHistory));
		headingLabel.addStyleName(CssStyles.VAADIN_CAPTION);
		getContent().addComponent(headingLabel, FOOD_HISTORY_HEADING_LOC);

		FoodHistoryItemsField itemsField = new FoodHistoryItemsField();
		addField(FoodHistoryDto.CONSUMED_ITEMS_DETAILS, itemsField).addStyleName(CssStyles.CAPTION_NO_UPPERCASE);

		TextArea otherFoodDetails = addField(FoodHistoryDto.OTHER_FOOD_DETAILS, TextArea.class);
		otherFoodDetails.setRows(2);
		otherFoodDetails.addStyleName(CssStyles.CAPTION_NO_UPPERCASE);
	}
}
