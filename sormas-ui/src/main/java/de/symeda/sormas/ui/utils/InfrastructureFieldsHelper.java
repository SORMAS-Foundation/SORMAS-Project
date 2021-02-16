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

package de.symeda.sormas.ui.utils;

import java.util.function.Supplier;

import com.vaadin.v7.ui.ComboBox;
import com.vaadin.v7.ui.TextField;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.facility.FacilityDto;
import de.symeda.sormas.api.facility.FacilityReferenceDto;
import de.symeda.sormas.api.facility.FacilityType;
import de.symeda.sormas.api.facility.FacilityTypeGroup;
import de.symeda.sormas.api.region.CommunityReferenceDto;
import de.symeda.sormas.api.region.DistrictReferenceDto;
import de.symeda.sormas.api.region.RegionReferenceDto;

public class InfrastructureFieldsHelper {

	public static void initInfrastructureFields(
		ComboBox regionCombo,
		ComboBox districtCombo,
		ComboBox communityCombo,
		ComboBox facilityTypeGroupCombo,
		ComboBox facilityTypeCombo,
		ComboBox facilityCombo,
		TextField facilityDetailsField,
		Supplier<String> facilityDetailsSupplier) {
		regionCombo.addValueChangeListener(e -> {
			RegionReferenceDto regionDto = (RegionReferenceDto) e.getProperty().getValue();
			FieldHelper
				.updateItems(districtCombo, regionDto != null ? FacadeProvider.getDistrictFacade().getAllActiveByRegion(regionDto.getUuid()) : null);
		});

		districtCombo.addValueChangeListener(e -> {
			DistrictReferenceDto districtDto = (DistrictReferenceDto) e.getProperty().getValue();

			if (communityCombo != null) {
				FieldHelper.updateItems(
					communityCombo,
					districtDto != null ? FacadeProvider.getCommunityFacade().getAllActiveByDistrict(districtDto.getUuid()) : null);
			}

			if (districtDto == null) {
				FieldHelper.removeItems(facilityCombo);
			} else if (facilityTypeCombo.getValue() != null) {
				FieldHelper.updateItems(
					facilityCombo,
					FacadeProvider.getFacilityFacade()
						.getActiveFacilitiesByDistrictAndType(districtDto, (FacilityType) facilityTypeCombo.getValue(), true, false));
			}
		});

		if (communityCombo != null) {
			communityCombo.addValueChangeListener(e -> {
				CommunityReferenceDto communityDto = (CommunityReferenceDto) e.getProperty().getValue();
				if (facilityTypeCombo.getValue() != null) {
					FieldHelper.updateItems(
						facilityCombo,
						communityDto != null
							? FacadeProvider.getFacilityFacade()
								.getActiveFacilitiesByCommunityAndType(communityDto, (FacilityType) facilityTypeCombo.getValue(), true, true)
							: districtCombo.getValue() != null
								? FacadeProvider.getFacilityFacade()
									.getActiveFacilitiesByDistrictAndType(
										(DistrictReferenceDto) districtCombo.getValue(),
										(FacilityType) facilityTypeCombo.getValue(),
										true,
										false)
								: null);
				}
			});
		}

		facilityTypeGroupCombo.addValueChangeListener(e -> {
			FieldHelper.removeItems(facilityCombo);
			FieldHelper.updateEnumData(facilityTypeCombo, FacilityType.getTypes((FacilityTypeGroup) facilityTypeGroupCombo.getValue()));
			facilityTypeCombo.setRequired(facilityTypeGroupCombo.getValue() != null);
		});
		facilityTypeCombo.addValueChangeListener(e -> {
			FieldHelper.removeItems(facilityCombo);
			if (facilityTypeCombo.getValue() != null && facilityTypeGroupCombo.getValue() == null) {
				facilityTypeGroupCombo.setValue(((FacilityType) facilityTypeCombo.getValue()).getFacilityTypeGroup());
			}
			if (facilityTypeCombo.getValue() != null && districtCombo.getValue() != null) {
				if (communityCombo != null && communityCombo.getValue() != null) {
					FieldHelper.updateItems(
						facilityCombo,
						FacadeProvider.getFacilityFacade()
							.getActiveFacilitiesByCommunityAndType(
								(CommunityReferenceDto) communityCombo.getValue(),
								(FacilityType) facilityTypeCombo.getValue(),
								true,
								false));
				} else {
					FieldHelper.updateItems(
						facilityCombo,
						FacadeProvider.getFacilityFacade()
							.getActiveFacilitiesByDistrictAndType(
								(DistrictReferenceDto) districtCombo.getValue(),
								(FacilityType) facilityTypeCombo.getValue(),
								true,
								false));
				}
			}
		});
		facilityCombo.addValueChangeListener(e -> {
			if (facilityCombo.getValue() != null) {
				boolean visibleAndRequired = isFacilityDetailsRequired(facilityCombo);

				facilityDetailsField.setVisible(visibleAndRequired);
				facilityDetailsField.setRequired(visibleAndRequired);

				if (!visibleAndRequired) {
					facilityDetailsField.clear();
				} else {
					facilityDetailsField.setValue(facilityDetailsSupplier.get());
				}
			} else {
				facilityDetailsField.setVisible(false);
				facilityDetailsField.setRequired(false);
				facilityDetailsField.clear();
			}
		});

		facilityDetailsField.setVisible(false);
		facilityDetailsField.setRequired(false);

		regionCombo.addItems(FacadeProvider.getRegionFacade().getAllActiveAsReference());
	}

	private static boolean isFacilityDetailsRequired(ComboBox facilityCombo) {
		return facilityCombo.getValue() != null
			&& ((FacilityReferenceDto) facilityCombo.getValue()).getUuid().equals(FacilityDto.OTHER_FACILITY_UUID);
	}
}
