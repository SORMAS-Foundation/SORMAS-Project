package de.symeda.sormas.app.settings;

import android.accounts.AuthenticatorException;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import de.symeda.sormas.app.R;
import de.symeda.sormas.app.databinding.FragmentSettingsLayoutBinding;
import de.symeda.sormas.app.rest.RetroProvider;
import de.symeda.sormas.app.rest.SynchronizeDataAsync;
import de.symeda.sormas.app.util.FormTab;
import de.symeda.sormas.app.util.SyncCallback;

import java.net.ConnectException;

import de.symeda.sormas.app.backend.common.AbstractDomainObject;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.config.ConfigProvider;

/**
 * Created by Orson on 03/11/2017.
 */

public class SettingsForm extends FormTab {


    private FragmentSettingsLayoutBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_settings_layout, container, false);

        binding.txtSettingsServerUrl.setValue((String) ConfigProvider.getServerRestUrl());

        binding.btnSettingsDropData.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                dropData();
            }
        });
        binding.btnSettingsDropData.setVisibility(View.GONE);

        return binding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();

        boolean hasUser = ConfigProvider.getUser() != null;
        binding.btnSettingsChangePIN.setVisibility(hasUser ? View.VISIBLE : View.GONE);
        binding.btnSettingsSyncLog.setVisibility(hasUser ? View.VISIBLE : View.GONE);
        binding.btnSettingsLogout.setVisibility(hasUser ? View.VISIBLE : View.GONE);
    }

    /**
     * Only possible when server connection is available
     */
    private void dropData() {

        if (!RetroProvider.isConnected()) {
            try {
                RetroProvider.connect(getContext());
            } catch (AuthenticatorException e) {
                Snackbar.make(getActivity().findViewById(R.id.base_layout), e.getMessage(), Snackbar.LENGTH_LONG).show();
            } catch (RetroProvider.ApiVersionException e) {
                Snackbar.make(getActivity().findViewById(R.id.base_layout), e.getMessage(), Snackbar.LENGTH_LONG).show();
            } catch (ConnectException e) {
                Snackbar.make(getActivity().findViewById(R.id.base_layout), e.getMessage(), Snackbar.LENGTH_LONG).show();
            }
        }

        if (RetroProvider.isConnected()) {
            binding.configProgressBar.setVisibility(View.VISIBLE);

            DatabaseHelper.clearTables(true);
            SynchronizeDataAsync.call(SynchronizeDataAsync.SyncMode.ChangesAndInfrastructure, getContext(), new SyncCallback() {
                @Override
                public void call(boolean syncFailed, String syncFailedMessage) {
                    SettingsForm.this.onResume();
                    binding.configProgressBar.setVisibility(View.GONE);
                }
            });
        } else {
            Snackbar.make(getActivity().findViewById(R.id.base_layout), R.string.snackbar_no_connection, Snackbar.LENGTH_LONG).show();
        }
    }

    public String getServerUrl() {
        return binding.txtSettingsServerUrl.getValue();
    }

    @Override
    public AbstractDomainObject getData() {
        return null;
    }
}
