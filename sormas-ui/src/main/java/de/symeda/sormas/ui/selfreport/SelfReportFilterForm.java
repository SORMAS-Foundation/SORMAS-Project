/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2024 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package de.symeda.sormas.ui.selfreport;

import de.symeda.sormas.api.selfreport.SelfReportCriteria;
import de.symeda.sormas.api.selfreport.SelfReportDto;
import de.symeda.sormas.ui.utils.AbstractFilterForm;

public class SelfReportFilterForm extends AbstractFilterForm<SelfReportCriteria> {

	private static final long serialVersionUID = -8445740290879441416L;

	protected SelfReportFilterForm() {
		super(SelfReportCriteria.class, SelfReportDto.I18N_PREFIX, null);
	}

	@Override
	protected String[] getMainFilterLocators() {
		return new String[0];
	}

	@Override
	protected void addFields() {

	}
}
