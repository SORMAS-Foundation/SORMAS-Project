package de.symeda.sormas.app.sample;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import java.util.List;

import de.symeda.sormas.api.facility.FacilityType;
import de.symeda.sormas.api.sample.SampleMaterial;
import de.symeda.sormas.api.sample.ShipmentStatus;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.common.AbstractDomainObject;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.sample.Sample;
import de.symeda.sormas.app.backend.sample.SampleDao;
import de.symeda.sormas.app.component.FieldHelper;
import de.symeda.sormas.app.databinding.SampleDataFragmentLayoutBinding;
import de.symeda.sormas.app.util.DataUtils;
import de.symeda.sormas.app.util.FormTab;
import de.symeda.sormas.app.util.Item;

/**
 * Created by Mate Strysewske on 07.02.2017.
 */

public class SampleEditTab extends FormTab {

    private SampleDataFragmentLayoutBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.sample_data_fragment_layout, container, false);
        return binding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();

        final String sampleUuid = getArguments().getString(Sample.UUID);
        final SampleDao sampleDao = DatabaseHelper.getSampleDao();
        final Sample sample = sampleDao.queryUuid(sampleUuid);
        binding.setSample(sample);

        ShipmentStatus shipmentStatus = sample.getShipmentStatus();
        if(shipmentStatus == ShipmentStatus.NOT_SHIPPED) {
            binding.sampleShipmentStatus.setChecked(false);
            binding.sampleShipmentDate.setVisibility(View.INVISIBLE);
        } else if(shipmentStatus == ShipmentStatus.SHIPPED) {
            binding.sampleShipmentStatus.setChecked(true);
        } else {
            binding.sampleShipmentStatus.setChecked(true);
            binding.sampleShipmentStatus.setEnabled(false);
            binding.sampleShipmentDate.setEnabled(false);
        }

        binding.sampleShipmentDate.initialize(this);

        binding.sampleShipmentStatus.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    binding.sampleShipmentDate.setVisibility(View.VISIBLE);
                } else {
                    binding.sampleShipmentDate.setVisibility(View.INVISIBLE);
                }
            }
        });

        if(binding.sampleMaterial.getValue() == SampleMaterial.OTHER) {
            binding.sampleMaterialText.setVisibility(View.INVISIBLE);
        }
        binding.sampleMaterial.registerListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(((Item)parent.getItemAtPosition(position)).getValue() == SampleMaterial.OTHER) {
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

        final List laboratories = DataUtils.toItems(DatabaseHelper.getFacilityDao().getByType(FacilityType.LABORATORY));
        FieldHelper.initSpinnerField(binding.sampleLab, laboratories);
        binding.sampleReceivedDate.initialize(this);
        binding.sampleReceivedDate.setEnabled(false);

    }

    @Override
    public AbstractDomainObject getData() {
        return binding.getSample();
    }

}
