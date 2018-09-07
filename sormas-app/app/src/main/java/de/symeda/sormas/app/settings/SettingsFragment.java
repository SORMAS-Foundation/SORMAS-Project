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
import de.symeda.sormas.app.core.adapter.multiview.EnumMapDataBinderAdapter;
import de.symeda.sormas.app.databinding.FragmentSettingsLayoutBinding;
import de.symeda.sormas.app.login.EnterPinActivity;
import de.symeda.sormas.app.login.LoginActivity;
import de.symeda.sormas.app.rest.SynchronizeDataAsync;
import de.symeda.sormas.app.util.Callback;
import de.symeda.sormas.app.util.SoftKeyboardHelper;

/**
 * TODO SettingsFragment should probably not be a BaseLandingFragment, but a BaseFragment
 */
public class SettingsFragment extends BaseLandingFragment {

    private final int SHOW_DEV_OPTIONS_CLICK_LIMIT = 5;

    private FragmentSettingsLayoutBinding binding;
    private int versionClickedCount;

    protected boolean isShowDevOptions() { return versionClickedCount >= SHOW_DEV_OPTIONS_CLICK_LIMIT; }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        super.onCreateView(inflater, container, savedInstanceState);
        binding = (FragmentSettingsLayoutBinding)rootBinding;

        binding.settingsServerUrl.setValue(ConfigProvider.getServerRestUrl());
        binding.changePin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changePIN(v);
            }
        });
        binding.resynchronizeData.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                repullData();
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

        binding.sormasVersion.setText("SORMAS " + InfoProvider.get().getVersion());
        binding.sormasVersion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                versionClickedCount++;
                if (isShowDevOptions()) {
                    binding.settingsServerUrl.setVisibility(View.VISIBLE);
                    if (ConfigProvider.getUser() != null) {
                        binding.logout.setVisibility(View.VISIBLE);
                    }
                    getBaseLandingActivity().getSaveMenu().setVisible(true);
                }
            }
        });

        return binding.getRoot();
    }

    @Override
    public int getRootLandingLayout() {
        return R.layout.fragment_settings_layout;
    }

    @Override
    public void onResume() {
        super.onResume();

        boolean hasUser = ConfigProvider.getUser() != null;
        binding.settingsServerUrl.setVisibility(isShowDevOptions() ? View.VISIBLE : View.GONE);
        binding.changePin.setVisibility(hasUser ? View.VISIBLE : View.GONE);
        binding.resynchronizeData.setVisibility(hasUser ? View.VISIBLE : View.GONE);
        binding.showSyncLog.setVisibility(hasUser ? View.VISIBLE : View.GONE);
        binding.logout.setVisibility(hasUser && isShowDevOptions() ? View.VISIBLE : View.GONE);
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

    private void repullData() {
        final ConfirmationDialog confirmationDialog = new ConfirmationDialog(getActivity(),
                R.string.heading_confirmation_dialog,
                R.string.heading_sub_confirmation_notification_dialog_resync);

        confirmationDialog.setPositiveCallback(new Callback() {
            @Override
            public void call() {
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

        confirmationDialog.show();
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

    @Override
    public boolean isShowSaveAction() {
        return isShowDevOptions();
    }
}
