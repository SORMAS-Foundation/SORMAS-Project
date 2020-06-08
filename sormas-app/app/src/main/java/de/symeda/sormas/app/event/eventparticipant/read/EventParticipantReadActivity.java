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

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;

import de.symeda.sormas.app.BaseReadActivity;
import de.symeda.sormas.app.BaseReadFragment;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.event.EventParticipant;
import de.symeda.sormas.app.component.menu.PageMenuItem;
import de.symeda.sormas.app.event.eventparticipant.edit.EventParticipantEditActivity;
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
	protected BaseReadFragment buildReadFragment(PageMenuItem menuItem, EventParticipant activityRootData) {
		return EventParticipantReadFragment.newInstance(activityRootData);
	}

	@Override
	public void goToEditView() {
		EventParticipantEditActivity.startActivity(this, getRootUuid(), eventUuid);
	}

	@Override
	public Enum getPageStatus() {
		return null;
	}

	@Override
	protected int getActivityTitle() {
		return R.string.heading_person_involved;
	}
}
