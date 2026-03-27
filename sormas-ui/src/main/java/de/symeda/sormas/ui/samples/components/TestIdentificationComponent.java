package de.symeda.sormas.ui.samples.components;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

import com.vaadin.data.Converter;
import com.vaadin.data.Result;
import com.vaadin.data.ValueContext;
import com.vaadin.shared.ui.ValueChangeMode;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.DateField;
import com.vaadin.ui.TextField;

import de.symeda.sormas.api.sample.PathogenTestDto;
import de.symeda.sormas.ui.samples.events.ViaLimsChangedEvent;
import de.symeda.sormas.ui.utils.FormComponent;
import de.symeda.sormas.ui.utils.FormEventBus;

/**
 * Report date, VIA_LIMS, external ID, external order ID.
 * Vaadin 8 components with own Binder.
 */
public class TestIdentificationComponent extends FormComponent<PathogenTestDto> {

	private static final long serialVersionUID = 1L;

	private final FormEventBus eventBus;

	private DateField reportDate;
	private CheckBox viaLims;
	private TextField externalId;
	private TextField externalOrderId;

	public TestIdentificationComponent(FormEventBus eventBus) {
		super(PathogenTestDto.class);
		this.eventBus = eventBus;
		buildLayout();
		bindFields();
		wireEvents();
	}

	private void buildLayout() {
		reportDate = createDateField(PathogenTestDto.REPORT_DATE, PathogenTestDto.I18N_PREFIX);
		viaLims = createCheckBox(PathogenTestDto.VIA_LIMS, PathogenTestDto.I18N_PREFIX);
		addRow(reportDate, viaLims);

		externalId = createTextField(PathogenTestDto.EXTERNAL_ID, PathogenTestDto.I18N_PREFIX);
		externalId.setValueChangeMode(ValueChangeMode.BLUR);
		externalOrderId = createTextField(PathogenTestDto.EXTERNAL_ORDER_ID, PathogenTestDto.I18N_PREFIX);
		externalOrderId.setValueChangeMode(ValueChangeMode.BLUR);
		addRow(externalId, externalOrderId);
	}

	private void bindFields() {
		binder.forField(reportDate)
			.withConverter(new LocalDateToDateConverter())
			.bind(PathogenTestDto::getReportDate, PathogenTestDto::setReportDate);

		binder.forField(viaLims).bind(PathogenTestDto::isViaLims, PathogenTestDto::setViaLims);
		binder.forField(externalId).bind(PathogenTestDto::getExternalId, PathogenTestDto::setExternalId);
		binder.forField(externalOrderId).bind(PathogenTestDto::getExternalOrderId, PathogenTestDto::setExternalOrderId);
	}

	private void wireEvents() {
		track(viaLims.addValueChangeListener(e -> eventBus.fire(new ViaLimsChangedEvent(Boolean.TRUE.equals(e.getValue())))));
	}

	public CheckBox getViaLimsField() {
		return viaLims;
	}

	/**
	 * Converter between Vaadin 8 LocalDate and java.util.Date used by the DTO.
	 */
	private static class LocalDateToDateConverter implements Converter<LocalDate, Date> {

		@Override
		public Result<Date> convertToModel(LocalDate value, ValueContext context) {
			if (value == null) {
				return Result.ok(null);
			}
			return Result.ok(Date.from(value.atStartOfDay(ZoneId.systemDefault()).toInstant()));
		}

		@Override
		public LocalDate convertToPresentation(Date value, ValueContext context) {
			if (value == null) {
				return null;
			}
			return value.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
		}
	}
}
