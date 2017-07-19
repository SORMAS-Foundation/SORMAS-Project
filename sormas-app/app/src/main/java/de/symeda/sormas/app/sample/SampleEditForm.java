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
import de.symeda.sormas.api.facility.FacilityType;
import de.symeda.sormas.api.sample.SampleMaterial;
import de.symeda.sormas.api.sample.SampleSource;
import de.symeda.sormas.api.sample.SampleTestType;
import de.symeda.sormas.api.sample.ShipmentStatus;
import de.symeda.sormas.api.sample.SpecimenCondition;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.caze.Case;
import de.symeda.sormas.app.backend.common.AbstractDomainObject;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.sample.Sample;
import de.symeda.sormas.app.backend.sample.SampleDao;
import de.symeda.sormas.app.backend.sample.SampleTest;
import de.symeda.sormas.app.component.FieldHelper;
import de.symeda.sormas.app.databinding.SampleDataFragmentLayoutBinding;
import de.symeda.sormas.app.util.DataUtils;
import de.symeda.sormas.app.util.FormTab;
import de.symeda.sormas.app.util.Item;

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
        }

        binding.setSample(sample);

        ShipmentStatus shipmentStatus = binding.getSample().getShipmentStatus();
        if (shipmentStatus == ShipmentStatus.NOT_SHIPPED) {
            binding.sampleShipmentStatus.setChecked(false);
            binding.sampleShipmentDate.setVisibility(View.INVISIBLE);
            binding.sampleShipmentDetails.setVisibility(View.GONE);
        } else if (shipmentStatus == ShipmentStatus.SHIPPED) {
            binding.sampleShipmentStatus.setChecked(true);
        } else {
            binding.sampleShipmentStatus.setChecked(true);
            binding.sampleShipmentStatus.setText(binding.getSample().getShipmentStatus().toString());
            binding.sampleShipmentStatus.setEnabled(false);
            binding.sampleShipmentDate.setEnabled(false);
        }

        binding.sampleShipmentDate.initialize(this);

        binding.sampleShipmentStatus.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    binding.sampleShipmentDate.setVisibility(View.VISIBLE);
                    binding.sampleShipmentDetails.setVisibility(View.VISIBLE);
                    binding.sampleShipmentDate.setValue(new Date());
                } else {
                    binding.sampleShipmentDate.setVisibility(View.INVISIBLE);
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

        final List laboratories = DataUtils.toItems(DatabaseHelper.getFacilityDao().getByType(FacilityType.LABORATORY, false));
        FieldHelper.initSpinnerField(binding.sampleLab, laboratories);
        binding.sampleReceivedDate.initialize(this);
        binding.sampleReceivedDate.setEnabled(false);
        binding.sampleLabSampleID.setEnabled(false);
        if (binding.getSample().getShipmentStatus() != ShipmentStatus.RECEIVED) {
            binding.sampleReceivedDate.setVisibility(View.GONE);
            binding.sampleLabSampleID.setVisibility(View.GONE);
        }

        // recent test should only be displayed when an existing sample is viewed, not
        // when a new one is created
        if (sampleUuid != null) {
            if (binding.getSample().getSpecimenCondition() == SpecimenCondition.NOT_ADEQUATE) {
                binding.sampleTypeOfTest.setVisibility(View.GONE);
                binding.sampleTestResult.setVisibility(View.GONE);
                binding.sampleNoRecentTestText.setVisibility(View.GONE);
            } else {
                SampleTest mostRecentTest = DatabaseHelper.getSampleTestDao().queryMostRecentBySample(binding.getSample());
                binding.sampleNoTestPossibleText.setVisibility(View.GONE);
                binding.sampleNoTestPossibleReason.setVisibility(View.GONE);
                if (mostRecentTest != null) {
                    binding.sampleNoRecentTestText.setVisibility(View.GONE);
                } else {
                    binding.sampleTypeOfTest.setVisibility(View.GONE);
                    binding.sampleTestResult.setVisibility(View.GONE);
                }
            }
        } else {
            binding.recentTestLayout.setVisibility(View.GONE);
        }

        return binding.getRoot();
    }

    @Override
    public AbstractDomainObject getData() {
        return binding.getSample();
    }

}
