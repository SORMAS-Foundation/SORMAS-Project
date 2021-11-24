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
package de.symeda.sormas.ui.hospitalization;

import static de.symeda.sormas.ui.utils.LayoutUtil.fluidRowLocs;
import static de.symeda.sormas.ui.utils.LayoutUtil.loc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.vaadin.v7.data.util.converter.Converter;
import com.vaadin.v7.ui.ComboBox;
import com.vaadin.v7.ui.DateField;
import com.vaadin.v7.ui.TextArea;
import com.vaadin.v7.ui.TextField;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.hospitalization.HospitalizationDto;
import de.symeda.sormas.api.hospitalization.HospitalizationReasonType;
import de.symeda.sormas.api.hospitalization.PreviousHospitalizationDto;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Validations;
import de.symeda.sormas.api.infrastructure.community.CommunityReferenceDto;
import de.symeda.sormas.api.infrastructure.district.DistrictReferenceDto;
import de.symeda.sormas.api.infrastructure.facility.FacilityDto;
import de.symeda.sormas.api.infrastructure.facility.FacilityReferenceDto;
import de.symeda.sormas.api.infrastructure.region.RegionReferenceDto;
import de.symeda.sormas.api.utils.YesNoUnknown;
import de.symeda.sormas.api.utils.fieldaccess.UiFieldAccessCheckers;
import de.symeda.sormas.api.utils.fieldvisibility.FieldVisibilityCheckers;
import de.symeda.sormas.ui.utils.AbstractEditForm;
import de.symeda.sormas.ui.utils.DateComparisonValidator;
import de.symeda.sormas.ui.utils.FieldHelper;
import de.symeda.sormas.ui.utils.NullableOptionGroup;

public class PreviousHospitalizationEditForm extends AbstractEditForm<PreviousHospitalizationDto> {

	private static final long serialVersionUID = 1L;
	private static final String PREVIOUS_HOSPITALIZATIONS_HEADING_LOC = "previousHospitalizationsHeadingLoc";

	private static final String HTML_LAYOUT = loc(PREVIOUS_HOSPITALIZATIONS_HEADING_LOC)
		+ fluidRowLocs(HospitalizationDto.ADMITTED_TO_HEALTH_FACILITY)
		+ fluidRowLocs(PreviousHospitalizationDto.ADMISSION_DATE, PreviousHospitalizationDto.DISCHARGE_DATE)
		+ fluidRowLocs(PreviousHospitalizationDto.REGION, PreviousHospitalizationDto.DISTRICT)
		+ fluidRowLocs(PreviousHospitalizationDto.COMMUNITY, PreviousHospitalizationDto.HEALTH_FACILITY)
		+ fluidRowLocs("", PreviousHospitalizationDto.HEALTH_FACILITY_DETAILS)
		+ fluidRowLocs(PreviousHospitalizationDto.ISOLATED, PreviousHospitalizationDto.ISOLATION_DATE)
		+ fluidRowLocs(PreviousHospitalizationDto.HOSPITALIZATION_REASON, PreviousHospitalizationDto.OTHER_HOSPITALIZATION_REASON)
		+ fluidRowLocs(
			PreviousHospitalizationDto.INTENSIVE_CARE_UNIT,
			PreviousHospitalizationDto.INTENSIVE_CARE_UNIT_START,
			PreviousHospitalizationDto.INTENSIVE_CARE_UNIT_END)
		+ fluidRowLocs(PreviousHospitalizationDto.DESCRIPTION);

	private NullableOptionGroup intensiveCareUnit;
	private DateField intensiveCareUnitStart;
	private DateField intensiveCareUnitEnd;

	private ComboBox regionCombo;
	private ComboBox districtCombo;
	private ComboBox healthFacilityCombo;

	private final RegionReferenceDto unknownRegion;
	private final DistrictReferenceDto unknownDistrict;
	private final FacilityReferenceDto unknownFacility;

	private final boolean create;

	public PreviousHospitalizationEditForm(
		boolean create,
		FieldVisibilityCheckers fieldVisibilityCheckers,
		UiFieldAccessCheckers fieldAccessCheckers) {
		super(PreviousHospitalizationDto.class, PreviousHospitalizationDto.I18N_PREFIX, false, fieldVisibilityCheckers, fieldAccessCheckers);
		this.create = create;
		unknownRegion = new RegionReferenceDto("uuid-unknown-region", I18nProperties.getCaption(Captions.unknown), null);
		unknownDistrict = new DistrictReferenceDto("uuid-unknown-district", I18nProperties.getCaption(Captions.unknown), null);
		unknownFacility = new FacilityReferenceDto("uuid-unknown-facility", I18nProperties.getCaption(Captions.unknown), null);

		setWidth(540, Unit.PIXELS);

		addFields();

		if (create) {
			hideValidationUntilNextCommit();
		}
	}

	@Override
	protected void addFields() {
		addField(PreviousHospitalizationDto.ADMITTED_TO_HEALTH_FACILITY, NullableOptionGroup.class);

		DateField admissionDate = addField(PreviousHospitalizationDto.ADMISSION_DATE, DateField.class);
		DateField dischargeDate = addField(PreviousHospitalizationDto.DISCHARGE_DATE, DateField.class);
		addField(PreviousHospitalizationDto.ISOLATED, NullableOptionGroup.class);
		addField(PreviousHospitalizationDto.ISOLATION_DATE);
		addField(PreviousHospitalizationDto.DESCRIPTION, TextArea.class).setRows(4);

		regionCombo = addInfrastructureField(PreviousHospitalizationDto.REGION);
		districtCombo = addInfrastructureField(PreviousHospitalizationDto.DISTRICT);
		ComboBox facilityCommunity = addInfrastructureField(PreviousHospitalizationDto.COMMUNITY);
		facilityCommunity.setNullSelectionAllowed(true);
		healthFacilityCombo = addInfrastructureField(PreviousHospitalizationDto.HEALTH_FACILITY);
		TextField healthFacilityDetails = addField(CaseDataDto.HEALTH_FACILITY_DETAILS, TextField.class);
		healthFacilityDetails.setVisible(false);

		addField(PreviousHospitalizationDto.HOSPITALIZATION_REASON);
		addField(PreviousHospitalizationDto.OTHER_HOSPITALIZATION_REASON, TextField.class);

		intensiveCareUnit = addField(PreviousHospitalizationDto.INTENSIVE_CARE_UNIT, NullableOptionGroup.class);
		intensiveCareUnitStart = addField(PreviousHospitalizationDto.INTENSIVE_CARE_UNIT_START, DateField.class);
		intensiveCareUnitStart.setVisible(false);
		intensiveCareUnitEnd = addField(PreviousHospitalizationDto.INTENSIVE_CARE_UNIT_END, DateField.class);
		intensiveCareUnitEnd.setVisible(false);
		FieldHelper
			.setVisibleWhen(intensiveCareUnit, Arrays.asList(intensiveCareUnitStart, intensiveCareUnitEnd), Arrays.asList(YesNoUnknown.YES), true);

		healthFacilityCombo.setImmediate(true);

		initializeAccessAndAllowedAccesses();

		if (isVisibleAllowed(PreviousHospitalizationDto.ISOLATION_DATE)) {
			FieldHelper.setVisibleWhen(
				getFieldGroup(),
				PreviousHospitalizationDto.ISOLATION_DATE,
				PreviousHospitalizationDto.ISOLATED,
				Arrays.asList(YesNoUnknown.YES),
				true);
		}
		FieldHelper.setVisibleWhen(
			getFieldGroup(),
			PreviousHospitalizationDto.OTHER_HOSPITALIZATION_REASON,
			PreviousHospitalizationDto.HOSPITALIZATION_REASON,
			Collections.singletonList(HospitalizationReasonType.OTHER),
			true);

		regionCombo.addValueChangeListener(e -> {
			RegionReferenceDto regionDto = (RegionReferenceDto) e.getProperty().getValue();
			boolean isEmpty = regionDto == null || regionDto.equals(unknownRegion);

			FieldHelper.removeItems(districtCombo);
			FieldHelper.updateItems(
				districtCombo,
				addUnknown(isEmpty ? null : FacadeProvider.getDistrictFacade().getAllActiveByRegion(regionDto.getUuid()), unknownDistrict));
		});
		districtCombo.addValueChangeListener(e -> {
			DistrictReferenceDto districtDto = (DistrictReferenceDto) e.getProperty().getValue();
			boolean isEmpty = districtDto == null || districtDto.equals(unknownDistrict);

			FieldHelper.removeItems(healthFacilityCombo);

			FieldHelper
				.updateItems(facilityCommunity, isEmpty ? null : FacadeProvider.getCommunityFacade().getAllActiveByDistrict(districtDto.getUuid()));
			FieldHelper.updateItems(
				healthFacilityCombo,
				addUnknown(isEmpty ? null : FacadeProvider.getFacilityFacade().getActiveHospitalsByDistrict(districtDto, true), unknownFacility));
		});
		facilityCommunity.addValueChangeListener(e -> {
			CommunityReferenceDto communityDto = (CommunityReferenceDto) e.getProperty().getValue();
			DistrictReferenceDto district = (DistrictReferenceDto) districtCombo.getValue();
			boolean isDistrictEmpty = district == null || district.equals(unknownDistrict);

			if (unknownFacility.equals(healthFacilityCombo.getValue())) {
				FieldHelper.removeItems(healthFacilityCombo);
			}

			FieldHelper.updateItems(
				healthFacilityCombo,
				addUnknown(
					communityDto != null
						? FacadeProvider.getFacilityFacade().getActiveHospitalsByCommunity(communityDto, true)
						: isDistrictEmpty ? null : FacadeProvider.getFacilityFacade().getActiveHospitalsByDistrict(district, true),
					unknownFacility));
		});
		regionCombo.addItems(addUnknown(FacadeProvider.getRegionFacade().getAllActiveByServerCountry(), unknownRegion));

		healthFacilityCombo.addValueChangeListener(e -> {
			if (e.getProperty().getValue() != null) {
				boolean otherHealthFacility = ((FacilityReferenceDto) e.getProperty().getValue()).getUuid().equals(FacilityDto.OTHER_FACILITY_UUID);
				boolean noneHealthFacility = ((FacilityReferenceDto) e.getProperty().getValue()).getUuid().equals(FacilityDto.NONE_FACILITY_UUID);
				boolean visibleAndRequired = otherHealthFacility || noneHealthFacility;

				healthFacilityDetails.setVisible(visibleAndRequired);
				healthFacilityDetails.setRequired(visibleAndRequired);

				if (otherHealthFacility) {
					healthFacilityDetails.setCaption(I18nProperties.getPrefixCaption(CaseDataDto.I18N_PREFIX, CaseDataDto.HEALTH_FACILITY_DETAILS));
				}
				if (noneHealthFacility) {
					healthFacilityDetails.setCaption(I18nProperties.getCaption(Captions.CaseData_noneHealthFacilityDetails));
				}
				if (!visibleAndRequired) {
					healthFacilityDetails.clear();
				}
			} else {
				healthFacilityDetails.setVisible(false);
				healthFacilityDetails.setRequired(false);
				healthFacilityDetails.clear();
			}
		});

		// Validations
		admissionDate.addValidator(
			new DateComparisonValidator(
				admissionDate,
				dischargeDate,
				true,
				false,
				I18nProperties.getValidationError(Validations.beforeDate, admissionDate.getCaption(), dischargeDate.getCaption())));
		dischargeDate.addValidator(
			new DateComparisonValidator(
				dischargeDate,
				admissionDate,
				false,
				false,
				I18nProperties.getValidationError(Validations.afterDate, dischargeDate.getCaption(), admissionDate.getCaption())));
		intensiveCareUnitStart.addValidator(
			new DateComparisonValidator(
				intensiveCareUnitStart,
				admissionDate,
				false,
				true,
				I18nProperties.getValidationError(Validations.afterDate, intensiveCareUnitStart.getCaption(), admissionDate.getCaption())));
		intensiveCareUnitStart.addValidator(
			new DateComparisonValidator(
				intensiveCareUnitStart,
				intensiveCareUnitEnd,
				true,
				true,
				I18nProperties.getValidationError(Validations.beforeDate, intensiveCareUnitStart.getCaption(), intensiveCareUnitEnd.getCaption())));
		intensiveCareUnitStart.addValidator(
			new DateComparisonValidator(
				intensiveCareUnitStart,
				dischargeDate,
				true,
				true,
				I18nProperties.getValidationError(Validations.beforeDate, intensiveCareUnitStart.getCaption(), dischargeDate.getCaption())));
		intensiveCareUnitEnd.addValidator(
			new DateComparisonValidator(
				intensiveCareUnitEnd,
				intensiveCareUnitStart,
				false,
				true,
				I18nProperties.getValidationError(Validations.afterDate, intensiveCareUnitEnd.getCaption(), intensiveCareUnitStart.getCaption())));
		intensiveCareUnitEnd.addValidator(
			new DateComparisonValidator(
				intensiveCareUnitEnd,
				dischargeDate,
				true,
				true,
				I18nProperties.getValidationError(Validations.beforeDate, intensiveCareUnitEnd.getCaption(), dischargeDate.getCaption())));

		FieldHelper.addSoftRequiredStyle(admissionDate, dischargeDate, facilityCommunity, healthFacilityDetails);

		if (isEditableAllowed(PreviousHospitalizationDto.HEALTH_FACILITY)) {
			setRequired(true, PreviousHospitalizationDto.REGION, PreviousHospitalizationDto.DISTRICT, PreviousHospitalizationDto.HEALTH_FACILITY);
		} else {
			setReadOnly(true, PreviousHospitalizationDto.REGION, PreviousHospitalizationDto.DISTRICT);
		}
	}

	@Override
	protected String createHtmlLayout() {
		return HTML_LAYOUT;
	}

	private <T> List<T> addUnknown(List<T> items, T unknownItem) {
		List<T> withUnknown = new ArrayList<>();

		if (items != null) {
			withUnknown.addAll(items);
		}

		withUnknown.add(unknownItem);

		return withUnknown;
	}

	@Override
	protected PreviousHospitalizationDto getInternalValue() {
		PreviousHospitalizationDto internalValue = super.getInternalValue();

		if (internalValue != null) {
			if (unknownRegion.equals(internalValue.getRegion())) {
				internalValue.setRegion(null);
			}
			if (unknownDistrict.equals(internalValue.getDistrict())) {
				internalValue.setDistrict(null);
			}
			if (unknownFacility.equals(internalValue.getHealthFacility())) {
				internalValue.setHealthFacility(null);
			}
		}

		return internalValue;
	}

	@Override
	public void setValue(PreviousHospitalizationDto newFieldValue) throws ReadOnlyException, Converter.ConversionException {
		super.setValue(newFieldValue);

		if (!create && newFieldValue != null) {
			if (newFieldValue.getRegion() == null) {
				regionCombo.setValue(unknownRegion);
			}
			if (newFieldValue.getDistrict() == null) {
				districtCombo.setValue(unknownDistrict);
			}
			if (newFieldValue.getHealthFacility() == null) {
				healthFacilityCombo.setValue(unknownFacility);
			}
		}
	}
}
