/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2021 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

package de.symeda.sormas.ui.configuration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.vaadin.data.Binder;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.DateField;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.infrastructure.district.DistrictReferenceDto;
import de.symeda.sormas.api.infrastructure.facility.FacilityReferenceDto;
import de.symeda.sormas.api.infrastructure.region.RegionReferenceDto;
import de.symeda.sormas.api.sample.SampleMaterial;
import de.symeda.sormas.api.sample.SamplePurpose;
import de.symeda.sormas.ui.configuration.generate.CaseGenerator;
import de.symeda.sormas.ui.configuration.generate.ContactGenerator;
import de.symeda.sormas.ui.configuration.generate.EventGenerator;
import de.symeda.sormas.ui.configuration.generate.SampleGenerator;
import de.symeda.sormas.ui.configuration.generate.config.CaseGenerationConfig;
import de.symeda.sormas.ui.configuration.generate.config.ContactGenerationConfig;
import de.symeda.sormas.ui.configuration.generate.config.EventGenerationConfig;
import de.symeda.sormas.ui.configuration.generate.config.SampleGenerationConfig;
import de.symeda.sormas.ui.configuration.validator.StringToNumberValidator;
import de.symeda.sormas.ui.utils.ButtonHelper;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.DateFormatHelper;
import de.symeda.sormas.ui.utils.VaadinUiUtil;

public class DevModeView extends AbstractConfigurationView {

	private static final long serialVersionUID = -6589135368637794263L;

	public static final String VIEW_NAME = ROOT_VIEW_NAME + "/devMode";

	private TextField seedField;
	private CheckBox useManualSeedCheckbox;
	private static boolean useManualSeed = false;
	private static long manualSeed = 0;

	private VerticalLayout contentLayout;

	private Binder<CaseGenerationConfig> caseGeneratorConfigBinder = new Binder<>();
	private Binder<ContactGenerationConfig> contactGeneratorConfigBinder = new Binder<>();
	private Binder<EventGenerationConfig> eventGeneratorConfigBinder = new Binder<>();
	private Binder<SampleGenerationConfig> sampleGeneratorConfigBinder = new Binder<>();
	CaseGenerationConfig caseGenerationConfig = new CaseGenerationConfig();
	ContactGenerationConfig contactGenerationConfig = new ContactGenerationConfig();
	EventGenerationConfig eventGenerationConfig = new EventGenerationConfig();
	static SampleGenerationConfig sampleGenerationConfig = SampleGenerationConfig.getDefaultConfig();

	private CaseGenerator caseGenerator = new CaseGenerator();
	private ContactGenerator contactGenerator = new ContactGenerator();
	private EventGenerator eventGenerator = new EventGenerator();
	private SampleGenerator sampleGenerator = new SampleGenerator();

	public DevModeView() {

		super(VIEW_NAME);

		contentLayout = new VerticalLayout();
		contentLayout.setMargin(true);
		contentLayout.setSpacing(true);
		contentLayout.setWidth(100, Unit.PERCENTAGE);
		contentLayout.setStyleName("crud-main-layout");

		contentLayout.addComponent(
			new Label(VaadinIcons.INFO_CIRCLE.getHtml() + " " + I18nProperties.getString(Strings.infoDeveloperOptions), ContentMode.HTML));
		contentLayout.addComponent(
			new Label(
				VaadinIcons.INFO_CIRCLE.getHtml() + " " + I18nProperties.getString(Strings.infoDeveloperOptionsContactGeneration),
				ContentMode.HTML));
		contentLayout.addComponent(
			new Label(VaadinIcons.INFO_CIRCLE.getHtml() + " " + I18nProperties.getString(Strings.infoDeveloperOptionsSeedUsage), ContentMode.HTML));

		contentLayout.addComponent(createSeedSettingsLayout());
		contentLayout.addComponent(createCaseGeneratorLayout());
		contentLayout.addComponent(createContactGeneratorLayout());
		contentLayout.addComponent(createEventsGeneratorLayout());
		contentLayout.addComponent(createSamplesGeneratorLayout());
		contentLayout.addComponent(createDevButtonsLayout());

		addComponent(contentLayout);
	}

	private HorizontalLayout createDevButtonsLayout() {

		HorizontalLayout horizontalLayout = new HorizontalLayout();
		horizontalLayout.setSpacing(true);

		Button btnResetEnumCache =
			ButtonHelper.createButton((Captions.actionResetEnumCache), e -> FacadeProvider.getCustomizableEnumFacade().loadData());
		horizontalLayout.addComponent(btnResetEnumCache);

		Button btnExecuteAutomaticDeletion = ButtonHelper.createButton((Captions.actionExecuteAutomaticDeletion), e -> {
			VaadinUiUtil.showSimplePopupWindow(
				I18nProperties.getString(Strings.headingAutomaticDeletionStarted),
				I18nProperties.getString(Strings.messageAutomaticDeletionStarted),
				ContentMode.TEXT,
				640);
			FacadeProvider.getDeletionConfigurationFacade().startAutomaticDeletion();
		});
		horizontalLayout.addComponent(btnExecuteAutomaticDeletion);

		return horizontalLayout;
	}

	private HorizontalLayout createSeedSettingsLayout() {
		HorizontalLayout horizontalLayout = new HorizontalLayout();
		VerticalLayout verticalLayout = new VerticalLayout();
		verticalLayout.setMargin(false);
		verticalLayout.setSpacing(false);

		Label seedLabel = new Label(I18nProperties.getString(Strings.labelActualLongSeed) + " " + manualSeed);
		seedField = new TextField();
		seedField.setCaption(I18nProperties.getCaption(Captions.devModeGeneratorSeed));
		seedField.setValue(Long.toString(manualSeed, 36));
		seedField.setMaxLength(11);
		seedField.addValueChangeListener(e -> {
			try {
				manualSeed = Long.parseLong(e.getValue(), 36);
			} catch (NumberFormatException ex) {
				manualSeed = 0;
			}
			seedLabel.setValue(I18nProperties.getString(Strings.labelActualLongSeed) + " " + manualSeed);
		});

		useManualSeedCheckbox = new CheckBox(I18nProperties.getCaption(Captions.devModeUseSeed));
		useManualSeedCheckbox.setValue(useManualSeed);
		useManualSeedCheckbox.addValueChangeListener(e -> {
			useManualSeed = e.getValue();
		});

		List<RegionReferenceDto> regions = FacadeProvider.getRegionFacade().getAllActiveByServerCountry();
		Button performanceConfigButton = ButtonHelper.createButton(I18nProperties.getCaption(Captions.devModeLoadPerformanceTestConfig), e -> {
			seedField.setValue("performance");
			useManualSeedCheckbox.setValue(true);
			RegionReferenceDto region = regions.isEmpty() ? null : regions.get(0);
			List<DistrictReferenceDto> allActiveDistrictsByRegion =
				region == null ? Collections.EMPTY_LIST : FacadeProvider.getDistrictFacade().getAllActiveByRegion(region.getUuid());
			DistrictReferenceDto district = allActiveDistrictsByRegion.isEmpty() ? null : allActiveDistrictsByRegion.get(0);

			caseGenerationConfig.loadPerformanceTestConfig();
			caseGenerationConfig.setRegion(region);
			caseGenerationConfig.setDistrict(district);
			contactGenerationConfig.loadPerformanceTestConfig();
			contactGenerationConfig.setRegion(region);
			contactGenerationConfig.setDistrict(district);
			eventGenerationConfig.loadPerformanceTestConfig();
			eventGenerationConfig.setRegion(region);
			eventGenerationConfig.setDistrict(district);
			sampleGenerationConfig = SampleGenerationConfig.getPerformanceTestConfig();
			sampleGenerationConfig.setRegion(region);
			sampleGenerationConfig.setDistrict(district);

			caseGeneratorConfigBinder.readBean(caseGenerationConfig);
			contactGeneratorConfigBinder.readBean(contactGenerationConfig);
			eventGeneratorConfigBinder.readBean(eventGenerationConfig);
			sampleGeneratorConfigBinder.readBean(sampleGenerationConfig);
		}, CssStyles.FORCE_CAPTION);
		Button defaultConfigButton = ButtonHelper.createButton(I18nProperties.getCaption(Captions.devModeLoadDefaultConfig), e -> {
			useManualSeedCheckbox.setValue(false);
			RegionReferenceDto region = regions.isEmpty() ? null : regions.get(0);
			List<DistrictReferenceDto> allActiveDistrictsByRegion =
				region == null ? Collections.EMPTY_LIST : FacadeProvider.getDistrictFacade().getAllActiveByRegion(region.getUuid());
			DistrictReferenceDto district = allActiveDistrictsByRegion.isEmpty() ? null : allActiveDistrictsByRegion.get(0);

			caseGenerationConfig.loadDefaultConfig();
			caseGenerationConfig.setRegion(region);
			contactGenerationConfig.loadDefaultConfig();
			contactGenerationConfig.setRegion(region);
			eventGenerationConfig.loadDefaultConfig();
			eventGenerationConfig.setRegion(region);
			eventGenerationConfig.setDistrict(district);
			sampleGenerationConfig = SampleGenerationConfig.getDefaultConfig();
			sampleGenerationConfig.setRegion(region);
			sampleGenerationConfig.setDistrict(district);

			caseGeneratorConfigBinder.readBean(caseGenerationConfig);
			contactGeneratorConfigBinder.readBean(contactGenerationConfig);
			eventGeneratorConfigBinder.readBean(eventGenerationConfig);
			sampleGeneratorConfigBinder.readBean(sampleGenerationConfig);
		}, CssStyles.FORCE_CAPTION);

		verticalLayout.addComponent(seedLabel);
		verticalLayout.addComponent(useManualSeedCheckbox);
		horizontalLayout.addComponent(seedField);
		horizontalLayout.addComponent(verticalLayout);
		horizontalLayout.addComponent(performanceConfigButton);
		horizontalLayout.addComponent(defaultConfigButton);
		horizontalLayout.setComponentAlignment(verticalLayout, Alignment.MIDDLE_LEFT);

		return horizontalLayout;
	}

	private VerticalLayout createCaseGeneratorLayout() {
		VerticalLayout caseGeneratorLayout = new VerticalLayout();
		caseGeneratorLayout.setMargin(false);
		caseGeneratorLayout.setSpacing(false);

		Label heading = new Label(I18nProperties.getString(Strings.headingGenerateCases));
		CssStyles.style(heading, CssStyles.H2);
		caseGeneratorLayout.addComponent(heading);

		HorizontalLayout caseOptionsLayout = new HorizontalLayout();

		TextField caseCountField = new TextField();
		caseCountField.setCaption(I18nProperties.getCaption(Captions.devModeCaseCount));
		caseGeneratorConfigBinder.forField(caseCountField)
			.withValidator(new StringToNumberValidator("Must be a positive number", true))
			.bind(CaseGenerationConfig::getEntityCount, CaseGenerationConfig::setEntityCount);
		caseOptionsLayout.addComponent(caseCountField);

		DateField startDateField = new DateField();
		startDateField.setCaption(I18nProperties.getCaption(Captions.devModeCaseStartDate));
		startDateField.setDateFormat(DateFormatHelper.getDateFormatPattern());
		startDateField.setLenient(true);
		caseGeneratorConfigBinder.bind(startDateField, CaseGenerationConfig::getStartDate, CaseGenerationConfig::setStartDate);
		caseOptionsLayout.addComponent(startDateField);

		DateField endDateField = new DateField();
		endDateField.setCaption(I18nProperties.getCaption(Captions.devModeCaseEndDate));
		endDateField.setDateFormat(DateFormatHelper.getDateFormatPattern());
		endDateField.setLenient(true);
		caseGeneratorConfigBinder.bind(endDateField, CaseGenerationConfig::getEndDate, CaseGenerationConfig::setEndDate);
		caseOptionsLayout.addComponent(endDateField);

		ComboBox<Disease> diseaseField = new ComboBox<>(null, FacadeProvider.getDiseaseConfigurationFacade().getAllDiseases(true, true, true));
		diseaseField.setCaption(I18nProperties.getCaption(Captions.devModeCaseDisease));
		caseGeneratorConfigBinder.bind(diseaseField, CaseGenerationConfig::getDisease, CaseGenerationConfig::setDisease);
		caseOptionsLayout.addComponent(diseaseField);

		List<RegionReferenceDto> regions = FacadeProvider.getRegionFacade().getAllActiveByServerCountry();
		ComboBox<RegionReferenceDto> regionField = new ComboBox<RegionReferenceDto>(null, regions);
		regionField.setCaption(I18nProperties.getCaption(Captions.devModeCaseRegion));
		caseGeneratorConfigBinder.bind(regionField, CaseGenerationConfig::getRegion, CaseGenerationConfig::setRegion);
		caseOptionsLayout.addComponent(regionField);

		ComboBox<DistrictReferenceDto> districtField = new ComboBox<DistrictReferenceDto>();
		districtField.setCaption(I18nProperties.getCaption(Captions.devModeCaseDistrict));
		caseGeneratorConfigBinder.bind(districtField, CaseGenerationConfig::getDistrict, CaseGenerationConfig::setDistrict);
		caseOptionsLayout.addComponent(districtField);

		regionField.addValueChangeListener(event -> {
			RegionReferenceDto region = event.getValue();
			if (region != null) {
				districtField.setItems(FacadeProvider.getDistrictFacade().getAllActiveByRegion(region.getUuid()));
			} else {
				districtField.setItems(new ArrayList<DistrictReferenceDto>());
			}
		});

		Button generateButton =
			ButtonHelper.createButton(Captions.devModeGenerateCases, e -> caseGenerator.generate(caseGeneratorConfigBinder), CssStyles.FORCE_CAPTION);
		caseOptionsLayout.addComponent(generateButton);

		caseGeneratorLayout.addComponent(caseOptionsLayout);

		if (!regions.isEmpty()) {
			caseGenerationConfig.setRegion(regions.get(0));
		}
		caseGeneratorConfigBinder.setBean(caseGenerationConfig);

		return caseGeneratorLayout;
	}

	private VerticalLayout createContactGeneratorLayout() {

		VerticalLayout contactGeneratorLayout = new VerticalLayout();
		contactGeneratorLayout.setMargin(false);
		contactGeneratorLayout.setSpacing(false);

		Label heading = new Label(I18nProperties.getString(Strings.headingGenerateContacts));
		CssStyles.style(heading, CssStyles.H2);
		contactGeneratorLayout.addComponent(heading);

		HorizontalLayout contactOptionsFirstLineLayout = new HorizontalLayout();

		TextField contactCountField = new TextField();
		contactCountField.setCaption(I18nProperties.getCaption(Captions.devModeContactCount));
		contactGeneratorConfigBinder.forField(contactCountField)
			.withValidator(new StringToNumberValidator("Must be a positive number", true))
			.bind(ContactGenerationConfig::getEntityCount, ContactGenerationConfig::setEntityCount);
		contactOptionsFirstLineLayout.addComponent(contactCountField);

		DateField startDateField = new DateField();
		startDateField.setCaption(I18nProperties.getCaption(Captions.devModeContactStartDate));
		startDateField.setDateFormat(DateFormatHelper.getDateFormatPattern());
		startDateField.setLenient(true);
		contactGeneratorConfigBinder.bind(startDateField, ContactGenerationConfig::getStartDate, ContactGenerationConfig::setStartDate);
		contactOptionsFirstLineLayout.addComponent(startDateField);

		DateField endDateField = new DateField();
		endDateField.setCaption(I18nProperties.getCaption(Captions.devModeContactEndDate));
		endDateField.setDateFormat(DateFormatHelper.getDateFormatPattern());
		endDateField.setLenient(true);
		contactGeneratorConfigBinder.bind(endDateField, ContactGenerationConfig::getEndDate, ContactGenerationConfig::setEndDate);
		contactOptionsFirstLineLayout.addComponent(endDateField);

		ComboBox<Disease> diseaseField = new ComboBox<>(null, FacadeProvider.getDiseaseConfigurationFacade().getAllDiseases(true, true, true));
		diseaseField.setCaption(I18nProperties.getCaption(Captions.devModeContactDisease));
		contactGeneratorConfigBinder.bind(diseaseField, ContactGenerationConfig::getDisease, ContactGenerationConfig::setDisease);
		contactOptionsFirstLineLayout.addComponent(diseaseField);

		List<RegionReferenceDto> regions = FacadeProvider.getRegionFacade().getAllActiveByServerCountry();
		ComboBox<RegionReferenceDto> regionField = new ComboBox<RegionReferenceDto>(null, regions);
		regionField.setCaption(I18nProperties.getCaption(Captions.devModeContactRegion));
		contactGeneratorConfigBinder.bind(regionField, ContactGenerationConfig::getRegion, ContactGenerationConfig::setRegion);
		contactOptionsFirstLineLayout.addComponent(regionField);

		ComboBox<DistrictReferenceDto> districtField = new ComboBox<DistrictReferenceDto>();
		districtField.setCaption(I18nProperties.getCaption(Captions.devModeContactDistrict));
		contactGeneratorConfigBinder.bind(districtField, ContactGenerationConfig::getDistrict, ContactGenerationConfig::setDistrict);
		contactOptionsFirstLineLayout.addComponent(districtField);

		regionField.addValueChangeListener(event -> {
			RegionReferenceDto region = event.getValue();
			if (region != null) {
				districtField.setItems(FacadeProvider.getDistrictFacade().getAllActiveByRegion(region.getUuid()));
			} else {
				districtField.setItems(new ArrayList<DistrictReferenceDto>());
			}
		});

		Button generateButton = ButtonHelper
			.createButton(Captions.devModeGenerateContacts, e -> contactGenerator.generate(contactGeneratorConfigBinder), CssStyles.FORCE_CAPTION);
		contactOptionsFirstLineLayout.addComponent(generateButton);

		contactGeneratorLayout.addComponent(contactOptionsFirstLineLayout);

		HorizontalLayout contactOptionsSecondLineLayout = new HorizontalLayout();

		CheckBox createWithoutSourceCasesField = new CheckBox(I18nProperties.getCaption(Captions.devModeContactCreateWithoutSourceCases));
		contactGeneratorConfigBinder.bind(
			createWithoutSourceCasesField,
			ContactGenerationConfig::isCreateWithoutSourceCases,
			ContactGenerationConfig::setCreateWithoutSourceCases);
		contactOptionsSecondLineLayout.addComponent(createWithoutSourceCasesField);

		CheckBox createMultipleContactsPerPersonField =
			new CheckBox(I18nProperties.getCaption(Captions.devModeContactCreateMultipleContactsPerPerson));
		contactGeneratorConfigBinder.bind(
			createMultipleContactsPerPersonField,
			ContactGenerationConfig::isCreateMultipleContactsPerPerson,
			ContactGenerationConfig::setCreateMultipleContactsPerPerson);
		contactOptionsSecondLineLayout.addComponent(createMultipleContactsPerPersonField);

		CheckBox createWithVisitsField = new CheckBox(I18nProperties.getCaption(Captions.devModeContactCreateWithVisits));
		contactGeneratorConfigBinder
			.bind(createWithVisitsField, ContactGenerationConfig::isCreateWithVisits, ContactGenerationConfig::setCreateWithVisits);
		contactOptionsSecondLineLayout.addComponent(createWithVisitsField);

		contactGeneratorLayout.addComponent(contactOptionsSecondLineLayout);

		if (!regions.isEmpty()) {
			contactGenerationConfig.setRegion(regions.get(0));
		}
		contactGeneratorConfigBinder.setBean(contactGenerationConfig);

		return contactGeneratorLayout;
	}

	private VerticalLayout createEventsGeneratorLayout() {
		VerticalLayout eventGeneratorLayout = new VerticalLayout();
		eventGeneratorLayout.setMargin(false);
		eventGeneratorLayout.setSpacing(false);

		Label heading = new Label(I18nProperties.getCaption(Captions.devModeGenerateEvents));
		CssStyles.style(heading, CssStyles.H2);
		eventGeneratorLayout.addComponent(heading);

		HorizontalLayout eventOptionsFirstLineLayout = new HorizontalLayout();

		TextField eventCountField = new TextField();
		eventCountField.setCaption(I18nProperties.getCaption(Captions.devModeEventCount));
		eventGeneratorConfigBinder.forField(eventCountField)
			.withValidator(new StringToNumberValidator("Must be a positive number", true))
			.bind(EventGenerationConfig::getEntityCount, EventGenerationConfig::setEntityCount);
		eventOptionsFirstLineLayout.addComponent(eventCountField);

		DateField startDateField = new DateField();
		startDateField.setCaption(I18nProperties.getCaption(Captions.devModeEventStartDate));
		startDateField.setDateFormat(DateFormatHelper.getDateFormatPattern());
		startDateField.setLenient(true);
		eventGeneratorConfigBinder.bind(startDateField, EventGenerationConfig::getStartDate, EventGenerationConfig::setStartDate);
		eventOptionsFirstLineLayout.addComponent(startDateField);

		DateField endDateField = new DateField();
		endDateField.setCaption(I18nProperties.getCaption(Captions.devModeEventEndDate));
		endDateField.setDateFormat(DateFormatHelper.getDateFormatPattern());
		endDateField.setLenient(true);
		eventGeneratorConfigBinder.bind(endDateField, EventGenerationConfig::getEndDate, EventGenerationConfig::setEndDate);
		eventOptionsFirstLineLayout.addComponent(endDateField);

		ComboBox<Disease> diseaseField = new ComboBox<>(null, FacadeProvider.getDiseaseConfigurationFacade().getAllDiseases(true, true, true));
		diseaseField.setCaption(I18nProperties.getCaption(Captions.devModeEventDisease));
		eventGeneratorConfigBinder.bind(diseaseField, EventGenerationConfig::getDisease, EventGenerationConfig::setDisease);
		eventOptionsFirstLineLayout.addComponent(diseaseField);

		List<RegionReferenceDto> regions = FacadeProvider.getRegionFacade().getAllActiveAsReference();
		ComboBox<RegionReferenceDto> regionField = new ComboBox<RegionReferenceDto>(null, regions);
		regionField.setCaption(I18nProperties.getCaption(Captions.devModeEventRegion));
		eventGeneratorConfigBinder.bind(regionField, EventGenerationConfig::getRegion, EventGenerationConfig::setRegion);
		eventOptionsFirstLineLayout.addComponent(regionField);

		ComboBox<DistrictReferenceDto> districtField = new ComboBox<DistrictReferenceDto>();
		districtField.setCaption(I18nProperties.getCaption(Captions.devModeEventDistrict));
		eventGeneratorConfigBinder.bind(districtField, EventGenerationConfig::getDistrict, EventGenerationConfig::setDistrict);
		eventOptionsFirstLineLayout.addComponent(districtField);

		regionField.addValueChangeListener(event -> {
			RegionReferenceDto region = event.getValue();
			if (region != null) {
				districtField.setItems(FacadeProvider.getDistrictFacade().getAllActiveByRegion(region.getUuid()));
			} else {
				districtField.setItems(new ArrayList<DistrictReferenceDto>());
			}
		});

		Button generateButton = ButtonHelper.createButton(
			I18nProperties.getCaption(Captions.devModeGenerateEvents),
			e -> eventGenerator.generate(eventGeneratorConfigBinder),
			CssStyles.FORCE_CAPTION);
		eventOptionsFirstLineLayout.addComponent(generateButton);

		eventGeneratorLayout.addComponent(eventOptionsFirstLineLayout);

		HorizontalLayout eventOptionsSecondLineLayout = new HorizontalLayout();

		TextField minParticipantsPerEventField = new TextField();
		minParticipantsPerEventField.setCaption(I18nProperties.getCaption(Captions.devModeEventMinParticipants));
		eventGeneratorConfigBinder.forField(minParticipantsPerEventField)
			.withValidator(new StringToNumberValidator("Must be a positive number", true))
			.bind(EventGenerationConfig::getMinParticipantsPerEvent, EventGenerationConfig::setMinParticipantsPerEvent);
		eventOptionsSecondLineLayout.addComponent(minParticipantsPerEventField);

		TextField maxParticipantsPerEventField = new TextField();
		maxParticipantsPerEventField.setCaption(I18nProperties.getCaption(Captions.devModeEventMaxParticipants));
		eventGeneratorConfigBinder.forField(maxParticipantsPerEventField)
			.withValidator(new StringToNumberValidator("Must be a positive number", true))
			.bind(EventGenerationConfig::getMaxParticipantsPerEvent, EventGenerationConfig::setMaxParticipantsPerEvent);
		eventOptionsSecondLineLayout.addComponent(maxParticipantsPerEventField);

		TextField minContactsPerParticipantField = new TextField();
		minContactsPerParticipantField.setCaption(I18nProperties.getCaption(Captions.devModeEventMinContacts));
		eventGeneratorConfigBinder.forField(minContactsPerParticipantField)
			.withValidator(new StringToNumberValidator("Must be a positive number", true))
			.bind(EventGenerationConfig::getMinContactsPerParticipant, EventGenerationConfig::setMinContactsPerParticipant);
		eventOptionsSecondLineLayout.addComponent(minContactsPerParticipantField);

		TextField maxContactsPerParticipantField = new TextField();
		maxContactsPerParticipantField.setCaption(I18nProperties.getCaption(Captions.devModeEventMaxContacts));
		eventGeneratorConfigBinder.forField(maxContactsPerParticipantField)
			.withValidator(new StringToNumberValidator("Must be a positive number", true))
			.bind(EventGenerationConfig::getMaxContactsPerParticipant, EventGenerationConfig::setMaxContactsPerParticipant);
		eventOptionsSecondLineLayout.addComponent(maxContactsPerParticipantField);

		TextField percentageOfCasesField = new TextField();
		percentageOfCasesField.setCaption(I18nProperties.getCaption(Captions.devModeEventCasePercentage));
		eventGeneratorConfigBinder.forField(percentageOfCasesField)
			.withValidator(new StringToNumberValidator("Must be a positive number", true))
			.bind(EventGenerationConfig::getPercentageOfCases, EventGenerationConfig::setPercentageOfCases);
		eventOptionsSecondLineLayout.addComponent(percentageOfCasesField);

		eventGeneratorLayout.addComponent(eventOptionsSecondLineLayout);
		eventGenerationConfig.setRegion(regions.get(0));
		List<DistrictReferenceDto> allActiveDistrictsByRegion =
			FacadeProvider.getDistrictFacade().getAllActiveByRegion(eventGenerationConfig.getRegion().getUuid());
		if (!allActiveDistrictsByRegion.isEmpty()) {
			eventGenerationConfig.setDistrict(allActiveDistrictsByRegion.get(0));
		}
		eventGeneratorConfigBinder.setBean(eventGenerationConfig);

		return eventGeneratorLayout;
	}

	private VerticalLayout createSamplesGeneratorLayout() {
		VerticalLayout sampleGeneratorLayout = new VerticalLayout();
		sampleGeneratorLayout.setMargin(false);
		sampleGeneratorLayout.setSpacing(false);

		Label heading = new Label(I18nProperties.getCaption(Captions.devModeGenerateSamples));
		CssStyles.style(heading, CssStyles.H2);
		sampleGeneratorLayout.addComponent(heading);

		HorizontalLayout sampleOptionsFirstLineLayout = new HorizontalLayout();

		TextField sampleCountField = new TextField();
		sampleCountField.setCaption(I18nProperties.getCaption(Captions.devModeSampleCount));
		sampleGeneratorConfigBinder.forField(sampleCountField)
			.withValidator(new StringToNumberValidator("Must be a positive number", true))
			.bind(SampleGenerationConfig::getEntityCount, SampleGenerationConfig::setEntityCount);
		sampleOptionsFirstLineLayout.addComponent(sampleCountField);

		DateField startDateField = new DateField();
		startDateField.setCaption(I18nProperties.getCaption(Captions.devModeSampleStartDate));
		startDateField.setDateFormat(DateFormatHelper.getDateFormatPattern());
		startDateField.setLenient(true);
		sampleGeneratorConfigBinder.bind(startDateField, SampleGenerationConfig::getStartDate, SampleGenerationConfig::setStartDate);
		sampleOptionsFirstLineLayout.addComponent(startDateField);

		DateField endDateField = new DateField();
		endDateField.setCaption(I18nProperties.getCaption(Captions.devModeSampleEndDate));
		endDateField.setDateFormat(DateFormatHelper.getDateFormatPattern());
		endDateField.setLenient(true);
		sampleGeneratorConfigBinder.bind(endDateField, SampleGenerationConfig::getEndDate, SampleGenerationConfig::setEndDate);
		sampleOptionsFirstLineLayout.addComponent(endDateField);

		ComboBox<Disease> diseaseField = new ComboBox<>(null, FacadeProvider.getDiseaseConfigurationFacade().getAllDiseases(true, true, true));
		diseaseField.setCaption(I18nProperties.getCaption(Captions.devModeSampleDisease));
		sampleGeneratorConfigBinder.bind(diseaseField, SampleGenerationConfig::getDisease, SampleGenerationConfig::setDisease);
		diseaseField.setRequiredIndicatorVisible(true);
		sampleOptionsFirstLineLayout.addComponent(diseaseField);

		List<RegionReferenceDto> regions = FacadeProvider.getRegionFacade().getAllActiveByServerCountry();
		ComboBox<RegionReferenceDto> regionField = new ComboBox<>(null, regions);
		regionField.setCaption(I18nProperties.getCaption(Captions.devModeSampleRegion));
		sampleGeneratorConfigBinder.bind(regionField, SampleGenerationConfig::getRegion, SampleGenerationConfig::setRegion);
		sampleOptionsFirstLineLayout.addComponent(regionField);

		ComboBox<DistrictReferenceDto> districtField = new ComboBox<>();
		districtField.setCaption(I18nProperties.getCaption(Captions.devModeSampleDistrict));
		sampleGeneratorConfigBinder.bind(districtField, SampleGenerationConfig::getDistrict, SampleGenerationConfig::setDistrict);
		sampleOptionsFirstLineLayout.addComponent(districtField);

		regionField.addValueChangeListener(event -> {
			RegionReferenceDto region = event.getValue();
			if (region != null) {
				districtField.setItems(FacadeProvider.getDistrictFacade().getAllActiveByRegion(region.getUuid()));
			} else {
				districtField.setItems(new ArrayList<DistrictReferenceDto>());
			}
		});

		Button generateButton = ButtonHelper.createButton(
			I18nProperties.getCaption(Captions.devModeGenerateSamples),
			e -> sampleGenerator.generate(sampleGeneratorConfigBinder),
			CssStyles.FORCE_CAPTION);
		sampleOptionsFirstLineLayout.addComponent(generateButton);

		sampleGeneratorLayout.addComponent(sampleOptionsFirstLineLayout);

		HorizontalLayout sampleOptionsSecondLineLayout = new HorizontalLayout();

		ComboBox<SampleMaterial> sampleMaterial = new ComboBox(null, Arrays.asList(SampleMaterial.values()));
		sampleMaterial.setCaption(I18nProperties.getCaption(Captions.devModeSampleMaterial));
		sampleGeneratorConfigBinder.bind(sampleMaterial, SampleGenerationConfig::getSampleMaterial, SampleGenerationConfig::setSampleMaterial);
		sampleMaterial.setRequiredIndicatorVisible(true);
		sampleOptionsSecondLineLayout.addComponent(sampleMaterial);

		ComboBox<FacilityReferenceDto> laboratory = new ComboBox(null, FacadeProvider.getFacilityFacade().getAllActiveLaboratories(true));
		laboratory.setCaption(I18nProperties.getCaption(Captions.devModeSampleLaboratory));
		sampleGeneratorConfigBinder.bind(laboratory, SampleGenerationConfig::getLaboratory, SampleGenerationConfig::setLaboratory);
		laboratory.setRequiredIndicatorVisible(true);
		sampleOptionsSecondLineLayout.addComponent(laboratory);

		sampleGeneratorLayout.addComponent(sampleOptionsSecondLineLayout);

		HorizontalLayout sampleOptionsThirdLineLayout = new HorizontalLayout();

		CheckBox externalLabTesting = new CheckBox(I18nProperties.getCaption(Captions.devModeSampleExternalLabTesting));
		sampleGeneratorConfigBinder.bind(
			externalLabTesting,
			SampleGenerationConfig::isExternalLabOrInternalInHouseTesting,
			SampleGenerationConfig::setExternalLabOrInternalInHouseTesting);
		externalLabTesting.setValue(true);
		sampleOptionsThirdLineLayout.addComponent(externalLabTesting);

		CheckBox requestPathogenTestsToBePerformed = new CheckBox(I18nProperties.getCaption(Captions.devModeSamplePathogenTestsToBePerformed));
		sampleGeneratorConfigBinder.bind(
			requestPathogenTestsToBePerformed,
			SampleGenerationConfig::isRequestPathogenTestsToBePerformed,
			SampleGenerationConfig::setRequestPathogenTestsToBePerformed);
		sampleOptionsThirdLineLayout.addComponent(requestPathogenTestsToBePerformed);

		CheckBox requestAdditionalTestsToBePerformed = new CheckBox(I18nProperties.getCaption(Captions.devModeSampleAdditionalTestsToBePerformed));
		sampleGeneratorConfigBinder.bind(
			requestAdditionalTestsToBePerformed,
			SampleGenerationConfig::isRequestAdditionalTestsToBePerformed,
			SampleGenerationConfig::setRequestAdditionalTestsToBePerformed);
		sampleOptionsThirdLineLayout.addComponent(requestAdditionalTestsToBePerformed);

		CheckBox sendDispatch = new CheckBox(I18nProperties.getCaption(Captions.devModeSampleSendDispatch));
		sampleGeneratorConfigBinder.bind(sendDispatch, SampleGenerationConfig::isSendDispatch, SampleGenerationConfig::setSendDispatch);
		sampleOptionsThirdLineLayout.addComponent(sendDispatch);

		CheckBox received = new CheckBox(I18nProperties.getCaption(Captions.devModeSampleReceived));
		sampleGeneratorConfigBinder.bind(received, SampleGenerationConfig::isReceived, SampleGenerationConfig::setReceived);
		sampleOptionsThirdLineLayout.addComponent(received);

		externalLabTesting.addValueChangeListener(event -> {
			if (externalLabTesting.getValue()) {
				requestPathogenTestsToBePerformed.setVisible(true);
				sendDispatch.setVisible(true);
				received.setVisible(true);
				laboratory.setVisible(true);
				sampleGenerationConfig.setSamplePurpose(SamplePurpose.EXTERNAL);
			} else {
				requestPathogenTestsToBePerformed.setVisible(false);
				sendDispatch.setVisible(false);
				received.setVisible(false);
				laboratory.setVisible(false);
				sampleGenerationConfig.setSamplePurpose(SamplePurpose.INTERNAL);
			}
		});

		sampleGeneratorLayout.addComponent(sampleOptionsThirdLineLayout);

		if (!regions.isEmpty()) {
			sampleGenerationConfig.setRegion(regions.get(0));
		}
		List<DistrictReferenceDto> allActiveDistrictsByRegion =
			FacadeProvider.getDistrictFacade().getAllActiveByRegion(sampleGenerationConfig.getRegion().getUuid());
		if (!allActiveDistrictsByRegion.isEmpty()) {
			sampleGenerationConfig.setDistrict(allActiveDistrictsByRegion.get(0));
		}
		sampleGeneratorConfigBinder.setBean(sampleGenerationConfig);

		return sampleGeneratorLayout;
	}

	@Override
	public void enter(ViewChangeEvent event) {
		super.enter(event);
	}
}
