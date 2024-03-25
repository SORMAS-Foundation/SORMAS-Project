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
import static java.util.function.Predicate.not;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;

import com.vaadin.ui.Label;
import com.vaadin.v7.data.Validator;
import com.vaadin.v7.data.util.converter.Converter;
import com.vaadin.v7.ui.CheckBox;
import com.vaadin.v7.ui.ComboBox;
import com.vaadin.v7.ui.OptionGroup;
import com.vaadin.v7.ui.TextField;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.feature.FeatureType;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.i18n.Validations;
import de.symeda.sormas.api.infrastructure.district.DistrictReferenceDto;
import de.symeda.sormas.api.infrastructure.region.RegionReferenceDto;
import de.symeda.sormas.api.user.JurisdictionLevel;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.api.user.UserHelper;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.api.user.UserRoleDto;
import de.symeda.sormas.api.user.UserRoleFacade;
import de.symeda.sormas.api.user.UserRoleReferenceDto;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.api.utils.fieldaccess.UiFieldAccessCheckers;
import de.symeda.sormas.api.utils.fieldvisibility.FieldVisibilityCheckers;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.location.LocationEditForm;
import de.symeda.sormas.ui.utils.AbstractEditForm;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.FieldHelper;
import de.symeda.sormas.ui.utils.UserPhoneNumberValidator;
import de.symeda.sormas.ui.utils.components.CheckboxSet;

public class UserEditForm extends AbstractEditForm<UserDto> {

	private static final long serialVersionUID = 1L;

	private static final String PERSON_DATA_HEADING_LOC = "personDataHeadingLoc";
	private static final String ADDRESS_HEADING_LOC = "addressHeadingLoc";
	private static final String USER_DATA_HEADING_LOC = "userDataHeadingLoc";
	private static final String USER_EMAIL_DESC_LOC = "userEmailDescLoc";
	private static final String USER_PHONE_DESC_LOC = "userPhoneDescLoc";
	private static final String LIMITED_DISEASES_HEADING_LOC = "limitedDiseasesHeadingLoc";
	public static final String RESTRICT_DISEASES_CHECKBOX_LOC = "restrictDiseasesCheckboxLoc";
	private static final String RESTRICT_DISEASES_DESCRIPTION_LOC = "restrictDiseasesDescriptionLoc";

	//@formatter:off
    private static final String HTML_LAYOUT =
            loc(UserDto.UUID) +
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
                    fluidRowLocs(LIMITED_DISEASES_HEADING_LOC) +
                    fluidRowLocs(RESTRICT_DISEASES_CHECKBOX_LOC) +
                    fluidRowLocs(RESTRICT_DISEASES_DESCRIPTION_LOC) +
                    fluidRowLocs(UserDto.LIMITED_DISEASES);
    //@formatter:off

    private Map<UserRoleReferenceDto, UserRoleDto> userRoleMap;

    private CheckBox restrictDiseasesCheckbox;

    private CheckboxSet<Disease> diseasesCheckboxSet;
    
    public UserEditForm(boolean create) {

        super(UserDto.class, UserDto.I18N_PREFIX, true, new FieldVisibilityCheckers(), UiFieldAccessCheckers.getNoop());
        setWidth(640, Unit.PIXELS);
        if (create) {
            hideValidationUntilNextCommit();
        }
    }

    @Override
    protected void addFields() {

        TextField uuid = addField(UserDto.UUID, TextField.class);
        uuid.setReadOnly(true);
        
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
            Label limitedDiseasesHeadingLabel = new Label(I18nProperties.getString(Strings.headingLimitedDiseases));
            limitedDiseasesHeadingLabel.addStyleName(H3);
            getContent().addComponent(limitedDiseasesHeadingLabel, LIMITED_DISEASES_HEADING_LOC);

            Label restrictDiseasesDescriptionLabel = new Label(I18nProperties.getString(Strings.infoRestrictDiseasesDescription));
            restrictDiseasesDescriptionLabel.addStyleNames(CssStyles.LABEL_ITALIC, CssStyles.VSPACE_TOP_3);
            restrictDiseasesDescriptionLabel.setVisible(false);
            getContent().addComponent(restrictDiseasesDescriptionLabel, RESTRICT_DISEASES_DESCRIPTION_LOC);

            diseasesCheckboxSet = addField(UserDto.LIMITED_DISEASES, CheckboxSet.class);
            diseasesCheckboxSet.setColumnCount(3);
            diseasesCheckboxSet.setCaption(null);
            diseasesCheckboxSet.setVisible(false);

            restrictDiseasesCheckbox = addCustomField(RESTRICT_DISEASES_CHECKBOX_LOC, I18nProperties.getCaption(Captions.userRestrictDiseases), Boolean.class, CheckBox.class);
            restrictDiseasesCheckbox.addValueChangeListener(e -> {
                boolean restrictDiseases = (boolean) e.getProperty().getValue();
                if (restrictDiseases) {
                    restrictDiseasesDescriptionLabel.setVisible(true);
                    diseasesCheckboxSet.setVisible(true);
                } else {
                    restrictDiseasesDescriptionLabel.setVisible(false);
                    diseasesCheckboxSet.setVisible(false);
                    diseasesCheckboxSet.clear();
                }
            });
        }

        Label userEmailDesc = new Label(I18nProperties.getString(Strings.infoUserEmail));
        getContent().addComponent(userEmailDesc, USER_EMAIL_DESC_LOC);
        userEmailDesc.setWidthFull();
        Label userPhoneDesc = new Label(I18nProperties.getString(Strings.infoUserPhoneNumber));
        userPhoneDesc.setWidthFull();
        getContent().addComponent(userPhoneDesc, USER_PHONE_DESC_LOC);

        ComboBox cbLanguage = addField(UserDto.LANGUAGE, ComboBox.class);
        CssStyles.style(cbLanguage, CssStyles.COMBO_BOX_WITH_FLAG_ICON);
        ControllerProvider.getUserController().setFlagIcons(cbLanguage);

        addField(UserDto.ADDRESS, LocationEditForm.class).setCaption(null);

        addField(UserDto.ACTIVE, CheckBox.class);
        addField(UserDto.USER_NAME, TextField.class);
        OptionGroup userRoles = addField(UserDto.USER_ROLES, OptionGroup.class);
        userRoleMap = FacadeProvider.getUserRoleFacade().getAll().stream().collect(Collectors.toMap(UserRoleDto::toReference, userRole -> userRole));
        userRoles.addItems(getFilteredUserRoles(Collections.emptySet()));
        userRoles.addValidator(new UserRolesValidator());
        userRoles.setMultiSelect(true);
        CssStyles.style(CssStyles.CAPTION_ON_TOP);

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
                    districtDto != null ? FacadeProvider.getUserFacade().getUserRefsByDistrict(districtDto, null, UserRight.CASE_RESPONSIBLE, UserRight.WEEKLYREPORT_CREATE) : null);
            FieldHelper.updateItems(
                    cbPointOfEntry,
                    districtDto != null ? FacadeProvider.getPointOfEntryFacade().getAllActiveByDistrict(districtDto.getUuid(), false) : null);
        });

        ComboBox laboratory = addInfrastructureField(UserDto.LABORATORY);
        laboratory.addItems(FacadeProvider.getFacilityFacade().getAllActiveLaboratories(false));

        region.addItems(FacadeProvider.getRegionFacade().getAllActiveByServerCountry());

        setRequired(true, UserDto.FIRST_NAME, UserDto.LAST_NAME, UserDto.USER_NAME, UserDto.USER_ROLES);
        addValidators(UserDto.USER_NAME, new UserNameValidator());

        addFieldListeners(UserDto.FIRST_NAME, e -> suggestUserName());
        addFieldListeners(UserDto.LAST_NAME, e -> suggestUserName());
        addFieldListeners(UserDto.USER_ROLES, e -> updateFieldsByUserRole());
        updateFieldsByUserRole();

        if (FacadeProvider.getFeatureConfigurationFacade().isFeatureEnabled(FeatureType.AUTH_PROVIDER_TO_SORMAS_USER_SYNC)) {
            this.getFieldGroup().getFields().forEach(userField ->{
                if (!userField.getId().equals(UserDto.USER_ROLES)) {
                    userField.setEnabled(false);
                }
            });
            this.getField(UserEditForm.RESTRICT_DISEASES_CHECKBOX_LOC).setEnabled(false);
        }
    }

	@Override
	public void setValue(UserDto newFieldValue) throws ReadOnlyException, Converter.ConversionException {
		final OptionGroup userRolesField = (OptionGroup)getFieldGroup().getField(UserDto.USER_ROLES);
		FieldHelper.updateItems(userRolesField, getFilteredUserRoles(newFieldValue.getUserRoles()));
        
        if (diseasesCheckboxSet != null) {
            Set<Disease> limitedDiseases = newFieldValue.getLimitedDiseases();
            restrictDiseasesCheckbox.setValue(CollectionUtils.isNotEmpty(limitedDiseases));
            diseasesCheckboxSet.setItems(getSelectableDiseases(limitedDiseases), null, null);
        }

		super.setValue(newFieldValue);
	}

	private List<UserRoleReferenceDto> getFilteredUserRoles(Set<UserRoleReferenceDto> selectedUserRoles) {
		return userRoleMap
				.entrySet().stream().filter(r -> r.getValue().isEnabled() || selectedUserRoles.stream().anyMatch(s -> DataHelper.isSame(s, r.getValue())))
				.map(Map.Entry::getKey)
				.sorted(Comparator.comparing(UserRoleReferenceDto::getCaption)).collect(Collectors.toList());
	}

    private static List<Disease> getSelectableDiseases(Set<Disease> limitedDiseases) {
        List<Disease> diseases = FacadeProvider.getDiseaseConfigurationFacade().getAllDiseases(true, true, true);
        if (CollectionUtils.isNotEmpty(limitedDiseases)) {
            List<Disease> inactiveSelectedDiseases = limitedDiseases.stream().filter(not(diseases::contains)).collect(Collectors.toList());
            diseases.addAll(inactiveSelectedDiseases);
        }

        return diseases;
    }


    @SuppressWarnings("unchecked")
    private void updateFieldsByUserRole() {

		final OptionGroup userRolesField = (OptionGroup)getFieldGroup().getField(UserDto.USER_ROLES);

        Set<UserRoleReferenceDto> userRolesFieldValue = (Set<UserRoleReferenceDto>) userRolesField.getValue();
        Set<UserRoleDto> userRoleDtos = userRolesFieldValue.stream().map(userRole -> userRoleMap.get(userRole)).collect(Collectors.toSet());
        final JurisdictionLevel jurisdictionLevel = UserRoleDto.getJurisdictionLevel(userRoleDtos);

        UserRoleFacade userRoleFacade = FacadeProvider.getUserRoleFacade();
        final boolean hasAssociatedDistrictUser = userRoleFacade.hasAssociatedDistrictUser(userRoleDtos);
		final boolean hasOptionalHealthFacility = userRoleFacade.hasOptionalHealthFacility(userRoleDtos);
		final boolean isPortHealthUser = userRoleFacade.isPortHealthUser(userRoleDtos);

		final boolean usePointOfEntry = (isPortHealthUser && hasAssociatedDistrictUser) || jurisdictionLevel == JurisdictionLevel.POINT_OF_ENTRY;
		final boolean useHealthFacility = jurisdictionLevel == JurisdictionLevel.HEALTH_FACILITY;
        final boolean useLaboratory = jurisdictionLevel == JurisdictionLevel.LABORATORY || jurisdictionLevel == JurisdictionLevel.EXTERNAL_LABORATORY;
		final boolean useCommunity = jurisdictionLevel == JurisdictionLevel.COMMUNITY;
		final boolean useDistrict = hasAssociatedDistrictUser || jurisdictionLevel == JurisdictionLevel.DISTRICT || useCommunity || useHealthFacility || usePointOfEntry;
		final boolean useRegion = jurisdictionLevel == JurisdictionLevel.REGION || useDistrict;

		final ComboBox associatedOfficer = (ComboBox) getFieldGroup().getField(UserDto.ASSOCIATED_OFFICER);
		associatedOfficer.setVisible(hasAssociatedDistrictUser);
		setRequired(hasAssociatedDistrictUser && !isPortHealthUser, UserDto.ASSOCIATED_OFFICER);
		if (!hasAssociatedDistrictUser) {
			associatedOfficer.clear();
		}

		final ComboBox community = (ComboBox) getFieldGroup().getField(UserDto.COMMUNITY);
		community.setVisible(useCommunity);
		setRequired(useCommunity, UserDto.COMMUNITY);
		if (!useCommunity) {
			community.clear();
		}

		final ComboBox healthFacility = (ComboBox) getFieldGroup().getField(UserDto.HEALTH_FACILITY);
		healthFacility.setVisible(useHealthFacility || hasOptionalHealthFacility);
		setRequired(useHealthFacility, UserDto.HEALTH_FACILITY);
		if (!healthFacility.isVisible()) {
			healthFacility.clear();
		}

		final ComboBox laboratory = (ComboBox) getFieldGroup().getField(UserDto.LABORATORY);
		laboratory.setVisible(useLaboratory);
		setRequired(useLaboratory, UserDto.LABORATORY);
		if (!useLaboratory) {
			laboratory.clear();
		}

		final ComboBox pointOfEntry = (ComboBox) getFieldGroup().getField(UserDto.POINT_OF_ENTRY);
		pointOfEntry.setVisible(usePointOfEntry);
		setRequired(usePointOfEntry, UserDto.POINT_OF_ENTRY);
		if (!usePointOfEntry) {
			pointOfEntry.clear();
		}

		final ComboBox district = (ComboBox) getFieldGroup().getField(UserDto.DISTRICT);
		district.setVisible(useDistrict || hasOptionalHealthFacility);
		setRequired(useDistrict, UserDto.DISTRICT);
		if (!useDistrict) {
			district.clear();
		}

		final ComboBox region = (ComboBox) getFieldGroup().getField(UserDto.REGION);
		region.setVisible(useRegion || hasOptionalHealthFacility);
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
}
