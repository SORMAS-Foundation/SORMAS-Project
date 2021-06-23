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
package de.symeda.sormas.ui.statistics;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.v7.ui.ComboBox;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.facility.FacilityDto;
import de.symeda.sormas.api.facility.FacilityReferenceDto;
import de.symeda.sormas.api.facility.FacilityType;
import de.symeda.sormas.api.facility.FacilityTypeGroup;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.region.CommunityReferenceDto;
import de.symeda.sormas.api.region.DistrictReferenceDto;
import de.symeda.sormas.api.region.RegionReferenceDto;
import de.symeda.sormas.api.statistics.StatisticsCaseAttribute;
import de.symeda.sormas.api.statistics.StatisticsCaseSubAttribute;
import de.symeda.sormas.ui.utils.ComboBoxHelper;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.FieldHelper;

@SuppressWarnings("serial")
public class StatisticsFilterJurisdictionElement extends StatisticsFilterElement {

	StatisticsFilterValuesElement regionElement;
	StatisticsFilterValuesElement districtElement;
	StatisticsFilterValuesElement communityElement;
	StatisticsFilterValuesElement facilityElement;
	private ComboBox type;

	public StatisticsFilterJurisdictionElement(int rowIndex) {
		setSpacing(true);
		addStyleName(CssStyles.LAYOUT_MINIMAL);
		setWidth(100, Unit.PERCENTAGE);

		VerticalLayout firstColumnLayout = new VerticalLayout();
		VerticalLayout secondColumnLayout = new VerticalLayout();

		regionElement = new StatisticsFilterValuesElement(
			I18nProperties.getPrefixCaption(CaseDataDto.I18N_PREFIX, CaseDataDto.REGION),
			StatisticsCaseAttribute.JURISDICTION,
			StatisticsCaseSubAttribute.REGION,
			this,
			rowIndex);
		districtElement = new StatisticsFilterValuesElement(
			I18nProperties.getPrefixCaption(CaseDataDto.I18N_PREFIX, CaseDataDto.DISTRICT),
			StatisticsCaseAttribute.JURISDICTION,
			StatisticsCaseSubAttribute.DISTRICT,
			this,
			rowIndex);
		communityElement = new StatisticsFilterValuesElement(
			I18nProperties.getPrefixCaption(CaseDataDto.I18N_PREFIX, CaseDataDto.COMMUNITY),
			StatisticsCaseAttribute.JURISDICTION,
			StatisticsCaseSubAttribute.COMMUNITY,
			this,
			rowIndex);
		HorizontalLayout facilityTypeFilterLayout = new HorizontalLayout();
		facilityTypeFilterLayout.setWidth(100, Unit.PERCENTAGE);
		ComboBox typeGroup = ComboBoxHelper.createComboBoxV7();
		typeGroup.setId("typeGroup");
		typeGroup.setCaption(I18nProperties.getCaption(Captions.Facility_typeGroup));
		typeGroup.setWidth(100, Unit.PERCENTAGE);
		typeGroup.addItems(FacilityTypeGroup.values());
		type = ComboBoxHelper.createComboBoxV7();
		type.setId("type");
		type.setCaption(I18nProperties.getPrefixCaption(FacilityDto.I18N_PREFIX, FacilityDto.TYPE));
		type.setWidth(100, Unit.PERCENTAGE);
		facilityTypeFilterLayout.addComponents(typeGroup, type);
		facilityElement = new StatisticsFilterValuesElement(
			I18nProperties.getPrefixCaption(CaseDataDto.I18N_PREFIX, CaseDataDto.HEALTH_FACILITY),
			StatisticsCaseAttribute.JURISDICTION,
			StatisticsCaseSubAttribute.FACILITY,
			this,
			rowIndex);

		firstColumnLayout.addComponent(regionElement);
		secondColumnLayout.addComponent(districtElement);
		firstColumnLayout.addComponent(communityElement);
		secondColumnLayout.addComponent(facilityTypeFilterLayout);
		secondColumnLayout.addComponent(facilityElement);

		addComponent(firstColumnLayout);
		addComponent(secondColumnLayout);

		regionElement.setValueChangeListener(e -> {
			districtElement.updateDropdownContent();
			// Remove values from district token field if the respective region is no longer selected
			if (regionElement.getSelectedValues() != null && districtElement.getSelectedValues() != null) {
				List<TokenizableValue> regionValues = regionElement.getSelectedValues();
				List<String> regionUuids = regionValues.stream().map(r -> ((RegionReferenceDto) r.getValue()).getUuid()).collect(Collectors.toList());
				List<TokenizableValue> districtValues = districtElement.getSelectedValues();
				Map<String, String> districtRegionsMap = FacadeProvider.getDistrictFacade()
					.getRegionUuidsForDistricts(districtValues.stream().map(d -> (DistrictReferenceDto) d.getValue()).collect(Collectors.toList()));

				districtValues.stream()
					.filter(d -> !regionUuids.contains(districtRegionsMap.get(((DistrictReferenceDto) d.getValue()).getUuid())))
					.forEach(d -> {
						districtElement.getTokenField().removeTokenizable(d);
					});
			}
		});

		districtElement.setValueChangeListener(e -> {
			communityElement.updateDropdownContent();
			facilityElement.updateDropdownContent();
			// Remove values from community/health facility token fields if the respective district is no longer selected
			if (districtElement.getSelectedValues() != null) {
				List<TokenizableValue> districtValues = districtElement.getSelectedValues();
				List<String> districtUuids =
					districtValues.stream().map(d -> ((DistrictReferenceDto) d.getValue()).getUuid()).collect(Collectors.toList());

				if (communityElement.getSelectedValues() != null) {
					List<TokenizableValue> communityValues = communityElement.getSelectedValues();
					Map<String, String> communityDistrictsMap = FacadeProvider.getCommunityFacade()
						.getDistrictUuidsForCommunities(
							communityValues.stream().map(c -> (CommunityReferenceDto) c.getValue()).collect(Collectors.toList()));
					communityValues.stream()
						.filter(c -> !districtUuids.contains(communityDistrictsMap.get(((CommunityReferenceDto) c.getValue()).getUuid())))
						.forEach(c -> {
							communityElement.getTokenField().removeTokenizable(c);
						});
				}
				if (facilityElement.getSelectedValues() != null) {
					List<TokenizableValue> facilityValues = facilityElement.getSelectedValues();
					Map<String, String> facilityDistrictsMap = FacadeProvider.getFacilityFacade()
						.getDistrictUuidsForFacilities(
							facilityValues.stream().map(f -> (FacilityReferenceDto) f.getValue()).collect(Collectors.toList()));
					facilityValues.stream()
						.filter(f -> !districtUuids.contains(facilityDistrictsMap.get(((FacilityReferenceDto) f.getValue()).getUuid())))
						.forEach(f -> {
							facilityElement.getTokenField().removeTokenizable(f);
						});
				}
			}
		});

		communityElement.setValueChangeListener(e -> {
			facilityElement.updateDropdownContent();
			// Remove values from health facility token field if the respective community is no longer selected
			if (communityElement.getSelectedValues() != null && facilityElement.getSelectedValues() != null) {
				List<TokenizableValue> communityValues = communityElement.getSelectedValues();
				List<String> communityUuids =
					communityValues.stream().map(c -> ((CommunityReferenceDto) c.getValue()).getUuid()).collect(Collectors.toList());
				List<TokenizableValue> facilityValues = facilityElement.getSelectedValues();
				Map<String, String> facilityCommunitiesMap = FacadeProvider.getFacilityFacade()
					.getCommunityUuidsForFacilities(
						facilityValues.stream().map(f -> (FacilityReferenceDto) f.getValue()).collect(Collectors.toList()));
				facilityValues.stream()
					.filter(f -> !communityUuids.contains(facilityCommunitiesMap.get(((FacilityReferenceDto) f.getValue()).getUuid())))
					.forEach(f -> {
						facilityElement.getTokenField().removeTokenizable(f);
					});
			}
		});

		typeGroup.addValueChangeListener(e -> {
			FieldHelper.updateEnumData(type, FacilityType.getAccommodationTypes((FacilityTypeGroup) typeGroup.getValue()));
		});

		type.addValueChangeListener(e -> {
			facilityElement.updateDropdownContent();
			if (facilityElement.getSelectedValues() != null) {
				List<TokenizableValue> facilityValues = facilityElement.getSelectedValues();
				List<String> facilityUuids =
					facilityValues.stream().map(c -> ((FacilityReferenceDto) c.getValue()).getUuid()).collect(Collectors.toList());
				List<FacilityReferenceDto> facilities = new ArrayList<>();
				for (TokenizableValue selectedCommunityTokenizable : communityElement.getSelectedValues()) {
					CommunityReferenceDto selectedCommunity = (CommunityReferenceDto) selectedCommunityTokenizable.getValue();
					facilities.addAll(
						FacadeProvider.getFacilityFacade()
							.getActiveFacilitiesByCommunityAndType(selectedCommunity, (FacilityType) type.getValue(), false, false));
				}
				Map<String, String> facilityCommunitiesMap = FacadeProvider.getFacilityFacade()
					.getCommunityUuidsForFacilities(
						facilityValues.stream().map(f -> (FacilityReferenceDto) f.getValue()).collect(Collectors.toList()));
				facilityValues.stream()
					.filter(f -> !facilityUuids.contains(facilityCommunitiesMap.get(((FacilityReferenceDto) f.getValue()).getUuid())))
					.forEach(f -> {
						facilityElement.getTokenField().removeTokenizable(f);
					});
			}
		});
	}

	public List<TokenizableValue> getSelectedRegions() {
		return regionElement.getSelectedValues();
	}

	public List<TokenizableValue> getSelectedDistricts() {
		return districtElement.getSelectedValues();
	}

	public List<TokenizableValue> getSelectedCommunities() {
		return communityElement.getSelectedValues();
	}

	public List<TokenizableValue> getSelectedHealthFacilities() {
		return facilityElement.getSelectedValues();
	}

	FacilityType getFacilityType() {
		return (FacilityType) type.getValue();
	}

	@Override
	public List<TokenizableValue> getSelectedValues() {
		throw new UnsupportedOperationException("You should call one of the dedicated getters instead");
	}
}
