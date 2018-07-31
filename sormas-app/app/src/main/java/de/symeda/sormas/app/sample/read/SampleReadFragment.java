package de.symeda.sormas.app.sample.read;

import android.os.Bundle;
import android.view.View;

import org.apache.commons.lang3.StringUtils;

import de.symeda.sormas.api.facility.FacilityDto;
import de.symeda.sormas.api.sample.SpecimenCondition;
import de.symeda.sormas.app.BaseReadFragment;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.sample.Sample;
import de.symeda.sormas.app.backend.sample.SampleTest;
import de.symeda.sormas.app.databinding.FragmentSampleReadLayoutBinding;
import de.symeda.sormas.app.sample.ShipmentStatus;

import static android.view.View.GONE;

public class SampleReadFragment extends BaseReadFragment<FragmentSampleReadLayoutBinding, Sample, Sample> {

    private Sample record;
    private SampleTest mostRecentTest;

    public static SampleReadFragment newInstance(Sample activityRootData) {
        return newInstance(SampleReadFragment.class, null, activityRootData);
    }

    private void setUpControlListeners(FragmentSampleReadLayoutBinding contentBinding) {
        if (!StringUtils.isEmpty(record.getReferredToUuid())) {
            contentBinding.sampleReferredToUuid.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String referredToUuid = record.getReferredToUuid();
                    Sample sample = DatabaseHelper.getSampleDao().queryUuid(referredToUuid);
                    if (sample != null) {
                        // Activity needs to be destroyed because it is only resumed, not created otherwise
                        // and therefore the record uuid is not changed
                        if (getActivity() != null) {
                            getActivity().finish();
                        }
                        SampleReadActivity.startActivity(getActivity(), sample.getUuid());
                    }
                }
            });
        }
    }

    private void setUpFieldVisibilities(FragmentSampleReadLayoutBinding contentBinding) {
        // Most recent test layout
        if (!record.isReceived() || record.getSpecimenCondition() != SpecimenCondition.ADEQUATE) {
            contentBinding.mostRecentTestLayout.setVisibility(GONE);
        } else {
            if (mostRecentTest != null) {
                contentBinding.noRecentTest.setVisibility(GONE);
            }
        }

        // Lab details
        if (!record.getLab().getUuid().equals(FacilityDto.OTHER_LABORATORY_UUID)) {
            contentBinding.sampleLabDetails.setVisibility(GONE);
        }
    }

    // Overrides

    @Override
    protected void prepareFragmentData(Bundle savedInstanceState) {
        record = getActivityRootData();
        mostRecentTest = DatabaseHelper.getSampleTestDao().queryMostRecentBySample(record);
    }

    @Override
    public void onLayoutBinding(FragmentSampleReadLayoutBinding contentBinding) {
        setUpControlListeners(contentBinding);

        contentBinding.setData(record);
        contentBinding.setSampleTest(mostRecentTest);
    }

    @Override
    public void onAfterLayoutBinding(FragmentSampleReadLayoutBinding contentBinding) {
        setUpFieldVisibilities(contentBinding);
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

}
