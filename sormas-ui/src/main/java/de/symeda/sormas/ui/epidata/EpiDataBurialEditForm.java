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

import com.vaadin.v7.ui.DateField;
import com.vaadin.v7.ui.OptionGroup;
import com.vaadin.v7.ui.TextField;

import de.symeda.sormas.api.epidata.EpiDataBurialDto;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Validations;
import de.symeda.sormas.api.utils.fieldvisibility.FieldVisibilityCheckers;
import de.symeda.sormas.ui.location.LocationEditForm;
import de.symeda.sormas.ui.utils.AbstractEditForm;
import de.symeda.sormas.ui.utils.DateComparisonValidator;
import de.symeda.sormas.ui.utils.FieldHelper;

public class EpiDataBurialEditForm extends AbstractEditForm<EpiDataBurialDto> {

	private static final long serialVersionUID = 1L;
	
	private static final String HTML_LAYOUT = 
			fluidRowLocs(EpiDataBurialDto.BURIAL_DATE_FROM, EpiDataBurialDto.BURIAL_DATE_TO) +
			fluidRowLocs(EpiDataBurialDto.BURIAL_PERSON_NAME, EpiDataBurialDto.BURIAL_RELATION) +
			fluidRowLocs(EpiDataBurialDto.BURIAL_ADDRESS) +
			fluidRowLocs(EpiDataBurialDto.BURIAL_ILL, EpiDataBurialDto.BURIAL_TOUCHING);
	
	public EpiDataBurialEditForm(boolean create) {
		super(EpiDataBurialDto.class, EpiDataBurialDto.I18N_PREFIX, new FieldVisibilityCheckers());
		
		setWidth(540, Unit.PIXELS);
		
		if (create) {
			hideValidationUntilNextCommit();
		}
	}
	
	@Override
	protected void addFields() {
		DateField burialDateFrom = addField(EpiDataBurialDto.BURIAL_DATE_FROM, DateField.class);
		DateField burialDateTo = addField(EpiDataBurialDto.BURIAL_DATE_TO, DateField.class);
		burialDateFrom.addValidator(new DateComparisonValidator(burialDateFrom, burialDateTo, true, false, 
				I18nProperties.getValidationError(Validations.beforeDate, burialDateFrom.getCaption(), burialDateTo.getCaption())));
		burialDateTo.addValidator(new DateComparisonValidator(burialDateTo, burialDateFrom, false, false, 
				I18nProperties.getValidationError(Validations.afterDate, burialDateFrom.getCaption(), burialDateTo.getCaption())));
		addField(EpiDataBurialDto.BURIAL_PERSON_NAME, TextField.class);
		addField(EpiDataBurialDto.BURIAL_RELATION, TextField.class);
		addField(EpiDataBurialDto.BURIAL_ILL, OptionGroup.class);
		addField(EpiDataBurialDto.BURIAL_TOUCHING, OptionGroup.class);
		addField(EpiDataBurialDto.BURIAL_ADDRESS, LocationEditForm.class).setCaption(null);

		FieldHelper.addSoftRequiredStyle(burialDateFrom, burialDateTo);
		setRequired(true,
				EpiDataBurialDto.BURIAL_ILL,
				EpiDataBurialDto.BURIAL_TOUCHING);
		
	}
	
	@Override
	protected String createHtmlLayout() {
		return HTML_LAYOUT;
	}
	
}
