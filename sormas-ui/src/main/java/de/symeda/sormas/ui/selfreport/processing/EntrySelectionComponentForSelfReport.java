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

package de.symeda.sormas.ui.selfreport.processing;

import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.ui.utils.processing.EntrySelectionComponent;
import de.symeda.sormas.ui.utils.processing.EntrySelectionField;

public class EntrySelectionComponentForSelfReport extends EntrySelectionComponent {

	private static final long serialVersionUID = 5256354894814802005L;

	public EntrySelectionComponentForSelfReport(EntrySelectionField.Options selectableOptions) {
		super(selectableOptions, Strings.infoSelfReportSelectOrCreateEntry, Strings.infoSelfReportCreateEntry);
	}

	@Override
	protected void createAndAddSearchFieldComponents() {

	}
}
