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
package de.symeda.sormas.ui.events;

import de.symeda.sormas.api.action.ActionCriteria;
import de.symeda.sormas.api.action.ActionDto;
import de.symeda.sormas.ui.utils.AbstractFilterForm;
import de.symeda.sormas.ui.utils.FieldConfiguration;

/**
 * Form for filtering actions related to an event.
 */
public class EventActionFilterForm extends AbstractFilterForm<ActionCriteria> {

	private static final long serialVersionUID = -8661345403078183132L;

	protected EventActionFilterForm() {
		super(ActionCriteria.class, ActionDto.I18N_PREFIX, null);
	}

	@Override
	protected String[] getMainFilterLocators() {
		return new String[] {
			ActionDto.ACTION_STATUS };
	}

	@Override
	protected void addFields() {
		addField(FieldConfiguration.pixelSized(ActionDto.ACTION_STATUS, 200));
	}
}
