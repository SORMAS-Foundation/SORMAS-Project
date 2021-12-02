package de.symeda.sormas.ui.utils.components.birthdate;

import java.time.Month;
import java.util.List;

import com.vaadin.data.Binder;
import com.vaadin.data.ValidationResult;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.CustomField;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.v7.data.Validator;

import de.symeda.sormas.api.caze.BirthDateDto;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.utils.CssStyles;

public class BirthDateField extends CustomField<BirthDateDto> {

	private final Binder<BirthDateDto> binder = new Binder<>(BirthDateDto.class);

	private final ComboBox<Integer> dateOfBirthYear;
	private final ComboBox<Integer> dateOfBirthMonth;
	private final ComboBox<Integer> dateOfBirthDay;

	public BirthDateField() {
		dateOfBirthYear = new ComboBox<>();
		dateOfBirthMonth = new ComboBox<>();
		dateOfBirthDay = new ComboBox<>();
	}

	@Override
	protected Component initContent() {
		if (getValue() == null) {
			setValue(new BirthDateDto());
		}

		HorizontalLayout layout = new HorizontalLayout();

		dateOfBirthYear.setId("dateOfBirthYear");
		dateOfBirthYear.setEmptySelectionAllowed(true);
		dateOfBirthYear.setItems(DateHelper.getYearsToNow());
		dateOfBirthYear.setWidth(80, Unit.PIXELS);
		dateOfBirthYear.addStyleName(CssStyles.CAPTION_OVERFLOW);
		binder.forField(dateOfBirthYear).withValidator((e, context) -> {
			try {
				ControllerProvider.getPersonController().validateBirthDate(e, dateOfBirthMonth.getValue(), dateOfBirthDay.getValue());
				return ValidationResult.ok();
			} catch (Validator.InvalidValueException ex) {
				return ValidationResult.error(ex.getMessage());
			}
		}).bind(BirthDateDto.DATE_OF_BIRTH_YYYY);

		dateOfBirthMonth.setId("dateOfBirthMonth");
		dateOfBirthMonth.setEmptySelectionAllowed(true);
		dateOfBirthMonth.setItems(DateHelper.getMonthsInYear());
		dateOfBirthMonth.setPageLength(12);
		setItemCaptionsForMonths(dateOfBirthMonth);
		dateOfBirthMonth.setWidth(120, Unit.PIXELS);
		binder.forField(dateOfBirthMonth).withValidator((e, context) -> {
			try {
				ControllerProvider.getPersonController().validateBirthDate(dateOfBirthYear.getValue(), e, dateOfBirthDay.getValue());
				return ValidationResult.ok();
			} catch (Validator.InvalidValueException ex) {
				return ValidationResult.error(ex.getMessage());
			}
		}).bind(BirthDateDto.DATE_OF_BIRTH_MM);

		dateOfBirthDay.setId("dateOfBirthDay");
		dateOfBirthDay.setEmptySelectionAllowed(true);
		dateOfBirthDay.setWidth(80, Unit.PIXELS);
		binder.forField(dateOfBirthDay).withValidator((e, context) -> {
			try {
				ControllerProvider.getPersonController().validateBirthDate(dateOfBirthYear.getValue(), dateOfBirthMonth.getValue(), e);
				return ValidationResult.ok();
			} catch (Validator.InvalidValueException ex) {
				return ValidationResult.error(ex.getMessage());
			}
		}).bind(BirthDateDto.DATE_OF_BIRTH_DD);

		// Update the list of days according to the selected month and year
		dateOfBirthYear.addValueChangeListener(e -> {
			getValue().setDateOfBirthYYYY(e.getValue());
			updateListOfDays(e.getValue(), dateOfBirthMonth.getValue(), dateOfBirthDay);
			dateOfBirthMonth.markAsDirty();
			dateOfBirthDay.markAsDirty();
		});
		dateOfBirthMonth.addValueChangeListener(e -> {
			getValue().setDateOfBirthMM(e.getValue());
			updateListOfDays(dateOfBirthYear.getValue(), e.getValue(), dateOfBirthDay);
			dateOfBirthYear.markAsDirty();
			dateOfBirthDay.markAsDirty();
		});
		dateOfBirthDay.addValueChangeListener(e -> {
			getValue().setDateOfBirthDD(e.getValue());
			dateOfBirthYear.markAsDirty();
			dateOfBirthMonth.markAsDirty();
		});

		layout.addComponents(dateOfBirthYear, dateOfBirthMonth, dateOfBirthDay);

		layout.setComponentAlignment(dateOfBirthMonth, Alignment.BOTTOM_LEFT);
		layout.setComponentAlignment(dateOfBirthDay, Alignment.BOTTOM_LEFT);

		return layout;
	}

	@Override
	protected void doSetValue(BirthDateDto birthDateDto) {
		binder.setBean(birthDateDto);
	}

	@Override
	public BirthDateDto getValue() {
		return binder.getBean();
	}

	@Override
	public void setEnabled(boolean enabled) {
		super.setEnabled(enabled);
		dateOfBirthYear.setEnabled(enabled);
		dateOfBirthMonth.setEnabled(enabled);
		dateOfBirthDay.setEnabled(enabled);
	}

	private void setItemCaptionsForMonths(ComboBox<Integer> comboBox) {
		comboBox.setItemCaptionGenerator(item -> I18nProperties.getEnumCaption(Month.of(item)));
	}

	private void updateListOfDays(Integer selectedYear, Integer selectedMonth, ComboBox<Integer> dateOfBirthDay) {
		Integer currentlySelected = dateOfBirthDay.getValue();
		List<Integer> daysInMonth = DateHelper.getDaysInMonth(selectedMonth, selectedYear);
		dateOfBirthDay.setItems(daysInMonth);
		if (daysInMonth.contains(currentlySelected)) {
			dateOfBirthDay.setValue(currentlySelected);
		}
	}
}
