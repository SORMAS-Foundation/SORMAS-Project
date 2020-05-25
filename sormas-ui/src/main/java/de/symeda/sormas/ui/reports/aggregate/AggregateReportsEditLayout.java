package de.symeda.sormas.ui.reports.aggregate;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.ValoTheme;
import com.vaadin.v7.data.fieldgroup.FieldGroup;
import com.vaadin.v7.ui.OptionGroup;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.facility.FacilityReferenceDto;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.infrastructure.PointOfEntryReferenceDto;
import de.symeda.sormas.api.region.DistrictReferenceDto;
import de.symeda.sormas.api.region.RegionReferenceDto;
import de.symeda.sormas.api.report.AggregateReportCriteria;
import de.symeda.sormas.api.report.AggregateReportDto;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.api.utils.EpiWeek;
import de.symeda.sormas.ui.UserProvider;
import de.symeda.sormas.ui.utils.ButtonHelper;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.EpiWeekFilterOption;
import de.symeda.sormas.ui.utils.VaadinUiUtil;

/**
 * @author Christopher Riedel
 * 
 *         Should better be an edit form and use an new DTO that contains the
 *         list of AggregateReport entries. The save method should be moved to a
 *         controller
 */
public class AggregateReportsEditLayout extends VerticalLayout {

	private static final long serialVersionUID = -7806379599024578146L;

	private Window window;

	private OptionGroup epiweekOptions;
	private ComboBox<Integer> comboBoxYear;
	private ComboBox<EpiWeek> comboBoxEpiweek;
	private ComboBox<RegionReferenceDto> comboBoxRegion;
	private ComboBox<DistrictReferenceDto> comboBoxDistrict;
	private ComboBox<FacilityReferenceDto> comboBoxFacility;
	private ComboBox<PointOfEntryReferenceDto> comboBoxPoe;
	private Button cancelButton;
	private Button saveButton;
	private List<AggregateReportEditForm> editForms = new ArrayList<>();
	private Map<String, Disease> diseaseMap;
	private Map<String, Disease> diseasesWithoutReport;
	private Map<Disease, AggregateReportDto> reports;
	private boolean popUpIsShown = false;

	public AggregateReportsEditLayout(Window window, AggregateReportCriteria criteria, boolean edit) {

		setWidth(560, Unit.PIXELS);
		setSpacing(false);

		this.window = window;
		
		epiweekOptions = new OptionGroup();
		epiweekOptions.addItems(EpiWeekFilterOption.values());
		epiweekOptions.addStyleNames(ValoTheme.OPTIONGROUP_HORIZONTAL, CssStyles.OPTIONGROUP_HORIZONTAL_PRIMARY);
		CssStyles.style(epiweekOptions, ValoTheme.OPTIONGROUP_HORIZONTAL);
		epiweekOptions.addValueChangeListener(e -> updateEpiweekFields());
		if (!edit) {
			addComponent(epiweekOptions);
		}

		comboBoxYear = new ComboBox<>(I18nProperties.getString(Strings.year), DateHelper.getYearsToNow(2000));
		comboBoxYear.setWidth(250, Unit.PIXELS);
		comboBoxYear.addValueChangeListener(e -> updateEpiweekFields());

		comboBoxEpiweek = new ComboBox<>(I18nProperties.getString(Strings.epiWeek));
		comboBoxEpiweek.setWidth(250, Unit.PIXELS);
		if (!edit) {
			comboBoxEpiweek.addValueChangeListener(e -> checkForExistingData());
		}
		addComponent(new HorizontalLayout(comboBoxYear, comboBoxEpiweek));

		comboBoxRegion = new ComboBox<>();
		comboBoxRegion.setWidth(250, Unit.PIXELS);
		comboBoxRegion
				.setCaption(I18nProperties.getPrefixCaption(AggregateReportDto.I18N_PREFIX, AggregateReportDto.REGION));
		comboBoxRegion.setItems(FacadeProvider.getRegionFacade().getAllActiveAsReference());
		comboBoxRegion.addValueChangeListener(e -> {
			RegionReferenceDto region = e.getValue();
			comboBoxDistrict.clear();
			if (region != null) {
				comboBoxDistrict.setItems(FacadeProvider.getDistrictFacade().getAllActiveByRegion(region.getUuid()));
				comboBoxDistrict.setEnabled(true);
			} else {
				comboBoxDistrict.setEnabled(false);
			}
		});

		comboBoxDistrict = new ComboBox<>();
		comboBoxDistrict.setWidth(250, Unit.PIXELS);
		comboBoxDistrict.setCaption(
				I18nProperties.getPrefixCaption(AggregateReportDto.I18N_PREFIX, AggregateReportDto.DISTRICT));
		comboBoxDistrict.addValueChangeListener(e -> {
			DistrictReferenceDto district = e.getValue();
			if (comboBoxFacility != null) {
				comboBoxFacility.clear();
			}
			if (comboBoxPoe != null) {
				comboBoxPoe.clear();
			}
			if (district != null) {
				if (comboBoxFacility != null) {
					comboBoxFacility.setItems(
							FacadeProvider.getFacilityFacade().getActiveHealthFacilitiesByDistrict(district, false));
					comboBoxFacility.setEnabled(true);
				}
				if (comboBoxPoe != null) {
					comboBoxPoe.setItems(
							FacadeProvider.getPointOfEntryFacade().getAllActiveByDistrict(district.getUuid(), false));
					comboBoxPoe.setEnabled(true);
				}
			} else {
				comboBoxFacility.setEnabled(false);
				comboBoxPoe.setEnabled(false);
			}
		});

		comboBoxDistrict.setEnabled(false);
		addComponent(new HorizontalLayout(comboBoxRegion, comboBoxDistrict));

		comboBoxFacility = new ComboBox<>();
		comboBoxFacility.setWidth(250, Unit.PIXELS);
		comboBoxFacility
				.setCaption(I18nProperties.getPrefixCaption(AggregateReportDto.I18N_PREFIX,
						AggregateReportDto.HEALTH_FACILITY));
		comboBoxFacility.setEnabled(false);
		comboBoxFacility.addValueChangeListener(e -> {
			if (comboBoxFacility.getValue() != null) {
				comboBoxPoe.clear();
			}
			if (!edit) {
				checkForExistingData();
			}
		});

		comboBoxPoe = new ComboBox<>();
		comboBoxPoe.setWidth(250, Unit.PIXELS);
		comboBoxPoe
				.setCaption(I18nProperties.getPrefixCaption(AggregateReportDto.I18N_PREFIX,
						AggregateReportDto.POINT_OF_ENTRY));
		comboBoxPoe.setEnabled(false);
		comboBoxPoe.addValueChangeListener(e -> {
			if (comboBoxPoe.getValue() != null) {
				comboBoxFacility.clear();
			}
			if (!edit) {
				checkForExistingData();
			}
		});
		addComponent(new HorizontalLayout(comboBoxFacility, comboBoxPoe));

		if (edit) {
			comboBoxYear.setValue(criteria.getEpiWeekFrom().getYear());
			comboBoxYear.setEnabled(false);
			comboBoxEpiweek.setValue(criteria.getEpiWeekFrom());
			comboBoxEpiweek.setEnabled(false);
			comboBoxRegion.setValue(criteria.getRegion());
			comboBoxRegion.setEnabled(false);
			comboBoxDistrict.setValue(criteria.getDistrict());
			comboBoxDistrict.setEnabled(false);
			comboBoxFacility.setValue(criteria.getHealthFacility());
			comboBoxFacility.setEnabled(false);
			comboBoxPoe.setValue(criteria.getPointOfEntry());
			comboBoxPoe.setEnabled(false);
			reports = FacadeProvider.getAggregateReportFacade()
					.getList(criteria).stream()
					.collect(Collectors.toMap(AggregateReportDto::getDisease, dto -> dto));
		}

		List<Disease> diseaseList = FacadeProvider.getDiseaseConfigurationFacade().getAllDiseases(true, null, false);
		diseaseMap = diseaseList.stream()
				.collect(Collectors.toMap(Disease::toString, disease -> disease));
		diseasesWithoutReport = new HashMap<String, Disease>(diseaseMap);
		if (reports != null) {
			for (AggregateReportDto report : reports.values()) {
				String disease = report.getDisease().toString();
				AggregateReportEditForm editForm = new AggregateReportEditForm(disease);
				editForm.setNewCases(report.getNewCases());
				editForm.setLabConfirmations(report.getLabConfirmations());
				editForm.setDeaths(report.getDeaths());
				editForms.add(editForm);
				diseasesWithoutReport.remove(disease);
			}
		}

		for (String disease : diseasesWithoutReport.keySet()) {

			AggregateReportEditForm editForm = new AggregateReportEditForm(disease);
			editForms.add(editForm);
		}

		Label legend = new Label(String.format(I18nProperties.getString(Strings.aggregateReportLegend),
				I18nProperties.getCaption(Captions.aggregateReportNewCasesShort),
				I18nProperties.getPrefixCaption(AggregateReportDto.I18N_PREFIX, AggregateReportDto.NEW_CASES),
				I18nProperties.getCaption(Captions.aggregateReportLabConfirmationsShort),
				I18nProperties.getPrefixCaption(AggregateReportDto.I18N_PREFIX, AggregateReportDto.LAB_CONFIRMATIONS),
				I18nProperties.getCaption(Captions.aggregateReportDeathsShort),
				I18nProperties.getPrefixCaption(AggregateReportDto.I18N_PREFIX, AggregateReportDto.DEATHS)));
		addComponent(legend);
		legend.addStyleName(CssStyles.VSPACE_TOP_1);

		editForms.sort((e1, e2) -> e1.getDisease().compareTo(e2.getDisease()));
		for (AggregateReportEditForm editForm : editForms) {
			addComponent(editForm);
		}

		if (!editForms.isEmpty()) {
			editForms.get(0).addStyleName(CssStyles.VSPACE_TOP_1);
		}

		HorizontalLayout buttonsPanel = new HorizontalLayout();
		buttonsPanel.setMargin(false);
		buttonsPanel.setSpacing(true);
		buttonsPanel.setWidth(100, Unit.PERCENTAGE);

		cancelButton = ButtonHelper.createButton(Captions.actionDiscard, e -> window.close());

		buttonsPanel.addComponent(cancelButton);
		buttonsPanel.setComponentAlignment(cancelButton, Alignment.BOTTOM_RIGHT);
		buttonsPanel.setExpandRatio(cancelButton, 1);

		saveButton = ButtonHelper.createButton(Captions.actionSave, event -> save(), ValoTheme.BUTTON_PRIMARY);

		buttonsPanel.addComponent(saveButton);
		buttonsPanel.setComponentAlignment(saveButton, Alignment.BOTTOM_RIGHT);
		buttonsPanel.setExpandRatio(saveButton, 0);

		buttonsPanel.addStyleName(CssStyles.VSPACE_TOP_2);
		addComponent(buttonsPanel);
		setComponentAlignment(buttonsPanel, Alignment.BOTTOM_RIGHT);

		if (!edit) {
			initialize();
		}
	}

	private void checkForExistingData() {
		if (comboBoxEpiweek.getValue() != null
				&& (comboBoxFacility.getValue() != null || comboBoxPoe.getValue() != null) && !popUpIsShown) {
			AggregateReportCriteria criteria = new AggregateReportCriteria();
			criteria.setDistrict(comboBoxDistrict.getValue());
			criteria.setEpiWeekFrom(comboBoxEpiweek.getValue());
			criteria.setEpiWeekTo(comboBoxEpiweek.getValue());
			criteria.setHealthFacility(comboBoxFacility.getValue());
			criteria.setPointOfEntry(comboBoxPoe.getValue());
			criteria.setRegion(comboBoxRegion.getValue());
			reports = FacadeProvider.getAggregateReportFacade()
					.getList(criteria).stream()
					.collect(Collectors.toMap(AggregateReportDto::getDisease, dto -> dto));
			if (!reports.isEmpty()) {
				popUpIsShown = true;
				Consumer<Boolean> resultConsumer = new Consumer<Boolean>() {
					@Override
					public void accept(Boolean edit) {
						if (edit) {
							switchToEditMode();
						} else {
							comboBoxFacility.clear();
							comboBoxPoe.clear();
						}
						popUpIsShown = false;
					}
				};
				VaadinUiUtil.showChooseOptionPopup(I18nProperties.getCaption(Captions.aggregateReportReportFound),
						new Label(I18nProperties.getString(Strings.messageAggregateReportFound)),
						I18nProperties.getCaption(Captions.aggregateReportEditReport),
						I18nProperties.getCaption(Captions.aggregateReportDiscardSelection), new Integer(480),
						resultConsumer);
			}
		}
	}

	private void initialize() {

		epiweekOptions.setValue(EpiWeekFilterOption.THIS_WEEK);
	}

	private void switchToEditMode() {

		window.setCaption(I18nProperties.getString(Strings.headingEditAggregateReport));
		for (AggregateReportEditForm editForm : editForms) {
			String disease = editForm.getDisease();
			AggregateReportDto report = reports.get(diseaseMap.get(disease));
			if (report != null) {
				editForm.setNewCases(report.getNewCases());
				editForm.setLabConfirmations(report.getLabConfirmations());
				editForm.setDeaths(report.getDeaths());
				diseasesWithoutReport.remove(disease);
			}
		}
		removeComponent(epiweekOptions);
		comboBoxYear.setEnabled(false);
		comboBoxEpiweek.setEnabled(false);
		comboBoxRegion.setEnabled(false);
		comboBoxDistrict.setEnabled(false);
		comboBoxFacility.setEnabled(false);
		comboBoxPoe.setEnabled(false);
	}

	private void save() {
		if (!isValid()) {
			Notification.show(I18nProperties.getString(Strings.errorIntegerFieldValidationFailed), "",
					Type.ERROR_MESSAGE);
			return;
		}

		for (AggregateReportEditForm editForm : editForms) {
			
			String deathsFieldValue = (String) editForm.getField(AggregateReportDto.DEATHS).getValue();
			String labFieldValue = (String) editForm.getField(AggregateReportDto.LAB_CONFIRMATIONS).getValue();
			String caseFieldValue = (String) editForm.getField(AggregateReportDto.NEW_CASES).getValue();
			
			int deaths;
			if (!deathsFieldValue.isEmpty()) {
				deaths = Integer.parseInt(deathsFieldValue);
			} else {
				deaths = 0;
			}
			
			int labConfirmations;
			if (!labFieldValue.isEmpty()) {
				labConfirmations = Integer.parseInt(labFieldValue);
			} else {
				labConfirmations = 0;
			}
			
			int newCases;
			if (!caseFieldValue.isEmpty()) {
				newCases = Integer.parseInt(caseFieldValue);
			} else {
				newCases = 0;
			}
			
			AggregateReportDto report = null;
			if (reports != null) {
				report = reports.get(diseaseMap.get(editForm.getDisease()));
			}
			if (report != null && (deaths > 0 || labConfirmations > 0 || newCases > 0)) {
				report.setDeaths(deaths);
				report.setLabConfirmations(labConfirmations);
				report.setNewCases(newCases);
				FacadeProvider.getAggregateReportFacade().saveAggregateReport(report);
			} else if (report != null) {
				FacadeProvider.getAggregateReportFacade().deleteReport(report.getUuid());
			} else {
				if (deaths > 0 || labConfirmations > 0 || newCases > 0) {
					AggregateReportDto newReport = AggregateReportDto.build();
					newReport.setDeaths(deaths);
					newReport.setDisease(diseaseMap.get(editForm.getDisease()));
					newReport.setDistrict(comboBoxDistrict.getValue());
					newReport.setEpiWeek(comboBoxEpiweek.getValue().getWeek());
					newReport.setHealthFacility(comboBoxFacility.getValue());
					newReport.setLabConfirmations(labConfirmations);
					newReport.setNewCases(newCases);
					newReport.setPointOfEntry(comboBoxPoe.getValue());
					newReport.setRegion(comboBoxRegion.getValue());
					newReport.setReportingUser(UserProvider.getCurrent().getUser().toReference());
					newReport.setYear(comboBoxYear.getValue());

					FacadeProvider.getAggregateReportFacade().saveAggregateReport(newReport);
				}
			}
		}

		window.close();
	}

	private boolean isValid() {
		boolean valid = true;
		for (AggregateReportEditForm editForm : editForms) {
			if (!editForm.isValid()) {
				valid = false;
			}
		}
		return valid;
	}

	private void updateEpiweekFields() {

		boolean enableEpiweekFields;

		if (EpiWeekFilterOption.LAST_WEEK.equals(epiweekOptions.getValue())) {

			EpiWeek lastEpiweek = DateHelper.getPreviousEpiWeek(Calendar.getInstance().getTime());

			comboBoxYear.setValue(lastEpiweek.getYear());
			comboBoxEpiweek.setValue(lastEpiweek);

			enableEpiweekFields = false;

		} else if (EpiWeekFilterOption.THIS_WEEK.equals(epiweekOptions.getValue())) {

			EpiWeek thisEpiweek = DateHelper.getEpiWeek(Calendar.getInstance().getTime());

			comboBoxYear.setValue(thisEpiweek.getYear());
			comboBoxEpiweek.setValue(thisEpiweek);

			enableEpiweekFields = false;

		} else {
			enableEpiweekFields = true;
		}

		comboBoxYear.setEnabled(enableEpiweekFields);
		comboBoxEpiweek.setEnabled(enableEpiweekFields);

		Integer year = comboBoxYear.getValue();

		if (year != null) {
			comboBoxEpiweek.setItems(DateHelper.createEpiWeekList(year));
		} else {
			comboBoxEpiweek.clear();
		}
	}

	public FieldGroup getFieldGroups() {
		// TODO Auto-generated method stub
		return null;
	}
}
