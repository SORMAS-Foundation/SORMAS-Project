package de.symeda.sormas.ui.statistics;

import java.util.List;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.I18nProperties;
import de.symeda.sormas.api.location.LocationDto;
import de.symeda.sormas.api.region.DistrictReferenceDto;
import de.symeda.sormas.api.region.RegionReferenceDto;
import de.symeda.sormas.api.statistics.StatisticsCaseAttribute;
import de.symeda.sormas.api.statistics.StatisticsCaseSubAttribute;
import de.symeda.sormas.ui.utils.CssStyles;

@SuppressWarnings("serial")
public class StatisticsFilterRegionDistrictElement extends StatisticsFilterElement {

	StatisticsFilterValuesElement regionElement;
	StatisticsFilterValuesElement districtElement;

	public StatisticsFilterRegionDistrictElement() {
		setSpacing(true);
		addStyleName(CssStyles.LAYOUT_MINIMAL);
		setWidth(100, Unit.PERCENTAGE);

		regionElement = new StatisticsFilterValuesElement(I18nProperties.getPrefixFieldCaption(LocationDto.I18N_PREFIX, LocationDto.REGION),
				StatisticsCaseAttribute.REGION_DISTRICT, StatisticsCaseSubAttribute.REGION, this);
		districtElement = new StatisticsFilterValuesElement(I18nProperties.getPrefixFieldCaption(LocationDto.I18N_PREFIX, LocationDto.DISTRICT),
				StatisticsCaseAttribute.REGION_DISTRICT, StatisticsCaseSubAttribute.DISTRICT, this);

		addComponent(regionElement);
		addComponent(districtElement);

		regionElement.setValueChangeListener(e -> {
			districtElement.updateDropdownContent();
			// Remove values from district token field if the respective region is not selected anymore
			if (regionElement.getSelectedValues() != null && districtElement.getSelectedValues() != null) {
				List<TokenizableValue> regionValues = regionElement.getSelectedValues();
				for (TokenizableValue districtValue : districtElement.getSelectedValues()) {
					RegionReferenceDto regionRef = FacadeProvider.getDistrictFacade()
							.getDistrictByUuid(((DistrictReferenceDto) districtValue.getValue()).getUuid())
							.getRegion();
					boolean keepDistrictValue = false;
					for (TokenizableValue regionValue : regionValues) {
						if (regionValue.getValue().equals(regionRef)) {
							keepDistrictValue = true;
							break;
						}
					}

					if (!keepDistrictValue) {
						districtElement.getTokenField().removeTokenizable(districtValue);
					}
				}
			}
		});
	}

	public List<TokenizableValue> getSelectedRegions() {
		return (List<TokenizableValue>) regionElement.getSelectedValues();
	}

	public List<TokenizableValue> getSelectedDistricts() {
		return (List<TokenizableValue>) districtElement.getSelectedValues();
	}

	@Override
	public List<TokenizableValue> getSelectedValues() {
		throw new UnsupportedOperationException("You should call getSelectedRegions and getSelectedDistricts instead");
	}

}
