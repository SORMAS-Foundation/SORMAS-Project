package de.symeda.sormas.app.sample.edit;

import android.os.Bundle;
import android.view.View;

import java.util.List;

import de.symeda.sormas.api.facility.FacilityDto;
import de.symeda.sormas.api.sample.SampleMaterial;
import de.symeda.sormas.api.sample.SampleSource;
import de.symeda.sormas.api.sample.SampleTestType;
import de.symeda.sormas.app.BaseEditFragment;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.facility.Facility;
import de.symeda.sormas.app.backend.sample.Sample;
import de.symeda.sormas.app.component.Item;
import de.symeda.sormas.app.component.controls.ControlPropertyField;
import de.symeda.sormas.app.component.controls.ValueChangeListener;
import de.symeda.sormas.app.core.YesNo;
import de.symeda.sormas.app.databinding.FragmentSampleNewLayoutBinding;
import de.symeda.sormas.app.shared.SampleFormNavigationCapsule;
import de.symeda.sormas.app.util.DataUtils;

public class SampleNewFragment extends BaseEditFragment<FragmentSampleNewLayoutBinding, Sample, Sample> {

    private Sample record;

    private List<Item> sampleMaterialList;
    private List<Item> testTypeList;
    private List<Item> sampleSourceList;
    private List<Facility> labList;

    @Override
    protected String getSubHeadingTitle() {
        return getResources().getString(R.string.caption_new_sample);
    }

    @Override
    public Sample getPrimaryData() {
        return record;
    }

    @Override
    protected void prepareFragmentData(Bundle savedInstanceState) {
        record = getActivityRootData();
        labList = DatabaseHelper.getFacilityDao().getLaboratories(true);
        sampleMaterialList = DataUtils.getEnumItems(SampleMaterial.class, false);
        testTypeList = DataUtils.getEnumItems(SampleTestType.class, false);
        sampleSourceList = DataUtils.getEnumItems(SampleSource.class, false);
    }

    @Override
    public void onLayoutBinding(FragmentSampleNewLayoutBinding contentBinding) {

        //TODO: Set required hints for sample data
        //SampleValidator.setRequiredHintsForSampleData(contentBinding);``

//        contentBinding.setShippedYesCallback(this);
        contentBinding.setYesNoClass(YesNo.class);
        contentBinding.setData(record);
        contentBinding.setCaze(record.getAssociatedCase());
        contentBinding.setLab(record.getLab());

    }

    @Override
    public void onAfterLayoutBinding(final FragmentSampleNewLayoutBinding contentBinding) {
        contentBinding.sampleSuggestedTypeOfTest.initializeSpinner(testTypeList);

        contentBinding.sampleSampleSource.initializeSpinner(sampleSourceList);

        contentBinding.sampleSampleMaterial.initializeSpinner(sampleMaterialList, null, new ValueChangeListener() {
            @Override
            public void onChange(ControlPropertyField field) {
                SampleMaterial material = (SampleMaterial) field.getValue();

                if (material == SampleMaterial.OTHER) {
                    contentBinding.sampleSampleMaterialText.setVisibility(View.VISIBLE);
                } else {
                    contentBinding.sampleSampleMaterialText.setVisibility(View.INVISIBLE);
                    contentBinding.sampleSampleMaterialText.setValue("");
                }
            }
        });

        contentBinding.sampleLab.initializeSpinner(DataUtils.toItems(labList), null, new ValueChangeListener() {
            @Override
            public void onChange(ControlPropertyField field) {
                Facility laboratory = (Facility) field.getValue();

                if (laboratory != null && laboratory.getUuid().equals(FacilityDto.OTHER_LABORATORY_UUID)) {
                    contentBinding.sampleLabDetails.setVisibility(View.VISIBLE);
                } else {
                    contentBinding.sampleLabDetails.setVisibility(View.GONE);
                    contentBinding.sampleLabDetails.setValue("");
                }
            }
        });

        contentBinding.sampleSampleDateTime.initializeDateTimeField(getFragmentManager());
        contentBinding.sampleShipmentDate.initializeDateField(getFragmentManager());

    }

    @Override
    public int getEditLayout() {
        return R.layout.fragment_sample_new_layout;
    }

    public static SampleNewFragment newInstance(SampleFormNavigationCapsule capsule, Sample activityRootData) {
        return newInstance(SampleNewFragment.class, capsule, activityRootData);
    }
}
