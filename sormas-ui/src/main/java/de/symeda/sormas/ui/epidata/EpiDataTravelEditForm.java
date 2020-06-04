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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *******************************************************************************/
package de.symeda.sormas.ui.epidata;

import static de.symeda.sormas.ui.utils.LayoutUtil.fluidRowLocs;

import com.vaadin.v7.ui.ComboBox;
import com.vaadin.v7.ui.DateField;
import com.vaadin.v7.ui.TextField;

import de.symeda.sormas.api.epidata.EpiDataTravelDto;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Validations;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.ui.utils.AbstractEditForm;
import de.symeda.sormas.ui.utils.DateComparisonValidator;
import de.symeda.sormas.ui.utils.FieldHelper;

public class EpiDataTravelEditForm extends AbstractEditForm<EpiDataTravelDto> {
	
	private static final long serialVersionUID = 1L;
	
	private static final String HTML_LAYOUT = 
			fluidRowLocs(EpiDataTravelDto.TRAVEL_DATE_FROM, EpiDataTravelDto.TRAVEL_DATE_TO) +
			fluidRowLocs(EpiDataTravelDto.TRAVEL_TYPE, EpiDataTravelDto.TRAVEL_DESTINATION);
	
	public EpiDataTravelEditForm(UserRight editOrCreateUserRight) {
		super(EpiDataTravelDto.class, EpiDataTravelDto.I18N_PREFIX, editOrCreateUserRight);
		
		setWidth(540, Unit.PIXELS);
	}
	
	@Override
	protected void addFields() {
		DateField travelDateFrom = addField(EpiDataTravelDto.TRAVEL_DATE_FROM, DateField.class);
		DateField travelDateTo = addField(EpiDataTravelDto.TRAVEL_DATE_TO, DateField.class);
		travelDateFrom.addValidator(new DateComparisonValidator(travelDateFrom, travelDateTo, true, true, 
				I18nProperties.getValidationError(Validations.beforeDate, travelDateFrom.getCaption(), travelDateTo.getCaption())));
		travelDateTo.addValidator(new DateComparisonValidator(travelDateTo, travelDateFrom, false, true, 
				I18nProperties.getValidationError(Validations.afterDate, travelDateFrom.getCaption(), travelDateTo.getCaption())));
		addField(EpiDataTravelDto.TRAVEL_TYPE, ComboBox.class);
		addField(EpiDataTravelDto.TRAVEL_DESTINATION, TextField.class);
		
		FieldHelper.addSoftRequiredStyle(travelDateFrom, travelDateTo);
	}
	
	@Override
	protected String createHtmlLayout() {
		return HTML_LAYOUT;
	}

}
