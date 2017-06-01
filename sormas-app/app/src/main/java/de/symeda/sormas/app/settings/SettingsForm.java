package de.symeda.sormas.app.settings;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.common.AbstractDomainObject;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.config.ConfigProvider;
import de.symeda.sormas.app.databinding.SettingsFragmentLayoutBinding;
import de.symeda.sormas.app.rest.RetroProvider;
import de.symeda.sormas.app.util.FormTab;
import de.symeda.sormas.app.util.SyncCallback;
import de.symeda.sormas.app.util.SyncInfrastructureTask;

/**
 * Created by Stefan Szczesny on 27.07.2016.
 */
public class SettingsForm extends FormTab {

    private SettingsFragmentLayoutBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.settings_fragment_layout, container, false);
        return binding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();

        binding.configServerUrl.setValue((String)ConfigProvider.getServerRestUrl());

        binding.configDropData.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                dropData();
            }
        });
    }

    private void dropData() {

        if (RetroProvider.isConnected()) {
            binding.configProgressBar.setVisibility(View.VISIBLE);

            DatabaseHelper.clearTables(true);
            SyncInfrastructureTask.syncAll(new SyncCallback() {
                @Override
                public void call(boolean syncFailed) {
                    SettingsForm.this.onResume();
                    binding.configProgressBar.setVisibility(View.GONE);
                }
            }, getContext());
        } else {
            Snackbar.make(getActivity().findViewById(R.id.base_layout), R.string.snackbar_no_connection, Snackbar.LENGTH_LONG).show();
        }
    }

    public String getServerUrl() {
        return binding.configServerUrl.getValue();
    }

    @Override
    public AbstractDomainObject getData() {
        return null;
    }
}