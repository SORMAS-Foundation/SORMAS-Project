/*******************************************************************************
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2018 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 *******************************************************************************/
package de.symeda.sormas.ui.samples.components;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Supplier;

import com.vaadin.shared.ui.ValueChangeMode;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.DateField;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Validations;
import de.symeda.sormas.api.infrastructure.facility.FacilityDto;
import de.symeda.sormas.api.infrastructure.facility.FacilityReferenceDto;
import de.symeda.sormas.api.sample.PCRTestSpecification;
import de.symeda.sormas.api.sample.PathogenTestCategory;
import de.symeda.sormas.api.sample.PathogenTestDto;
import de.symeda.sormas.api.sample.PathogenTestType;
import de.symeda.sormas.api.utils.UtilDate;
import de.symeda.sormas.api.utils.fieldaccess.UiFieldAccessCheckers;
import de.symeda.sormas.api.utils.fieldvisibility.FieldVisibilityCheckers;
import de.symeda.sormas.ui.samples.events.DiseaseChangedEvent;
import de.symeda.sormas.ui.samples.events.TestTypeChangedEvent;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.FormComponent;
import de.symeda.sormas.ui.utils.FormEventBus;

/**
 * Test type, typing ID, test date/time, lab, lab details.
 * as a standalone composable component
 */
public class TestMethodComponent extends FormComponent<PathogenTestDto> {

	private static final long serialVersionUID = 1L;

	private final FormEventBus eventBus;
	private final Supplier<Date> sampleDateSupplier;
	private final Map<Integer, String> timeItems = new LinkedHashMap<>();

	private ComboBox<PathogenTestCategory> testCategoryField;
	private ComboBox<PathogenTestType> testTypeField;
	private TextField testTypeTextField;
	private Label testTypeTextSpacer;
	private ComboBox<PCRTestSpecification> pcrTestSpecField;
	private HorizontalLayout pcrTestSpecRow;
	private TextField typingIdField;
	private DateField testDateField;
	private ComboBox<Integer> testTimeField;
	private ComboBox<FacilityReferenceDto> labField;
	private TextField labDetailsField;

	private PathogenTestDto currentDto;
	private Disease currentDisease;

	private HorizontalLayout typingIdRow;
	private HorizontalLayout labDetailsRow;

	private Supplier<Date> testDateSampleDateSupplier;
	private String testDateValidationError;

	public TestMethodComponent(FormEventBus eventBus, Supplier<Date> sampleDateSupplier, Disease initialDisease) {
		super(PathogenTestDto.class);
		this.eventBus = eventBus;
		this.sampleDateSupplier = sampleDateSupplier;
		this.currentDisease = initialDisease;
		buildLayout();
		bindFields();
		wireEvents();
	}

	private void buildLayout() {
		// Test category — scopes the method list below; not persisted (derived from the chosen method)
		testCategoryField = new ComboBox<>();
		testCategoryField.setId(PathogenTestDto.TEST_TYPE + "_category");
		testCategoryField.setCaption(I18nProperties.getCaption(Captions.PathogenTest_testCategory));
		testCategoryField.setRequiredIndicatorVisible(true);
		CssStyles.style(testCategoryField, CssStyles.CAPTION_ON_TOP);
		testCategoryField.setWidth(100, Unit.PERCENTAGE);
		testCategoryField.setItems(getVisibleTestCategories(currentDisease));
		testCategoryField.setItemCaptionGenerator(PathogenTestCategory::toString);
		addRow(testCategoryField);

		// Test type
		testTypeField = createComboBox(PathogenTestDto.TEST_TYPE, PathogenTestDto.I18N_PREFIX);
		updateTestTypeItemsByCategory(testTypeField, currentDisease, null);
		testTypeField.setItemCaptionGenerator(this::getTestTypeCaption);

		testTypeTextField = createTextField(PathogenTestDto.TEST_TYPE_TEXT, PathogenTestDto.I18N_PREFIX, ValueChangeMode.BLUR);
		testTypeTextField.setVisible(false);

		testTypeTextSpacer = createSpacer();
		addToggleRow(testTypeField, testTypeTextField, testTypeTextSpacer);

		// PCR test specification (Coronavirus-specific, below test type)
		pcrTestSpecField = createComboBox(PathogenTestDto.PCR_TEST_SPECIFICATION, PathogenTestDto.I18N_PREFIX);
		pcrTestSpecField.setItems(PCRTestSpecification.values());
		pcrTestSpecField.setItemCaptionGenerator(PCRTestSpecification::toString);
		pcrTestSpecField.setVisible(false);
		pcrTestSpecRow = addRow(pcrTestSpecField);

		// Typing ID
		typingIdField = createTextField(PathogenTestDto.TYPING_ID, PathogenTestDto.I18N_PREFIX, ValueChangeMode.BLUR);
		typingIdField.setVisible(false);
		typingIdRow = addRow(typingIdField);

		// Test date/time — custom IDs so created manually and tracked
		testDateField = new DateField();
		testDateField.setId(PathogenTestDto.TEST_DATE_TIME + "_date");
		testDateField.setCaption(I18nProperties.getPrefixCaption(PathogenTestDto.I18N_PREFIX, PathogenTestDto.TEST_DATE_TIME));
		testDateField.setRequiredIndicatorVisible(true);
		CssStyles.style(testDateField, CssStyles.CAPTION_ON_TOP);
		testDateField.setWidth(100, Unit.PERCENTAGE);

		testTimeField = new ComboBox<>();
		testTimeField.setId(PathogenTestDto.TEST_DATE_TIME + "_time");
		testTimeField.setCaption("");
		CssStyles.style(testTimeField, CssStyles.CAPTION_ON_TOP);
		testTimeField.setWidth(100, Unit.PERCENTAGE);
		testTimeField.setEmptySelectionAllowed(true);
		for (int hours = 0; hours <= 23; hours++) {
			for (int minutes = 0; minutes <= 59; minutes += 15) {
				int totalMinutes = hours * 60 + minutes;
				timeItems.put(totalMinutes, String.format("%02d:%02d", hours, minutes));
			}
		}
		testTimeField.setItems(timeItems.keySet());
		testTimeField.setItemCaptionGenerator(timeItems::get);

		HorizontalLayout testDateTimeGroup = new HorizontalLayout();
		testDateTimeGroup.setWidth(100, Unit.PERCENTAGE);
		testDateTimeGroup.setSpacing(true);
		testDateTimeGroup.addComponent(testDateField);
		testDateTimeGroup.setExpandRatio(testDateField, 1);
		testDateTimeGroup.addComponent(testTimeField);
		testDateTimeGroup.setExpandRatio(testTimeField, 1);

		// Lab
		labField = createComboBox(PathogenTestDto.LAB, PathogenTestDto.I18N_PREFIX);
		labField.setItems(FacadeProvider.getFacilityFacade().getAllActiveLaboratories(true));
		labField.setItemCaptionGenerator(FacilityReferenceDto::getCaption);

		addRow(testDateTimeGroup, labField);

		// Lab details
		labDetailsField = createTextField(PathogenTestDto.LAB_DETAILS, PathogenTestDto.I18N_PREFIX, ValueChangeMode.BLUR);
		labDetailsField.setVisible(false);
		labDetailsRow = addRowWithLeadingSpacer(labDetailsField);
	}

	private void bindFields() {
		binder.forField(testTypeField).asRequired().bind(PathogenTestDto::getTestType, PathogenTestDto::setTestType);
		binder.forField(testTypeTextField).bind(PathogenTestDto::getTestTypeText, PathogenTestDto::setTestTypeText);
		binder.forField(pcrTestSpecField).bind(PathogenTestDto::getPcrTestSpecification, PathogenTestDto::setPcrTestSpecification);
		binder.forField(typingIdField).bind(PathogenTestDto::getTypingId, PathogenTestDto::setTypingId);
		// testDateField and testTimeField are managed manually (both map to testDateTime)
		binder.forField(labField).bind(PathogenTestDto::getLab, PathogenTestDto::setLab);
		binder.forField(labDetailsField).bind(PathogenTestDto::getLabDetails, PathogenTestDto::setLabDetails);
	}

	private void wireEvents() {
		// Category scopes the method list. Clear the method only when it no longer fits the new category
		// (updateTestTypeItemsByCategory keeps a still-valid selection).
		track(testCategoryField.addValueChangeListener(e -> {
			PathogenTestType selectedType = testTypeField.getValue();
			// If the selected category is null, clear the test type field and update the test type list
			if (testCategoryField.getValue() == null) {
				testTypeField.clear();
			}
			if (selectedType != null && e.getValue() != null && PathogenTestType.getCategory(selectedType) != e.getValue()) {
				testTypeField.clear();
			}
			updateTestTypeItemsByCategory(testTypeField, currentDisease, e.getValue());
		}));

		// Self-managed visibility: testTypeText reveals for any value annotated with @RevealsTestTypeText
		// (PCR_RT_PCR + OTHER for every disease; the four typing tests for Salmonellosis only).
		track(testTypeField.addValueChangeListener(e -> {
			PathogenTestType type = e.getValue();

			// Keep the category selector in sync when a method is set (e.g. when editing an existing test)
			if (type != null) {
				PathogenTestCategory category = PathogenTestType.getCategory(type);
				if (category != null && testCategoryField.getValue() != category) {
					testCategoryField.setValue(category);
				}
			}

			boolean showTestTypeText = PathogenTestType.revealsTestTypeText(type, currentDisease);
			testTypeTextField.setVisible(showTestTypeText);
			testTypeTextSpacer.setVisible(!showTestTypeText);
			if (!showTestTypeText) {
				testTypeTextField.clear();
			}

			boolean showTypingId =
				type == PathogenTestType.PCR_RT_PCR || type == PathogenTestType.DNA_MICROARRAY || type == PathogenTestType.SEQUENCING;
			typingIdField.setVisible(showTypingId);
			if (!showTypingId) {
				typingIdField.clear();
			}
			updateRowVisibility(typingIdRow);

			updatePcrTestSpecVisibility(type);

			eventBus.fire(new TestTypeChangedEvent(type));
		}));

		// Self-managed: lab details visible when "Other" lab selected
		track(labField.addValueChangeListener(e -> {
			FacilityReferenceDto lab = e.getValue();
			boolean showDetails = lab != null && FacilityDto.OTHER_FACILITY_UUID.equals(lab.getUuid());
			labDetailsField.setVisible(showDetails);
			if (!showDetails) {
				labDetailsField.clear();
			}
			updateRowVisibility(labDetailsRow);
		}));

		// Sync date/time fields back to DTO on value change
		track(testDateField.addValueChangeListener(e -> syncTestDateTimeToDto()));
		track(testTimeField.addValueChangeListener(e -> syncTestDateTimeToDto()));

		// Listen for disease changes to update test type items and PCR spec visibility. Only reset the
		// category/method picker when the disease actually changes (an existing test is restored via
		// setDto afterwards, so we must not clear on a no-op event).
		track(eventBus.on(DiseaseChangedEvent.class, event -> {
			if (event.getDisease() == currentDisease) {
				return;
			}
			currentDisease = event.getDisease();
			updateTestTypeItems(currentDisease);
			updatePcrTestSpecVisibility(testTypeField.getValue());
		}));
	}

	private void syncTestDateTimeToDto() {
		if (currentDto == null) {
			return;
		}
		LocalDate date = testDateField.getValue();
		if (date == null) {
			currentDto.setTestDateTime(null);
			return;
		}
		Integer totalMinutes = testTimeField.getValue();
		LocalDateTime dateTime;
		if (totalMinutes != null) {
			dateTime = date.atTime(totalMinutes / 60, totalMinutes % 60);
		} else {
			dateTime = date.atStartOfDay();
		}
		currentDto.setTestDateTime(Date.from(dateTime.atZone(ZoneId.systemDefault()).toInstant()));
	}

	private void populateTestDateTimeFields(PathogenTestDto dto) {
		Date testDateTime = dto != null ? dto.getTestDateTime() : null;
		if (testDateTime == null) {
			testDateField.setValue(null);
			testTimeField.setValue(null);
			return;
		}
		LocalDateTime ldt = testDateTime.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
		testDateField.setValue(ldt.toLocalDate());
		LocalTime time = ldt.toLocalTime();
		int totalMinutes = time.getHour() * 60 + time.getMinute();
		if (!timeItems.containsKey(totalMinutes)) {
			timeItems.put(totalMinutes, String.format("%02d:%02d", time.getHour(), time.getMinute()));
			testTimeField.setItems(timeItems.keySet());
			testTimeField.setItemCaptionGenerator(timeItems::get);
		}
		testTimeField.setValue(totalMinutes);
	}

	private void updateTestTypeItems(Disease disease) {
		// Changing disease starts the test-type selection fresh: clear category (which leaves the method
		// unscoped) and the method, mirroring how the method already resets when it no longer fits.
		testCategoryField.setItems(getVisibleTestCategories(disease));
		testCategoryField.clear();
		testTypeField.clear();
		updateTestTypeItemsByCategory(testTypeField, disease, null);
	}

	/**
	 * Disease-aware caption for a test type. For Invasive Pneumococcal Infection the SEROGROUPING
	 * test type is presented as "Serotyping" without affecting other diseases.
	 */
	private String getTestTypeCaption(PathogenTestType testType) {
		if (currentDisease == Disease.INVASIVE_PNEUMOCOCCAL_INFECTION && testType == PathogenTestType.SEROGROUPING) {
			return I18nProperties.getCaption(Captions.PathogenTest_seroGrouping_INVASIVE_PNEUMOCOCCAL_INFECTION);
		}
		return PathogenTestType.toString(testType, null);
	}

	private void updatePcrTestSpecVisibility(PathogenTestType testType) {
		boolean visible = currentDisease == Disease.CORONAVIRUS && testType == PathogenTestType.PCR_RT_PCR;
		pcrTestSpecField.setVisible(visible);
		if (!visible) {
			pcrTestSpecField.clear();
		}
		updateRowVisibility(pcrTestSpecRow);
	}

	/** Forces typingId field visible when a preset value exists, regardless of current test type. */
	public void showTypingIdIfPreset(String typingId) {
		if (typingId != null && !typingId.trim().isEmpty()) {
			typingIdField.setVisible(true);
			updateRowVisibility(typingIdRow);
		}
	}

	/**
	 * Registers a validator ensuring test date is not before sample date.
	 * Evaluated during {@link #validate()}.
	 * <p>
	 * Note: Future date validation is always applied in {@link #validate()} and does not require
	 * this method to be called.
	 */
	public void addTestDateAfterSampleDateValidator(Supplier<Date> sampleDateSupplier, String errorMessage) {
		this.testDateSampleDateSupplier = sampleDateSupplier;
		this.testDateValidationError = errorMessage;
	}

	@Override
	public void validate() {
		super.validate();
		// Test date is mandatory whenever the field is shown
		if (testDateField.isVisible() && testDateField.getValue() == null) {
			throw new com.vaadin.v7.data.Validator.InvalidValueException(
				I18nProperties.getValidationError(
					Validations.required,
					I18nProperties.getPrefixCaption(PathogenTestDto.I18N_PREFIX, PathogenTestDto.TEST_DATE_TIME)));
		}
		if (testDateField.getValue() != null) {
			Integer totalMinutes = testTimeField.getValue();
			LocalDateTime ldt = totalMinutes != null
				? testDateField.getValue().atTime(totalMinutes / 60, totalMinutes % 60)
				: testDateField.getValue().atStartOfDay();
			Date testDate = Date.from(ldt.atZone(ZoneId.systemDefault()).toInstant());
			// Check that test date is not in the future
			if (testDate.after(UtilDate.now())) {
				throw new com.vaadin.v7.data.Validator.InvalidValueException(
					I18nProperties.getValidationError(Validations.futureDateStrict, I18nProperties.getCaption(Captions.PathogenTest_testDateTime)));
			}
			// Check that test date is not before sample date
			if (testDateSampleDateSupplier != null) {
				Date sampleDate = testDateSampleDateSupplier.get();
				if (sampleDate != null && testDate.before(sampleDate)) {
					throw new com.vaadin.v7.data.Validator.InvalidValueException(testDateValidationError);
				}
			}
		}
	}

	public ComboBox<PathogenTestType> getTestTypeField() {
		return testTypeField;
	}

	public ComboBox<FacilityReferenceDto> getLabField() {
		return labField;
	}

	public void setLabRequired(boolean required) {
		binder.removeBinding(labField);
		if (required) {
			binder.forField(labField).asRequired().bind(PathogenTestDto::getLab, PathogenTestDto::setLab);
		} else {
			binder.forField(labField).bind(PathogenTestDto::getLab, PathogenTestDto::setLab);
		}
	}

	@Override
	public void setDto(PathogenTestDto dto) {
		this.currentDto = dto;
		populateTestDateTimeFields(dto);
		// Pre-select the category and ensure the saved method is in the scoped list before binding, so a
		// test recorded with a now-hidden/legacy method still loads and displays its method + category.
		PathogenTestType savedType = dto != null ? dto.getTestType() : null;
		PathogenTestCategory savedCategory = PathogenTestType.getCategory(savedType);
		// A legacy/hidden saved method's category may not be among the scoped categories; include it so
		// the selection is a member of the combo's items (Vaadin 8 won't display a value outside them).
		testCategoryField.setItems(getVisibleTestCategories(currentDisease, savedCategory));
		testCategoryField.setValue(savedCategory);
		// Make the saved method selectable but DON'T pre-select it — let binder.setBean set the value so
		// the value-change fires TestTypeChangedEvent, which the disease sections rely on to show their
		// fields when an existing test is opened.
		updateTestTypeItemsByCategory(testTypeField, currentDisease, savedCategory, savedType, false);
		super.setDto(dto);
	}

	@Override
	public void applyVisibility(FieldVisibilityCheckers checkers, Class<?> dtoClass) {
		super.applyVisibility(checkers, dtoClass);
		// testDateField/testTimeField/testCategoryField have custom IDs that don't match a DTO property.
		// The category selector follows the test-type field's visibility (it scopes the same field).
		if (!checkers.isVisible(dtoClass, PathogenTestDto.TEST_DATE_TIME)) {
			testDateField.setVisible(false);
			testTimeField.setVisible(false);
		}
		if (!checkers.isVisible(dtoClass, PathogenTestDto.TEST_TYPE)) {
			testCategoryField.setVisible(false);
		}
		updateRowAndSelfVisibility();
	}

	@Override
	public void applyAccess(UiFieldAccessCheckers checkers, Class<?> dtoClass) {
		super.applyAccess(checkers, dtoClass);
		// testDateField/testTimeField/testCategoryField have custom IDs that don't match a DTO property
		if (!checkers.isAccessible(dtoClass, PathogenTestDto.TEST_DATE_TIME)) {
			testDateField.setEnabled(false);
			testDateField.addStyleName(CssStyles.INACCESSIBLE_FIELD);
			testTimeField.setEnabled(false);
			testTimeField.addStyleName(CssStyles.INACCESSIBLE_FIELD);
		}
		if (!checkers.isAccessible(dtoClass, PathogenTestDto.TEST_TYPE)) {
			testCategoryField.setEnabled(false);
			testCategoryField.addStyleName(CssStyles.INACCESSIBLE_FIELD);
		}
	}
}
