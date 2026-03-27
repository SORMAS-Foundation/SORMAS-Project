package de.symeda.sormas.ui.samples.components;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Arrays;
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
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.infrastructure.facility.FacilityDto;
import de.symeda.sormas.api.infrastructure.facility.FacilityReferenceDto;
import de.symeda.sormas.api.sample.PathogenTestDto;
import de.symeda.sormas.api.sample.PathogenTestType;
import de.symeda.sormas.api.utils.fieldaccess.UiFieldAccessCheckers;
import de.symeda.sormas.api.utils.fieldvisibility.FieldVisibilityCheckers;
import de.symeda.sormas.ui.samples.events.DiseaseChangedEvent;
import de.symeda.sormas.ui.samples.events.TestTypeChangedEvent;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.FormComponent;
import de.symeda.sormas.ui.utils.FormEventBus;

/**
 * Test type, typing ID, test date/time, lab, lab details.
 * Vaadin 8 components with own Binder, self-managed visibility.
 */
public class TestMethodComponent extends FormComponent<PathogenTestDto> {

	private static final long serialVersionUID = 1L;

	private final FormEventBus eventBus;
	private final Supplier<Date> sampleDateSupplier;
	private final Map<Integer, String> timeItems = new LinkedHashMap<>();

	private ComboBox<PathogenTestType> testTypeField;
	private TextField testTypeTextField;
	private Label testTypeTextSpacer;
	private TextField typingIdField;
	private DateField testDateField;
	private ComboBox<Integer> testTimeField;
	private ComboBox<FacilityReferenceDto> labField;
	private TextField labDetailsField;

	private PathogenTestDto currentDto;

	private HorizontalLayout typingIdRow;
	private HorizontalLayout labDetailsRow;

	public TestMethodComponent(FormEventBus eventBus, Supplier<Date> sampleDateSupplier) {
		super(PathogenTestDto.class);
		this.eventBus = eventBus;
		this.sampleDateSupplier = sampleDateSupplier;
		buildLayout();
		bindFields();
		wireEvents();
	}

	private void buildLayout() {
		// Test type
		testTypeField = createComboBox(PathogenTestDto.TEST_TYPE, PathogenTestDto.I18N_PREFIX);
		testTypeField.setItems(Arrays.asList(PathogenTestType.values()));
		testTypeField.setItemCaptionGenerator(PathogenTestType::toString);

		testTypeTextField = createTextField(PathogenTestDto.TEST_TYPE_TEXT, PathogenTestDto.I18N_PREFIX, ValueChangeMode.BLUR);
		testTypeTextField.setVisible(false);

		testTypeTextSpacer = createSpacer();
		addToggleRow(testTypeField, testTypeTextField, testTypeTextSpacer);

		// Typing ID
		typingIdField = createTextField(PathogenTestDto.TYPING_ID, PathogenTestDto.I18N_PREFIX, ValueChangeMode.BLUR);
		typingIdField.setVisible(false);
		typingIdRow = addRow(typingIdField);

		// Test date/time — custom IDs so created manually and tracked
		testDateField = new DateField();
		testDateField.setId(PathogenTestDto.TEST_DATE_TIME + "_date");
		testDateField.setCaption(I18nProperties.getPrefixCaption(PathogenTestDto.I18N_PREFIX, PathogenTestDto.TEST_DATE_TIME));
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
		binder.forField(typingIdField).bind(PathogenTestDto::getTypingId, PathogenTestDto::setTypingId);
		// testDateField and testTimeField are managed manually (both map to testDateTime)
		binder.forField(labField).bind(PathogenTestDto::getLab, PathogenTestDto::setLab);
		binder.forField(labDetailsField).bind(PathogenTestDto::getLabDetails, PathogenTestDto::setLabDetails);
	}

	private void wireEvents() {
		// Self-managed visibility: testTypeText visible for PCR_RT_PCR or OTHER
		track(testTypeField.addValueChangeListener(e -> {
			PathogenTestType type = e.getValue();

			boolean showTestTypeText = type == PathogenTestType.PCR_RT_PCR || type == PathogenTestType.OTHER;
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

		// Listen for disease changes to update test type items
		track(eventBus.on(DiseaseChangedEvent.class, event -> updateTestTypeItems(event.getDisease())));
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
		updateComboBoxByDisease(testTypeField, PathogenTestType.class, disease);
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
		super.setDto(dto);
	}

	@Override
	public void applyVisibility(FieldVisibilityCheckers checkers, Class<?> dtoClass) {
		super.applyVisibility(checkers, dtoClass);
		// testDateField/testTimeField have custom IDs that don't match the DTO property
		if (!checkers.isVisible(dtoClass, PathogenTestDto.TEST_DATE_TIME)) {
			testDateField.setVisible(false);
			testTimeField.setVisible(false);
		}
		updateRowAndSelfVisibility();
	}

	@Override
	public void applyAccess(UiFieldAccessCheckers checkers, Class<?> dtoClass) {
		super.applyAccess(checkers, dtoClass);
		// testDateField/testTimeField have custom IDs that don't match the DTO property
		if (!checkers.isAccessible(dtoClass, PathogenTestDto.TEST_DATE_TIME)) {
			testDateField.setEnabled(false);
			testDateField.addStyleName(CssStyles.INACCESSIBLE_FIELD);
			testTimeField.setEnabled(false);
			testTimeField.addStyleName(CssStyles.INACCESSIBLE_FIELD);
		}
	}
}
