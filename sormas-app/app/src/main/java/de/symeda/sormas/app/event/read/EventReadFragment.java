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
import de.symeda.sormas.api.event.EventStatus;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.api.utils.fieldaccess.UiFieldAccessCheckers;
import de.symeda.sormas.api.utils.fieldvisibility.FieldVisibilityCheckers;
import de.symeda.sormas.app.BaseReadFragment;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.config.ConfigProvider;
import de.symeda.sormas.app.backend.event.Event;
import de.symeda.sormas.app.databinding.FragmentEventReadLayoutBinding;

public class EventReadFragment extends BaseReadFragment<FragmentEventReadLayoutBinding, Event, Event> {

	private static final String EVENT_ENTITY = "Event";
	private static final String EVOLUTION_DATE_WITH_STATUS = "eventEvolutionDateWithStatus";
	private static final String EVOLUTION_COMMENT_WITH_STATUS = "eventEvolutionCommentWithStatus";

	private Event record;

	public static EventReadFragment newInstance(Event activityRootData) {
		return newInstanceWithFieldCheckers(
			EventReadFragment.class,
			null,
			activityRootData,
			new FieldVisibilityCheckers(),
			UiFieldAccessCheckers.forSensitiveData(activityRootData.isPseudonymized()));
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

		EventStatus eventStatus = record.getEventStatus();
		// The status will be used to modify the caption of the field
		// However we don't want to have somthing like "Dropped evolution date"
		// So let's ignore the DROPPED status and use the EVENT status instead
		String statusCaption;
		if (eventStatus == EventStatus.DROPPED) {
			statusCaption = I18nProperties.getCaption(EVENT_ENTITY);
		} else {
			statusCaption = I18nProperties.getEnumCaption(eventStatus);
		}

		contentBinding.eventEvolutionDate.setCaption(String.format(
			I18nProperties.getCaption(EVOLUTION_DATE_WITH_STATUS), statusCaption));
		contentBinding.eventEvolutionComment.setCaption(String.format(
			I18nProperties.getCaption(EVOLUTION_COMMENT_WITH_STATUS), statusCaption));
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
