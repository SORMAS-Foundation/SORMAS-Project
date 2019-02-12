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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *******************************************************************************/
package de.symeda.sormas.ui.samples;

import java.util.Arrays;
import java.util.Date;

import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.DateField;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.facility.FacilityDto;
import de.symeda.sormas.api.facility.FacilityReferenceDto;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.i18n.Validations;
import de.symeda.sormas.api.sample.SampleDto;
import de.symeda.sormas.api.sample.SampleMaterial;
import de.symeda.sormas.api.sample.SampleReferenceDto;
import de.symeda.sormas.api.sample.SpecimenCondition;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.UserProvider;
import de.symeda.sormas.ui.utils.AbstractEditForm;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.DateComparisonValidator;
import de.symeda.sormas.ui.utils.DateTimeField;
import de.symeda.sormas.ui.utils.FieldHelper;
import de.symeda.sormas.ui.utils.LayoutUtil;

@SuppressWarnings("serial")
public class SampleEditForm extends AbstractEditForm<SampleDto> {

	private static final String REPORT_INFORMATION_LOC = "reportInformationLoc";

	private static final String HTML_LAYOUT = 
			LayoutUtil.h3(I18nProperties.getString(Strings.headingLaboratorySample)) +
			LayoutUtil.locCss(CssStyles.VSPACE_2, REPORT_INFORMATION_LOC) +
			LayoutUtil.divs(
					LayoutUtil.fluidRowLocs(SampleDto.SAMPLE_DATE_TIME, SampleDto.SAMPLE_CODE),
					LayoutUtil.fluidRowLocs(SampleDto.SAMPLE_MATERIAL, SampleDto.SAMPLE_MATERIAL_TEXT),
					LayoutUtil.fluidRowLocs(SampleDto.SAMPLE_SOURCE, ""),
					LayoutUtil.fluidRowLocs(SampleDto.SUGGESTED_TYPE_OF_TEST, ""),
					LayoutUtil.fluidRowLocs(SampleDto.LAB, SampleDto.LAB_DETAILS)
					) +
			LayoutUtil.locCss(CssStyles.VSPACE_TOP_3, SampleDto.SHIPPED) +
			LayoutUtil.fluidRowLocs(SampleDto.SHIPMENT_DATE, SampleDto.SHIPMENT_DETAILS) +
			LayoutUtil.locCss(CssStyles.VSPACE_TOP_3, SampleDto.RECEIVED) +
			LayoutUtil.fluidRowLocs(SampleDto.RECEIVED_DATE, SampleDto.LAB_SAMPLE_ID) +
			LayoutUtil.fluidRowLocs(SampleDto.SPECIMEN_CONDITION, SampleDto.NO_TEST_POSSIBLE_REASON) +
			LayoutUtil.fluidRowLocs(SampleDto.COMMENT)
			;

	public SampleEditForm(UserRight editOrCreateUserRight) {
		super(SampleDto.class, SampleDto.I18N_PREFIX, editOrCreateUserRight);
	}

	@Override
	protected void addFields() {
		addField(SampleDto.SAMPLE_CODE, TextField.class);
		addField(SampleDto.LAB_SAMPLE_ID, TextField.class);
		DateTimeField sampleDateField = addField(SampleDto.SAMPLE_DATE_TIME, DateTimeField.class);
		sampleDateField.setInvalidCommitted(false);
		addField(SampleDto.SAMPLE_MATERIAL, ComboBox.class);
		addField(SampleDto.SAMPLE_MATERIAL_TEXT, TextField.class);
		ComboBox sampleSource = addField(SampleDto.SAMPLE_SOURCE, ComboBox.class);
		DateField shipmentDate = addDateField(SampleDto.SHIPMENT_DATE, DateField.class, 7);
		addField(SampleDto.SHIPMENT_DETAILS, TextField.class);		
		addField(SampleDto.SUGGESTED_TYPE_OF_TEST, ComboBox.class);
		DateField receivedDate = addField(SampleDto.RECEIVED_DATE, DateField.class);
		ComboBox lab = addField(SampleDto.LAB, ComboBox.class);
		lab.addItems(FacadeProvider.getFacilityFacade().getAllLaboratories(true));
		TextField labDetails = addField(SampleDto.LAB_DETAILS, TextField.class);
		labDetails.setVisible(false);
		addField(SampleDto.SPECIMEN_CONDITION, ComboBox.class);
		addField(SampleDto.NO_TEST_POSSIBLE_REASON, TextField.class);
		addField(SampleDto.COMMENT, TextArea.class).setRows(2);
		CheckBox shipped = addField(SampleDto.SHIPPED, CheckBox.class);
		CheckBox received = addField(SampleDto.RECEIVED, CheckBox.class);

		// Validators
		sampleDateField.addValidator(new DateComparisonValidator(sampleDateField, shipmentDate, true, false,
				I18nProperties.getValidationError(Validations.beforeDate, sampleDateField.getCaption(), shipmentDate.getCaption())));
		sampleDateField.addValidator(new DateComparisonValidator(sampleDateField, receivedDate, true, false,
				I18nProperties.getValidationError(Validations.beforeDate, sampleDateField.getCaption(), receivedDate.getCaption())));
		shipmentDate.addValidator(new DateComparisonValidator(shipmentDate, sampleDateField, false, false,
				I18nProperties.getValidationError(Validations.afterDate, shipmentDate.getCaption(), sampleDateField.getCaption())));
		shipmentDate.addValidator(new DateComparisonValidator(shipmentDate, receivedDate, true, false,
				I18nProperties.getValidationError(Validations.beforeDate, shipmentDate.getCaption(), receivedDate.getCaption())));
		receivedDate.addValidator(new DateComparisonValidator(receivedDate, sampleDateField, false, false,
				I18nProperties.getValidationError(Validations.afterDate, receivedDate.getCaption(), sampleDateField.getCaption())));
		receivedDate.addValidator(new DateComparisonValidator(receivedDate, shipmentDate, false, false,
				I18nProperties.getValidationError(Validations.afterDate, receivedDate.getCaption(), shipmentDate.getCaption())));

		FieldHelper.setVisibleWhen(getFieldGroup(), SampleDto.SAMPLE_MATERIAL_TEXT, SampleDto.SAMPLE_MATERIAL, Arrays.asList(SampleMaterial.OTHER), true);
		FieldHelper.setVisibleWhen(getFieldGroup(), SampleDto.NO_TEST_POSSIBLE_REASON, SampleDto.SPECIMEN_CONDITION, Arrays.asList(SpecimenCondition.NOT_ADEQUATE), true);
		FieldHelper.setRequiredWhen(getFieldGroup(), SampleDto.SAMPLE_MATERIAL, Arrays.asList(SampleDto.SAMPLE_MATERIAL_TEXT), Arrays.asList(SampleMaterial.OTHER));
		FieldHelper.setRequiredWhen(getFieldGroup(), SampleDto.SPECIMEN_CONDITION, Arrays.asList(SampleDto.NO_TEST_POSSIBLE_REASON), Arrays.asList(SpecimenCondition.NOT_ADEQUATE));

		addValueChangeListener(e -> {
			CaseDataDto caze = FacadeProvider.getCaseFacade().getCaseDataByUuid(getValue().getAssociatedCase().getUuid());

			FieldHelper.setRequiredWhen(getFieldGroup(), received, Arrays.asList(SampleDto.RECEIVED_DATE, SampleDto.SPECIMEN_CONDITION), Arrays.asList(true));
			FieldHelper.setEnabledWhen(getFieldGroup(), received, Arrays.asList(true), Arrays.asList(SampleDto.RECEIVED_DATE, SampleDto.LAB_SAMPLE_ID, SampleDto.SPECIMEN_CONDITION, SampleDto.NO_TEST_POSSIBLE_REASON), true);

			if (caze.getDisease() != Disease.NEW_INFLUENCA) {
				sampleSource.setVisible(false);
			}
			if ((UserProvider.getCurrent().getUuid().equals(getValue().getReportingUser().getUuid()))) {
				FieldHelper.setEnabledWhen(getFieldGroup(), shipped, Arrays.asList(true), Arrays.asList(SampleDto.SHIPMENT_DATE, SampleDto.SHIPMENT_DETAILS), true);
				FieldHelper.setRequiredWhen(getFieldGroup(), shipped, Arrays.asList(SampleDto.SHIPMENT_DATE), Arrays.asList(true));
				setRequired(true, SampleDto.SAMPLE_DATE_TIME, SampleDto.SAMPLE_MATERIAL, SampleDto.LAB);
			} else {
				getField(SampleDto.SAMPLE_DATE_TIME).setEnabled(false);
				getField(SampleDto.SAMPLE_CODE).setEnabled(false);
				getField(SampleDto.SAMPLE_MATERIAL).setEnabled(false);
				getField(SampleDto.SAMPLE_MATERIAL_TEXT).setEnabled(false);
				getField(SampleDto.SUGGESTED_TYPE_OF_TEST).setEnabled(false);
				getField(SampleDto.LAB).setEnabled(false);
				getField(SampleDto.SHIPPED).setEnabled(false);
				getField(SampleDto.SHIPMENT_DATE).setEnabled(false);
				getField(SampleDto.SHIPMENT_DETAILS).setEnabled(false);
				getField(SampleDto.SAMPLE_SOURCE).setEnabled(false);
			}

			shipped.addValueChangeListener(event -> {
				if ((boolean) event.getProperty().getValue() == true) {
					if (shipmentDate.getValue() == null) {
						shipmentDate.setValue(new Date());
					}
				}
			});

			received.addValueChangeListener(event -> {
				if ((boolean) event.getProperty().getValue() == true) {
					if (receivedDate.getValue() == null) {
						receivedDate.setValue(new Date());
					}
				}
			});

			// Initialize referral and report information
			VerticalLayout reportInfoLayout = new VerticalLayout();

			String reportInfoText = I18nProperties.getString(Strings.sReportedOn) + " "
					+ DateHelper.formatLocalDateTime(getValue().getReportDateTime()) + " "
					+ I18nProperties.getString(Strings.sBy) + " " + getValue().getReportingUser().toString();
			Label reportInfoLabel = new Label(reportInfoText);
			reportInfoLabel.setEnabled(false);
			reportInfoLayout.addComponent(reportInfoLabel);

			SampleReferenceDto referredFromRef = FacadeProvider.getSampleFacade().getReferredFrom(getValue().getUuid());
			if (referredFromRef != null) {
				SampleDto referredFrom = FacadeProvider.getSampleFacade().getSampleByUuid(referredFromRef.getUuid());
				Button referredButton = new Button(I18nProperties.getCaption(Captions.sampleReferredFrom) + " " + referredFrom.getLab().toString());
				referredButton.addStyleName(ValoTheme.BUTTON_LINK);
				referredButton.addStyleName(CssStyles.VSPACE_NONE);
				referredButton.addClickListener(s -> ControllerProvider.getSampleController().navigateToData(referredFrom.getUuid()));
				reportInfoLayout.addComponent(referredButton);
			}

			getContent().addComponent(reportInfoLayout, REPORT_INFORMATION_LOC);
		});

		lab.addValueChangeListener(event -> {
			if (event.getProperty().getValue() != null && ((FacilityReferenceDto) event.getProperty().getValue()).getUuid().equals(FacilityDto.OTHER_LABORATORY_UUID)) {
				labDetails.setVisible(true);
				labDetails.setRequired(true);
			} else {
				labDetails.setVisible(false);
				labDetails.setRequired(false);
				labDetails.clear();
			}
		});
	}

	@Override
	protected String createHtmlLayout() {
		return HTML_LAYOUT;
	}

}
