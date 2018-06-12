package de.symeda.sormas.app.caze.edit;

import android.content.res.Resources;
import android.databinding.ViewDataBinding;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;

import com.android.databinding.library.baseAdapters.BR;

import java.util.List;
import java.util.Random;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.caze.DengueFeverType;
import de.symeda.sormas.api.caze.InvestigationStatus;
import de.symeda.sormas.api.caze.PlagueType;
import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.app.BaseEditActivityFragment;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.caze.Case;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.config.ConfigProvider;
import de.symeda.sormas.app.backend.facility.Facility;
import de.symeda.sormas.app.backend.region.Community;
import de.symeda.sormas.app.backend.region.District;
import de.symeda.sormas.app.backend.region.Region;
import de.symeda.sormas.app.backend.user.User;
import de.symeda.sormas.app.component.Item;
import de.symeda.sormas.app.component.TeboSpinner;
import de.symeda.sormas.app.component.VisualState;
import de.symeda.sormas.app.component.dialog.CommunityLoader;
import de.symeda.sormas.app.component.dialog.DistrictLoader;
import de.symeda.sormas.app.component.dialog.FacilityLoader;
import de.symeda.sormas.app.component.dialog.RegionLoader;
import de.symeda.sormas.app.core.BoolResult;
import de.symeda.sormas.app.core.IActivityCommunicator;
import de.symeda.sormas.app.core.OnSetBindingVariableListener;
import de.symeda.sormas.app.core.async.IJobDefinition;
import de.symeda.sormas.app.core.async.ITaskExecutor;
import de.symeda.sormas.app.core.async.ITaskResultCallback;
import de.symeda.sormas.app.core.async.ITaskResultHolderIterator;
import de.symeda.sormas.app.core.async.TaskExecutorFor;
import de.symeda.sormas.app.core.async.TaskResultHolder;
import de.symeda.sormas.app.databinding.FragmentCaseNewLayoutBinding;
import de.symeda.sormas.app.shared.CaseFormNavigationCapsule;
import de.symeda.sormas.app.util.DataUtils;


/**
 * Created by Orson on 15/02/2018.
 * <p>
 * www.technologyboard.org
 * sampson.orson@gmail.com
 * sampson.orson@technologyboard.org
 */

public class CaseNewFragment extends BaseEditActivityFragment<FragmentCaseNewLayoutBinding, Case, Case> {

    public static final String TAG = CaseNewFragment.class.getSimpleName();

    private AsyncTask onResumeTask;
    private String recordUuid = null;
    private String personUuid = null;
    private String contactUuid = null;
    private InvestigationStatus pageStatus = null;
    private Case record;
    //private Person person;

    private List<Item> diseaseList;
    private List<Item> plagueTypeList;
    private List<Item> dengueFeverTypeList;

    private HealthFacilityLayoutProcessor healthFacilityLayoutProcessor;

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        savePageStatusState(outState, pageStatus);
        saveRecordUuidState(outState, recordUuid);
        savePersonUuidState(outState, personUuid);
        saveContactUuidState(outState, contactUuid);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle arguments = (savedInstanceState != null)? savedInstanceState : getArguments();

        recordUuid = getRecordUuidArg(arguments);
        pageStatus = (InvestigationStatus) getPageStatusArg(arguments);
        personUuid = getPersonUuidArg(arguments);
        contactUuid = getContactUuidArg(arguments);
    }

    @Override
    protected String getSubHeadingTitle() {
        Resources r = getResources();
        return r.getString(R.string.caption_new_case);
    }

    @Override
    public Case getPrimaryData() {
        return record;
    }

    @Override
    public boolean onBeforeLayoutBinding(Bundle savedInstanceState, TaskResultHolder resultHolder, BoolResult resultStatus, boolean executionComplete) {
        if (!executionComplete) {
            Case caze = getActivityRootData();

            if (caze != null) {
                if (caze.getPerson() == null) {
                    caze.setPerson(DatabaseHelper.getPersonDao().build());
                } /*else {
                    caze.setPerson(DatabaseHelper.getPersonDao().queryUuid(caze.getPerson().getUuid()));
                }*/
            }


            resultHolder.forItem().add(caze);
            //resultHolder.forItem().add(caze.getPerson());

            resultHolder.forOther().add(DataUtils.getEnumItems(Disease.class, false));
            resultHolder.forOther().add(DataUtils.getEnumItems(PlagueType.class, false));
            resultHolder.forOther().add(DataUtils.getEnumItems(DengueFeverType.class, false));
        } else {
            ITaskResultHolderIterator itemIterator = resultHolder.forItem().iterator();
            ITaskResultHolderIterator otherIterator = resultHolder.forOther().iterator();

            if (itemIterator.hasNext())
                record = itemIterator.next();

            /*if (itemIterator.hasNext())
                person = itemIterator.next();*/

            /*if (itemIterator.hasNext())
                record = itemIterator.next();*/

            if (otherIterator.hasNext())
                diseaseList =  otherIterator.next();

            if (otherIterator.hasNext())
                plagueTypeList =  otherIterator.next();

            if (otherIterator.hasNext())
                dengueFeverTypeList = otherIterator.next();

            setupCallback();
        }

        return true;
    }

    @Override
    public void onLayoutBinding(FragmentCaseNewLayoutBinding contentBinding) {
        //binding = DataBindingUtil.inflate(inflater, getEditLayout(), container, true);

        healthFacilityLayoutProcessor = new HealthFacilityLayoutProcessor(getContext(), contentBinding, record);
        healthFacilityLayoutProcessor.setOnSetBindingVariable(new OnSetBindingVariableListener() {
            @Override
            public void onSetBindingVariable(ViewDataBinding binding, String layoutName) {
                setRootNotificationBindingVariable(binding, layoutName);
                setLocalBindingVariable(binding, layoutName);
            }
        });

        healthFacilityLayoutProcessor.setHealthFacilityDetailFieldChecker(new IHealthFacilityDetailFieldChecker() {
            @Override
            public boolean hasDetailField(Facility facility) {
                //TODO: Orson Replace
                return new Random().nextBoolean();
            }
        });

        Disease disease = record.getDisease();

        contentBinding.setData(record);
        //contentBinding.setCheckedCallback(onEventTypeCheckedCallback);
    }

    @Override
    public void onAfterLayoutBinding(FragmentCaseNewLayoutBinding contentBinding) {
        contentBinding.spnDisease.initialize(new TeboSpinner.ISpinnerInitConfig() {
            @Override
            public Object getSelectedValue() {
                return null;
            }

            @Override
            public List<Item> getDataSource(Object parentValue) {
                return (diseaseList.size() > 0) ? DataUtils.addEmptyItem(diseaseList)
                        : diseaseList;
            }

            @Override
            public VisualState getInitVisualState() {
                if ((recordUuid != null && !recordUuid.isEmpty()) || (contactUuid != null && !contactUuid.isEmpty()) || (personUuid != null && !personUuid.isEmpty())) {
                    return VisualState.DISABLED;
                }

                return VisualState.NORMAL;
            }

            @Override
            public void onItemSelected(TeboSpinner view, Object value, int position, long id) {
                if (value == null)
                    return;

                Disease disease = (Disease)value;

                if (disease == Disease.OTHER) {
                    getContentBinding().txtDiseaseDetail.setVisibility(View.VISIBLE);
                    getContentBinding().spnPlague.setVisibility(View.GONE);
                    getContentBinding().spnDengueFever.setVisibility(View.GONE);
                } else if (disease == Disease.PLAGUE) {
                    getContentBinding().txtDiseaseDetail.setVisibility(View.GONE);
                    getContentBinding().spnPlague.setVisibility(View.VISIBLE);
                    getContentBinding().spnDengueFever.setVisibility(View.GONE);
                } else if (disease == Disease.DENGUE) {
                    getContentBinding().txtDiseaseDetail.setVisibility(View.GONE);
                    getContentBinding().spnPlague.setVisibility(View.GONE);
                    getContentBinding().spnDengueFever.setVisibility(View.VISIBLE);
                } else {
                    getContentBinding().txtDiseaseDetail.setVisibility(View.GONE);
                    getContentBinding().spnPlague.setVisibility(View.GONE);
                    getContentBinding().spnDengueFever.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        contentBinding.spnPlague.initialize(new TeboSpinner.ISpinnerInitSimpleConfig() {
            @Override
            public Object getSelectedValue() {
                return null;
            }

            @Override
            public List<Item> getDataSource(Object parentValue) {
                return (plagueTypeList.size() > 0) ? DataUtils.addEmptyItem(plagueTypeList) : plagueTypeList;
            }

            @Override
            public VisualState getInitVisualState() {
                return null;
            }
        });

        contentBinding.spnDengueFever.initialize(new TeboSpinner.ISpinnerInitSimpleConfig() {
            @Override
            public Object getSelectedValue() {
                return null;
            }

            @Override
            public List<Item> getDataSource(Object parentValue) {
                return (dengueFeverTypeList.size() > 0) ? DataUtils.addEmptyItem(dengueFeverTypeList) : dengueFeverTypeList;
            }

            @Override
            public VisualState getInitVisualState() {
                return null;
            }
        });

        contentBinding.spnState.initialize(new TeboSpinner.ISpinnerInitSimpleConfig() {
            @Override
            public Object getSelectedValue() {
                return null;
            }

            @Override
            public List<Item> getDataSource(Object parentValue) {
                List<Item> regions = RegionLoader.getInstance().load();
                return (regions.size() > 0) ? DataUtils.addEmptyItem(regions) : regions;
            }

            @Override
            public VisualState getInitVisualState() {
                return VisualState.DISABLED;
            }
        });

        contentBinding.spnLga.initialize(contentBinding.spnState, new TeboSpinner.ISpinnerInitSimpleConfig() {
            @Override
            public Object getSelectedValue() {
                return null;
            }

            @Override
            public List<Item> getDataSource(Object parentValue) {
                List<Item> districts = DistrictLoader.getInstance().load((Region)parentValue);
                return (districts.size() > 0) ? DataUtils.addEmptyItem(districts) : districts;
            }

            @Override
            public VisualState getInitVisualState() {
                return VisualState.DISABLED;
            }
        });

        contentBinding.spnWard.initialize(contentBinding.spnLga, new TeboSpinner.ISpinnerInitSimpleConfig() {
            @Override
            public Object getSelectedValue() {
                return null;
            }

            @Override
            public List<Item> getDataSource(Object parentValue) {
                List<Item> communities = CommunityLoader.getInstance().load((District)parentValue);
                return (communities.size() > 0) ? DataUtils.addEmptyItem(communities) : communities;
            }

            @Override
            public VisualState getInitVisualState() {
                return null;
            }
        });

        contentBinding.spnFacility.initialize(contentBinding.spnWard, new TeboSpinner.ISpinnerInitConfig() {
            @Override
            public Object getSelectedValue() {
                return null;
            }

            @Override
            public List<Item> getDataSource(Object parentValue) {
                List<Item> facilities;
                if(parentValue != null) {
                    facilities = FacilityLoader.getInstance().load((Community)parentValue, true);
                } else {
                    facilities = FacilityLoader.getInstance().load((District) record.getDistrict(), true);
                }

                return (facilities.size() > 0) ? DataUtils.addEmptyItem(facilities) : facilities;
            }

            @Override
            public VisualState getInitVisualState() {
                return null;
            }

            @Override
            public void onItemSelected(TeboSpinner view, Object value, int position, long id) {
                if (value == null)
                    return;

                if (!healthFacilityLayoutProcessor.processLayout((Facility) value))
                    return;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //&& record.getDisease() != null
        //&& recordUuid != null && !recordUuid.isEmpty()
        if ((recordUuid != null && !recordUuid.isEmpty()) || (contactUuid != null && !contactUuid.isEmpty()) || (personUuid != null && !personUuid.isEmpty())) {
            contentBinding.txtFirstName.changeVisualState(VisualState.DISABLED);
            contentBinding.txtLastName.changeVisualState(VisualState.DISABLED);
            //contentBinding.spnDisease.changeVisualState(VisualState.DISABLED);
            contentBinding.txtDiseaseDetail.changeVisualState(VisualState.DISABLED);

            //record.setPerson(person);
        }

        //contentBinding.spnState.changeVisualState(VisualState.DISABLED);
        //contentBinding.spnLga.changeVisualState(VisualState.DISABLED);

        User user = ConfigProvider.getUser();
        if (user.hasUserRole(UserRole.INFORMANT)&& user.getHealthFacility() != null) {
            // this is ok, because informants are required to have a community and health facility
            contentBinding.spnWard.changeVisualState(VisualState.DISABLED);
            contentBinding.spnFacility.changeVisualState(VisualState.DISABLED);
        } else {
            contentBinding.spnWard.changeVisualState(VisualState.NORMAL, editOrCreateUserRight);
            contentBinding.spnFacility.changeVisualState(VisualState.NORMAL, editOrCreateUserRight);
        }
    }

    @Override
    protected void updateUI(FragmentCaseNewLayoutBinding contentBinding, Case aCase) {

    }

    @Override
    public void onPageResume(FragmentCaseNewLayoutBinding contentBinding, boolean hasBeforeLayoutBindingAsyncReturn) {
        if (!hasBeforeLayoutBindingAsyncReturn)
            return;

        try {
            ITaskExecutor executor = TaskExecutorFor.job(new IJobDefinition() {
                @Override
                public void preExecute(BoolResult resultStatus, TaskResultHolder resultHolder) {
                    //getActivityCommunicator().showPreloader();
                    //getActivityCommunicator().hideFragmentView();
                }

                @Override
                public void execute(BoolResult resultStatus, TaskResultHolder resultHolder) {
                    Case caze = getActivityRootData();

                    if (caze != null && caze.getPerson() == null) {
                        caze.setPerson(DatabaseHelper.getPersonDao().build());
                    }


                    resultHolder.forItem().add(caze);
                }
            });
            onResumeTask = executor.execute(new ITaskResultCallback() {
                @Override
                public void taskResult(BoolResult resultStatus, TaskResultHolder resultHolder) {
                    //getActivityCommunicator().hidePreloader();
                    //getActivityCommunicator().showFragmentView();

                    if (resultHolder == null){
                        return;
                    }

                    ITaskResultHolderIterator itemIterator = resultHolder.forItem().iterator();

                    if (itemIterator.hasNext())
                        record =  itemIterator.next();

                    if (record != null)
                        requestLayoutRebind();
                    else {
                        getActivity().finish();
                    }
                }
            });
        } catch (Exception ex) {
            //getActivityCommunicator().hidePreloader();
            //getActivityCommunicator().showFragmentView();
        }

    }

    @Override
    public int getEditLayout() {
        return R.layout.fragment_case_new_layout;
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

    private void setupCallback() {
        /*onEventTypeCheckedCallback = new OnTeboSwitchCheckedChangeListener() {
            @Override
            public void onCheckedChanged(TeboSwitch teboSwitch, Object checkedItem, int checkedId) {
                if (mLastCheckedId == checkedId) {
                    return;
                }

                mLastCheckedId = checkedId;

            }
        };*/
    }

    private void setLocalBindingVariable(final ViewDataBinding binding, String layoutName) {
        if (!binding.setVariable(BR.data, record)) {
            Log.e(TAG, "There is no variable 'data' in layout " + layoutName);
        }
    }


    public static CaseNewFragment newInstance(IActivityCommunicator activityCommunicator, CaseFormNavigationCapsule capsule, Case activityRootData) {
        return newInstance(activityCommunicator, CaseNewFragment.class, capsule, activityRootData);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (onResumeTask != null && !onResumeTask.isCancelled())
            onResumeTask.cancel(true);
    }
}
