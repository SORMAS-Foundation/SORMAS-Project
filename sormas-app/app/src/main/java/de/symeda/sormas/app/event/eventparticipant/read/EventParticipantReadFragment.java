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

import static android.view.View.GONE;

import android.os.Bundle;
import android.view.View;

import de.symeda.sormas.api.utils.fieldvisibility.FieldVisibilityCheckers;
import de.symeda.sormas.app.BaseReadFragment;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.event.EventParticipant;
import de.symeda.sormas.app.caze.read.CaseReadActivity;
import de.symeda.sormas.app.databinding.FragmentEventParticipantReadLayoutBinding;
import de.symeda.sormas.app.databinding.FragmentPersonReadLayoutBinding;
import de.symeda.sormas.app.person.edit.PersonEditFragment;
import de.symeda.sormas.app.person.read.PersonReadFragment;
import de.symeda.sormas.app.util.InfrastructureHelper;

public class EventParticipantReadFragment extends BaseReadFragment<FragmentEventParticipantReadLayoutBinding, EventParticipant, EventParticipant> {

	private EventParticipant record;

	// Static methods

	public static EventParticipantReadFragment newInstance(EventParticipant activityRootData) {
		return newInstanceWithFieldCheckers(
			EventParticipantReadFragment.class,
			null,
			activityRootData,
			FieldVisibilityCheckers.withDisease(activityRootData.getEvent().getDisease()),
			null);
	}

	// Instance methods

	private void setUpFieldVisibilities(FragmentEventParticipantReadLayoutBinding contentBinding) {
		if (record.getResultingCaseUuid() == null || DatabaseHelper.getCaseDao().queryUuidBasic(record.getResultingCaseUuid()) == null) {
			contentBinding.eventParticipantButtonsPanel.setVisibility(GONE);
		}
	}

	private void setUpControlListeners(FragmentEventParticipantReadLayoutBinding contentBinding) {
		contentBinding.openEventPersonCase.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				CaseReadActivity.startActivity(getActivity(), record.getResultingCaseUuid(), true);
			}
		});
	}

	private void setUpPersonFragmentFieldVisibilities(FragmentPersonReadLayoutBinding contentBinding) {
		PersonReadFragment.setUpFieldVisibilities(this, contentBinding, record.getEvent());
		InfrastructureHelper
			.initializeHealthFacilityDetailsFieldVisibility(contentBinding.personOccupationFacility, contentBinding.personOccupationFacilityDetails);
		PersonEditFragment.initializeCauseOfDeathDetailsFieldVisibility(
			contentBinding.personCauseOfDeath,
			contentBinding.personCauseOfDeathDisease,
			contentBinding.personCauseOfDeathDetails);
	}

	// Overrides

	@Override
	protected void prepareFragmentData(Bundle savedInstanceState) {
		record = getActivityRootData();
	}

	@Override
	public void onLayoutBinding(FragmentEventParticipantReadLayoutBinding contentBinding) {
		setUpControlListeners(contentBinding);

		contentBinding.setData(record);
	}

	@Override
	public void onAfterLayoutBinding(FragmentEventParticipantReadLayoutBinding contentBinding) {
		setUpFieldVisibilities(contentBinding);

		if (contentBinding.eventParticipantPersonLayout != null) {
			setUpPersonFragmentFieldVisibilities(contentBinding.eventParticipantPersonLayout);
		}
	}

	@Override
	protected String getSubHeadingTitle() {
		return getResources().getString(R.string.caption_person_involved);
	}

	@Override
	public EventParticipant getPrimaryData() {
		return record;
	}

	@Override
	public int getReadLayout() {
		return R.layout.fragment_event_participant_read_layout;
	}
}
