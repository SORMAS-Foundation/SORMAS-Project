package de.symeda.sormas.ui.immunization.components.filter;

import com.vaadin.v7.data.Property;
import com.vaadin.v7.ui.AbstractSelect;
import com.vaadin.v7.ui.ComboBox;
import com.vaadin.v7.ui.TextField;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.facility.FacilityDto;
import de.symeda.sormas.api.facility.FacilityType;
import de.symeda.sormas.api.facility.FacilityTypeGroup;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.Descriptions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.immunization.ImmunizationCriteria;
import de.symeda.sormas.api.immunization.ImmunizationDto;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.region.CommunityReferenceDto;
import de.symeda.sormas.api.region.DistrictReferenceDto;
import de.symeda.sormas.api.region.RegionReferenceDto;
import de.symeda.sormas.api.user.JurisdictionLevel;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.ui.UserProvider;
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
			ImmunizationCriteria.COMMUNITY,
			ImmunizationCriteria.FACILITY_TYPE_GROUP,
			ImmunizationCriteria.FACILITY_TYPE,
			ImmunizationCriteria.HEALTH_FACILITY };
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

		ComboBox typeGroup = addField(getContent(), FieldConfiguration.pixelSized(ImmunizationCriteria.FACILITY_TYPE_GROUP, 140));
		typeGroup.setInputPrompt(I18nProperties.getCaption(Captions.Facility_typeGroup));
		typeGroup.removeAllItems();
		FieldHelper.updateEnumData(typeGroup, FacilityTypeGroup.getAccomodationGroups());

		ComboBox type = addField(getContent(), FieldConfiguration.pixelSized(ImmunizationCriteria.FACILITY_TYPE, 140));
		type.setInputPrompt(I18nProperties.getPrefixCaption(FacilityDto.I18N_PREFIX, FacilityDto.TYPE));
		type.removeAllItems();

		ComboBox facilityField = addField(getContent(), FieldConfiguration.pixelSized(ImmunizationCriteria.HEALTH_FACILITY, 140));
		facilityField.setDescription(I18nProperties.getDescription(Descriptions.descFacilityFilter));
	}

	@Override
	protected void applyDependenciesOnFieldChange(String propertyId, Property.ValueChangeEvent event) {
		super.applyDependenciesOnFieldChange(propertyId, event);

		final ImmunizationCriteria criteria = getValue();

		final ComboBox regionField = getField(ImmunizationCriteria.REGION);
		final ComboBox districtField = getField(ImmunizationCriteria.DISTRICT);
		final ComboBox communityField = getField(ImmunizationCriteria.COMMUNITY);
		final ComboBox facilityTypeGroupField = getField(ImmunizationCriteria.FACILITY_TYPE_GROUP);
		final ComboBox facilityTypeField = getField(ImmunizationCriteria.FACILITY_TYPE);
		final ComboBox facilityField = getField(ImmunizationCriteria.HEALTH_FACILITY);

		final UserDto user = currentUserDto();
		final DistrictReferenceDto currentDistrict =
			user.getDistrict() != null ? user.getDistrict() : (DistrictReferenceDto) districtField.getValue();

		switch (propertyId) {
		case ImmunizationCriteria.REGION: {
			final RegionReferenceDto region = user.getRegion() != null ? user.getRegion() : (RegionReferenceDto) event.getProperty().getValue();

			if (!DataHelper.equal(region, criteria.getRegion())) {
				if (region != null) {
					enableFields(districtField);
					FieldHelper.updateItems(districtField, FacadeProvider.getDistrictFacade().getAllActiveByRegion(region.getUuid()));
				} else {
					clearAndDisableFields(districtField);
				}
				clearAndDisableFields(communityField, facilityField, facilityTypeField, facilityTypeGroupField);
			}

			break;
		}
		case ImmunizationCriteria.DISTRICT: {
			final DistrictReferenceDto newDistrict = (DistrictReferenceDto) event.getProperty().getValue();

			if (!DataHelper.equal(newDistrict, criteria.getDistrict())) {
				if (newDistrict != null) {
					enableFields(communityField, facilityTypeGroupField);

					clearAndDisableFields(facilityField);
					if (facilityTypeGroupField != null) {
						if (facilityTypeGroupField.getValue() != null && facilityTypeField.getValue() != null) {
							FieldHelper.updateItems(
								facilityField,
								FacadeProvider.getFacilityFacade()
									.getActiveFacilitiesByDistrictAndType(newDistrict, (FacilityType) facilityTypeField.getValue(), true, false));
							enableFields(facilityField);
						} else {
							FieldHelper.updateEnumData(facilityTypeGroupField, FacilityTypeGroup.getAccomodationGroups());
						}
					}

					FieldHelper.updateItems(communityField, FacadeProvider.getCommunityFacade().getAllActiveByDistrict(newDistrict.getUuid()));
				} else {
					clearAndDisableFields(communityField, facilityField, facilityTypeField, facilityTypeGroupField);
				}
			}

			break;
		}
		case ImmunizationCriteria.COMMUNITY: {
			CommunityReferenceDto community = (CommunityReferenceDto) event.getProperty().getValue();
			if (!DataHelper.equal(community, criteria.getCommunity())) {
				if (facilityField != null) {
					facilityField.setValue(null);
				}

				final FacilityType facilityType = facilityTypeField != null ? (FacilityType) facilityTypeField.getValue() : null;

				if (facilityType == null && facilityField != null) {
					facilityField.removeAllItems();
				} else if (facilityField != null) {
					if (community == null) {
						FieldHelper.updateItems(
							facilityField,
							FacadeProvider.getFacilityFacade().getActiveFacilitiesByDistrictAndType(currentDistrict, facilityType, true, false));
					} else {
						FieldHelper.updateItems(
							facilityField,
							FacadeProvider.getFacilityFacade().getActiveFacilitiesByCommunityAndType(community, facilityType, true, false));
					}
				}
			}
			break;
		}
		case ImmunizationCriteria.FACILITY_TYPE_GROUP: {
			FacilityTypeGroup typeGroup = (FacilityTypeGroup) event.getProperty().getValue();
			if (!DataHelper.equal(typeGroup, criteria.getFacilityTypeGroup())) {
				if (typeGroup != null) {
					enableFields(ImmunizationCriteria.FACILITY_TYPE);
					FieldHelper.updateEnumData(facilityTypeField, FacilityType.getAccommodationTypes(typeGroup));
					facilityField.setValue(null);
				} else {
					clearAndDisableFields(facilityTypeField, facilityField);
				}
			}

			break;
		}
		case ImmunizationCriteria.FACILITY_TYPE: {
			FacilityType facilityType = (FacilityType) event.getProperty().getValue();
			if (!DataHelper.equal(facilityType, criteria.getFacilityType())) {
				if (facilityType == null) {
					clearAndDisableFields(facilityField);
				} else {
					enableFields(facilityField);
					facilityField.setValue(null);

					CommunityReferenceDto community = (CommunityReferenceDto) communityField.getValue();
					if (community != null) {
						FieldHelper.updateItems(
							facilityField,
							FacadeProvider.getFacilityFacade().getActiveFacilitiesByCommunityAndType(community, facilityType, true, false));
					} else if (currentDistrict != null) {
						FieldHelper.updateItems(
							facilityField,
							FacadeProvider.getFacilityFacade().getActiveFacilitiesByDistrictAndType(currentDistrict, facilityType, true, false));
					}
				}
			}
			break;
		}
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
		}
	}

	@Override
	protected void applyDependenciesOnNewValue(ImmunizationCriteria criteria) {
		final UserDto user = currentUserDto();
		final JurisdictionLevel userJurisdictionLevel = UserRole.getJurisdictionLevel(UserProvider.getCurrent().getUserRoles());

		final ComboBox districtField = getField(ImmunizationCriteria.DISTRICT);
		final ComboBox communityField = getField(ImmunizationCriteria.COMMUNITY);
		final ComboBox facilityTypeGroupField = getField(ImmunizationCriteria.FACILITY_TYPE_GROUP);
		final ComboBox facilityTypeField = getField(ImmunizationCriteria.FACILITY_TYPE);
		final ComboBox facilityField = getField(ImmunizationCriteria.HEALTH_FACILITY);

		// Disable all fields
		clearAndDisableFields(districtField, communityField, facilityTypeGroupField, facilityTypeField, facilityField);

		// Get initial field values according to user and criteria
		final RegionReferenceDto region = user.getRegion() == null ? criteria.getRegion() : user.getRegion();
		final DistrictReferenceDto district = user.getDistrict() == null ? criteria.getDistrict() : user.getDistrict();
		final CommunityReferenceDto community = user.getCommunity() == null ? criteria.getCommunity() : user.getCommunity();
		final FacilityTypeGroup facilityTypeGroup = criteria.getFacilityTypeGroup();
		final FacilityType facilityType = criteria.getFacilityType();

		// district
		if (region != null) {
			enableFields(districtField);
			districtField.addItems(FacadeProvider.getDistrictFacade().getAllActiveByRegion(region.getUuid()));
			// community
			if (district != null) {
				districtField.setValue(district);
				communityField.addItems(FacadeProvider.getCommunityFacade().getAllActiveByDistrict(district.getUuid()));
				enableFields(communityField);
				if (community != null) {
					communityField.setValue(community);
				}
			} else {
				clearAndDisableFields(communityField);
			}
		} else {
			clearAndDisableFields(districtField, communityField);
		}

		// facility
		if (userJurisdictionLevel == JurisdictionLevel.HEALTH_FACILITY) {
			facilityField.setValue(user.getHealthFacility());
			disableFields(facilityTypeGroupField, facilityTypeField, facilityField);
		} else if (facilityTypeGroupField != null && district != null) {
			enableFields(facilityTypeGroupField);
			FieldHelper.updateEnumData(facilityTypeGroupField, FacilityTypeGroup.getAccomodationGroups());
			if (facilityTypeGroup != null) {
				facilityTypeGroupField.setValue(facilityTypeGroup);
				enableFields(facilityTypeField);
				FieldHelper.updateEnumData(facilityTypeField, FacilityType.getAccommodationTypes(facilityTypeGroup));
				if (facilityType != null) {
					facilityTypeField.setValue(facilityType);
					enableFields(facilityField);
					if (community != null) {
						facilityField
							.addItems(FacadeProvider.getFacilityFacade().getActiveFacilitiesByCommunityAndType(community, facilityType, true, false));
					} else {
						facilityField
							.addItems(FacadeProvider.getFacilityFacade().getActiveFacilitiesByDistrictAndType(district, facilityType, true, false));
					}
				} else {
					disableFields(facilityField);
				}
			} else {
				disableFields(facilityTypeField);
			}
		}

		// Disable fields according to user & jurisdiction
		if (userJurisdictionLevel == JurisdictionLevel.DISTRICT) {
			clearAndDisableFields(districtField);
		} else if (userJurisdictionLevel == JurisdictionLevel.COMMUNITY) {
			clearAndDisableFields(districtField, communityField);
		} else if (userJurisdictionLevel == JurisdictionLevel.HEALTH_FACILITY) {
			clearAndDisableFields(districtField, communityField, facilityTypeGroupField, facilityTypeField, facilityField);
		}

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
