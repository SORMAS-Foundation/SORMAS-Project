package de.symeda.sormas.ui.dashboard;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.vaadin.hene.popupbutton.PopupButton;

import com.vaadin.ui.OptionGroup;
import com.vaadin.ui.VerticalLayout;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.contact.ContactClassification;
import de.symeda.sormas.api.contact.ContactCriteria;
import de.symeda.sormas.api.contact.ContactStatus;
import de.symeda.sormas.api.contact.FollowUpStatus;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.ui.login.LoginHelper;
import de.symeda.sormas.ui.utils.CssStyles;

public class EpiCurveContactsComponent extends AbstractEpiCurveComponent {

	private static final long serialVersionUID = 6582975657305031105L;

	private EpiCurveContactsMode epiCurveContactsMode;
	
	public EpiCurveContactsComponent(DashboardDataProvider dashboardDataProvider) {
		super(dashboardDataProvider);
	}

	@Override
	protected PopupButton createEpiCurveModeSelector() {
		if (epiCurveContactsMode == null) {
			epiCurveContactsMode = EpiCurveContactsMode.CONTACT_STATUS;
			epiCurveLabel.setValue(epiCurveContactsMode.toString() + " Curve");
		}
		
		PopupButton dataDropdown = new PopupButton("Data");
		CssStyles.style(dataDropdown, CssStyles.BUTTON_SUBTLE);

		VerticalLayout groupingLayout = new VerticalLayout();
		groupingLayout.setMargin(true);
		groupingLayout.setSizeUndefined();
		dataDropdown.setContent(groupingLayout);
		
		OptionGroup dataSelect = new OptionGroup();
		dataSelect.setWidth(100, Unit.PERCENTAGE);
		dataSelect.addItems((Object[]) EpiCurveContactsMode.values());
		dataSelect.setValue(epiCurveContactsMode);
		dataSelect.select(epiCurveContactsMode);
		dataSelect.addValueChangeListener(e -> {
			epiCurveContactsMode = (EpiCurveContactsMode) e.getProperty().getValue();
			epiCurveLabel.setValue(epiCurveContactsMode.toString() + " Curve");
			clearAndFillEpiCurveChart();
		});
		groupingLayout.addComponent(dataSelect);
		
		return dataDropdown;
	}
	
	@Override
	public void clearAndFillEpiCurveChart() {
		StringBuilder hcjs = new StringBuilder();
		hcjs.append(
				"var options = {"
						+ "chart:{ "
						+ " type: 'column', "
						+ " backgroundColor: 'transparent' "
						+ "},"
						+ "credits:{ enabled: false },"
						+ "exporting:{ "
						+ " enabled: true,"
						+ " buttons:{ contextButton:{ theme:{ fill: 'transparent' } } }"
						+ "},"
						+ "title:{ text: '' },"
				);

		// Creates and sets the labels for each day on the x-axis
		List<Date> filteredDates = buildListOfFilteredDates();
		List<String> newLabels = new ArrayList<>();
		Calendar calendar = Calendar.getInstance();
		for (Date date : filteredDates) {
			if (epiCurveGrouping == EpiCurveGrouping.DAY) {
				String label = DateHelper.formatLocalShortDate(date);
				newLabels.add(label);
			} else if (epiCurveGrouping == EpiCurveGrouping.WEEK) {
				calendar.setTime(date);
				String label = DateHelper.getEpiWeek(date).toShortString();
				newLabels.add(label);
			} else {
				String label = DateHelper.formatDateWithMonthAbbreviation(date);
				newLabels.add(label);
			}
		}

		hcjs.append("xAxis: { categories: [");
		for (String s : newLabels) {
			if (newLabels.indexOf(s) == newLabels.size() - 1) {
				hcjs.append("'" + s + "']},");
			} else {
				hcjs.append("'" + s + "', ");
			}
		}

		hcjs.append("yAxis: { min: 0, title: { text: 'Number of Contacts' }, allowDecimals: false, softMax: 10, "
				+ "stackLabels: { enabled: true, "
				+ "style: {fontWeight: 'normal', textOutline: '0', gridLineColor: '#000000', color: (Highcharts.theme && Highcharts.theme.textColor) || 'gray' } } },"
				+ "legend: { verticalAlign: 'top', backgroundColor: 'transparent', align: 'left', "
				+ "borderWidth: 0, shadow: false, margin: 30, padding: 0 },"
				+ "tooltip: { headerFormat: '<b>{point.x}</b><br/>', pointFormat: '{series.name}: {point.y}<br/>Total: {point.stackTotal}'},"
				+ "plotOptions: { column: { borderWidth: 0, stacking: 'normal', groupPadding: 0, pointPadding: 0, dataLabels: {"
				+ "enabled: true, formatter: function() { if (this.y > 0) return this.y; },"
				+ "color: (Highcharts.theme && Highcharts.theme.dataLabelsColor) || 'white' } } },");

		if (epiCurveContactsMode == EpiCurveContactsMode.CONTACT_STATUS) {
			int[] activeNumbers = new int[newLabels.size()];
			int[] convertedNumbers = new int[newLabels.size()];
			int[] droppedNumbers = new int[newLabels.size()];

			for (int i = 0; i < filteredDates.size(); i++) {
				Date date = filteredDates.get(i);

				ContactCriteria contactCriteria = new ContactCriteria()
						.caseDiseaseEquals(dashboardDataProvider.getDisease())
						.caseRegion(dashboardDataProvider.getRegion())
						.caseDistrict(dashboardDataProvider.getDistrict());
				if (epiCurveGrouping == EpiCurveGrouping.DAY) {
					contactCriteria.reportDateBetween(DateHelper.getStartOfDay(date), DateHelper.getEndOfDay(date));
				} else if (epiCurveGrouping == EpiCurveGrouping.WEEK) {
					contactCriteria.reportDateBetween(DateHelper.getStartOfWeek(date), DateHelper.getEndOfWeek(date));
				} else {
					contactCriteria.reportDateBetween(DateHelper.getStartOfMonth(date), DateHelper.getEndOfMonth(date));
				}

				Map<ContactStatus, Long> contactCounts = FacadeProvider.getContactFacade()
						.getNewContactCountPerStatus(contactCriteria, LoginHelper.getCurrentUser().getUuid());

				Long activeCount = contactCounts.get(ContactStatus.ACTIVE);
				Long convertedCount = contactCounts.get(ContactStatus.CONVERTED);
				Long droppedCount = contactCounts.get(ContactStatus.DROPPED);
				activeNumbers[i] = activeCount != null ? activeCount.intValue() : 0;
				convertedNumbers[i] = convertedCount != null ? convertedCount.intValue() : 0;
				droppedNumbers[i] = droppedCount != null ? droppedCount.intValue() : 0;
			}

			hcjs.append("series: [");
			hcjs.append("{ name: 'Active', color: '#B22222', dataLabels: { allowOverlap: false }, data: [");
			for (int i = 0; i < activeNumbers.length; i++) {
				if (i == activeNumbers.length - 1) {
					hcjs.append(activeNumbers[i] + "]},");
				} else {
					hcjs.append(activeNumbers[i] + ", ");
				}
			}
			hcjs.append("{ name: 'Converted To Case', color: '#FFD700', dataLabels: { allowOverlap: false },  data: [");
			for (int i = 0; i < convertedNumbers.length; i++) {
				if (i == convertedNumbers.length - 1) {
					hcjs.append(convertedNumbers[i] + "]},");
				} else {
					hcjs.append(convertedNumbers[i] + ", ");
				}
			}
			hcjs.append("{ name: 'Dropped', color: '#808080', dataLabels: { allowOverlap: false },  data: [");
			for (int i = 0; i < droppedNumbers.length; i++) {
				if (i == droppedNumbers.length - 1) {
					hcjs.append(droppedNumbers[i] + "]}]};");
				} else {
					hcjs.append(droppedNumbers[i] + ", ");
				}
			}
		} else if (epiCurveContactsMode == EpiCurveContactsMode.CONTACT_CLASSIFICATION) {
			int[] unconfirmedNumbers = new int[newLabels.size()];
			int[] confirmedNumbers = new int[newLabels.size()];

			for (int i = 0; i < filteredDates.size(); i++) {
				Date date = filteredDates.get(i);

				ContactCriteria contactCriteria = new ContactCriteria()
						.caseDiseaseEquals(dashboardDataProvider.getDisease())
						.caseRegion(dashboardDataProvider.getRegion())
						.caseDistrict(dashboardDataProvider.getDistrict());
				if (epiCurveGrouping == EpiCurveGrouping.DAY) {
					contactCriteria.reportDateBetween(DateHelper.getStartOfDay(date), DateHelper.getEndOfDay(date));
				} else if (epiCurveGrouping == EpiCurveGrouping.WEEK) {
					contactCriteria.reportDateBetween(DateHelper.getStartOfWeek(date), DateHelper.getEndOfWeek(date));
				} else {
					contactCriteria.reportDateBetween(DateHelper.getStartOfMonth(date), DateHelper.getEndOfMonth(date));
				}

				Map<ContactClassification, Long> contactCounts = FacadeProvider.getContactFacade()
						.getNewContactCountPerClassification(contactCriteria, LoginHelper.getCurrentUser().getUuid());

				Long unconfirmedCount = contactCounts.get(ContactClassification.UNCONFIRMED);
				Long confirmedCount = contactCounts.get(ContactClassification.CONFIRMED);
				unconfirmedNumbers[i] = unconfirmedCount != null ? unconfirmedCount.intValue() : 0;
				confirmedNumbers[i] = confirmedCount != null ? confirmedCount.intValue() : 0;
			}

			hcjs.append("series: [");
			hcjs.append("{ name: 'Unconfirmed', color: '#FFD700', dataLabels: { allowOverlap: false }, data: [");
			for (int i = 0; i < unconfirmedNumbers.length; i++) {
				if (i == unconfirmedNumbers.length - 1) {
					hcjs.append(unconfirmedNumbers[i] + "]},");
				} else {
					hcjs.append(unconfirmedNumbers[i] + ", ");
				}
			}
			hcjs.append("{ name: 'Confirmed', color: '#B22222', dataLabels: { allowOverlap: false },  data: [");
			for (int i = 0; i < confirmedNumbers.length; i++) {
				if (i == confirmedNumbers.length - 1) {
					hcjs.append(confirmedNumbers[i] + "]}]};");
				} else {
					hcjs.append(confirmedNumbers[i] + ", ");
				}
			}
		} else if (epiCurveContactsMode == EpiCurveContactsMode.FOLLOW_UP_STATUS) {
			int[] underFollowUpNumbers = new int[newLabels.size()];
			int[] lostToFollowUpNumbers = new int[newLabels.size()];
			int[] completedFollowUpNumbers = new int[newLabels.size()];
			int[] canceledFollowUpNumbers = new int[newLabels.size()];

			for (int i = 0; i < filteredDates.size(); i++) {
				Date date = filteredDates.get(i);

				ContactCriteria contactCriteria = new ContactCriteria()
						.caseDiseaseEquals(dashboardDataProvider.getDisease())
						.caseRegion(dashboardDataProvider.getRegion())
						.caseDistrict(dashboardDataProvider.getDistrict());
				if (epiCurveGrouping == EpiCurveGrouping.DAY) {
					contactCriteria.reportDateBetween(DateHelper.getStartOfDay(date), DateHelper.getEndOfDay(date));
				} else if (epiCurveGrouping == EpiCurveGrouping.WEEK) {
					contactCriteria.reportDateBetween(DateHelper.getStartOfWeek(date), DateHelper.getEndOfWeek(date));
				} else {
					contactCriteria.reportDateBetween(DateHelper.getStartOfMonth(date), DateHelper.getEndOfMonth(date));
				}

				Map<FollowUpStatus, Long> contactCounts = FacadeProvider.getContactFacade()
						.getNewContactCountPerFollowUpStatus(contactCriteria, LoginHelper.getCurrentUser().getUuid());

				Long underFollowUpCount = contactCounts.get(FollowUpStatus.FOLLOW_UP);
				Long lostToFollowUpCount = contactCounts.get(FollowUpStatus.LOST);
				Long completedFollowUpCount = contactCounts.get(FollowUpStatus.COMPLETED);
				Long canceledFollowUpCount = contactCounts.get(FollowUpStatus.CANCELED);
				underFollowUpNumbers[i] = underFollowUpCount != null ? underFollowUpCount.intValue() : 0;
				lostToFollowUpNumbers[i] = lostToFollowUpCount != null ? lostToFollowUpCount.intValue() : 0;
				completedFollowUpNumbers[i] = completedFollowUpCount != null ? completedFollowUpCount.intValue() : 0;
				canceledFollowUpNumbers[i] = canceledFollowUpCount != null ? canceledFollowUpCount.intValue() : 0;
			}

			hcjs.append("series: [");
			hcjs.append("{ name: 'Under F/U', color: '#005A9C', dataLabels: { allowOverlap: false }, data: [");
			for (int i = 0; i < underFollowUpNumbers.length; i++) {
				if (i == underFollowUpNumbers.length - 1) {
					hcjs.append(underFollowUpNumbers[i] + "]},");
				} else {
					hcjs.append(underFollowUpNumbers[i] + ", ");
				}
			}
			hcjs.append("{ name: 'Lost To F/U', color: '#FF0000', dataLabels: { allowOverlap: false },  data: [");
			for (int i = 0; i < lostToFollowUpNumbers.length; i++) {
				if (i == lostToFollowUpNumbers.length - 1) {
					hcjs.append(lostToFollowUpNumbers[i] + "]},");
				} else {
					hcjs.append(lostToFollowUpNumbers[i] + ", ");
				}
			}
			hcjs.append("{ name: 'Completed F/U', color: '#32CD32', dataLabels: { allowOverlap: false },  data: [");
			for (int i = 0; i < completedFollowUpNumbers.length; i++) {
				if (i == completedFollowUpNumbers.length - 1) {
					hcjs.append(completedFollowUpNumbers[i] + "]},");
				} else {
					hcjs.append(completedFollowUpNumbers[i] + ", ");
				}
			}
			hcjs.append("{ name: 'Canceled F/U', color: '#FF8C00', dataLabels: { allowOverlap: false },  data: [");
			for (int i = 0; i < canceledFollowUpNumbers.length; i++) {
				if (i == canceledFollowUpNumbers.length - 1) {
					hcjs.append(canceledFollowUpNumbers[i] + "]}]};");
				} else {
					hcjs.append(canceledFollowUpNumbers[i] + ", ");
				}
			}
		}

		epiCurveChart.setHcjs(hcjs.toString());	
	}
	
	
}
