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
package de.symeda.sormas.ui.user;

import static de.symeda.sormas.ui.utils.CssStyles.H3;
import static de.symeda.sormas.ui.utils.CssStyles.VSPACE_TOP_3;
import static de.symeda.sormas.ui.utils.LayoutUtil.fluidRowLocs;
import static de.symeda.sormas.ui.utils.LayoutUtil.fluidRowLocsCss;
import static de.symeda.sormas.ui.utils.LayoutUtil.loc;

import java.util.Set;

import com.vaadin.ui.Label;
import com.vaadin.v7.data.Validator;
import com.vaadin.v7.data.util.converter.Converter;
import com.vaadin.v7.ui.CheckBox;
import com.vaadin.v7.ui.ComboBox;
import com.vaadin.v7.ui.Field;
import com.vaadin.v7.ui.OptionGroup;
import com.vaadin.v7.ui.TextField;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.feature.FeatureType;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.i18n.Validations;
import de.symeda.sormas.api.region.DistrictReferenceDto;
import de.symeda.sormas.api.region.RegionReferenceDto;
import de.symeda.sormas.api.user.JurisdictionLevel;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.api.user.UserHelper;
import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.api.utils.fieldaccess.UiFieldAccessCheckers;
import de.symeda.sormas.api.utils.fieldvisibility.FieldVisibilityCheckers;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.location.LocationEditForm;
import de.symeda.sormas.ui.utils.AbstractEditForm;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.FieldHelper;
import de.symeda.sormas.ui.utils.UserPhoneNumberValidator;

public class UserEditForm extends AbstractEditForm<UserDto> {

	private static final long serialVersionUID = 1L;

	private static final String PERSON_DATA_HEADING_LOC = "personDataHeadingLoc";
	private static final String ADDRESS_HEADING_LOC = "addressHeadingLoc";
	private static final String USER_DATA_HEADING_LOC = "userDataHeadingLoc";
	private static final String USER_EMAIL_DESC_LOC = "userEmailDescLoc";
	private static final String USER_PHONE_DESC_LOC = "userPhoneDescLoc";

	//@formatter:off
    private static final String HTML_LAYOUT =
            loc(PERSON_DATA_HEADING_LOC) +
                    fluidRowLocs(UserDto.FIRST_NAME, UserDto.LAST_NAME) +
                    fluidRowLocs(UserDto.USER_EMAIL, UserDto.PHONE) +
                    fluidRowLocs(USER_EMAIL_DESC_LOC, USER_PHONE_DESC_LOC) +
                    fluidRowLocsCss(VSPACE_TOP_3, UserDto.LANGUAGE, "") +

                    loc(ADDRESS_HEADING_LOC) +
                    fluidRowLocs(UserDto.ADDRESS) +

					loc(USER_DATA_HEADING_LOC) +
                    fluidRowLocs(UserDto.ACTIVE) +
                    fluidRowLocs(UserDto.USER_NAME, UserDto.USER_ROLES) +
                    fluidRowLocs(UserDto.REGION, UserDto.DISTRICT, UserDto.COMMUNITY) +
                    fluidRowLocs(UserDto.HEALTH_FACILITY, UserDto.POINT_OF_ENTRY, UserDto.ASSOCIATED_OFFICER, UserDto.LABORATORY) +
                    fluidRowLocs(UserDto.LIMITED_DISEASE, "", "");
    //@formatter:off

    public UserEditForm(boolean create) {

        super(UserDto.class, UserDto.I18N_PREFIX, true, new FieldVisibilityCheckers(), UiFieldAccessCheckers.getNoop());


        setWidth(640, Unit.PIXELS);

        if (create) {
            hideValidationUntilNextCommit();
        }
    }

    @Override
    protected void addFields() {

        Label personDataHeadingLabel = new Label(I18nProperties.getString(Strings.headingPersonData));
        personDataHeadingLabel.addStyleName(H3);
        getContent().addComponent(personDataHeadingLabel, PERSON_DATA_HEADING_LOC);

        Label addressHeadingLabel = new Label(I18nProperties.getString(Strings.address));
        addressHeadingLabel.addStyleName(H3);
        getContent().addComponent(addressHeadingLabel, ADDRESS_HEADING_LOC);

		Label userDataHeadingLabel = new Label(I18nProperties.getString(Strings.headingUserData));
		userDataHeadingLabel.addStyleName(H3);
		getContent().addComponent(userDataHeadingLabel, USER_DATA_HEADING_LOC);

        addField(UserDto.FIRST_NAME, TextField.class);
        addField(UserDto.LAST_NAME, TextField.class);
        addField(UserDto.USER_EMAIL, TextField.class);
        TextField phone = addField(UserDto.PHONE, TextField.class);
        phone.addValidator(new UserPhoneNumberValidator(I18nProperties.getValidationError(Validations.phoneNumberValidation)));
        if (FacadeProvider.getFeatureConfigurationFacade().isFeatureEnabled(FeatureType.AGGREGATE_REPORTING) || FacadeProvider.getFeatureConfigurationFacade().isFeatureEnabled(FeatureType.EVENT_SURVEILLANCE) || FacadeProvider.getFeatureConfigurationFacade().isFeatureEnabled(FeatureType.WEEKLY_REPORTING) || FacadeProvider.getFeatureConfigurationFacade().isFeatureEnabled(FeatureType.CASE_SURVEILANCE)) {
            addDiseaseField(UserDto.LIMITED_DISEASE, false);
        }

        Label userEmailDesc = new Label(I18nProperties.getString(Strings.infoUserEmail));
        getContent().addComponent(userEmailDesc, USER_EMAIL_DESC_LOC);
        Label userPhoneDesc = new Label(I18nProperties.getString(Strings.infoUserPhoneNumber));
        getContent().addComponent(userPhoneDesc, USER_PHONE_DESC_LOC);

        ComboBox cbLanguage = addField(UserDto.LANGUAGE, ComboBox.class);
        CssStyles.style(cbLanguage, CssStyles.COMBO_BOX_WITH_FLAG_ICON);
        ControllerProvider.getUserController().setFlagIcons(cbLanguage);

        addField(UserDto.ADDRESS, LocationEditForm.class).setCaption(null);

        addField(UserDto.ACTIVE, CheckBox.class);
        addField(UserDto.USER_NAME, TextField.class);
        addField(UserDto.USER_ROLES, OptionGroup.class).addValidator(new UserRolesValidator());
        OptionGroup userRoles = (OptionGroup) getFieldGroup().getField(UserDto.USER_ROLES);
        userRoles.setMultiSelect(true);

        ComboBox region = addInfrastructureField(UserDto.REGION);
        ComboBox community = addInfrastructureField(UserDto.COMMUNITY);

        ComboBox district = addInfrastructureField(UserDto.DISTRICT);
        region.addValueChangeListener(e -> {
            FieldHelper.removeItems(community);
            RegionReferenceDto regionDto = (RegionReferenceDto) e.getProperty().getValue();
            FieldHelper
                    .updateItems(district, regionDto != null ? FacadeProvider.getDistrictFacade().getAllActiveByRegion(regionDto.getUuid()) : null);
        });

        // for informant
        ComboBox associatedOfficer = addField(UserDto.ASSOCIATED_OFFICER, ComboBox.class);

        ComboBox healthFacility = addInfrastructureField(UserDto.HEALTH_FACILITY);
        ComboBox cbPointOfEntry = addInfrastructureField(UserDto.POINT_OF_ENTRY);
        district.addValueChangeListener(e -> {
            FieldHelper.removeItems(healthFacility);
            FieldHelper.removeItems(associatedOfficer);
            FieldHelper.removeItems(cbPointOfEntry);
            DistrictReferenceDto districtDto = (DistrictReferenceDto) e.getProperty().getValue();
            FieldHelper.updateItems(
                    community,
                    districtDto != null ? FacadeProvider.getCommunityFacade().getAllActiveByDistrict(districtDto.getUuid()) : null);
            FieldHelper.updateItems(
                    healthFacility,
                    districtDto != null ? FacadeProvider.getFacilityFacade().getActiveHospitalsByDistrict(districtDto, false) : null);
            FieldHelper.updateItems(
                    associatedOfficer,
                    districtDto != null ? FacadeProvider.getUserFacade().getUserRefsByDistrict(districtDto, false, UserRole.SURVEILLANCE_OFFICER) : null);
            FieldHelper.updateItems(
                    cbPointOfEntry,
                    districtDto != null ? FacadeProvider.getPointOfEntryFacade().getAllActiveByDistrict(districtDto.getUuid(), false) : null);
        });

        ComboBox laboratory = addField(UserDto.LABORATORY, ComboBox.class);
        laboratory.addItems(FacadeProvider.getFacilityFacade().getAllActiveLaboratories(false));

        region.addItems(FacadeProvider.getRegionFacade().getAllActiveByServerCountry());

        setRequired(true, UserDto.FIRST_NAME, UserDto.LAST_NAME, UserDto.USER_NAME, UserDto.USER_ROLES);
        addValidators(UserDto.USER_NAME, new UserNameValidator());

        addFieldListeners(UserDto.FIRST_NAME, e -> suggestUserName());
        addFieldListeners(UserDto.LAST_NAME, e -> suggestUserName());
        addFieldListeners(UserDto.USER_ROLES, e -> updateFieldsByUserRole());
        updateFieldsByUserRole();
    }

    @SuppressWarnings("unchecked")
    private void updateFieldsByUserRole() {

		final Field userRolesField = getFieldGroup().getField(UserDto.USER_ROLES);
		final Set<UserRole> userRoles = (Set<UserRole>) userRolesField.getValue();

		final JurisdictionLevel jurisdictionLevel = UserRole.getJurisdictionLevel(userRoles);

		final boolean hasAssociatedOfficer = UserRole.hasAssociatedOfficer(userRoles);
		final boolean hasOptionalHealthFacility = UserRole.hasOptionalHealthFacility(userRoles);
		final boolean isLabUser = UserRole.isLabUser(userRoles);
		final boolean isPortHealthUser = UserRole.isPortHealthUser(userRoles);

		final boolean usePointOfEntry = (isPortHealthUser && hasAssociatedOfficer) || jurisdictionLevel == JurisdictionLevel.POINT_OF_ENTRY;
		final boolean useHealthFacility = jurisdictionLevel == JurisdictionLevel.HEALTH_FACILITY;
		final boolean useCommunity = jurisdictionLevel == JurisdictionLevel.COMMUNITY;
		final boolean useDistrict = hasAssociatedOfficer || jurisdictionLevel == JurisdictionLevel.DISTRICT	|| useCommunity || useHealthFacility || usePointOfEntry;;
		final boolean useRegion = jurisdictionLevel == JurisdictionLevel.REGION || useDistrict;

		final ComboBox associatedOfficer = (ComboBox) getFieldGroup().getField(UserDto.ASSOCIATED_OFFICER);
		associatedOfficer.setVisible(hasAssociatedOfficer);
		setRequired(hasAssociatedOfficer && !isPortHealthUser, UserDto.ASSOCIATED_OFFICER);
		if (!hasAssociatedOfficer) {
			associatedOfficer.clear();
		}

		final ComboBox community = (ComboBox) getFieldGroup().getField(UserDto.COMMUNITY);
		community.setVisible(useCommunity);
		setRequired(useCommunity, UserDto.COMMUNITY);
		if (!useCommunity) {
			community.clear();
		}

		final ComboBox healthFacility = (ComboBox) getFieldGroup().getField(UserDto.HEALTH_FACILITY);
		healthFacility.setVisible(hasOptionalHealthFacility || useHealthFacility);
		setRequired(useHealthFacility, UserDto.HEALTH_FACILITY);
		if (!healthFacility.isVisible()) {
			healthFacility.clear();
		}

		final ComboBox laboratory = (ComboBox) getFieldGroup().getField(UserDto.LABORATORY);
		laboratory.setVisible(isLabUser);
		setRequired(isLabUser, UserDto.LABORATORY);
		if (!isLabUser) {
			laboratory.clear();
		}

		final ComboBox pointOfEntry = (ComboBox) getFieldGroup().getField(UserDto.POINT_OF_ENTRY);
		pointOfEntry.setVisible(usePointOfEntry);
		setRequired(usePointOfEntry, UserDto.POINT_OF_ENTRY);
		if (!usePointOfEntry) {
			pointOfEntry.clear();
		}

		final ComboBox district = (ComboBox) getFieldGroup().getField(UserDto.DISTRICT);
		district.setVisible(useDistrict);
		setRequired(useDistrict, UserDto.DISTRICT);
		if (!useDistrict) {
			district.clear();
		}

		final ComboBox region = (ComboBox) getFieldGroup().getField(UserDto.REGION);
		region.setVisible(useRegion);
		setRequired(useRegion, UserDto.REGION);
		if (!useRegion) {
			region.clear();
		}
	}

    private void suggestUserName() {
        TextField fnField = (TextField) getFieldGroup().getField(UserDto.FIRST_NAME);
        TextField lnField = (TextField) getFieldGroup().getField(UserDto.LAST_NAME);
        TextField unField = (TextField) getFieldGroup().getField(UserDto.USER_NAME);
        if (!fnField.isEmpty() && !lnField.isEmpty() && unField.isEmpty() && !unField.isReadOnly()) {
            unField.setValue(UserHelper.getSuggestedUsername(fnField.getValue(), lnField.getValue()));
        }
    }

    @Override
    protected String createHtmlLayout() {
        return HTML_LAYOUT;
    }

    class UserNameValidator implements Validator {

        private static final long serialVersionUID = 1L;

        @Override
        public void validate(Object value) throws InvalidValueException {
            UserDto dto = getValue();
            if (!(value instanceof String && ControllerProvider.getUserController().isLoginUnique(dto.getUuid(), (String) value)))
                throw new InvalidValueException(I18nProperties.getValidationError(Validations.userNameNotUnique));
        }
    }

    @Override
    public void setValue(UserDto userDto) throws com.vaadin.v7.data.Property.ReadOnlyException, Converter.ConversionException {

        OptionGroup userRoles = (OptionGroup) getFieldGroup().getField(UserDto.USER_ROLES);
        userRoles.removeAllItems();
        userRoles.addItems(UserUiHelper.getAssignableRoles(userDto.getUserRoles()));
        
        super.setValue(userDto);
    }
}
