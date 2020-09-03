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

package de.symeda.sormas.app.event.eventparticipant.edit;

import de.symeda.sormas.app.BaseEditFragment;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.event.EventParticipant;
import de.symeda.sormas.app.databinding.FragmentEventParticipantNewLayoutBinding;

public class EventParticipantNewFragment extends BaseEditFragment<FragmentEventParticipantNewLayoutBinding, EventParticipant, EventParticipant> {

	public static final String TAG = EventParticipantNewFragment.class.getSimpleName();

	private EventParticipant record;

	public static EventParticipantNewFragment newInstance(EventParticipant activityRootData) {
		return newInstance(EventParticipantNewFragment.class, null, activityRootData);
	}

	// Overrides

	@Override
	protected String getSubHeadingTitle() {
		return getResources().getString(R.string.caption_new_event_participant);
	}

	@Override
	public EventParticipant getPrimaryData() {
		return record;
	}

	@Override
	protected void prepareFragmentData() {
		record = getActivityRootData();
	}

	@Override
	public void onLayoutBinding(FragmentEventParticipantNewLayoutBinding contentBinding) {
		contentBinding.setData(record);
	}

	@Override
	public int getEditLayout() {
		return R.layout.fragment_event_participant_new_layout;
	}

	@Override
	public boolean isShowSaveAction() {
		return true;
	}

	@Override
	public boolean isShowNewAction() {
		return false;
	}
}
