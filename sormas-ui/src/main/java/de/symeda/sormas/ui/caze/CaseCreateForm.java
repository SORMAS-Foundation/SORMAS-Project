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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *******************************************************************************/
package de.symeda.sormas.ui.caze;

import java.util.Arrays;

import com.vaadin.v7.ui.ComboBox;
import com.vaadin.v7.ui.DateField;
import com.vaadin.v7.ui.OptionGroup;
import com.vaadin.v7.ui.TextField;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.caze.CaseOrigin;
import de.symeda.sormas.api.facility.FacilityDto;
import de.symeda.sormas.api.facility.FacilityReferenceDto;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.infrastructure.PointOfEntryReferenceDto;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.region.CommunityReferenceDto;
import de.symeda.sormas.api.region.DistrictReferenceDto;
import de.symeda.sormas.api.region.RegionReferenceDto;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.ui.UserProvider;
import de.symeda.sormas.ui.utils.AbstractEditForm;
import de.symeda.sormas.ui.utils.FieldHelper;
import de.symeda.sormas.ui.utils.LayoutUtil;

@SuppressWarnings("serial")
public class CaseCreateForm extends AbstractEditForm<CaseDataDto> {

	public static final String FIRST_NAME = PersonDto.FIRST_NAME;
	public static final String LAST_NAME = PersonDto.LAST_NAME;
	public static final String NONE_HEALTH_FACILITY_DETAILS = CaseDataDto.NONE_HEALTH_FACILITY_DETAILS;

	private static final String HTML_LAYOUT = 
			LayoutUtil.fluidRowLocs(CaseDataDto.CASE_ORIGIN, "") +
			LayoutUtil.fluidRowLocs(CaseDataDto.REPORT_DATE, CaseDataDto.DISEASE) +
			LayoutUtil.fluidRow(LayoutUtil.locs(CaseDataDto.DISEASE_DETAILS, CaseDataDto.PLAGUE_TYPE, CaseDataDto.DENGUE_FEVER_TYPE)) +
			LayoutUtil.fluidRowLocs(FIRST_NAME, LAST_NAME) +
			LayoutUtil.fluidRowLocs(CaseDataDto.REGION, CaseDataDto.DISTRICT) +
			LayoutUtil.fluidRowLocs(CaseDataDto.COMMUNITY, CaseDataDto.HEALTH_FACILITY) +
			LayoutUtil.fluidRowLocs("", CaseDataDto.HEALTH_FACILITY_DETAILS) +
			LayoutUtil.fluidRowLocs(CaseDataDto.POINT_OF_ENTRY) +
			LayoutUtil.fluidRowLocs(CaseDataDto.POINT_OF_ENTRY_DETAILS);

	public CaseCreateForm(UserRight editOrCreateUserRight) {
		super(CaseDataDto.class, CaseDataDto.I18N_PREFIX, editOrCreateUserRight);

		setWidth(540, Unit.PIXELS);		

		hideValidationUntilNextCommit();
	}

	@Override
	protected void addFields() {
		addField(CaseDataDto.REPORT_DATE, DateField.class);
		addDiseaseField(CaseDataDto.DISEASE, false);
		addField(CaseDataDto.DISEASE_DETAILS, TextField.class);
		OptionGroup plagueType = addField(CaseDataDto.PLAGUE_TYPE, OptionGroup.class);
		addField(CaseDataDto.DENGUE_FEVER_TYPE, OptionGroup.class);
		addCustomField(FIRST_NAME, String.class, TextField.class);
		addCustomField(LAST_NAME, String.class, TextField.class);

		ComboBox region = addField(CaseDataDto.REGION, ComboBox.class);
		ComboBox district = addField(CaseDataDto.DISTRICT, ComboBox.class);
		ComboBox community = addField(CaseDataDto.COMMUNITY, ComboBox.class);
		community.setNullSelectionAllowed(true);
		ComboBox facility = addField(CaseDataDto.HEALTH_FACILITY, ComboBox.class);
		facility.setImmediate(true);
		TextField facilityDetails = addField(CaseDataDto.HEALTH_FACILITY_DETAILS, TextField.class);
		facilityDetails.setVisible(false);
		ComboBox cbPointOfEntry = addField(CaseDataDto.POINT_OF_ENTRY, ComboBox.class);
		cbPointOfEntry.setImmediate(true);
		TextField tfPointOfEntryDetails = addField(CaseDataDto.POINT_OF_ENTRY_DETAILS, TextField.class);
		tfPointOfEntryDetails.setVisible(false);

		region.addValueChangeListener(e -> {
			RegionReferenceDto regionDto = (RegionReferenceDto)e.getProperty().getValue();
			FieldHelper.updateItems(district, regionDto != null ? FacadeProvider.getDistrictFacade().getAllByRegion(regionDto.getUuid()) : null);
		});
		district.addValueChangeListener(e -> {
			if (community.getValue() == null) {
				FieldHelper.removeItems(facility);
			}
			FieldHelper.removeItems(community);
			DistrictReferenceDto districtDto = (DistrictReferenceDto)e.getProperty().getValue();
			FieldHelper.updateItems(community, districtDto != null ? FacadeProvider.getCommunityFacade().getAllByDistrict(districtDto.getUuid()) : null);
			FieldHelper.updateItems(facility, districtDto != null ? FacadeProvider.getFacilityFacade().getHealthFacilitiesByDistrict(districtDto, true) : null);
			FieldHelper.updateItems(cbPointOfEntry, districtDto != null ? FacadeProvider.getPointOfEntryFacade().getAllByDistrict(districtDto.getUuid(), true) : null);
		});
		community.addValueChangeListener(e -> {
			FieldHelper.removeItems(facility);
			CommunityReferenceDto communityDto = (CommunityReferenceDto)e.getProperty().getValue();
			FieldHelper.updateItems(facility, communityDto != null ? FacadeProvider.getFacilityFacade().getHealthFacilitiesByCommunity(communityDto, true) :
				district.getValue() != null ? FacadeProvider.getFacilityFacade().getHealthFacilitiesByDistrict((DistrictReferenceDto) district.getValue(), true) :
					null);
		});
		region.addItems(FacadeProvider.getRegionFacade().getAllAsReference());

		OptionGroup ogCaseOrigin = addField(CaseDataDto.CASE_ORIGIN, OptionGroup.class);
		ogCaseOrigin.setRequired(true);

		if (UserRole.isPortHealthUser(UserProvider.getCurrent().getUserRoles())) {
			setVisible(false, CaseDataDto.CASE_ORIGIN, CaseDataDto.DISEASE, CaseDataDto.COMMUNITY, CaseDataDto.HEALTH_FACILITY);
			setVisible(true, CaseDataDto.POINT_OF_ENTRY);
		} else {
			ogCaseOrigin.addValueChangeListener(e -> {
				if (e.getProperty().getValue() == CaseOrigin.IN_COUNTRY) {
					setVisible(false, CaseDataDto.POINT_OF_ENTRY, CaseDataDto.POINT_OF_ENTRY_DETAILS);
					setRequired(true, CaseDataDto.HEALTH_FACILITY);
					setRequired(false, CaseDataDto.POINT_OF_ENTRY);
					updateFacilityFields(facility, facilityDetails);
				} else {
					setVisible(true, CaseDataDto.POINT_OF_ENTRY);
					setRequired(true, CaseDataDto.POINT_OF_ENTRY);
					setRequired(false, CaseDataDto.HEALTH_FACILITY);
					updatePointOfEntryFields(cbPointOfEntry, tfPointOfEntryDetails);
				}
			});
		}

		setRequired(true, CaseDataDto.REPORT_DATE, FIRST_NAME, LAST_NAME, CaseDataDto.DISEASE, CaseDataDto.REGION, CaseDataDto.DISTRICT);
		FieldHelper.addSoftRequiredStyle(plagueType, community, facilityDetails);

		FieldHelper.setVisibleWhen(getFieldGroup(), Arrays.asList(CaseDataDto.DISEASE_DETAILS), CaseDataDto.DISEASE, Arrays.asList(Disease.OTHER), true);
		FieldHelper.setRequiredWhen(getFieldGroup(), CaseDataDto.DISEASE, Arrays.asList(CaseDataDto.DISEASE_DETAILS), Arrays.asList(Disease.OTHER));
		FieldHelper.setRequiredWhen(getFieldGroup(), CaseDataDto.CASE_ORIGIN, Arrays.asList(CaseDataDto.HEALTH_FACILITY), Arrays.asList(CaseOrigin.IN_COUNTRY));
		FieldHelper.setRequiredWhen(getFieldGroup(), CaseDataDto.CASE_ORIGIN, Arrays.asList(CaseDataDto.POINT_OF_ENTRY), Arrays.asList(CaseOrigin.POINT_OF_ENTRY));
		FieldHelper.setVisibleWhen(getFieldGroup(), Arrays.asList(CaseDataDto.PLAGUE_TYPE), CaseDataDto.DISEASE, Arrays.asList(Disease.PLAGUE), true);
		FieldHelper.setVisibleWhen(getFieldGroup(), Arrays.asList(CaseDataDto.DENGUE_FEVER_TYPE), CaseDataDto.DISEASE, Arrays.asList(Disease.DENGUE), true);

		facility.addValueChangeListener(e -> {
			updateFacilityFields(facility, facilityDetails);
		});

		cbPointOfEntry.addValueChangeListener(e -> {
			updatePointOfEntryFields(cbPointOfEntry, tfPointOfEntryDetails);
		});
	}

	private void updateFacilityFields(ComboBox cbFacility, TextField tfFacilityDetails) {
		if (cbFacility.getValue() != null) {
			boolean otherHealthFacility = ((FacilityReferenceDto) cbFacility.getValue()).getUuid().equals(FacilityDto.OTHER_FACILITY_UUID);
			boolean noneHealthFacility = ((FacilityReferenceDto) cbFacility.getValue()).getUuid().equals(FacilityDto.NONE_FACILITY_UUID);
			boolean visibleAndRequired = otherHealthFacility || noneHealthFacility;

			tfFacilityDetails.setVisible(visibleAndRequired);
			tfFacilityDetails.setRequired(visibleAndRequired);

			if (otherHealthFacility) {
				tfFacilityDetails.setCaption(I18nProperties.getPrefixCaption(CaseDataDto.I18N_PREFIX, CaseDataDto.HEALTH_FACILITY_DETAILS));
			}
			if (noneHealthFacility) {
				tfFacilityDetails.setCaption(I18nProperties.getPrefixCaption(CaseDataDto.I18N_PREFIX, NONE_HEALTH_FACILITY_DETAILS));
			}
			if (!visibleAndRequired) {
				tfFacilityDetails.clear();
			}
		} else {
			tfFacilityDetails.setVisible(false);
			tfFacilityDetails.setRequired(false);
			tfFacilityDetails.clear();
		}	
	}

	private void updatePointOfEntryFields(ComboBox cbPointOfEntry, TextField tfPointOfEntryDetails) {
		if (cbPointOfEntry.getValue() != null) {
			boolean isOtherPointOfEntry = ((PointOfEntryReferenceDto) cbPointOfEntry.getValue()).isOtherPointOfEntry();
			setVisible(isOtherPointOfEntry, CaseDataDto.POINT_OF_ENTRY_DETAILS);
			setRequired(isOtherPointOfEntry, CaseDataDto.POINT_OF_ENTRY_DETAILS);
			if (!isOtherPointOfEntry) {
				tfPointOfEntryDetails.clear();
			}
		} else {
			tfPointOfEntryDetails.setVisible(false);
			tfPointOfEntryDetails.setRequired(false);
			tfPointOfEntryDetails.clear();
		}
	}

	public String getPersonFirstName() {
		return (String)getField(FIRST_NAME).getValue();
	}

	public String getPersonLastName() {
		return (String)getField(LAST_NAME).getValue();
	}

	public void setPerson(PersonDto person) {
		((TextField) getField(FIRST_NAME)).setValue(person.getFirstName());
		((TextField) getField(LAST_NAME)).setValue(person.getLastName());
	}

	public void setNameReadOnly(boolean readOnly) {
		getField(FIRST_NAME).setEnabled(!readOnly);
		getField(LAST_NAME).setEnabled(!readOnly);
	}

	public void setDiseaseReadOnly(boolean readOnly) {
		getField(CaseDataDto.DISEASE).setEnabled(!readOnly);
	}

	@Override
	protected String createHtmlLayout() {
		return HTML_LAYOUT;
	}
}
