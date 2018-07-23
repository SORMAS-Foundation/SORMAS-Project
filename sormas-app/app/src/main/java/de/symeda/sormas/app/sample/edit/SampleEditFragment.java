package de.symeda.sormas.app.sample.edit;

import android.os.Bundle;
import android.view.View;

import java.util.List;

import de.symeda.sormas.api.facility.FacilityDto;
import de.symeda.sormas.api.sample.SampleMaterial;
import de.symeda.sormas.api.sample.SampleSource;
import de.symeda.sormas.api.sample.SampleTestType;
import de.symeda.sormas.api.sample.SpecimenCondition;
import de.symeda.sormas.app.BaseEditFragment;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.config.ConfigProvider;
import de.symeda.sormas.app.backend.facility.Facility;
import de.symeda.sormas.app.backend.sample.Sample;
import de.symeda.sormas.app.backend.sample.SampleTest;
import de.symeda.sormas.app.component.Item;
import de.symeda.sormas.app.component.controls.ControlPropertyField;
import de.symeda.sormas.app.component.controls.ValueChangeListener;
import de.symeda.sormas.app.databinding.FragmentSampleEditLayoutBinding;
import de.symeda.sormas.app.shared.SampleFormNavigationCapsule;
import de.symeda.sormas.app.util.DataUtils;

import static android.view.View.GONE;

public class SampleEditFragment extends BaseEditFragment<FragmentSampleEditLayoutBinding, Sample, Sample> {

    private Sample record;
    private SampleTest mostRecentTest;

    // Enum lists

    private List<Item> sampleMaterialList;
    private List<Item> sampleTestTypeList;
    private List<Item> sampleSourceList;
    private List<Facility> labList;

    // Instance methods

    public static SampleEditFragment newInstance(SampleFormNavigationCapsule capsule, Sample activityRootData) {
        return newInstance(SampleEditFragment.class, capsule, activityRootData);
    }

    private void setUpFieldVisibilities(final FragmentSampleEditLayoutBinding contentBinding) {
        // Most recent test layout
        if (!record.isReceived() || record.getSpecimenCondition() != SpecimenCondition.ADEQUATE) {
            contentBinding.mostRecentTestLayout.setVisibility(GONE);
        } else {
            if (mostRecentTest != null) {
                contentBinding.noRecentTest.setVisibility(GONE);
            }
        }
    }

    // Overrides

    @Override
    protected String getSubHeadingTitle() {
        return getResources().getString(R.string.caption_sample_information);
    }

    @Override
    public Sample getPrimaryData() {
        return record;
    }

    @Override
    protected void prepareFragmentData(Bundle savedInstanceState) {
        record = getActivityRootData();
        mostRecentTest = DatabaseHelper.getSampleTestDao().queryMostRecentBySample(record);

        sampleMaterialList = DataUtils.getEnumItems(SampleMaterial.class, true);
        sampleTestTypeList = DataUtils.getEnumItems(SampleTestType.class, true);
        sampleSourceList = DataUtils.getEnumItems(SampleSource.class, true);
        labList = DatabaseHelper.getFacilityDao().getLaboratories(true);
    }

    @Override
    public void onLayoutBinding(FragmentSampleEditLayoutBinding contentBinding) {
        contentBinding.setData(record);
        contentBinding.setSampleTest(mostRecentTest);
    }

    @Override
    public void onAfterLayoutBinding(final FragmentSampleEditLayoutBinding contentBinding) {
        setUpFieldVisibilities(contentBinding);

        // Initialize ControlSpinnerFields
        contentBinding.sampleSampleMaterial.initializeSpinner(sampleMaterialList);
        contentBinding.sampleSuggestedTypeOfTest.initializeSpinner(sampleTestTypeList);
        contentBinding.sampleSampleSource.initializeSpinner(sampleSourceList);
        contentBinding.sampleLab.initializeSpinner(DataUtils.toItems(labList), new ValueChangeListener() {
            @Override
            public void onChange(ControlPropertyField field) {
                Facility laboratory = (Facility) field.getValue();
                if (laboratory != null && laboratory.getUuid().equals(FacilityDto.OTHER_LABORATORY_UUID)) {
                    contentBinding.sampleLabDetails.setVisibility(View.VISIBLE);
                } else {
                    contentBinding.sampleLabDetails.hideField(true);
                }
            }
        });

        // Initialize ControlDateFields and ControlDateTimeFields
        contentBinding.sampleSampleDateTime.initializeDateTimeField(getFragmentManager());
        contentBinding.sampleShipmentDate.initializeDateField(getFragmentManager());

        // Disable fields the user doesn't have access to - this involves almost all fields when
        // the user is not the one that originally reported the sample
        if (!ConfigProvider.getUser().equals(record.getReportingUser())) {
            contentBinding.sampleSampleCode.setEnabled(false);
            contentBinding.sampleSampleDateTime.setEnabled(false);
            contentBinding.sampleSampleMaterial.setEnabled(false);
            contentBinding.sampleSampleMaterialText.setEnabled(false);
            contentBinding.sampleSampleSource.setEnabled(false);
            contentBinding.sampleSuggestedTypeOfTest.setEnabled(false);
            contentBinding.sampleLab.setEnabled(false);
            contentBinding.sampleLabDetails.setEnabled(false);
            contentBinding.sampleShipped.setEnabled(false);
            contentBinding.sampleShipmentDate.setEnabled(false);
            contentBinding.sampleShipmentDetails.setEnabled(false);
        }
    }

    @Override
    public int getEditLayout() {
        return R.layout.fragment_sample_edit_layout;
    }

}
