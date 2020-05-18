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
package de.symeda.sormas.ui.statistics;

import java.util.ArrayList;
import java.util.List;

import com.vaadin.v7.ui.DateField;

import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;

@SuppressWarnings("serial")
public class StatisticsFilterDateRangeElement extends StatisticsFilterElement {

	private DateField dateFromField;
	private DateField dateToField;
	
	public StatisticsFilterDateRangeElement(int rowIndex) {
		setSpacing(true);
		
		dateFromField = new DateField(I18nProperties.getCaption(Captions.from));
		dateFromField.setId("dateFrom-" + rowIndex);
		dateToField = new DateField(I18nProperties.getCaption(Captions.to));
		dateToField.setId("dateTo-" + rowIndex);
		
		addComponent(dateFromField);
		addComponent(dateToField);
	}
	
	@Override
	public List<TokenizableValue> getSelectedValues() {
		List<TokenizableValue> values = new ArrayList<>();
		TokenizableValue fromValue = new TokenizableValue(dateFromField.getValue(), 0);
		TokenizableValue toValue = new TokenizableValue(dateToField.getValue(), 1);
		values.add(fromValue);
		values.add(toValue);
		return values;
	}

}
