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

import static android.view.View.GONE;

import java.util.List;

import android.view.View;
import de.symeda.sormas.api.caze.VaccinationStatus;
import de.symeda.sormas.api.event.EventDto;
import de.symeda.sormas.api.utils.fieldaccess.UiFieldAccessCheckers;
import de.symeda.sormas.api.utils.fieldvisibility.FieldVisibilityCheckers;
import de.symeda.sormas.app.BaseEditFragment;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.config.ConfigProvider;
import de.symeda.sormas.app.backend.event.EventParticipant;
import de.symeda.sormas.app.backend.location.Location;
import de.symeda.sormas.app.caze.edit.CaseNewActivity;
import de.symeda.sormas.app.caze.read.CaseReadActivity;
import de.symeda.sormas.app.component.Item;
import de.symeda.sormas.app.databinding.FragmentEventParticipantEditLayoutBinding;
import de.symeda.sormas.app.util.InfrastructureDaoHelper;
import de.symeda.sormas.app.util.InfrastructureFieldsDependencyHandler;

public class EventParticipantEditFragment extends BaseEditFragment<FragmentEventParticipantEditLayoutBinding, EventParticipant, EventParticipant> {

	public static final String TAG = EventParticipantEditFragment.class.getSimpleName();

	private EventParticipant record;
	private List<Item> initialRegions;
	private List<Item> initialDistricts;

	public static EventParticipantEditFragment newInstance(EventParticipant activityRootData) {
		return newInstanceWithFieldCheckers(
			EventParticipantEditFragment.class,
			null,
			activityRootData,
			new FieldVisibilityCheckers(),
			UiFieldAccessCheckers.getDefault(activityRootData.isPseudonymized(), ConfigProvider.getServerCountryCode()));
	}

	// Instance methods

	private void setUpFieldVisibilities(FragmentEventParticipantEditLayoutBinding contentBinding) {
		setFieldVisibilitiesAndAccesses(EventDto.class, contentBinding.mainContent);

		if (record.getResultingCaseUuid() != null) {
			contentBinding.createCaseFromEventParticipant.setVisibility(GONE);
			if (DatabaseHelper.getCaseDao().queryUuidBasic(record.getResultingCaseUuid()) == null) {
				contentBinding.eventParticipantButtonsPanel.setVisibility(GONE);
			}
		} else {
			contentBinding.openEventParticipantCase.setVisibility(GONE);
		}
	}

	private void setUpControlListeners(FragmentEventParticipantEditLayoutBinding contentBinding) {
		contentBinding.openEventParticipantCase.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				CaseReadActivity.startActivity(getActivity(), record.getResultingCaseUuid(), true);
			}
		});

		contentBinding.createCaseFromEventParticipant.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				CaseNewActivity.startActivityFromEventPerson(getContext(), record);
			}
		});
	}

	// Overrides

	@Override
	protected String getSubHeadingTitle() {
		return getResources().getString(R.string.caption_event_participant);
	}

	@Override
	public EventParticipant getPrimaryData() {
		return record;
	}

	@Override
	protected void prepareFragmentData() {
		record = getActivityRootData();
		initialRegions = InfrastructureDaoHelper.loadRegionsByServerCountry();
		initialDistricts = InfrastructureDaoHelper.loadDistricts(record.getResponsibleRegion());
	}

	@Override
	public void onLayoutBinding(FragmentEventParticipantEditLayoutBinding contentBinding) {
		setUpControlListeners(contentBinding);

		contentBinding.setData(record);
		contentBinding.setVaccinationStatusClass(VaccinationStatus.class);

		InfrastructureFieldsDependencyHandler.instance.initializeRegionFields(
			contentBinding.eventParticipantResponsibleRegion,
			initialRegions,
			record.getResponsibleRegion(),
			contentBinding.eventParticipantResponsibleDistrict,
			initialDistricts,
			record.getResponsibleDistrict(),
			null,
			null,
			null);

		Location eventLocation = record.getEvent().getEventLocation();
		contentBinding.eventParticipantResponsibleRegion.setRequired(eventLocation.getRegion() == null || eventLocation.getDistrict() == null);
		contentBinding.eventParticipantResponsibleDistrict.setRequired(eventLocation.getRegion() == null || eventLocation.getDistrict() == null);
	}

	@Override
	public void onAfterLayoutBinding(FragmentEventParticipantEditLayoutBinding contentBinding) {
		setUpFieldVisibilities(contentBinding);
	}

	@Override
	public int getEditLayout() {
		return R.layout.fragment_event_participant_edit_layout;
	}
}
