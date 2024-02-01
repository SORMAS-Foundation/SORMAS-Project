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

package de.symeda.sormas.ui.immunization.components.form;

import static de.symeda.sormas.ui.utils.CssStyles.ERROR_COLOR_PRIMARY;
import static de.symeda.sormas.ui.utils.CssStyles.H3;
import static de.symeda.sormas.ui.utils.CssStyles.SOFT_REQUIRED;
import static de.symeda.sormas.ui.utils.CssStyles.VSPACE_3;
import static de.symeda.sormas.ui.utils.CssStyles.style;
import static de.symeda.sormas.ui.utils.LayoutUtil.fluidColumnLoc;
import static de.symeda.sormas.ui.utils.LayoutUtil.fluidRow;
import static de.symeda.sormas.ui.utils.LayoutUtil.fluidRowLocs;

import java.util.Collections;

import com.vaadin.ui.Label;
import com.vaadin.v7.ui.CheckBox;
import com.vaadin.v7.ui.ComboBox;
import com.vaadin.v7.ui.DateField;
import com.vaadin.v7.ui.Field;
import com.vaadin.v7.ui.TextField;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.feature.FeatureType;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.i18n.Validations;
import de.symeda.sormas.api.immunization.ImmunizationDto;
import de.symeda.sormas.api.immunization.ImmunizationManagementStatus;
import de.symeda.sormas.api.immunization.ImmunizationStatus;
import de.symeda.sormas.api.immunization.MeansOfImmunization;
import de.symeda.sormas.api.infrastructure.community.CommunityReferenceDto;
import de.symeda.sormas.api.infrastructure.district.DistrictReferenceDto;
import de.symeda.sormas.api.infrastructure.facility.FacilityDto;
import de.symeda.sormas.api.infrastructure.facility.FacilityReferenceDto;
import de.symeda.sormas.api.infrastructure.facility.FacilityType;
import de.symeda.sormas.api.infrastructure.facility.FacilityTypeGroup;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.person.PersonReferenceDto;
import de.symeda.sormas.api.travelentry.TravelEntryDto;
import de.symeda.sormas.api.utils.fieldvisibility.FieldVisibilityCheckers;
import de.symeda.sormas.ui.UiUtil;
import de.symeda.sormas.ui.person.PersonCreateForm;
import de.symeda.sormas.ui.utils.AbstractEditForm;
import de.symeda.sormas.ui.utils.ComboBoxHelper;
import de.symeda.sormas.ui.utils.DateComparisonValidator;
import de.symeda.sormas.ui.utils.FieldHelper;
import de.symeda.sormas.ui.utils.InfrastructureFieldsHelper;
import de.symeda.sormas.ui.utils.NumberValidator;

public class ImmunizationCreationForm extends AbstractEditForm<ImmunizationDto> {

	private static final long serialVersionUID = 3618329421439620286L;

	private static final String OVERWRITE_IMMUNIZATION_MANAGEMENT_STATUS = "overwriteImmunizationManagementStatus";
	private static final String RESPONSIBLE_JURISDICTION_HEADING_LOC = "responsibleJurisdictionHeadingLoc";
	private static final String FACILITY_TYPE_GROUP_LOC = "facilityTypeGroupLoc";
	private static final String VACCINATION_HEADING_LOC = "vaccinationHeadingLoc";

	//@formatter:off
	private static final String HTML_LAYOUT = fluidRowLocs(ImmunizationDto.REPORT_DATE, ImmunizationDto.EXTERNAL_ID)
		+ fluidRowLocs(ImmunizationDto.DISEASE, ImmunizationDto.DISEASE_DETAILS)
		+ fluidRowLocs(ImmunizationDto.MEANS_OF_IMMUNIZATION, ImmunizationDto.MEANS_OF_IMMUNIZATION_DETAILS)
		+ fluidRowLocs(OVERWRITE_IMMUNIZATION_MANAGEMENT_STATUS)
		+ fluidRowLocs(ImmunizationDto.IMMUNIZATION_MANAGEMENT_STATUS, ImmunizationDto.IMMUNIZATION_STATUS)
		+ fluidRowLocs(RESPONSIBLE_JURISDICTION_HEADING_LOC)
		+ fluidRowLocs(ImmunizationDto.RESPONSIBLE_REGION, ImmunizationDto.RESPONSIBLE_DISTRICT, ImmunizationDto.RESPONSIBLE_COMMUNITY)
		+ fluidRowLocs(FACILITY_TYPE_GROUP_LOC, ImmunizationDto.FACILITY_TYPE)
		+ fluidRowLocs(ImmunizationDto.HEALTH_FACILITY, ImmunizationDto.HEALTH_FACILITY_DETAILS)
		+ fluidRowLocs(ImmunizationDto.START_DATE, ImmunizationDto.END_DATE)
		+ fluidRowLocs(ImmunizationDto.VALID_FROM, ImmunizationDto.VALID_UNTIL)
		+ fluidRowLocs(VACCINATION_HEADING_LOC)
		+ fluidRow(fluidColumnLoc(6, 0, ImmunizationDto.NUMBER_OF_DOSES))
		+ fluidRowLocs(ImmunizationDto.PERSON);
	//@formatter:on

	private PersonCreateForm personCreateForm;

	private final PersonReferenceDto personDto;
	private final Disease disease;
	private ComboBox responsibleRegion;
	private ComboBox responsibleDistrict;
	private ComboBox responsibleCommunity;

	public ImmunizationCreationForm() {
		this(null, null);
	}

	public ImmunizationCreationForm(PersonReferenceDto personDto, Disease disease) {
		super(
			ImmunizationDto.class,
			ImmunizationDto.I18N_PREFIX,
			FieldVisibilityCheckers.withCountry(FacadeProvider.getConfigFacade().getCountryLocale()));
		this.personDto = personDto;
		this.disease = disease;
		setWidth(720, Unit.PIXELS);
		hideValidationUntilNextCommit();
	}

	@Override
	protected String createHtmlLayout() {
		return HTML_LAYOUT;
	}

	@SuppressWarnings("deprecation")
	@Override
	protected void addFields() {
		addField(ImmunizationDto.REPORT_DATE, DateField.class);

		TextField externalIdField = addField(ImmunizationDto.EXTERNAL_ID, TextField.class);
		style(externalIdField, ERROR_COLOR_PRIMARY);

		ComboBox diseaseField = addDiseaseField(ImmunizationDto.DISEASE, false, true);
		addField(ImmunizationDto.DISEASE_DETAILS, TextField.class);

		ComboBox meansOfImmunizationField = addField(ImmunizationDto.MEANS_OF_IMMUNIZATION, ComboBox.class);
		addField(ImmunizationDto.MEANS_OF_IMMUNIZATION_DETAILS, TextField.class);

		CheckBox overwriteImmunizationManagementStatus = addCustomField(OVERWRITE_IMMUNIZATION_MANAGEMENT_STATUS, Boolean.class, CheckBox.class);
		overwriteImmunizationManagementStatus.addStyleName(VSPACE_3);

		ComboBox managementStatusField =
			addCustomField(ImmunizationDto.IMMUNIZATION_MANAGEMENT_STATUS, ImmunizationManagementStatus.class, ComboBox.class);
		managementStatusField.setValue(ImmunizationManagementStatus.SCHEDULED);
		managementStatusField.setEnabled(false);
		managementStatusField.setNullSelectionAllowed(false);

		ComboBox immunizationStatusField = addCustomField(ImmunizationDto.IMMUNIZATION_STATUS, ImmunizationStatus.class, ComboBox.class);
		immunizationStatusField.setValue(ImmunizationStatus.PENDING);
		immunizationStatusField.setEnabled(false);

		Label jurisdictionHeadingLabel = new Label(I18nProperties.getString(Strings.headingResponsibleJurisdiction));
		jurisdictionHeadingLabel.addStyleName(H3);
		getContent().addComponent(jurisdictionHeadingLabel, RESPONSIBLE_JURISDICTION_HEADING_LOC);

		responsibleRegion = addInfrastructureField(ImmunizationDto.RESPONSIBLE_REGION);
		responsibleRegion.setRequired(true);
		responsibleDistrict = addInfrastructureField(ImmunizationDto.RESPONSIBLE_DISTRICT);
		responsibleDistrict.setRequired(true);
		responsibleCommunity = addInfrastructureField(ImmunizationDto.RESPONSIBLE_COMMUNITY);
		responsibleCommunity.setNullSelectionAllowed(true);
		responsibleCommunity.addStyleName(SOFT_REQUIRED);

		InfrastructureFieldsHelper.initInfrastructureFields(responsibleRegion, responsibleDistrict, responsibleCommunity);

		ComboBox facilityTypeGroup = ComboBoxHelper.createComboBoxV7();
		facilityTypeGroup.setId("typeGroup");
		facilityTypeGroup.setCaption(I18nProperties.getCaption(Captions.Facility_typeGroup));
		facilityTypeGroup.setWidth(100, Unit.PERCENTAGE);
		facilityTypeGroup.addItems(FacilityTypeGroup.getAccomodationGroups());
		getContent().addComponent(facilityTypeGroup, FACILITY_TYPE_GROUP_LOC);
		ComboBox facilityType = ComboBoxHelper.createComboBoxV7();
		facilityType.setId("type");
		facilityType.setCaption(I18nProperties.getCaption(Captions.facilityType));
		facilityType.setWidth(100, Unit.PERCENTAGE);
		getContent().addComponent(facilityType, ImmunizationDto.FACILITY_TYPE);
		ComboBox facilityCombo = addInfrastructureField(ImmunizationDto.HEALTH_FACILITY);
		facilityCombo.setImmediate(true);
		TextField facilityDetails = addField(ImmunizationDto.HEALTH_FACILITY_DETAILS, TextField.class);
		facilityDetails.setVisible(false);

		DateField startDate = addField(ImmunizationDto.START_DATE, DateField.class);
		DateField endDate = addDateField(ImmunizationDto.END_DATE, DateField.class, -1);
		DateComparisonValidator.addStartEndValidators(startDate, endDate);
		DateComparisonValidator.dateFieldDependencyValidationVisibility(startDate, endDate);

		DateField validFrom = addDateField(ImmunizationDto.VALID_FROM, DateField.class, -1);
		DateField validUntil = addDateField(ImmunizationDto.VALID_UNTIL, DateField.class, -1);
		DateComparisonValidator.addStartEndValidators(validFrom, validUntil);
		DateComparisonValidator.dateFieldDependencyValidationVisibility(validFrom, validUntil);

		Field numberOfDosesField = addField(ImmunizationDto.NUMBER_OF_DOSES);
		numberOfDosesField.addValidator(new NumberValidator(I18nProperties.getValidationError(Validations.vaccineDosesFormat), 1, 10, false));
		numberOfDosesField.setVisible(false);

		personCreateForm = new PersonCreateForm(false, true, false);
		personCreateForm.setWidth(100, Unit.PERCENTAGE);
		personCreateForm.setValue(new PersonDto());
		diseaseField.addValueChangeListener(
			(ValueChangeListener) valueChangeEvent -> personCreateForm
				.updatePresentConditionEnum((Disease) valueChangeEvent.getProperty().getValue()));
		getContent().addComponent(personCreateForm, TravelEntryDto.PERSON);

		// Set initial visibilities & accesses
		initializeVisibilitiesAndAllowedVisibilities();

		setRequired(true, ImmunizationDto.REPORT_DATE, ImmunizationDto.MEANS_OF_IMMUNIZATION);

		FieldHelper.setVisibleWhen(
			getFieldGroup(),
			Collections.singletonList(ImmunizationDto.DISEASE_DETAILS),
			ImmunizationDto.DISEASE,
			Collections.singletonList(Disease.OTHER),
			true);
		FieldHelper.setRequiredWhen(
			getFieldGroup(),
			ImmunizationDto.DISEASE,
			Collections.singletonList(ImmunizationDto.DISEASE_DETAILS),
			Collections.singletonList(Disease.OTHER));

		FieldHelper.setVisibleWhen(
			getFieldGroup(),
			Collections.singletonList(ImmunizationDto.MEANS_OF_IMMUNIZATION_DETAILS),
			ImmunizationDto.MEANS_OF_IMMUNIZATION,
			Collections.singletonList(MeansOfImmunization.OTHER),
			true);

		overwriteImmunizationManagementStatus.addValueChangeListener(e -> {
			boolean selectedValue = (boolean) e.getProperty().getValue();
			if (!selectedValue) {
				managementStatusField.setValue(ImmunizationManagementStatus.SCHEDULED);
			}
			managementStatusField.setEnabled(selectedValue);
		});

		meansOfImmunizationField.addValueChangeListener(e -> {
			MeansOfImmunization meansOfImmunization = (MeansOfImmunization) e.getProperty().getValue();
			if (MeansOfImmunization.RECOVERY.equals(meansOfImmunization) || MeansOfImmunization.OTHER.equals(meansOfImmunization)) {
				managementStatusField.setValue(ImmunizationManagementStatus.COMPLETED);
			} else {
				managementStatusField.setValue(ImmunizationManagementStatus.SCHEDULED);
			}
			boolean isVaccinationVisible =
				MeansOfImmunization.VACCINATION.equals(meansOfImmunization) || MeansOfImmunization.VACCINATION_RECOVERY.equals(meansOfImmunization);
			numberOfDosesField.setVisible(isVaccinationVisible);
			if (!isVaccinationVisible) {
				numberOfDosesField.setValue(null);
			}
		});

		managementStatusField.addValueChangeListener(e -> {
			ImmunizationManagementStatus managementStatusValue = (ImmunizationManagementStatus) e.getProperty().getValue();
			switch (managementStatusValue) {
			case SCHEDULED:
			case ONGOING:
				immunizationStatusField.setValue(ImmunizationStatus.PENDING);
				break;
			case COMPLETED:
				immunizationStatusField.setValue(ImmunizationStatus.ACQUIRED);
				break;
			case CANCELED:
				immunizationStatusField.setValue(ImmunizationStatus.NOT_ACQUIRED);
				break;
			default:
				break;
			}
		});

		responsibleDistrict.addValueChangeListener(e -> {
			FieldHelper.removeItems(facilityCombo);
			DistrictReferenceDto districtDto = (DistrictReferenceDto) e.getProperty().getValue();
			if (districtDto != null && facilityType.getValue() != null) {
				FieldHelper.updateItems(
					facilityCombo,
					FacadeProvider.getFacilityFacade()
						.getActiveFacilitiesByDistrictAndType(districtDto, (FacilityType) facilityType.getValue(), true, false));
			}
		});

		responsibleCommunity.addValueChangeListener(e -> {
			FieldHelper.removeItems(facilityCombo);
			CommunityReferenceDto communityDto = (CommunityReferenceDto) e.getProperty().getValue();
			if (facilityType.getValue() != null) {
				FieldHelper.updateItems(
					facilityCombo,
					communityDto != null
						? FacadeProvider.getFacilityFacade()
							.getActiveFacilitiesByCommunityAndType(communityDto, (FacilityType) facilityType.getValue(), true, false)
						: responsibleDistrict.getValue() != null
							? FacadeProvider.getFacilityFacade()
								.getActiveFacilitiesByDistrictAndType(
									(DistrictReferenceDto) responsibleDistrict.getValue(),
									(FacilityType) facilityType.getValue(),
									true,
									false)
							: null);
			}
		});

		facilityTypeGroup.addValueChangeListener(e -> {
			FieldHelper.removeItems(facilityCombo);
			FieldHelper.updateEnumData(facilityType, FacilityType.getAccommodationTypes((FacilityTypeGroup) facilityTypeGroup.getValue()));
		});
		facilityType.addValueChangeListener(e -> {
			FieldHelper.removeItems(facilityCombo);
			if (facilityType.getValue() != null && responsibleDistrict.getValue() != null) {
				if (responsibleCommunity.getValue() != null) {
					FieldHelper.updateItems(
						facilityCombo,
						FacadeProvider.getFacilityFacade()
							.getActiveFacilitiesByCommunityAndType(
								(CommunityReferenceDto) responsibleCommunity.getValue(),
								(FacilityType) facilityType.getValue(),
								true,
								false));
				} else {
					FieldHelper.updateItems(
						facilityCombo,
						FacadeProvider.getFacilityFacade()
							.getActiveFacilitiesByDistrictAndType(
								(DistrictReferenceDto) responsibleDistrict.getValue(),
								(FacilityType) facilityType.getValue(),
								true,
								false));
				}
			}
		});

		facilityCombo.addValueChangeListener(e -> {
			updateFacilityFields(facilityCombo, facilityDetails);
			this.getValue().setFacilityType((FacilityType) facilityType.getValue());
		});

		addValueChangeListener(e -> {
			if (disease != null) {
				setVisible(false, ImmunizationDto.DISEASE, ImmunizationDto.DISEASE_DETAILS);
				setReadOnly(false, ImmunizationDto.DISEASE, ImmunizationDto.DISEASE_DETAILS);
			} else {
				setRequired(true, ImmunizationDto.DISEASE);
			}
			if (personDto != null) {
				personCreateForm.setVisible(false);
				personCreateForm.setReadOnly(false);
			} else {
				personCreateForm.enablePersonFields(true);
			}
		});
	}

	private void updateFacilityFields(ComboBox cbFacility, TextField tfFacilityDetails) {

		if (cbFacility.getValue() != null) {
			boolean otherHealthFacility = ((FacilityReferenceDto) cbFacility.getValue()).getUuid().equals(FacilityDto.OTHER_FACILITY_UUID);
			boolean noneHealthFacility = ((FacilityReferenceDto) cbFacility.getValue()).getUuid().equals(FacilityDto.NONE_FACILITY_UUID);
			boolean visibleAndRequired = otherHealthFacility || noneHealthFacility;

			tfFacilityDetails.setVisible(visibleAndRequired);
			tfFacilityDetails.setRequired(otherHealthFacility);

			if (otherHealthFacility) {
				tfFacilityDetails.setCaption(I18nProperties.getPrefixCaption(ImmunizationDto.I18N_PREFIX, ImmunizationDto.HEALTH_FACILITY_DETAILS));
			}
			if (noneHealthFacility) {
				tfFacilityDetails.setCaption(I18nProperties.getCaption(Captions.CaseData_noneHealthFacilityDetails));
			}
			if (!visibleAndRequired) {
				tfFacilityDetails.clear();
			}
		} else {
			tfFacilityDetails.setVisible(false);
			tfFacilityDetails.setRequired(false);
			tfFacilityDetails.clear();
		}
	}

	private void hideAndFillJurisdictionFields() {

		getContent().getComponent(RESPONSIBLE_JURISDICTION_HEADING_LOC).setVisible(false);
		responsibleRegion.setVisible(false);
		responsibleRegion.setValue(FacadeProvider.getRegionFacade().getDefaultInfrastructureReference());
		responsibleDistrict.setVisible(false);
		responsibleDistrict.setValue(FacadeProvider.getDistrictFacade().getDefaultInfrastructureReference());
		responsibleCommunity.setVisible(false);
		responsibleCommunity.setValue(FacadeProvider.getCommunityFacade().getDefaultInfrastructureReference());
	}

	public PersonDto getPerson() {
		PersonDto person = PersonDto.build();
		personCreateForm.transferDataToPerson(person);
		return person;
	}

	public PersonDto getSearchedPerson() {
		return personCreateForm.getSearchedPerson();
	}

	@Override
	public ImmunizationDto getValue() {
		ImmunizationDto immunizationDto = super.getValue();
		immunizationDto
			.setImmunizationManagementStatus((ImmunizationManagementStatus) getField(ImmunizationDto.IMMUNIZATION_MANAGEMENT_STATUS).getValue());
		immunizationDto.setImmunizationStatus((ImmunizationStatus) getField(ImmunizationDto.IMMUNIZATION_STATUS).getValue());
		return immunizationDto;
	}

	@Override
	protected void setInternalValue(ImmunizationDto newValue) {
		super.setInternalValue(newValue);

		if (UiUtil.enabled(FeatureType.HIDE_JURISDICTION_FIELDS)) {
			hideAndFillJurisdictionFields();
		}
	}
}
