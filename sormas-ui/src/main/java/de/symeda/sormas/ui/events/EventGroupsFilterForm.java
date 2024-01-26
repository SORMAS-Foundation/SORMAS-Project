package de.symeda.sormas.ui.events;

import com.vaadin.v7.data.Property;
import com.vaadin.v7.ui.ComboBox;
import com.vaadin.v7.ui.TextField;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.event.EventGroupCriteria;
import de.symeda.sormas.api.event.EventGroupIndexDto;
import de.symeda.sormas.api.i18n.Descriptions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.infrastructure.district.DistrictReferenceDto;
import de.symeda.sormas.api.infrastructure.region.RegionReferenceDto;
import de.symeda.sormas.api.location.LocationDto;
import de.symeda.sormas.ui.utils.AbstractFilterForm;
import de.symeda.sormas.ui.utils.FieldConfiguration;
import de.symeda.sormas.ui.utils.FieldHelper;

public class EventGroupsFilterForm extends AbstractFilterForm<EventGroupCriteria> {

	private static final long serialVersionUID = -1166745065032487009L;

	protected EventGroupsFilterForm() {
		super(
			EventGroupCriteria.class,
			EventGroupIndexDto.I18N_PREFIX,
			JurisdictionFieldConfig.of(EventGroupCriteria.REGION, EventGroupCriteria.DISTRICT, EventGroupCriteria.COMMUNITY));
	}

	@Override
	protected String[] getMainFilterLocators() {

		return new String[] {
			EventGroupCriteria.FREE_TEXT,
			EventGroupCriteria.FREE_TEXT_EVENT,
			EventGroupCriteria.REGION,
			EventGroupCriteria.DISTRICT,
			EventGroupCriteria.COMMUNITY };
	}

	@Override
	protected void addFields() {

		TextField searchField = addField(
			FieldConfiguration
				.withCaptionAndPixelSized(EventGroupCriteria.FREE_TEXT, I18nProperties.getString(Strings.promptEventGroupSearchField), 200));
		searchField.setNullRepresentation("");

		TextField searchFieldEventParticipants = addField(
			FieldConfiguration.withCaptionAndPixelSized(
				EventGroupCriteria.FREE_TEXT_EVENT,
				I18nProperties.getString(Strings.promptEventGroupSearchFieldEvent),
				200));
		searchFieldEventParticipants.setNullRepresentation("");

		ComboBox regionField = addField(
			FieldConfiguration
				.withCaptionAndPixelSized(LocationDto.REGION, I18nProperties.getPrefixCaption(LocationDto.I18N_PREFIX, LocationDto.REGION), 140));
		regionField.addItems(FacadeProvider.getRegionFacade().getAllActiveAsReference());

		ComboBox districtField = addField(
			FieldConfiguration
				.withCaptionAndPixelSized(LocationDto.DISTRICT, I18nProperties.getPrefixCaption(LocationDto.I18N_PREFIX, LocationDto.DISTRICT), 140));
		districtField.setDescription(I18nProperties.getDescription(Descriptions.descDistrictFilter));
		districtField.setEnabled(false);

		ComboBox communityField = addField(
			FieldConfiguration.withCaptionAndPixelSized(
				LocationDto.COMMUNITY,
				I18nProperties.getPrefixCaption(LocationDto.I18N_PREFIX, LocationDto.COMMUNITY),
				140));
		communityField.setDescription(I18nProperties.getDescription(Descriptions.descCommunityFilter));
		communityField.setEnabled(false);
	}

	@Override
	protected void applyDependenciesOnFieldChange(String propertyId, Property.ValueChangeEvent event) {
		switch (propertyId) {
		case LocationDto.REGION:
			RegionReferenceDto region = (RegionReferenceDto) event.getProperty().getValue();
			if (region != null) {
				applyRegionFilterDependency(region, LocationDto.DISTRICT);
				clearAndDisableFields(LocationDto.COMMUNITY);
			} else {
				clearAndDisableFields(LocationDto.DISTRICT, LocationDto.COMMUNITY);
			}
			break;
		case LocationDto.DISTRICT:
			DistrictReferenceDto district = (DistrictReferenceDto) event.getProperty().getValue();
			if (district != null) {
				applyDistrictDependency(district, LocationDto.COMMUNITY);
			} else {
				clearAndDisableFields(LocationDto.COMMUNITY);
			}
			break;
		}
	}

	@Override
	protected void applyRegionFilterDependency(RegionReferenceDto region, String districtFieldId) {
		final ComboBox districtField = getField(districtFieldId);
		if (region != null) {
			FieldHelper.updateItems(districtField, FacadeProvider.getDistrictFacade().getAllActiveByRegion(region.getUuid()));
			districtField.setEnabled(true);
		} else {
			districtField.setEnabled(false);
		}
	}

	@Override
	protected void applyDistrictDependency(DistrictReferenceDto district, String communityFieldId) {
		final ComboBox communityField = getField(communityFieldId);
		if (district != null) {
			FieldHelper.updateItems(communityField, FacadeProvider.getCommunityFacade().getAllActiveByDistrict(district.getUuid()));
			communityField.setEnabled(true);
		} else {
			communityField.setEnabled(false);
		}
	}

	@Override
	protected void applyDependenciesOnNewValue(EventGroupCriteria criteria) {
		RegionReferenceDto region = criteria.getRegion();
		DistrictReferenceDto district = criteria.getDistrict();
		applyRegionAndDistrictFilterDependency(region, LocationDto.DISTRICT, district, LocationDto.COMMUNITY);
	}
}
