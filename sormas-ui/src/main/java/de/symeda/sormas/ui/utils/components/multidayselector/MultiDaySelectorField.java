package de.symeda.sormas.ui.utils.components.multidayselector;

import java.time.LocalDate;
import java.util.Properties;

import com.vaadin.data.Binder;
import com.vaadin.data.BinderValidationStatus;
import com.vaadin.data.ValidationResult;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.CustomField;
import com.vaadin.ui.DateField;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.VerticalLayout;

import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Validations;
import de.symeda.sormas.ui.utils.CssStyles;

public class MultiDaySelectorField extends CustomField<MultiDaySelectorDto> {

	private static final long serialVersionUID = -5990656967372610518L;

	private final Binder<MultiDaySelectorDto> binder = new Binder<>(MultiDaySelectorDto.class);

	private final CheckBox multiDaySelect;
	private final DateField startDate;
	private final DateField endDate;

	private final DateField reportDate;

	protected Properties properties;

	public MultiDaySelectorField(DateField reportDate) {
		this.reportDate = reportDate;

		multiDaySelect = new CheckBox();
		startDate = new DateField();
		endDate = new DateField();

		this.properties = new Properties();
	}

	@Override
	protected Component initContent() {
		setValue(new MultiDaySelectorDto());

		VerticalLayout layout = new VerticalLayout();
		layout.setSpacing(false);
		layout.setMargin(false);

		HorizontalLayout selectorLayout = new HorizontalLayout();

		multiDaySelect.setId("multiDaySelect");
		binder.forField(multiDaySelect).bind(MultiDaySelectorDto.MULTI_DAY);
		multiDaySelect.addValueChangeListener(e -> {
			getValue().setMultiDay(e.getValue());
			startDate.setVisible(e.getValue());
			startDate.setValue(null);
		});
		selectorLayout.addComponent(multiDaySelect);

		HorizontalLayout datesLayout = new HorizontalLayout();

		startDate.setId("firstDate");
		startDate.setWidth(150, Unit.PIXELS);
		startDate.setRangeEnd(LocalDate.now());
		startDate.setVisible(getValue().isMultiDay());
		Binder.BindingBuilder<MultiDaySelectorDto, LocalDate> startDateBindingBuilder = binder.forField(startDate)
			.withValidator(
				new DateComparisonValidator(
					endDate,
					true,
					I18nProperties.getValidationError(Validations.beforeDate, startDate.getCaption(), endDate.getCaption())));
		if (reportDate != null) {
			startDateBindingBuilder = startDateBindingBuilder.withValidator(
				new DateComparisonValidator(
					reportDate,
					true,
					I18nProperties.getValidationError(Validations.beforeDate, startDate.getCaption(), reportDate.getCaption())));
		}
		startDateBindingBuilder.bind(MultiDaySelectorDto.START_DATE);
		startDate.addValueChangeListener(e -> {
			binder.validate();
			getValue().setStartDate(e.getValue());
			enableValidationForEndDate(e.getValue() != null);
		});

		reportDate.addValueChangeListener(event -> binder.validate());

		endDate.setId("lastDate");
		endDate.setWidth(150, Unit.PIXELS);
		Binder.BindingBuilder<MultiDaySelectorDto, LocalDate> endDateBindingBuilder =
			binder.forField(endDate).asRequired((localDate, valueContext) -> {
				if (multiDaySelect.getValue() != Boolean.TRUE || startDate.getValue() == null || localDate != null) {
					return ValidationResult.ok();
				}
				return ValidationResult.error("");
			})
				.withValidator(
					new DateComparisonValidator(
						startDate,
						false,
						I18nProperties.getValidationError(Validations.afterDate, endDate.getCaption(), startDate.getCaption())));
		if (reportDate != null) {
			endDateBindingBuilder = endDateBindingBuilder.withValidator(
				new DateComparisonValidator(
					reportDate,
					true,
					I18nProperties.getValidationError(Validations.beforeDate, endDate.getCaption(), reportDate.getCaption())));
		}
		endDateBindingBuilder.bind(MultiDaySelectorDto.END_DATE);
		endDate.setRangeEnd(LocalDate.now());
		endDate.addValueChangeListener(e -> getValue().setEndDate(e.getValue()));

		enableValidationForEndDate(false);

		datesLayout.addComponents(startDate, endDate);

		layout.addComponents(selectorLayout, datesLayout);

		return layout;
	}

	@Override
	protected void doSetValue(MultiDaySelectorDto multiDaySelectorDto) {
		binder.setBean(multiDaySelectorDto);
	}

	@Override
	public MultiDaySelectorDto getValue() {
		return binder.getBean();
	}

	public BinderValidationStatus<MultiDaySelectorDto> validate() {
		return binder.validate();
	}

	public void showCaptions() {
		String prefix = properties.getProperty("prefix");
		multiDaySelect.setCaption(I18nProperties.getPrefixCaption(prefix, properties.getProperty("multiDay")));
		multiDaySelect.removeStyleName(CssStyles.CAPTION_HIDDEN);
		startDate.setCaption(I18nProperties.getPrefixCaption(prefix, properties.getProperty("firstDate")));
		startDate.removeStyleName(CssStyles.CAPTION_HIDDEN);
		endDate.setCaption(I18nProperties.getPrefixCaption(prefix, properties.getProperty("lastDate")));
		endDate.removeStyleName(CssStyles.CAPTION_HIDDEN);
		endDate.addStyleName(CssStyles.SOFT_REQUIRED);
	}

	private void enableValidationForEndDate(boolean enable) {
		binder.getBinding(MultiDaySelectorDto.END_DATE).get().setAsRequiredEnabled(enable);
	}
}
