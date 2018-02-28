package de.symeda.sormas.app.sample.edit;

import android.databinding.ObservableArrayList;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewStub;

import de.symeda.sormas.app.BaseEditActivityFragment;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.component.Item;
import de.symeda.sormas.app.component.OnTeboSwitchCheckedChangeListener;
import de.symeda.sormas.app.component.TeboSpinner;
import de.symeda.sormas.app.component.TeboSwitch;
import de.symeda.sormas.app.core.IEntryItemOnClickListener;
import de.symeda.sormas.app.core.YesNo;
import de.symeda.sormas.app.databinding.FragmentSampleEditLayoutBinding;
import de.symeda.sormas.app.sample.SampleFormNavigationCapsule;
import de.symeda.sormas.app.sample.ShipmentStatus;
import de.symeda.sormas.app.util.DataUtils;
import de.symeda.sormas.app.util.MemoryDatabaseHelper;

import java.util.List;

import de.symeda.sormas.api.sample.SampleMaterial;
import de.symeda.sormas.api.sample.SampleTestType;
import de.symeda.sormas.app.backend.common.AbstractDomainObject;
import de.symeda.sormas.app.backend.facility.Facility;
import de.symeda.sormas.app.backend.sample.Sample;

/**
 * Created by Orson on 05/02/2018.
 * <p>
 * www.technologyboard.org
 * sampson.orson@gmail.com
 * sampson.orson@technologyboard.org
 */

public class SampleEditFragment extends BaseEditActivityFragment<FragmentSampleEditLayoutBinding> implements OnTeboSwitchCheckedChangeListener {

    private String recordUuid = null;
    private ShipmentStatus pageStatus;
    private Sample record;
    private IEntryItemOnClickListener onRecentTestItemClickListener;
    private int mLastCheckedId = -1;

    private List<SampleMaterial> sampleMaterialList;
    private List<SampleTestType> testTypeList;
    private List<Facility> labList;

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        //SaveFilterStatusState(outState, filterStatus);
        SavePageStatusState(outState, pageStatus);
        SaveRecordUuidState(outState, recordUuid);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle arguments = (savedInstanceState != null)? savedInstanceState : getArguments();

        recordUuid = getRecordUuidArg(arguments);
        //filterStatus = (EventStatus) getFilterStatusArg(arguments);
        pageStatus = (ShipmentStatus) getPageStatusArg(arguments);
    }

    @Override
    protected String getSubHeadingTitle() {
        String title = "";
        if (record != null) {
            title = record.getSampleMaterial().name();
        }

        return title;
    }

    @Override
    public AbstractDomainObject getData() {
        return record;
    }

    @Override
    public void onBeforeLayoutBinding(Bundle savedInstanceState) {
        sampleMaterialList = MemoryDatabaseHelper.SAMPLE_MATERIAL.getSampleMaterials();
        testTypeList = MemoryDatabaseHelper.TEST_TYPE.getTestTypes();
        labList = MemoryDatabaseHelper.FACILITY.getFacilities(5);

        setupCallback();
    }

    @Override
    public void onLayoutBinding(ViewStub stub, View inflated, FragmentSampleEditLayoutBinding contentBinding) {
        //binding = DataBindingUtil.inflate(inflater, getEditLayout(), container, true);
        record = MemoryDatabaseHelper.SAMPLE.getSamples(1).get(0);

        contentBinding.setData(record);
        contentBinding.setCaze(record.getAssociatedCase());
        contentBinding.setLab(record.getLab());
        contentBinding.setResults(getTestResults());
        contentBinding.setRecentTestItemClickCallback(onRecentTestItemClickListener);

        contentBinding.setYesNoClass(YesNo.class);
        contentBinding.setShippedYesCallback(this);

        //return binding;
    }

    @Override
    public void onAfterLayoutBinding(FragmentSampleEditLayoutBinding binding) {
        binding.spnTestType.initialize(new TeboSpinner.ISpinnerInitSimpleConfig() {
            @Override
            public Object getSelectedValue() {
                return null;
            }

            @Override
            public List<Item> getDataSource(Object parentValue) {
                return (testTypeList.size() > 0) ? DataUtils.toItems(testTypeList)
                        : DataUtils.toItems(testTypeList, false);
            }
        });

        binding.spnSampleMaterial.initialize(new TeboSpinner.ISpinnerInitSimpleConfig() {
            @Override
            public Object getSelectedValue() {
                return null;
            }

            @Override
            public List<Item> getDataSource(Object parentValue) {
                return (sampleMaterialList.size() > 0) ? DataUtils.toItems(sampleMaterialList)
                        : DataUtils.toItems(sampleMaterialList, false);
            }
        });

        binding.spnLaboratory.initialize(new TeboSpinner.ISpinnerInitSimpleConfig() {
            @Override
            public Object getSelectedValue() {
                return null;
            }

            @Override
            public List<Item> getDataSource(Object parentValue) {
                return (labList.size() > 0) ? DataUtils.toItems(labList)
                        : DataUtils.toItems(labList, false);
            }
        });

        binding.dtpDateAndTimeOfSampling.initialize(getFragmentManager());
        binding.dtpShipmentDate.initialize(getFragmentManager());
    }

    @Override
    public int getEditLayout() {
        return R.layout.fragment_sample_edit_layout;
    }

    private ObservableArrayList getTestResults() {
        ObservableArrayList results = new ObservableArrayList();
        results.add(MemoryDatabaseHelper.TEST.getSampleTests(1).get(0));
        return results;
    }

    private void setupCallback() {
        onRecentTestItemClickListener = new IEntryItemOnClickListener() {
            @Override
            public void onClick(View v, Object item) {

            }
        };
    }

    @Override
    public boolean includeFabNonOverlapPadding() {
        return false;
    }

    @Override
    public void onCheckedChanged(TeboSwitch teboSwitch, Object checkedItem, int checkedId) {
        if (checkedId < 0)
            return;

        if (mLastCheckedId == checkedId) {
            return;
        }

        mLastCheckedId = checkedId;

        YesNo result = ((YesNo)checkedItem);

        if (result == YesNo.YES) {
            getContentBinding().dtpShipmentDate.setVisibility(View.VISIBLE);
            getContentBinding().txtShipmentDetails.setVisibility(View.VISIBLE);
        } else {
            getContentBinding().dtpShipmentDate.setVisibility(View.GONE);
            getContentBinding().txtShipmentDetails.setVisibility(View.GONE);
        }
    }

    public static SampleEditFragment newInstance(SampleFormNavigationCapsule capsule)
            throws java.lang.InstantiationException, IllegalAccessException {
        return newInstance(SampleEditFragment.class, capsule);
    }
}
