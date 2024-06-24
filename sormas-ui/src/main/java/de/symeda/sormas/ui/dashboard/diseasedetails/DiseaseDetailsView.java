package de.symeda.sormas.ui.dashboard.diseasedetails;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.caze.CaseClassification;
import de.symeda.sormas.api.caze.NewCaseDateType;
import de.symeda.sormas.api.dashboard.NewDateFilterType;
import de.symeda.sormas.api.infrastructure.region.RegionDto;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.ui.dashboard.AbstractDashboardView;
import de.symeda.sormas.ui.dashboard.DashboardType;

import static com.vaadin.navigator.ViewChangeListener.*;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.EnumSet;
import java.util.logging.Level;
import java.util.logging.Logger;

@SuppressWarnings("serial")
public class DiseaseDetailsView extends AbstractDashboardView {

	private static final long serialVersionUID = -1L;
	public static final String VIEW_NAME = ROOT_VIEW_NAME + "/disease";

	private static final Logger LOGGER = Logger.getLogger(DiseaseDetailsView.class.getName());

	protected DiseaseDetailsViewLayout diseaseDetailsViewLayout;

	public static  String diseaseDetailsData;
	public static void setDiseaseDetailsData(String newData) {
		diseaseDetailsData =newData;
	}


	public DiseaseDetailsView() {
		super(VIEW_NAME, DashboardType.DISEASE);

		dashboardLayout.setSpacing(false);

		if (diseaseDetailsData != null) {
			String[] dataParts = diseaseDetailsData.split("/");
			if (dataParts.length == 6) {
				String dateFrom = dataParts[0];
				String dateTo = dataParts[1];
				String newDateFilterType = dataParts[2];
				String caseClassification = dataParts[3];
				String newCaseDateType = dataParts[4];
				String regionId = dataParts[5];

				setDateFilters(dateFrom, dateTo);
				setDateFilterType(newDateFilterType);
				setCaseClassification(caseClassification);
				setNewCaseDateType(newCaseDateType);
				setRegion(regionId);
			}
		}

		if (dashboardDataProvider.getDashboardType() == null) {
			dashboardDataProvider.setDashboardType(DashboardType.DISEASE);
		}

		if (DashboardType.DISEASE.equals(dashboardDataProvider.getDashboardType())) {
			dashboardDataProvider.setDisease(FacadeProvider.getDiseaseConfigurationFacade().getDefaultDisease());
		}

		filterLayout = new DiseaseFilterLayout(this, dashboardDataProvider);
		dashboardLayout.addComponent(filterLayout);

		dashboardSwitcher.setValue(DashboardType.DISEASE);
		dashboardSwitcher.addValueChangeListener(e -> {
			dashboardDataProvider.setDashboardType((DashboardType) e.getProperty().getValue());
			navigateToDashboardView(e);
		});

		// Added Component
		diseaseDetailsViewLayout = new DiseaseDetailsViewLayout(dashboardDataProvider);
		dashboardLayout.addComponent(diseaseDetailsViewLayout);
		dashboardLayout.setExpandRatio(diseaseDetailsViewLayout, 1);
	}

	private void setDateFilters(String dateFrom, String dateTo) {
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		try {
			if (dateFrom != null) {
				Date startDate = dateFormat.parse(dateFrom);
				dashboardDataProvider.setFromDate(DateHelper.getStartOfDay(startDate));
			}
			if (dateTo != null) {
				Date endDate = dateFormat.parse(dateTo);
				dashboardDataProvider.setToDate(DateHelper.getEndOfDay(endDate));
			}
		} catch (ParseException e) {
			LOGGER.log(Level.SEVERE, "Date parsing error", e);
		}
	}

	private void setDateFilterType(String newDateFilterType) {
		try {
			NewDateFilterType filterType = NewDateFilterType.valueOf(newDateFilterType);
			dashboardDataProvider.setDateFilterType(filterType);
		} catch (IllegalArgumentException e) {
			LOGGER.log(Level.WARNING, "Unsupported date filter type: " + newDateFilterType, e);
		}
	}

	private void setCaseClassification(String caseClassification) {
		try {
			CaseClassification classification = CaseClassification.valueOf(caseClassification.replace(" ", "_").toUpperCase().trim());
			dashboardDataProvider.setCaseClassification(classification);
		} catch (IllegalArgumentException e) {
			LOGGER.log(Level.WARNING, "Unsupported case classification: " + caseClassification, e);
			dashboardDataProvider.setCaseClassification(CaseClassification.NOT_CLASSIFIED);
		}
	}

	private void setNewCaseDateType(String newCaseDateType) {
		NewCaseDateType caseDateType;
		switch (newCaseDateType) {
			case "Creation date":
				caseDateType = NewCaseDateType.CREATION;
				break;
			case "Investigation date":
				caseDateType = NewCaseDateType.INVESTIGATION;
				break;
			case "Symptom onset date":
				caseDateType = NewCaseDateType.ONSET;
				break;
			case "Case report date":
				caseDateType = NewCaseDateType.REPORT;
				break;
			case "Classification date":
				caseDateType = NewCaseDateType.CLASSIFICATION;
				break;
			default:
				caseDateType = NewCaseDateType.MOST_RELEVANT;
		}
		dashboardDataProvider.setNewCaseDateType(caseDateType);
	}

	private void setRegion(String regionId) {
		if (!"null".equals(regionId)) {
			RegionDto region = FacadeProvider.getRegionFacade().getByUuid(regionId);
			dashboardDataProvider.setRegion(region.toReference());
		}
	}

	@Override
	public void refreshDiseaseData() {

		super.refreshDiseaseData();

		if (diseaseDetailsViewLayout != null)
			diseaseDetailsViewLayout.refresh();
	}

	@Override
	public void enter(ViewChangeEvent event) {
		super.enter(event);
		dashboardDataProvider.setDisease(Disease.valueOf(event.getParameters()));
		refreshDiseaseData();
	}

}
