package de.symeda.sormas.ui.person;

import com.vaadin.v7.data.Property;
import com.vaadin.v7.ui.AbstractSelect;
import com.vaadin.v7.ui.ComboBox;
import com.vaadin.v7.ui.TextField;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.i18n.Descriptions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.person.PersonCriteria;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.region.DistrictReferenceDto;
import de.symeda.sormas.api.region.RegionReferenceDto;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.api.utils.DataHelper;
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

		final UserDto user = currentUserDto();
		ComboBox regionField = null;
		if (user.getRegion() == null) {
			regionField = addField(getContent(), FieldConfiguration.pixelSized(PersonCriteria.REGION, 140));
			regionField.addItems(FacadeProvider.getRegionFacade().getAllActiveAsReference());
		}

		ComboBox districtField = addField(getContent(), FieldConfiguration.pixelSized(PersonCriteria.DISTRICT, 140));
		districtField.setDescription(I18nProperties.getDescription(Descriptions.descDistrictFilter));

		addField(getContent(), FieldConfiguration.pixelSized(PersonCriteria.COMMUNITY, 140));

	}

	@Override
	protected void applyDependenciesOnFieldChange(String propertyId, Property.ValueChangeEvent event) {
		super.applyDependenciesOnFieldChange(propertyId, event);

		final PersonCriteria criteria = getValue();
		final ComboBox districtField = getField(PersonCriteria.DISTRICT);
		final ComboBox communityField = getField(PersonCriteria.COMMUNITY);

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
		case PersonCriteria.REGION: {
			final UserDto user = currentUserDto();
			final RegionReferenceDto region = user.getRegion() != null ? user.getRegion() : (RegionReferenceDto) event.getProperty().getValue();
			if (!DataHelper.equal(region, criteria.getRegion())) {
				if (region != null) {
					enableFields(districtField);
					FieldHelper.updateItems(districtField, FacadeProvider.getDistrictFacade().getAllActiveByRegion(region.getUuid()));
				} else {
					clearAndDisableFields(districtField);
				}
			}
			break;
		}
		case PersonCriteria.DISTRICT: {
			final DistrictReferenceDto newDistrict = (DistrictReferenceDto) event.getProperty().getValue();
			if (!DataHelper.equal(newDistrict, criteria.getDistrict())) {
				if (newDistrict != null) {
					enableFields(communityField);
					FieldHelper.updateItems(communityField, FacadeProvider.getCommunityFacade().getAllActiveByDistrict(newDistrict.getUuid()));
				} else {
					clearAndDisableFields(communityField);
				}
			}
			break;
		}
		}
	}
}
