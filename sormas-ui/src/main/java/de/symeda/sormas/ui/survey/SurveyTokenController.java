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

import com.vaadin.navigator.Navigator;
import com.vaadin.server.Sizeable.Unit;
import com.vaadin.ui.Notification;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.survey.SurveyTokenDto;
import de.symeda.sormas.api.survey.SurveyTokenReferenceDto;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.SormasUI;
import de.symeda.sormas.ui.UiUtil;
import de.symeda.sormas.ui.utils.CommitDiscardWrapperComponent;
import de.symeda.sormas.ui.utils.VaadinUiUtil;
import de.symeda.sormas.ui.utils.components.page.title.TitleLayout;

public class SurveyTokenController {

	public void registeredViews(Navigator navigator) {
		navigator.addView(SurveyTokenDataView.VIEW_NAME, SurveyTokenDataView.class);
	}

	public void navigateToSurveyToken(String surveyTokenUuid) {
		final String navigationState = SurveyTokenDataView.VIEW_NAME + "/" + surveyTokenUuid;
		SormasUI.get().getNavigator().navigateTo(navigationState);
	}

	private void navigateToIndex() {
		String navigationState = SurveyTokensView.VIEW_NAME;
		SormasUI.get().getNavigator().navigateTo(navigationState);
	}

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

	public TitleLayout getSurveyTokenViewTitleLayout(String uuid) {
		SurveyTokenDto surveyTokenDto = findSurveyToken(uuid);

		TitleLayout titleLayout = new TitleLayout();

		String shortUuid = DataHelper.getShortUuid(surveyTokenDto.getUuid());

		StringBuilder mainRowText = new StringBuilder(I18nProperties.getCaption(SurveyTokenDto.I18N_PREFIX));
		mainRowText.append(" (" + shortUuid + ")");

		titleLayout.addMainRow(mainRowText.toString());

		return titleLayout;
	}

	private SurveyTokenDto findSurveyToken(String uuid) {
		return FacadeProvider.getSurveyTokenFacade().getByUuid(uuid);
	}

	public CommitDiscardWrapperComponent<SurveyTokenDataForm> getSurveyTokenEditComponent(
		String surveyTokenUuid,
		UserRight editUserRight,
		boolean isEditAllowed) {

		SurveyTokenDto surveyTokenDto = findSurveyToken(surveyTokenUuid);

		SurveyTokenDataForm surveyTokenDataForm =
			new SurveyTokenDataForm(UiUtil.permitted(isEditAllowed, editUserRight), surveyTokenDto.toReference());
		surveyTokenDataForm.setValue(surveyTokenDto);

		CommitDiscardWrapperComponent<SurveyTokenDataForm> editComponent =
			new CommitDiscardWrapperComponent<>(surveyTokenDataForm, isEditAllowed, surveyTokenDataForm.getFieldGroup());

		editComponent.addCommitListener(() -> {
			saveSurveyToken(surveyTokenDto);
			navigateToSurveyToken(surveyTokenUuid);
		});

		editComponent.addDeleteListener(() -> {
			FacadeProvider.getSurveyTokenFacade().deletePermanent(surveyTokenUuid);
			ControllerProvider.getSurveyController().navigateToSurveyTokens(surveyTokenDto.getSurvey().getUuid());
		}, I18nProperties.getCaption(SurveyTokenDto.I18N_PREFIX), () -> {
			if (surveyTokenDto.getGeneratedDocument() != null) {
				return I18nProperties.getString(Strings.messageSurveyTokenDelete);
			}
			return null;
		});

		return editComponent;
	}

	private void saveSurveyToken(SurveyTokenDto surveyToken) {
		FacadeProvider.getSurveyTokenFacade().save(surveyToken);
		Notification.show(I18nProperties.getString(Strings.messageSurveyTokenSaved), Notification.Type.WARNING_MESSAGE);
		SormasUI.refreshView();
	}
}
