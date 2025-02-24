package de.symeda.sormas.ui.survey;

import com.vaadin.navigator.Navigator;
import com.vaadin.ui.Notification;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.survey.SurveyDto;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.ui.SormasUI;
import de.symeda.sormas.ui.UiUtil;
import de.symeda.sormas.ui.UserProvider;
import de.symeda.sormas.ui.utils.CommitDiscardWrapperComponent;
import de.symeda.sormas.ui.utils.VaadinUiUtil;
import de.symeda.sormas.ui.utils.components.page.title.TitleLayout;

public class SurveyController {

	public void registeredViews(Navigator navigator) {
		navigator.addView(SurveysView.VIEW_NAME, SurveysView.class);
		navigator.addView(SurveyDataView.VIEW_NAME, SurveyDataView.class);
	}

	public void navigateToSurvey(String uuid) {
		navigateToView(SurveyDataView.VIEW_NAME, uuid);
	}

	public void navigateToView(String viewName, String uuid) {
		final String navigationState = viewName + "/" + uuid;
		SormasUI.get().getNavigator().navigateTo(navigationState);
	}

	public void navigateToIndex() {
		String navigationState = SurveysView.VIEW_NAME;
		SormasUI.get().getNavigator().navigateTo(navigationState);
	}

	private static void saveSurvey(SurveyDto survey) {
		FacadeProvider.getSurveyFacade().save(survey);
		Notification.show(I18nProperties.getString(Strings.messageSurveySaved), Notification.Type.WARNING_MESSAGE);
		SormasUI.refreshView();
	}

	public void create() {
		CommitDiscardWrapperComponent<SurveyCreateForm> surveyCreateComponent = getSurveyCreateComponent();
		if (surveyCreateComponent != null) {
			VaadinUiUtil.showModalPopupWindow(surveyCreateComponent, I18nProperties.getString(Strings.headingCreateNewSurvey));
		}
	}

	public CommitDiscardWrapperComponent<SurveyCreateForm> getSurveyCreateComponent() {
		UserProvider curentUser = UiUtil.getCurrentUserProvider();

		if (curentUser != null) {
			SurveyCreateForm surveyCreateForm;
			surveyCreateForm = new SurveyCreateForm();
			final SurveyDto surveyDto = SurveyDto.build();
			surveyCreateForm.setValue(surveyDto);

			final CommitDiscardWrapperComponent<SurveyCreateForm> createView =
				new CommitDiscardWrapperComponent<>(surveyCreateForm, UiUtil.permitted(UserRight.SURVEY_CREATE), surveyCreateForm.getFieldGroup());

			createView.addCommitListener(() -> {

				if (!surveyCreateForm.getFieldGroup().isModified()) {
					SurveyDto dto = surveyCreateForm.getValue();
					FacadeProvider.getSurveyFacade().save(dto);
					Notification.show(I18nProperties.getString(Strings.messageSurveySaved), Notification.Type.WARNING_MESSAGE);

					navigateToSurvey(dto.getUuid());
				}

			});
			return createView;
		}
		return null;
	}

	public CommitDiscardWrapperComponent<SurveyDataForm> getSurveyEditComponent(String surveyUuid, UserRight editUserRight, boolean isEditAllwed) {

		SurveyDto surveyDto = FacadeProvider.getSurveyFacade().getByUuid(surveyUuid);

		SurveyDataForm surveyDataForm = new SurveyDataForm(UiUtil.permitted(isEditAllwed, editUserRight), surveyDto.toReference());
		surveyDataForm.setValue(surveyDto);

		CommitDiscardWrapperComponent<SurveyDataForm> editComponent =
			new CommitDiscardWrapperComponent<>(surveyDataForm, true, surveyDataForm.getFieldGroup());

		editComponent.addCommitListener(() -> {
			saveSurvey(surveyDto);
			navigateToSurvey(surveyUuid);
		});

		editComponent.addDeleteListener(() -> {
			FacadeProvider.getSurveyFacade().deletePermanent(surveyUuid);
			navigateToIndex();
		}, I18nProperties.getCaption(SurveyDto.I18N_PREFIX));

		return editComponent;
	}

	public TitleLayout getSurveyViewTitleLayout(String uuid) {
		SurveyDto surveyDto = findSurvey(uuid);

		TitleLayout titleLayout = new TitleLayout();

		String shortUuid = DataHelper.getShortUuid(surveyDto.getUuid());

		StringBuilder mainRowText = new StringBuilder(surveyDto.getName());
		mainRowText.append(mainRowText.length() > 0 ? " (" + shortUuid + ")" : shortUuid);

		titleLayout.addMainRow(mainRowText.toString());

		return titleLayout;
	}

	private SurveyDto findSurvey(String uuid) {
		return FacadeProvider.getSurveyFacade().getByUuid(uuid);
	}
}
