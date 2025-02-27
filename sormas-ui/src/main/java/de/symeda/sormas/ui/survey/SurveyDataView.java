package de.symeda.sormas.ui.survey;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.ValoTheme;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.survey.SurveyDto;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.UiUtil;
import de.symeda.sormas.ui.configuration.infrastructure.ImportSurveyTokenResponsesLayout;
import de.symeda.sormas.ui.configuration.infrastructure.ImportSurveyTokensLayout;
import de.symeda.sormas.ui.samples.HasName;
import de.symeda.sormas.ui.utils.ButtonHelper;
import de.symeda.sormas.ui.utils.CommitDiscardWrapperComponent;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.DetailSubComponentWrapper;
import de.symeda.sormas.ui.utils.LayoutWithSidePanel;
import de.symeda.sormas.ui.utils.VaadinUiUtil;

public class SurveyDataView extends AbstractSurveyView implements HasName {

	public static final String VIEW_NAME = ROOT_VIEW_NAME + "/data";

	private CommitDiscardWrapperComponent<SurveyDataForm> editComponent;
	private SurveyDto survey;

	public SurveyDataView() {
		super(VIEW_NAME);
	}

	@Override
	public String getName() {
		return "";
	}

	@Override
	protected String getRootViewName() {
		return SurveysView.VIEW_NAME;
	}

	@Override
	protected void initView(String params) {
		setHeightUndefined();

		survey = FacadeProvider.getSurveyFacade().getByUuid(getReference().getUuid());
		editComponent =
			ControllerProvider.getSurveyController().getSurveyEditComponent(getReference().getUuid(), UserRight.SURVEY_EDIT, isEditAllowed());

		DetailSubComponentWrapper container = new DetailSubComponentWrapper(() -> editComponent);
		container.setWidth(100, Unit.PERCENTAGE);
		container.setMargin(true);
		setSubComponent(container);
		container.setEnabled(true);

		LayoutWithSidePanel layout = new LayoutWithSidePanel(editComponent);
		container.addComponent(createSurveyTokenImportButton());
		container.addComponent(createSurveyTokenResponsesButton());
		container.addComponent(layout);
	}
	private HorizontalLayout createSurveyTokenResponsesButton() {
		HorizontalLayout statusFilterLayout = new HorizontalLayout();
		statusFilterLayout.setSpacing(true);
		statusFilterLayout.setWidth("100%");
		statusFilterLayout.addStyleName(CssStyles.VSPACE_3);

		if (UiUtil.permitted(UserRight.SURVEY_TOKEN_IMPORT)) {
			Button importSurveyTokenResponsesButton = ButtonHelper.createIconButton(Captions.actionImportSurveyTokenResponses, VaadinIcons.UPLOAD, e -> {
				Window window = VaadinUiUtil.showPopupWindow(new ImportSurveyTokenResponsesLayout(survey));
				window.setCaption(I18nProperties.getString(Strings.headingImportSurveyTokenResponses));
			}, ValoTheme.BUTTON_PRIMARY);

			statusFilterLayout.addComponent(importSurveyTokenResponsesButton);
			statusFilterLayout.setComponentAlignment(importSurveyTokenResponsesButton, Alignment.MIDDLE_CENTER);
		}
		statusFilterLayout.addStyleName("top-bar");
		return statusFilterLayout;
	}

	public HorizontalLayout createSurveyTokenImportButton() {

		HorizontalLayout statusFilterLayout = new HorizontalLayout();
		statusFilterLayout.setSpacing(true);
		statusFilterLayout.setWidth("100%");
		statusFilterLayout.addStyleName(CssStyles.VSPACE_3);

		if (UiUtil.permitted(UserRight.SURVEY_TOKEN_IMPORT)) {
			Button importSurveyTokenButton = ButtonHelper.createIconButton(Captions.actionImportSurveyTokens, VaadinIcons.UPLOAD, e -> {
				Window window = VaadinUiUtil.showPopupWindow(new ImportSurveyTokensLayout(survey));
				window.setCaption(I18nProperties.getString(Strings.headingImportSurveyTokens));
			}, ValoTheme.BUTTON_PRIMARY);

			statusFilterLayout.addComponent(importSurveyTokenButton);
			statusFilterLayout.setComponentAlignment(importSurveyTokenButton, Alignment.MIDDLE_CENTER);
		}
		statusFilterLayout.addStyleName("top-bar");
		return statusFilterLayout;
	}

}
