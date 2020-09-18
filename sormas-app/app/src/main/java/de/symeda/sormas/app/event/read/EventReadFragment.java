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

import de.symeda.sormas.api.event.EventDto;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.api.utils.fieldvisibility.FieldVisibilityCheckers;
import de.symeda.sormas.app.BaseReadFragment;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.config.ConfigProvider;
import de.symeda.sormas.app.backend.event.Event;
import de.symeda.sormas.app.backend.event.EventEditAuthorization;
import de.symeda.sormas.app.core.FieldHelper;
import de.symeda.sormas.app.databinding.FragmentEventReadLayoutBinding;
import de.symeda.sormas.app.util.AppFieldAccessCheckers;

public class EventReadFragment extends BaseReadFragment<FragmentEventReadLayoutBinding, Event, Event> {

	private Event record;

	public static EventReadFragment newInstance(Event activityRootData) {
		return newInstanceWithFieldCheckers(
			EventReadFragment.class,
			null,
			activityRootData,
			new FieldVisibilityCheckers(),
			AppFieldAccessCheckers
				.withCheckers(EventEditAuthorization.isEventEditAllowed(activityRootData), FieldHelper.createSensitiveDataFieldAccessChecker()));
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
		contentBinding.setParticipantCount(DatabaseHelper.getEventParticipantDao().countByEvent(record).intValue());
	}

	@Override
	protected void onAfterLayoutBinding(FragmentEventReadLayoutBinding contentBinding) {
		super.onAfterLayoutBinding(contentBinding);

		String startDateCaption = Boolean.TRUE.equals(contentBinding.getMultiDayEvent())
			? I18nProperties.getPrefixCaption(EventDto.I18N_PREFIX, EventDto.START_DATE)
			: I18nProperties.getCaption(Captions.singleDayEventDate);

		contentBinding.eventStartDate.setCaption(startDateCaption);

		setFieldVisibilitiesAndAccesses(EventDto.class, contentBinding.mainContent);
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
