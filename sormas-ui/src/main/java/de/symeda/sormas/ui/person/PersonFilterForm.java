package de.symeda.sormas.ui.person;

import com.vaadin.v7.data.Property;
import com.vaadin.v7.ui.ComboBox;
import com.vaadin.v7.ui.TextField;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.i18n.Descriptions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.infrastructure.community.CommunityReferenceDto;
import de.symeda.sormas.api.infrastructure.district.DistrictReferenceDto;
import de.symeda.sormas.api.infrastructure.region.RegionReferenceDto;
import de.symeda.sormas.api.person.PersonCriteria;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.user.JurisdictionLevel;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.ui.UserProvider;
import de.symeda.sormas.ui.utils.AbstractFilterForm;
import de.symeda.sormas.ui.utils.FieldConfiguration;
import de.symeda.sormas.ui.utils.FieldHelper;

public class PersonFilterForm extends AbstractFilterForm<PersonCriteria> {

	protected PersonFilterForm() {
		super(
			PersonCriteria.class,
			PersonDto.I18N_PREFIX,
			JurisdictionFieldConfig.of(PersonCriteria.REGION, PersonCriteria.DISTRICT, PersonCriteria.COMMUNITY));
	}

	@Override
	protected String[] getMainFilterLocators() {
		return new String[] {
			PersonCriteria.BIRTHDATE_YYYY,
			PersonCriteria.BIRTHDATE_MM,
			PersonCriteria.BIRTHDATE_DD,
			PersonCriteria.NAME_ADDRESS_PHONE_EMAIL_LIKE,
			PersonCriteria.PRESENT_CONDITION,
			PersonCriteria.REGION,
			PersonCriteria.DISTRICT,
			PersonCriteria.COMMUNITY };
	}

	@Override
	protected void addFields() {

		addBirthDateFields(getContent(), PersonCriteria.BIRTHDATE_YYYY, PersonCriteria.BIRTHDATE_MM, PersonCriteria.BIRTHDATE_DD);

		final TextField searchField = addField(
			FieldConfiguration.withCaptionAndPixelSized(
				PersonCriteria.NAME_ADDRESS_PHONE_EMAIL_LIKE,
				I18nProperties.getString(Strings.promptPersonsSearchField),
				200));
		searchField.setNullRepresentation("");

		final ComboBox presentConditionField = addField(getContent(), FieldConfiguration.pixelSized(PersonCriteria.PRESENT_CONDITION, 140));
		presentConditionField.setInputPrompt(I18nProperties.getPrefixCaption(PersonDto.I18N_PREFIX, PersonDto.PRESENT_CONDITION));

		UserDto user = currentUserDto();
		ComboBox regionField = null;
		if (user.getRegion() == null) {
			regionField = addField(getContent(), FieldConfiguration.pixelSized(PersonCriteria.REGION, 140));
			regionField.addItems(FacadeProvider.getRegionFacade().getAllActiveByServerCountry());
		}

		final ComboBox districtFilter = addField(getContent(), FieldConfiguration.pixelSized(PersonCriteria.DISTRICT, 140));
		districtFilter.setDescription(I18nProperties.getDescription(Descriptions.descDistrictFilter));

		addField(getContent(), FieldConfiguration.pixelSized(PersonCriteria.COMMUNITY, 140));
	}

	@Override
	protected void applyDependenciesOnFieldChange(String propertyId, Property.ValueChangeEvent event) {
		super.applyDependenciesOnFieldChange(propertyId, event);

		final ComboBox districtFilter = getField(PersonCriteria.DISTRICT);
		final ComboBox communityFilter = getField(PersonCriteria.COMMUNITY);

		switch (propertyId) {
		case PersonCriteria.BIRTHDATE_MM: {
			Integer birthMM = (Integer) event.getProperty().getValue();

			ComboBox birthDayDD = getField(PersonCriteria.BIRTHDATE_DD);
			birthDayDD.setEnabled(birthMM != null);
			FieldHelper.updateItems(
				birthDayDD,
				DateHelper.getDaysInMonth(
					(Integer) getField(PersonCriteria.BIRTHDATE_MM).getValue(),
					(Integer) getField(PersonCriteria.BIRTHDATE_YYYY).getValue()));

			break;
		}
		case PersonCriteria.REGION:
			RegionReferenceDto region = (RegionReferenceDto) event.getProperty().getValue();
			if (region != null) {
				enableFields(districtFilter);
				districtFilter.removeAllItems();
				districtFilter.addItems(FacadeProvider.getDistrictFacade().getAllActiveByRegion(region.getUuid()));
			} else {
				districtFilter.removeAllItems();
				districtFilter.clear();
			}
			clearAndDisableFields(communityFilter);
			break;
		case PersonCriteria.DISTRICT:
			DistrictReferenceDto district = (DistrictReferenceDto) event.getProperty().getValue();

			if (district != null) {
				enableFields(communityFilter);
				communityFilter.removeAllItems();
				communityFilter.addItems(FacadeProvider.getCommunityFacade().getAllActiveByDistrict(district.getUuid()));
			} else {
				communityFilter.removeAllItems();
				communityFilter.clear();
			}
			break;
		}
	}

	@Override
	protected void applyDependenciesOnNewValue(PersonCriteria criteria) {

		final UserDto user = currentUserDto();
		final JurisdictionLevel userJurisdictionLevel = UserProvider.getCurrent().getJurisdictionLevel();

		final ComboBox districtFilter = getField(PersonCriteria.DISTRICT);
		final ComboBox communityFilter = getField(PersonCriteria.COMMUNITY);

		// Get initial field values according to user and criteria
		final RegionReferenceDto region = user.getRegion() == null ? criteria.getRegion() : user.getRegion();
		final DistrictReferenceDto district = user.getDistrict() == null ? criteria.getDistrict() : user.getDistrict();
		final CommunityReferenceDto community = user.getCommunity() == null ? criteria.getCommunity() : user.getCommunity();

		// district
		if (region != null) {
			enableFields(districtFilter);
			districtFilter.addItems(FacadeProvider.getDistrictFacade().getAllActiveByRegion(region.getUuid()));
			// community
			if (district != null) {
				districtFilter.setValue(district);
				communityFilter.addItems(FacadeProvider.getCommunityFacade().getAllActiveByDistrict(district.getUuid()));
				enableFields(communityFilter);
				if (community != null) {
					communityFilter.setValue(community);
				}
			} else {
				clearAndDisableFields(communityFilter);
			}
		} else {
			clearAndDisableFields(districtFilter, communityFilter);
		}

		// Disable fields according to user & jurisdiction
		if (userJurisdictionLevel == JurisdictionLevel.DISTRICT) {
			clearAndDisableFields(districtFilter);
		} else if (userJurisdictionLevel == JurisdictionLevel.COMMUNITY) {
			clearAndDisableFields(districtFilter, communityFilter);
		} else if (userJurisdictionLevel == JurisdictionLevel.HEALTH_FACILITY) {
			clearAndDisableFields(districtFilter, communityFilter);
		}

		ComboBox birthDateDD = getField(PersonCriteria.BIRTHDATE_DD);
		if (getField(PersonCriteria.BIRTHDATE_YYYY).getValue() != null && getField(PersonCriteria.BIRTHDATE_MM).getValue() != null) {
			birthDateDD.addItems(
				DateHelper.getDaysInMonth(
					(Integer) getField(PersonCriteria.BIRTHDATE_MM).getValue(),
					(Integer) getField(PersonCriteria.BIRTHDATE_YYYY).getValue()));
			birthDateDD.setEnabled(true);
		} else {
			birthDateDD.clear();
			birthDateDD.setEnabled(false);
		}
	}
}
