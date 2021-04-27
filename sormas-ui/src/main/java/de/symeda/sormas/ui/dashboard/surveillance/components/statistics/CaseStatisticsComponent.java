package de.symeda.sormas.ui.dashboard.surveillance.components.statistics;

import java.util.List;

import com.vaadin.ui.CssLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;

import de.symeda.sormas.api.caze.CaseClassification;
import de.symeda.sormas.api.caze.DashboardCaseDto;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.ui.dashboard.statistics.CountElementStyle;
import de.symeda.sormas.ui.dashboard.statistics.DashboardStatisticsCountElement;
import de.symeda.sormas.ui.dashboard.statistics.DashboardStatisticsSubComponent;
import de.symeda.sormas.ui.utils.CssStyles;

public class CaseStatisticsComponent extends DashboardStatisticsSubComponent {

	private Label caseCountLabel;
	private DashboardStatisticsCountElement caseClassificationConfirmed;
	private DashboardStatisticsCountElement caseClassificationProbable;
	private DashboardStatisticsCountElement caseClassificationSuspect;
	private DashboardStatisticsCountElement caseClassificationNotACase;
	private DashboardStatisticsCountElement caseClassificationNotYetClassified;

	public CaseStatisticsComponent() {
		// Header
		HorizontalLayout headerLayout = new HorizontalLayout();
		headerLayout.setMargin(false);
		headerLayout.setSpacing(false);
		// count
		caseCountLabel = new Label();
		CssStyles.style(
			caseCountLabel,
			CssStyles.LABEL_PRIMARY,
			CssStyles.LABEL_XXXLARGE,
			CssStyles.LABEL_BOLD,
			CssStyles.VSPACE_4,
			CssStyles.VSPACE_TOP_NONE);
		headerLayout.addComponent(caseCountLabel);
		// title
		Label caseComponentTitle = new Label(I18nProperties.getCaption(Captions.dashboardNewCases));
		CssStyles.style(caseComponentTitle, CssStyles.H2, CssStyles.HSPACE_LEFT_4);
		headerLayout.addComponent(caseComponentTitle);

		addComponent(headerLayout);

		// Count layout
		CssLayout countLayout = createCountLayout(true);
		caseClassificationConfirmed =
			new DashboardStatisticsCountElement(I18nProperties.getCaption(Captions.dashboardConfirmed), CountElementStyle.CRITICAL);
		addComponentToCountLayout(countLayout, caseClassificationConfirmed);
		caseClassificationProbable =
			new DashboardStatisticsCountElement(I18nProperties.getCaption(Captions.dashboardProbable), CountElementStyle.IMPORTANT);
		addComponentToCountLayout(countLayout, caseClassificationProbable);
		caseClassificationSuspect =
			new DashboardStatisticsCountElement(I18nProperties.getCaption(Captions.dashboardSuspect), CountElementStyle.RELEVANT);
		addComponentToCountLayout(countLayout, caseClassificationSuspect);
		caseClassificationNotACase =
			new DashboardStatisticsCountElement(I18nProperties.getCaption(Captions.dashboardNotACase), CountElementStyle.POSITIVE);
		addComponentToCountLayout(countLayout, caseClassificationNotACase);
		caseClassificationNotYetClassified =
			new DashboardStatisticsCountElement(I18nProperties.getCaption(Captions.dashboardNotYetClassified), CountElementStyle.MINOR);
		addComponentToCountLayout(countLayout, caseClassificationNotYetClassified);
		addComponent(countLayout);
	}

	public void update(List<DashboardCaseDto> cases) {
		caseCountLabel.setValue(Integer.toString(cases.size()).toString());

		int confirmedCasesCount = (int) cases.stream().filter(c -> c.getCaseClassification() == CaseClassification.CONFIRMED).count();
		caseClassificationConfirmed.updateCountLabel(confirmedCasesCount);
		int probableCasesCount = (int) cases.stream().filter(c -> c.getCaseClassification() == CaseClassification.PROBABLE).count();
		caseClassificationProbable.updateCountLabel(probableCasesCount);
		int suspectCasesCount = (int) cases.stream().filter(c -> c.getCaseClassification() == CaseClassification.SUSPECT).count();
		caseClassificationSuspect.updateCountLabel(suspectCasesCount);
		int notACaseCasesCount = (int) cases.stream().filter(c -> c.getCaseClassification() == CaseClassification.NO_CASE).count();
		caseClassificationNotACase.updateCountLabel(notACaseCasesCount);
		int notYetClassifiedCasesCount = (int) cases.stream().filter(c -> c.getCaseClassification() == CaseClassification.NOT_CLASSIFIED).count();
		caseClassificationNotYetClassified.updateCountLabel(notYetClassifiedCasesCount);
	}
}
