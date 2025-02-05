/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2025 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

package de.symeda.sormas.ui.survey;

import com.vaadin.server.Sizeable.Unit;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.survey.SurveyTokenDto;
import de.symeda.sormas.api.survey.SurveyTokenReferenceDto;
import de.symeda.sormas.ui.utils.CommitDiscardWrapperComponent;
import de.symeda.sormas.ui.utils.VaadinUiUtil;

public class SurveyTokenController {

	public void showCaseSurveyDetails(SurveyTokenReferenceDto surveyToken, Runnable callback) {
		SurveyTokenDataForm form = new SurveyTokenDataForm();
		form.setWidth(600, Unit.PIXELS);
		form.setValue(FacadeProvider.getSurveyTokenFacade().getByUuid(surveyToken.getUuid()));

		CommitDiscardWrapperComponent<SurveyTokenDataForm> editView = new CommitDiscardWrapperComponent<>(form, form.getFieldGroup());
		editView.addCommitListener(() -> {
			SurveyTokenDto editedSurveyToken = form.getValue();
			FacadeProvider.getSurveyTokenFacade().save(editedSurveyToken);
			callback.run();
		});

		VaadinUiUtil.showModalPopupWindow(editView, I18nProperties.getString(Strings.headingCaseSurveyDetails));
	}
}
