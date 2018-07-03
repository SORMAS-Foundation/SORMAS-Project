package de.symeda.sormas.app.caze.edit;

import android.content.res.Resources;
import android.databinding.ViewDataBinding;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;

import com.android.databinding.library.baseAdapters.BR;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.person.ApproximateAgeType;
import de.symeda.sormas.api.person.ApproximateAgeType.ApproximateAgeHelper;
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
import de.symeda.sormas.app.backend.location.Location;
import de.symeda.sormas.app.backend.person.Person;
import de.symeda.sormas.app.component.Item;
import de.symeda.sormas.app.component.VisualState;
import de.symeda.sormas.app.component.controls.ControlDateField;
import de.symeda.sormas.app.component.controls.ControlPropertyField;
import de.symeda.sormas.app.component.controls.ControlSpinnerField;
import de.symeda.sormas.app.component.controls.ValueChangeListener;
import de.symeda.sormas.app.component.dialog.LocationDialog;
import de.symeda.sormas.app.component.dialog.TeboAlertDialogInterface;
import de.symeda.sormas.app.core.IEntryItemOnClickListener;
import de.symeda.sormas.app.core.OnSetBindingVariableListener;
import de.symeda.sormas.app.databinding.FragmentCaseEditPatientLayoutBinding;
import de.symeda.sormas.app.shared.CaseFormNavigationCapsule;
import de.symeda.sormas.app.shared.OnDateOfDeathChangeListener;
import de.symeda.sormas.app.util.DataUtils;
import de.symeda.sormas.app.util.layoutprocessor.OccupationTypeLayoutProcessor;
import de.symeda.sormas.app.util.layoutprocessor.PresentConditionLayoutProcessor;

public class CaseEditPersonFragment extends BaseEditFragment<FragmentCaseEditPatientLayoutBinding, Person, Case> {

    public static final String TAG = CaseEditPersonFragment.class.getSimpleName();

    private static final int DEFAULT_YEAR = 2000;

    private Person record;

    private List<Item> dayList;
    private List<Item> monthList;
    private List<Item> yearList;
    private List<Item> ageTypeList;
    private List<Item> genderList;
    private List<Item> occupationTypeList;

    private List<Item> causeOfDeathList;
    private List<Item> deathPlaceTypeList;
    private List<Item> diseaseList;
    private List<Item> burialConductorList;

    private IEntryItemOnClickListener onAddressLinkClickedCallback;

    private OccupationTypeLayoutProcessor occupationTypeLayoutProcessor;
    private PresentConditionLayoutProcessor presentConditionLayoutProcessor;

    @Override
    protected String getSubHeadingTitle() {
        Resources r = getResources();
        return r.getString(R.string.caption_patient_information);
    }

    @Override
    public Person getPrimaryData() {
        return record;
    }

    @Override
    protected void prepareFragmentData(Bundle savedInstanceState) {
        Case caze = getActivityRootData();
        record = caze.getPerson();

        // TODO not necessary to prepare this?
        occupationTypeList = DataUtils.getEnumItems(OccupationType.class, false);
        genderList = DataUtils.getEnumItems(Sex.class, false);
        ageTypeList = DataUtils.getEnumItems(ApproximateAgeType.class, false);
        dayList = DataUtils.toItems(DateHelper.getDaysInMonth(), true);
        monthList = DataUtils.getMonthItems(true);
        yearList = DataUtils.toItems(DateHelper.getYearsToNow(), true);
        causeOfDeathList = DataUtils.getEnumItems(CauseOfDeath.class, false);
        deathPlaceTypeList = DataUtils.getEnumItems(DeathPlaceType.class, false);
        diseaseList = DataUtils.getEnumItems(Disease.class, false);
        burialConductorList = DataUtils.getEnumItems(BurialConductor.class, false);
    }

    @Override
    public void onLayoutBinding(FragmentCaseEditPatientLayoutBinding contentBinding) {

        setupCallback();

        occupationTypeLayoutProcessor = new OccupationTypeLayoutProcessor(getContext(), contentBinding, record);
        occupationTypeLayoutProcessor.setOnSetBindingVariable(new OnSetBindingVariableListener() {
            @Override
            public void onSetBindingVariable(ViewDataBinding binding, String layoutName) {
//                setRootNotificationBindingVariable(binding, layoutName);
                setLocalBindingVariable(binding, layoutName);
            }
        });

        presentConditionLayoutProcessor = new PresentConditionLayoutProcessor(getContext(),
                getFragmentManager(), contentBinding, record, causeOfDeathList, deathPlaceTypeList, diseaseList, burialConductorList);
        presentConditionLayoutProcessor.setOnSetBindingVariable(new OnSetBindingVariableListener() {
            @Override
            public void onSetBindingVariable(ViewDataBinding binding, String layoutName) {
//                setRootNotificationBindingVariable(binding, layoutName);
                setLocalBindingVariable(binding, layoutName);
            }
        });
        presentConditionLayoutProcessor.setOnDateOfDeathChange(new OnDateOfDeathChangeListener() {
            @Override
            public void onChange(ControlDateField view, Date value) {
                updateApproximateAgeField();
            }
        });

        contentBinding.setData(record);
        contentBinding.setPresentConditionClass(PresentCondition.class);
        contentBinding.setAddressLinkCallback(onAddressLinkClickedCallback);
    }

    @Override
    public void onAfterLayoutBinding(FragmentCaseEditPatientLayoutBinding contentBinding) {
        contentBinding.spnOccupationType.initializeSpinner(occupationTypeList, null, new ValueChangeListener() {
            @Override
            public void onChange(ControlPropertyField field) {
                if (!occupationTypeLayoutProcessor.processLayout((OccupationType) field.getValue()))
                    return;
            }
        });

        contentBinding.spnGender.initializeSpinner(genderList);

        contentBinding.spnAgeType.initializeSpinner(ageTypeList, null, new ValueChangeListener() {
            @Override
            public void onChange(ControlPropertyField field) {
                updateApproximateAgeField();
            }
        });

        contentBinding.spnYear.initializeSpinner(yearList, null, new ValueChangeListener() {
            @Override
            public void onChange(ControlPropertyField field) {
                updateApproximateAgeField();
            }
        });

        contentBinding.spnMonth.initializeSpinner(monthList, null, new ValueChangeListener() {
            @Override
            public void onChange(ControlPropertyField field) {
                updateApproximateAgeField();
            }
        });

        contentBinding.spnDate.initializeSpinner(dayList, null, new ValueChangeListener() {
            @Override
            public void onChange(ControlPropertyField field) {
                updateApproximateAgeField();
            }
        });
    }

    @Override
    protected void updateUI(FragmentCaseEditPatientLayoutBinding contentBinding, Person person) {
        contentBinding.spnOccupationType.setValue(person.getOccupationType());
        contentBinding.spnGender.setValue(person.getSex());
        contentBinding.spnAgeType.setValue(person.getApproximateAgeType());
        contentBinding.spnYear.setValue(person.getBirthdateYYYY());
        contentBinding.spnMonth.setValue(person.getBirthdateMM());
        contentBinding.spnDate.setValue(person.getBirthdateDD());
    }

    @Override
    public int getEditLayout() {
        return R.layout.fragment_case_edit_patient_layout;
    }

    @Override
    public boolean isShowSaveAction() {
        return true;
    }

    @Override
    public boolean isShowAddAction() {
        return false;
    }

    private void setupCallback() {

        onAddressLinkClickedCallback = new IEntryItemOnClickListener() {
            @Override
            public void onClick(View v, Object item) {
                final Location location = record.getAddress();
                final LocationDialog locationDialog = new LocationDialog(BaseActivity.getActiveActivity(), location);
                locationDialog.show(null);


                locationDialog.setOnPositiveClickListener(new TeboAlertDialogInterface.PositiveOnClickListener() {
                    @Override
                    public void onOkClick(View v, Object item, View viewRoot) {
                        getContentBinding().txtPermAddress.setValue(location.toString());
                        record.setAddress(location);

                        locationDialog.dismiss();
                    }
                });
            }
        };
    }

    private void setLocalBindingVariable(final ViewDataBinding binding, String layoutName) {
        if (!binding.setVariable(BR.data, record)) {
            Log.e(TAG, "There is no variable 'data' in layout " + layoutName);
        }
    }

    private void updateApproximateAgeField() {
        Integer birthyear = record.getBirthdateYYYY();
        //TeboTextRead approximateAgeTextField =  getContentBinding().txtAge;
        //TeboSpinner approximateAgeTypeField = getContentBinding().spnAgeType;

        if (birthyear != null) {
            Integer birthday = record.getBirthdateDD();
            Integer birthmonth = record.getBirthdateMM();

            Calendar birthDate = new GregorianCalendar();
            birthDate.set(birthyear, birthmonth != null ? birthmonth - 1 : 0, birthday != null ? birthday : 1);

            Date to = new Date();
            if (record.getDeathDate() != null) {
                to = record.getDeathDate();
            }
            DataHelper.Pair<Integer, ApproximateAgeType> approximateAge = ApproximateAgeHelper.getApproximateAge(birthDate.getTime(), to);
            ApproximateAgeType ageType = approximateAge.getElement1();
            Integer age = approximateAge.getElement0();

            record.setApproximateAge(age);
            record.setApproximateAgeType(ageType);

            updateUI();
        } else {
            //getContentBinding().txtAge.setEnabled(true, editOrCreateUserRight);
            //getContentBinding().spnAgeType.setEnabled(true, editOrCreateUserRight);
        }
    }

    public static CaseEditPersonFragment newInstance(CaseFormNavigationCapsule capsule, Case activityRootData) {
        return newInstance(CaseEditPersonFragment.class, capsule, activityRootData);
    }
}
