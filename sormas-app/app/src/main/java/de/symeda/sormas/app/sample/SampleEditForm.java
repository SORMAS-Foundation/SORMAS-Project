package de.symeda.sormas.app.sample;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CompoundButton;

import java.util.Date;
import java.util.List;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.sample.SampleMaterial;
import de.symeda.sormas.api.sample.SampleSource;
import de.symeda.sormas.api.sample.SampleTestType;
import de.symeda.sormas.api.sample.SpecimenCondition;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.caze.Case;
import de.symeda.sormas.app.backend.common.AbstractDomainObject;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.config.ConfigProvider;
import de.symeda.sormas.app.backend.sample.Sample;
import de.symeda.sormas.app.backend.sample.SampleDao;
import de.symeda.sormas.app.backend.sample.SampleTest;
import de.symeda.sormas.app.component.FieldHelper;
import de.symeda.sormas.app.databinding.SampleDataFragmentLayoutBinding;
import de.symeda.sormas.app.util.DataUtils;
import de.symeda.sormas.app.util.FormTab;
import de.symeda.sormas.app.util.Item;
import de.symeda.sormas.app.validation.SampleValidator;

/**
 * Created by Mate Strysewske on 07.02.2017.
 */

public class SampleEditForm extends FormTab {

    private SampleDataFragmentLayoutBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.sample_data_fragment_layout, container, false);

        final String sampleUuid = getArguments().getString(SampleEditActivity.KEY_SAMPLE_UUID);

        final SampleDao sampleDao = DatabaseHelper.getSampleDao();
        Sample sample = null;

        if (sampleUuid == null) {
            final String caseUuid = getArguments().getString(SampleEditActivity.KEY_CASE_UUID);
            final Case associatedCase = DatabaseHelper.getCaseDao().queryUuid(caseUuid);
            sample = DatabaseHelper.getSampleDao().create(associatedCase);
        } else {
            sample = sampleDao.queryUuid(sampleUuid);
            DatabaseHelper.getSampleDao().markAsRead(sample);
            sample = sampleDao.queryForId(sample.getId());
        }

        binding.setSample(sample);

        binding.sampleShipmentDate.initialize(this);

        // Visibility initialization of shipment details
        if (!binding.sampleShipped.getValue()) {
            binding.sampleShipmentDate.setVisibility(View.GONE);
            binding.sampleShipmentDetails.setVisibility(View.GONE);
        }
        binding.sampleShipped.setAdditionalListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    binding.sampleShipmentDate.setVisibility(View.VISIBLE);
                    binding.sampleShipmentDetails.setVisibility(View.VISIBLE);
                    binding.sampleShipmentDate.setValue(new Date());
                } else {
                    binding.sampleShipmentDate.setVisibility(View.GONE);
                    binding.sampleShipmentDetails.setVisibility(View.GONE);
                }
            }
        });

        if (binding.sampleMaterial.getValue() == SampleMaterial.OTHER) {
            binding.sampleMaterialText.setVisibility(View.INVISIBLE);
        }
        binding.sampleMaterial.registerListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (((Item) parent.getItemAtPosition(position)).getValue() == SampleMaterial.OTHER) {
                    binding.sampleMaterialText.setVisibility(View.VISIBLE);
                } else {
                    binding.sampleMaterialText.setVisibility(View.INVISIBLE);
                    binding.sampleMaterialText.setValue(null);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        binding.sampleDateTime.initialize(this);
        FieldHelper.initSpinnerField(binding.sampleMaterial, SampleMaterial.class);
        FieldHelper.initSpinnerField(binding.sampleSampleSource, SampleSource.class);
        if (binding.getSample().getAssociatedCase().getDisease() != Disease.AVIAN_INFLUENCA) {
            binding.sampleSampleSource.setVisibility(View.GONE);
        }

        FieldHelper.initSpinnerField(binding.sampleSuggestedTypeOfTest, SampleTestType.class);

        final List laboratories = DataUtils.toItems(DatabaseHelper.getFacilityDao().getLaboratories());
        FieldHelper.initSpinnerField(binding.sampleLab, laboratories);

        // only show received fields when sample has been received
        binding.sampleReceivedDate.initialize(this);
        binding.sampleReceivedDate.setEnabled(false);
        FieldHelper.initSpinnerField(binding.sampleSpecimenCondition, SpecimenCondition.class);
        binding.sampleSpecimenCondition.setEnabled(false);
        if (sampleUuid != null) {
            if (binding.getSample().isReceived()) {
                binding.sampleReceivedLayout.setVisibility(View.VISIBLE);
            }
        }

        // recent test should only be displayed when an existing sample is viewed, not
        // when a new one is created
        if (sampleUuid != null) {
            if (binding.getSample().getSpecimenCondition() != SpecimenCondition.NOT_ADEQUATE) {
                binding.recentTestLayout.setVisibility(View.VISIBLE);
                SampleTest mostRecentTest = DatabaseHelper.getSampleTestDao().queryMostRecentBySample(binding.getSample());
                if (mostRecentTest != null) {
                    binding.sampleTypeOfTest.setVisibility(View.VISIBLE);
                    binding.sampleTestResult.setVisibility(View.VISIBLE);
                } else {
                    binding.sampleNoRecentTestText.setVisibility(View.VISIBLE);
                }
            }
        }

        SampleValidator.setRequiredHintsForSampleData(binding);

        if (sampleUuid != null) {
            if (!ConfigProvider.getUser().getUuid().equals(binding.getSample().getReportingUser().getUuid())) {
                binding.sampleSampleCode.setEnabled(false);
                binding.sampleDateTime.setEnabled(false);
                binding.sampleMaterial.setEnabled(false);
                binding.sampleMaterialText.setEnabled(false);
                binding.sampleSuggestedTypeOfTest.setEnabled(false);
                binding.sampleLab.setEnabled(false);
                binding.sampleSampleCode.setEnabled(false);
                binding.sampleShipped.setEnabled(false);
                binding.sampleShipmentDate.setEnabled(false);
                binding.sampleShipmentDetails.setEnabled(false);
            }
        }

        return binding.getRoot();
    }

    @Override
    public AbstractDomainObject getData() {
        return binding.getSample();
    }

    public SampleDataFragmentLayoutBinding getBinding() {
        return binding;
    }

}
