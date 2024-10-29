/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2024 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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
 */

package de.symeda.sormas.ui.adverseeventsfollowingimmunization.aefilink;

import org.apache.commons.lang3.StringUtils;

import com.vaadin.ui.Label;

import de.symeda.sormas.api.adverseeventsfollowingimmunization.AefiListEntryDto;
import de.symeda.sormas.api.adverseeventsfollowingimmunization.AefiType;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.utils.YesNoUnknown;
import de.symeda.sormas.api.vaccination.VaccinationDto;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.DateFormatHelper;
import de.symeda.sormas.ui.utils.components.sidecomponent.SideComponentField;

@SuppressWarnings("serial")
public class AefiListEntry extends SideComponentField {

	public static final String SEPARATOR = ": ";

	private final AefiListEntryDto aefiListEntryDto;

	public AefiListEntry(AefiListEntryDto aefiListEntryDto) {

		this.aefiListEntryDto = aefiListEntryDto;

		Label labelAefiType = new Label(AefiType.toString(aefiListEntryDto.getSerious()));
		CssStyles.style(labelAefiType, CssStyles.LABEL_BOLD, CssStyles.LABEL_UPPERCASE);
		if (aefiListEntryDto.getSerious() == YesNoUnknown.YES) {
			CssStyles.style(labelAefiType, CssStyles.LABEL_CRITICAL);
		}
		addComponentToField(labelAefiType);

		Label labelVaccineName = new Label(aefiListEntryDto.getPrimaryVaccineName().toString());
		CssStyles.style(labelVaccineName, CssStyles.LABEL_BOLD, CssStyles.LABEL_UPPERCASE);
		addComponentToField(labelVaccineName);

		if (!StringUtils.isBlank(aefiListEntryDto.getPrimaryVaccineDose())) {
			Label labelVaccineDose = new Label(
				I18nProperties.getPrefixCaption(VaccinationDto.I18N_PREFIX, VaccinationDto.VACCINE_DOSE)
					+ SEPARATOR
					+ aefiListEntryDto.getPrimaryVaccineDose());
			CssStyles.style(labelVaccineDose, CssStyles.LABEL_BOLD, CssStyles.LABEL_UPPERCASE);
			addComponentToField(labelVaccineDose);
		}

		Label labelVaccineDate = new Label(
			I18nProperties.getPrefixCaption(AefiListEntryDto.I18N_PREFIX, AefiListEntryDto.PRIMARY_VACCINE_VACCINATION_DATE)
				+ SEPARATOR
				+ DateFormatHelper.formatLocalDate(aefiListEntryDto.getPrimaryVaccineVaccinationDate()));
		addComponentToField(labelVaccineDate);

		Label labelAdverseEvents = new Label(StringUtils.abbreviate(aefiListEntryDto.getAdverseEvents(), 56));
		CssStyles.style(labelAdverseEvents, CssStyles.LABEL_BOLD, CssStyles.LABEL_UPPERCASE);
		addComponentToField(labelAdverseEvents);
	}

	public AefiListEntryDto getAefiListEntryDto() {
		return aefiListEntryDto;
	}
}
