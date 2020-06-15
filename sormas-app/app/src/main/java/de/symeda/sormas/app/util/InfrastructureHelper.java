/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2018 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

package de.symeda.sormas.app.util;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static de.symeda.sormas.app.util.DataUtils.toItems;

import java.util.ArrayList;
import java.util.List;

import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.facility.FacilityDto;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.infrastructure.PointOfEntryDto;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.facility.Facility;
import de.symeda.sormas.app.backend.infrastructure.PointOfEntry;
import de.symeda.sormas.app.backend.region.Community;
import de.symeda.sormas.app.backend.region.District;
import de.symeda.sormas.app.backend.region.Region;
import de.symeda.sormas.app.component.Item;
import de.symeda.sormas.app.component.controls.ControlPropertyEditField;
import de.symeda.sormas.app.component.controls.ControlPropertyField;
import de.symeda.sormas.app.component.controls.ControlSpinnerField;

public final class InfrastructureHelper {

	public static List<Item> loadRegions() {
		return toItems(DatabaseHelper.getRegionDao().queryActiveForAll(Region.NAME, true));
	}

	public static List<Item> loadDistricts(Region region) {
		return toItems(region != null ? DatabaseHelper.getDistrictDao().getByRegion(region) : new ArrayList<>(), true);
	}

	public static List<Item> loadCommunities(District district) {
		return toItems(district != null ? DatabaseHelper.getCommunityDao().getByDistrict(district) : new ArrayList<>(), true);
	}

	public static List<Item> loadFacilities(District district, Community community) {
		return toItems(
			community != null
				? DatabaseHelper.getFacilityDao().getActiveHealthFacilitiesByCommunity(community, true, true)
				: district != null ? DatabaseHelper.getFacilityDao().getActiveHealthFacilitiesByDistrict(district, true, true) : new ArrayList<>(),
			true);
	}

	public static List<Item> loadPointsOfEntry(District district) {
		return toItems(district != null ? DatabaseHelper.getPointOfEntryDao().getActiveByDistrict(district, true) : new ArrayList<>(), true);
	}

	public static void initializeRegionFields(
		final ControlSpinnerField regionField,
		List<Item> initialRegions,
		Region initialRegion,
		final ControlSpinnerField districtField,
		List<Item> initialDistricts,
		District initialDistrict,
		final ControlSpinnerField communityField,
		List<Item> initialCommunities,
		Community initialCommunity) {

		Item regionItem = initialRegion != null ? DataUtils.toItem(initialRegion) : null;
		Item districtItem = initialDistrict != null ? DataUtils.toItem(initialDistrict) : null;
		Item communityItem = initialCommunity != null ? DataUtils.toItem(initialCommunity) : null;

		if (regionItem != null && !initialRegions.contains(regionItem)) {
			initialRegions.add(regionItem);
		}
		if (districtItem != null && !initialDistricts.contains(districtItem)) {
			initialDistricts.add(districtItem);
		}
		if (communityItem != null && !initialCommunities.contains(communityItem)) {
			initialCommunities.add(communityItem);
		}

		regionField.initializeSpinner(initialRegions, field -> {
			Region selectedRegion = (Region) field.getValue();
			if (selectedRegion != null) {
				List<Item> newDistricts = loadDistricts(selectedRegion);
				if (initialDistrict != null && selectedRegion.equals(initialDistrict.getRegion()) && !newDistricts.contains(districtItem)) {
					newDistricts.add(districtItem);
				}
				districtField.setSpinnerData(newDistricts, districtField.getValue());
			} else {
				districtField.setSpinnerData(null);
			}
		});

		if (communityField != null) {
			districtField.initializeSpinner(initialDistricts, field -> {
				District selectedDistrict = (District) field.getValue();
				if (selectedDistrict != null) {
					List<Item> newCommunities = loadCommunities(selectedDistrict);
					if (initialCommunity != null
						&& selectedDistrict.equals(initialCommunity.getDistrict())
						&& !newCommunities.contains(communityItem)) {
						newCommunities.add(communityItem);
					}
					communityField.setSpinnerData(newCommunities, communityField.getValue());
				} else {
					communityField.setSpinnerData(null);
				}
			});
		} else {
			districtField.initializeSpinner(initialDistricts);
		}

		if (communityField != null) {
			communityField.initializeSpinner(initialCommunities);
		}
	}

	public static void initializeFacilityFields(
		final ControlSpinnerField regionField,
		List<Item> regions,
		Region initialRegion,
		final ControlSpinnerField districtField,
		List<Item> districts,
		District initialDistrict,
		final ControlSpinnerField communityField,
		List<Item> communities,
		Community initialCommunity,
		final ControlSpinnerField facilityField,
		List<Item> facilities,
		Facility initialFacility) {
		initializeFacilityFields(
			regionField,
			regions,
			initialRegion,
			districtField,
			districts,
			initialDistrict,
			communityField,
			communities,
			initialCommunity,
			facilityField,
			facilities,
			initialFacility,
			null,
			null,
			null);
	}

	public static void initializeFacilityFields(
		final ControlSpinnerField regionField,
		List<Item> regions,
		Region initialRegion,
		final ControlSpinnerField districtField,
		List<Item> districts,
		District initialDistrict,
		final ControlSpinnerField communityField,
		List<Item> communities,
		Community initialCommunity,
		final ControlSpinnerField facilityField,
		List<Item> facilities,
		Facility initialFacility,
		final ControlSpinnerField pointOfEntryField,
		List<Item> pointsOfEntry,
		PointOfEntry initialPointOfEntry) {

		Item regionItem = initialRegion != null ? DataUtils.toItem(initialRegion) : null;
		Item districtItem = initialDistrict != null ? DataUtils.toItem(initialDistrict) : null;
		Item communityItem = initialCommunity != null ? DataUtils.toItem(initialCommunity) : null;
		Item facilityItem = initialFacility != null ? DataUtils.toItem(initialFacility) : null;
		Item pointOfEntryItem = initialPointOfEntry != null ? DataUtils.toItem(initialPointOfEntry) : null;

		if (regionItem != null && !regions.contains(regionItem)) {
			regions.add(regionItem);
		}
		if (districtItem != null && !districts.contains(districtItem)) {
			districts.add(districtItem);
		}
		if (communityItem != null && !communities.contains(communityItem)) {
			communities.add(communityItem);
		}
		if (facilityItem != null && !facilities.contains(facilityItem)) {
			facilities.add(facilityItem);
		}
		if (pointOfEntryItem != null && !pointsOfEntry.contains(pointOfEntryItem)) {
			pointsOfEntry.add(pointOfEntryItem);
		}

		regionField.initializeSpinner(regions, field -> {
			Region selectedRegion = (Region) field.getValue();
			if (selectedRegion != null) {
				List<Item> newDistricts = loadDistricts(selectedRegion);
				if (initialDistrict != null && selectedRegion.equals(initialDistrict.getRegion()) && !newDistricts.contains(districtItem)) {
					newDistricts.add(districtItem);
				}
				districtField.setSpinnerData(newDistricts, districtField.getValue());
			} else {
				districtField.setSpinnerData(null);
			}
		});

		districtField.initializeSpinner(districts, field -> {
			District selectedDistrict = (District) field.getValue();
			if (selectedDistrict != null) {
				List<Item> newCommunities = loadCommunities(selectedDistrict);
				List<Item> newFacilities = loadFacilities(selectedDistrict, null);
				if (initialCommunity != null && selectedDistrict.equals(initialCommunity.getDistrict()) && !newCommunities.contains(communityItem)) {
					newCommunities.add(communityItem);
				}
				if (initialFacility != null && selectedDistrict.equals(initialFacility.getDistrict()) && !newFacilities.contains(facilityItem)) {
					newFacilities.add(facilityItem);
				}
				communityField.setSpinnerData(newCommunities, communityField.getValue());
				facilityField.setSpinnerData(newFacilities, facilityField.getValue());
				if (pointOfEntryField != null) {
					List<Item> newPointsOfEntry = loadPointsOfEntry(selectedDistrict);
					if (initialPointOfEntry != null
						&& selectedDistrict.equals(initialPointOfEntry.getDistrict())
						&& !newPointsOfEntry.contains(pointOfEntryItem)) {
						newPointsOfEntry.add(pointOfEntryItem);
					}
					pointOfEntryField.setSpinnerData(newPointsOfEntry, pointOfEntryField.getValue());
				}
			} else {
				communityField.setSpinnerData(null);
				facilityField.setSpinnerData(null);
				if (pointOfEntryField != null) {
					pointOfEntryField.setSpinnerData(null);
				}
			}
		});

		communityField.initializeSpinner(communities, field -> {
			Community selectedCommunity = (Community) field.getValue();
			if (selectedCommunity != null) {
				List<Item> newFacilities = loadFacilities(null, selectedCommunity);
				if (initialFacility != null && selectedCommunity.equals(initialFacility.getCommunity()) && !newFacilities.contains(facilityItem)) {
					newFacilities.add(facilityItem);
				}
				facilityField.setSpinnerData(newFacilities);
			} else if (districtField.getValue() != null) {
				List<Item> newFacilities = loadFacilities((District) districtField.getValue(), null);
				if (initialFacility != null
					&& districtField.getValue().equals(initialFacility.getDistrict())
					&& !newFacilities.contains(facilityItem)) {
					newFacilities.add(facilityItem);
				}
				facilityField.setSpinnerData(newFacilities);
			} else {
				facilityField.setSpinnerData(null);
			}
		});

		facilityField.initializeSpinner(facilities);

		if (pointOfEntryField != null) {
			pointOfEntryField.initializeSpinner(pointsOfEntry);
		}
	}

	/**
	 * Hide facilityDetails when no static health facility is selected and adjust the caption based on
	 * the selected static health facility.
	 */
	public static void initializeHealthFacilityDetailsFieldVisibility(
		final ControlPropertyField healthFacilityField,
		final ControlPropertyField healthFacilityDetailsField) {
		setHealthFacilityDetailsFieldVisibility(healthFacilityField, healthFacilityDetailsField);
		healthFacilityField
			.addValueChangedListener(field -> setHealthFacilityDetailsFieldVisibility(healthFacilityField, healthFacilityDetailsField));
	}

	public static void setHealthFacilityDetailsFieldVisibility(
		ControlPropertyField healthFacilityField,
		ControlPropertyField healthFacilityDetailsField) {
		Facility selectedFacility = (Facility) healthFacilityField.getValue();

		if (selectedFacility != null) {
			boolean otherHealthFacility = selectedFacility.getUuid().equals(FacilityDto.OTHER_FACILITY_UUID);
			boolean noneHealthFacility = selectedFacility.getUuid().equals(FacilityDto.NONE_FACILITY_UUID);

			if (otherHealthFacility) {
				healthFacilityDetailsField.setVisibility(VISIBLE);
				String caption = I18nProperties.getPrefixCaption(CaseDataDto.I18N_PREFIX, CaseDataDto.HEALTH_FACILITY_DETAILS);
				healthFacilityDetailsField.setCaption(caption);
				if (healthFacilityDetailsField instanceof ControlPropertyEditField) {
					((ControlPropertyEditField) healthFacilityDetailsField).setHint(caption);
				}
			} else if (noneHealthFacility) {
				healthFacilityDetailsField.setVisibility(VISIBLE);
				String caption = I18nProperties.getPrefixCaption(CaseDataDto.I18N_PREFIX, CaseDataDto.NONE_HEALTH_FACILITY_DETAILS);
				healthFacilityDetailsField.setCaption(caption);
				if (healthFacilityDetailsField instanceof ControlPropertyEditField) {
					((ControlPropertyEditField) healthFacilityDetailsField).setHint(caption);
				}
			} else {
				healthFacilityDetailsField.setVisibility(GONE);
			}
		} else {
			healthFacilityDetailsField.setVisibility(GONE);
		}
	}

	public static void initializePointOfEntryDetailsFieldVisibility(
		final ControlPropertyField pointOfEntryField,
		final ControlPropertyField pointOfEntryDetailsField) {
		setPointOfEntryDetailsFieldVisibility(pointOfEntryField, pointOfEntryDetailsField);
		pointOfEntryField.addValueChangedListener(e -> setPointOfEntryDetailsFieldVisibility(pointOfEntryField, pointOfEntryDetailsField));
	}

	public static void setPointOfEntryDetailsFieldVisibility(
		final ControlPropertyField pointOfEntryField,
		final ControlPropertyField pointOfEntryDetailsField) {
		PointOfEntry selectedPointOfEntry = (PointOfEntry) pointOfEntryField.getValue();
		if (selectedPointOfEntry != null) {
			pointOfEntryDetailsField.setVisibility(
				selectedPointOfEntry.getUuid().equals(PointOfEntryDto.OTHER_AIRPORT_UUID)
					|| selectedPointOfEntry.getUuid().equals(PointOfEntryDto.OTHER_SEAPORT_UUID)
					|| selectedPointOfEntry.getUuid().equals(PointOfEntryDto.OTHER_GROUND_CROSSING_UUID)
					|| selectedPointOfEntry.getUuid().equals(PointOfEntryDto.OTHER_POE_UUID) ? VISIBLE : GONE);
		} else {
			pointOfEntryDetailsField.setVisibility(GONE);
		}
	}
}
