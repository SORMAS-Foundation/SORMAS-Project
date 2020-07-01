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

import android.os.Bundle;

import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.app.BaseReadFragment;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.config.ConfigProvider;
import de.symeda.sormas.app.backend.event.Event;
import de.symeda.sormas.app.databinding.FragmentEventReadLayoutBinding;

public class EventReadFragment extends BaseReadFragment<FragmentEventReadLayoutBinding, Event, Event> {

	private Event record;

	public static EventReadFragment newInstance(Event activityRootData) {
		return newInstance(EventReadFragment.class, null, activityRootData);
	}

	// Overrides

	@Override
	protected void prepareFragmentData(Bundle savedInstanceState) {
		record = getActivityRootData();
	}

	@Override
	public void onLayoutBinding(FragmentEventReadLayoutBinding contentBinding) {
		contentBinding.setData(record);
		contentBinding.setMultiDayEvent(record.getEndDate() != null);
	}

	@Override
	protected void onAfterLayoutBinding(FragmentEventReadLayoutBinding contentBinding) {
		super.onAfterLayoutBinding(contentBinding);

		String startDateCaption = I18nProperties
				.getCaption(Boolean.TRUE.equals(contentBinding.getMultiDayEvent()) ? Captions.Event_startDate : Captions.Event_eventDate);
		contentBinding.eventStartDate.setCaption(startDateCaption);

	}

	@Override
	protected String getSubHeadingTitle() {
		return getResources().getString(R.string.caption_event_information);
	}

	@Override
	public Event getPrimaryData() {
		return record;
	}

	@Override
	public int getReadLayout() {
		return R.layout.fragment_event_read_layout;
	}

	@Override
	public boolean showEditAction() {
		return ConfigProvider.hasUserRight(UserRight.EVENT_EDIT);
	}
}
