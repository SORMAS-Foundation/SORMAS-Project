package de.symeda.sormas.app.sample.read;

import android.databinding.ObservableArrayList;
import android.os.Bundle;
import android.view.View;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.facility.FacilityDto;
import de.symeda.sormas.api.sample.SampleMaterial;
import de.symeda.sormas.api.sample.SpecimenCondition;
import de.symeda.sormas.app.BaseReadFragment;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.sample.Sample;
import de.symeda.sormas.app.backend.sample.SampleTest;
import de.symeda.sormas.app.core.IEntryItemOnClickListener;
import de.symeda.sormas.app.databinding.FragmentSampleReadLayoutBinding;
import de.symeda.sormas.app.shared.SampleFormNavigationCapsule;

public class SampleReadFragment extends BaseReadFragment<FragmentSampleReadLayoutBinding, Sample, Sample> {

    private Sample record;
    private SampleTest mostRecentTest;

    private IEntryItemOnClickListener onRecentTestItemClickListener;

    @Override
    protected void prepareFragmentData(Bundle savedInstanceState) {
        record = getActivityRootData();
        mostRecentTest = DatabaseHelper.getSampleTestDao().queryMostRecentBySample(record);
    }

    @Override
    public void onLayoutBinding(FragmentSampleReadLayoutBinding contentBinding) {

        setupCallback();

        contentBinding.sampleShipmentDetails.setVisibility((record.isShipped()) ? View.VISIBLE : View.GONE);
        contentBinding.sampleSampleMaterialText.setVisibility((record.getSampleMaterial() == SampleMaterial.OTHER) ? View.VISIBLE : View.GONE);
        contentBinding.sampleSampleSource.setVisibility((record.getAssociatedCase().getDisease() == Disease.NEW_INFLUENCA) ? View.VISIBLE : View.GONE);
        contentBinding.sampleReceivedLayout.setVisibility((record.isReceived()) ? View.VISIBLE : View.GONE);

        if (record.getSpecimenCondition() != SpecimenCondition.NOT_ADEQUATE) {
            contentBinding.recentTestLayout.setVisibility(View.VISIBLE);
            if (mostRecentTest != null) {
                contentBinding.sampleSuggestedTypeOfTest.setVisibility(View.VISIBLE);
                //contentBinding.sampleTestResult.setVisibility(View.VISIBLE);
            }

            //contentBinding.sampleSuggestedTypeOfTest.setVisibility((mostRecentTest != null) ? View.VISIBLE : View.GONE);
            contentBinding.sampleNoRecentTestText.setVisibility((mostRecentTest == null) ? View.VISIBLE : View.GONE);
        }

        // only show referred to field when there is a referred sample
        if (record.getReferredTo() != null) {
            Sample referredSample = record.getReferredTo();
            contentBinding.sampleReferredTo.setVisibility(View.VISIBLE);
            contentBinding.sampleReferredTo.setValue(getActivity().getResources().getString(R.string.sample_referred_to) + " " + referredSample.getLab().toString() + " " + "\u279D");
        } else {
            contentBinding.sampleReferredTo.setVisibility(View.GONE);
        }

        contentBinding.setSample(record);
        contentBinding.setCaze(record.getAssociatedCase());

        contentBinding.setResults(getTestResults());
        contentBinding.setRecentTestItemClickCallback(onRecentTestItemClickListener);
    }

    @Override
    public void onAfterLayoutBinding(FragmentSampleReadLayoutBinding contentBinding) {
        contentBinding.sampleLab.setValue(record.getLab() +
                (record.getLab().getUuid().equals(FacilityDto.OTHER_LABORATORY_UUID) ?
                        (" (" + record.getLabDetails() + ")") : ""));
    }

    @Override
    protected String getSubHeadingTitle() {
        return getResources().getString(R.string.caption_sample_information);
    }

    @Override
    public Sample getPrimaryData() {
        return record;
    }

    @Override
    public int getReadLayout() {
        return R.layout.fragment_sample_read_layout;
    }

    private ObservableArrayList getTestResults() {
        ObservableArrayList results = new ObservableArrayList();

        if (mostRecentTest != null)
            results.add(mostRecentTest);

        return results;
    }

    private void setupCallback() {
        onRecentTestItemClickListener = new IEntryItemOnClickListener() {
            @Override
            public void onClick(View v, Object item) {
                // add some functionality here
            }
        };
    }

    public static SampleReadFragment newInstance(SampleFormNavigationCapsule capsule, Sample activityRootData) {
        return newInstance(SampleReadFragment.class, capsule, activityRootData);
    }
}
