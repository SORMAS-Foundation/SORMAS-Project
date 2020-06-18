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

import com.vaadin.v7.ui.ComboBox;
import com.vaadin.v7.ui.DateField;
import com.vaadin.v7.ui.OptionGroup;
import com.vaadin.v7.ui.TextArea;
import com.vaadin.v7.ui.TextField;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.facility.FacilityDto;
import de.symeda.sormas.api.facility.FacilityReferenceDto;
import de.symeda.sormas.api.hospitalization.PreviousHospitalizationDto;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Validations;
import de.symeda.sormas.api.region.CommunityReferenceDto;
import de.symeda.sormas.api.region.DistrictReferenceDto;
import de.symeda.sormas.api.region.RegionReferenceDto;
import de.symeda.sormas.api.utils.fieldvisibility.FieldVisibilityCheckers;
import de.symeda.sormas.ui.utils.AbstractEditForm;
import de.symeda.sormas.ui.utils.DateComparisonValidator;
import de.symeda.sormas.ui.utils.FieldHelper;
import de.symeda.sormas.ui.utils.UiFieldAccessCheckers;

public class PreviousHospitalizationEditForm extends AbstractEditForm<PreviousHospitalizationDto> {

	private static final long serialVersionUID = 1L;

	private static final String HTML_LAYOUT = fluidRowLocs(PreviousHospitalizationDto.ADMISSION_DATE, PreviousHospitalizationDto.DISCHARGE_DATE)
		+ fluidRowLocs(PreviousHospitalizationDto.REGION, PreviousHospitalizationDto.DISTRICT)
		+ fluidRowLocs(PreviousHospitalizationDto.COMMUNITY, PreviousHospitalizationDto.HEALTH_FACILITY)
		+ fluidRowLocs(PreviousHospitalizationDto.ISOLATED, PreviousHospitalizationDto.HEALTH_FACILITY_DETAILS)
		+ fluidRowLocs(PreviousHospitalizationDto.DESCRIPTION);

	public PreviousHospitalizationEditForm(
		boolean create,
		FieldVisibilityCheckers fieldVisibilityCheckers,
		UiFieldAccessCheckers fieldAccessCheckers) {
		super(PreviousHospitalizationDto.class, PreviousHospitalizationDto.I18N_PREFIX, true, fieldVisibilityCheckers, fieldAccessCheckers);

		setWidth(540, Unit.PIXELS);

		if (create) {
			hideValidationUntilNextCommit();
		}
	}

	@Override
	protected void addFields() {
		DateField admissionDate = addField(PreviousHospitalizationDto.ADMISSION_DATE, DateField.class);
		DateField dischargeDate = addField(PreviousHospitalizationDto.DISCHARGE_DATE, DateField.class);
		addField(PreviousHospitalizationDto.ISOLATED, OptionGroup.class);
		addField(PreviousHospitalizationDto.DESCRIPTION, TextArea.class).setRows(2);

		ComboBox facilityRegion = addInfrastructureField(PreviousHospitalizationDto.REGION);
		ComboBox facilityDistrict = addInfrastructureField(PreviousHospitalizationDto.DISTRICT);
		ComboBox facilityCommunity = addInfrastructureField(PreviousHospitalizationDto.COMMUNITY);
		facilityCommunity.setNullSelectionAllowed(true);
		ComboBox healthFacility = addInfrastructureField(PreviousHospitalizationDto.HEALTH_FACILITY);
		TextField healthFacilityDetails = addField(CaseDataDto.HEALTH_FACILITY_DETAILS, TextField.class);
		healthFacilityDetails.setVisible(false);

		healthFacility.setImmediate(true);

		initializeAccessAndAllowedAccesses();

		facilityRegion.addValueChangeListener(e -> {
			RegionReferenceDto regionDto = (RegionReferenceDto) e.getProperty().getValue();
			FieldHelper.updateItems(
				facilityDistrict,
				regionDto != null ? FacadeProvider.getDistrictFacade().getAllActiveByRegion(regionDto.getUuid()) : null);
		});
		facilityDistrict.addValueChangeListener(e -> {
			if (facilityCommunity.getValue() == null) {
				FieldHelper.removeItems(healthFacility);
			}
			FieldHelper.removeItems(facilityCommunity);
			DistrictReferenceDto districtDto = (DistrictReferenceDto) e.getProperty().getValue();
			FieldHelper.updateItems(
				facilityCommunity,
				districtDto != null ? FacadeProvider.getCommunityFacade().getAllActiveByDistrict(districtDto.getUuid()) : null);
			FieldHelper.updateItems(
				healthFacility,
				districtDto != null ? FacadeProvider.getFacilityFacade().getActiveHealthFacilitiesByDistrict(districtDto, true) : null);
		});
		facilityCommunity.addValueChangeListener(e -> {
			FieldHelper.removeItems(healthFacility);
			CommunityReferenceDto communityDto = (CommunityReferenceDto) e.getProperty().getValue();
			FieldHelper.updateItems(
				healthFacility,
				communityDto != null
					? FacadeProvider.getFacilityFacade().getActiveHealthFacilitiesByCommunity(communityDto, true)
					: facilityDistrict.getValue() != null
						? FacadeProvider.getFacilityFacade()
							.getActiveHealthFacilitiesByDistrict((DistrictReferenceDto) facilityDistrict.getValue(), true)
						: null);
		});
		facilityRegion.addItems(FacadeProvider.getRegionFacade().getAllActiveAsReference());

		healthFacility.addValueChangeListener(e -> {
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
					healthFacilityDetails
						.setCaption(I18nProperties.getPrefixCaption(CaseDataDto.I18N_PREFIX, CaseDataDto.NONE_HEALTH_FACILITY_DETAILS));
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

		FieldHelper.addSoftRequiredStyle(admissionDate, dischargeDate, facilityCommunity, healthFacilityDetails);

		if(isEditableAllowed(PreviousHospitalizationDto.HEALTH_FACILITY)) {
			setRequired(true, PreviousHospitalizationDto.REGION, PreviousHospitalizationDto.DISTRICT, PreviousHospitalizationDto.HEALTH_FACILITY);
		}
		else {
			setReadOnly(true, PreviousHospitalizationDto.REGION, PreviousHospitalizationDto.DISTRICT);
		}
	}

	@Override
	protected String createHtmlLayout() {
		return HTML_LAYOUT;
	}
}
