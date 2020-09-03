/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2018 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package de.symeda.sormas.app.person.edit;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.person.ApproximateAgeType;
import de.symeda.sormas.api.person.BurialConductor;
import de.symeda.sormas.api.person.CauseOfDeath;
import de.symeda.sormas.api.person.DeathPlaceType;
import de.symeda.sormas.api.person.EducationType;
import de.symeda.sormas.api.person.OccupationType;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.person.PresentCondition;
import de.symeda.sormas.api.person.Sex;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.api.utils.fieldaccess.FieldAccessCheckers;
import de.symeda.sormas.api.utils.fieldvisibility.FieldVisibilityCheckers;
import de.symeda.sormas.app.BaseActivity;
import de.symeda.sormas.app.BaseEditFragment;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.caze.Case;
import de.symeda.sormas.app.backend.caze.CaseEditAuthorization;
import de.symeda.sormas.app.backend.common.AbstractDomainObject;
import de.symeda.sormas.app.backend.config.ConfigProvider;
import de.symeda.sormas.app.backend.contact.Contact;
import de.symeda.sormas.app.backend.contact.ContactEditAuthorization;
import de.symeda.sormas.app.backend.location.Location;
import de.symeda.sormas.app.backend.person.Person;
import de.symeda.sormas.app.component.Item;
import de.symeda.sormas.app.component.controls.ControlPropertyField;
import de.symeda.sormas.app.component.controls.ValueChangeListener;
import de.symeda.sormas.app.component.dialog.LocationDialog;
import de.symeda.sormas.app.databinding.FragmentPersonEditLayoutBinding;
import de.symeda.sormas.app.util.DataUtils;
import de.symeda.sormas.app.util.DiseaseConfigurationCache;
import de.symeda.sormas.app.util.InfrastructureHelper;

public class PersonEditFragment extends BaseEditFragment<FragmentPersonEditLayoutBinding, Person, AbstractDomainObject> {

	public static final String TAG = PersonEditFragment.class.getSimpleName();

	private Person record;
	private AbstractDomainObject rootData;

	// Instance methods

	public static PersonEditFragment newInstance(Case activityRootData) {

		return newInstanceWithFieldCheckers(
			PersonEditFragment.class,
			null,
			activityRootData,
			FieldVisibilityCheckers.withDisease(activityRootData.getDisease()),
			FieldAccessCheckers.withPersonalData(ConfigProvider::hasUserRight, CaseEditAuthorization.isCaseEditAllowed(activityRootData)));
	}

	public static PersonEditFragment newInstance(Contact activityRootData) {

		return newInstanceWithFieldCheckers(
			PersonEditFragment.class,
			null,
			activityRootData,
			FieldVisibilityCheckers.withDisease(activityRootData.getDisease()),
			FieldAccessCheckers.withPersonalData(ConfigProvider::hasUserRight, ContactEditAuthorization.isContactEditAllowed(activityRootData)));
	}

	public static void setUpLayoutBinding(
		final BaseEditFragment fragment,
		final Person record,
		final FragmentPersonEditLayoutBinding contentBinding,
		AbstractDomainObject rootData) {
		setUpControlListeners(record, fragment, contentBinding);

		fragment.setFieldVisibilitiesAndAccesses(PersonDto.class, contentBinding.mainContent);

		List<Item> monthList = DataUtils.getMonthItems(true);
		List<Item> yearList = DataUtils.toItems(DateHelper.getYearsToNow(), true);
		List<Item> approximateAgeTypeList = DataUtils.getEnumItems(ApproximateAgeType.class, true);
		List<Item> sexList = DataUtils.getEnumItems(Sex.class, true);
		List<Item> causeOfDeathList = DataUtils.getEnumItems(CauseOfDeath.class, true);
		List<Item> diseaseList = DataUtils.toItems(DiseaseConfigurationCache.getInstance().getAllDiseases(true, true, true));
		if (record.getCauseOfDeathDisease() != null && !diseaseList.contains(record.getCauseOfDeathDisease())) {
			diseaseList.add(DataUtils.toItem(record.getCauseOfDeathDisease()));
		}
		List<Item> deathPlaceTypeList = DataUtils.getEnumItems(DeathPlaceType.class, true);
		List<Item> burialConductorList = DataUtils.getEnumItems(BurialConductor.class, true);

		List<Item> initialOccupationRegions = InfrastructureHelper.loadRegions();
		List<Item> initialOccupationDistricts = InfrastructureHelper.loadDistricts(record.getOccupationRegion());
		List<Item> initialOccupationCommunities = InfrastructureHelper.loadCommunities(record.getOccupationDistrict());
		List<Item> initialOccupationFacilities = InfrastructureHelper.loadFacilities(record.getOccupationDistrict(), record.getOccupationCommunity());

		List<Item> initialPlaceOfBirthRegions = InfrastructureHelper.loadRegions();
		List<Item> initialPlaceOfBirthDistricts = InfrastructureHelper.loadDistricts(record.getPlaceOfBirthRegion());
		List<Item> initialPlaceOfBirthCommunities = InfrastructureHelper.loadCommunities(record.getPlaceOfBirthDistrict());
		List<Item> initialPlaceOfBirthFacilities =
			InfrastructureHelper.loadFacilities(record.getPlaceOfBirthDistrict(), record.getPlaceOfBirthCommunity());

		InfrastructureHelper
			.initializeHealthFacilityDetailsFieldVisibility(contentBinding.personOccupationFacility, contentBinding.personOccupationFacilityDetails);
		InfrastructureHelper.initializeHealthFacilityDetailsFieldVisibility(
			contentBinding.personPlaceOfBirthFacility,
			contentBinding.personPlaceOfBirthFacilityDetails);
		initializeCauseOfDeathDetailsFieldVisibility(
			contentBinding.personCauseOfDeath,
			contentBinding.personCauseOfDeathDisease,
			contentBinding.personCauseOfDeathDetails);
		initializeOccupationDetailsFieldVisibility(contentBinding.personOccupationType, contentBinding.personOccupationDetails);

		InfrastructureHelper.initializeFacilityFields(
			contentBinding.personOccupationRegion,
			initialOccupationRegions,
			record.getOccupationRegion(),
			contentBinding.personOccupationDistrict,
			initialOccupationDistricts,
			record.getOccupationDistrict(),
			contentBinding.personOccupationCommunity,
			initialOccupationCommunities,
			record.getOccupationCommunity(),
			contentBinding.personOccupationFacility,
			initialOccupationFacilities,
			record.getOccupationFacility());
		InfrastructureHelper.initializeFacilityFields(
			contentBinding.personPlaceOfBirthRegion,
			initialPlaceOfBirthRegions,
			record.getPlaceOfBirthRegion(),
			contentBinding.personPlaceOfBirthDistrict,
			initialPlaceOfBirthDistricts,
			record.getPlaceOfBirthDistrict(),
			contentBinding.personPlaceOfBirthCommunity,
			initialPlaceOfBirthCommunities,
			record.getPlaceOfBirthCommunity(),
			contentBinding.personPlaceOfBirthFacility,
			initialPlaceOfBirthFacilities,
			record.getPlaceOfBirthFacility());

		// Initialize ControlSpinnerFields
		contentBinding.personBirthdateDD.initializeSpinner(new ArrayList<>(), field -> updateApproximateAgeField(contentBinding));
		contentBinding.personBirthdateMM.initializeSpinner(monthList, field -> {
			updateApproximateAgeField(contentBinding);
			DataUtils.updateListOfDays(
				contentBinding.personBirthdateDD,
				(Integer) contentBinding.personBirthdateYYYY.getValue(),
				(Integer) field.getValue());
		});
		contentBinding.personBirthdateYYYY.initializeSpinner(yearList, field -> {
			updateApproximateAgeField(contentBinding);
			DataUtils.updateListOfDays(
				contentBinding.personBirthdateDD,
				(Integer) field.getValue(),
				(Integer) contentBinding.personBirthdateMM.getValue());
		});
		int year = Calendar.getInstance().get(Calendar.YEAR);
		contentBinding.personBirthdateYYYY.setSelectionOnOpen(year - 35);
		contentBinding.personApproximateAgeType.initializeSpinner(approximateAgeTypeList);
		contentBinding.personSex.initializeSpinner(sexList);
		contentBinding.personCauseOfDeath.initializeSpinner(causeOfDeathList);
		contentBinding.personCauseOfDeathDisease.initializeSpinner(diseaseList);
		contentBinding.personDeathPlaceType.initializeSpinner(deathPlaceTypeList);
		contentBinding.personBurialConductor.initializeSpinner(burialConductorList);
		contentBinding.personOccupationType.initializeSpinner(DataUtils.getEnumItems(OccupationType.class, true));
		contentBinding.personEducationType.initializeSpinner(DataUtils.getEnumItems(EducationType.class, true));
		contentBinding.personPresentCondition.initializeSpinner(DataUtils.getEnumItems(PresentCondition.class, true));

		contentBinding.personApproximateAge.addValueChangedListener(field -> {
			if (DataHelper.isNullOrEmpty((String) field.getValue())) {
				contentBinding.personApproximateAgeType.setRequired(false);
				contentBinding.personApproximateAgeType.setValue(null);
			} else {
				contentBinding.personApproximateAgeType.setRequired(true);
				if (contentBinding.personApproximateAgeType.getValue() == null) {
					contentBinding.personApproximateAgeType.setValue(ApproximateAgeType.YEARS);
				}
			}
		});

		if (!DataHelper.isNullOrEmpty(contentBinding.personApproximateAge.getValue())) {
			contentBinding.personApproximateAgeType.setRequired(true);
			if (contentBinding.personApproximateAgeType.getValue() == null) {
				contentBinding.personApproximateAgeType.setValue(ApproximateAgeType.YEARS);
			}
		}

		// Initialize ControlDateFields
		contentBinding.personDeathDate.initializeDateField(fragment.getFragmentManager());
		contentBinding.personBurialDate.initializeDateField(fragment.getFragmentManager());
	}

	public static void setUpControlListeners(
		final Person record,
		final BaseEditFragment fragment,
		final FragmentPersonEditLayoutBinding contentBinding) {
		contentBinding.personAddress.setOnClickListener(v -> openAddressPopup(record, fragment, contentBinding));
	}

	public static Date calculateBirthDateValue(FragmentPersonEditLayoutBinding contentBinding) {
		Integer birthYear = (Integer) contentBinding.personBirthdateYYYY.getValue();

		if (birthYear != null) {
			contentBinding.personApproximateAge.setEnabled(false);
			contentBinding.personApproximateAgeType.setEnabled(false);

			Integer birthDay = (Integer) contentBinding.personBirthdateDD.getValue();
			Integer birthMonth = (Integer) contentBinding.personBirthdateMM.getValue();

			Calendar birthDate = new GregorianCalendar();
			birthDate.set(birthYear, birthMonth != null ? birthMonth : 0, birthDay != null ? birthDay : 1);
			return birthDate.getTime();
		}
		return null;
	}

	private static void updateApproximateAgeField(FragmentPersonEditLayoutBinding contentBinding) {

		Date birthDate = calculateBirthDateValue(contentBinding);
		if (birthDate != null) {
			contentBinding.personApproximateAge.setEnabled(false);
			contentBinding.personApproximateAgeType.setEnabled(false);

			Date to = new Date();
			if (contentBinding.personDeathDate != null) {
				to = contentBinding.personDeathDate.getValue();
			}

			DataHelper.Pair<Integer, ApproximateAgeType> approximateAge = ApproximateAgeType.ApproximateAgeHelper.getApproximateAge(birthDate, to);
			ApproximateAgeType ageType = approximateAge.getElement1();
			contentBinding.personApproximateAge.setValue(String.valueOf(approximateAge.getElement0()));
			contentBinding.personApproximateAgeType.setValue(ageType);
		} else {
			contentBinding.personApproximateAge.setEnabled(true);
			contentBinding.personApproximateAgeType.setEnabled(true);
		}
	}

	private static void openAddressPopup(final Person record, final BaseEditFragment fragment, final FragmentPersonEditLayoutBinding contentBinding) {
		final Location location = record.getAddress();
		final Location locationClone = (Location) location.clone();
		final LocationDialog locationDialog = new LocationDialog(BaseActivity.getActiveActivity(), locationClone, fragment.getFieldAccessCheckers());
		locationDialog.show();

		locationDialog.setPositiveCallback(() -> {
			contentBinding.personAddress.setValue(locationClone);
			record.setAddress(locationClone);
		});
	}

	/**
	 * Only show the causeOfDeathDetails field when either the selected cause of death is 'Other cause'
	 * or the selected cause of death disease is 'Other'. Additionally, adjust the caption of the
	 * causeOfDeathDetails field based on the selected options.
	 */
	public static void initializeCauseOfDeathDetailsFieldVisibility(
		final ControlPropertyField causeOfDeathField,
		final ControlPropertyField causeOfDeathDiseaseField,
		final ControlPropertyField causeOfDeathDetailsField) {
		setCauseOfDeathDetailsFieldVisibility(causeOfDeathField, causeOfDeathDiseaseField, causeOfDeathDetailsField);
		causeOfDeathField.addValueChangedListener(
			field -> setCauseOfDeathDetailsFieldVisibility(causeOfDeathField, causeOfDeathDiseaseField, causeOfDeathDetailsField));
		causeOfDeathDiseaseField.addValueChangedListener(
			field -> setCauseOfDeathDetailsFieldVisibility(causeOfDeathField, causeOfDeathDiseaseField, causeOfDeathDetailsField));
	}

	private static void setCauseOfDeathDetailsFieldVisibility(
		final ControlPropertyField causeOfDeathField,
		final ControlPropertyField causeOfDeathDiseaseField,
		final ControlPropertyField causeOfDeathDetailsField) {
		CauseOfDeath selectedCauseOfDeath = (CauseOfDeath) causeOfDeathField.getValue();
		Disease selectedCauseOfDeathDisease = (Disease) causeOfDeathDiseaseField.getValue();

		if (selectedCauseOfDeath == CauseOfDeath.OTHER_CAUSE) {
			causeOfDeathDetailsField.setVisibility(VISIBLE);
			causeOfDeathDetailsField.setCaption(I18nProperties.getPrefixCaption(PersonDto.I18N_PREFIX, PersonDto.CAUSE_OF_DEATH_DETAILS));
		} else if (selectedCauseOfDeathDisease == Disease.OTHER) {
			causeOfDeathDetailsField.setVisibility(VISIBLE);
			causeOfDeathDetailsField.setCaption(I18nProperties.getPrefixCaption(PersonDto.I18N_PREFIX, PersonDto.CAUSE_OF_DEATH_DISEASE_DETAILS));
		} else {
			causeOfDeathDetailsField.setVisibility(GONE);
		}
	}

	/**
	 * Only show the occupationDetails field when an appropriate occupation is selected. Additionally,
	 * adjust the caption of the occupationDetails field based on the selected occupation.
	 */
	public static void initializeOccupationDetailsFieldVisibility(
		final ControlPropertyField occupationTypeField,
		final ControlPropertyField occupationDetailsField) {
		setOccupationDetailsFieldVisibility(occupationTypeField, occupationDetailsField);
		occupationTypeField.addValueChangedListener(new ValueChangeListener() {

			@Override
			public void onChange(ControlPropertyField field) {
				setOccupationDetailsFieldVisibility(occupationTypeField, occupationDetailsField);
			}
		});
	}

	private static void setOccupationDetailsFieldVisibility(
		final ControlPropertyField occupationTypeField,
		final ControlPropertyField occupationDetailsField) {
		OccupationType selectedOccupationType = (OccupationType) occupationTypeField.getValue();
		if (selectedOccupationType != null) {
			switch (selectedOccupationType) {
			case BUSINESSMAN_WOMAN:
				occupationDetailsField.setVisibility(VISIBLE);
				occupationDetailsField.setCaption(I18nProperties.getCaption(PersonDto.I18N_PREFIX + ".business." + PersonDto.OCCUPATION_DETAILS));
				break;
			case TRANSPORTER:
				occupationDetailsField.setVisibility(VISIBLE);
				occupationDetailsField.setCaption(I18nProperties.getCaption(PersonDto.I18N_PREFIX + ".transporter." + PersonDto.OCCUPATION_DETAILS));
				break;
			case HEALTHCARE_WORKER:
				occupationDetailsField.setVisibility(VISIBLE);
				occupationDetailsField.setCaption(I18nProperties.getCaption(PersonDto.I18N_PREFIX + ".healthcare." + PersonDto.OCCUPATION_DETAILS));
				break;
			case OTHER:
				occupationDetailsField.setVisibility(VISIBLE);
				occupationDetailsField.setCaption(I18nProperties.getPrefixCaption(PersonDto.I18N_PREFIX, PersonDto.OCCUPATION_DETAILS));
				break;
			default:
				occupationDetailsField.setVisibility(GONE);
				break;
			}
		} else {
			occupationDetailsField.setVisibility(GONE);
		}
	}

	// Overrides

	@Override
	protected String getSubHeadingTitle() {
		return getResources().getString(R.string.caption_person_information);
	}

	@Override
	public Person getPrimaryData() {
		return record;
	}

	@Override
	protected void prepareFragmentData() {
		AbstractDomainObject ado = getActivityRootData();

		if (ado instanceof Case) {
			record = ((Case) ado).getPerson();
			rootData = ado;
		} else if (ado instanceof Contact) {
			record = ((Contact) ado).getPerson();
			rootData = ado;
		} else {
			throw new UnsupportedOperationException(
				"ActivityRootData of class " + ado.getClass().getSimpleName() + " does not support PersonEditFragment");
		}
	}

	@Override
	public void onLayoutBinding(FragmentPersonEditLayoutBinding contentBinding) {
		contentBinding.setData(record);

		PersonValidator.initializePersonValidation(contentBinding);
	}

	@Override
	public void onAfterLayoutBinding(final FragmentPersonEditLayoutBinding contentBinding) {
		PersonEditFragment.setUpLayoutBinding(this, record, contentBinding, rootData);
	}

	@Override
	public int getEditLayout() {
		return R.layout.fragment_person_edit_layout;
	}
}
