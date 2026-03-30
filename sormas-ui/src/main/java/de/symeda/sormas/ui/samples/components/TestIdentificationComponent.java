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
 * as a standalone composable component
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
