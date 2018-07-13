package de.symeda.sormas.app.person.edit;

import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.person.ApproximateAgeType;
import de.symeda.sormas.api.person.BurialConductor;
import de.symeda.sormas.api.person.CauseOfDeath;
import de.symeda.sormas.api.person.DeathPlaceType;
import de.symeda.sormas.api.person.OccupationType;
import de.symeda.sormas.api.person.PresentCondition;
import de.symeda.sormas.api.person.Sex;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.app.BaseActivity;
import de.symeda.sormas.app.BaseEditFragment;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.caze.Case;
import de.symeda.sormas.app.backend.common.AbstractDomainObject;
import de.symeda.sormas.app.backend.contact.Contact;
import de.symeda.sormas.app.backend.location.Location;
import de.symeda.sormas.app.backend.person.Person;
import de.symeda.sormas.app.component.Item;
import de.symeda.sormas.app.component.controls.ControlPropertyField;
import de.symeda.sormas.app.component.controls.ValueChangeListener;
import de.symeda.sormas.app.component.dialog.LocationDialog;
import de.symeda.sormas.app.component.dialog.TeboAlertDialogInterface;
import de.symeda.sormas.app.core.BaseFormNavigationCapsule;
import de.symeda.sormas.app.databinding.FragmentPersonEditLayoutBinding;
import de.symeda.sormas.app.util.DataUtils;

public class PersonEditFragment extends BaseEditFragment<FragmentPersonEditLayoutBinding, Person, AbstractDomainObject> {

    public static final String TAG = PersonEditFragment.class.getSimpleName();

    private Person record;

    // Spinner lists

    private List<Item> dayList;
    private List<Item> monthList;
    private List<Item> yearList;
    private List<Item> approximateAgeTypeList;
    private List<Item> sexList;
    private List<Item> causeOfDeathList;
    private List<Item> diseaseList;
    private List<Item> deathPlaceTypeList;
    private List<Item> burialConductorList;
    private List<Item> occupationTypeList;

    // Instance methods

    public static PersonEditFragment newInstance(BaseFormNavigationCapsule capsule, AbstractDomainObject activityRootData) {
        return newInstance(PersonEditFragment.class, capsule, activityRootData);
    }

    private void setUpFieldVisibilities(FragmentPersonEditLayoutBinding contentBinding) {
        initializeHealthFacilityDetailsFieldVisibility(contentBinding.personOccupationFacility, contentBinding.personOccupationFacilityDetails);
        initializeCauseOfDeathDetailsFieldVisibility(contentBinding.personCauseOfDeath, contentBinding.personCauseOfDeathDisease, contentBinding.personCauseOfDeathDetails);
        initializeOccupationDetailsFieldVisibility(contentBinding.personOccupationType, contentBinding.personOccupationDetails);
        initializeFacilityFields(contentBinding.personOccupationRegion, contentBinding.personOccupationDistrict, contentBinding.personOccupationCommunity, contentBinding.personOccupationFacility);
    }

    private void setUpControlListeners(final FragmentPersonEditLayoutBinding contentBinding) {
        contentBinding.personAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openAddressPopup(contentBinding);
            }
        });
    }

    private void updateApproximateAgeField(FragmentPersonEditLayoutBinding contentBinding) {
        Integer birthYear = (Integer) contentBinding.personBirthdateYYYY.getValue();

        if (birthYear != null) {
            contentBinding.personApproximateAge.setEnabled(false);
            contentBinding.personApproximateAgeType.setEnabled(false);

            Integer birthDay = (Integer) contentBinding.personBirthdateDD.getValue();
            Integer birthMonth = (Integer) contentBinding.personBirthdateMM.getValue();

            Calendar birthDate = new GregorianCalendar();
            birthDate.set(birthYear, birthMonth != null ? birthMonth - 1 : 0, birthDay != null ? birthDay : 1);

            Date to = new Date();
            if (contentBinding.personDeathDate != null) {
                to = contentBinding.personDeathDate.getValue();
            }

            DataHelper.Pair<Integer, ApproximateAgeType> approximateAge = ApproximateAgeType.ApproximateAgeHelper.getApproximateAge(birthDate.getTime(), to);
            ApproximateAgeType ageType = approximateAge.getElement1();
            contentBinding.personApproximateAge.setValue(String.valueOf(approximateAge.getElement0()));
            contentBinding.personApproximateAgeType.setValue(ageType);
        } else {
            contentBinding.personApproximateAge.setEnabled(true);
            contentBinding.personApproximateAgeType.setEnabled(true);
        }
    }

    private void openAddressPopup(final FragmentPersonEditLayoutBinding contentBinding) {
        final Location location = record.getAddress();
        final LocationDialog locationDialog = new LocationDialog(BaseActivity.getActiveActivity(), location);
        locationDialog.show(null);

        locationDialog.setOnPositiveClickListener(new TeboAlertDialogInterface.PositiveOnClickListener() {
            @Override
            public void onOkClick(View v, Object item, View viewRoot) {
                contentBinding.personAddress.setValue(location);
                locationDialog.dismiss();
            }
        });
    }

    // Overrides

    @Override
    protected String getSubHeadingTitle() {
        Resources r = getResources();
        return r.getString(R.string.caption_person_information);
    }

    @Override
    public Person getPrimaryData() {
        return record;
    }

    @Override
    protected void prepareFragmentData(Bundle savedInstanceState) {
        AbstractDomainObject ado = getActivityRootData();

        if (ado instanceof Case) {
            record = ((Case) ado).getPerson();
        } else if (ado instanceof Contact) {
            record = ((Contact) ado).getPerson();
        } else {
            throw new UnsupportedOperationException("ActivityRootData of class " + ado.getClass().getSimpleName()
                    + " does not support PersonReadFragment");
        }

        dayList = DataUtils.toItems(DateHelper.getDaysInMonth(), true);
        monthList = DataUtils.getMonthItems(true);
        yearList = DataUtils.toItems(DateHelper.getYearsToNow(), true);
        approximateAgeTypeList = DataUtils.getEnumItems(ApproximateAgeType.class, true);
        sexList = DataUtils.getEnumItems(Sex.class, true);
        causeOfDeathList = DataUtils.getEnumItems(CauseOfDeath.class, true);
        diseaseList = DataUtils.getEnumItems(Disease.class, true);
        deathPlaceTypeList = DataUtils.getEnumItems(DeathPlaceType.class, true);
        burialConductorList = DataUtils.getEnumItems(BurialConductor.class, true);
        occupationTypeList = DataUtils.getEnumItems(OccupationType.class, true);
    }

    @Override
    public void onLayoutBinding(FragmentPersonEditLayoutBinding contentBinding) {
        setUpControlListeners(contentBinding);

        contentBinding.setData(record);
        contentBinding.setPresentConditionClass(PresentCondition.class);
    }

    @Override
    public void onAfterLayoutBinding(final FragmentPersonEditLayoutBinding contentBinding) {
        setUpFieldVisibilities(contentBinding);

        // Initialize ControlSpinnerFields
        contentBinding.personBirthdateDD.initializeSpinner(dayList, new ValueChangeListener() {
            @Override
            public void onChange(ControlPropertyField field) {
                updateApproximateAgeField(contentBinding);
            }
        });
        contentBinding.personBirthdateMM.initializeSpinner(monthList, new ValueChangeListener() {
            @Override
            public void onChange(ControlPropertyField field) {
                updateApproximateAgeField(contentBinding);
            }
        });
        contentBinding.personBirthdateYYYY.initializeSpinner(yearList, new ValueChangeListener() {
            @Override
            public void onChange(ControlPropertyField field) {
                updateApproximateAgeField(contentBinding);
            }
        });
        contentBinding.personApproximateAgeType.initializeSpinner(approximateAgeTypeList);
        contentBinding.personSex.initializeSpinner(sexList);
        contentBinding.personCauseOfDeath.initializeSpinner(causeOfDeathList);
        contentBinding.personCauseOfDeathDisease.initializeSpinner(diseaseList);
        contentBinding.personDeathPlaceType.initializeSpinner(deathPlaceTypeList);
        contentBinding.personBurialConductor.initializeSpinner(burialConductorList);
        contentBinding.personOccupationType.initializeSpinner(occupationTypeList);

        // Initialize ControlDateFields
        contentBinding.personDeathDate.initializeDateField(getFragmentManager());
        contentBinding.personBurialDate.initializeDateField(getFragmentManager());
    }

    @Override
    public int getEditLayout() {
        return R.layout.fragment_person_edit_layout;
    }

    @Override
    public boolean isShowSaveAction() {
        return true;
    }

    @Override
    public boolean isShowAddAction() {
        return false;
    }

}
