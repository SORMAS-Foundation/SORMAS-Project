package de.symeda.sormas.app.caze.edit;

import android.databinding.ViewDataBinding;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.AdapterView;

import java.util.List;
import java.util.Random;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.caze.InvestigationStatus;
import de.symeda.sormas.app.BR;
import de.symeda.sormas.app.BaseEditActivityFragment;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.caze.Case;
import de.symeda.sormas.app.backend.facility.Facility;
import de.symeda.sormas.app.backend.region.Community;
import de.symeda.sormas.app.backend.region.District;
import de.symeda.sormas.app.backend.region.Region;
import de.symeda.sormas.app.caze.CaseFormNavigationCapsule;
import de.symeda.sormas.app.component.Item;
import de.symeda.sormas.app.component.TeboSpinner;
import de.symeda.sormas.app.core.BoolResult;
import de.symeda.sormas.app.core.IActivityCommunicator;
import de.symeda.sormas.app.core.async.TaskResultHolder;
import de.symeda.sormas.app.databinding.FragmentCaseNewLayoutBinding;
import de.symeda.sormas.app.event.edit.OnSetBindingVariableListener;
import de.symeda.sormas.app.util.DataUtils;
import de.symeda.sormas.app.util.MemoryDatabaseHelper;

/**
 * Created by Orson on 15/02/2018.
 * <p>
 * www.technologyboard.org
 * sampson.orson@gmail.com
 * sampson.orson@technologyboard.org
 */

public class CaseNewFragment extends BaseEditActivityFragment<FragmentCaseNewLayoutBinding, Case> {

    public static final String TAG = CaseNewFragment.class.getSimpleName();

    private String recordUuid = null;
    private InvestigationStatus pageStatus = null;
    private Case record;

    private List<Disease> diseaseList;
    private List<Region> stateList;
    private List<District> lgaList;
    private List<Community> wardList;
    private List<Facility> healthFacilityList;

    private HealthFacilityLayoutProcessor healthFacilityLayoutProcessor;

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        SavePageStatusState(outState, pageStatus);
        SaveRecordUuidState(outState, recordUuid);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle arguments = (savedInstanceState != null)? savedInstanceState : getArguments();

        recordUuid = getRecordUuidArg(arguments);
        pageStatus = (InvestigationStatus) getPageStatusArg(arguments);
    }

    @Override
    protected String getSubHeadingTitle() {
        return getResources().getString(R.string.heading_level4_case_new);
    }

    @Override
    public Case getPrimaryData() {
        return record;
    }

    @Override
    public boolean onBeforeLayoutBinding(Bundle savedInstanceState, TaskResultHolder resultHolder, BoolResult resultStatus, boolean executionComplete) {
        record = MemoryDatabaseHelper.CASE.getCases(1).get(0);
        diseaseList = MemoryDatabaseHelper.DISEASE.getDiseases(5);
        stateList = MemoryDatabaseHelper.REGION.getRegions(5);
        lgaList = MemoryDatabaseHelper.DISTRICT.getDistricts(5);
        wardList = MemoryDatabaseHelper.COMMUNITY.getCommunities(5);
        healthFacilityList = MemoryDatabaseHelper.FACILITY.getFacilities(5);

        setupCallback();

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

        contentBinding.setData(record);
        //contentBinding.setCheckedCallback(onEventTypeCheckedCallback);
    }

    @Override
    public void onAfterLayoutBinding(FragmentCaseNewLayoutBinding contentBinding) {
        contentBinding.spnDisease.initialize(new TeboSpinner.ISpinnerInitSimpleConfig() {
            @Override
            public Object getSelectedValue() {
                return null;
            }

            @Override
            public List<Item> getDataSource(Object parentValue) {
                return (diseaseList.size() > 0) ? DataUtils.toItems(diseaseList)
                        : DataUtils.toItems(diseaseList, false);
            }
        });

        contentBinding.spnState.initialize(new TeboSpinner.ISpinnerInitSimpleConfig() {
            @Override
            public Object getSelectedValue() {
                return null;
            }

            @Override
            public List<Item> getDataSource(Object parentValue) {
                return (stateList.size() > 0) ? DataUtils.toItems(stateList)
                        : DataUtils.toItems(stateList, false);
            }
        });

        contentBinding.spnLga.initialize(contentBinding.spnState, new TeboSpinner.ISpinnerInitSimpleConfig() {
            @Override
            public Object getSelectedValue() {
                return null;
            }

            @Override
            public List<Item> getDataSource(Object parentValue) {
                return (lgaList.size() > 0) ? DataUtils.toItems(lgaList)
                        : DataUtils.toItems(lgaList, false);
            }
        });

        contentBinding.spnWard.initialize(contentBinding.spnLga, new TeboSpinner.ISpinnerInitSimpleConfig() {
            @Override
            public Object getSelectedValue() {
                return null;
            }

            @Override
            public List<Item> getDataSource(Object parentValue) {
                return (wardList.size() > 0) ? DataUtils.toItems(wardList)
                        : DataUtils.toItems(wardList, false);
            }
        });

        contentBinding.spnFacility.initialize(contentBinding.spnWard, new TeboSpinner.ISpinnerInitConfig() {
            @Override
            public Object getSelectedValue() {
                return null;
            }

            @Override
            public List<Item> getDataSource(Object parentValue) {
                return (healthFacilityList.size() > 0) ? DataUtils.toItems(healthFacilityList)
                        : DataUtils.toItems(healthFacilityList, false);
            }

            @Override
            public void onItemSelected(TeboSpinner view, Object value, int position, long id) {
                if (!healthFacilityLayoutProcessor.processLayout((Facility) value))
                    return;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    @Override
    public int getEditLayout() {
        return R.layout.fragment_case_new_layout;
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
            Log.w(TAG, "There is no variable 'data' in layout " + layoutName);
        }
    }


    public static CaseNewFragment newInstance(IActivityCommunicator activityCommunicator, CaseFormNavigationCapsule capsule)
            throws java.lang.InstantiationException, IllegalAccessException {
        return newInstance(activityCommunicator, CaseNewFragment.class, capsule);
    }
}
