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

package de.symeda.sormas.ui.caze;

import static de.symeda.sormas.ui.utils.CssStyles.LABEL_WHITE_SPACE_NORMAL;
import static de.symeda.sormas.ui.utils.CssStyles.VSPACE_3;
import static de.symeda.sormas.ui.utils.LayoutUtil.fluidRowLocs;

import com.vaadin.ui.Label;

import de.symeda.sormas.api.hospitalization.HospitalizationDto;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.ui.utils.AbstractEditForm;
import de.symeda.sormas.ui.utils.NullableOptionGroup;

public class CurrentHospitalizationForm extends AbstractEditForm<HospitalizationDto> {

	private static final String MORE_DETAILS_LABEL = "moreDetailsField";

	public CurrentHospitalizationForm() {

		super(HospitalizationDto.class, HospitalizationDto.I18N_PREFIX, false, null, null);
		addFields();
	}

	@Override
	protected String createHtmlLayout() {
		return fluidRowLocs(HospitalizationDto.ADMITTED_TO_HEALTH_FACILITY) + fluidRowLocs(MORE_DETAILS_LABEL);
	}

	@Override
	protected void addFields() {
		addField(HospitalizationDto.ADMITTED_TO_HEALTH_FACILITY, NullableOptionGroup.class);
		Label moreDetailsLabel = new Label(I18nProperties.getString(Strings.infoMoreDetailsAboutHospitalization));
		moreDetailsLabel.addStyleNames(VSPACE_3, LABEL_WHITE_SPACE_NORMAL);
		moreDetailsLabel.setWidthFull();
		getContent().addComponent(moreDetailsLabel, MORE_DETAILS_LABEL);
	}
}
