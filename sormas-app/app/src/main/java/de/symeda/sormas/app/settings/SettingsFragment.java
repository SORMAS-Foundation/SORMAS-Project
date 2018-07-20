package de.symeda.sormas.app.settings;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import de.symeda.sormas.api.utils.InfoProvider;
import de.symeda.sormas.app.BaseLandingFragment;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.config.ConfigProvider;
import de.symeda.sormas.app.component.dialog.ConfirmationDialog;
import de.symeda.sormas.app.component.dialog.SyncLogDialog;
import de.symeda.sormas.app.component.dialog.TeboProgressDialog;
import de.symeda.sormas.app.core.adapter.multiview.EnumMapDataBinderAdapter;
import de.symeda.sormas.app.databinding.FragmentSettingsLayoutBinding;
import de.symeda.sormas.app.login.EnterPinActivity;
import de.symeda.sormas.app.login.LoginActivity;
import de.symeda.sormas.app.rest.RetroProvider;
import de.symeda.sormas.app.rest.SynchronizeDataAsync;
import de.symeda.sormas.app.util.Callback;
import de.symeda.sormas.app.util.LocationService;
import de.symeda.sormas.app.util.SoftKeyboardHelper;

/**
 * Created by Orson on 03/11/2017.
 */

public class SettingsFragment extends BaseLandingFragment {

    private final int SHOW_DEV_OPTIONS_CLICK_LIMIT = 5;

    private FragmentSettingsLayoutBinding binding;
    private TeboProgressDialog progressDialog;
    private int versionClickedCount;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        progressDialog = new TeboProgressDialog(getActivity());

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_settings_layout, container, false);

        binding.settingsServerUrl.setValue(ConfigProvider.getServerRestUrl());
        binding.changePin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changePIN(v);
            }
        });
        binding.resynchronizeData.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                repullData(v);
            }
        });
        binding.showSyncLog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openSyncLog(v);
            }
        });
        binding.logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logout(v);
            }
        });
        binding.resynchronizeData.setVisibility(View.GONE);

        binding.sormasVersion.setText("SORMAS " + InfoProvider.get().getVersion());
        binding.sormasVersion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                versionClickedCount++;
                if (versionClickedCount >= SHOW_DEV_OPTIONS_CLICK_LIMIT) {
                    binding.settingsServerUrl.setVisibility(View.VISIBLE);
                    binding.logout.setVisibility(View.VISIBLE);
                }
            }
        });

        return binding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();

        boolean hasUser = ConfigProvider.getUser() != null;
        binding.settingsServerUrl.setVisibility(versionClickedCount >= SHOW_DEV_OPTIONS_CLICK_LIMIT ? View.VISIBLE : View.GONE);
        binding.changePin.setVisibility(hasUser ? View.VISIBLE : View.GONE);
        binding.resynchronizeData.setVisibility(hasUser ? View.VISIBLE : View.GONE);
        binding.showSyncLog.setVisibility(hasUser ? View.VISIBLE : View.GONE);
        binding.logout.setVisibility(hasUser && versionClickedCount >= SHOW_DEV_OPTIONS_CLICK_LIMIT ? View.VISIBLE : View.GONE);
    }

    @Override
    public void onPause() {
        super.onPause();

        SoftKeyboardHelper.hideKeyboard(getActivity(), this);
    }

    public String getServerUrl() {
        return binding.settingsServerUrl.getValue();
    }

    public void changePIN(View view) {
        Intent intent = new Intent(getActivity(), EnterPinActivity.class);
        intent.putExtra(EnterPinActivity.CALLED_FROM_SETTINGS, true);
        startActivity(intent);
    }

    private void repullData(View view) {

        final ConfirmationDialog confirmationDialog = new ConfirmationDialog(getActivity(),
                R.string.heading_confirmation_dialog,
                R.string.heading_sub_confirmation_notification_dialog_resync);

        confirmationDialog.setOnPositiveClickListener(new de.symeda.sormas.app.component.dialog.TeboAlertDialogInterface.PositiveOnClickListener() {
            @Override
            public void onOkClick(View v, Object confirmationItem, View viewRoot) {
                confirmationDialog.dismiss();

                getBaseActivity().synchronizeData(SynchronizeDataAsync.SyncMode.CompleteAndRepull,
                        true, true, null, null,
                        new Callback() {
                            @Override
                            public void call() {
                                DatabaseHelper.clearTables(false);
                            }
                        });
            }
        });

        confirmationDialog.show(null);
    }

    public void openSyncLog(View view) {
        SyncLogDialog syncLogDialog = new SyncLogDialog(getContext());
        syncLogDialog.show(getContext());
    }

    public void logout(View view) {
        if (SynchronizeDataAsync.hasAnyUnsynchronizedData()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setCancelable(true);
            builder.setMessage(R.string.alert_unsynchronized_changes);
            builder.setTitle(R.string.alert_title_unsynchronized_changes);
            builder.setIcon(R.drawable.ic_perm_device_information_black_24dp);
            AlertDialog dialog = builder.create();

            dialog.setButton(AlertDialog.BUTTON_POSITIVE, getString(R.string.action_cancel),
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    }
            );
            dialog.setButton(AlertDialog.BUTTON_NEGATIVE, getString(R.string.action_logout_anyway),
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            processLogout();
                        }
                    }
            );

            dialog.show();
        } else {
            processLogout();
        }
    }

    private void processLogout() {
        ConfigProvider.clearUsernameAndPassword();
        ConfigProvider.clearPin();
        Intent intent = new Intent(getActivity(), LoginActivity.class);
        startActivity(intent);
    }

    @Override
    public EnumMapDataBinderAdapter createLandingAdapter() {
        return null;
    }

    @Override
    public RecyclerView.LayoutManager createLayoutManager() {
        return null;
    }
}
