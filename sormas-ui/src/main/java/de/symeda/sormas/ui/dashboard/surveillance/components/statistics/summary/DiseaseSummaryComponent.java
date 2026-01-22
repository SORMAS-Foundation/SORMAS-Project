package de.symeda.sormas.ui.dashboard.surveillance.components.statistics.summary;

import de.symeda.sormas.api.CountryHelper;
import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.feature.FeatureType;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.ui.UiUtil;
import de.symeda.sormas.ui.dashboard.DashboardDataProvider;
import de.symeda.sormas.ui.dashboard.statistics.DashboardStatisticsSubComponent;
import de.symeda.sormas.ui.utils.CssStyles;

public class DiseaseSummaryComponent extends DashboardStatisticsSubComponent {

	// "Case Fatality" elements
	private final FatalitiesSummaryElementComponent fatalitiesSummaryElementComponent;

	// "Outbreak Districts" elements
	private DiseaseSummaryElementComponent lastReportedDistrict;
	private DiseaseSummaryElementComponent outbreakDistrictCount;

	//"cases in quarantine" elements 
	private final DiseaseSummaryElementComponent casesInQuarantineByDate;
	private final DiseaseSummaryElementComponent casesPlacedInQuarantineByDate;

	// "Contacts converted to cases"
	private final DiseaseSummaryElementComponent contactsConvertedToCase;

	// Cases for which Reference definition is Fulfilled
	private DiseaseSummaryElementComponent casesWithReferenceDefinitionFulfilled;

	public DiseaseSummaryComponent() {
		fatalitiesSummaryElementComponent = new FatalitiesSummaryElementComponent();
		addComponent(fatalitiesSummaryElementComponent);

		boolean jurisdictionFieldsVisible = UiUtil.disabled(FeatureType.HIDE_JURISDICTION_FIELDS);
		if (jurisdictionFieldsVisible) {
			lastReportedDistrict =
				new DiseaseSummaryElementComponent(Strings.headingLastReportedDistrict, I18nProperties.getString(Strings.none).toUpperCase());
			addComponent(lastReportedDistrict);
		}

		if (UiUtil.enabled(FeatureType.OUTBREAKS) && jurisdictionFieldsVisible) {
			outbreakDistrictCount = new DiseaseSummaryElementComponent(Strings.headingOutbreakDistricts);
			addComponent(outbreakDistrictCount);
		}

		casesInQuarantineByDate = new DiseaseSummaryElementComponent(Strings.headingCasesInQuarantine);
		addComponent(casesInQuarantineByDate);

		casesPlacedInQuarantineByDate = new DiseaseSummaryElementComponent(Strings.headingCasesPlacedInQuarantine);
		addComponent(casesPlacedInQuarantineByDate);

		contactsConvertedToCase = new DiseaseSummaryElementComponent(Strings.headingCasesResultingFromContacts);
		addComponent(contactsConvertedToCase);

		if (FacadeProvider.getConfigFacade().isConfiguredCountry(CountryHelper.COUNTRY_CODE_GERMANY)) {
			casesWithReferenceDefinitionFulfilled = new DiseaseSummaryElementComponent(Strings.headingcasesWithReferenceDefinitionFulfilled);
			addComponent(casesWithReferenceDefinitionFulfilled);
		}

		addStyleName(CssStyles.VSPACE_TOP_4);
	}

	public void update(DashboardDataProvider dashboardDataProvider) {
		fatalitiesSummaryElementComponent.update(dashboardDataProvider.getCases(), dashboardDataProvider.getPreviousCases());

		if (lastReportedDistrict != null) {
			String district = dashboardDataProvider.getLastReportedDistrict();
			lastReportedDistrict
				.updateTotalLabel(DataHelper.isNullOrEmpty(district) ? I18nProperties.getString(Strings.none).toUpperCase() : district);
		}

		if (outbreakDistrictCount != null) {
			outbreakDistrictCount.updateTotalLabel(dashboardDataProvider.getOutbreakDistrictCount().toString());
		}

		casesInQuarantineByDate.updateTotalLabel(dashboardDataProvider.getCasesInQuarantineCount().toString());
		casesPlacedInQuarantineByDate.updateTotalLabel(dashboardDataProvider.getCasesPlacedInQuarantineCount().toString());
		contactsConvertedToCase.updateTotalLabel(dashboardDataProvider.getContactsConvertedToCaseCount().toString());
		if (FacadeProvider.getConfigFacade().isConfiguredCountry(CountryHelper.COUNTRY_CODE_GERMANY)) {
			casesWithReferenceDefinitionFulfilled.updateTotalLabel(dashboardDataProvider.getCaseWithReferenceDefinitionFulfilledCount().toString());
		}
	}
}
