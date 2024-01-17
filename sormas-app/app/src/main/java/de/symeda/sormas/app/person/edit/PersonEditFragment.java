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
import java.util.Optional;

import android.view.View;
import android.view.ViewGroup;

import androidx.databinding.ObservableArrayList;
import androidx.databinding.ObservableList;

import de.symeda.sormas.api.CountryHelper;
import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.customizableenum.CustomizableEnum;
import de.symeda.sormas.api.customizableenum.CustomizableEnumType;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.infrastructure.facility.FacilityType;
import de.symeda.sormas.api.location.LocationDto;
import de.symeda.sormas.api.person.ApproximateAgeType;
import de.symeda.sormas.api.person.ArmedForcesRelationType;
import de.symeda.sormas.api.person.BurialConductor;
import de.symeda.sormas.api.person.CauseOfDeath;
import de.symeda.sormas.api.person.DeathPlaceType;
import de.symeda.sormas.api.person.EducationType;
import de.symeda.sormas.api.person.PersonContactDetailDto;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.person.PresentCondition;
import de.symeda.sormas.api.person.Salutation;
import de.symeda.sormas.api.person.Sex;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.api.utils.fieldaccess.UiFieldAccessCheckers;
import de.symeda.sormas.api.utils.fieldvisibility.FieldVisibilityCheckers;
import de.symeda.sormas.api.utils.fieldvisibility.checkers.CountryFieldVisibilityChecker;
import de.symeda.sormas.app.BaseActivity;
import de.symeda.sormas.app.BaseEditFragment;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.caze.Case;
import de.symeda.sormas.app.backend.common.AbstractDomainObject;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.common.PseudonymizableAdo;
import de.symeda.sormas.app.backend.config.ConfigProvider;
import de.symeda.sormas.app.backend.contact.Contact;
import de.symeda.sormas.app.backend.event.EventParticipant;
import de.symeda.sormas.app.backend.immunization.Immunization;
import de.symeda.sormas.app.backend.location.Location;
import de.symeda.sormas.app.backend.person.Person;
import de.symeda.sormas.app.backend.person.PersonContactDetail;
import de.symeda.sormas.app.component.Item;
import de.symeda.sormas.app.component.controls.ControlPropertyField;
import de.symeda.sormas.app.component.dialog.ConfirmationDialog;
import de.symeda.sormas.app.component.dialog.LocationDialog;
import de.symeda.sormas.app.core.IEntryItemOnClickListener;
import de.symeda.sormas.app.databinding.FragmentPersonEditLayoutBinding;
import de.symeda.sormas.app.person.PersonContactDetailDialog;
import de.symeda.sormas.app.util.Callback;
import de.symeda.sormas.app.util.DataUtils;
import de.symeda.sormas.app.util.DiseaseConfigurationCache;
import de.symeda.sormas.app.util.InfrastructureDaoHelper;
import de.symeda.sormas.app.util.InfrastructureFieldsDependencyHandler;

public class PersonEditFragment extends BaseEditFragment<FragmentPersonEditLayoutBinding, Person, PseudonymizableAdo> {

	public static final String TAG = PersonEditFragment.class.getSimpleName();

	private Person record;
	private AbstractDomainObject rootData;
	private IEntryItemOnClickListener onAddressItemClickListener;
	private IEntryItemOnClickListener onPersonContactDetailItemClickListener;

	// Instance methods

	public static PersonEditFragment newInstance(Case activityRootData) {

		return newInstanceWithFieldCheckers(
			PersonEditFragment.class,
			null,
			activityRootData,
			FieldVisibilityCheckers.withDisease(activityRootData.getDisease())
				.add(new CountryFieldVisibilityChecker(ConfigProvider.getServerLocale())),
			UiFieldAccessCheckers.getDefault(activityRootData.isPseudonymized()));
	}

	public static PersonEditFragment newInstance(Contact activityRootData) {

		return newInstanceWithFieldCheckers(
			PersonEditFragment.class,
			null,
			activityRootData,
			FieldVisibilityCheckers.withDisease(activityRootData.getDisease()),
			UiFieldAccessCheckers.getDefault(activityRootData.isPseudonymized()));
	}

	public static PersonEditFragment newInstance(Immunization activityRootData) {

		return newInstanceWithFieldCheckers(
			PersonEditFragment.class,
			null,
			activityRootData,
			FieldVisibilityCheckers.withDisease(activityRootData.getDisease()),
			UiFieldAccessCheckers.getDefault(activityRootData.isPseudonymized()));
	}

	public static PersonEditFragment newInstance(EventParticipant activityRootData) {

		return newInstanceWithFieldCheckers(
			PersonEditFragment.class,
			null,
			activityRootData,
			FieldVisibilityCheckers.withDisease(activityRootData.getEvent().getDisease()),
			UiFieldAccessCheckers.getDefault(activityRootData.isPseudonymized()));
	}

	private void setUpLayoutBinding(final BaseEditFragment fragment, final Person record, final FragmentPersonEditLayoutBinding contentBinding) {

		setUpControlListeners(record, fragment, contentBinding);

		fragment.setFieldVisibilitiesAndAccesses(PersonDto.class, contentBinding.mainContent);

		List<Item> salutationList = DataUtils.getEnumItems(Salutation.class, true);
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

		List<Item> initialPlaceOfBirthRegions = InfrastructureDaoHelper.loadRegionsByServerCountry();
		List<Item> initialPlaceOfBirthDistricts = InfrastructureDaoHelper.loadDistricts(record.getPlaceOfBirthRegion());
		List<Item> initialPlaceOfBirthCommunities = InfrastructureDaoHelper.loadCommunities(record.getPlaceOfBirthDistrict());
		List<Item> initialPlaceOfBirthFacilities =
			InfrastructureDaoHelper.loadFacilities(record.getPlaceOfBirthDistrict(), record.getPlaceOfBirthCommunity(), null);

		List<Item> occupationTypeList = DataUtils.toItems(
			DatabaseHelper.getCustomizableEnumValueDao()
				.getEnumValues(
					CustomizableEnumType.OCCUPATION_TYPE,
					Optional.ofNullable(record.getOccupationType()).map(CustomizableEnum::getValue).orElse(null),
					null));
		List<Item> placeOfBirthFacilityTypeList = DataUtils.toItems(FacilityType.getPlaceOfBirthTypes(), true);
		List<Item> countryList = InfrastructureDaoHelper.loadCountries();

		InfrastructureDaoHelper.initializeHealthFacilityDetailsFieldVisibility(
			contentBinding.personPlaceOfBirthFacility,
			contentBinding.personPlaceOfBirthFacilityDetails);
		initializeCauseOfDeathDetailsFieldVisibility(
			contentBinding.personCauseOfDeath,
			contentBinding.personCauseOfDeathDisease,
			contentBinding.personCauseOfDeathDetails);

		InfrastructureFieldsDependencyHandler.instance.initializeFacilityFields(
			record,
			contentBinding.personPlaceOfBirthRegion,
			initialPlaceOfBirthRegions,
			record.getPlaceOfBirthRegion(),
			contentBinding.personPlaceOfBirthDistrict,
			initialPlaceOfBirthDistricts,
			record.getPlaceOfBirthDistrict(),
			contentBinding.personPlaceOfBirthCommunity,
			initialPlaceOfBirthCommunities,
			record.getPlaceOfBirthCommunity(),
			null,
			null,
			null,
			null,
			contentBinding.personPlaceOfBirthFacilityType,
			placeOfBirthFacilityTypeList,
			contentBinding.personPlaceOfBirthFacility,
			initialPlaceOfBirthFacilities,
			record.getPlaceOfBirthFacility(),
			contentBinding.personPlaceOfBirthFacilityDetails,
			false);

		// Initialize ControlSpinnerFields
		contentBinding.personSalutation.initializeSpinner(salutationList);
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
		FieldVisibilityCheckers countryVisibilityChecker = FieldVisibilityCheckers.withCountry(ConfigProvider.getServerCountryCode());
		contentBinding.personBirthdateYYYY.setSelectionOnOpen(year - 35);
		contentBinding.personApproximateAgeType.initializeSpinner(approximateAgeTypeList);
		contentBinding.personSex.initializeSpinner(sexList);
		contentBinding.personCauseOfDeath.initializeSpinner(causeOfDeathList);
		contentBinding.personCauseOfDeathDisease.initializeSpinner(diseaseList);
		contentBinding.personDeathPlaceType.initializeSpinner(deathPlaceTypeList);
		contentBinding.personBurialConductor.initializeSpinner(burialConductorList);
		contentBinding.personOccupationType.initializeSpinner(occupationTypeList);
		contentBinding.personArmedForcesRelationType.initializeSpinner(DataUtils.getEnumItems(ArmedForcesRelationType.class, true));
		contentBinding.personEducationType.initializeSpinner(DataUtils.getEnumItems(EducationType.class, true));
		// Determine which values should show as personPresentCondition (the person may have a value that by default is not shown for the current disease)
		List<Item> items = DataUtils.getEnumItems(PresentCondition.class, true, getFieldVisibilityCheckers());
		PresentCondition currentValue = record.getPresentCondition();
		if (currentValue != null) {
			Item currentValueItem = new Item(currentValue.toString(), currentValue);
			if (!items.contains(currentValueItem)) {
				items.add(currentValueItem);
			}
		}
		contentBinding.personPresentCondition.initializeSpinner(items);
		contentBinding.personPresentCondition.addValueChangedListener(field -> setBurialFieldVisibilities(contentBinding));
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

		contentBinding.personBirthCountry.initializeSpinner(countryList);
		contentBinding.personCitizenship.initializeSpinner(countryList);

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
			birthDate.set(birthYear, birthMonth != null ? birthMonth - 1 : 0, birthDay != null ? birthDay : 1);
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
			if (contentBinding.personApproximateAge.isEnabled() == false && contentBinding.personApproximateAgeType.isEnabled() == false) {
				contentBinding.personApproximateAge.setValue(null);
				contentBinding.personApproximateAgeType.setValue(null);
			}
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

	private ObservableList<Location> getAddresses() {
		ObservableArrayList<Location> newAddresses = new ObservableArrayList<>();
		newAddresses.addAll(record.getAddresses());
		return newAddresses;
	}

	private ObservableList<PersonContactDetail> getPersonContactDetails() {
		ObservableArrayList<PersonContactDetail> personContactDetails = new ObservableArrayList<>();
		personContactDetails.addAll(record.getPersonContactDetails());
		return personContactDetails;
	}

	private void setLocationFieldVisibilitiesAndAccesses(View view) {
		setFieldVisibilitiesAndAccesses(LocationDto.class, (ViewGroup) view);
	}

	private void setPersonContactDetailFieldVisibilitiesAndAccesses(View view) {
		setFieldVisibilitiesAndAccesses(PersonContactDetailDto.class, (ViewGroup) view);
	}

	private void setBurialFieldVisibilities(final FragmentPersonEditLayoutBinding contentBinding) {
		if (PresentCondition.BURIED.equals(contentBinding.personPresentCondition.getValue())) {
			if (isVisibleAllowed(PersonDto.class, contentBinding.personBurialDate)) {
				contentBinding.personBurialDate.setVisibility(VISIBLE);
			} else {
				contentBinding.personBurialDate.setVisibility(GONE);
			}

			if (isVisibleAllowed(PersonDto.class, contentBinding.personBurialPlaceDescription)) {
				contentBinding.personBurialPlaceDescription.setVisibility(VISIBLE);
			} else {
				contentBinding.personBurialPlaceDescription.setVisibility(GONE);
			}

			if (isVisibleAllowed(PersonDto.class, contentBinding.personBurialConductor)) {
				contentBinding.personBurialConductor.setVisibility(VISIBLE);
			} else {
				contentBinding.personBurialConductor.setVisibility(GONE);
			}
		}

	}

	private void setUpControlListeners() {
		onAddressItemClickListener = (v, item) -> {
			final Location address = (Location) item;
			final Location addressClone = (Location) address.clone();
			final LocationDialog dialog = new LocationDialog(BaseActivity.getActiveActivity(), addressClone, getFieldAccessCheckers());

			dialog.setPositiveCallback(() -> {
				record.getAddresses().set(record.getAddresses().indexOf(address), addressClone);
				updateAddresses();
			});

			dialog.setDeleteCallback(() -> {
				removeAddress(address);
				dialog.dismiss();
			});

			dialog.show();
			dialog.configureAsPersonAddressDialog(true);
		};
		onPersonContactDetailItemClickListener = (v, item) -> {
			final PersonContactDetail personContactDetail = (PersonContactDetail) item;
			final PersonContactDetail personContactDetailClone = (PersonContactDetail) personContactDetail.clone();
			final PersonContactDetailDialog dialog = new PersonContactDetailDialog(
				BaseActivity.getActiveActivity(),
				personContactDetailClone,
				record,
				getActivityRootData(),
				getFieldAccessCheckers(),
				false);

			dialog.setPositiveCallback(() -> checkExistingPrimaryContactDetails(personContactDetailClone, dialog, () -> {
				record.getPersonContactDetails().set(record.getPersonContactDetails().indexOf(personContactDetail), personContactDetailClone);
				updatePersonContactDetails();
			}));

			dialog.setDeleteCallback(() -> {
				removePersonContactDetail(personContactDetail);
				dialog.dismiss();
			});

			dialog.show();
			dialog.configureAsPersonContactDetailDialog(true);
		};

		getContentBinding().btnAddAddress.setOnClickListener(v -> {
			final Location address = DatabaseHelper.getLocationDao().build();
			final LocationDialog dialog = new LocationDialog(BaseActivity.getActiveActivity(), address, null);

			dialog.setPositiveCallback(() -> addAddress(address));

			dialog.show();
			dialog.configureAsPersonAddressDialog(false);
		});

		getContentBinding().btnAddPersonContactDetail.setOnClickListener(v -> {
			final PersonContactDetail personContactDetail = DatabaseHelper.getPersonContactDetailDao().build();
			final PersonContactDetailDialog dialog = new PersonContactDetailDialog(
				BaseActivity.getActiveActivity(),
				personContactDetail,
				record,
				getActivityRootData(),
				getFieldAccessCheckers(),
				true);

			dialog.setPositiveCallback(() -> checkExistingPrimaryContactDetails(personContactDetail, dialog, () -> {
				record.getPersonContactDetails().add(0, personContactDetail);
				updatePersonContactDetails();
			}));
			dialog.show();

			dialog.configureAsPersonContactDetailDialog(false);
		});
	}

	private void updateAddresses() {
		getContentBinding().setAddressList(getAddresses());
		getContentBinding().setAddressBindCallback(this::setLocationFieldVisibilitiesAndAccesses);
	}

	private void removeAddress(Location item) {
		record.getAddresses().remove(item);
		updateAddresses();
	}

	private void addAddress(Location item) {
		record.getAddresses().add(0, item);
		updateAddresses();
	}

	private void updatePersonContactDetails() {
		getContentBinding().setPersonContactDetailList(getPersonContactDetails());
		getContentBinding().setPersonContactDetailBindCallback(this::setPersonContactDetailFieldVisibilitiesAndAccesses);
	}

	private void removePersonContactDetail(PersonContactDetail item) {
		record.getPersonContactDetails().remove(item);
		updatePersonContactDetails();
	}

	private void checkExistingPrimaryContactDetails(PersonContactDetail item, PersonContactDetailDialog dialog, Callback callback) {
		final List<PersonContactDetail> personContactDetails = record.getPersonContactDetails();
		for (PersonContactDetail pcd : personContactDetails) {
			if (item.isPrimaryContact()
				&& pcd.isPrimaryContact()
				&& pcd.getPersonContactDetailType() == item.getPersonContactDetailType()
				&& !item.getUuid().equals(pcd.getUuid())) {

				final ConfirmationDialog confirmationDialog = new ConfirmationDialog(
					BaseActivity.getActiveActivity(),
					I18nProperties.getString(Strings.headingUpdatePersonContactDetails),
					I18nProperties.getString(Strings.messagePersonContactDetailsPrimaryDuplicate),
					R.string.yes,
					R.string.no);

				confirmationDialog.setPositiveCallback(() -> {
					pcd.setPrimaryContact(false);
					callback.call();
				});

				confirmationDialog.setNegativeCallback(() -> {
					item.setPrimaryContact(false);
					callback.call();
				});

				confirmationDialog.show();
				dialog.dismiss();
				return;
			}
		}

		callback.call();
		dialog.dismiss();
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
		} else if (ado instanceof EventParticipant) {
			record = ((EventParticipant) ado).getPerson();
			rootData = ado;
		} else if (ado instanceof Immunization) {
			record = ((Immunization) ado).getPerson();
			rootData = ado;
		} else {
			throw new UnsupportedOperationException(
				"ActivityRootData of class " + ado.getClass().getSimpleName() + " does not support PersonEditFragment");
		}

		// Workaround because person is not an embedded entity and therefore the locations are not
		// automatically loaded (because there's no additional queryForId call for person when the
		// parent data is loaded)
		DatabaseHelper.getPersonDao().initLocations(record);
		DatabaseHelper.getPersonDao().initPersonContactDetails(record);
	}

	@Override
	public void onLayoutBinding(FragmentPersonEditLayoutBinding contentBinding) {
		setUpControlListeners();

		contentBinding.setData(record);

		PersonValidator.initializePersonValidation(contentBinding);

		contentBinding.setAddressList(getAddresses());
		contentBinding.setAddressItemClickCallback(onAddressItemClickListener);
		getContentBinding().setAddressBindCallback(this::setLocationFieldVisibilitiesAndAccesses);

		contentBinding.setPersonContactDetailList(getPersonContactDetails());
		contentBinding.setPersonContactDetailItemClickCallback(onPersonContactDetailItemClickListener);
		getContentBinding().setPersonContactDetailBindCallback(this::setPersonContactDetailFieldVisibilitiesAndAccesses);

		setUpLayoutBinding(this, record, contentBinding);
	}

	@Override
	public void onAfterLayoutBinding(final FragmentPersonEditLayoutBinding contentBinding) {
		if (!ConfigProvider.isConfiguredServer(CountryHelper.COUNTRY_CODE_GERMANY)) {
			contentBinding.personArmedForcesRelationType.setVisibility(GONE);
		}
		contentBinding.personCitizenship.setVisibility(GONE);
		contentBinding.personBirthCountry.setVisibility(GONE);
	}

	@Override
	public int getEditLayout() {
		return R.layout.fragment_person_edit_layout;
	}
}
