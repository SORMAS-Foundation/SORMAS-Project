package de.symeda.sormas.ui.survey;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.ValoTheme;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.survey.SurveyTokenCriteria;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.ui.UiUtil;
import de.symeda.sormas.ui.configuration.infrastructure.ImportSurveyTokensLayout;
import de.symeda.sormas.ui.utils.ButtonHelper;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.VaadinUiUtil;
import de.symeda.sormas.ui.utils.ViewConfiguration;

public class SurveyTokenGridComponent extends VerticalLayout {

	private SurveyTokenFilterForm filterForm;
	private SurveyTokenGrid grid;

	public SurveyTokenGridComponent(
		SurveyTokenCriteria criteria,
		ViewConfiguration viewConfiguration,
		Runnable filterChangeHandler,
		Runnable filterResetHandler) {

		setSizeFull();
		setSpacing(false);

		grid = new SurveyTokenGrid(criteria, viewConfiguration);

		filterForm = new SurveyTokenFilterForm();
		filterForm.addApplyHandler(e -> filterChangeHandler.run());
		filterForm.addResetHandler(e -> filterResetHandler.run());

		addComponent(filterForm);
		addComponent(createSurveyTokenImportButton(criteria));
		addComponent(grid);
		setExpandRatio(grid, 1);
	}

	public void reload() {
		grid.reload();
	}

	public void updateFilterComponents(SurveyTokenCriteria criteria) {
		filterForm.setValue(criteria);
	}

	public HorizontalLayout createSurveyTokenImportButton(SurveyTokenCriteria criteria) {

		HorizontalLayout statusFilterLayout = new HorizontalLayout();
		statusFilterLayout.setSpacing(true);
		statusFilterLayout.setWidth("100%");
		statusFilterLayout.addStyleName(CssStyles.VSPACE_3);

		if (UiUtil.permitted(UserRight.SURVEY_TOKEN_IMPORT)) {
			Button importSurveyTokenButton = ButtonHelper.createIconButton(Captions.actionImportSurveyTokens, VaadinIcons.UPLOAD, e -> {
				Window window = VaadinUiUtil
					.showPopupWindow(new ImportSurveyTokensLayout(FacadeProvider.getSurveyFacade().getByUuid(criteria.getSurvey().getUuid())));
				window.setCaption(I18nProperties.getString(Strings.headingImportSurveyTokens));
				window.addCloseListener((event) -> reload());
			}, ValoTheme.BUTTON_PRIMARY);

			statusFilterLayout.addComponent(importSurveyTokenButton);
			statusFilterLayout.setComponentAlignment(importSurveyTokenButton, Alignment.TOP_RIGHT);
		}
		statusFilterLayout.addStyleName("top-bar");
		return statusFilterLayout;
	}

}
