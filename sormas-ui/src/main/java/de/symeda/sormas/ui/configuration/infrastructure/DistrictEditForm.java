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
package de.symeda.sormas.ui.configuration.infrastructure;

import com.vaadin.v7.data.util.converter.StringToFloatConverter;
import com.vaadin.v7.ui.ComboBox;
import com.vaadin.v7.ui.TextField;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Validations;
import de.symeda.sormas.api.region.DistrictDto;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.ui.utils.AbstractEditForm;
import de.symeda.sormas.ui.utils.LayoutUtil;

public class DistrictEditForm extends AbstractEditForm<DistrictDto> {

	private static final long serialVersionUID = 7573666294384000190L;

	private static final String HTML_LAYOUT =
			LayoutUtil.fluidRowLocs(DistrictDto.NAME, DistrictDto.EPID_CODE)
			+ LayoutUtil.fluidRowLocs(DistrictDto.REGION, DistrictDto.GROWTH_RATE);

	private boolean create;
	
	public DistrictEditForm(UserRight editOrCreateUserRight, boolean create) {
		super(DistrictDto.class, DistrictDto.I18N_PREFIX, editOrCreateUserRight, false);
		this.create = create;
		
		setWidth(540, Unit.PIXELS);
		
		if (create) {
			hideValidationUntilNextCommit();
		}
		addFields();
	}
	
	@Override
	protected void addFields() {
		addField(DistrictDto.NAME, TextField.class);
		addField(DistrictDto.EPID_CODE, TextField.class);
		ComboBox region = addField(DistrictDto.REGION, ComboBox.class);
		TextField growthRate = addField(DistrictDto.GROWTH_RATE, TextField.class);
		growthRate.setConverter(new StringToFloatConverter());
		growthRate.setConversionError(I18nProperties.getValidationError(Validations.onlyDecimalNumbersAllowed, growthRate.getCaption()));

		setRequired(true, DistrictDto.NAME, DistrictDto.EPID_CODE, DistrictDto.REGION);
		
		region.addItems(FacadeProvider.getRegionFacade().getAllAsReference());		
		
		// TODO: Workaround until cases and other data is properly transfered when infrastructure data changes
		if (!create) {
			region.setEnabled(false);
		}
	}

	@Override
	protected String createHtmlLayout() {
		return HTML_LAYOUT;
	}

}
