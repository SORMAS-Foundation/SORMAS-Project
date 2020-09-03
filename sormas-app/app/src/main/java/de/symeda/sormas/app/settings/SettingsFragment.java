/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2018 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package de.symeda.sormas.app.settings;

import static de.symeda.sormas.app.core.notification.NotificationType.ERROR;

import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import de.symeda.sormas.api.Language;
import de.symeda.sormas.api.utils.InfoProvider;
import de.symeda.sormas.app.BaseLandingFragment;
import de.symeda.sormas.app.LocaleManager;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.caze.Case;
import de.symeda.sormas.app.backend.common.DaoException;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.config.ConfigProvider;
import de.symeda.sormas.app.backend.contact.Contact;
import de.symeda.sormas.app.backend.event.Event;
import de.symeda.sormas.app.backend.event.EventParticipant;
import de.symeda.sormas.app.backend.person.Person;
import de.symeda.sormas.app.backend.sample.Sample;
import de.symeda.sormas.app.backend.user.User;
import de.symeda.sormas.app.backend.visit.Visit;
import de.symeda.sormas.app.component.dialog.ConfirmationDialog;
import de.symeda.sormas.app.component.dialog.ConfirmationInputDialog;
import de.symeda.sormas.app.component.dialog.SyncLogDialog;
import de.symeda.sormas.app.core.adapter.multiview.EnumMapDataBinderAdapter;
import de.symeda.sormas.app.core.notification.NotificationHelper;
import de.symeda.sormas.app.databinding.FragmentSettingsLayoutBinding;
import de.symeda.sormas.app.login.EnterPinActivity;
import de.symeda.sormas.app.login.LoginActivity;
import de.symeda.sormas.app.rest.SynchronizeDataAsync;
import de.symeda.sormas.app.util.Callback;
import de.symeda.sormas.app.util.DataUtils;
import de.symeda.sormas.app.util.SoftKeyboardHelper;

/**
 * TODO SettingsFragment should probably not be a BaseLandingFragment, but a BaseFragment
 */
public class SettingsFragment extends BaseLandingFragment {

	private final int SHOW_DEV_OPTIONS_CLICK_LIMIT = 5;

	private FragmentSettingsLayoutBinding binding;
	private int versionClickedCount;

	protected boolean isShowDevOptions() {
		return versionClickedCount >= SHOW_DEV_OPTIONS_CLICK_LIMIT;
	}

	protected boolean hasServerUrl() {
		return ConfigProvider.getServerRestUrl() != null;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		binding = (FragmentSettingsLayoutBinding) rootBinding;

		binding.settingsServerUrl.setValue(ConfigProvider.getServerRestUrl());
		binding.changePin.setOnClickListener(v -> changePIN());
		binding.resynchronizeData.setOnClickListener(v -> repullData());
		binding.showSyncLog.setOnClickListener(v -> openSyncLog());
		binding.logout.setOnClickListener(v -> logout());

		binding.sormasVersion.setText("SORMAS " + InfoProvider.get().getVersion());
		binding.sormasVersion.setOnClickListener(v -> {
			versionClickedCount++;
			if (isShowDevOptions()) {
				binding.settingsServerUrl.setVisibility(View.VISIBLE);
				if (ConfigProvider.getUser() != null) {
					binding.logout.setVisibility(View.VISIBLE);
				}
				getBaseLandingActivity().getSaveMenu().setVisible(true);
			}
		});

		binding.setData(ConfigProvider.getUser());
		binding.userLanguage.initializeSpinner(DataUtils.getEnumItems(Language.class, true));
		binding.userLanguage.addValueChangedListener(e -> {
			if (!(e.getValue() == null
				&& Language.fromLocaleString(ConfigProvider.getServerLocale()).equals(LocaleManager.getLanguagePref(getContext())))) {
				if (!LocaleManager.getLanguagePref(getContext()).equals(e.getValue())) {
					try {
						Language newLanguage =
							e.getValue() != null ? (Language) e.getValue() : Language.fromLocaleString(ConfigProvider.getServerLocale());
						User user = binding.getData();
						if (user != null) {
							DatabaseHelper.getUserDao().saveAndSnapshot(user);
						}
						if (newLanguage != null) {
							((SettingsActivity) getActivity()).setNewLocale((AppCompatActivity) getActivity(), newLanguage);
						}
					} catch (DaoException ex) {
						NotificationHelper
							.showNotification((SettingsActivity) getActivity(), ERROR, getString(R.string.message_language_change_unsuccessful));
					}
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
		binding.settingsServerUrlInfo.setVisibility(!hasServerUrl() ? View.VISIBLE : View.GONE);
		binding.settingsServerUrl.setVisibility(!hasServerUrl() || isShowDevOptions() ? View.VISIBLE : View.GONE);
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

	public void changePIN() {
		Intent intent = new Intent(getActivity(), EnterPinActivity.class);
		intent.putExtra(EnterPinActivity.CALLED_FROM_SETTINGS, true);
		startActivity(intent);
	}

	private void repullData() {
		checkAndShowUnsynchronizedChangesDialog(() -> showRepullDataConfirmationDialog(), "SYNC");
	}

	private void checkAndShowUnsynchronizedChangesDialog(Callback confirmedCallback, String wordToType) {
		if (SynchronizeDataAsync.hasAnyUnsynchronizedData()) {
			final ConfirmationInputDialog unsynchronizedChangesDialog = new ConfirmationInputDialog(
				getActivity(),
				R.string.heading_unsynchronized_changes,
				R.string.message_unsynchronized_changes_confirmation,
				wordToType);

			unsynchronizedChangesDialog.setPositiveCallback(confirmedCallback::call);

			unsynchronizedChangesDialog.show();
		} else {
			confirmedCallback.call();
		}
	}

	private void showRepullDataConfirmationDialog() {
		final ConfirmationDialog confirmationDialog =
			new ConfirmationDialog(getActivity(), R.string.heading_confirmation_dialog, R.string.info_resync_duration);

		confirmationDialog.setPositiveCallback(() -> {
			// Collect unsynchronized changes
			final List<Case> modifiedCases = DatabaseHelper.getCaseDao().getModifiedEntities();
			final List<Contact> modifiedContacts = DatabaseHelper.getContactDao().getModifiedEntities();
			final List<Person> modifiedPersons = DatabaseHelper.getPersonDao().getModifiedEntities();
			final List<Event> modifiedEvents = DatabaseHelper.getEventDao().getModifiedEntities();
			final List<EventParticipant> modifiedEventParticipants = DatabaseHelper.getEventParticipantDao().getModifiedEntities();
			final List<Sample> modifiedSamples = DatabaseHelper.getSampleDao().getModifiedEntities();
			final List<Visit> modifiedVisits = DatabaseHelper.getVisitDao().getModifiedEntities();

			getBaseActivity().synchronizeData(SynchronizeDataAsync.SyncMode.CompleteAndRepull, true, true, null, new Callback() {

				@Override
				public void call() {
					// Add deleted entities that had unsynchronized changes to sync log
					for (Case caze : modifiedCases) {
						if (DatabaseHelper.getCaseDao().queryUuidReference(caze.getUuid()) == null) {
							DatabaseHelper.getSyncLogDao()
								.createWithParentStack(caze.toString(), getResources().getString(R.string.caption_changed_data_lost));
						}
					}
					for (Contact contact : modifiedContacts) {
						if (DatabaseHelper.getContactDao().queryUuidReference(contact.getUuid()) == null) {
							DatabaseHelper.getSyncLogDao()
								.createWithParentStack(contact.toString(), getResources().getString(R.string.caption_changed_data_lost));
						}
					}
					for (Person person : modifiedPersons) {
						if (DatabaseHelper.getPersonDao().queryUuidReference(person.getUuid()) == null) {
							DatabaseHelper.getSyncLogDao()
								.createWithParentStack(person.toString(), getResources().getString(R.string.caption_changed_data_lost));
						}
					}
					for (Event event : modifiedEvents) {
						if (DatabaseHelper.getEventDao().queryUuidReference(event.getUuid()) == null) {
							DatabaseHelper.getSyncLogDao()
								.createWithParentStack(event.toString(), getResources().getString(R.string.caption_changed_data_lost));
						}
					}
					for (EventParticipant eventParticipant : modifiedEventParticipants) {
						if (DatabaseHelper.getEventParticipantDao().queryUuidReference(eventParticipant.getUuid()) == null) {
							DatabaseHelper.getSyncLogDao()
								.createWithParentStack(eventParticipant.toString(), getResources().getString(R.string.caption_changed_data_lost));
						}
					}
					for (Sample sample : modifiedSamples) {
						if (DatabaseHelper.getSampleDao().queryUuidReference(sample.getUuid()) == null) {
							DatabaseHelper.getSyncLogDao()
								.createWithParentStack(sample.toString(), getResources().getString(R.string.caption_changed_data_lost));
						}
					}
					for (Visit visit : modifiedVisits) {
						if (DatabaseHelper.getVisitDao().queryUuidReference(visit.getUuid()) == null) {
							DatabaseHelper.getSyncLogDao()
								.createWithParentStack(visit.toString(), getResources().getString(R.string.caption_changed_data_lost));
						}
					}
				}
			}, new Callback() {

				@Override
				public void call() {
					DatabaseHelper.clearTables(false);
				}
			});
		});

		confirmationDialog.show();
	}

	public void openSyncLog() {
		SyncLogDialog syncLogDialog = new SyncLogDialog(this.getActivity());
		syncLogDialog.show();
	}

	public void logout() {
		checkAndShowUnsynchronizedChangesDialog(() -> {
			ConfigProvider.clearUserLogin();
			ConfigProvider.clearPin();
			Intent intent = new Intent(getActivity(), LoginActivity.class);
			startActivity(intent);
		}, "LOGOUT");
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
		return !hasServerUrl() || isShowDevOptions();
	}
}
