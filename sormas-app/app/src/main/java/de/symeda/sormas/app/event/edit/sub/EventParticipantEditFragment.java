package de.symeda.sormas.app.event.edit.sub;

import android.app.AlertDialog;
import android.databinding.ViewDataBinding;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.android.databinding.library.baseAdapters.BR;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.person.ApproximateAgeType;
import de.symeda.sormas.api.person.ApproximateAgeType.ApproximateAgeHelper;
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
import de.symeda.sormas.app.backend.event.EventParticipant;
import de.symeda.sormas.app.backend.location.Location;
import de.symeda.sormas.app.component.Item;
import de.symeda.sormas.app.component.controls.ControlDateField;
import de.symeda.sormas.app.component.controls.ControlPropertyField;
import de.symeda.sormas.app.component.controls.ValueChangeListener;
import de.symeda.sormas.app.component.dialog.LocationDialog;
import de.symeda.sormas.app.component.dialog.TeboAlertDialogInterface;
import de.symeda.sormas.app.core.Callback;
import de.symeda.sormas.app.core.IEntryItemOnClickListener;
import de.symeda.sormas.app.core.OnSetBindingVariableListener;
import de.symeda.sormas.app.databinding.FragmentEventEditPersonInfoLayoutBinding;
import de.symeda.sormas.app.event.edit.OccupationTypeLayoutProcessor;
import de.symeda.sormas.app.event.edit.PresentConditionLayoutProcessor;
import de.symeda.sormas.app.shared.EventParticipantFormNavigationCapsule;
import de.symeda.sormas.app.shared.OnDateOfDeathChangeListener;
import de.symeda.sormas.app.util.DataUtils;

public class EventParticipantEditFragment extends BaseEditFragment<FragmentEventEditPersonInfoLayoutBinding, EventParticipant, EventParticipant> {

    public static final String TAG = EventParticipantEditFragment.class.getSimpleName();

    private EventParticipant record;
    private IEntryItemOnClickListener onAddressLinkClickedCallback;

    private List<Item> occupationTypeList;
    private List<Item> genderList;
    private List<Item> ageTypeList;
    private List<Item> dateList;
    private List<Item> monthList;
    private List<Item> yearList;

    private List<Item> causeOfDeathList;
    private List<Item> deathPlaceTypeList;
    private List<Item> diseaseList;

    private OccupationTypeLayoutProcessor occupationTypeLayoutProcessor;
    private PresentConditionLayoutProcessor presentConditionLayoutProcessor;

    @Override
    protected String getSubHeadingTitle() {
        return getResources().getString(R.string.caption_person_involved);
    }

    @Override
    public EventParticipant getPrimaryData() {
        return record;
    }

    @Override
    protected void prepareFragmentData(Bundle savedInstanceState) {
        record = getActivityRootData();

        occupationTypeList = DataUtils.getEnumItems(OccupationType.class, false);
        genderList = DataUtils.getEnumItems(Sex.class, false);
        ageTypeList = DataUtils.getEnumItems(ApproximateAgeType.class, false);
        dateList = DataUtils.toItems(DateHelper.getDaysInMonth(), true);
        monthList = DataUtils.getMonthItems(true);
        yearList = DataUtils.toItems(DateHelper.getYearsToNow(), true);
        causeOfDeathList = DataUtils.getEnumItems(CauseOfDeath.class, false);
        deathPlaceTypeList = DataUtils.getEnumItems(DeathPlaceType.class, false);
        diseaseList = DataUtils.getEnumItems(Disease.class, false);
    }

    @Override
    public void onLayoutBinding(FragmentEventEditPersonInfoLayoutBinding contentBinding) {

        setupCallback();

        //binding = DataBindingUtil.inflate(inflater, getEditLayout(), container, true);

        //TODO: Validation
        //EventParticipantValidator.setRequiredHintsForEventParticipantData(binding);

        occupationTypeLayoutProcessor = new OccupationTypeLayoutProcessor(getContext(), contentBinding, record.getPerson());
        occupationTypeLayoutProcessor.setOnSetBindingVariable(new OnSetBindingVariableListener() {
            @Override
            public void onSetBindingVariable(ViewDataBinding binding, String layoutName) {
//                setRootNotificationBindingVariable(binding, layoutName);
                setLocalBindingVariable(binding, layoutName);
            }
        });

        presentConditionLayoutProcessor = new PresentConditionLayoutProcessor(getContext(), getFragmentManager(), contentBinding, record.getPerson(), causeOfDeathList, deathPlaceTypeList, diseaseList);
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

        //TODO: Validation
        /*PersonValidator.setRequiredHintsForPersonData(binding);
        contentBinding.personPresentCondition.makeFieldSoftRequired();
        contentBinding.personSex.makeFieldSoftRequired();
        contentBinding.personDeathDate.makeFieldSoftRequired();
        contentBinding.personDeathPlaceDescription.makeFieldSoftRequired();
        contentBinding.personDeathPlaceType.makeFieldSoftRequired();
        contentBinding.personCauseOfDeath.makeFieldSoftRequired();
        contentBinding.personCauseOfDeathDetails.makeFieldSoftRequired();
        contentBinding.personCauseOfDeathDisease.makeFieldSoftRequired();
        contentBinding.personBurialDate.makeFieldSoftRequired();
        contentBinding.personBurialPlaceDescription.makeFieldSoftRequired();
        contentBinding.personBurialConductor.makeFieldSoftRequired();*/
    }

    @Override
    public void onAfterLayoutBinding(FragmentEventEditPersonInfoLayoutBinding contentBinding) {
        contentBinding.personOccupationType.initializeSpinner(occupationTypeList, null, new ValueChangeListener() {
            @Override
            public void onChange(ControlPropertyField field) {
                if (!occupationTypeLayoutProcessor.processLayout((OccupationType) field.getValue()))
                    return;
            }
        });

        contentBinding.personSex.initializeSpinner(genderList);

        contentBinding.personApproximateAgeType.initializeSpinner(ageTypeList, null, new ValueChangeListener() {
            @Override
            public void onChange(ControlPropertyField field) {
                updateApproximateAgeField();
            }
        });

        contentBinding.personBirthdateYYYY.initializeSpinner(yearList, null, new ValueChangeListener() {
            @Override
            public void onChange(ControlPropertyField field) {
                updateApproximateAgeField();
            }
        });

        contentBinding.personBirthdateMM.initializeSpinner(monthList, null, new ValueChangeListener() {
            @Override
            public void onChange(ControlPropertyField field) {
                updateApproximateAgeField();
            }
        });

        contentBinding.personBirthdateDD.initializeSpinner(dateList, null, new ValueChangeListener() {
            @Override
            public void onChange(ControlPropertyField field) {
                updateApproximateAgeField();
            }
        });

    }

    @Override
    public int getEditLayout() {
        return R.layout.fragment_event_edit_person_info_layout;
    }


    private void setupCallback() {

        onAddressLinkClickedCallback = new IEntryItemOnClickListener() {
            @Override
            public void onClick(View v, Object item) {
                final Location location = record.getPerson().getAddress();
                final LocationDialog locationDialog = new LocationDialog(BaseActivity.getActiveActivity(), location);

                locationDialog.setOnPositiveClickListener(new TeboAlertDialogInterface.PositiveOnClickListener() {
                    @Override
                    public void onOkClick(View v, Object item, View viewRoot) {
                        getContentBinding().personAddress.setValue(location.toString());
                        record.getPerson().setAddress(location);

                        locationDialog.dismiss();
                    }
                });

                locationDialog.show(new Callback.IAction<AlertDialog>() {
                    @Override
                    public void call(AlertDialog result) {

                    }
                });
            }
        };
    }

    private void setLocalBindingVariable(final ViewDataBinding binding, String layoutName) {
        if (!binding.setVariable(BR.data, record.getPerson())) {
            Log.e(TAG, "There is no variable 'data' in layout " + layoutName);
        }
    }

    private void updateApproximateAgeField() {
        Integer birthyear = record.getPerson().getBirthdateYYYY();
        //TeboTextRead approximateAgeTextField =  getContentBinding().txtAge;
        //TeboSpinner approximateAgeTypeField = getContentBinding().spnAgeType;

        if (birthyear != null) {
            // TODO take values from fields
            Integer birthday = record.getPerson().getBirthdateDD();
            Integer birthmonth = record.getPerson().getBirthdateMM();

            Calendar birthDate = new GregorianCalendar();
            birthDate.set(birthyear, birthmonth != null ? birthmonth - 1 : 0, birthday != null ? birthday : 1);

            Date to = new Date();
            if (record.getPerson().getDeathDate() != null) {
                to = record.getPerson().getDeathDate();
            }
            DataHelper.Pair<Integer, ApproximateAgeType> approximateAge = ApproximateAgeHelper.getApproximateAge(birthDate.getTime(), to);
            ApproximateAgeType ageType = approximateAge.getElement1();
            Integer age = approximateAge.getElement0();

            // TODO has to be set in fields
            record.getPerson().setApproximateAge(age);
            record.getPerson().setApproximateAgeType(ageType);
        } else {
            //getContentBinding().txtAge.setEnabled(true, editOrCreateUserRight);
            //getContentBinding().spnAgeType.setEnabled(true, editOrCreateUserRight);
        }
    }

    public static EventParticipantEditFragment newInstance(EventParticipantFormNavigationCapsule capsule, EventParticipant activityRootData) {
        return newInstance(EventParticipantEditFragment.class, capsule, activityRootData);
    }
}
