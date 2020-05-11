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

import static de.symeda.sormas.ui.utils.CssStyles.ERROR_COLOR_PRIMARY;
import static de.symeda.sormas.ui.utils.CssStyles.FORCE_CAPTION;
import static de.symeda.sormas.ui.utils.CssStyles.style;
import static de.symeda.sormas.ui.utils.LayoutUtil.fluidColumn;
import static de.symeda.sormas.ui.utils.LayoutUtil.fluidColumnLoc;
import static de.symeda.sormas.ui.utils.LayoutUtil.fluidRow;
import static de.symeda.sormas.ui.utils.LayoutUtil.fluidRowLocs;
import static de.symeda.sormas.ui.utils.LayoutUtil.locs;

import java.time.Month;
import java.util.Arrays;
import java.util.Date;

import com.vaadin.v7.ui.AbstractSelect;
import com.vaadin.v7.ui.AbstractSelect.ItemCaptionMode;
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
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.infrastructure.PointOfEntryReferenceDto;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.person.PresentCondition;
import de.symeda.sormas.api.person.Sex;
import de.symeda.sormas.api.region.CommunityReferenceDto;
import de.symeda.sormas.api.region.DistrictReferenceDto;
import de.symeda.sormas.api.region.RegionReferenceDto;
import de.symeda.sormas.api.symptoms.SymptomsDto;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.ui.UserProvider;
import de.symeda.sormas.ui.utils.AbstractEditForm;
import de.symeda.sormas.ui.utils.FieldHelper;

public class CaseCreateForm extends AbstractEditForm<CaseDataDto> {
	
	private static final long serialVersionUID = 1L;
	
	private ComboBox birthDateDay;

	private static final String HTML_LAYOUT = fluidRowLocs(CaseDataDto.CASE_ORIGIN, "") +
			fluidRowLocs(CaseDataDto.REPORT_DATE, CaseDataDto.EPID_NUMBER) +
			fluidRow(
					fluidColumnLoc(6, 0, CaseDataDto.DISEASE),
					fluidColumn(6, 0,
							locs(CaseDataDto.DISEASE_DETAILS, 
									CaseDataDto.PLAGUE_TYPE,
									CaseDataDto.DENGUE_FEVER_TYPE, 
									CaseDataDto.RABIES_TYPE))) +
			fluidRowLocs(CaseDataDto.REGION, CaseDataDto.DISTRICT) +
			fluidRowLocs(CaseDataDto.COMMUNITY, CaseDataDto.HEALTH_FACILITY) +
			fluidRowLocs(CaseDataDto.HEALTH_FACILITY_DETAILS) +
			fluidRowLocs(CaseDataDto.POINT_OF_ENTRY, CaseDataDto.POINT_OF_ENTRY_DETAILS) +
			fluidRowLocs(PersonDto.FIRST_NAME, PersonDto.LAST_NAME) +
			fluidRow(
					fluidRowLocs(PersonDto.BIRTH_DATE_YYYY, PersonDto.BIRTH_DATE_MM, PersonDto.BIRTH_DATE_DD),
					fluidRowLocs(PersonDto.SEX)) +
			fluidRowLocs(PersonDto.PRESENT_CONDITION, SymptomsDto.ONSET_DATE);

	public CaseCreateForm(UserRight editOrCreateUserRight) {
		super(CaseDataDto.class, CaseDataDto.I18N_PREFIX, editOrCreateUserRight);

		setWidth(720, Unit.PIXELS);

		hideValidationUntilNextCommit();
	}

	@Override
	protected void addFields() {
		TextField epidField = addField(CaseDataDto.EPID_NUMBER, TextField.class);
		epidField.setInvalidCommitted(true);
		style(epidField, ERROR_COLOR_PRIMARY);

		addField(CaseDataDto.REPORT_DATE, DateField.class);
		ComboBox disease = addDiseaseField(CaseDataDto.DISEASE, false);
		addField(CaseDataDto.DISEASE_DETAILS, TextField.class);
		OptionGroup plagueType = addField(CaseDataDto.PLAGUE_TYPE, OptionGroup.class);
		addField(CaseDataDto.DENGUE_FEVER_TYPE, OptionGroup.class);
		addField(CaseDataDto.RABIES_TYPE, OptionGroup.class);
		addCustomField(PersonDto.FIRST_NAME, String.class, TextField.class);
		addCustomField(PersonDto.LAST_NAME, String.class, TextField.class);

		birthDateDay = addCustomField(PersonDto.BIRTH_DATE_DD, Integer.class, ComboBox.class);
		// @TODO: Done for nullselection Bug, fixed in Vaadin 7.7.3
		birthDateDay.setNullSelectionAllowed(true);
		birthDateDay.addStyleName(FORCE_CAPTION);
		birthDateDay.setInputPrompt(I18nProperties.getString(Strings.day));
		ComboBox birthDateMonth = addCustomField(PersonDto.BIRTH_DATE_MM, Integer.class, ComboBox.class);
		// @TODO: Done for nullselection Bug, fixed in Vaadin 7.7.3
		birthDateMonth.setNullSelectionAllowed(true);
		birthDateMonth.addItems(DateHelper.getMonthsInYear());
		birthDateMonth.setPageLength(12);
		birthDateMonth.addStyleName(FORCE_CAPTION);
		birthDateMonth.setInputPrompt(I18nProperties.getString(Strings.month));
		setItemCaptionsForMonths(birthDateMonth);
		ComboBox birthDateYear = addCustomField(PersonDto.BIRTH_DATE_YYYY, Integer.class, ComboBox.class);
		birthDateYear.setCaption(I18nProperties.getPrefixCaption(PersonDto.I18N_PREFIX, PersonDto.BIRTH_DATE));
		// @TODO: Done for nullselection Bug, fixed in Vaadin 7.7.3
		birthDateYear.setNullSelectionAllowed(true);
		birthDateYear.addItems(DateHelper.getYearsToNow());
		birthDateYear.setItemCaptionMode(ItemCaptionMode.ID_TOSTRING);
		birthDateYear.setInputPrompt(I18nProperties.getString(Strings.year));
		// Update the list of days according to the selected month and year
		birthDateYear.addValueChangeListener(e -> {
			updateListOfDays((Integer) e.getProperty().getValue(), (Integer) birthDateMonth.getValue());
		});
		birthDateMonth.addValueChangeListener(e -> {
			updateListOfDays((Integer) birthDateYear.getValue(), (Integer) e.getProperty().getValue());
		});

		ComboBox sex = addCustomField(PersonDto.SEX, Sex.class, ComboBox.class);
		sex.setCaption(I18nProperties.getCaption(Captions.Person_sex));
		ComboBox presentCondition = addCustomField(PersonDto.PRESENT_CONDITION, PresentCondition.class, ComboBox.class);
		presentCondition.setCaption(I18nProperties.getCaption(Captions.Person_presentCondition));

		DateField onsetDate = addCustomField(SymptomsDto.ONSET_DATE, Date.class, DateField.class);
		onsetDate.setCaption(I18nProperties.getCaption(Captions.Symptoms_onsetDate));

		ComboBox region = addInfrastructureField(CaseDataDto.REGION);
		ComboBox district = addInfrastructureField(CaseDataDto.DISTRICT);
		ComboBox community = addInfrastructureField(CaseDataDto.COMMUNITY);
		community.setNullSelectionAllowed(true);
		ComboBox facility = addInfrastructureField(CaseDataDto.HEALTH_FACILITY);
		facility.setImmediate(true);
		TextField facilityDetails = addField(CaseDataDto.HEALTH_FACILITY_DETAILS, TextField.class);
		facilityDetails.setVisible(false);
		ComboBox cbPointOfEntry = addInfrastructureField(CaseDataDto.POINT_OF_ENTRY);
		cbPointOfEntry.setImmediate(true);
		TextField tfPointOfEntryDetails = addField(CaseDataDto.POINT_OF_ENTRY_DETAILS, TextField.class);
		tfPointOfEntryDetails.setVisible(false);

		region.addValueChangeListener(e -> {
			RegionReferenceDto regionDto = (RegionReferenceDto) e.getProperty().getValue();
			FieldHelper.updateItems(district,
					regionDto != null ? FacadeProvider.getDistrictFacade().getAllActiveByRegion(regionDto.getUuid())
							: null);
		});
		district.addValueChangeListener(e -> {
			if (community.getValue() == null) {
				FieldHelper.removeItems(facility);
			}
			FieldHelper.removeItems(community);
			DistrictReferenceDto districtDto = (DistrictReferenceDto) e.getProperty().getValue();
			FieldHelper.updateItems(community,
					districtDto != null
							? FacadeProvider.getCommunityFacade().getAllActiveByDistrict(districtDto.getUuid())
							: null);
			FieldHelper.updateItems(facility,
					districtDto != null
							? FacadeProvider.getFacilityFacade().getActiveHealthFacilitiesByDistrict(districtDto, true)
							: null);
			FieldHelper.updateItems(cbPointOfEntry,
					districtDto != null
							? FacadeProvider.getPointOfEntryFacade().getAllActiveByDistrict(districtDto.getUuid(), true)
							: null);
		});
		community.addValueChangeListener(e -> {
			FieldHelper.removeItems(facility);
			CommunityReferenceDto communityDto = (CommunityReferenceDto) e.getProperty().getValue();
			FieldHelper.updateItems(facility, communityDto != null
					? FacadeProvider.getFacilityFacade().getActiveHealthFacilitiesByCommunity(communityDto, true)
					: district.getValue() != null ? FacadeProvider.getFacilityFacade()
							.getActiveHealthFacilitiesByDistrict((DistrictReferenceDto) district.getValue(), true)
							: null);
		});
		region.addItems(FacadeProvider.getRegionFacade().getAllActiveAsReference());

		OptionGroup ogCaseOrigin = addField(CaseDataDto.CASE_ORIGIN, OptionGroup.class);
		ogCaseOrigin.setRequired(true);

		if (UserRole.isPortHealthUser(UserProvider.getCurrent().getUserRoles())) {
			setVisible(false, CaseDataDto.CASE_ORIGIN, CaseDataDto.DISEASE, CaseDataDto.COMMUNITY,
					CaseDataDto.HEALTH_FACILITY);
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

		setRequired(true, CaseDataDto.REPORT_DATE, PersonDto.FIRST_NAME, PersonDto.LAST_NAME, CaseDataDto.DISEASE,
				CaseDataDto.REGION, CaseDataDto.DISTRICT);
		FieldHelper.addSoftRequiredStyle(plagueType, community, facilityDetails, sex);

		FieldHelper.setVisibleWhen(getFieldGroup(), Arrays.asList(CaseDataDto.DISEASE_DETAILS), CaseDataDto.DISEASE,
				Arrays.asList(Disease.OTHER), true);
		FieldHelper.setRequiredWhen(getFieldGroup(), CaseDataDto.DISEASE, Arrays.asList(CaseDataDto.DISEASE_DETAILS),
				Arrays.asList(Disease.OTHER));
		FieldHelper.setRequiredWhen(getFieldGroup(), CaseDataDto.CASE_ORIGIN,
				Arrays.asList(CaseDataDto.HEALTH_FACILITY), Arrays.asList(CaseOrigin.IN_COUNTRY));
		FieldHelper.setRequiredWhen(getFieldGroup(), CaseDataDto.CASE_ORIGIN, Arrays.asList(CaseDataDto.POINT_OF_ENTRY),
				Arrays.asList(CaseOrigin.POINT_OF_ENTRY));
		FieldHelper.setVisibleWhen(getFieldGroup(), Arrays.asList(CaseDataDto.PLAGUE_TYPE), CaseDataDto.DISEASE,
				Arrays.asList(Disease.PLAGUE), true);
		FieldHelper.setVisibleWhen(getFieldGroup(), Arrays.asList(CaseDataDto.DENGUE_FEVER_TYPE), CaseDataDto.DISEASE,
				Arrays.asList(Disease.DENGUE), true);
		FieldHelper.setVisibleWhen(getFieldGroup(), Arrays.asList(CaseDataDto.RABIES_TYPE), CaseDataDto.DISEASE,
				Arrays.asList(Disease.RABIES), true);

		facility.addValueChangeListener(e -> {
			updateFacilityFields(facility, facilityDetails);
		});

		cbPointOfEntry.addValueChangeListener(e -> {
			updatePointOfEntryFields(cbPointOfEntry, tfPointOfEntryDetails);
		});
	
		addValueChangeListener(e -> {
			Disease defaultDisease = FacadeProvider.getDiseaseConfigurationFacade().getDefaultDisease();
			if (defaultDisease != null) {
				disease.setValue(defaultDisease);
			}
		});
	}

	private void updateListOfDays(Integer selectedYear, Integer selectedMonth) {
		Integer currentlySelected = (Integer) birthDateDay.getValue();
		birthDateDay.removeAllItems();
		birthDateDay.addItems(DateHelper.getDaysInMonth(selectedMonth, selectedYear));
		if (birthDateDay.containsId(currentlySelected)) {
			birthDateDay.setValue(currentlySelected);
		}
	}

	private void updateFacilityFields(ComboBox cbFacility, TextField tfFacilityDetails) {
		if (cbFacility.getValue() != null) {
			boolean otherHealthFacility = ((FacilityReferenceDto) cbFacility.getValue()).getUuid()
					.equals(FacilityDto.OTHER_FACILITY_UUID);
			boolean noneHealthFacility = ((FacilityReferenceDto) cbFacility.getValue()).getUuid()
					.equals(FacilityDto.NONE_FACILITY_UUID);
			boolean visibleAndRequired = otherHealthFacility || noneHealthFacility;

			tfFacilityDetails.setVisible(visibleAndRequired);
			tfFacilityDetails.setRequired(visibleAndRequired);

			if (otherHealthFacility) {
				tfFacilityDetails.setCaption(
						I18nProperties.getPrefixCaption(CaseDataDto.I18N_PREFIX, CaseDataDto.HEALTH_FACILITY_DETAILS));
			}
			if (noneHealthFacility) {
				tfFacilityDetails.setCaption(I18nProperties.getPrefixCaption(CaseDataDto.I18N_PREFIX,
						CaseDataDto.NONE_HEALTH_FACILITY_DETAILS));
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

	private void setItemCaptionsForMonths(AbstractSelect months) {
		months.setItemCaption(1, I18nProperties.getEnumCaption(Month.JANUARY));
		months.setItemCaption(2, I18nProperties.getEnumCaption(Month.FEBRUARY));
		months.setItemCaption(3, I18nProperties.getEnumCaption(Month.MARCH));
		months.setItemCaption(4, I18nProperties.getEnumCaption(Month.APRIL));
		months.setItemCaption(5, I18nProperties.getEnumCaption(Month.MAY));
		months.setItemCaption(6, I18nProperties.getEnumCaption(Month.JUNE));
		months.setItemCaption(7, I18nProperties.getEnumCaption(Month.JULY));
		months.setItemCaption(8, I18nProperties.getEnumCaption(Month.AUGUST));
		months.setItemCaption(9, I18nProperties.getEnumCaption(Month.SEPTEMBER));
		months.setItemCaption(10, I18nProperties.getEnumCaption(Month.OCTOBER));
		months.setItemCaption(11, I18nProperties.getEnumCaption(Month.NOVEMBER));
		months.setItemCaption(12, I18nProperties.getEnumCaption(Month.DECEMBER));
	}

	public String getPersonFirstName() {
		return (String) getField(PersonDto.FIRST_NAME).getValue();
	}

	public String getPersonLastName() {
		return (String) getField(PersonDto.LAST_NAME).getValue();
	}

	public Integer getBirthdateDD() {
		return (Integer) getField(PersonDto.BIRTH_DATE_DD).getValue();
	}

	public Integer getBirthdateMM() {
		return (Integer) getField(PersonDto.BIRTH_DATE_MM).getValue();
	}

	public Integer getBirthdateYYYY() {
		return (Integer) getField(PersonDto.BIRTH_DATE_YYYY).getValue();
	}

	public Sex getSex() {
		return (Sex) getField(PersonDto.SEX).getValue();
	}

	public PresentCondition getPresentCondition() {
		return (PresentCondition) getField(PersonDto.PRESENT_CONDITION).getValue();
	}

	public Date getOnsetDate() {
		return (Date) getField(SymptomsDto.ONSET_DATE).getValue();
	}

	public void setPerson(PersonDto person) {
		if (person != null) {
			((TextField) getField(PersonDto.FIRST_NAME)).setValue(person.getFirstName());
			((TextField) getField(PersonDto.LAST_NAME)).setValue(person.getLastName());
			((ComboBox) getField(PersonDto.BIRTH_DATE_YYYY)).setValue(person.getBirthdateYYYY());
			((ComboBox) getField(PersonDto.BIRTH_DATE_MM)).setValue(person.getBirthdateMM());
			((ComboBox) getField(PersonDto.BIRTH_DATE_DD)).setValue(person.getBirthdateDD());
			((ComboBox) getField(PersonDto.SEX)).setValue(person.getSex());
			((ComboBox) getField(PersonDto.PRESENT_CONDITION)).setValue(person.getPresentCondition());
		} else {
			getField(PersonDto.FIRST_NAME).clear();
			getField(PersonDto.LAST_NAME).clear();
			getField(PersonDto.BIRTH_DATE_DD).clear();
			getField(PersonDto.BIRTH_DATE_MM).clear();
			getField(PersonDto.BIRTH_DATE_YYYY).clear();
			getField(PersonDto.SEX).clear();
			getField(PersonDto.PRESENT_CONDITION).clear();
		}
	}

	public void setSymptoms(SymptomsDto symptoms) {
		if (symptoms != null) {
			((DateField) getField(SymptomsDto.ONSET_DATE)).setValue(symptoms.getOnsetDate());
		} else {
			getField(SymptomsDto.ONSET_DATE).clear();
		}
	}

	public void setPersonalDetailsReadOnly(boolean readOnly) {
		getField(PersonDto.FIRST_NAME).setEnabled(!readOnly);
		getField(PersonDto.LAST_NAME).setEnabled(!readOnly);
		getField(PersonDto.SEX).setEnabled(!readOnly);
		getField(PersonDto.BIRTH_DATE_YYYY).setEnabled(!readOnly);
		getField(PersonDto.BIRTH_DATE_MM).setEnabled(!readOnly);
		getField(PersonDto.BIRTH_DATE_DD).setEnabled(!readOnly);
	}

	public void setDiseaseReadOnly(boolean readOnly) {
		getField(CaseDataDto.DISEASE).setEnabled(!readOnly);
	}

	@Override
	protected String createHtmlLayout() {
		return HTML_LAYOUT;
	}
}
