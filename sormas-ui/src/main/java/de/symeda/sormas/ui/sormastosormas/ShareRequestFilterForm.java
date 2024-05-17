/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2021 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

package de.symeda.sormas.ui.sormastosormas;

import com.vaadin.v7.ui.ComboBox;

import de.symeda.sormas.api.sormastosormas.share.ShareRequestCriteria;
import de.symeda.sormas.api.sormastosormas.share.incoming.ShareRequestStatus;
import de.symeda.sormas.api.sormastosormas.share.incoming.SormasToSormasShareRequestDto;
import de.symeda.sormas.ui.utils.AbstractFilterForm;
import de.symeda.sormas.ui.utils.FieldConfiguration;

public class ShareRequestFilterForm extends AbstractFilterForm<ShareRequestCriteria> {

	private static final long serialVersionUID = 1551296359856190303L;

	private final ShareRequestViewType viewType;

	protected ShareRequestFilterForm(ShareRequestViewType viewType) {
		super(ShareRequestCriteria.class, SormasToSormasShareRequestDto.I18N_PREFIX, null, false);

		this.viewType = viewType;
		addFields();
	}

	@Override
	protected String[] getMainFilterLocators() {
		return new String[] {
			ShareRequestCriteria.STATUS };
	}

	@Override
	protected void addFields() {
		ComboBox statusFiled = addField(FieldConfiguration.pixelSized(ShareRequestCriteria.STATUS, 140));

		if (viewType == ShareRequestViewType.INCOMING) {
			statusFiled.removeItem(ShareRequestStatus.REJECTED);
			statusFiled.removeItem(ShareRequestStatus.REVOKED);
		}
	}
}
