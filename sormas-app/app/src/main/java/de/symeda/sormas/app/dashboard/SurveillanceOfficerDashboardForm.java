package de.symeda.sormas.app.dashboard;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import de.symeda.sormas.app.R;
import de.symeda.sormas.app.databinding.FragmentDashboardSurveillanceOfficerLayoutBinding;
import de.symeda.sormas.app.util.FormTab;

import de.symeda.sormas.app.backend.common.AbstractDomainObject;

//import de.symeda.sormas.app.databinding.SettingsFragmentLayoutBinding;

/**
 * Created by Orson on 20/11/2017.
 */

public class SurveillanceOfficerDashboardForm extends FormTab {

    private FragmentDashboardSurveillanceOfficerLayoutBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_dashboard_surveillance_officer_layout, container, false);

        return binding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();

        /*boolean hasUser = ConfigProvider.getUser() != null;
        binding.btnSettingsChangePIN.setVisibility(hasUser ? View.VISIBLE : View.GONE);
        binding.btnSettingsSyncLog.setVisibility(hasUser ? View.VISIBLE : View.GONE);
        binding.btnSettingsLogout.setVisibility(hasUser ? View.VISIBLE : View.GONE);*/
    }

    @Override
    public AbstractDomainObject getData() {
        return null;
    }
}
