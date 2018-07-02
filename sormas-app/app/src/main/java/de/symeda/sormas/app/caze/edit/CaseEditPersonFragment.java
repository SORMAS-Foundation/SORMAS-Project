package de.symeda.sormas.app.caze.edit;

import android.content.res.Resources;
import android.databinding.ViewDataBinding;
import android.os.AsyncTask;
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
import de.symeda.sormas.app.BaseEditActivityFragment;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.caze.Case;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.location.Location;
import de.symeda.sormas.app.backend.person.Person;
import de.symeda.sormas.app.component.Item;
import de.symeda.sormas.app.component.VisualState;
import de.symeda.sormas.app.component.controls.ControlDateField;
import de.symeda.sormas.app.component.controls.TeboSpinner;
import de.symeda.sormas.app.component.controls.ControlSwitchField;
import de.symeda.sormas.app.component.dialog.LocationDialog;
import de.symeda.sormas.app.component.dialog.TeboAlertDialogInterface;
import de.symeda.sormas.app.core.BoolResult;
import de.symeda.sormas.app.core.IEntryItemOnClickListener;
import de.symeda.sormas.app.core.OnSetBindingVariableListener;
import de.symeda.sormas.app.core.async.DefaultAsyncTask;
import de.symeda.sormas.app.core.async.ITaskResultCallback;
import de.symeda.sormas.app.core.async.ITaskResultHolderIterator;
import de.symeda.sormas.app.core.async.TaskResultHolder;
import de.symeda.sormas.app.databinding.FragmentCaseEditPatientLayoutBinding;
import de.symeda.sormas.app.shared.CaseFormNavigationCapsule;
import de.symeda.sormas.app.shared.OnDateOfDeathChangeListener;
import de.symeda.sormas.app.util.DataUtils;
import de.symeda.sormas.app.util.layoutprocessor.OccupationTypeLayoutProcessor;
import de.symeda.sormas.app.util.layoutprocessor.PresentConditionLayoutProcessor;

public class CaseEditPersonFragment extends BaseEditActivityFragment<FragmentCaseEditPatientLayoutBinding, Person, Case> {

    public static final String TAG = CaseEditPersonFragment.class.getSimpleName();

    private static final int DEFAULT_YEAR = 2000;

    private AsyncTask onResumeTask;
    private Person record;
    private IEntryItemOnClickListener onAddressLinkClickedCallback;

    private List<Item> dateList;
    private List<Item> monthList;
    private List<Item> yearList;
    private List<Item> ageTypeList;
    private List<Item> genderList;
    private List<Item> occupationTypeList;

    private List<Item> causeOfDeathList;
    private List<Item> deathPlaceTypeList;
    private List<Item> diseaseList;
    private List<Item> burialConductorList;

    private int mLastCheckedId = -1;

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
    public boolean onBeforeLayoutBinding(Bundle savedInstanceState, TaskResultHolder resultHolder, BoolResult resultStatus, boolean executionComplete) {
        if (!executionComplete) {
            Case caze = getActivityRootData();

            if (caze != null) {
                if (caze.isUnreadOrChildUnread())
                    DatabaseHelper.getCaseDao().markAsRead(caze);

                if (caze.getPerson() == null) {
                    caze.setPerson(DatabaseHelper.getPersonDao().build());
                } else {
                    caze.setPerson(DatabaseHelper.getPersonDao().queryUuid(caze.getPerson().getUuid()));
                }
            }

            resultHolder.forItem().add(caze.getPerson());
            //resultHolder.forItem().add(caze);

            resultHolder.forOther().add(DataUtils.getEnumItems(OccupationType.class, false));
            resultHolder.forOther().add(DataUtils.getEnumItems(Sex.class, false));
            resultHolder.forOther().add(DataUtils.getEnumItems(ApproximateAgeType.class, false));
            resultHolder.forOther().add(DataUtils.toItems(DateHelper.getDaysInMonth(), true));
            resultHolder.forOther().add(DataUtils.getMonthItems(true));
            resultHolder.forOther().add(DataUtils.toItems(DateHelper.getYearsToNow(), true));
            resultHolder.forOther().add(DataUtils.getEnumItems(CauseOfDeath.class, false));
            resultHolder.forOther().add(DataUtils.getEnumItems(DeathPlaceType.class, false));
            resultHolder.forOther().add(DataUtils.getEnumItems(Disease.class, false));
            resultHolder.forOther().add(DataUtils.getEnumItems(BurialConductor.class, false));

        } else {
            ITaskResultHolderIterator itemIterator = resultHolder.forItem().iterator();
            ITaskResultHolderIterator otherIterator = resultHolder.forOther().iterator();

            if (itemIterator.hasNext())
                record = itemIterator.next();

            if (record == null)
                getActivity().finish();

            /*if (itemIterator.hasNext())
                caze = itemIterator.next();*/

            if (otherIterator.hasNext())
                occupationTypeList = otherIterator.next();

            if (otherIterator.hasNext())
                genderList = otherIterator.next();

            if (otherIterator.hasNext())
                ageTypeList = otherIterator.next();

            if (otherIterator.hasNext())
                dateList = otherIterator.next();

            if (otherIterator.hasNext())
                monthList = otherIterator.next();

            if (otherIterator.hasNext())
                yearList = otherIterator.next();

            if (otherIterator.hasNext())
                causeOfDeathList = otherIterator.next();

            if (otherIterator.hasNext())
                deathPlaceTypeList = otherIterator.next();

            if (otherIterator.hasNext())
                diseaseList = otherIterator.next();

            if (otherIterator.hasNext())
                burialConductorList = otherIterator.next();

            setupCallback();
        }

        return true;
    }

    @Override
    public void onLayoutBinding(FragmentCaseEditPatientLayoutBinding contentBinding) {
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
        contentBinding.spnOccupationType.initialize(new TeboSpinner.ISpinnerInitConfig() {
            @Override
            public Object getSelectedValue() {
                return null;
            }

            @Override
            public List<Item> getDataSource(Object parentValue) {
                return (occupationTypeList.size() > 0) ? DataUtils.addEmptyItem(occupationTypeList)
                        : occupationTypeList;
            }

            @Override
            public VisualState getInitVisualState() {
                return null;
            }

            @Override
            public void onItemSelected(TeboSpinner view, Object value, int position, long id) {
                if (!occupationTypeLayoutProcessor.processLayout((OccupationType) value))
                    return;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        contentBinding.spnGender.initialize(new TeboSpinner.ISpinnerInitSimpleConfig() {
            @Override
            public Object getSelectedValue() {
                return null;
            }

            @Override
            public List<Item> getDataSource(Object parentValue) {
                return (genderList.size() > 0) ? DataUtils.addEmptyItem(genderList)
                        : genderList;
            }

            @Override
            public VisualState getInitVisualState() {
                return null;
            }
        });

        contentBinding.spnAgeType.initialize(new TeboSpinner.ISpinnerInitConfig() {
            @Override
            public Object getSelectedValue() {
                return null;
            }

            @Override
            public List<Item> getDataSource(Object parentValue) {
                return (ageTypeList.size() > 0) ? DataUtils.addEmptyItem(ageTypeList)
                        : ageTypeList;
            }

            @Override
            public VisualState getInitVisualState() {
                return null;
            }

            @Override
            public void onItemSelected(TeboSpinner view, Object value, int position, long id) {
                updateApproximateAgeField();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        contentBinding.spnYear.initialize(new TeboSpinner.ISpinnerInitConfig() {
            @Override
            public Object getSelectedValue() {
                return DEFAULT_YEAR;
            }

            @Override
            public List<Item> getDataSource(Object parentValue) {
                return yearList;
            }

            @Override
            public VisualState getInitVisualState() {
                return null;
            }

            @Override
            public void onItemSelected(TeboSpinner view, Object value, int position, long id) {
                updateApproximateAgeField();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        contentBinding.spnMonth.initialize(new TeboSpinner.ISpinnerInitConfig() {
            @Override
            public Object getSelectedValue() {
                return null;
            }

            @Override
            public List<Item> getDataSource(Object parentValue) {
                return monthList;
            }

            @Override
            public VisualState getInitVisualState() {
                return null;
            }

            @Override
            public void onItemSelected(TeboSpinner view, Object value, int position, long id) {
                updateApproximateAgeField();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        contentBinding.spnDate.initialize(new TeboSpinner.ISpinnerInitConfig() {
            @Override
            public Object getSelectedValue() {
                return null;
            }

            @Override
            public List<Item> getDataSource(Object parentValue) {
                return dateList;
            }

            @Override
            public VisualState getInitVisualState() {
                return null;
            }

            @Override
            public void onItemSelected(TeboSpinner view, Object value, int position, long id) {
                updateApproximateAgeField();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    @Override
    protected void updateUI(FragmentCaseEditPatientLayoutBinding contentBinding, Person person) {
        contentBinding.spnOccupationType.setValue(person.getOccupationType(), true);
        contentBinding.spnGender.setValue(person.getSex(), true);
        contentBinding.spnAgeType.setValue(person.getApproximateAgeType(), true);
        contentBinding.spnYear.setValue(person.getBirthdateYYYY(), true);
        contentBinding.spnMonth.setValue(person.getBirthdateMM(), true);
        contentBinding.spnDate.setValue(person.getBirthdateDD(), true);
    }

    @Override
    public void onPageResume(FragmentCaseEditPatientLayoutBinding contentBinding, boolean hasBeforeLayoutBindingAsyncReturn) {
        if (!hasBeforeLayoutBindingAsyncReturn)
            return;

        DefaultAsyncTask executor = new DefaultAsyncTask(getContext()) {
            @Override
            public void onPreExecute() {
                //getBaseActivity().showPreloader();
                //
            }

            @Override
            public void doInBackground(TaskResultHolder resultHolder) {

                Case caze = getActivityRootData();

                if (caze != null) {
                    if (caze.isUnreadOrChildUnread())
                        DatabaseHelper.getCaseDao().markAsRead(caze);

                    if (caze.getPerson() == null) {
                        caze.setPerson(DatabaseHelper.getPersonDao().build());
                    } else {
                        caze.setPerson(DatabaseHelper.getPersonDao().queryUuid(caze.getPerson().getUuid()));
                    }
                }

                resultHolder.forItem().add(caze.getPerson());
                //resultHolder.forItem().add(caze);
            }
        };
        onResumeTask = executor.execute(new ITaskResultCallback() {
            @Override
            public void taskResult(BoolResult resultStatus, TaskResultHolder resultHolder) {
                //getBaseActivity().hidePreloader();
                //getBaseActivity().showFragmentView();

                if (resultHolder == null) {
                    return;
                }

                ITaskResultHolderIterator itemIterator = resultHolder.forItem().iterator();

                if (itemIterator.hasNext())
                    record = itemIterator.next();

                    /*if (itemIterator.hasNext())
                        caze = itemIterator.next();*/

                if (record != null)
                    requestLayoutRebind();
                else {
                    getActivity().finish();
                }
            }
        });
    }

    @Override
    public int getEditLayout() {
        return R.layout.fragment_case_edit_patient_layout;
    }

    @Override
    public boolean includeFabNonOverlapPadding() {
        return false;
    }

    @Override
    public boolean showSaveAction() {
        return true;
    }

    @Override
    public boolean showAddAction() {
        return false;
    }


    // <editor-fold defaultstate="collapsed" desc="Private Methods">

    private void setupCallback() {
//        onPresentConditionCheckedCallback = new OnTeboSwitchCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(ControlSwitchField teboSwitch, Object checkedItem, int checkedId) {
//                if (checkedId < 0)
//                    return;
//
//                if (mLastCheckedId == checkedId) {
//                    return;
//                }
//
//                mLastCheckedId = checkedId;
//
//                if (!presentConditionLayoutProcessor.processLayout((PresentCondition)checkedItem))
//                    return;
//            }
//        };

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

    // </editor-fold>

    public static CaseEditPersonFragment newInstance(CaseFormNavigationCapsule capsule, Case activityRootData) {
        return newInstance(CaseEditPersonFragment.class, capsule, activityRootData);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (onResumeTask != null && !onResumeTask.isCancelled())
            onResumeTask.cancel(true);
    }
}
