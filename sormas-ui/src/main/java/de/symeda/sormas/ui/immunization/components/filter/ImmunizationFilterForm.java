package de.symeda.sormas.ui.immunization.components.filter;

import com.vaadin.v7.data.Property;
import com.vaadin.v7.ui.AbstractSelect;
import com.vaadin.v7.ui.ComboBox;
import com.vaadin.v7.ui.TextField;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.i18n.Descriptions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.immunization.ImmunizationCriteria;
import de.symeda.sormas.api.immunization.ImmunizationDto;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.region.DistrictReferenceDto;
import de.symeda.sormas.api.region.RegionReferenceDto;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.ui.utils.AbstractFilterForm;
import de.symeda.sormas.ui.utils.FieldConfiguration;
import de.symeda.sormas.ui.utils.FieldHelper;

public class ImmunizationFilterForm extends AbstractFilterForm<ImmunizationCriteria> {

	public ImmunizationFilterForm() {
		super(ImmunizationCriteria.class, ImmunizationDto.I18N_PREFIX);
	}

	@Override
	protected String[] getMainFilterLocators() {
		return new String[] {
			ImmunizationCriteria.DISEASE,
			ImmunizationCriteria.NAME_ADDRESS_PHONE_EMAIL_LIKE,
			ImmunizationCriteria.BIRTHDATE_YYYY,
			ImmunizationCriteria.BIRTHDATE_MM,
			ImmunizationCriteria.BIRTHDATE_DD,
			ImmunizationCriteria.MEANS_OF_IMMUNIZATION,
			ImmunizationCriteria.MANAGEMENT_STATUS,
			ImmunizationCriteria.IMMUNIZATION_STATUS,
			ImmunizationCriteria.REGION,
			ImmunizationCriteria.DISTRICT,
			ImmunizationCriteria.COMMUNITY };
	}

	@Override
	protected void addFields() {
		addField(FieldConfiguration.pixelSized(ImmunizationDto.DISEASE, 140));

		final TextField searchField = addField(
			FieldConfiguration.withCaptionAndPixelSized(
				ImmunizationCriteria.NAME_ADDRESS_PHONE_EMAIL_LIKE,
				I18nProperties.getString(Strings.promptPersonsSearchField),
				200));
		searchField.setNullRepresentation("");

		final ComboBox birthDateYYYY = addField(getContent(), ImmunizationCriteria.BIRTHDATE_YYYY, ComboBox.class);
		birthDateYYYY.setInputPrompt(I18nProperties.getPrefixCaption(PersonDto.I18N_PREFIX, PersonDto.BIRTH_DATE_YYYY));
		birthDateYYYY.setWidth(140, Unit.PIXELS);
		birthDateYYYY.addItems(DateHelper.getYearsToNow());
		birthDateYYYY.setItemCaptionMode(AbstractSelect.ItemCaptionMode.ID_TOSTRING);
		final ComboBox birthDateMM = addField(getContent(), ImmunizationCriteria.BIRTHDATE_MM, ComboBox.class);
		birthDateMM.setInputPrompt(I18nProperties.getPrefixCaption(PersonDto.I18N_PREFIX, PersonDto.BIRTH_DATE_MM));
		birthDateMM.setWidth(140, Unit.PIXELS);
		birthDateMM.addItems(DateHelper.getMonthsInYear());
		final ComboBox birthDateDD = addField(getContent(), ImmunizationCriteria.BIRTHDATE_DD, ComboBox.class);
		birthDateDD.setInputPrompt(I18nProperties.getPrefixCaption(PersonDto.I18N_PREFIX, PersonDto.BIRTH_DATE_DD));
		birthDateDD.setWidth(140, Unit.PIXELS);

		addFields(
			FieldConfiguration.pixelSized(ImmunizationDto.MEANS_OF_IMMUNIZATION, 140),
			FieldConfiguration.pixelSized(ImmunizationDto.MANAGEMENT_STATUS, 140),
			FieldConfiguration.pixelSized(ImmunizationDto.IMMUNIZATION_STATUS, 140));

		final ComboBox regionFilter = addField(getContent(), FieldConfiguration.pixelSized(ImmunizationCriteria.REGION, 140));
		regionFilter.addItems(FacadeProvider.getRegionFacade().getAllActiveByServerCountry());

		final ComboBox districtFilter = addField(getContent(), FieldConfiguration.pixelSized(ImmunizationCriteria.DISTRICT, 140));
		districtFilter.setDescription(I18nProperties.getDescription(Descriptions.descDistrictFilter));

		addField(getContent(), FieldConfiguration.pixelSized(ImmunizationCriteria.COMMUNITY, 140));
	}

	@Override
	protected void applyDependenciesOnFieldChange(String propertyId, Property.ValueChangeEvent event) {
		super.applyDependenciesOnFieldChange(propertyId, event);

		final ComboBox districtFilter = getField(ImmunizationCriteria.DISTRICT);
		final ComboBox communityFilter = getField(ImmunizationCriteria.COMMUNITY);

		switch (propertyId) {
		case ImmunizationCriteria.BIRTHDATE_MM: {
			Integer birthMM = (Integer) event.getProperty().getValue();

			ComboBox birthDayDD = getField(ImmunizationCriteria.BIRTHDATE_DD);
			birthDayDD.setEnabled(birthMM != null);
			FieldHelper.updateItems(
				birthDayDD,
				DateHelper.getDaysInMonth(
					(Integer) getField(ImmunizationCriteria.BIRTHDATE_MM).getValue(),
					(Integer) getField(ImmunizationCriteria.BIRTHDATE_YYYY).getValue()));

			break;
		}
		case ImmunizationCriteria.REGION:
			RegionReferenceDto region = (RegionReferenceDto) event.getProperty().getValue();
			if (region != null) {
				districtFilter.removeAllItems();
				districtFilter.addItems(FacadeProvider.getDistrictFacade().getAllActiveByRegion(region.getUuid()));
			} else {
				districtFilter.removeAllItems();
				districtFilter.clear();
			}
			break;
		case ImmunizationCriteria.DISTRICT:
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
	protected void applyDependenciesOnNewValue(ImmunizationCriteria criteria) {
		final ComboBox regionFilter = getField(ImmunizationCriteria.REGION);
		final ComboBox districtFilter = getField(ImmunizationCriteria.DISTRICT);
		final ComboBox communityFilter = getField(ImmunizationCriteria.COMMUNITY);

		regionFilter.clear();
		districtFilter.clear();
		communityFilter.clear();

		ComboBox birthDateDD = getField(ImmunizationCriteria.BIRTHDATE_DD);
		if (getField(ImmunizationCriteria.BIRTHDATE_YYYY).getValue() != null && getField(ImmunizationCriteria.BIRTHDATE_MM).getValue() != null) {
			birthDateDD.addItems(
				DateHelper.getDaysInMonth(
					(Integer) getField(ImmunizationCriteria.BIRTHDATE_MM).getValue(),
					(Integer) getField(ImmunizationCriteria.BIRTHDATE_YYYY).getValue()));
			birthDateDD.setEnabled(true);
		} else {
			birthDateDD.clear();
			birthDateDD.setEnabled(false);
		}
	}
}
