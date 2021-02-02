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

package de.symeda.sormas.app.event.edit;

import static de.symeda.sormas.app.core.notification.NotificationType.ERROR;

import java.util.List;

import android.view.View;
import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.event.DiseaseTransmissionMode;
import de.symeda.sormas.api.event.EventDto;
import de.symeda.sormas.api.event.EventInvestigationStatus;
import de.symeda.sormas.api.event.EventSourceType;
import de.symeda.sormas.api.event.EventStatus;
import de.symeda.sormas.api.event.InstitutionalPartnerType;
import de.symeda.sormas.api.event.MeansOfTransport;
import de.symeda.sormas.api.event.RiskLevel;
import de.symeda.sormas.api.event.TypeOfPlace;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.utils.ValidationException;
import de.symeda.sormas.api.utils.fieldaccess.UiFieldAccessCheckers;
import de.symeda.sormas.api.utils.fieldvisibility.FieldVisibilityCheckers;
import de.symeda.sormas.app.BaseActivity;
import de.symeda.sormas.app.BaseEditFragment;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.event.Event;
import de.symeda.sormas.app.backend.location.Location;
import de.symeda.sormas.app.component.Item;
import de.symeda.sormas.app.component.dialog.LocationDialog;
import de.symeda.sormas.app.component.validation.FragmentValidator;
import de.symeda.sormas.app.core.notification.NotificationHelper;
import de.symeda.sormas.app.databinding.FragmentEventEditLayoutBinding;
import de.symeda.sormas.app.util.DataUtils;
import de.symeda.sormas.app.util.DiseaseConfigurationCache;

public class EventEditFragment extends BaseEditFragment<FragmentEventEditLayoutBinding, Event, Event> {

	private Event record;

	// Enum lists

	private List<Item> diseaseList;
	private List<Item> typeOfPlaceList;
	private List<Item> srcTypeList;
	private List<Item> srcInstitutionalPartnerTypeList;
	private List<Item> meansOfTransportList;
	private List<Item> diseaseTransmissionModeList;
	private boolean isMultiDayEvent;

	public static EventEditFragment newInstance(Event activityRootData) {
		EventEditFragment fragment = newInstanceWithFieldCheckers(
			EventEditFragment.class,
			null,
			activityRootData,
			new FieldVisibilityCheckers(),
			UiFieldAccessCheckers.getDefault(activityRootData.isPseudonymized()));

		fragment.isMultiDayEvent = activityRootData.getEndDate() != null;

		return fragment;
	}

	private void setUpControlListeners(final FragmentEventEditLayoutBinding contentBinding) {
		contentBinding.eventEventLocation.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				openAddressPopup(contentBinding);
			}
		});
	}

	private void openAddressPopup(final FragmentEventEditLayoutBinding contentBinding) {
		final Location location = record.getEventLocation();
		final Location locationClone = (Location) location.clone();
		final LocationDialog locationDialog = new LocationDialog(BaseActivity.getActiveActivity(), locationClone, false, null);
		locationDialog.show();
		locationDialog.setRegionAndDistrictRequired(true);
		locationDialog.setFacilityFieldsVisible(record.getTypeOfPlace() == TypeOfPlace.FACILITY, true);
		locationDialog.setPositiveCallback(() -> {
			try {
				FragmentValidator.validate(getContext(), locationDialog.getContentBinding());
				contentBinding.eventEventLocation.setValue(locationClone);
				record.setEventLocation(locationClone);
				locationDialog.dismiss();
			} catch (ValidationException e) {
				NotificationHelper.showDialogNotification(locationDialog, ERROR, e.getMessage());
			}
		});
	}

	// Overrides

	@Override
	protected String getSubHeadingTitle() {
		return getResources().getString(R.string.caption_event_information);
	}

	@Override
	public Event getPrimaryData() {
		return record;
	}

	@Override
	protected void prepareFragmentData() {
		record = getActivityRootData();

		List<Disease> diseases = DiseaseConfigurationCache.getInstance().getAllDiseases(true, true, true);
		diseaseList = DataUtils.toItems(diseases);
		if (record.getDisease() != null && !diseases.contains(record.getDisease())) {
			diseaseList.add(DataUtils.toItem(record.getDisease()));
		}
		typeOfPlaceList = DataUtils.getEnumItems(TypeOfPlace.class, true);
		srcTypeList = DataUtils.getEnumItems(EventSourceType.class, true);
		srcInstitutionalPartnerTypeList = DataUtils.getEnumItems(InstitutionalPartnerType.class, true);
		meansOfTransportList = DataUtils.getEnumItems(MeansOfTransport.class, true);
		diseaseTransmissionModeList = DataUtils.getEnumItems(DiseaseTransmissionMode.class, true);
	}

	@Override
	public void onLayoutBinding(FragmentEventEditLayoutBinding contentBinding) {
		setUpControlListeners(contentBinding);

		contentBinding.setData(record);
		contentBinding.setEventStatusClass(EventStatus.class);
		contentBinding.setEventInvestigationStatusClass(EventInvestigationStatus.class);
		contentBinding.setRiskLevelClass(RiskLevel.class);
		contentBinding.setIsMultiDayEvent(isMultiDayEvent);

	}

	@Override
	public void onAfterLayoutBinding(FragmentEventEditLayoutBinding contentBinding) {
		// Initialize ControlSpinnerFields
		contentBinding.eventDisease.initializeSpinner(diseaseList);
		contentBinding.eventTypeOfPlace.initializeSpinner(typeOfPlaceList);
		contentBinding.eventSrcType.initializeSpinner(srcTypeList);
		contentBinding.eventSrcInstitutionalPartnerType.initializeSpinner(srcInstitutionalPartnerTypeList);
		contentBinding.eventMeansOfTransport.initializeSpinner(meansOfTransportList);
		contentBinding.eventDiseaseTransmissionMode.initializeSpinner(diseaseTransmissionModeList);

		// Initialize ControlDateFields
		contentBinding.eventStartDate.initializeDateField(getFragmentManager());
		String startDateCaption = Boolean.TRUE.equals(contentBinding.eventMultiDayEvent.getValue())
			? I18nProperties.getPrefixCaption(EventDto.I18N_PREFIX, EventDto.START_DATE)
			: I18nProperties.getCaption(Captions.singleDayEventDate);
		contentBinding.eventStartDate.setCaption(startDateCaption);

		contentBinding.eventEndDate.initializeDateField(getFragmentManager());

		contentBinding.eventEventInvestigationStartDate.initializeDateField(getFragmentManager());
		contentBinding.eventEventInvestigationEndDate.initializeDateField(getFragmentManager());
		contentBinding.eventTravelDate.initializeDateField(getFragmentManager());

		setFieldVisibilitiesAndAccesses(EventDto.class, contentBinding.mainContent);
	}

	@Override
	public int getEditLayout() {
		return R.layout.fragment_event_edit_layout;
	}
}
