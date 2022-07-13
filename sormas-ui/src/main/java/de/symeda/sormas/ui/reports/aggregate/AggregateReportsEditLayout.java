package de.symeda.sormas.ui.reports.aggregate;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import de.symeda.sormas.api.utils.AgeGroupUtils;
import org.apache.commons.lang3.StringUtils;

import com.vaadin.server.ErrorMessage;
import com.vaadin.shared.ui.ErrorLevel;
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
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.i18n.Validations;
import de.symeda.sormas.api.infrastructure.district.DistrictReferenceDto;
import de.symeda.sormas.api.infrastructure.facility.FacilityReferenceDto;
import de.symeda.sormas.api.infrastructure.pointofentry.PointOfEntryReferenceDto;
import de.symeda.sormas.api.infrastructure.region.RegionReferenceDto;
import de.symeda.sormas.api.report.AggregateReportCriteria;
import de.symeda.sormas.api.report.AggregateReportDto;
import de.symeda.sormas.api.report.DiseaseAgeGroup;
import de.symeda.sormas.api.user.UserDto;
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

	private OptionGroup epiWeekOptions;
	private ComboBox<Integer> comboBoxYear;
	private ComboBox<EpiWeek> comboBoxEpiWeek;
	private ComboBox<RegionReferenceDto> comboBoxRegion;
	private ComboBox<DistrictReferenceDto> comboBoxDistrict;
	private ComboBox<FacilityReferenceDto> comboBoxFacility;
	private ComboBox<PointOfEntryReferenceDto> comboBoxPoe;
	private Button deleteButton;
	private Button cancelButton;
	private Button saveButton;
	private List<AggregateReportEditForm> editForms = new ArrayList<>();
	private Map<String, Disease> diseaseMap;
	private List<Disease> diseasesWithoutReport = new ArrayList<>();
	private List<DiseaseAgeGroup> diseaseAgeGroupsWithoutReport = new ArrayList<>();
	private List<AggregateReportDto> reports;
	private boolean popUpIsShown = false;

	public AggregateReportsEditLayout(Window window, boolean edit, AggregateReportDto selectedAggregateReport) {

		setWidth(560, Unit.PIXELS);
		setSpacing(false);

		this.window = window;

		epiWeekOptions = new OptionGroup();
		epiWeekOptions.addItems(EpiWeekFilterOption.values());
		epiWeekOptions.addStyleNames(ValoTheme.OPTIONGROUP_HORIZONTAL, CssStyles.OPTIONGROUP_HORIZONTAL_PRIMARY);
		CssStyles.style(epiWeekOptions, ValoTheme.OPTIONGROUP_HORIZONTAL);
		epiWeekOptions.addValueChangeListener(e -> updateEpiWeekFields());
		if (!edit) {
			addComponent(epiWeekOptions);
		}

		comboBoxYear = new ComboBox<>(I18nProperties.getString(Strings.year), DateHelper.getYearsToNow(2000));
		comboBoxYear.setWidth(250, Unit.PIXELS);
		comboBoxYear.addValueChangeListener(e -> {
			updateEpiWeekFields();
			setEpiWeekComponentErrors();
		});
		comboBoxYear.setRequiredIndicatorVisible(true);

		comboBoxEpiWeek = new ComboBox<>(I18nProperties.getString(Strings.epiWeek));
		comboBoxEpiWeek.setWidth(250, Unit.PIXELS);
		comboBoxEpiWeek.addValueChangeListener(e -> {
			if (!edit) {
				checkForExistingData();
			}
			setEpiWeekComponentErrors();
		});
		comboBoxEpiWeek.setRequiredIndicatorVisible(true);
		addComponent(new HorizontalLayout(comboBoxYear, comboBoxEpiWeek));

		comboBoxRegion = new ComboBox<>();
		comboBoxRegion.setWidth(250, Unit.PIXELS);
		comboBoxRegion.setCaption(I18nProperties.getPrefixCaption(AggregateReportDto.I18N_PREFIX, AggregateReportDto.REGION));
		comboBoxRegion.setItems(FacadeProvider.getRegionFacade().getAllActiveByServerCountry());
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
		comboBoxDistrict.setCaption(I18nProperties.getPrefixCaption(AggregateReportDto.I18N_PREFIX, AggregateReportDto.DISTRICT));
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
					comboBoxFacility.setItems(FacadeProvider.getFacilityFacade().getActiveHospitalsByDistrict(district, false));
					comboBoxFacility.setEnabled(true);
				}
				if (comboBoxPoe != null) {
					comboBoxPoe.setItems(FacadeProvider.getPointOfEntryFacade().getAllActiveByDistrict(district.getUuid(), false));
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
		comboBoxFacility.setCaption(I18nProperties.getPrefixCaption(AggregateReportDto.I18N_PREFIX, AggregateReportDto.HEALTH_FACILITY));
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
		comboBoxPoe.setCaption(I18nProperties.getPrefixCaption(AggregateReportDto.I18N_PREFIX, AggregateReportDto.POINT_OF_ENTRY));
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
			comboBoxYear.setValue(selectedAggregateReport.getYear());
			comboBoxYear.setEnabled(false);
			comboBoxEpiWeek.setValue(new EpiWeek(selectedAggregateReport.getYear(), selectedAggregateReport.getEpiWeek()));
			comboBoxEpiWeek.setEnabled(false);
			comboBoxRegion.setValue(selectedAggregateReport.getRegion());
			comboBoxRegion.setEnabled(false);
			comboBoxDistrict.setValue(selectedAggregateReport.getDistrict());
			comboBoxDistrict.setEnabled(false);
			comboBoxFacility.setValue(selectedAggregateReport.getHealthFacility());
			comboBoxFacility.setEnabled(false);
			comboBoxPoe.setValue(selectedAggregateReport.getPointOfEntry());
			comboBoxPoe.setEnabled(false);

			reports = FacadeProvider.getAggregateReportFacade().getSimilarAggregateReports(selectedAggregateReport);
		}

		List<Disease> diseaseList = FacadeProvider.getDiseaseConfigurationFacade().getAllDiseases(true, null, false);
		diseaseMap = diseaseList.stream().collect(Collectors.toMap(Disease::toString, disease -> disease));
		diseaseMap.values().forEach(disease -> {
			List<String> ageGroups = FacadeProvider.getDiseaseConfigurationFacade().getAgeGroups(disease);
			if (ageGroups != null) {
				ageGroups.forEach(ageGroup -> {
					diseaseAgeGroupsWithoutReport.add(new DiseaseAgeGroup(disease, ageGroup));
				});
			} else {
				diseaseAgeGroupsWithoutReport.add(new DiseaseAgeGroup(disease, null));
			}
			diseasesWithoutReport.add(disease);
		});

		if (reports != null) {
			for (AggregateReportDto report : reports) {

				String firstAgeGroup = FacadeProvider.getDiseaseConfigurationFacade().getFirstAgeGroup(report.getDisease());

				boolean isFirstAgeGroup = report.getAgeGroup() != null && report.getAgeGroup().equals(firstAgeGroup);

				AggregateReportEditForm editForm = new AggregateReportEditForm(report.getDisease(), report.getAgeGroup(), isFirstAgeGroup);
				editForm.setNewCases(report.getNewCases());
				editForm.setLabConfirmations(report.getLabConfirmations());
				editForm.setDeaths(report.getDeaths());
				editForms.add(editForm);
				diseaseAgeGroupsWithoutReport.remove(new DiseaseAgeGroup(report.getDisease(), report.getAgeGroup()));
				diseasesWithoutReport.remove(report.getDisease());
			}
		}

		for (Disease disease : diseasesWithoutReport) {

			List<String> ageGroups = FacadeProvider.getDiseaseConfigurationFacade().getAgeGroups(disease);
			if (ageGroups == null || ageGroups.isEmpty()) {
				editForms.add(new AggregateReportEditForm(disease, null, false));
			} else {
				int i = 0;
				for (String ageGroup : ageGroups) {
					editForms.add(new AggregateReportEditForm(disease, ageGroup, i == 0));
					i++;
				}
			}
		}

		Label legend = new Label(
			String.format(
				I18nProperties.getString(Strings.aggregateReportLegend),
				I18nProperties.getCaption(Captions.aggregateReportNewCasesShort),
				I18nProperties.getPrefixCaption(AggregateReportDto.I18N_PREFIX, AggregateReportDto.NEW_CASES),
				I18nProperties.getCaption(Captions.aggregateReportLabConfirmationsShort),
				I18nProperties.getPrefixCaption(AggregateReportDto.I18N_PREFIX, AggregateReportDto.LAB_CONFIRMATIONS),
				I18nProperties.getCaption(Captions.aggregateReportDeathsShort),
				I18nProperties.getPrefixCaption(AggregateReportDto.I18N_PREFIX, AggregateReportDto.DEATHS)));
		addComponent(legend);
		legend.addStyleName(CssStyles.VSPACE_TOP_1);

		editForms.stream()
			.sorted(
				Comparator.comparing(AggregateReportEditForm::getDisease, Comparator.nullsFirst(Comparator.comparing(Disease::toString)))
					.thenComparing(AggregateReportEditForm::getAgeGroup, AgeGroupUtils.getComparator()))
			.forEach(this::addComponent);

		if (!editForms.isEmpty()) {
			editForms.get(0).addStyleName(CssStyles.VSPACE_TOP_1);
		}

		HorizontalLayout buttonsPanel = new HorizontalLayout();
		buttonsPanel.setMargin(false);
		buttonsPanel.setSpacing(true);
		buttonsPanel.setWidth(100, Unit.PERCENTAGE);

		if (edit) {
			deleteButton = ButtonHelper.createButton(Captions.actionDelete, clickEvent -> {
				deleteAggregateReports();
			});
			buttonsPanel.addComponent(deleteButton);
			buttonsPanel.setComponentAlignment(deleteButton, Alignment.BOTTOM_RIGHT);
			buttonsPanel.setExpandRatio(deleteButton, 0);
		}

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
		if (comboBoxEpiWeek.getValue() != null && (comboBoxFacility.getValue() != null || comboBoxPoe.getValue() != null) && !popUpIsShown) {
			AggregateReportCriteria criteria = new AggregateReportCriteria();
			criteria.setDistrict(comboBoxDistrict.getValue());
			criteria.setEpiWeekFrom(comboBoxEpiWeek.getValue());
			criteria.setEpiWeekTo(comboBoxEpiWeek.getValue());
			criteria.setHealthFacility(comboBoxFacility.getValue());
			criteria.setPointOfEntry(comboBoxPoe.getValue());
			criteria.setRegion(comboBoxRegion.getValue());
			criteria.setConsiderNullJurisdictionCheck(true);
			reports = FacadeProvider.getAggregateReportFacade().getAggregateReports(criteria);
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
				VaadinUiUtil.showChooseOptionPopup(
					I18nProperties.getCaption(Captions.aggregateReportReportFound),
					new Label(I18nProperties.getString(Strings.messageAggregateReportFound)),
					I18nProperties.getCaption(Captions.aggregateReportEditReport),
					I18nProperties.getCaption(Captions.aggregateReportDiscardSelection),
					new Integer(480),
					resultConsumer);
			}
		}
	}

	private void initialize() {

		epiWeekOptions.setValue(EpiWeekFilterOption.THIS_WEEK);
		final UserDto currentUser = UserProvider.getCurrent().getUser();
		RegionReferenceDto region = currentUser.getRegion();
		if (region != null) {
			comboBoxRegion.setValue(region);
			comboBoxRegion.setEnabled(false);
		}
		DistrictReferenceDto district = currentUser.getDistrict();
		if (district != null) {
			comboBoxDistrict.setValue(district);
			comboBoxDistrict.setEnabled(false);
		}
		FacilityReferenceDto healthFacility = currentUser.getHealthFacility();
		PointOfEntryReferenceDto pointOfEntry = currentUser.getPointOfEntry();
		if (healthFacility != null || pointOfEntry != null) {
			comboBoxFacility.setValue(healthFacility);
			comboBoxPoe.setValue(pointOfEntry);
			comboBoxFacility.setEnabled(false);
			comboBoxPoe.setEnabled(false);
		}
	}

	private void switchToEditMode() {

		window.setCaption(I18nProperties.getString(Strings.headingEditAggregateReport));
		for (AggregateReportEditForm editForm : editForms) {
			String ageGroup = editForm.getAgeGroup();
			Optional<AggregateReportDto> optionalAggregateReportDto = getReportByDiseaseAndAgeGroup(editForm.getDisease(), ageGroup);
			if (optionalAggregateReportDto.isPresent()) {
				AggregateReportDto report = optionalAggregateReportDto.get();
				editForm.setNewCases(report.getNewCases());
				editForm.setLabConfirmations(report.getLabConfirmations());
				editForm.setDeaths(report.getDeaths());
				diseaseAgeGroupsWithoutReport.remove(new DiseaseAgeGroup(editForm.getDisease(), ageGroup));
			}
		}
		removeComponent(epiWeekOptions);
		comboBoxYear.setEnabled(false);
		comboBoxEpiWeek.setEnabled(false);
		comboBoxRegion.setEnabled(false);
		comboBoxDistrict.setEnabled(false);
		comboBoxFacility.setEnabled(false);
		comboBoxPoe.setEnabled(false);
	}

	private Optional<AggregateReportDto> getReportByDiseaseAndAgeGroup(Disease disease, String ageGroup) {

		List<AggregateReportDto> foundReports = new ArrayList<>();
		reports.stream().filter(dto -> {
			boolean ageGroupMatches = dto.getAgeGroup() != null ? dto.getAgeGroup().equals(ageGroup) : ageGroup == null;
			boolean diseaseMatches = dto.getDisease() != null ? dto.getDisease().equals(disease) : disease == null;
			return diseaseMatches && ageGroupMatches;
		}).max(Comparator.comparing(AggregateReportDto::getChangeDate)).ifPresent(foundReports::add);

		if (foundReports.isEmpty()) {
			return Optional.empty();
		}
		return Optional.ofNullable(foundReports.get(0));
	}

	private void save() {

		if (!isValid()) {
			Notification.show(I18nProperties.getString(Strings.errorIntegerFieldValidationFailed), "", Type.ERROR_MESSAGE);
			return;
		}

		if (comboBoxYear.getComponentError() != null) {
			Notification.show(I18nProperties.getValidationError(Validations.specifyYear), "", Type.ERROR_MESSAGE);
			return;
		}

		if (comboBoxEpiWeek.getComponentError() != null) {
			Notification.show(I18nProperties.getValidationError(Validations.specifyEpiWeek), "", Type.ERROR_MESSAGE);
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
				Optional<AggregateReportDto> reportByDiseaseAndAgeGroup =
					getReportByDiseaseAndAgeGroup(editForm.getDisease(), editForm.getAgeGroup());
				if (reportByDiseaseAndAgeGroup.isPresent()) {
					report = reportByDiseaseAndAgeGroup.get();
				}
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
					newReport.setDisease(editForm.getDisease());
					newReport.setAgeGroup(editForm.getAgeGroup());
					newReport.setDistrict(comboBoxDistrict.getValue());
					newReport.setEpiWeek(comboBoxEpiWeek.getValue().getWeek());
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

	private void deleteAggregateReports() {
		List<String> aggregateReportUuidList = reports.stream().map(AggregateReportDto::getUuid).collect(Collectors.toList());

		FacadeProvider.getAggregateReportFacade().deleteAggregateReports(aggregateReportUuidList);
		window.close();
	}

	private void updateEpiWeekFields() {

		boolean enableEpiweekFields;

		if (EpiWeekFilterOption.LAST_WEEK.equals(epiWeekOptions.getValue())) {
			EpiWeek lastEpiweek = DateHelper.getPreviousEpiWeek(Calendar.getInstance().getTime());
			comboBoxYear.setValue(lastEpiweek.getYear());
			comboBoxEpiWeek.setValue(lastEpiweek);
			enableEpiweekFields = false;
		} else if (EpiWeekFilterOption.THIS_WEEK.equals(epiWeekOptions.getValue())) {
			EpiWeek thisEpiweek = DateHelper.getEpiWeek(Calendar.getInstance().getTime());
			comboBoxYear.setValue(thisEpiweek.getYear());
			comboBoxEpiWeek.setValue(thisEpiweek);
			enableEpiweekFields = false;
		} else {
			enableEpiweekFields = true;
		}

		comboBoxYear.setEnabled(enableEpiweekFields);
		comboBoxEpiWeek.setEnabled(enableEpiweekFields);

		Integer year = comboBoxYear.getValue();

		if (year != null) {
			EpiWeek selectedEpiWeek = comboBoxEpiWeek.getValue();
			List<EpiWeek> epiWeekOptions = DateHelper.createEpiWeekList(year);
			comboBoxEpiWeek.setItems(epiWeekOptions);
			if (selectedEpiWeek != null) {
				EpiWeek adjustedEpiWeek = DateHelper.getSameEpiWeek(selectedEpiWeek, epiWeekOptions);
				comboBoxEpiWeek.setValue(adjustedEpiWeek);
			}
		} else {
			comboBoxEpiWeek.clear();
		}
	}

	private void setEpiWeekComponentErrors() {

		comboBoxYear.setComponentError(comboBoxYear.getValue() == null ? new ErrorMessage() {

			@Override
			public ErrorLevel getErrorLevel() {
				return ErrorLevel.ERROR;
			}

			@Override
			public String getFormattedHtmlMessage() {
				return I18nProperties.getValidationError(Validations.specifyYear);
			}
		} : null);

		comboBoxEpiWeek.setComponentError(comboBoxEpiWeek.getValue() == null ? new ErrorMessage() {

			@Override
			public ErrorLevel getErrorLevel() {
				return ErrorLevel.ERROR;
			}

			@Override
			public String getFormattedHtmlMessage() {
				return I18nProperties.getValidationError(Validations.specifyEpiWeek);
			}
		} : null);
	}

	public FieldGroup getFieldGroups() {
		// TODO Auto-generated method stub
		return null;
	}
}
