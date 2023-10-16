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

package de.symeda.sormas.app.event.eventparticipant.read;

import java.util.List;

import android.content.Context;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;

import de.symeda.sormas.api.event.EventParticipantReferenceDto;
import de.symeda.sormas.api.feature.FeatureType;
import de.symeda.sormas.api.feature.FeatureTypeProperty;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.app.BaseReadActivity;
import de.symeda.sormas.app.BaseReadFragment;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.config.ConfigProvider;
import de.symeda.sormas.app.backend.event.Event;
import de.symeda.sormas.app.backend.event.EventEditAuthorization;
import de.symeda.sormas.app.backend.event.EventParticipant;
import de.symeda.sormas.app.component.menu.PageMenuItem;
import de.symeda.sormas.app.event.eventparticipant.EventParticipantSection;
import de.symeda.sormas.app.event.eventparticipant.edit.EventParticipantEditActivity;
import de.symeda.sormas.app.person.read.PersonReadFragment;
import de.symeda.sormas.app.util.Bundler;

public class EventParticipantReadActivity extends BaseReadActivity<EventParticipant> {

	private String eventUuid;

	public static void startActivity(Context context, String rootUuid, String eventUuid) {
		BaseReadActivity.startActivity(context, EventParticipantReadActivity.class, buildBundle(rootUuid, eventUuid));
	}

	public static Bundler buildBundle(String rootUuid, String eventUuid) {
		return buildBundle(rootUuid, 0).setEventUuid(eventUuid);
	}

	@Override
	protected void onCreateInner(Bundle savedInstanceState) {
		super.onCreateInner(savedInstanceState);
		eventUuid = new Bundler(savedInstanceState).getEventUuid();
	}

	@Override
	public void onSaveInstanceState(@NonNull Bundle outState) {
		super.onSaveInstanceState(outState);
		new Bundler(outState).setEventUuid(eventUuid);
	}

	@Override
	protected EventParticipant queryRootEntity(String recordUuid) {
		return DatabaseHelper.getEventParticipantDao().queryUuid(recordUuid);
	}

	@Override
	public List<PageMenuItem> getPageMenuData() {
		List<PageMenuItem> menuItems = PageMenuItem.fromEnum(EventParticipantSection.values(), getContext());
		Event event = DatabaseHelper.getEventDao().queryUuid(eventUuid);
		if (!ConfigProvider.hasUserRight(UserRight.IMMUNIZATION_VIEW)
			|| DatabaseHelper.getFeatureConfigurationDao().isPropertyValueTrue(FeatureType.IMMUNIZATION_MANAGEMENT, FeatureTypeProperty.REDUCED)) {
			menuItems.set(EventParticipantSection.IMMUNIZATIONS.ordinal(), null);
		}
		if (!ConfigProvider.hasUserRight(UserRight.IMMUNIZATION_VIEW)
			|| !DatabaseHelper.getFeatureConfigurationDao().isPropertyValueTrue(FeatureType.IMMUNIZATION_MANAGEMENT, FeatureTypeProperty.REDUCED)
			|| event.getDisease() == null) {
			menuItems.set(EventParticipantSection.VACCINATIONS.ordinal(), null);
		}
		return menuItems;
	}

	@Override
	protected BaseReadFragment buildReadFragment(PageMenuItem menuItem, EventParticipant activityRootData) {

		EventParticipantSection section = EventParticipantSection.fromOrdinal(menuItem.getPosition());
		BaseReadFragment fragment;
		switch (section) {

		case EVENT_PARTICIPANT_INFO:
			fragment = EventParticipantReadFragment.newInstance(activityRootData);
			break;
		case PERSON_INFO:
			fragment = PersonReadFragment.newInstance(activityRootData);
			break;
		case IMMUNIZATIONS:
			fragment = EventParticipantReadImmunizationListFragment.newInstance(activityRootData);
			break;
		case VACCINATIONS:
			fragment = EventParticipantReadVaccinationListFragment.newInstance(activityRootData);
			break;
		default:
			throw new IndexOutOfBoundsException(DataHelper.toStringNullable(section));
		}
		return fragment;
	}

	@Override
	public void goToEditView() {
		EventParticipantSection section = EventParticipantSection.fromOrdinal(getActivePage().getPosition());

		EventParticipantEditActivity.startActivity(this, getRootUuid(), eventUuid, section);
	}

	@Override
	public Enum getPageStatus() {
		return null;
	}

	@Override
	protected int getActivityTitle() {
		return R.string.heading_event_participant;
	}

	@Override
	protected void processActionbarMenu() {
		super.processActionbarMenu();

		final MenuItem editMenu = getEditMenu();

		final EventParticipant selectedEventParticipant =
			DatabaseHelper.getEventParticipantDao().getByReferenceDto(new EventParticipantReferenceDto(getRootUuid()));

		if (editMenu != null) {
			editMenu.setVisible(
				ConfigProvider.hasUserRight(UserRight.EVENTPARTICIPANT_EDIT)
					&& EventEditAuthorization.isEventParticipantEditAllowed(selectedEventParticipant));
		}
	}
}
