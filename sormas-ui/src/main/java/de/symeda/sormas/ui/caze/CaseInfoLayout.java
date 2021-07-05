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
package de.symeda.sormas.ui.caze;

import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

import de.symeda.sormas.api.CountryHelper;
import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.DiseaseHelper;
import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.person.ApproximateAgeType.ApproximateAgeHelper;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.symptoms.SymptomsDto;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.api.utils.fieldaccess.UiFieldAccessCheckers;
import de.symeda.sormas.ui.AbstractInfoLayout;
import de.symeda.sormas.ui.UserProvider;
import de.symeda.sormas.ui.utils.DateFormatHelper;

@SuppressWarnings("serial")
public class CaseInfoLayout extends AbstractInfoLayout<CaseDataDto> {

	private final CaseDataDto caseDto;

	public CaseInfoLayout(CaseDataDto caseDto) {
		super(CaseDataDto.class, UiFieldAccessCheckers.getDefault(caseDto.isPseudonymized()));

		this.caseDto = caseDto;
		setSpacing(true);
		setMargin(false);
		setWidth(100, Unit.PERCENTAGE);
		updateCaseInfo();
	}

	private void updateCaseInfo() {

		this.removeAllComponents();

		PersonDto personDto = FacadeProvider.getPersonFacade().getPersonByUuid(caseDto.getPerson().getUuid());

		VerticalLayout leftColumnLayout = new VerticalLayout();
		leftColumnLayout.setMargin(false);
		leftColumnLayout.setSpacing(true);
		{
			final Label caseIdLabel = addDescLabel(
				leftColumnLayout,
				CaseDataDto.UUID,
				DataHelper.getShortUuid(caseDto.getUuid()),
				I18nProperties.getPrefixCaption(CaseDataDto.I18N_PREFIX, CaseDataDto.UUID));
			caseIdLabel.setId("caseIdLabel");
			caseIdLabel.setDescription(caseDto.getUuid());

			if (FacadeProvider.getConfigFacade().isConfiguredCountry(CountryHelper.COUNTRY_CODE_GERMANY)) {
				addDescLabel(
					leftColumnLayout,
					CaseDataDto.EXTERNAL_ID,
					caseDto.getExternalID(),
					I18nProperties.getPrefixCaption(CaseDataDto.I18N_PREFIX, CaseDataDto.EXTERNAL_ID)).setDescription(caseDto.getEpidNumber());
				addDescLabel(
						leftColumnLayout,
						CaseDataDto.EXTERNAL_TOKEN,
						caseDto.getExternalToken(),
						I18nProperties.getPrefixCaption(CaseDataDto.I18N_PREFIX, CaseDataDto.EXTERNAL_TOKEN)).setDescription(caseDto.getExternalToken());
			} else {
				addDescLabel(
					leftColumnLayout,
					CaseDataDto.EPID_NUMBER,
					caseDto.getEpidNumber(),
					I18nProperties.getPrefixCaption(CaseDataDto.I18N_PREFIX, CaseDataDto.EPID_NUMBER)).setDescription(caseDto.getEpidNumber());
			}

			if (UserProvider.getCurrent().hasUserRight(UserRight.CASE_VIEW)) {
				addDescLabel(
					leftColumnLayout,
					CaseDataDto.PERSON,
					caseDto.getPerson(),
					I18nProperties.getPrefixCaption(CaseDataDto.I18N_PREFIX, CaseDataDto.PERSON));

				HorizontalLayout ageSexLayout = new HorizontalLayout();
				ageSexLayout.setMargin(false);
				ageSexLayout.setSpacing(true);
				addCustomDescLabel(
					ageSexLayout,
					PersonDto.class,
					PersonDto.APPROXIMATE_AGE,
					ApproximateAgeHelper.formatApproximateAge(personDto.getApproximateAge(), personDto.getApproximateAgeType()),
					I18nProperties.getPrefixCaption(PersonDto.I18N_PREFIX, PersonDto.APPROXIMATE_AGE));
				addCustomDescLabel(
					ageSexLayout,
					PersonDto.class,
					PersonDto.SEX,
					personDto.getSex(),
					I18nProperties.getPrefixCaption(PersonDto.I18N_PREFIX, PersonDto.SEX));
				leftColumnLayout.addComponent(ageSexLayout);
			}

			if (UserProvider.getCurrent().hasUserRight(UserRight.CASE_MANAGEMENT_ACCESS)) {
				addDescLabel(
					leftColumnLayout,
					CaseDataDto.CLINICIAN_NAME,
					caseDto.getClinicianName(),
					I18nProperties.getPrefixCaption(CaseDataDto.I18N_PREFIX, CaseDataDto.CLINICIAN_NAME));
			}
		}
		this.addComponent(leftColumnLayout);

		VerticalLayout rightColumnLayout = new VerticalLayout();
		rightColumnLayout.setMargin(false);
		rightColumnLayout.setSpacing(true);
		{
			addDescLabel(
				rightColumnLayout,
				CaseDataDto.DISEASE,
				caseDto.getDisease() != Disease.OTHER
					? DiseaseHelper.toString(caseDto.getDisease(), caseDto.getDiseaseDetails(), caseDto.getDiseaseVariant())
					: DataHelper.toStringNullable(caseDto.getDiseaseDetails()),
				I18nProperties.getPrefixCaption(CaseDataDto.I18N_PREFIX, CaseDataDto.DISEASE));

			if (UserProvider.getCurrent().hasUserRight(UserRight.CASE_VIEW)) {
				addDescLabel(
					rightColumnLayout,
					CaseDataDto.CASE_CLASSIFICATION,
					caseDto.getCaseClassification(),
					I18nProperties.getPrefixCaption(CaseDataDto.I18N_PREFIX, CaseDataDto.CASE_CLASSIFICATION));
				addDescLabel(
					rightColumnLayout,
					CaseDataDto.SYMPTOMS,
					DateFormatHelper.formatDate(caseDto.getSymptoms().getOnsetDate()),
					I18nProperties.getPrefixCaption(SymptomsDto.I18N_PREFIX, SymptomsDto.ONSET_DATE));
			}
		}
		this.addComponent(rightColumnLayout);
	}
}
