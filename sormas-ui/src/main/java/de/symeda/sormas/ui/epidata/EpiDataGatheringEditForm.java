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
import com.vaadin.v7.ui.TextArea;

import de.symeda.sormas.api.epidata.EpiDataGatheringDto;
import de.symeda.sormas.ui.location.LocationEditForm;
import de.symeda.sormas.ui.utils.AbstractEditForm;
import de.symeda.sormas.ui.utils.FieldHelper;

public class EpiDataGatheringEditForm extends AbstractEditForm<EpiDataGatheringDto> {

	private static final long serialVersionUID = 1L;
	
	private static final String HTML_LAYOUT =
			fluidRowLocs(EpiDataGatheringDto.GATHERING_DATE, "") +
			fluidRowLocs(EpiDataGatheringDto.DESCRIPTION) +
			fluidRowLocs(EpiDataGatheringDto.GATHERING_ADDRESS);
	
	public EpiDataGatheringEditForm() {
		super(EpiDataGatheringDto.class, EpiDataGatheringDto.I18N_PREFIX);
		
		setWidth(540, Unit.PIXELS);
	}	
	
	@Override
	protected void addFields() {
		DateField gatheringDate = addField(EpiDataGatheringDto.GATHERING_DATE, DateField.class);
		addField(EpiDataGatheringDto.DESCRIPTION, TextArea.class).setRows(2);
		addField(EpiDataGatheringDto.GATHERING_ADDRESS, LocationEditForm.class).setCaption(null);
		
		FieldHelper.addSoftRequiredStyle(gatheringDate);
	}
	
	@Override
	protected String createHtmlLayout() {
		return HTML_LAYOUT;
	}
	
}
