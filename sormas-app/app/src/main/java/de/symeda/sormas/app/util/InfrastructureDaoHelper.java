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
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.event.TypeOfPlace;
import de.symeda.sormas.api.facility.FacilityDto;
import de.symeda.sormas.api.facility.FacilityType;
import de.symeda.sormas.api.facility.FacilityTypeGroup;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.infrastructure.PointOfEntryDto;
import de.symeda.sormas.app.backend.caze.Case;
import de.symeda.sormas.app.backend.common.AbstractDomainObject;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.config.ConfigProvider;
import de.symeda.sormas.app.backend.facility.Facility;
import de.symeda.sormas.app.backend.infrastructure.PointOfEntry;
import de.symeda.sormas.app.backend.region.Area;
import de.symeda.sormas.app.backend.region.Community;
import de.symeda.sormas.app.backend.region.Continent;
import de.symeda.sormas.app.backend.region.Country;
import de.symeda.sormas.app.backend.region.District;
import de.symeda.sormas.app.backend.region.Region;
import de.symeda.sormas.app.backend.region.Subcontinent;
import de.symeda.sormas.app.component.Item;
import de.symeda.sormas.app.component.controls.ControlPropertyEditField;
import de.symeda.sormas.app.component.controls.ControlPropertyField;
import de.symeda.sormas.app.component.controls.ControlSpinnerField;
import de.symeda.sormas.app.component.controls.ControlTextEditField;
import de.symeda.sormas.app.component.controls.ValueChangeListener;

public final class InfrastructureDaoHelper {

	public static List<Item> loadContinents() {
		List<Item> items = new ArrayList<>();

		items.add(new Item<>("", null));
		items.addAll(
			DatabaseHelper.getContinentDao()
				.queryActiveForAll(Continent.DEFAULT_NAME, true)
				.stream()
				.map(c -> new Item<>(I18nProperties.getContinentName(c.getDefaultName()), c))
				.sorted(Comparator.comparing(Item::getKey))
				.collect(Collectors.toList()));
		return items;
	}

	public static List<Item> loadSubcontinents() {
		List<Item> items = new ArrayList<>();

		items.add(new Item<>("", null));
		items.addAll(mapToDisplaySubcontinentNames(DatabaseHelper.getSubcontinentDao().queryActiveForAll(Subcontinent.DEFAULT_NAME, true)));
		return items;
	}

	public static List<Item> loadSubcontinentsByContinent(Continent continent) {
		List<Item> items = new ArrayList<>();

		items.add(new Item<>("", null));
		items.addAll(mapToDisplaySubcontinentNames(DatabaseHelper.getSubcontinentDao().queryActiveByContinent(continent)));
		return items;
	}

	private static List<Item<Subcontinent>> mapToDisplaySubcontinentNames(List<Subcontinent> subcontinents) {
		return subcontinents.stream()
			.map(c -> new Item<>(I18nProperties.getSubcontinentName(c.getDefaultName()), c))
			.sorted(Comparator.comparing(Item::getKey))
			.collect(Collectors.toList());
	}

	public static List<Item> loadCountries() {
		List<Item> items = new ArrayList<>();

		items.add(new Item<>("", null));
		items.addAll(mapToDisplayCountryNames(DatabaseHelper.getCountryDao().queryActiveForAll(Country.ISO_CODE, true)));

		return items;
	}

	public static List<Item> loadCountriesBySubcontinent(Subcontinent subcontinent) {
		List<Item> items = new ArrayList<>();

		items.add(new Item<>("", null));
		items.addAll(mapToDisplayCountryNames(DatabaseHelper.getCountryDao().queryActiveBySubcontinent(subcontinent)));

		return items;
	}

	public static List<Item> loadCountriesByContinent(Continent continent) {
		List<Item> items = new ArrayList<>();

		List<Subcontinent> subcontinents = DatabaseHelper.getSubcontinentDao().queryActiveByContinent(continent);
		subcontinents.forEach(subcontinent -> items.addAll(loadCountriesBySubcontinent(subcontinent)));

		return items;
	}

	private static List<Item<Country>> mapToDisplayCountryNames(List<Country> countries) {
		return countries.stream()
			.map(c -> new Item<>(I18nProperties.getCountryName(c.getIsoCode(), c.getName()), c))
			.sorted(Comparator.comparing(Item::getKey))
			.collect(Collectors.toList());
	}

	public static List<Item> loadAreas() {
		return toItems(DatabaseHelper.getAreaDao().queryActiveForAll());
	}
	public static List<Item> loadRegionsByArea(Area area) {
		return toItems(DatabaseHelper.getRegionDao().queryActiveByArea(area));
	}

	public static List<Item> loadRegionsByServerCountry() {
		return toItems(DatabaseHelper.getRegionDao().queryActiveByServerCountry());
	}

	public static List<Item> loadRegionsByCountry(Country country) {
		return toItems(DatabaseHelper.getRegionDao().queryActiveByCountry(country));
	}

	public static List<Item> loadAllDistricts() {
		return toItems(DatabaseHelper.getDistrictDao().queryActiveForAll());
	}

	public static List<Item> loadDistricts(Region region) {
		return toItems(region != null ? DatabaseHelper.getDistrictDao().getByRegion(region) : new ArrayList<>(), true);
	}

	public static List<Item> loadAllCommunities() {
		return toItems(DatabaseHelper.getCommunityDao().queryActiveForAll());
	}

	public static List<Item> loadCommunities(District district) {
		return toItems(district != null ? DatabaseHelper.getCommunityDao().getByDistrict(district) : new ArrayList<>(), true);
	}

	public static List<Item> loadFacilities(District district, Community community, FacilityType type) {
		return toItems(
			community != null
				? DatabaseHelper.getFacilityDao().getActiveHealthFacilitiesByCommunityAndType(community, type, true, false)
				: district != null
					? DatabaseHelper.getFacilityDao().getActiveHealthFacilitiesByDistrictAndType(district, type, true, false)
					: new ArrayList<>(),
			true);
	}

	public static List<Item> loadPointsOfEntry(District district) {
		return toItems(district != null ? DatabaseHelper.getPointOfEntryDao().getActiveByDistrict(district, true) : new ArrayList<>(), true);
	}

	public static void initializeRegionAreaFields(
		final ControlSpinnerField areaField,
		List<Item> initialAreas,
		Area initialArea,
		final ControlSpinnerField regionField,
		List<Item> initialRegions,
		Region initialRegion,
		final ControlSpinnerField districtField,
		List<Item> initialDistricts,
		District initialDistrict,
		final ControlSpinnerField communityField,
		List<Item> initialCommunities,
		Community initialCommunity) {

		Item areaItem = initialArea != null ? DataUtils.toItem(initialArea) : null;
		Item regionItem = initialRegion != null ? DataUtils.toItem(initialRegion) : null;
		Item districtItem = initialDistrict != null ? DataUtils.toItem(initialDistrict) : null;
		Item communityItem = initialCommunity != null ? DataUtils.toItem(initialCommunity) : null;

		if (areaItem != null && !initialAreas.contains(areaItem)) {
			initialAreas.add(areaItem);
		}
		if (regionItem != null && !initialRegions.contains(regionItem)) {
			initialRegions.add(regionItem);
		}
		if (districtItem != null && !initialDistricts.contains(districtItem)) {
			initialDistricts.add(districtItem);
		}
		if (communityItem != null && !initialCommunities.contains(communityItem)) {
			initialCommunities.add(communityItem);
		}

		areaField.initializeSpinner(initialAreas, field -> {
			Area selectedArea = (Area) field.getValue();
			if (selectedArea != null) {
				List<Item> newRegions = loadRegionsByArea(selectedArea);
				if (initialRegion != null && selectedArea.equals(initialRegion.getArea()) && !newRegions.contains(regionItem)) {
					newRegions.add(regionItem);
				}
				regionField.setSpinnerData(newRegions, regionField.getValue());
			} else {
				regionField.setSpinnerData(null);
			}
		});

		regionField.initializeSpinner(initialRegions, field -> {
			Region selectedRegion = (Region) field.getValue();
			if (selectedRegion != null) {
				List<Item> newDistricts = loadDistricts(selectedRegion);
				if (initialDistrict != null && selectedRegion.equals(initialDistrict.getRegion()) && !newDistricts.contains(districtItem)) {
					newDistricts.add(districtItem);
				}
				districtField.setSpinnerData(newDistricts, districtField.getValue());

				areaField.setValue(selectedRegion.getArea());
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
		AbstractDomainObject entity,
		final ControlSpinnerField continentField,
		List<Item> continents,
		Continent initialContinent,
		final ControlSpinnerField subcontinentField,
		List<Item> subcontinents,
		Subcontinent initialSubcontinent,
		final ControlSpinnerField countryField,
		List<Item> countries,
		Country initialCountry,
		final ControlSpinnerField regionField,
		List<Item> regions,
		Region initialRegion,
		final ControlSpinnerField districtField,
		List<Item> districts,
		District initialDistrict,
		final ControlSpinnerField communityField,
		List<Item> communities,
		Community initialCommunity,
		final ControlSpinnerField facilityOrHomeField,
		List<Item> facilityOrHomeList,
		final ControlSpinnerField typeGroupField,
		List<Item> typeGroups,
		final ControlSpinnerField typeField,
		List<Item> types,
		final ControlSpinnerField facilityField,
		List<Item> facilities,
		Facility initialFacility,
		final ControlTextEditField facilityDetailsField,
		boolean withLaboratory) {

		Item continentItem = initialContinent != null ? DataUtils.toItem(initialContinent) : null;
		if (continentItem != null && !continents.contains(continentItem)) {
			continents.add(continentItem);
		}
		Item subcontinentItem = initialSubcontinent != null ? DataUtils.toItem(initialSubcontinent) : null;
		if (subcontinentItem != null && !continents.contains(subcontinentItem)) {
			subcontinents.add(subcontinentItem);
		}
		Item countryItem = initialCountry != null ? DataUtils.toItem(initialCountry) : null;
		if (countryItem != null && !countries.contains(countryItem)) {
			countries.add(countryItem);
		}

		continentField.initializeSpinner(continents);
		continentField.setValue(initialContinent);
		continentField.setVisibility(GONE);
		ValueChangeListener continentValueChangeListener = field -> {
			if (continentField.getVisibility() != GONE) {
				Continent selectedContinent = (Continent) field.getValue();
				if (selectedContinent != null) {
					List<Item> newSubcontinents = loadSubcontinentsByContinent(selectedContinent);
					if (initialSubcontinent != null
						&& selectedContinent.equals(initialSubcontinent.getContinent())
						&& !newSubcontinents.contains(subcontinentItem)) {
						newSubcontinents.add(subcontinentItem);
					}
					subcontinentField.setSpinnerData(newSubcontinents, subcontinentField.getValue());
				} else {
					subcontinentField.setSpinnerData(loadSubcontinents(), null);
				}
			}
		};
		continentField.initializeSpinner(continents, continentValueChangeListener);

		subcontinentField.initializeSpinner(subcontinents);
		subcontinentField.setValue(initialSubcontinent);
		subcontinentField.setVisibility(GONE);
		ValueChangeListener subcontinentValueChangeListener = field -> {
			if (subcontinentField.getVisibility() != GONE) {
				Subcontinent selectedSubcontinent = (Subcontinent) field.getValue();
				if (selectedSubcontinent != null) {
					List<Item> newCountries = loadCountriesBySubcontinent(selectedSubcontinent);
					if (initialCountry != null
						&& selectedSubcontinent.equals(initialCountry.getSubcontinent())
						&& !newCountries.contains(countryItem)) {
						newCountries.add(countryItem);
					}
					countryField.setSpinnerData(newCountries, countryField.getValue());

					continentField.unregisterListener(continentValueChangeListener);
					continentField.setValue(selectedSubcontinent.getContinent());
					continentField.registerListener(continentValueChangeListener);
				} else {
					Continent continentFieldValue = (Continent) continentField.getValue();
					if (continentFieldValue != null) {
						countryField.setSpinnerData(loadCountriesByContinent(continentFieldValue), null);
					} else {
						countryField.setSpinnerData(loadCountries(), null);
					}
				}
			}
		};
		subcontinentField.initializeSpinner(subcontinents, subcontinentValueChangeListener);

		countryField.initializeSpinner(countries, field -> {
			Country selectedCountry = (Country) field.getValue();
			String serverCountryName = ConfigProvider.getServerCountryName();
			boolean isServerCountry = serverCountryName == null
				? selectedCountry == null
				: selectedCountry == null || serverCountryName.equalsIgnoreCase(selectedCountry.getName());

			List<Item> newRegions = isServerCountry ? loadRegionsByServerCountry() : loadRegionsByCountry(selectedCountry);
			regionField.setSpinnerData(newRegions, regionField.getValue());
			if (selectedCountry != null) {
				final Subcontinent subcontinent = selectedCountry.getSubcontinent();
				if (subcontinent != null) {
					subcontinentField.unregisterListener(subcontinentValueChangeListener);
					subcontinentField.setValue(subcontinent);
					subcontinentField.registerListener(subcontinentValueChangeListener);
				}
				if (!(subcontinent == null || subcontinent.getContinent() == null)) {
					continentField.unregisterListener(continentValueChangeListener);
					continentField.setValue(subcontinent != null ? subcontinent.getContinent() : null);
					continentField.registerListener(continentValueChangeListener);
				}
			}
		});
		countryField.setValue(initialCountry);
		initializeFacilityFields(
			entity,
			regionField,
			regions,
			initialRegion,
			districtField,
			districts,
			initialDistrict,
			communityField,
			communities,
			initialCommunity,
			facilityOrHomeField,
			facilityOrHomeList,
			typeGroupField,
			typeGroups,
			typeField,
			types,
			facilityField,
			facilities,
			initialFacility,
			facilityDetailsField,
			null,
			null,
			null,
			withLaboratory);
	}

	public static void initializeFacilityFields(
		AbstractDomainObject entity,
		final ControlSpinnerField regionField,
		List<Item> regions,
		Region initialRegion,
		final ControlSpinnerField districtField,
		List<Item> districts,
		District initialDistrict,
		final ControlSpinnerField communityField,
		List<Item> communities,
		Community initialCommunity,
		final ControlSpinnerField facilityOrHomeField,
		List<Item> facilityOrHomeList,
		final ControlSpinnerField typeGroupField,
		List<Item> typeGroups,
		final ControlSpinnerField typeField,
		List<Item> types,
		final ControlSpinnerField facilityField,
		List<Item> facilities,
		Facility initialFacility,
		final ControlTextEditField facilityDetailsField,
		boolean withLaboratory) {
		initializeFacilityFields(
			entity,
			regionField,
			regions,
			initialRegion,
			districtField,
			districts,
			initialDistrict,
			communityField,
			communities,
			initialCommunity,
			facilityOrHomeField,
			facilityOrHomeList,
			typeGroupField,
			typeGroups,
			typeField,
			types,
			facilityField,
			facilities,
			initialFacility,
			facilityDetailsField,
			null,
			null,
			null,
			withLaboratory);
	}

	public static void initializeFacilityFields(
		AbstractDomainObject entity,
		final ControlSpinnerField regionField,
		List<Item> regions,
		Region initialRegion,
		final ControlSpinnerField districtField,
		List<Item> districts,
		District initialDistrict,
		final ControlSpinnerField communityField,
		List<Item> communities,
		Community initialCommunity,
		final ControlSpinnerField facilityOrHomeField,
		List<Item> facilityOrHomeList,
		final ControlSpinnerField typeGroupField,
		List<Item> typeGroups,
		final ControlSpinnerField typeField,
		List<Item> types,
		final ControlSpinnerField facilityField,
		List<Item> facilities,
		Facility initialFacility,
		final ControlTextEditField facilityDetailsField,
		final ControlSpinnerField pointOfEntryField,
		List<Item> pointsOfEntry,
		PointOfEntry initialPointOfEntry,
		boolean withLaboratory) {

		final Case caze = entity != null && entity.getClass().isAssignableFrom(Case.class) ? (Case) entity : null;

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
				if (initialCommunity != null && selectedDistrict.equals(initialCommunity.getDistrict()) && !newCommunities.contains(communityItem)) {
					newCommunities.add(communityItem);
				}
				if (typeField == null) {
					List<Item> newFacilities = loadFacilities(selectedDistrict, null, FacilityType.HOSPITAL);
					if (initialFacility != null && selectedDistrict.equals(initialFacility.getDistrict()) && !newFacilities.contains(facilityItem)) {
						newFacilities.add(facilityItem);
					}
					facilityField.setSpinnerData(newFacilities, facilityField.getValue());
				} else if (typeField.getValue() != null) {
					List<Item> newFacilities = loadFacilities(selectedDistrict, null, (FacilityType) typeField.getValue());
					if (initialFacility != null && selectedDistrict.equals(initialFacility.getDistrict()) && !newFacilities.contains(facilityItem)) {
						newFacilities.add(facilityItem);
					}
					facilityField.setSpinnerData(newFacilities, facilityField.getValue());
				} else {
					facilityField.setSpinnerData(null);
				}
				communityField.setSpinnerData(newCommunities, communityField.getValue());
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
			if (selectedCommunity != null && typeField == null) {
				List<Item> newFacilities = loadFacilities(null, selectedCommunity, FacilityType.HOSPITAL);
				if (initialFacility != null && selectedCommunity.equals(initialFacility.getCommunity()) && !newFacilities.contains(facilityItem)) {
					newFacilities.add(facilityItem);
				}
				facilityField.setSpinnerData(newFacilities);
			} else if (selectedCommunity != null && typeField.getValue() != null) {
				List<Item> newFacilities = loadFacilities(null, selectedCommunity, (FacilityType) typeField.getValue());
				if (initialFacility != null && selectedCommunity.equals(initialFacility.getCommunity()) && !newFacilities.contains(facilityItem)) {
					newFacilities.add(facilityItem);
				}
				facilityField.setSpinnerData(newFacilities);
			} else if (districtField.getValue() != null && typeField == null) {
				List<Item> newFacilities = loadFacilities((District) districtField.getValue(), null, FacilityType.HOSPITAL);
				if (initialFacility != null
					&& districtField.getValue().equals(initialFacility.getDistrict())
					&& !newFacilities.contains(facilityItem)) {
					newFacilities.add(facilityItem);
				}
				facilityField.setSpinnerData(newFacilities);
			} else if (districtField.getValue() != null && typeField.getValue() != null) {
				List<Item> newFacilities = loadFacilities((District) districtField.getValue(), null, (FacilityType) typeField.getValue());
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

		if (facilityOrHomeField != null) {
			facilityOrHomeField.initializeSpinner(facilityOrHomeList, field -> {
				TypeOfPlace selectedType = (TypeOfPlace) field.getValue();
				if (selectedType == null) {
					typeGroupField.setSpinnerData(null);
					facilityDetailsField.setVisibility(GONE);
				} else if (TypeOfPlace.HOME.equals(selectedType)) {
					typeGroupField.setSpinnerData(null);
					Facility noneFacility = DatabaseHelper.getFacilityDao().queryUuid(FacilityDto.NONE_FACILITY_UUID);
					facilityField.setSpinnerData(DataUtils.toItems(Arrays.asList(noneFacility)));
					facilityField.setValue(noneFacility);
					if (caze != null) {
						caze.setHealthFacility(noneFacility);
						caze.setFacilityType(null);
					}
				} else if (TypeOfPlace.FACILITY.equals(selectedType)) {
					typeGroupField.setSpinnerData(typeGroups);
					facilityDetailsField.setVisibility(GONE);
				}
			});
		}

		if (typeGroupField != null) {
			typeGroupField.initializeSpinner(typeGroups, field -> {
				FacilityTypeGroup selectedGroup = (FacilityTypeGroup) field.getValue();
				if (selectedGroup != null && withLaboratory) {
					typeField.setSpinnerData(DataUtils.toItems(FacilityType.getTypes(selectedGroup), true));
				} else if (selectedGroup != null) {
					typeField.setSpinnerData(DataUtils.toItems(FacilityType.getAccommodationTypes(selectedGroup), true));
				} else {
					typeField.setSpinnerData(null);
				}
			});
		}

		if (typeField != null) {
			typeField.initializeSpinner(types, field -> {
				FacilityType selectedType = (FacilityType) field.getValue();
				if (selectedType != null) {
					facilityField
						.setSpinnerData(loadFacilities((District) districtField.getValue(), (Community) communityField.getValue(), selectedType));
				} else {
					facilityField.setSpinnerData(null);
				}
			});
		}

		facilityField.setSpinnerData(facilities);

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
					ControlPropertyEditField healthFacilityDetailsEditField = (ControlPropertyEditField) healthFacilityDetailsField;
					healthFacilityDetailsEditField.setHint(caption);
					healthFacilityDetailsEditField.setRequired(true);
				}
			} else if (noneHealthFacility) {
				healthFacilityDetailsField.setVisibility(VISIBLE);
				String caption = I18nProperties.getCaption(Captions.CaseData_noneHealthFacilityDetails);
				healthFacilityDetailsField.setCaption(caption);
				if (healthFacilityDetailsField instanceof ControlPropertyEditField) {
					ControlPropertyEditField healthFacilityDetailsEditField = (ControlPropertyEditField) healthFacilityDetailsField;
					healthFacilityDetailsEditField.setHint(caption);
					healthFacilityDetailsEditField.setRequired(false);
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
