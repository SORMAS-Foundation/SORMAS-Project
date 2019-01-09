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
package de.symeda.sormas.ui.caze;

import com.vaadin.ui.AbstractLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.I18nProperties;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.person.ApproximateAgeType.ApproximateAgeHelper;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.symptoms.SymptomsDto;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.ui.CurrentUser;

@SuppressWarnings("serial")
public class CaseInfoLayout extends HorizontalLayout {

	private final CaseDataDto caseDto;

	public CaseInfoLayout(CaseDataDto caseDto) {
		this.caseDto = caseDto;
		this.setSpacing(true);
		this.setWidth(100, Unit.PERCENTAGE);
		updateCaseInfo();
	}

	private void updateCaseInfo() {
		this.removeAllComponents();

		PersonDto personDto = FacadeProvider.getPersonFacade().getPersonByUuid(caseDto.getPerson().getUuid());

		VerticalLayout leftColumnLayout = new VerticalLayout();
		leftColumnLayout.setSpacing(true);
		{
			addDescLabel(leftColumnLayout, DataHelper.getShortUuid(caseDto.getUuid()),
					I18nProperties.getPrefixFieldCaption(CaseDataDto.I18N_PREFIX, CaseDataDto.UUID))
			.setDescription(caseDto.getUuid());

			if (CurrentUser.getCurrent().hasUserRight(UserRight.CASE_VIEW)) {
				addDescLabel(leftColumnLayout, caseDto.getPerson(),
						I18nProperties.getPrefixFieldCaption(CaseDataDto.I18N_PREFIX, CaseDataDto.PERSON));

				HorizontalLayout ageSexLayout = new HorizontalLayout();
				ageSexLayout.setSpacing(true);
				addDescLabel(ageSexLayout, ApproximateAgeHelper.formatApproximateAge(
						personDto.getApproximateAge(),personDto.getApproximateAgeType()),
						I18nProperties.getPrefixFieldCaption(PersonDto.I18N_PREFIX, PersonDto.APPROXIMATE_AGE));
				addDescLabel(ageSexLayout, personDto.getSex(),
						I18nProperties.getPrefixFieldCaption(PersonDto.I18N_PREFIX, PersonDto.SEX));
				leftColumnLayout.addComponent(ageSexLayout);
			}
		}
		this.addComponent(leftColumnLayout);

		VerticalLayout rightColumnLayout = new VerticalLayout();
		rightColumnLayout.setSpacing(true);
		{
			addDescLabel(rightColumnLayout, 
					caseDto.getDisease() != Disease.OTHER 
					? caseDto.getDisease().toShortString()
							: DataHelper.toStringNullable(caseDto.getDiseaseDetails()),
							I18nProperties.getPrefixFieldCaption(CaseDataDto.I18N_PREFIX, CaseDataDto.DISEASE));

			if (CurrentUser.getCurrent().hasUserRight(UserRight.CASE_VIEW)) {
				addDescLabel(rightColumnLayout, caseDto.getCaseClassification(),
						I18nProperties.getPrefixFieldCaption(CaseDataDto.I18N_PREFIX, CaseDataDto.CASE_CLASSIFICATION));
				addDescLabel(rightColumnLayout, DateHelper.formatLocalShortDate(caseDto.getSymptoms().getOnsetDate()),
						I18nProperties.getPrefixFieldCaption(SymptomsDto.I18N_PREFIX, SymptomsDto.ONSET_DATE));
			}
		}
		this.addComponent(rightColumnLayout);
	}

	private static Label addDescLabel(AbstractLayout layout, Object content, String caption) {
		String contentString = content != null ? content.toString() : "";
		Label label = new Label(contentString);
		label.setCaption(caption);
		layout.addComponent(label);
		return label;
	}

}
