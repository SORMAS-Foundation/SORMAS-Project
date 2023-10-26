package de.symeda.sormas.app.util;

import static android.view.View.GONE;
import static de.symeda.sormas.app.util.InfrastructureDaoHelper.isEmptyDistrict;
import static de.symeda.sormas.app.util.InfrastructureDaoHelper.isEmptyFacility;
import static de.symeda.sormas.app.util.InfrastructureDaoHelper.isEmptyRegion;
import static de.symeda.sormas.app.util.InfrastructureDaoHelper.isUnknownDistrict;
import static de.symeda.sormas.app.util.InfrastructureDaoHelper.isUnknownFacility;
import static de.symeda.sormas.app.util.InfrastructureDaoHelper.itemsWithEmpty;
import static de.symeda.sormas.app.util.InfrastructureDaoHelper.loadCommunities;
import static de.symeda.sormas.app.util.InfrastructureDaoHelper.loadCountries;
import static de.symeda.sormas.app.util.InfrastructureDaoHelper.loadCountriesByContinent;
import static de.symeda.sormas.app.util.InfrastructureDaoHelper.loadCountriesBySubcontinent;
import static de.symeda.sormas.app.util.InfrastructureDaoHelper.loadDistricts;
import static de.symeda.sormas.app.util.InfrastructureDaoHelper.loadFacilities;
import static de.symeda.sormas.app.util.InfrastructureDaoHelper.loadPointsOfEntry;
import static de.symeda.sormas.app.util.InfrastructureDaoHelper.loadRegionsByCountry;
import static de.symeda.sormas.app.util.InfrastructureDaoHelper.loadRegionsByServerCountry;
import static de.symeda.sormas.app.util.InfrastructureDaoHelper.loadSubcontinents;
import static de.symeda.sormas.app.util.InfrastructureDaoHelper.loadSubcontinentsByContinent;
import static de.symeda.sormas.app.util.InfrastructureDaoHelper.unknownDistrict;
import static de.symeda.sormas.app.util.InfrastructureDaoHelper.unknownFacility;
import static de.symeda.sormas.app.util.InfrastructureDaoHelper.unknownRegion;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

import de.symeda.sormas.api.event.TypeOfPlace;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.infrastructure.facility.FacilityDto;
import de.symeda.sormas.api.infrastructure.facility.FacilityType;
import de.symeda.sormas.api.infrastructure.facility.FacilityTypeGroup;
import de.symeda.sormas.app.backend.caze.Case;
import de.symeda.sormas.app.backend.common.AbstractDomainObject;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.config.ConfigProvider;
import de.symeda.sormas.app.backend.facility.Facility;
import de.symeda.sormas.app.backend.pointofentry.PointOfEntry;
import de.symeda.sormas.app.backend.region.Community;
import de.symeda.sormas.app.backend.region.Continent;
import de.symeda.sormas.app.backend.region.Country;
import de.symeda.sormas.app.backend.region.District;
import de.symeda.sormas.app.backend.region.Region;
import de.symeda.sormas.app.backend.region.Subcontinent;
import de.symeda.sormas.app.component.Item;
import de.symeda.sormas.app.component.controls.ControlPropertyField;
import de.symeda.sormas.app.component.controls.ControlSpinnerField;
import de.symeda.sormas.app.component.controls.ControlTextEditField;
import de.symeda.sormas.app.component.controls.ValueChangeListener;

public class InfrastructureFieldsDependencyHandler {

	public static InfrastructureFieldsDependencyHandler instance = new InfrastructureFieldsDependencyHandler(false);
	public static InfrastructureFieldsDependencyHandler withUnknownValues = new InfrastructureFieldsDependencyHandler(true);

	private final boolean withUnknownItems;

	private InfrastructureFieldsDependencyHandler(boolean withUnknownItems) {
		this.withUnknownItems = withUnknownItems;
	}

	public void initializeFacilityFields(
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
		boolean showAllFacilityTypeGroups) {
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
			showAllFacilityTypeGroups);
	}

	public void initializeFacilityFields(
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
		boolean showAllFacilityTypeGroups) {
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
			pointOfEntryField,
			pointsOfEntry,
			initialPointOfEntry,
			showAllFacilityTypeGroups,
			null);
	}

	public void initializeFacilityFields(
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
		boolean showAllFacilityTypeGroups) {

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
			regionField.setSpinnerData(addUnknownItem(newRegions, unknownRegion), regionField.getValue());
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
			showAllFacilityTypeGroups);
	}

	public void initializeFacilityFields(
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
		boolean showAllFacilityTypeGroups,
		Supplier<Boolean> skipRegionListeners) {

		final Case caze = entity != null && entity.getClass().isAssignableFrom(Case.class) ? (Case) entity : null;

		Item regionItem = isEmptyRegion(initialRegion) ? null : DataUtils.toItem(initialRegion);
		Item districtItem = isEmptyDistrict(initialDistrict) ? null : DataUtils.toItem(initialDistrict);
		Item communityItem = initialCommunity != null ? DataUtils.toItem(initialCommunity) : null;
		Item facilityItem = isEmptyFacility(initialFacility) ? null : DataUtils.toItem(initialFacility);
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

		regionField.initializeSpinner(addUnknownItem(regions, unknownRegion));
		districtField.initializeSpinner(addUnknownItem(districts, unknownDistrict));
		communityField.initializeSpinner(communities);
		initializeRegionFieldListeners(
			regionField,
			districtField,
			initialDistrict,
			communityField,
			initialCommunity,
			typeField,
			facilityField,
			initialFacility,
			pointOfEntryField,
			initialPointOfEntry,
			skipRegionListeners);

		if (facilityOrHomeField != null) {
			facilityOrHomeField.initializeSpinner(facilityOrHomeList, field -> {
				TypeOfPlace selectedType = (TypeOfPlace) field.getValue();
				if (selectedType == null) {
					typeGroupField.setSpinnerData(null);
					facilityDetailsField.setValue(null);
					facilityDetailsField.setVisibility(GONE);
				} else if (TypeOfPlace.HOME.equals(selectedType)) {
					typeGroupField.setSpinnerData(null);
					typeField.setSpinnerData(null);
					Facility noneFacility = DatabaseHelper.getFacilityDao().queryUuid(FacilityDto.NONE_FACILITY_UUID);
					facilityField.setSpinnerData(DataUtils.toItems(Arrays.asList(noneFacility)));
					facilityField.setValue(noneFacility);
					if (caze != null) {
						caze.setHealthFacility(noneFacility);
						caze.setFacilityType(null);
					}
				} else if (TypeOfPlace.FACILITY.equals(selectedType)) {
					typeGroupField.setSpinnerData(typeGroups);
					if (facilityField.getValue() != null
						&& !FacilityDto.OTHER_FACILITY_UUID.equals(((Facility) facilityField.getValue()).getUuid())) {
						facilityDetailsField.setValue(null);
						facilityDetailsField.setVisibility(GONE);
					}
				}
			});
		}

		if (typeGroupField != null) {
			typeGroupField.initializeSpinner(typeGroups, field -> {
				FacilityTypeGroup selectedGroup = (FacilityTypeGroup) field.getValue();
				if (selectedGroup != null && showAllFacilityTypeGroups) {
					typeField.setSpinnerData(DataUtils.toItems(FacilityType.getTypes(selectedGroup), true));
				} else if (selectedGroup != null) {
					typeField.setSpinnerData(DataUtils.toItems(FacilityType.getAccommodationTypes(selectedGroup), true));
				} else {
					typeField.setSpinnerData(null);
				}
			});
		}

		if (typeField != null) {
			typeField.initializeSpinner(types);
		}

		facilityField.setSpinnerData(addUnknownItem(facilities, unknownFacility));

		if (pointOfEntryField != null) {
			pointOfEntryField.initializeSpinner(pointsOfEntry);
		}
	}

	public void initializeRegionFields(
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

		regionField.initializeSpinner(addUnknownItem(initialRegions, unknownRegion), field -> {
			handleRegionChange(field, districtField, districtItem, initialDistrict);
		});

		if (communityField != null) {
			districtField.initializeSpinner(addUnknownItem(initialDistricts, unknownDistrict), field -> {
				District selectedDistrict = (District) field.getValue();
				if (!isEmptyDistrict(selectedDistrict)) {
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
			districtField.initializeSpinner(addUnknownItem(initialDistricts, unknownDistrict));
		}

		if (communityField != null) {
			communityField.initializeSpinner(initialCommunities);
		}
	}

	public void initializeRegionFieldListeners(
		final ControlSpinnerField regionField,
		final ControlSpinnerField districtField,
		District initialDistrict,
		final ControlSpinnerField communityField,
		Community initialCommunity,
		final ControlSpinnerField typeField,
		final ControlSpinnerField facilityField,
		Facility initialFacility,
		final ControlSpinnerField pointOfEntryField,
		PointOfEntry initialPointOfEntry,
		Supplier<Boolean> skipListeners) {
		Item districtItem = initialDistrict != null ? DataUtils.toItem(initialDistrict) : null;
		Item communityItem = initialCommunity != null ? DataUtils.toItem(initialCommunity) : null;
		Item facilityItem = initialFacility != null ? DataUtils.toItem(initialFacility) : null;
		Item pointOfEntryItem = initialPointOfEntry != null ? DataUtils.toItem(initialPointOfEntry) : null;

		regionField.registerListener(field -> {
			if (skipListeners != null && skipListeners.get()) {
				return;
			}

			handleRegionChange(field, districtField, districtItem, initialDistrict);
		});

		districtField.registerListener(field -> {
			if (skipListeners != null && skipListeners.get()) {
				return;
			}

			District selectedDistrict = (District) field.getValue();
			if (!isEmptyDistrict(selectedDistrict)) {
				List<Item> newCommunities = loadCommunities(selectedDistrict);
				if (initialCommunity != null && selectedDistrict.equals(initialCommunity.getDistrict()) && !newCommunities.contains(communityItem)) {
					newCommunities.add(communityItem);
				}

				final List<Item> newFacilities;
				if (typeField == null) {
					newFacilities = loadFacilities(selectedDistrict, null, FacilityType.HOSPITAL);
					if (initialFacility != null && selectedDistrict.equals(initialFacility.getDistrict()) && !newFacilities.contains(facilityItem)) {
						newFacilities.add(facilityItem);
					}
				} else if (typeField.getValue() != null) {
					newFacilities = loadFacilities(selectedDistrict, null, (FacilityType) typeField.getValue());
					if (initialFacility != null && selectedDistrict.equals(initialFacility.getDistrict()) && !newFacilities.contains(facilityItem)) {
						newFacilities.add(facilityItem);
					}
				} else {
					newFacilities = addUnknownItem(itemsWithEmpty(), unknownFacility);
					if (initialFacility != null && !newFacilities.contains(facilityItem)) {
						newFacilities.add(facilityItem);
					}
				}

				Facility selectedFacility = (Facility) facilityField.getValue();
				facilityField.setSpinnerData(
					addUnknownItem(newFacilities, unknownFacility),
					isUnknownFacility(selectedFacility) && !isUnknownFacility(initialFacility) ? null : selectedFacility);

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
				facilityField.setSpinnerData(addUnknownItem(itemsWithEmpty(), unknownFacility));
				if (pointOfEntryField != null) {
					pointOfEntryField.setSpinnerData(null);
				}
			}
		});

		communityField.registerListener(field -> {
			if (skipListeners != null && skipListeners.get()) {
				return;
			}

			handleCommunityChange(field, districtField, facilityField, typeField, initialFacility);
		});

		if (typeField != null) {
			typeField.addValueChangedListener(field -> {
				if (skipListeners != null && skipListeners.get()) {
					return;
				}

				FacilityType selectedType = (FacilityType) field.getValue();
				District selectedDistrict = (District) districtField.getValue();

				if (selectedType != null && !isEmptyDistrict(selectedDistrict)) {
					List<Item> newFacilities = loadFacilities(selectedDistrict, (Community) communityField.getValue(), selectedType);
					if (facilityItem != null && !newFacilities.contains(facilityItem)) {
						newFacilities.add(facilityItem);
					}
					facilityField.setSpinnerData(addUnknownItem(newFacilities, unknownFacility));
				} else if (facilityField.getValue() != null) {
					Facility noneFacility = DatabaseHelper.getFacilityDao().queryUuid(FacilityDto.NONE_FACILITY_UUID);
					if (!facilityField.getValue().equals(noneFacility)) {
						List<Item> newFacilities = itemsWithEmpty();
						if (facilityItem != null) {
							newFacilities.add(facilityItem);
						}
						facilityField.setSpinnerData(addUnknownItem(newFacilities, unknownFacility));
					}
				}
			});
		}
	}

	private void handleRegionChange(
		ControlPropertyField regionField,
		ControlSpinnerField districtField,
		Item initialDistrictItem,
		District initialDistrict) {
		Region selectedRegion = (Region) regionField.getValue();
		if (selectedRegion != null && !selectedRegion.equals(unknownRegion)) {
			List<Item> newDistricts = loadDistricts(selectedRegion);
			if (initialDistrict != null && selectedRegion.equals(initialDistrict.getRegion()) && !newDistricts.contains(initialDistrictItem)) {
				newDistricts.add(initialDistrictItem);
			}
			District selectedDistrict = (District) districtField.getValue();
			districtField.setSpinnerData(
				addUnknownItem(newDistricts, unknownDistrict),
				isUnknownDistrict(selectedDistrict) && !isUnknownDistrict(initialDistrict) ? null : selectedDistrict);
		} else {
			districtField.setSpinnerData(addUnknownItem(itemsWithEmpty(), unknownDistrict));
		}
	}

	public void handleCommunityChange(
		ControlPropertyField communityField,
		ControlSpinnerField districtField,
		ControlSpinnerField facilityField,
		ControlSpinnerField typeField,
		Facility initialFacility) {
		Item facilityItem = initialFacility != null ? DataUtils.toItem(initialFacility) : null;
		Community selectedCommunity = (Community) communityField.getValue();
		District selectedDistrict = (District) districtField.getValue();

		final List<Item> newFacilities;
		if (selectedCommunity != null && typeField == null) {
			newFacilities = loadFacilities(null, selectedCommunity, FacilityType.HOSPITAL);
			if (initialFacility != null && selectedCommunity.equals(initialFacility.getCommunity()) && !newFacilities.contains(facilityItem)) {
				newFacilities.add(facilityItem);
			}

		} else if (selectedCommunity != null && typeField.getValue() != null) {
			newFacilities = loadFacilities(null, selectedCommunity, (FacilityType) typeField.getValue());
			if (initialFacility != null && selectedCommunity.equals(initialFacility.getCommunity()) && !newFacilities.contains(facilityItem)) {
				newFacilities.add(facilityItem);
			}
		} else {
			if (!isEmptyDistrict(selectedDistrict) && typeField == null) {
				newFacilities = loadFacilities(selectedDistrict, null, FacilityType.HOSPITAL);
				if (initialFacility != null && selectedDistrict.equals(initialFacility.getDistrict()) && !newFacilities.contains(facilityItem)) {
					newFacilities.add(facilityItem);
				}
			} else if (!isEmptyDistrict(selectedDistrict) && typeField.getValue() != null) {
				newFacilities = loadFacilities(selectedDistrict, null, (FacilityType) typeField.getValue());
				if (initialFacility != null && selectedDistrict.equals(initialFacility.getDistrict()) && !newFacilities.contains(facilityItem)) {
					newFacilities.add(facilityItem);
				}
			} else {
				newFacilities = itemsWithEmpty();
				if (initialFacility != null && !newFacilities.contains(facilityItem)) {
					newFacilities.add(facilityItem);
				}
			}
		}

		Facility selectedFacility = (Facility) facilityField.getValue();
		if (selectedFacility == null) {
			facilityField.setSpinnerData(addUnknownItem(newFacilities, unknownFacility));
		} else {
			if (isEmptyFacility(selectedFacility) && !isEmptyFacility(initialFacility) && !isEmptyDistrict(selectedDistrict)) {
				selectedFacility = null;
			}

			facilityField.setSpinnerData(addUnknownItem(newFacilities, unknownFacility), selectedFacility);
		}
	}

	private List<Item> addUnknownItem(List<Item> items, Object unknownItem) {

		if (items == null && !withUnknownItems) {
			return null;
		}

		List<Item> allItems = items == null ? new ArrayList<>() : new ArrayList<>(items);

		if (withUnknownItems) {
			allItems.add(new Item(I18nProperties.getCaption(Captions.unknown), unknownItem));
		}

		return allItems;
	}
}
