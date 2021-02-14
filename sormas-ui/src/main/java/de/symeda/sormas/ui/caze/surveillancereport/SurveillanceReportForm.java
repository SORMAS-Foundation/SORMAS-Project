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

package de.symeda.sormas.ui.caze.surveillancereport;

import static de.symeda.sormas.ui.utils.LayoutUtil.fluidRowLocs;

import com.vaadin.v7.ui.ComboBox;
import com.vaadin.v7.ui.TextArea;

import de.symeda.sormas.api.caze.surveillancereport.SurveillanceReportDto;
import de.symeda.sormas.api.facility.FacilityTypeGroup;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.ui.utils.AbstractEditForm;
import de.symeda.sormas.ui.utils.InfrastructureFieldsHelper;

public class SurveillanceReportForm extends AbstractEditForm<SurveillanceReportDto> {

	private static final String FACILITY_TYPE_GROUP_LOC = "facilityTypeGroup";

	//@formatter:off
	protected static final String HTML_LAYOUT =
			fluidRowLocs(SurveillanceReportDto.REPORTING_TYPE, SurveillanceReportDto.CREATING_USER) +
			fluidRowLocs(SurveillanceReportDto.REPORT_DATE, SurveillanceReportDto.DATE_OF_DIAGNOSIS) +
			fluidRowLocs(SurveillanceReportDto.FACILITY_REGION, SurveillanceReportDto.FACILITY_DISTRICT) +
			fluidRowLocs(FACILITY_TYPE_GROUP_LOC, SurveillanceReportDto.FACILITY_TYPE) +
			fluidRowLocs(SurveillanceReportDto.FACILITY, SurveillanceReportDto.FACILITY_DETAILS) +
			fluidRowLocs(SurveillanceReportDto.NOTIFICATION_DETAILS);
	//@formatter:on

	protected SurveillanceReportForm() {
		super(SurveillanceReportDto.class, SurveillanceReportDto.I18N_PREFIX);
	}

	@Override
	protected String createHtmlLayout() {
		return HTML_LAYOUT;
	}

	@Override
	protected void addFields() {

		addField(SurveillanceReportDto.REPORTING_TYPE).setRequired(true);
		addField(SurveillanceReportDto.CREATING_USER).setReadOnly(true);

		addField(SurveillanceReportDto.REPORT_DATE).setRequired(true);
		addField(SurveillanceReportDto.DATE_OF_DIAGNOSIS);

		InfrastructureFieldsHelper.initInfrastructureFields(
			addField(SurveillanceReportDto.FACILITY_REGION),
			addField(SurveillanceReportDto.FACILITY_DISTRICT),
			null,
			addFacilityTypeGroupField(),
			addField(SurveillanceReportDto.FACILITY_TYPE),
			addField(SurveillanceReportDto.FACILITY),
			addField(SurveillanceReportDto.FACILITY_DETAILS),
			() -> getValue().getFacilityDetails());

		addField(SurveillanceReportDto.NOTIFICATION_DETAILS, TextArea.class).setRows(7);

	}

	private ComboBox addFacilityTypeGroupField() {
		ComboBox facilityTypeGroup = new ComboBox();
		facilityTypeGroup.setId(FACILITY_TYPE_GROUP_LOC);
		facilityTypeGroup.setCaption(I18nProperties.getCaption(Captions.Facility_typeGroup));
		facilityTypeGroup.setWidth(100, Unit.PERCENTAGE);
		facilityTypeGroup.addItems(FacilityTypeGroup.getAccomodationGroups());
		getContent().addComponent(facilityTypeGroup, FACILITY_TYPE_GROUP_LOC);

		return facilityTypeGroup;
	}
}
