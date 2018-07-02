package de.symeda.sormas.app.settings;

import android.accounts.AuthenticatorException;
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
import android.widget.AdapterView;

import java.net.ConnectException;

import de.symeda.sormas.api.utils.InfoProvider;
import de.symeda.sormas.app.BaseLandingFragment;
import de.symeda.sormas.app.EnterPinActivity;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.config.ConfigProvider;
import de.symeda.sormas.app.component.SyncLogDialog;
import de.symeda.sormas.app.component.dialog.TeboProgressDialog;
import de.symeda.sormas.app.component.menu.LandingPageMenuItem;
import de.symeda.sormas.app.core.Callback;
import de.symeda.sormas.app.core.NotificationContext;
import de.symeda.sormas.app.core.adapter.multiview.EnumMapDataBinderAdapter;
import de.symeda.sormas.app.core.notification.NotificationHelper;
import de.symeda.sormas.app.core.notification.NotificationPosition;
import de.symeda.sormas.app.core.notification.NotificationType;
import de.symeda.sormas.app.databinding.FragmentSettingsLayoutBinding;
import de.symeda.sormas.app.login.LoginActivity;
import de.symeda.sormas.app.rest.RetroProvider;
import de.symeda.sormas.app.rest.SynchronizeDataAsync;
import de.symeda.sormas.app.util.AppUpdateController;
import de.symeda.sormas.app.util.SoftKeyboardHelper;
import de.symeda.sormas.app.util.SyncCallback;

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

        binding.txtSettingsServerUrl.setValue(ConfigProvider.getServerRestUrl());
        binding.btnSettingsChangePIN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changePIN(v);
            }
        });
        binding.btnSettingsRepullData.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                repullData(v);
            }
        });
        binding.btnSettingsSyncLog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openSyncLog(v);
            }
        });
        binding.btnSettingsLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logout(v);
            }
        });
        binding.btnSettingsRepullData.setVisibility(View.GONE);

        binding.sormasVersion.setText("SORMAS " + InfoProvider.get().getVersion());
        binding.sormasVersion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                versionClickedCount++;
                if (versionClickedCount >= SHOW_DEV_OPTIONS_CLICK_LIMIT) {
                    binding.txtSettingsServerUrl.setVisibility(View.VISIBLE);
                    binding.btnSettingsLogout.setVisibility(View.VISIBLE);
                }
            }
        });

        return binding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();

        boolean hasUser = ConfigProvider.getUser() != null;
        binding.txtSettingsServerUrl.setVisibility(versionClickedCount >= SHOW_DEV_OPTIONS_CLICK_LIMIT ? View.VISIBLE : View.GONE);
        binding.btnSettingsChangePIN.setVisibility(hasUser ? View.VISIBLE : View.GONE);
        binding.btnSettingsRepullData.setVisibility(hasUser ? View.VISIBLE : View.GONE);
        binding.btnSettingsSyncLog.setVisibility(hasUser ? View.VISIBLE : View.GONE);
        binding.btnSettingsLogout.setVisibility(hasUser && versionClickedCount >= SHOW_DEV_OPTIONS_CLICK_LIMIT ? View.VISIBLE : View.GONE);
    }

    @Override
    public void onPause() {
        super.onPause();

        SoftKeyboardHelper.hideKeyboard(getActivity(), this);
    }

    public String getServerUrl() {
        return binding.txtSettingsServerUrl.getValue();
    }

    public void changePIN(View view) {
        Intent intent = new Intent(getActivity(), EnterPinActivity.class);
        intent.putExtra(EnterPinActivity.CALLED_FROM_SETTINGS, true);
        startActivity(intent);
    }

    /**
     * Only possible when server connection is available
     */
    private void repullData(View view) {

        if (!RetroProvider.isConnected()) {
            try {
                RetroProvider.connect(getContext());
            } catch (AuthenticatorException e) {
                //Snackbar.make(getActivity().findViewById(R.id.base_layout), e.getMessage(), Snackbar.LENGTH_LONG).show();
                NotificationHelper.showNotification((NotificationContext)getActivity(), NotificationPosition.BOTTOM, NotificationType.ERROR, e.getMessage());
            } catch (RetroProvider.ApiVersionException e) {
                if (e.getAppUrl() != null) {
                    //TODO: Orson Remove Version Check
                    AppUpdateController.getInstance().updateApp(this.getActivity(), e.getAppUrl(), e.getVersion(), true, null);
                    return;
                } else {
                    //Snackbar.make(getActivity().findViewById(R.id.base_layout), e.getMessage(), Snackbar.LENGTH_LONG).show();
                    NotificationHelper.showNotification((NotificationContext)getActivity(), NotificationPosition.BOTTOM, NotificationType.ERROR, e.getMessage());
                }
            } catch (ConnectException e) {
                //Snackbar.make(getActivity().findViewById(R.id.base_layout), e.getMessage(), Snackbar.LENGTH_LONG).show();
                NotificationHelper.showNotification((NotificationContext)getActivity(), NotificationPosition.BOTTOM, NotificationType.ERROR, e.getMessage());
            }
        }

        if (RetroProvider.isConnected()) {
            progressDialog.show(new Callback.IAction<AlertDialog>() {
                @Override
                public void call(AlertDialog result) {

                }
            });
            //binding.configProgressBar.setVisibility(View.VISIBLE);

            DatabaseHelper.clearTables(false);
            SynchronizeDataAsync.call(SynchronizeDataAsync.SyncMode.CompleteAndRepull, getContext(), new SyncCallback() {
                @Override
                public void call(boolean syncFailed, String syncFailedMessage) {
                    SettingsFragment.this.onResume();
                    progressDialog.dismiss();
                    //binding.configProgressBar.setVisibility(View.GONE);
                }
            });
        } else {
            //Snackbar.make(getActivity().findViewById(R.id.base_layout), R.string.snackbar_no_connection, Snackbar.LENGTH_LONG).show();
            NotificationHelper.showNotification((NotificationContext)getActivity(), NotificationPosition.BOTTOM, NotificationType.ERROR, R.string.snackbar_no_connection);
        }
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
        ConfigProvider.setAccessGranted(false);
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

    @Override
    public int getMenuData() {
        return -1;
    }

    @Override
    public boolean onLandingPageMenuClick(AdapterView parent, View view, LandingPageMenuItem menuItem, int position, long id) {
        return false;
    }

    @Override
    public int onNotificationCountChangingAsync(AdapterView parent, LandingPageMenuItem menuItem, int position) {
        return 0;
    }
}
