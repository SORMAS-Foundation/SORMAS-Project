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

import java.util.ArrayList;
import java.util.List;

import de.symeda.sormas.api.utils.InfoProvider;
import de.symeda.sormas.app.BaseLandingFragment;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.caze.Case;
import de.symeda.sormas.app.backend.common.AbstractDomainObject;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.config.ConfigProvider;
import de.symeda.sormas.app.backend.contact.Contact;
import de.symeda.sormas.app.backend.event.Event;
import de.symeda.sormas.app.backend.event.EventParticipant;
import de.symeda.sormas.app.backend.person.Person;
import de.symeda.sormas.app.backend.report.WeeklyReport;
import de.symeda.sormas.app.backend.report.WeeklyReportEntry;
import de.symeda.sormas.app.backend.sample.Sample;
import de.symeda.sormas.app.backend.sample.SampleTest;
import de.symeda.sormas.app.backend.task.Task;
import de.symeda.sormas.app.backend.visit.Visit;
import de.symeda.sormas.app.component.dialog.ConfirmationDialog;
import de.symeda.sormas.app.component.dialog.ConfirmationInputDialog;
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
                openSyncLog();
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
        if (SynchronizeDataAsync.hasAnyUnsynchronizedData()) {
            final ConfirmationInputDialog unsynchronizedChangesDialog = new ConfirmationInputDialog(getActivity(),
                    R.string.alert_title_unsynchronized_changes,
                    R.string.confirmation_unsynchronized_changes, getString(R.string.confirmation_resync));

            unsynchronizedChangesDialog.setPositiveCallback(new Callback() {
                @Override
                public void call() {
                    unsynchronizedChangesDialog.dismiss();
                    showRepullDataConfirmationDialog();
                }
            });

            unsynchronizedChangesDialog.show();
        } else {
            showRepullDataConfirmationDialog();
        }
    }

    private void showRepullDataConfirmationDialog() {
        final ConfirmationDialog confirmationDialog = new ConfirmationDialog(getActivity(),
                R.string.heading_confirmation_dialog,
                R.string.heading_sub_confirmation_notification_dialog_resync);

        confirmationDialog.setPositiveCallback(new Callback() {
            @Override
            public void call() {
                confirmationDialog.dismiss();

                // Collect unsynchronized changes
                final List<Case> modifiedCases = DatabaseHelper.getCaseDao().getModifiedEntities();
                final List<Contact> modifiedContacts = DatabaseHelper.getContactDao().getModifiedEntities();
                final List<Person> modifiedPersons = DatabaseHelper.getPersonDao().getModifiedEntities();
                final List<Event> modifiedEvents = DatabaseHelper.getEventDao().getModifiedEntities();
                final List<EventParticipant> modifiedEventParticipants = DatabaseHelper.getEventParticipantDao().getModifiedEntities();
                final List<Sample> modifiedSamples = DatabaseHelper.getSampleDao().getModifiedEntities();
                final List<Visit> modifiedVisits = DatabaseHelper.getVisitDao().getModifiedEntities();

                getBaseActivity().synchronizeData(SynchronizeDataAsync.SyncMode.CompleteAndRepull,
                        true, true, null,
                        new Callback() {
                            @Override
                            public void call() {
                                // Add deleted entities that had unsynchronized changes to sync log
                                for (Case caze : modifiedCases) {
                                    if (DatabaseHelper.getCaseDao().queryUuidReference(caze.getUuid()) == null) {
                                        DatabaseHelper.getSyncLogDao().createWithParentStack(caze.toString(),
                                                getResources().getString(R.string.synclog_resync));
                                    }
                                }
                                for (Contact contact : modifiedContacts) {
                                    if (DatabaseHelper.getContactDao().queryUuidReference(contact.getUuid()) == null) {
                                        DatabaseHelper.getSyncLogDao().createWithParentStack(contact.toString(),
                                                getResources().getString(R.string.synclog_resync));
                                    }
                                }
                                for (Person person : modifiedPersons) {
                                    if (DatabaseHelper.getPersonDao().queryUuidReference(person.getUuid()) == null) {
                                        DatabaseHelper.getSyncLogDao().createWithParentStack(person.toString(),
                                                getResources().getString(R.string.synclog_resync));
                                    }
                                }
                                for (Event event : modifiedEvents) {
                                    if (DatabaseHelper.getEventDao().queryUuidReference(event.getUuid()) == null) {
                                        DatabaseHelper.getSyncLogDao().createWithParentStack(event.toString(),
                                                getResources().getString(R.string.synclog_resync));
                                    }
                                }
                                for (EventParticipant eventParticipant : modifiedEventParticipants) {
                                    if (DatabaseHelper.getEventParticipantDao().queryUuidReference(eventParticipant.getUuid()) == null) {
                                        DatabaseHelper.getSyncLogDao().createWithParentStack(eventParticipant.toString(),
                                                getResources().getString(R.string.synclog_resync));
                                    }
                                }
                                for (Sample sample : modifiedSamples) {
                                    if (DatabaseHelper.getSampleDao().queryUuidReference(sample.getUuid()) == null) {
                                        DatabaseHelper.getSyncLogDao().createWithParentStack(sample.toString(),
                                                getResources().getString(R.string.synclog_resync));
                                    }
                                }
                                for (Visit visit : modifiedVisits) {
                                    if (DatabaseHelper.getVisitDao().queryUuidReference(visit.getUuid()) == null) {
                                        DatabaseHelper.getSyncLogDao().createWithParentStack(visit.toString(),
                                                getResources().getString(R.string.synclog_resync));
                                    }
                                }
                            }
                        },
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

    public void openSyncLog() {
        SyncLogDialog syncLogDialog = new SyncLogDialog(this.getActivity());
        syncLogDialog.show();
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
