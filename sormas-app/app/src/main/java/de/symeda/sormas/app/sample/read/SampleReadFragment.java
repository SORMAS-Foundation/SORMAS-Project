package de.symeda.sormas.app.sample.read;

import android.databinding.DataBindingUtil;
import android.databinding.ObservableArrayList;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import de.symeda.sormas.app.BaseReadActivityFragment;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.core.IEntryItemOnClickListener;
import de.symeda.sormas.app.databinding.FragmentSampleReadLayoutBinding;
import de.symeda.sormas.app.util.MemoryDatabaseHelper;

import de.symeda.sormas.app.backend.common.AbstractDomainObject;
import de.symeda.sormas.app.backend.sample.Sample;

/**
 * Created by Orson on 11/12/2017.
 */

public class SampleReadFragment extends BaseReadActivityFragment<FragmentSampleReadLayoutBinding> {

    private Sample record;
    private FragmentSampleReadLayoutBinding binding;

    private IEntryItemOnClickListener onRecentTestItemClickListener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        binding = DataBindingUtil.inflate(inflater, getRootReadLayout(), container, false);
        record = MemoryDatabaseHelper.SAMPLE.getSamples(1).get(0);

        setupCallback();

        binding.setSample(record);
        binding.setCaze(record.getAssociatedCase());
        binding.setLab(record.getLab());
        binding.setResults(getTestResults());
        binding.setRecentTestItemClickCallback(onRecentTestItemClickListener);

        return binding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
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
        return binding.getSample();
    }

    @Override
    public FragmentSampleReadLayoutBinding getBinding() {
        return binding;
    }

    @Override
    public Object getRecord() {
        return record;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


        /*txtDiseaseName.setValue("Very Good");
        txtSampleCode.setValue("Better");
        txtSampleMaterial.setValue("Better");
        txtDateTimeOfSampling.setValue("Very Good");
        txtTestType.setValue("Better");*/
    }

    public void showSampleEditView(Sample sample) {
        /*Intent intent = new Intent(getActivity(), TaskEditActivity.class);
        intent.putExtra(Task.UUID, task.getUuid());
        if(parentCaseUuid != null) {
            intent.putExtra(KEY_CASE_UUID, parentCaseUuid);
        }
        if(parentContactUuid != null) {
            intent.putExtra(KEY_CONTACT_UUID, parentContactUuid);
        }
        if(parentEventUuid != null) {
            intent.putExtra(KEY_EVENT_UUID, parentEventUuid);
        }
        startActivity(intent);*/
    }

    @Override
    public int getRootReadLayout() {
        return R.layout.fragment_sample_read_layout;
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

}
