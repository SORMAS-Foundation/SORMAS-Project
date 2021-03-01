package de.symeda.sormas.ui.person;

import com.vaadin.v7.data.Property;
import com.vaadin.v7.ui.AbstractSelect;
import com.vaadin.v7.ui.ComboBox;
import com.vaadin.v7.ui.TextField;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.campaign.data.CampaignFormDataDto;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.Descriptions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.person.PersonCriteria;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.region.CommunityReferenceDto;
import de.symeda.sormas.api.region.DistrictReferenceDto;
import de.symeda.sormas.api.region.RegionReferenceDto;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.ui.utils.AbstractFilterForm;
import de.symeda.sormas.ui.utils.FieldConfiguration;
import de.symeda.sormas.ui.utils.FieldHelper;

public class PersonFilterForm extends AbstractFilterForm<PersonCriteria> {

	protected PersonFilterForm() {
		super(PersonCriteria.class, PersonDto.I18N_PREFIX);
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
		final ComboBox birthDateYYYY = addField(getContent(), PersonCriteria.BIRTHDATE_YYYY, ComboBox.class);
		birthDateYYYY.setInputPrompt(I18nProperties.getPrefixCaption(PersonDto.I18N_PREFIX, PersonDto.BIRTH_DATE_YYYY));
		birthDateYYYY.setWidth(140, Unit.PIXELS);
		birthDateYYYY.addItems(DateHelper.getYearsToNow());
		birthDateYYYY.setItemCaptionMode(AbstractSelect.ItemCaptionMode.ID_TOSTRING);
		final ComboBox birthDateMM = addField(getContent(), PersonCriteria.BIRTHDATE_MM, ComboBox.class);
		birthDateMM.setInputPrompt(I18nProperties.getPrefixCaption(PersonDto.I18N_PREFIX, PersonDto.BIRTH_DATE_MM));
		birthDateMM.setWidth(140, Unit.PIXELS);
		birthDateMM.addItems(DateHelper.getMonthsInYear());
		final ComboBox birthDateDD = addField(getContent(), PersonCriteria.BIRTHDATE_DD, ComboBox.class);
		birthDateDD.setInputPrompt(I18nProperties.getPrefixCaption(PersonDto.I18N_PREFIX, PersonDto.BIRTH_DATE_DD));
		birthDateDD.setWidth(140, Unit.PIXELS);

		final TextField searchField = addField(
			FieldConfiguration.withCaptionAndPixelSized(
				PersonCriteria.NAME_ADDRESS_PHONE_EMAIL_LIKE,
				I18nProperties.getString(Strings.promptPersonsSearchField),
				200));
		searchField.setNullRepresentation("");

		final ComboBox presentConditionField = addField(getContent(), FieldConfiguration.pixelSized(PersonCriteria.PRESENT_CONDITION, 140));
		presentConditionField.setInputPrompt(I18nProperties.getPrefixCaption(PersonDto.I18N_PREFIX, PersonDto.PRESENT_CONDITION));

		final ComboBox regionFilter = addField(getContent(), FieldConfiguration.pixelSized(PersonCriteria.REGION, 140));
		regionFilter.addItems(FacadeProvider.getRegionFacade().getAllActiveAsReference());

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
		case CampaignFormDataDto.REGION:
			RegionReferenceDto region = (RegionReferenceDto) event.getProperty().getValue();
			if (region != null) {
				districtFilter.removeAllItems();
				districtFilter.addItems(FacadeProvider.getDistrictFacade().getAllActiveByRegion(region.getUuid()));
			} else {
				districtFilter.removeAllItems();
				districtFilter.clear();
			}
			break;
		case CampaignFormDataDto.DISTRICT:
			DistrictReferenceDto district = (DistrictReferenceDto) event.getProperty().getValue();

			if (district != null) {
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
		final ComboBox regionFilter = getField(PersonCriteria.REGION);
		final ComboBox districtFilter = getField(PersonCriteria.DISTRICT);
		final ComboBox communityFilter = getField(PersonCriteria.COMMUNITY);
		UserDto user = currentUserDto();
		final RegionReferenceDto userRegion = user.getRegion();
		final DistrictReferenceDto userDistrict = user.getDistrict();
		final CommunityReferenceDto userCommunity = user.getCommunity();
		if (userRegion != null) {
			regionFilter.setEnabled(false);
			districtFilter.addItems(FacadeProvider.getDistrictFacade().getAllActiveByRegion(userRegion.getUuid()));
			if (userDistrict != null) {
				districtFilter.setEnabled(false);
				communityFilter.addItems(FacadeProvider.getCommunityFacade().getAllActiveByDistrict(userDistrict.getUuid()));
				if (userCommunity != null) {
					communityFilter.setEnabled(false);
				}
			}
		} else {
			regionFilter.clear();
		}
		if (userDistrict == null) {
			districtFilter.clear();
		}
		if (userCommunity == null) {
			communityFilter.clear();
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
