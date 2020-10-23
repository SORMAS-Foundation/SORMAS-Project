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
package de.symeda.sormas.ui.epidata;

import static de.symeda.sormas.ui.utils.CssStyles.VSPACE_3;
import static de.symeda.sormas.ui.utils.LayoutUtil.divsCss;
import static de.symeda.sormas.ui.utils.LayoutUtil.fluidColumn;
import static de.symeda.sormas.ui.utils.LayoutUtil.fluidRow;
import static de.symeda.sormas.ui.utils.LayoutUtil.h3;
import static de.symeda.sormas.ui.utils.LayoutUtil.loc;
import static de.symeda.sormas.ui.utils.LayoutUtil.locsCss;

import com.vaadin.shared.ui.ContentMode;
import com.vaadin.ui.Label;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.epidata.EpiDataDto;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.utils.fieldaccess.UiFieldAccessCheckers;
import de.symeda.sormas.api.utils.fieldvisibility.FieldVisibilityCheckers;
import de.symeda.sormas.ui.utils.AbstractEditForm;
import de.symeda.sormas.ui.utils.CssStyles;

public class EpiDataForm extends AbstractEditForm<EpiDataDto> {

	private static final long serialVersionUID = 1L;

	private static final String EPI_DATA_CAPTION_LOC = "epiDataCaptionLoc";

	//@formatter:off
	private static final String HTML_LAYOUT = 
			loc(EPI_DATA_CAPTION_LOC) +
			fluidRow(
					fluidColumn(6, 0, locsCss(VSPACE_3,
							EpiDataDto.AREA_INFECTED_ANIMALS
					))
			);
	//@formatter:on

	private final Disease disease;

	public EpiDataForm(Disease disease, boolean isPseudonymized) {
		super(
			EpiDataDto.class,
			EpiDataDto.I18N_PREFIX,
			false,
			FieldVisibilityCheckers.withDisease(disease),
			UiFieldAccessCheckers.forSensitiveData(isPseudonymized));
		this.disease = disease;
		addFields();
	}

	@Override
	protected void addFields() {
		if (disease == null) {
			return;
		}

		addFields(EpiDataDto.AREA_INFECTED_ANIMALS);

		Label kindOfExposureLabel = new Label(I18nProperties.getCaption(Captions.EpiData_kindOfExposure));
		CssStyles.style(kindOfExposureLabel, CssStyles.H3);

		initializeVisibilitiesAndAllowedVisibilities();
		initializeAccessAndAllowedAccesses();

		String animalCaptionLayout = h3(I18nProperties.getString(Strings.headingAnimalContacts))
			+ divsCss(VSPACE_3, I18nProperties.getString(Strings.messageAnimalContactsHint));

		Label animalCaptionLabel = new Label(animalCaptionLayout);
		animalCaptionLabel.setContentMode(ContentMode.HTML);

		Label environmentalCaptionLabel = new Label(h3(I18nProperties.getString(Strings.headingEnvironmentalExposure)));
		environmentalCaptionLabel.setContentMode(ContentMode.HTML);
	}

	@Override
	protected String createHtmlLayout() {
		return HTML_LAYOUT;
	}
}
