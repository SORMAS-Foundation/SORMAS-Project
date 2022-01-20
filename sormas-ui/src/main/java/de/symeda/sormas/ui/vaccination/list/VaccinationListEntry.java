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

package de.symeda.sormas.ui.vaccination.list;

import org.apache.commons.lang3.StringUtils;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;

import de.symeda.sormas.api.caze.Vaccine;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.api.vaccination.VaccinationDto;
import de.symeda.sormas.api.vaccination.VaccinationListEntryDto;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.DateFormatHelper;
import de.symeda.sormas.ui.utils.components.sidecomponent.SideComponentField;

public class VaccinationListEntry extends SideComponentField {

	private static final long serialVersionUID = -8775209997959611902L;
	public static final String SEPARATOR = ": ";

	private final VaccinationListEntryDto vaccination;

	public VaccinationListEntry(VaccinationListEntryDto vaccination, boolean showDisease) {
		this.vaccination = vaccination;

		buildLayout(showDisease);
		setEnabled(vaccination.isRelevant());
	}

	private void buildLayout(boolean showDisease) {
		HorizontalLayout uuidDateLayout = new HorizontalLayout();
		uuidDateLayout.setMargin(false);
		uuidDateLayout.setSpacing(true);
		uuidDateLayout.setWidthFull();

		Label uuidLabel = new Label(DataHelper.getShortUuid(vaccination.getUuid()));
		uuidLabel.setDescription(vaccination.getUuid());
		CssStyles.style(uuidLabel, CssStyles.LABEL_BOLD, CssStyles.LABEL_UPPERCASE);
		uuidDateLayout.addComponent(uuidLabel);
		uuidDateLayout.setComponentAlignment(uuidLabel, Alignment.MIDDLE_LEFT);

		Label dateLabel = new Label(
			vaccination.getVaccinationDate() != null
				? DateFormatHelper.formatDate(vaccination.getVaccinationDate())
				: I18nProperties.getString(Strings.labelNoVaccinationDate));
		CssStyles.style(dateLabel, CssStyles.LABEL_BOLD, CssStyles.LABEL_UPPERCASE);
		uuidDateLayout.addComponent(dateLabel);
		uuidDateLayout.setComponentAlignment(dateLabel, Alignment.MIDDLE_RIGHT);
		addComponentToField(uuidDateLayout);

		HorizontalLayout vaccineNameAndInfoLayout = new HorizontalLayout();
		vaccineNameAndInfoLayout.setMargin(false);
		vaccineNameAndInfoLayout.setSpacing(true);
		vaccineNameAndInfoLayout.setWidthFull();

		String vaccine = vaccination.getVaccineName() != null
			? (vaccination.getVaccineName() == Vaccine.OTHER ? vaccination.getOtherVaccineName() : vaccination.getVaccineName().toString())
			: null;
		Label vaccineLabel = new Label(
			StringUtils.isNotBlank(vaccine)
				? I18nProperties.getPrefixCaption(VaccinationDto.I18N_PREFIX, VaccinationDto.VACCINE_NAME) + SEPARATOR + vaccine
				: I18nProperties.getString(Strings.labelNoVaccineName));
		vaccineNameAndInfoLayout.addComponent(vaccineLabel);
		vaccineNameAndInfoLayout.setComponentAlignment(vaccineLabel, Alignment.MIDDLE_LEFT);

		if (!vaccination.isRelevant()) {
			Label vaccinationNotRelevantInfo = new Label(VaadinIcons.INFO_CIRCLE.getHtml(), ContentMode.HTML);
			vaccinationNotRelevantInfo.setDescription(vaccination.getNonRelevantMessage());
			vaccineNameAndInfoLayout.addComponent(vaccinationNotRelevantInfo);
			vaccineNameAndInfoLayout.setComponentAlignment(vaccinationNotRelevantInfo, Alignment.MIDDLE_RIGHT);
		}

		addComponentToField(vaccineNameAndInfoLayout);

		if (showDisease) {
			Label diseaseLabel = new Label(vaccination.getDisease().toString());
			addComponentToField(diseaseLabel);
		}
	}

	public VaccinationListEntryDto getVaccination() {
		return vaccination;
	}

}
