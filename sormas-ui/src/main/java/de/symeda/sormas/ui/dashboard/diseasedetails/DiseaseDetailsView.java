/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2022 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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
package de.symeda.sormas.ui.dashboard.diseasedetails;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.caze.CaseClassification;
import de.symeda.sormas.api.caze.NewCaseDateType;
import de.symeda.sormas.api.dashboard.NewDateFilterType;
import de.symeda.sormas.api.infrastructure.region.RegionDto;
import de.symeda.sormas.ui.dashboard.AbstractDashboardView;
import de.symeda.sormas.ui.dashboard.DashboardDataProvider;
import de.symeda.sormas.ui.dashboard.DashboardType;
import static com.vaadin.navigator.ViewChangeListener.*;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

@SuppressWarnings("serial")
public class DiseaseDetailsView extends AbstractDashboardView {

	private static final long serialVersionUID = -1L;
	public static final String VIEW_NAME = ROOT_VIEW_NAME + "/disease";

	private static final Logger LOGGER = Logger.getLogger(DiseaseDetailsView.class.getName());

	protected DiseaseDetailsViewLayout diseaseDetailsViewLayout;

	private static String diseaseDetailsData;
	public static void setDiseaseDetailsData(String newData) {

		diseaseDetailsData =newData;
	}

	public DiseaseDetailsView() {

		super(VIEW_NAME);

		dashboardDataProvider = new DashboardDataProvider();
		dashboardLayout.setSpacing(false);
		if (dashboardDataProvider.getDashboardType() == null) {
			dashboardDataProvider.setDashboardType(DashboardType.DISEASE);
		}

		dashboardDataProvider.setDisease(getDiseases());

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

		if (DashboardType.DISEASE.equals(dashboardDataProvider.getDashboardType())) {
			dashboardDataProvider.setDisease(FacadeProvider.getDiseaseConfigurationFacade().getDefaultDisease());
		}

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

		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
		try {
			if (dateFrom != null) {
				Date startDate = dateFormat.parse(dateFrom);
				dashboardDataProvider.setFromDate(startDate);
			}
			if (dateTo != null) {
				Date endDate = dateFormat.parse(dateTo);
				dashboardDataProvider.setToDate(endDate);
			}
		} catch (ParseException e) {
			LOGGER.log(Level.SEVERE, "Date parsing error", e);
		}
	}

	private void setDateFilterType(String newDateFilterType) {

		try {
			if(newDateFilterType!=null&&!newDateFilterType.equals("null")) {
				NewDateFilterType filterType = NewDateFilterType.valueOf(newDateFilterType);
				dashboardDataProvider.setDateFilterType(filterType);
			}
		} catch (IllegalArgumentException e) {
			LOGGER.log(Level.WARNING, "Unsupported date filter type: " + newDateFilterType, e);
		}
	}

	private void setCaseClassification(String caseClassification) {

		try {
			if(caseClassification!=null && !caseClassification.equals("null")) {
				CaseClassification classification = CaseClassification.valueOf(caseClassification.replace(" ", "_").toUpperCase().trim());
				dashboardDataProvider.setCaseClassification(classification);
			}
		} catch (IllegalArgumentException e) {
			LOGGER.log(Level.WARNING, "Unsupported case classification: " + caseClassification, e);
			dashboardDataProvider.setCaseClassification(CaseClassification.NOT_CLASSIFIED);
		}
	}

	private void setNewCaseDateType(String newCaseDateType) {

		NewCaseDateType caseDateType;
		switch (newCaseDateType) {
			case "Symptom onset date":
				caseDateType = NewCaseDateType.ONSET;
				break;
			case "Case report date":
				caseDateType = NewCaseDateType.REPORT;
				break;
			default:
				caseDateType = NewCaseDateType.MOST_RELEVANT;
		}
		dashboardDataProvider.setNewCaseDateType(caseDateType);
	}

	private void setRegion(String regionId) {

		if (regionId != null && !"null".equals(regionId) && !regionId.isEmpty()) {
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