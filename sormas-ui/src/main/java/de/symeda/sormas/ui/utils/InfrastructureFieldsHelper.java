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

import java.util.function.Consumer;
import java.util.function.Supplier;

import javax.annotation.Nullable;

import com.vaadin.v7.ui.ComboBox;
import com.vaadin.v7.ui.TextField;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.infrastructure.facility.FacilityDto;
import de.symeda.sormas.api.infrastructure.facility.FacilityReferenceDto;
import de.symeda.sormas.api.infrastructure.facility.FacilityType;
import de.symeda.sormas.api.infrastructure.facility.FacilityTypeGroup;
import de.symeda.sormas.api.infrastructure.community.CommunityReferenceDto;
import de.symeda.sormas.api.infrastructure.country.CountryReferenceDto;
import de.symeda.sormas.api.infrastructure.district.DistrictReferenceDto;
import de.symeda.sormas.api.infrastructure.region.RegionReferenceDto;

public class InfrastructureFieldsHelper {

	public static void initInfrastructureFields(ComboBox regionCombo, ComboBox districtCombo, ComboBox communityCombo) {
		initInfrastructureFields(regionCombo, districtCombo, communityCombo, null, null, null, null, null);
	}

	public static void initInfrastructureFields(
		ComboBox regionCombo,
		ComboBox districtCombo,
		ComboBox communityCombo,
		@Nullable ComboBox facilityTypeGroupCombo,
		@Nullable ComboBox facilityTypeCombo,
		@Nullable ComboBox facilityCombo,
		@Nullable TextField facilityDetailsField,
		@Nullable Supplier<String> facilityDetailsSupplier) {
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

			if (facilityCombo != null) {
				if (districtDto == null) {
					FieldHelper.removeItems(facilityCombo);
				} else if (facilityTypeCombo != null && facilityTypeCombo.getValue() != null) {
					FieldHelper.updateItems(
						facilityCombo,
						FacadeProvider.getFacilityFacade()
							.getActiveFacilitiesByDistrictAndType(districtDto, (FacilityType) facilityTypeCombo.getValue(), true, false));
				}
			}
		});

		if (communityCombo != null && facilityCombo != null) {
			communityCombo.addValueChangeListener(e -> {
				CommunityReferenceDto communityDto = (CommunityReferenceDto) e.getProperty().getValue();
				FacilityType facilityType = facilityTypeCombo != null ? (FacilityType) facilityTypeCombo.getValue() : null;

				if (facilityType != null) {
					FieldHelper.updateItems(
						facilityCombo,
						communityDto != null
							? FacadeProvider.getFacilityFacade().getActiveFacilitiesByCommunityAndType(communityDto, facilityType, true, true)
							: districtCombo.getValue() != null
								? FacadeProvider.getFacilityFacade()
									.getActiveFacilitiesByDistrictAndType((DistrictReferenceDto) districtCombo.getValue(), facilityType, true, false)
								: null);
				}
			});
		}

		if (facilityTypeGroupCombo != null && facilityTypeCombo != null && facilityCombo != null) {
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
		}

		if (facilityCombo != null && facilityDetailsField != null) {
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
		}

		regionCombo.addItems(FacadeProvider.getRegionFacade().getAllActiveByServerCountry());
	}

	/**
	 * Updates the region combo items based on the selected country
	 *
	 * @param countryCombo
	 *            country field
	 * @param regionCombo
	 *            region field
	 * @param extraConfig
	 *            function called that accepts a boolean.
	 *            It is called with true if the server country or no country is selected in the country combo
	 */
	public static void updateAreaBasedOnCountry(ComboBox countryCombo, ComboBox areaCombo, Consumer<Boolean> extraConfig) {
		CountryReferenceDto serverCountryDto = FacadeProvider.getCountryFacade().getServerCountry();
		CountryReferenceDto countryDto = (CountryReferenceDto) countryCombo.getValue();
		boolean isNoCountryOrServerCountry = serverCountryDto == null
			? countryDto == null
			: countryDto == null || serverCountryDto.getIsoCode().equalsIgnoreCase(countryDto.getIsoCode());

		if (isNoCountryOrServerCountry) {
			FieldHelper.updateItems(areaCombo, FacadeProvider.getAreaFacade().getAllActiveAsReference());
		} else {
			FieldHelper.updateItems(areaCombo, FacadeProvider.getAreaFacade().getAllActiveAsReference()); //(countryDto.getUuid()
		}

		if (extraConfig != null) {
			extraConfig.accept(isNoCountryOrServerCountry);
		}
	}
	/*
	public static void updateRegionBasedOnCountry(ComboBox countryCombo, ComboBox regionCombo, Consumer<Boolean> extraConfig) {
		CountryReferenceDto serverCountryDto = FacadeProvider.getCountryFacade().getServerCountry();
		CountryReferenceDto countryDto = (CountryReferenceDto) countryCombo.getValue();
		boolean isNoCountryOrServerCountry = serverCountryDto == null
			? countryDto == null
			: countryDto == null || serverCountryDto.getIsoCode().equalsIgnoreCase(countryDto.getIsoCode());

		if (isNoCountryOrServerCountry) {
			FieldHelper.updateItems(regionCombo, FacadeProvider.getRegionFacade().getAllActiveByServerCountry());
		} else {
			FieldHelper.updateItems(regionCombo, FacadeProvider.getRegionFacade().getAllActiveByCountry(countryDto.getUuid()));
		}

		if (extraConfig != null) {
			extraConfig.accept(isNoCountryOrServerCountry);
		}
	}
*/
	private static boolean isFacilityDetailsRequired(ComboBox facilityCombo) {
		return facilityCombo.getValue() != null
			&& ((FacilityReferenceDto) facilityCombo.getValue()).getUuid().equals(FacilityDto.OTHER_FACILITY_UUID);
	}
}
