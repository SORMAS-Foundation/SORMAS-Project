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

package de.symeda.sormas.app.event.read;

import java.util.List;

import android.content.Context;
import android.view.Menu;
import android.view.MenuItem;

import de.symeda.sormas.api.ReferenceDto;
import de.symeda.sormas.api.event.EventReferenceDto;
import de.symeda.sormas.api.event.EventStatus;
import de.symeda.sormas.api.feature.FeatureType;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.app.BaseReadActivity;
import de.symeda.sormas.app.BaseReadFragment;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.event.Event;
import de.symeda.sormas.app.backend.event.EventEditAuthorization;
import de.symeda.sormas.app.component.menu.PageMenuItem;
import de.symeda.sormas.app.event.EventSection;
import de.symeda.sormas.app.event.edit.EventEditActivity;

public class EventReadActivity extends BaseReadActivity<Event> {

	public static final String TAG = EventReadActivity.class.getSimpleName();

	public static void startActivity(Context context, String rootUuid, boolean finishInsteadOfUpNav) {
		BaseReadActivity.startActivity(context, EventReadActivity.class, buildBundle(rootUuid, finishInsteadOfUpNav));
	}

	public static void startActivity(Context context, String rootUuid) {
		BaseReadActivity.startActivity(context, EventReadActivity.class, buildBundle(rootUuid));
	}

	@Override
	protected Event queryRootEntity(String recordUuid) {
		return DatabaseHelper.getEventDao().queryUuid(recordUuid);
	}

	@Override
	public EventStatus getPageStatus() {
		return getStoredRootEntity() == null ? null : getStoredRootEntity().getEventStatus();
	}

	@Override
	public List<PageMenuItem> getPageMenuData() {
		List<PageMenuItem> menuItems = PageMenuItem.fromEnum(EventSection.values(), getContext());
		// Sections must be removed in reverse order
		if (DatabaseHelper.getFeatureConfigurationDao().isFeatureDisabled(FeatureType.TASK_MANAGEMENT)) {
			menuItems.set(EventSection.TASKS.ordinal(), null);
		}
		return menuItems;
	}

	@Override
	protected BaseReadFragment buildReadFragment(PageMenuItem menuItem, Event activityRootData) {
		EventSection section = EventSection.fromOrdinal(menuItem.getPosition());
		BaseReadFragment fragment;
		switch (section) {
		case EVENT_INFO:
			fragment = EventReadFragment.newInstance(activityRootData);
			break;
		case EVENT_PARTICIPANTS:
			fragment = EventReadPersonsInvolvedListFragment.newInstance(activityRootData);
			break;
		case TASKS:
			fragment = EventReadTaskListFragment.newInstance(activityRootData);
			break;
		default:
			throw new IndexOutOfBoundsException(DataHelper.toStringNullable(section));
		}

		return fragment;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		boolean result = super.onCreateOptionsMenu(menu);
		getEditMenu().setTitle(R.string.action_edit_event);
		return result;
	}

	@Override
	protected void processActionbarMenu() {
		super.processActionbarMenu();

		final MenuItem editMenu = getEditMenu();

		final ReferenceDto referenceDto = new EventReferenceDto(getRootUuid());
		final Event selectedEvent = DatabaseHelper.getEventDao().getByReferenceDto(referenceDto);

		if (editMenu != null) {
			if (EventEditAuthorization.isEventEditAllowed(selectedEvent)
				|| (getActiveFragment() != null && getActiveFragment() instanceof EventReadPersonsInvolvedListFragment)) {
				editMenu.setVisible(true);
			} else {
				editMenu.setVisible(false);
			}
		}
	}

	@Override
	protected int getActivityTitle() {
		return R.string.heading_event_read;
	}

	@Override
	public void goToEditView() {
		EventSection section = EventSection.fromOrdinal(getActivePage().getPosition());
		EventEditActivity.startActivity(this, getRootUuid(), section);
	}
}
