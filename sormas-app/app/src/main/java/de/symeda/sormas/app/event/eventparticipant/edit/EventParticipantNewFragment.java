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

import java.util.List;

import de.symeda.sormas.api.person.Sex;
import de.symeda.sormas.app.BaseEditFragment;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.event.EventParticipant;
import de.symeda.sormas.app.backend.location.Location;
import de.symeda.sormas.app.component.Item;
import de.symeda.sormas.app.databinding.FragmentEventParticipantNewLayoutBinding;
import de.symeda.sormas.app.util.DataUtils;
import de.symeda.sormas.app.util.InfrastructureDaoHelper;
import de.symeda.sormas.app.util.InfrastructureFieldsDependencyHandler;

public class EventParticipantNewFragment extends BaseEditFragment<FragmentEventParticipantNewLayoutBinding, EventParticipant, EventParticipant> {

	public static final String TAG = EventParticipantNewFragment.class.getSimpleName();

	private EventParticipant record;
	private List<Item> initialRegions;
	private List<Item> initialDistricts;

	private boolean rapidEntry;

	public static EventParticipantNewFragment newInstance(EventParticipant activityRootData, boolean rapidEntry) {
		EventParticipantNewFragment eventParticipantNewFragment = newInstance(EventParticipantNewFragment.class, null, activityRootData);
		eventParticipantNewFragment.rapidEntry = rapidEntry;
		return eventParticipantNewFragment;
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

		initialRegions = InfrastructureDaoHelper.loadRegionsByServerCountry();
		initialDistricts = InfrastructureDaoHelper.loadDistricts(record.getResponsibleRegion());
	}

	@Override
	public void onLayoutBinding(FragmentEventParticipantNewLayoutBinding contentBinding) {
		contentBinding.setData(record);
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

		contentBinding.eventParticipantRapidEntry.setValue(rapidEntry);
		contentBinding.eventParticipantRapidEntry.addValueChangedListener(field -> {
			rapidEntry = (Boolean) contentBinding.eventParticipantRapidEntry.getValue();
		});
	}

	@Override
	public void onAfterLayoutBinding(FragmentEventParticipantNewLayoutBinding contentBinding) {
		List<Item> sexList = DataUtils.getEnumItems(Sex.class, true);
		contentBinding.eventParticipantSex.initializeSpinner(sexList);
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

	public boolean isRapidEntry() {
		return rapidEntry;
	}
}
