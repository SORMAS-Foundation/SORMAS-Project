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
package de.symeda.sormas.ui.caze;

import static de.symeda.sormas.ui.utils.CssStyles.VSPACE_4;
import static de.symeda.sormas.ui.utils.LayoutUtil.fluidColumn;
import static de.symeda.sormas.ui.utils.LayoutUtil.fluidColumnLoc;
import static de.symeda.sormas.ui.utils.LayoutUtil.fluidRow;
import static de.symeda.sormas.ui.utils.LayoutUtil.fluidRowLocs;
import static de.symeda.sormas.ui.utils.LayoutUtil.fluidRowLocsCss;
import static de.symeda.sormas.ui.utils.LayoutUtil.locs;

import java.util.Arrays;
import java.util.List;

import com.vaadin.v7.ui.CheckBox;
import com.vaadin.v7.ui.ComboBox;
import com.vaadin.v7.ui.OptionGroup;
import com.vaadin.v7.ui.TextField;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.region.CommunityReferenceDto;
import de.symeda.sormas.api.region.DistrictReferenceDto;
import de.symeda.sormas.api.region.RegionReferenceDto;
import de.symeda.sormas.api.user.UserReferenceDto;
import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.ui.utils.AbstractEditForm;
import de.symeda.sormas.ui.utils.FieldHelper;

public class BulkCaseDataForm extends AbstractEditForm<CaseBulkEditData> {
	
	private static final long serialVersionUID = 1L;
	
	private static final String DISEASE_CHECKBOX = "diseaseCheckbox";
	private static final String CLASSIFICATION_CHECKBOX = "classificationCheckbox";
	private static final String INVESTIGATION_STATUS_CHECKBOX = "investigationStatusCheckbox";
	private static final String OUTCOME_CHECKBOX = "outcomeCheckbox";
	private static final String SURVEILLANCE_OFFICER_CHECKBOX = "surveillanceOfficerCheckbox";
	private static final String HEALTH_FACILITY_CHECKBOX = "healthFacilityCheckbox";

	private static final String HTML_LAYOUT = 
			fluidRowLocsCss(VSPACE_4, DISEASE_CHECKBOX) +
			fluidRow(
					fluidColumnLoc(6, 0, CaseDataDto.DISEASE),
					fluidColumn(6, 0,locs(
							CaseDataDto.DISEASE_DETAILS, 
							CaseDataDto.PLAGUE_TYPE,
							CaseDataDto.DENGUE_FEVER_TYPE, 
							CaseDataDto.RABIES_TYPE))) +
			fluidRowLocsCss(VSPACE_4, CLASSIFICATION_CHECKBOX) +
			fluidRowLocs(CaseBulkEditData.CASE_CLASSIFICATION) +
			fluidRowLocsCss(VSPACE_4, INVESTIGATION_STATUS_CHECKBOX) +
			fluidRowLocs(CaseBulkEditData.INVESTIGATION_STATUS) +
			fluidRowLocsCss(VSPACE_4, OUTCOME_CHECKBOX) +
			fluidRowLocs(CaseBulkEditData.OUTCOME) +
			fluidRowLocsCss(VSPACE_4, SURVEILLANCE_OFFICER_CHECKBOX) +
			fluidRowLocs(CaseBulkEditData.SURVEILLANCE_OFFICER, "") +
			fluidRowLocsCss(VSPACE_4, HEALTH_FACILITY_CHECKBOX) +
			fluidRowLocs(
					CaseBulkEditData.REGION, 
					CaseBulkEditData.DISTRICT, 
					CaseBulkEditData.COMMUNITY,
					CaseBulkEditData.HEALTH_FACILITY);

	private final DistrictReferenceDto singleSelectedDistrict;

	private boolean initialized = false;

	private CheckBox diseaseCheckBox;
	private CheckBox classificationCheckBox;
	private CheckBox investigationStatusCheckBox;
	private CheckBox outcomeCheckBox;
	private CheckBox surveillanceOfficerCheckBox;
	private CheckBox healthFacilityCheckbox;

	public BulkCaseDataForm(DistrictReferenceDto singleSelectedDistrict) {
		super(CaseBulkEditData.class, CaseDataDto.I18N_PREFIX);
		this.singleSelectedDistrict = singleSelectedDistrict;
		setWidth(680, Unit.PIXELS);
		hideValidationUntilNextCommit();
		initialized = true;
		addFields();
	}

	@Override
	protected void addFields() {
		if (!initialized) {
			return;
		}

		diseaseCheckBox = new CheckBox(I18nProperties.getCaption(Captions.bulkDisease));
		getContent().addComponent(diseaseCheckBox, DISEASE_CHECKBOX);
		ComboBox disease = addDiseaseField(CaseDataDto.DISEASE, false);
		disease.setEnabled(false);
		addField(CaseDataDto.DISEASE_DETAILS, TextField.class);
		addField(CaseDataDto.PLAGUE_TYPE, OptionGroup.class);
		addField(CaseDataDto.DENGUE_FEVER_TYPE, OptionGroup.class);
		addField(CaseDataDto.RABIES_TYPE, OptionGroup.class);

		if (isVisibleAllowed(CaseDataDto.DISEASE_DETAILS)) {
			FieldHelper.setVisibleWhen(getFieldGroup(), Arrays.asList(CaseDataDto.DISEASE_DETAILS), CaseDataDto.DISEASE,
					Arrays.asList(Disease.OTHER), true);
			FieldHelper.setRequiredWhen(getFieldGroup(), CaseDataDto.DISEASE,
					Arrays.asList(CaseDataDto.DISEASE_DETAILS), Arrays.asList(Disease.OTHER));
		}
		if (isVisibleAllowed(CaseDataDto.PLAGUE_TYPE)) {
			FieldHelper.setVisibleWhen(getFieldGroup(), Arrays.asList(CaseDataDto.PLAGUE_TYPE), CaseDataDto.DISEASE,
					Arrays.asList(Disease.PLAGUE), true);
		}
		if (isVisibleAllowed(CaseDataDto.DENGUE_FEVER_TYPE)) {
			FieldHelper.setVisibleWhen(getFieldGroup(), Arrays.asList(CaseDataDto.DENGUE_FEVER_TYPE),
					CaseDataDto.DISEASE, Arrays.asList(Disease.DENGUE), true);
		}
		if (isVisibleAllowed(CaseDataDto.RABIES_TYPE)) {
			FieldHelper.setVisibleWhen(getFieldGroup(), Arrays.asList(CaseDataDto.RABIES_TYPE), CaseDataDto.DISEASE,
					Arrays.asList(Disease.RABIES), true);
		}

		classificationCheckBox = new CheckBox(I18nProperties.getCaption(Captions.bulkCaseClassification));
		getContent().addComponent(classificationCheckBox, CLASSIFICATION_CHECKBOX);
		investigationStatusCheckBox = new CheckBox(I18nProperties.getCaption(Captions.bulkInvestigationStatus));
		getContent().addComponent(investigationStatusCheckBox, INVESTIGATION_STATUS_CHECKBOX);
		outcomeCheckBox = new CheckBox(I18nProperties.getCaption(Captions.bulkCaseOutcome));
		getContent().addComponent(outcomeCheckBox, OUTCOME_CHECKBOX);
		OptionGroup caseClassification = addField(CaseBulkEditData.CASE_CLASSIFICATION, OptionGroup.class);
		caseClassification.setEnabled(false);
		OptionGroup investigationStatus = addField(CaseBulkEditData.INVESTIGATION_STATUS, OptionGroup.class);
		investigationStatus.setEnabled(false);
		OptionGroup outcome = addField(CaseBulkEditData.OUTCOME, OptionGroup.class);
		outcome.setEnabled(false);

		if (singleSelectedDistrict != null) {
			surveillanceOfficerCheckBox = new CheckBox(I18nProperties.getCaption(Captions.bulkSurveillanceOfficer));
			getContent().addComponent(surveillanceOfficerCheckBox, SURVEILLANCE_OFFICER_CHECKBOX);
			ComboBox surveillanceOfficer = addField(CaseBulkEditData.SURVEILLANCE_OFFICER, ComboBox.class);
			surveillanceOfficer.setEnabled(false);
			FieldHelper.addSoftRequiredStyleWhen(getFieldGroup(), surveillanceOfficerCheckBox,
					Arrays.asList(CaseBulkEditData.SURVEILLANCE_OFFICER), Arrays.asList(true), null);
			List<UserReferenceDto> assignableSurveillanceOfficers = FacadeProvider.getUserFacade()
					.getUserRefsByDistrict(singleSelectedDistrict, false, UserRole.SURVEILLANCE_OFFICER);
			FieldHelper.updateItems(surveillanceOfficer, assignableSurveillanceOfficers);

			surveillanceOfficerCheckBox.addValueChangeListener(e -> {
				surveillanceOfficer.setEnabled((boolean) e.getProperty().getValue());
			});
		}

		healthFacilityCheckbox = new CheckBox(I18nProperties.getCaption(Captions.bulkHealthFacility));
		getContent().addComponent(healthFacilityCheckbox, HEALTH_FACILITY_CHECKBOX);

		ComboBox region = addInfrastructureField(CaseBulkEditData.REGION);
		region.setEnabled(false);
		ComboBox district = addInfrastructureField(CaseBulkEditData.DISTRICT);
		district.setEnabled(false);
		ComboBox community = addInfrastructureField(CaseBulkEditData.COMMUNITY);
		community.setNullSelectionAllowed(true);
		community.setEnabled(false);
		ComboBox healthFacility = addInfrastructureField(CaseBulkEditData.HEALTH_FACILITY);
		healthFacility.setImmediate(true);
		healthFacility.setEnabled(false);

		region.addValueChangeListener(e -> {
			RegionReferenceDto regionDto = (RegionReferenceDto) e.getProperty().getValue();
			FieldHelper.updateItems(district,
					regionDto != null ? FacadeProvider.getDistrictFacade().getAllActiveByRegion(regionDto.getUuid())
							: null);
		});
		district.addValueChangeListener(e -> {
			if (community.getValue() == null) {
				FieldHelper.removeItems(healthFacility);
			}
			FieldHelper.removeItems(community);
			DistrictReferenceDto districtDto = (DistrictReferenceDto) e.getProperty().getValue();
			FieldHelper.updateItems(community,
					districtDto != null
							? FacadeProvider.getCommunityFacade().getAllActiveByDistrict(districtDto.getUuid())
							: null);
			FieldHelper.updateItems(healthFacility,
					districtDto != null
							? FacadeProvider.getFacilityFacade().getActiveHealthFacilitiesByDistrict(districtDto, false)
							: null);
		});
		community.addValueChangeListener(e -> {
			FieldHelper.removeItems(healthFacility);
			CommunityReferenceDto communityDto = (CommunityReferenceDto) e.getProperty().getValue();
			FieldHelper.updateItems(healthFacility, communityDto != null
					? FacadeProvider.getFacilityFacade().getActiveHealthFacilitiesByCommunity(communityDto, false)
					: district.getValue() != null ? FacadeProvider.getFacilityFacade()
							.getActiveHealthFacilitiesByDistrict((DistrictReferenceDto) district.getValue(), false)
							: null);
		});
		region.addItems(FacadeProvider.getRegionFacade().getAllActiveAsReference());

		FieldHelper.setRequiredWhen(getFieldGroup(), diseaseCheckBox, Arrays.asList(CaseBulkEditData.DISEASE),
				Arrays.asList(true));
		FieldHelper.setRequiredWhen(getFieldGroup(), classificationCheckBox,
				Arrays.asList(CaseBulkEditData.CASE_CLASSIFICATION), Arrays.asList(true));
		FieldHelper.setRequiredWhen(getFieldGroup(), investigationStatusCheckBox,
				Arrays.asList(CaseBulkEditData.INVESTIGATION_STATUS), Arrays.asList(true));
		FieldHelper.setRequiredWhen(getFieldGroup(), outcomeCheckBox, Arrays.asList(CaseBulkEditData.OUTCOME),
				Arrays.asList(true));
		FieldHelper.setRequiredWhen(getFieldGroup(), healthFacilityCheckbox,
				Arrays.asList(CaseBulkEditData.REGION, CaseBulkEditData.DISTRICT, CaseBulkEditData.HEALTH_FACILITY),
				Arrays.asList(true));

		diseaseCheckBox.addValueChangeListener(e -> {
			disease.setEnabled((boolean) e.getProperty().getValue());
		});
		classificationCheckBox.addValueChangeListener(e -> {
			caseClassification.setEnabled((boolean) e.getProperty().getValue());
		});
		investigationStatusCheckBox.addValueChangeListener(e -> {
			investigationStatus.setEnabled((boolean) e.getProperty().getValue());
		});
		outcomeCheckBox.addValueChangeListener(e -> {
			outcome.setEnabled((boolean) e.getProperty().getValue());
		});
		healthFacilityCheckbox.addValueChangeListener(e -> {
			region.setEnabled((boolean) e.getProperty().getValue());
			district.setEnabled((boolean) e.getProperty().getValue());
			community.setEnabled((boolean) e.getProperty().getValue());
			healthFacility.setEnabled((boolean) e.getProperty().getValue());
			if ((boolean) e.getProperty().getValue()) {
				FieldHelper.addSoftRequiredStyle(community);
			} else {
				FieldHelper.removeSoftRequiredStyle(community);
			}
		});
	}

	@Override
	protected String createHtmlLayout() {
		return HTML_LAYOUT;
	}

	public CheckBox getDiseaseCheckBox() {
		return diseaseCheckBox;
	}

	public CheckBox getClassificationCheckBox() {
		return classificationCheckBox;
	}

	public CheckBox getInvestigationStatusCheckBox() {
		return investigationStatusCheckBox;
	}

	public CheckBox getOutcomeCheckBox() {
		return outcomeCheckBox;
	}

	public CheckBox getSurveillanceOfficerCheckBox() {
		return surveillanceOfficerCheckBox;
	}

	public CheckBox getHealthFacilityCheckbox() {
		return healthFacilityCheckbox;
	}
}
