package de.symeda.sormas.ui.dashboard.diseasedetails;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.caze.CaseClassification;
import de.symeda.sormas.api.caze.NewCaseDateType;
import de.symeda.sormas.api.dashboard.NewDateFilterType;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.infrastructure.region.RegionDto;
import de.symeda.sormas.ui.SormasUI;
import de.symeda.sormas.ui.dashboard.AbstractDashboardView;
import de.symeda.sormas.ui.dashboard.DashboardDataProvider;
import de.symeda.sormas.ui.dashboard.DashboardType;
import de.symeda.sormas.ui.dashboard.components.DashboardFilterLayout;
import de.symeda.sormas.ui.dashboard.contacts.components.ContactsFilterLayout;
import de.symeda.sormas.ui.utils.ViewConfiguration;

import static com.vaadin.navigator.ViewChangeListener.*;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.EnumSet;

@SuppressWarnings("serial")
public class DiseaseDetailsView extends AbstractDashboardView {

	private static final long serialVersionUID = -1L;
	public static final String VIEW_NAME = ROOT_VIEW_NAME + "/disease";

	protected DiseaseDetailsViewLayout diseaseDetailsViewLayout;

	public static  String data;
	public static void setData(String newData) {
		data=newData;
	}


	public DiseaseDetailsView() {
		super(VIEW_NAME, DashboardType.DISEASE);

		dashboardLayout.setSpacing(false);

		String paramData = (String) SormasUI.get().getSession().getAttribute("paramdata");


		if (data != null) {

			String dateFrom = data.split("/")[0];

			String dateTo = data.split("/")[1];

			String type = data.split("/")[2];

			String caseClassification = data.split("/")[3];

			String newCaseDateType = data.split("/")[4];

			String regionId = data.split("/")[5];

			if (dateFrom != null) {


				DateFormat df1 = new SimpleDateFormat("yyyy-MM-dd");
				String string1 = dateFrom;
				try {
					Date result1 = df1.parse(string1);
					dashboardDataProvider.setFromDate(result1);
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				// dashboardDataProvider.setFromDate(new Date(params[1]));
			}

			if (dateTo != null) {

				DateFormat df1 = new SimpleDateFormat("yyyy-MM-dd");
				String string1 = dateTo;
				try {
					Date result1 = df1.parse(string1);
					dashboardDataProvider.setToDate(result1);
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				// dashboardDataProvider.setFromDate(new Date(params[2]));
			}

			if (type != null) {


				// NewDateFilterType.values();
				dashboardDataProvider.setDateFilterType(EnumSet.allOf(NewDateFilterType.class).stream()
						.filter(e -> e.name().equals(type)).findFirst()
						.orElseThrow(() -> new IllegalStateException(String.format("Unsupported type %s.", type))));

				// dashboardDataProvider.setFromDate(new Date(params[2]));
			}

			// dashboardDataProvider.setRegion(region);

			if (caseClassification != null) {

				String caseClass = caseClassification.replace("case", "").toUpperCase().trim();

				if (!caseClass.equals("NULL")) {


					if(caseClass.equals("NOT YET CLASSIFIED")) {

						dashboardDataProvider.setCaseClassification(CaseClassification.NOT_CLASSIFIED);

					}else if (caseClass.equals("CONFIRMED  WITH UNKNOWN SYMPTOMS")){

						dashboardDataProvider.setCaseClassification(CaseClassification.CONFIRMED_UNKNOWN_SYMPTOMS);

					}
					else if (caseClass.equals("NOT A")){

						dashboardDataProvider.setCaseClassification(CaseClassification.NO_CASE);

					}else if(caseClass.equals("CONFIRMED  WITH NO SYMPTOMS")) {

						dashboardDataProvider.setCaseClassification(CaseClassification.CONFIRMED_NO_SYMPTOMS);

					}
					else {

						dashboardDataProvider.setCaseClassification(EnumSet.allOf(CaseClassification.class).stream()
								.filter(e -> e.name().equals(caseClass)).findFirst().orElseThrow(
										() -> new IllegalStateException(String.format("Unsupported type %s.", caseClass))));
					}
					// dashboardDataProvider.setFromDate(new Date(params[2]));
					//dashboardDataProvider.setCaseClassification(CaseClassification.CONFIRMED);

				}
			} else {
				dashboardDataProvider.setCaseClassification(CaseClassification.NOT_CLASSIFIED);
			}



			if (newCaseDateType != null) {

				NewCaseDateType enumNewCaseDateType;
				switch (newCaseDateType) {
					case "Creation date":
						enumNewCaseDateType = NewCaseDateType.CREATION;
						break;
					case "Investigation date":
						enumNewCaseDateType = NewCaseDateType.INVESTIGATION;
						break;
					case "Most relevant date":
						enumNewCaseDateType = NewCaseDateType.MOST_RELEVANT;
						break;
					case "Symptom onset date":
						enumNewCaseDateType = NewCaseDateType.ONSET;
						break;
					case "Case report date":
						enumNewCaseDateType = NewCaseDateType.REPORT;
						break;
					case "Classification date":
						enumNewCaseDateType = NewCaseDateType.CLASSIFICATION;
						break;
					default:
						enumNewCaseDateType = NewCaseDateType.MOST_RELEVANT;
				}

				dashboardDataProvider.setNewCaseDateType(enumNewCaseDateType);
			}

			if(!regionId.equals("null")) {

				RegionDto region = FacadeProvider.getRegionFacade().getByUuid(regionId);
				dashboardDataProvider.setRegion(region.toReference());

			}

		}


		if (dashboardDataProvider.getDashboardType() == null) {
			dashboardDataProvider.setDashboardType(DashboardType.DISEASE);
		}

		if (DashboardType.DISEASE.equals(dashboardDataProvider.getDashboardType())) {
			dashboardDataProvider.setDisease(FacadeProvider.getDiseaseConfigurationFacade().getDefaultDisease());
		}

		// dashboardDataProvider.setNewCaseDateType(NewCaseDateType.MOST_RELEVANT);

		// filterLayout = new DashboardFilterLayout(this, dashboardDataProvider);
		// filterLayout.setInfoLabelText(I18nProperties.getString(Strings.classificationForDisease));
//


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
