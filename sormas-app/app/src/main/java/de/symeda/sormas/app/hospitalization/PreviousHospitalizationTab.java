package de.symeda.sormas.app.hospitalization;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.annotations.Nullable;

import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.common.AbstractDomainObject;
import de.symeda.sormas.app.backend.hospitalization.PreviousHospitalization;
import de.symeda.sormas.app.component.AbstractFormDialogFragment;
import de.symeda.sormas.app.databinding.PreviousHospitalizationEditFragmentLayoutBinding;


public class PreviousHospitalizationTab extends AbstractFormDialogFragment<PreviousHospitalization> {

    private PreviousHospitalizationEditFragmentLayoutBinding binding;

    @Override
    public View onCreateDialogView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.previous_hospitalization_edit_fragment_layout, container, false);
        View view = binding.getRoot();
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.setPrevHosp(getFormItem());
        binding.prevHospAdmissionDate.initialize(this);
        binding.prevHospDischargeDate.initialize(this);
    }

    @Override
    public AbstractDomainObject getData() {
        return binding.getPrevHosp();
    }
}
