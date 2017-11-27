package de.symeda.sormas.app.epidata;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import de.symeda.sormas.api.epidata.TravelType;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.common.AbstractDomainObject;
import de.symeda.sormas.app.backend.epidata.EpiDataBurial;
import de.symeda.sormas.app.backend.epidata.EpiDataTravel;
import de.symeda.sormas.app.component.AbstractFormDialogFragment;
import de.symeda.sormas.app.component.FieldHelper;
import de.symeda.sormas.app.databinding.EpidataBurialEditFragmentLayoutBinding;
import de.symeda.sormas.app.databinding.EpidataTravelEditFragmentLayoutBinding;
import de.symeda.sormas.app.validation.EpiDataValidator;

/**
 * Created by Mate Strysewske on 09.03.2017.
 */

public class EpiDataTravelForm extends AbstractFormDialogFragment<EpiDataTravel> {

    private EpidataTravelEditFragmentLayoutBinding binding;

    @Override
    public View onCreateDialogView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.epidata_travel_edit_fragment_layout, container, false);
        View view = binding.getRoot();
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.setEpiDataTravel(getFormItem());
        binding.travelFrom.initialize(this);
        binding.travelTo.initialize(this);
        FieldHelper.initSpinnerField(binding.travelType, TravelType.class);

        EpiDataValidator.setSoftRequiredHintsForTravel(binding);
    }

    @Override
    public AbstractDomainObject getData() {
        return binding.getEpiDataTravel();
    }

    public EpidataTravelEditFragmentLayoutBinding getBinding() {
        return binding;
    }
}
