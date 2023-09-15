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

import static java.util.Objects.nonNull;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
import com.vaadin.ui.Notification;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.EntityDto;
import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.VisitOrigin;
import de.symeda.sormas.api.caze.CaseCriteria;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.caze.CaseOrigin;
import de.symeda.sormas.api.caze.CaseReferenceDto;
import de.symeda.sormas.api.contact.ContactDto;
import de.symeda.sormas.api.contact.ContactLogic;
import de.symeda.sormas.api.contact.FollowUpStatus;
import de.symeda.sormas.api.contact.QuarantineType;
import de.symeda.sormas.api.event.EventDto;
import de.symeda.sormas.api.event.EventParticipantDto;
import de.symeda.sormas.api.event.EventStatus;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.infrastructure.district.DistrictCriteria;
import de.symeda.sormas.api.infrastructure.district.DistrictDto;
import de.symeda.sormas.api.infrastructure.district.DistrictIndexDto;
import de.symeda.sormas.api.infrastructure.district.DistrictReferenceDto;
import de.symeda.sormas.api.infrastructure.facility.FacilityCriteria;
import de.symeda.sormas.api.infrastructure.facility.FacilityDto;
import de.symeda.sormas.api.infrastructure.facility.FacilityIndexDto;
import de.symeda.sormas.api.infrastructure.facility.FacilityReferenceDto;
import de.symeda.sormas.api.infrastructure.region.RegionReferenceDto;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.person.PresentCondition;
import de.symeda.sormas.api.person.Sex;
import de.symeda.sormas.api.sample.AdditionalTestDto;
import de.symeda.sormas.api.sample.AdditionalTestType;
import de.symeda.sormas.api.sample.PathogenTestType;
import de.symeda.sormas.api.sample.SampleDto;
import de.symeda.sormas.api.sample.SampleMaterial;
import de.symeda.sormas.api.sample.SamplePurpose;
import de.symeda.sormas.api.sample.SimpleTestResultType;
import de.symeda.sormas.api.sample.SpecimenCondition;
import de.symeda.sormas.api.user.UserReferenceDto;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.api.utils.SortProperty;
import de.symeda.sormas.api.utils.UtilDate;
import de.symeda.sormas.api.utils.ValidationRuntimeException;
import de.symeda.sormas.api.utils.fieldvisibility.FieldVisibilityCheckers;
import de.symeda.sormas.api.visit.VisitDto;
import de.symeda.sormas.api.visit.VisitLogic;
import de.symeda.sormas.api.visit.VisitStatus;
import de.symeda.sormas.ui.UserProvider;
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

	private final transient Logger logger = LoggerFactory.getLogger(getClass());

	private static Random randomGenerator;

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

	private FieldVisibilityCheckers fieldVisibilityCheckers;

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
		regionField.setItemCaptionGenerator(item -> item.buildCaption());
		regionField.setCaption(I18nProperties.getCaption(Captions.devModeCaseRegion));
		caseGeneratorConfigBinder.bind(regionField, CaseGenerationConfig::getRegion, CaseGenerationConfig::setRegion);
		caseOptionsLayout.addComponent(regionField);

		ComboBox<DistrictReferenceDto> districtField = new ComboBox<DistrictReferenceDto>();
		districtField.setCaption(I18nProperties.getCaption(Captions.devModeCaseDistrict));
		districtField.setItemCaptionGenerator(item -> item.buildCaption());
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

		Button generateButton = ButtonHelper.createButton(Captions.devModeGenerateCases, e -> generateCases(), CssStyles.FORCE_CAPTION);
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
		regionField.setItemCaptionGenerator(item -> item.buildCaption());
		contactGeneratorConfigBinder.bind(regionField, ContactGenerationConfig::getRegion, ContactGenerationConfig::setRegion);
		contactOptionsFirstLineLayout.addComponent(regionField);

		ComboBox<DistrictReferenceDto> districtField = new ComboBox<DistrictReferenceDto>();
		districtField.setItemCaptionGenerator(item -> item.buildCaption());
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

		Button generateButton = ButtonHelper.createButton(Captions.devModeGenerateContacts, e -> generateContacts(), CssStyles.FORCE_CAPTION);
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
		regionField.setItemCaptionGenerator(item -> item.buildCaption());
		regionField.setCaption(I18nProperties.getCaption(Captions.devModeEventRegion));
		eventGeneratorConfigBinder.bind(regionField, EventGenerationConfig::getRegion, EventGenerationConfig::setRegion);
		eventOptionsFirstLineLayout.addComponent(regionField);

		ComboBox<DistrictReferenceDto> districtField = new ComboBox<DistrictReferenceDto>();
		districtField.setCaption(I18nProperties.getCaption(Captions.devModeEventDistrict));
		districtField.setItemCaptionGenerator(item -> item.buildCaption());
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

		Button generateButton =
			ButtonHelper.createButton(I18nProperties.getCaption(Captions.devModeGenerateEvents), e -> generateEvents(), CssStyles.FORCE_CAPTION);
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
		regionField.setItemCaptionGenerator(item -> item.buildCaption());
		sampleGeneratorConfigBinder.bind(regionField, SampleGenerationConfig::getRegion, SampleGenerationConfig::setRegion);
		sampleOptionsFirstLineLayout.addComponent(regionField);

		ComboBox<DistrictReferenceDto> districtField = new ComboBox<>();
		districtField.setCaption(I18nProperties.getCaption(Captions.devModeSampleDistrict));
		districtField.setItemCaptionGenerator(item -> item.buildCaption());
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

		Button generateButton =
			ButtonHelper.createButton(I18nProperties.getCaption(Captions.devModeGenerateSamples), e -> generateSamples(), CssStyles.FORCE_CAPTION);
		sampleOptionsFirstLineLayout.addComponent(generateButton);

		sampleGeneratorLayout.addComponent(sampleOptionsFirstLineLayout);

		HorizontalLayout sampleOptionsSecondLineLayout = new HorizontalLayout();

		ComboBox<SampleMaterial> sampleMaterial = new ComboBox(null, Arrays.asList(SampleMaterial.values()));
		sampleMaterial.setCaption(I18nProperties.getCaption(Captions.devModeSampleMaterial));
		sampleGeneratorConfigBinder.bind(sampleMaterial, SampleGenerationConfig::getSampleMaterial, SampleGenerationConfig::setSampleMaterial);
		sampleMaterial.setRequiredIndicatorVisible(true);
		sampleOptionsSecondLineLayout.addComponent(sampleMaterial);

		ComboBox<FacilityReferenceDto> laboratory = new ComboBox(null, FacadeProvider.getFacilityFacade().getAllActiveLaboratories(true));
		laboratory.setItemCaptionGenerator(item -> item.buildCaption());
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

	private final String[] maleFirstNames = new String[] {
		"Nelson",
		"Malik",
		"Thato",
		"Omar",
		"Dion",
		"Darius",
		"Bandile",
		"Demarco" };
	private final String[] femaleFirstNames = new String[] {
		"Ayana",
		"Shaka",
		"Shaniqua",
		"Charlize",
		"Zari",
		"Jayla",
		"Aisha",
		"Iminathi" };
	private final String[] lastNames = new String[] {
		"Ajanlekoko",
		"Omiata",
		"Apeloko",
		"Adisa",
		"Abioye",
		"Chipo",
		"Baako",
		"Akua",
		"Ekua",
		"Katlego",
		"Furaha",
		"Chuks",
		"Babak",
		"Tinibu",
		"Okar",
		"Egwu" };
	private final String[] eventTitles = new String[] {
		"Wedding",
		"Party",
		"Funeral",
		"Concert",
		"Fair",
		"Rallye",
		"Demonstration",
		"Football Match",
		"Tournament",
		"Festival",
		"Carnival" };
	private final String[] sampleComments = new String[] {
		"Very expensive test",
		"Urgent need to have the results",
		"This is a repeated test",
		"Repeated test after 1 day",
		"Repeated test after 1 week",
		"-" };
	private final String[] sampleShipmentDetails = new String[] {
		"Dispatch is required within 1 week",
		"Dispatch is required within 2 weeks",
		"Dispatch is required within 1 months",
		"Dispatch is required by the end of a day",
		"Dispatch is very urgent",
		"-" };
	private final String[] otherPerformedTestsAndResultsSample = new String[] {
		"Blood donation has been performed 1 week ago",
		"Blood donation has been performed 2 weeks ago",
		"Haemoglobin in urine was positive 1 week ago",
		"Protein is urine was negative 1 month ago",
		"Red blood cells in urine was indeterminate 2 weeks ago",
		"-" };

	private static void initializeRandomGenerator() {
		if (useManualSeed) {
			randomGenerator = new Random(manualSeed);
		} else {
			randomGenerator = new Random();
		}
	}

	private static Random random() {
		return randomGenerator;
	}

	private static boolean randomPercent(int p) {
		return random().nextInt(100) <= p;
	}

	private static int randomInt(int min, int max) {
		if (max <= min) {
			return min;
		}
		return min + random().nextInt(max - min);
	}

	private static <T> T random(List<T> list) {
		return list.get(random().nextInt(list.size()));
	}

	private static <T> T random(T[] a) {
		return a[random().nextInt(a.length)];
	}

	private static Date randomDate(LocalDateTime referenceDateTime) {
		return Date.from(referenceDateTime.plusMinutes(random().nextInt(60 * 24 * 5)).atZone(ZoneId.systemDefault()).toInstant());
	}

	private void fillEntity(EntityDto entity, LocalDateTime referenceDateTime) {

		try {
			Class<? extends EntityDto> entityClass = entity.getClass();
			List<Method> setters = setters(entityClass);
			for (Method setter : setters) {
				String propertyId = setter.getName().substring(3, 4).toLowerCase() + setter.getName().substring(4);
				// leave some empty/default
				if (randomPercent(40) || !fieldVisibilityCheckers.isVisible(entityClass, propertyId)) {
					continue;
				}
				Class<?> parameterType = setter.getParameterTypes()[0];
				// doesn't make sense
				//				if (parameterType.isAssignableFrom(String.class)) {
				//					setter.invoke(entity, words[random.nextInt(words.length)]);
				//				}
				//				else
				if (parameterType.isAssignableFrom(Date.class)) {
					setter.invoke(entity, randomDate(referenceDateTime));
				} else if (parameterType.isEnum()) {
					Object[] enumConstants;
					// Only use active primary diseases

					enumConstants = Arrays.stream(getEnumConstants(parameterType))
						.filter(value -> fieldVisibilityCheckers.isVisible(parameterType, value.toString()))
						.toArray();
					// Generate more living persons
					if (parameterType == PresentCondition.class && randomPercent(50)) {
						setter.invoke(entity, PresentCondition.ALIVE);
					} else {
						setter.invoke(entity, random(enumConstants));
					}
				} else if (EntityDto.class.isAssignableFrom(parameterType)) {
					getter(setter).ifPresent(g -> {
						Object subEntity;
						try {
							subEntity = g.invoke(entity);
							if (subEntity instanceof EntityDto) {
								fillEntity((EntityDto) subEntity, referenceDateTime);
							}
						} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
							throw new RuntimeException(e.getMessage(), e);
						}
					});
				}
			}
			if (entity.getClass() == CaseDataDto.class) {
				if (((CaseDataDto) entity).getQuarantineTo() != null
					&& ((CaseDataDto) entity).getQuarantineFrom() != null
					&& ((CaseDataDto) entity).getQuarantineTo().before(((CaseDataDto) entity).getQuarantineFrom())) {
					Date quarantineTo = ((CaseDataDto) entity).getQuarantineTo();
					((CaseDataDto) entity).setQuarantineTo(((CaseDataDto) entity).getQuarantineFrom());
					((CaseDataDto) entity).setQuarantineFrom(quarantineTo);
				}

				if (((CaseDataDto) entity).getProhibitionToWorkFrom() != null
					&& ((CaseDataDto) entity).getProhibitionToWorkUntil() != null
					&& ((CaseDataDto) entity).getProhibitionToWorkUntil().before(((CaseDataDto) entity).getProhibitionToWorkFrom())) {
					Date prohibitionToWorkUntil = ((CaseDataDto) entity).getProhibitionToWorkUntil();
					((CaseDataDto) entity).setProhibitionToWorkUntil(((CaseDataDto) entity).getProhibitionToWorkFrom());
					((CaseDataDto) entity).setProhibitionToWorkFrom(prohibitionToWorkUntil);
				}
			}
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException e) {
			throw new RuntimeException(e);
		}
	}

	Object[] diseaseEnumConstants;

	private Object[] getEnumConstants(Class<?> parameterType) {
		if (parameterType == Disease.class) {
			if (diseaseEnumConstants == null) {
				diseaseEnumConstants = FacadeProvider.getDiseaseConfigurationFacade().getAllDiseases(true, true, true).toArray();
			}
			return diseaseEnumConstants;
		} else {
			return parameterType.getEnumConstants();
		}
	}

	private Map<Class<? extends EntityDto>, List<Method>> setters = new HashMap<>();
	private Map<Method, Optional<Method>> getters = new HashMap<>();

	private List<Method> setters(Class<? extends EntityDto> entityClass) {

		return setters.computeIfAbsent(
			entityClass,
			c -> Arrays.stream(c.getDeclaredMethods())
				.filter(method -> method.getName().startsWith("set") && method.getParameterTypes().length == 1)
				.collect(Collectors.toList()));
	}

	private Optional<Method> getter(Method setter) throws NoSuchMethodException {

		return getters.computeIfAbsent(setter, s -> {
			try {
				return Optional.of(s.getDeclaringClass().getDeclaredMethod(s.getName().replaceFirst("set", "get")));
			} catch (NoSuchMethodException | SecurityException e) {
				return Optional.empty();
			}
		});
	}

	private void generateCases() {
		CaseGenerationConfig config = caseGeneratorConfigBinder.getBean();
		StringBuilder errorMessage = new StringBuilder();
		boolean valid = true;
		if (config.getEntityCountAsNumber() <= 0) {
			errorMessage.append("You must set a valid value for field 'Number of generated cases' in 'Generate Cases'").append("<br>");
			valid = false;
		}
		if (config.getStartDate() == null) {
			errorMessage.append("You must set a valid value for field 'Earliest Case start date' in 'Generate Cases'").append("<br>");
			valid = false;
		}

		if (config.getEndDate() == null) {
			errorMessage.append("You must set a valid value for field 'Latest Case start date' in 'Generate Cases'").append("<br>");
			valid = false;
		}

		if (config.getDistrict() == null) {
			errorMessage.append("You must set a valid value for field 'District of the cases' in 'Generate Cases'").append("<br>");
			valid = false;
		}

		if (valid) {
			generateCases(config);
		} else {
			throw new ValidationRuntimeException(errorMessage.toString());
		}
	}

	private void generateCases(CaseGenerationConfig config) {
		initializeRandomGenerator();

		List<Disease> diseases = FacadeProvider.getDiseaseConfigurationFacade().getAllDiseases(true, true, true);
		float baseOffset = random().nextFloat();
		int daysBetween = (int) ChronoUnit.DAYS.between(config.getStartDate(), config.getEndDate());

		FacilityCriteria facilityCriteria = new FacilityCriteria();
		facilityCriteria.region(config.getRegion());
		facilityCriteria.district(config.getDistrict());

		// just load some health facilities. Alphabetical order is not random, but the best we can get
		List<FacilityIndexDto> healthFacilities = FacadeProvider.getFacilityFacade()
			.getIndexList(facilityCriteria, 0, Math.min(config.getEntityCountAsNumber() * 2, 300), Arrays.asList(new SortProperty(FacilityDto.NAME)));

		// Filter list, so that only health facilities meant for accomodation are selected
		healthFacilities.removeIf(el -> (!el.getType().isAccommodation()));

		long dt = System.nanoTime();

		for (int i = 0; i < config.getEntityCountAsNumber(); i++) {
			Disease disease = config.getDisease();
			if (disease == null) {
				disease = random(diseases);
			}

			fieldVisibilityCheckers =
				FieldVisibilityCheckers.withDisease(disease).andWithCountry(FacadeProvider.getConfigFacade().getCountryLocale());

			LocalDateTime referenceDateTime =
				getReferenceDateTime(i, config.getEntityCountAsNumber(), baseOffset, disease, config.getStartDate(), daysBetween);

			// person
			PersonDto person = PersonDto.build();
			fillEntity(person, referenceDateTime);
			person.setSymptomJournalStatus(null);
			setPersonName(person);

			CaseDataDto caze = CaseDataDto.build(person.toReference(), disease);
			fillEntity(caze, referenceDateTime);
			caze.setDisease(disease); // reset
			if (caze.getDisease() == Disease.OTHER) {
				caze.setDiseaseDetails("RD " + (random().nextInt(20) + 1));
			}

			if (!QuarantineType.isQuarantineInEffect(caze.getQuarantine())) {
				caze.setQuarantineFrom(null);
				caze.setQuarantineTo(null);
				caze.setQuarantineExtended(false);
				caze.setQuarantineReduced(false);
			}

			// description
			caze.setAdditionalDetails("Case generated using DevMode on " + LocalDate.now());

			// report
			UserReferenceDto userReference = UserProvider.getCurrent().getUserReference();
			caze.setReportingUser(userReference);
			caze.setReportDate(Date.from(referenceDateTime.atZone(ZoneId.systemDefault()).toInstant()));

			// region & facility
			if (healthFacilities.isEmpty() || randomPercent(20)) {
				FacilityReferenceDto noFacilityRef = FacadeProvider.getFacilityFacade().getByUuid(FacilityDto.NONE_FACILITY_UUID).toReference();
				caze.setCaseOrigin(CaseOrigin.IN_COUNTRY);
				caze.setHealthFacility(noFacilityRef);
				caze.setFacilityType(null);
				caze.setResponsibleRegion(config.getRegion());
				caze.setResponsibleDistrict(config.getDistrict());
			} else {
				FacilityIndexDto healthFacility = random(healthFacilities);
				caze.setCaseOrigin(CaseOrigin.IN_COUNTRY);
				caze.setResponsibleRegion(healthFacility.getRegion());
				caze.setResponsibleDistrict(healthFacility.getDistrict());
				caze.setResponsibleCommunity(healthFacility.getCommunity());
				caze.setHealthFacility(healthFacility.toReference());
				caze.setFacilityType(healthFacility.getType());
				caze.setReportLat(healthFacility.getLatitude());
				caze.setReportLon(healthFacility.getLongitude());
			}

			FacadeProvider.getPersonFacade().save(person);
			FacadeProvider.getCaseFacade().save(caze);
		}

		dt = System.nanoTime() - dt;
		long perCase = dt / config.getEntityCountAsNumber();
		String msg = String
			.format("Generating %,d cases took %,d  ms (%,d ms per case)", config.getEntityCountAsNumber(), dt / 1_000_000, perCase / 1_000_000);
		logger.info(msg);
		Notification.show("", msg, Notification.Type.TRAY_NOTIFICATION);
	}

	private void generateSamples() {
		SampleGenerationConfig sampleGenerationConfig = sampleGeneratorConfigBinder.getBean();
		boolean valid = true;
		StringBuilder errorMessage = new StringBuilder();

		if (sampleGenerationConfig.getEntityCountAsNumber() <= 0) {
			errorMessage.append("You must set a valid value for field 'Number of generated samples' in 'Generate Samples'").append("<br>");
			valid = false;
		}
		if (sampleGenerationConfig.getStartDate() == null) {
			errorMessage.append("You must set a valid value for field 'Sample collected start date' in 'Generate Samples'").append("<br>");
			valid = false;
		}

		if (sampleGenerationConfig.getEndDate() == null) {
			errorMessage.append("You must set a valid value for field 'Sample collected end date' in 'Generate Samples'").append("<br>");
			valid = false;
		}

		if (sampleGenerationConfig.getSampleMaterial() == null) {
			errorMessage.append("You must set a valid value for field 'Type of the Sample' in 'Generate Samples'").append("<br>");
			valid = false;
		}

		if (valid) {
			generateSamples(sampleGenerationConfig);
		} else {
			throw new ValidationRuntimeException(errorMessage.toString());
		}
	}

	private void generateSamples(SampleGenerationConfig sampleGenerationConfig) {
		initializeRandomGenerator();

		float baseOffset = random().nextFloat();
		int daysBetween = (int) ChronoUnit.DAYS.between(sampleGenerationConfig.getStartDate(), sampleGenerationConfig.getEndDate());

		FacilityCriteria facilityCriteria = new FacilityCriteria();
		facilityCriteria.region(sampleGenerationConfig.getRegion());
		facilityCriteria.district(sampleGenerationConfig.getDistrict());

		long dt = System.nanoTime();

		UserReferenceDto user = UserProvider.getCurrent().getUserReference();

		List<CaseReferenceDto> cases = FacadeProvider.getCaseFacade()
			.getRandomCaseReferences(
				new CaseCriteria().region(sampleGenerationConfig.getRegion())
					.district(sampleGenerationConfig.getDistrict())
					.disease(sampleGenerationConfig.getDisease()),
				sampleGenerationConfig.getEntityCountAsNumber() * 2,
				random());

		if (nonNull(cases)) {
			for (int i = 0; i < sampleGenerationConfig.getEntityCountAsNumber(); i++) {

				CaseReferenceDto caseReference = random(cases);

				List<Disease> diseases = FacadeProvider.getDiseaseConfigurationFacade().getAllDiseases(true, true, true);
				Disease disease = sampleGenerationConfig.getDisease();
				if (disease == null) {
					disease = random(diseases);
					sampleGenerationConfig.setDisease(disease);
				}

				LocalDateTime referenceDateTime = getReferenceDateTime(
					i,
					sampleGenerationConfig.getEntityCountAsNumber(),
					baseOffset,
					sampleGenerationConfig.getDisease(),
					sampleGenerationConfig.getStartDate(),
					daysBetween);

				SampleDto sample = SampleDto.build(user, caseReference);

				sample.setSamplePurpose(sampleGenerationConfig.getSamplePurpose());

				Date date = Date.from(referenceDateTime.toLocalDate().atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
				sample.setSampleDateTime(date);

				sample.setSampleMaterial(sampleGenerationConfig.getSampleMaterial());

				sample.setFieldSampleID(UUID.randomUUID().toString());

				sample.setComment(random(sampleComments));

				sample.setLab(sampleGenerationConfig.getLaboratory());

				if (sampleGenerationConfig.isRequestPathogenTestsToBePerformed()) {
					Set pathogenTestTypes = new HashSet<PathogenTestType>();
					int until = randomInt(1, PathogenTestType.values().length);
					for (int j = 0; j < until; j++) {
						pathogenTestTypes.add(PathogenTestType.values()[j]);
					}
					sample.setPathogenTestingRequested(true);
					sample.setRequestedPathogenTests(pathogenTestTypes);
				}

				if (sampleGenerationConfig.isRequestAdditionalTestsToBePerformed()) {
					Set additionalTestTypes = new HashSet<AdditionalTestType>();
					int until = randomInt(1, AdditionalTestType.values().length);
					for (int j = 0; j < until; j++) {
						additionalTestTypes.add(AdditionalTestType.values()[j]);
					}
					sample.setAdditionalTestingRequested(true);
					sample.setRequestedAdditionalTests(additionalTestTypes);
				}

				if (sampleGenerationConfig.isSendDispatch()) {
					sample.setShipped(true);
					sample.setShipmentDate(date);
					sample.setShipmentDetails(random(sampleShipmentDetails));
				}

				if (sampleGenerationConfig.isReceived()) {
					sample.setReceived(true);
					sample.setReceivedDate(date);

					sample.setSpecimenCondition(random(SpecimenCondition.values()));
				}

				SampleDto sampleDto = FacadeProvider.getSampleFacade().saveSample(sample);

				if (sampleGenerationConfig.isRequestAdditionalTestsToBePerformed()) {
					createAdditionalTest(sampleDto, date);
				}

			}

			dt = System.nanoTime() - dt;
			long perSample = dt / sampleGenerationConfig.getEntityCountAsNumber();
			String msg = String.format(
				"Generating %d samples took %.2f  s (%.1f ms per sample)",
				sampleGenerationConfig.getEntityCountAsNumber(),
				(double) dt / 1_000_000_000,
				(double) perSample / 1_000_000);
			logger.info(msg);
			Notification.show("", msg, Notification.Type.TRAY_NOTIFICATION);
		} else {
			String msg = "No Sample has been generated because cases is null ";
			logger.info(msg);
			Notification.show("", msg, Notification.Type.TRAY_NOTIFICATION);
		}
	}

	private void createAdditionalTest(SampleDto sample, Date date) {

		AdditionalTestDto additionalTestDto = new AdditionalTestDto();
		additionalTestDto.setUuid(UUID.randomUUID().toString());
		additionalTestDto.setTestDateTime(date);
		additionalTestDto.setHaemoglobinuria(random(SimpleTestResultType.values()));
		additionalTestDto.setProteinuria(random(SimpleTestResultType.values()));
		additionalTestDto.setHematuria(random(SimpleTestResultType.values()));

		additionalTestDto.setArterialVenousGasPH(new Random().nextFloat());
		additionalTestDto.setArterialVenousGasPco2(new Random().nextFloat());
		additionalTestDto.setArterialVenousGasPao2(new Random().nextFloat());
		additionalTestDto.setArterialVenousGasHco3(new Random().nextFloat());
		additionalTestDto.setGasOxygenTherapy(new Random().nextFloat());

		additionalTestDto.setAltSgpt(new Random().nextFloat());
		additionalTestDto.setTotalBilirubin(new Random().nextFloat());
		additionalTestDto.setAstSgot(new Random().nextFloat());
		additionalTestDto.setConjBilirubin(new Random().nextFloat());
		additionalTestDto.setCreatinine(new Random().nextFloat());
		additionalTestDto.setWbcCount(new Random().nextFloat());
		additionalTestDto.setPotassium(new Random().nextFloat());
		additionalTestDto.setPlatelets(new Random().nextFloat());
		additionalTestDto.setUrea(new Random().nextFloat());
		additionalTestDto.setProthrombinTime(new Random().nextFloat());
		additionalTestDto.setHaemoglobin(new Random().nextFloat());

		additionalTestDto.setOtherTestResults(random(otherPerformedTestsAndResultsSample));

		additionalTestDto.setSample(sample.toReference());

		FacadeProvider.getAdditionalTestFacade().saveAdditionalTest(additionalTestDto);
	}

	private void generateContacts() {
		ContactGenerationConfig contactGenerationConfig = contactGeneratorConfigBinder.getBean();
		boolean valid = true;
		StringBuilder errorMessage = new StringBuilder();

		if (contactGenerationConfig.getEntityCountAsNumber() <= 0) {
			errorMessage.append("You must set a valid value for field 'Number of generated contacts' in 'Generate Contacts'").append("<br>");
			valid = false;
		}
		if (contactGenerationConfig.getStartDate() == null) {
			errorMessage.append("You must set a valid value for field 'Earliest Contact start date' in 'Generate Contacts'").append("<br>");
			valid = false;
		}

		if (contactGenerationConfig.getEndDate() == null) {
			errorMessage.append("You must set a valid value for field 'Latest Contact start date'  in 'Generate Contacts'").append("<br>");
			valid = false;
		}

		if (valid) {
			generateContacts(contactGenerationConfig);
		} else {
			throw new ValidationRuntimeException(errorMessage.toString());
		}
	}

	private void generateContacts(ContactGenerationConfig contactGenerationConfig) {
		initializeRandomGenerator();

		Disease disease = contactGenerationConfig.getDisease();
		List<Disease> diseases = disease == null ? FacadeProvider.getDiseaseConfigurationFacade().getAllDiseases(true, true, true) : null;

		if (disease == null) {
			disease = random(diseases);
			Notification.show("", "Automatically chosen disease: " + disease.getName(), Notification.Type.TRAY_NOTIFICATION);
		}
		List<String> personUuids = new ArrayList<>();
		List<CaseReferenceDto> cases = null;
		List<DistrictIndexDto> districts = contactGenerationConfig.getDistrict() == null
			? FacadeProvider.getDistrictFacade()
				.getIndexList(
					new DistrictCriteria().region(contactGenerationConfig.getRegion()),
					0,
					Math.min(contactGenerationConfig.getEntityCountAsNumber() * 2, 50),
					Arrays.asList(new SortProperty(DistrictDto.NAME)))
			: null;
		if (!contactGenerationConfig.isCreateWithoutSourceCases()) {
			cases = FacadeProvider.getCaseFacade()
				.getRandomCaseReferences(
					new CaseCriteria().region(contactGenerationConfig.getRegion()).district(contactGenerationConfig.getDistrict()).disease(disease),
					contactGenerationConfig.getEntityCountAsNumber() * 2,
					random());
			if (cases == null) {
				Notification.show("Error", I18nProperties.getString(Strings.messageMissingCases), Notification.Type.ERROR_MESSAGE);
				return;
			}
		}

		float baseOffset = random().nextFloat();
		int daysBetween = (int) ChronoUnit.DAYS.between(contactGenerationConfig.getStartDate(), contactGenerationConfig.getEndDate());

		long dt = System.nanoTime();

		for (int i = 0; i < contactGenerationConfig.getEntityCountAsNumber(); i++) {
			fieldVisibilityCheckers =
				FieldVisibilityCheckers.withDisease(disease).andWithCountry(FacadeProvider.getConfigFacade().getCountryLocale());

			LocalDateTime referenceDateTime = getReferenceDateTime(
				i,
				contactGenerationConfig.getEntityCountAsNumber(),
				baseOffset,
				disease,
				contactGenerationConfig.getStartDate(),
				daysBetween);

			PersonDto person;
			if (contactGenerationConfig.isCreateMultipleContactsPerPerson() && !personUuids.isEmpty() && randomPercent(25)) {
				String personUuid = random(personUuids);
				person = FacadeProvider.getPersonFacade().getByUuid(personUuid);
			} else {
				person = PersonDto.build();
				fillEntity(person, referenceDateTime);
				person.setSymptomJournalStatus(null);
				setPersonName(person);

				if (contactGenerationConfig.isCreateMultipleContactsPerPerson()) {
					personUuids.add(person.getUuid());
				}
			}

			CaseReferenceDto contactCase = null;
			if (!contactGenerationConfig.isCreateWithoutSourceCases()) {
				contactCase = random(cases);
			}

			ContactDto contact = ContactDto.build();
			contact.setPerson(person.toReference());
			fillEntity(contact, referenceDateTime);
			if (contactCase != null) {
				contact.setCaze(contactCase);
			}
			contact.setDisease(disease);
			if (contact.getDisease() == Disease.OTHER) {
				contact.setDiseaseDetails("RD " + (random().nextInt(20) + 1));
			}

			UserReferenceDto userReference = UserProvider.getCurrent().getUserReference();
			contact.setReportingUser(userReference);
			contact.setReportDateTime(Date.from(referenceDateTime.atZone(ZoneId.systemDefault()).toInstant()));

			if (districts != null) {
				DistrictIndexDto district = random(districts);
				contact.setRegion(district.getRegion());
				contact.setDistrict(district.toReference());
			} else {
				contact.setRegion(contactGenerationConfig.getRegion());
				contact.setDistrict(contactGenerationConfig.getDistrict());
			}

			if (contact.getLastContactDate() != null && contact.getLastContactDate().after(contact.getReportDateTime())) {
				contact.setLastContactDate(contact.getReportDateTime());
			}
			if (FollowUpStatus.CANCELED.equals(contact.getFollowUpStatus()) || FollowUpStatus.LOST.equals(contact.getFollowUpStatus())) {
				contact.setFollowUpComment("-");
			}

			// description
			contact.setDescription("Contact generated using DevMode on " + LocalDate.now());

			FacadeProvider.getPersonFacade().save(person);
			contact = FacadeProvider.getContactFacade().save(contact);

			if (FacadeProvider.getDiseaseConfigurationFacade().hasFollowUp(contact.getDisease())) {
				contact.setFollowUpStatus(random(FollowUpStatus.values()));
			} else {
				contact.setFollowUpStatus(FollowUpStatus.NO_FOLLOW_UP);
			}
			contact.setFollowUpUntil(contact.getFollowUpStatus() == FollowUpStatus.NO_FOLLOW_UP ? null : randomDate(referenceDateTime));

			// Create visits
			if (contactGenerationConfig.isCreateWithVisits()
				&& FacadeProvider.getDiseaseConfigurationFacade().hasFollowUp(contact.getDisease())
				&& FollowUpStatus.NO_FOLLOW_UP != contact.getFollowUpStatus()) {
				Date latestFollowUpDate = contact.getFollowUpUntil().before(new Date()) ? contact.getFollowUpUntil() : new Date();
				Date contactStartDate = ContactLogic.getStartDate(contact);
				int followUpCount = random().nextInt(DateHelper.getDaysBetween(contactStartDate, latestFollowUpDate) + 1);
				if (followUpCount > 0) {
					int[] followUpDays = random().ints(1, followUpCount + 1).distinct().limit(followUpCount).toArray();
					List<LocalDateTime> followUpDates = new ArrayList<>();
					for (int day : followUpDays) {
						followUpDates
							.add(UtilDate.toLocalDate(contactStartDate).atStartOfDay().plusDays(day - 1).plusMinutes(random().nextInt(60 * 24 + 1)));
					}

					for (LocalDateTime date : followUpDates) {
						VisitDto visit = VisitDto.build(contact.getPerson(), contact.getDisease(), VisitOrigin.USER);
						fillEntity(visit, date);
						visit.setVisitUser(userReference);
						visit.setVisitDateTime(UtilDate.from(date));
						visit.setDisease(contact.getDisease());
						if (visit.getVisitStatus() == null) {
							visit.setVisitStatus(VisitStatus.COOPERATIVE);
						}
						FacadeProvider.getVisitFacade()
							.saveVisit(
								visit,
								VisitLogic.getAllowedStartDate(ContactLogic.getStartDate(contact)),
								VisitLogic.getAllowedEndDate(ContactLogic.getEndDate(contact)));
					}
				}
			}
		}

		dt = System.nanoTime() - dt;
		long perContact = dt / contactGenerationConfig.getEntityCountAsNumber();
		String msg = String.format(
			"Generating %,d contacts took %,d  ms (%,d ms per contact)",
			contactGenerationConfig.getEntityCountAsNumber(),
			dt / 1_000_000,
			perContact / 1_000_000);
		logger.info(msg);
		Notification.show("", msg, Notification.Type.TRAY_NOTIFICATION);
	}

	private void generateEvents() {
		EventGenerationConfig eventGenerationConfig = eventGeneratorConfigBinder.getBean();
		boolean valid = true;
		StringBuilder errorMessage = new StringBuilder();

		if (eventGenerationConfig.getEntityCountAsNumber() <= 0) {
			errorMessage.append("You must set a valid value for field 'Number of generated events' in 'Generate Events'").append("<br>");
			valid = false;
		}
		if (eventGenerationConfig.getStartDate() == null) {
			errorMessage.append("You must set a valid value for field 'Earliest Event start date' in 'Generate Events'").append("<br>");
			valid = false;
		}

		if (eventGenerationConfig.getEndDate() == null) {
			errorMessage.append("You must set a valid value for field 'Latest Event start date' in 'Generate Events'").append("<br>");
			valid = false;
		}

		if (eventGenerationConfig.getDistrict() == null) {
			errorMessage.append("You must set a valid value for field 'District of the events' in 'Generate Events'").append("<br>");
			valid = false;
		}

		if (valid) {
			generateEvents(eventGenerationConfig);
		} else {
			throw new ValidationRuntimeException(errorMessage.toString());
		}
	}

	private void generateEvents(EventGenerationConfig eventGenerationConfig) {
		initializeRandomGenerator();

		int minParticipantsPerEvent = eventGenerationConfig.convetToNumber(eventGenerationConfig.getMinParticipantsPerEvent());
		int maxParticipantsPerEvent = eventGenerationConfig.convetToNumber(eventGenerationConfig.getMaxParticipantsPerEvent());
		int percentageOfCases = eventGenerationConfig.convetToNumber(eventGenerationConfig.getPercentageOfCases());
		int minContactsPerParticipant = eventGenerationConfig.convetToNumber(eventGenerationConfig.getMinContactsPerParticipant());
		int maxContactsPerParticipant = eventGenerationConfig.convetToNumber(eventGenerationConfig.getMaxContactsPerParticipant());

		int generatedParticipants = 0;
		int generatedCases = 0;
		int generatedContacts = 0;

		Disease disease = eventGenerationConfig.getDisease();

		float baseOffset = random().nextFloat();
		int daysBetween = (int) ChronoUnit.DAYS.between(eventGenerationConfig.getStartDate(), eventGenerationConfig.getEndDate());

		// this should be adjusted to be much more complex
		FacilityCriteria facilityCriteria = new FacilityCriteria();
		facilityCriteria.region(eventGenerationConfig.getRegion());
		facilityCriteria.district(eventGenerationConfig.getDistrict());
		List<FacilityIndexDto> healthFacilities =
			FacadeProvider.getFacilityFacade().getIndexList(facilityCriteria, 0, (int) (maxContactsPerParticipant * percentageOfCases / 100), null);

		// Filter list, so that only health facilities meant for accomodation are selected
		healthFacilities.removeIf(el -> (!el.getType().isAccommodation()));

		long dt = System.nanoTime();

		for (int i = 0; i < eventGenerationConfig.getEntityCountAsNumber(); i++) {
			LocalDateTime referenceDateTime;

			EventDto event = EventDto.build();

			// disease
			if (disease != null) {
				event.setDisease(disease); // reset
				if (event.getDisease() == Disease.OTHER) {
					event.setDiseaseDetails("RD " + (random().nextInt(20) + 1));
				}
				referenceDateTime = getReferenceDateTime(
					i,
					eventGenerationConfig.getEntityCountAsNumber(),
					baseOffset,
					disease,
					eventGenerationConfig.getStartDate(),
					daysBetween);
				fieldVisibilityCheckers =
					FieldVisibilityCheckers.withDisease(disease).andWithCountry(FacadeProvider.getConfigFacade().getCountryLocale());
			} else {
				referenceDateTime = getReferenceDateTime(
					i,
					eventGenerationConfig.getEntityCountAsNumber(),
					baseOffset,
					Disease.OTHER,
					eventGenerationConfig.getStartDate(),
					daysBetween);
				fieldVisibilityCheckers =
					FieldVisibilityCheckers.withDisease(Disease.OTHER).andWithCountry(FacadeProvider.getConfigFacade().getCountryLocale());
			}

			// title
			event.setEventTitle(random(eventTitles));

			// description
			event.setEventDesc("Event generated using DevMode on " + LocalDate.now());

			// report
			UserReferenceDto userReference = UserProvider.getCurrent().getUserReference();
			event.setReportingUser(userReference);
			event.setReportDateTime(Date.from(referenceDateTime.atZone(ZoneId.systemDefault()).toInstant()));

			// region & district
			event.getEventLocation().setRegion(eventGenerationConfig.getRegion());
			event.getEventLocation().setDistrict(eventGenerationConfig.getDistrict());

			// status
			event.setEventStatus(EventStatus.EVENT);

			FacadeProvider.getEventFacade().save(event);

			// EventParticipants
			int numParticipants = randomInt(minParticipantsPerEvent, maxParticipantsPerEvent);
			for (int j = 0; j < numParticipants; j++) {
				EventParticipantDto eventParticipant = EventParticipantDto.build(event.toReference(), UserProvider.getCurrent().getUserReference());
				// person
				// instead of creating new persons everytime, it would be nice if some persons came of the original database
				PersonDto person = PersonDto.build();
				fillEntity(person, referenceDateTime);
				person.setSymptomJournalStatus(null);
				setPersonName(person);
				FacadeProvider.getPersonFacade().save(person);
				eventParticipant.setPerson(person);
				eventParticipant.setInvolvementDescription("Participant");

				if (disease != null) {
					// generate cases for some participants
					if (randomPercent(percentageOfCases) && !healthFacilities.isEmpty()) {
						CaseDataDto caze = CaseDataDto.buildFromEventParticipant(eventParticipant, person, event.getDisease());
						fillEntity(caze, referenceDateTime);
						caze.setDisease(event.getDisease());
						caze.setReportingUser(UserProvider.getCurrent().getUserReference());
						caze.setReportDate(Date.from(referenceDateTime.atZone(ZoneId.systemDefault()).toInstant()));
						caze.setCaseOrigin(CaseOrigin.IN_COUNTRY);
						caze.setResponsibleRegion(eventGenerationConfig.getRegion());
						caze.setResponsibleDistrict(eventGenerationConfig.getDistrict());
						FacilityIndexDto facility = random(healthFacilities);
						caze.setHealthFacility(facility.toReference());
						caze.setFacilityType(facility.getType());
						caze.setAdditionalDetails("Case generated using DevMode on " + LocalDate.now());
						FacadeProvider.getCaseFacade().save(caze);
						eventParticipant.setResultingCase(caze.toReference());
						generatedCases++;
					}

					// generate contacts for some participants
					List<CaseReferenceDto> cases = FacadeProvider.getCaseFacade()
						.getRandomCaseReferences(
							new CaseCriteria().region(eventGenerationConfig.getRegion())
								.district(eventGenerationConfig.getDistrict())
								.disease(event.getDisease()),
							numParticipants * 2,
							random());
					int numContacts = randomInt(minContactsPerParticipant, maxContactsPerParticipant);
					for (int k = 0; (k < numContacts && (cases != null)); k++) {
						ContactDto contact = ContactDto.build(eventParticipant);
						contact.setDisease(event.getDisease());
						contact.setCaze(random(cases));
						contact.setReportingUser(UserProvider.getCurrent().getUserReference());
						contact.setReportDateTime(Date.from(referenceDateTime.atZone(ZoneId.systemDefault()).toInstant()));
						contact.setDescription("Contact generated using DevMode on " + LocalDate.now());
						FacadeProvider.getContactFacade().save(contact);
						generatedContacts++;
					}
				}

				FacadeProvider.getEventParticipantFacade().save(eventParticipant);
				generatedParticipants++;
			}
		}

		dt = System.nanoTime() - dt;
		long perCase = dt / eventGenerationConfig.getEntityCountAsNumber();
		String msg = String.format(
			"Generating %d events with a total of %d participants (%d contacts, %d cases) took %.2f  s (%.1f ms per event)",
			eventGenerationConfig.getEntityCountAsNumber(),
			generatedParticipants,
			generatedContacts,
			generatedCases,
			(double) dt / 1_000_000_000,
			(double) perCase / 1_000_000);
		logger.info(msg);
		Notification.show("", msg, Notification.Type.TRAY_NOTIFICATION);
	}

	private LocalDateTime getReferenceDateTime(int i, int count, float baseOffset, Disease disease, LocalDate startDate, int daysBetween) {

		float x = (float) i / count;
		x += baseOffset;
		x += 0.13f * disease.ordinal();
		x += 0.5f * random().nextFloat();
		x = (float) (Math.asin((x % 2) - 1) / Math.PI / 2) + 0.5f;

		return startDate.atStartOfDay().plusMinutes((int) (x * 60 * 24 * daysBetween));
	}

	private void setPersonName(PersonDto person) {

		Sex sex = Sex.values()[random().nextInt(2)];
		person.setSex(sex);
		if (sex == Sex.MALE) {
			person.setFirstName(random(maleFirstNames) + " " + random(maleFirstNames));
		} else {
			person.setFirstName(random(femaleFirstNames) + " " + random(femaleFirstNames));
		}
		person.setLastName(random(lastNames) + "-" + random(lastNames));

	}

	@Override
	public void enter(ViewChangeEvent event) {
		super.enter(event);
	}
}
