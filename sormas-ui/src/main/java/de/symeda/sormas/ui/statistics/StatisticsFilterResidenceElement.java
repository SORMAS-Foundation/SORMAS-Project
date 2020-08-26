package de.symeda.sormas.ui.statistics;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.VerticalLayout;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.location.LocationDto;
import de.symeda.sormas.api.region.CommunityReferenceDto;
import de.symeda.sormas.api.region.DistrictReferenceDto;
import de.symeda.sormas.api.region.RegionReferenceDto;
import de.symeda.sormas.api.statistics.StatisticsCaseAttribute;
import de.symeda.sormas.api.statistics.StatisticsCaseSubAttribute;
import de.symeda.sormas.ui.utils.CssStyles;

public class StatisticsFilterResidenceElement extends StatisticsFilterElement {

	StatisticsFilterValuesElement regionElement;
	StatisticsFilterValuesElement districtElement;
	StatisticsFilterValuesElement communityElement;
	StatisticsFilterSimpleTextElement cityElement;
	StatisticsFilterSimpleTextElement postcodeElement;
	StatisticsFilterSimpleTextElement addressElement;

	public StatisticsFilterResidenceElement(int rowIndex) {
		setSpacing(true);
		addStyleName(CssStyles.LAYOUT_MINIMAL);
		setWidth(100, Unit.PERCENTAGE);

		HorizontalLayout regionDistrictCommunityLayout = new HorizontalLayout();

		VerticalLayout firstColumnLayout = new VerticalLayout();
		VerticalLayout secondColumnLayout = new VerticalLayout();

		regionElement = new StatisticsFilterValuesElement(
			I18nProperties.getPrefixCaption(LocationDto.I18N_PREFIX, LocationDto.REGION),
			StatisticsCaseAttribute.PLACE_OF_RESIDENCE,
			StatisticsCaseSubAttribute.PERSON_REGION,
			this,
			rowIndex);
		districtElement = new StatisticsFilterValuesElement(
			I18nProperties.getPrefixCaption(LocationDto.I18N_PREFIX, LocationDto.DISTRICT),
			StatisticsCaseAttribute.PLACE_OF_RESIDENCE,
			StatisticsCaseSubAttribute.PERSON_DISTRICT,
			this,
			rowIndex);
		communityElement = new StatisticsFilterValuesElement(
			I18nProperties.getPrefixCaption(LocationDto.I18N_PREFIX, LocationDto.COMMUNITY),
			StatisticsCaseAttribute.PLACE_OF_RESIDENCE,
			StatisticsCaseSubAttribute.PERSON_COMMUNITY,
			this,
			rowIndex);
		cityElement = new StatisticsFilterSimpleTextElement(I18nProperties.getCaption(Captions.city), rowIndex);
		postcodeElement = new StatisticsFilterSimpleTextElement(I18nProperties.getCaption(Captions.postcode), rowIndex);
		addressElement = new StatisticsFilterSimpleTextElement(I18nProperties.getCaption(Captions.address), rowIndex);

		firstColumnLayout.addComponent(regionElement);
		secondColumnLayout.addComponent(districtElement);

		firstColumnLayout.addComponent(communityElement);

		regionDistrictCommunityLayout.addComponent(firstColumnLayout);
		regionDistrictCommunityLayout.addComponent(secondColumnLayout);

		HorizontalLayout cityPostcodeAddressLayout = new HorizontalLayout();

		cityPostcodeAddressLayout.addComponent(new VerticalLayout(cityElement));
		cityPostcodeAddressLayout.addComponent(new VerticalLayout(postcodeElement));
		cityPostcodeAddressLayout.addComponent(new VerticalLayout(addressElement));

		addComponent(new VerticalLayout(regionDistrictCommunityLayout, cityPostcodeAddressLayout));

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

	public String getCity() {
		return cityElement.getSelectedValues().get(0).getStringValue();
	}

	public String getPostcode() {
		return postcodeElement.getSelectedValues().get(0).getStringValue();
	}

	public String getAddress() {
		return addressElement.getSelectedValues().get(0).getStringValue();
	}

	@Override
	public List<TokenizableValue> getSelectedValues() {
		throw new UnsupportedOperationException("You should call one of the dedicated getters instead");
	}

}
