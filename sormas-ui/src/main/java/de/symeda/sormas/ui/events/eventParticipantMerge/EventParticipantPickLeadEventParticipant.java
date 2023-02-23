package de.symeda.sormas.ui.events.eventParticipantMerge;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.vaadin.server.ExternalResource;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Link;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.v7.ui.CustomField;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.event.EventDto;
import de.symeda.sormas.api.event.EventParticipantDto;
import de.symeda.sormas.api.event.EventParticipantSelectionDto;
import de.symeda.sormas.api.event.EventSelectionDto;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.infrastructure.district.DistrictDto;
import de.symeda.sormas.api.person.PersonHelper;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.ui.SormasUI;
import de.symeda.sormas.ui.events.EventDataView;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.VaadinUiUtil;

public class EventParticipantPickLeadEventParticipant extends CustomField<List<String>> {

	protected List<EventParticipantDto> eventParticipantDtos;
	private Map<String, EventParticipantDuplicate> eventParticipantSelectionDtos;
	private Map<String, EventParticipantDuplicateGrid> eventParticipantMergeSelectionGrid = new HashMap<>();

	protected VerticalLayout mainLayout;

	public EventParticipantPickLeadEventParticipant(List<EventParticipantDto> eventsFromBothPersons) {
		this.eventParticipantDtos = eventsFromBothPersons;

		initializeGrid();
	}

	@Override
	protected Component initContent() {
		mainLayout = new VerticalLayout();
		mainLayout.setSpacing(true);
		mainLayout.setMargin(false);
		mainLayout.setSizeUndefined();
		mainLayout.setWidth(100, Unit.PERCENTAGE);

		HorizontalLayout infoLayout = new HorizontalLayout();
		infoLayout.addComponent(VaadinUiUtil.createInfoComponent(I18nProperties.getString(Strings.infoPickEventParticipantsForPersonMerge)));
		mainLayout.addComponent(infoLayout);

		eventParticipantMergeSelectionGrid.forEach((key, value) -> {
			mainLayout.addComponent(value.getEventTitle());
			mainLayout.addComponent(value.eventParticipantMergeSelectionGrid);
		});

		return mainLayout;
	}

	@Override
	public Class<? extends List<String>> getType() {
		return (Class<? extends List<String>>) new ArrayList<String>(0).getClass();
	}

	@Override
	public List<String> getValue() {
		List<String> pickedEventParticipants = new ArrayList<>();

		eventParticipantMergeSelectionGrid.forEach((s, eventParticipantDuplicateGrid) -> {
			Set<EventParticipantSelectionDto> selectedRow = eventParticipantDuplicateGrid.getEventParticipantMergeSelectionGrid().getSelectedItems();
			if (!selectedRow.isEmpty()) {
				pickedEventParticipants.add(selectedRow.iterator().next().getEventParticipantUuid());
			}
		});

		return pickedEventParticipants;
	}

	protected void initializeGrid() {

		eventParticipantSelectionDtos = transformDuplicateEventParticipants(eventParticipantDtos);

		eventParticipantSelectionDtos.forEach((key, value) -> {
			List<EventParticipantSelectionDto> eventParticipantSelectionDtos =
				mapEventParticipantToSelection(value.getDuplicateEventParticipantList());
			EventParticipantMergeSelectionGrid newGrid = new EventParticipantMergeSelectionGrid(eventParticipantSelectionDtos);

			EventSelectionDto eventSelectionDto =
				new EventSelectionDto(eventParticipantSelectionDtos.get(0).getEventUuid(), eventParticipantSelectionDtos.get(0).getEventTitle());
			Component eventComponent = buildEventComponent(eventSelectionDto);

			EventParticipantDuplicateGrid eventParticipantDuplicateGrid = new EventParticipantDuplicateGrid();
			eventParticipantDuplicateGrid.setEventTitle(eventComponent);
			eventParticipantDuplicateGrid.setEventParticipantMergeSelectionGrid(newGrid);

			eventParticipantMergeSelectionGrid.put(key, eventParticipantDuplicateGrid);

		});
	}

	private Map<String, EventParticipantDuplicate> transformDuplicateEventParticipants(List<EventParticipantDto> duplicateEventParticipantList) {

		Map<String, EventParticipantDuplicate> duplicateEvParticipantByEvent = new HashMap<>();

		duplicateEventParticipantList.forEach(eventParticipantDto -> {

			if (duplicateEvParticipantByEvent.containsKey(eventParticipantDto.getEvent().getUuid())) {
				duplicateEvParticipantByEvent.get(eventParticipantDto.getEvent().getUuid())
					.getDuplicateEventParticipantList()
					.add(eventParticipantDto);
			} else {
				EventParticipantDuplicate newEventWithDuplicate = new EventParticipantDuplicate();
				List<EventParticipantDto> newDuplicateEventParticipantList = new ArrayList<>();
				newDuplicateEventParticipantList.add(eventParticipantDto);
				newEventWithDuplicate.setDuplicateEventParticipantList(newDuplicateEventParticipantList);
				duplicateEvParticipantByEvent.put(eventParticipantDto.getEvent().getUuid(), newEventWithDuplicate);
			}

		});

		return duplicateEvParticipantByEvent;
	}

	public static class EventParticipantDuplicate {

		private List<EventParticipantDto> duplicateEventParticipantList;

		public List<EventParticipantDto> getDuplicateEventParticipantList() {
			return duplicateEventParticipantList;
		}

		public void setDuplicateEventParticipantList(List<EventParticipantDto> duplicateEventParticipantList) {
			this.duplicateEventParticipantList = duplicateEventParticipantList;
		}
	}

	public static class EventParticipantDuplicateGrid {

		private Component eventTitle;
		private EventParticipantMergeSelectionGrid eventParticipantMergeSelectionGrid;

		public Component getEventTitle() {
			return eventTitle;
		}

		public void setEventTitle(Component eventTitle) {
			this.eventTitle = eventTitle;
		}

		public EventParticipantMergeSelectionGrid getEventParticipantMergeSelectionGrid() {
			return eventParticipantMergeSelectionGrid;
		}

		public void setEventParticipantMergeSelectionGrid(EventParticipantMergeSelectionGrid eventParticipantMergeSelectionGrid) {
			this.eventParticipantMergeSelectionGrid = eventParticipantMergeSelectionGrid;
		}
	}

	private Component buildEventComponent(EventSelectionDto eventSelectionDto) {
		HorizontalLayout eventLayout = new HorizontalLayout();
		eventLayout.setSpacing(true);
		eventLayout.setMargin(false);

		VerticalLayout eventUuidLayout = new VerticalLayout();
		eventUuidLayout.setMargin(false);
		eventUuidLayout.setSpacing(false);
		Label eventUuidLabel = new Label(I18nProperties.getCaption(Captions.Event_uuid));
		eventUuidLabel.setStyleName(CssStyles.LABEL_BOLD);
		eventUuidLabel.setStyleName(CssStyles.VSPACE_NONE, true);
		eventUuidLayout.addComponent(eventUuidLabel);

		Link linkUuidData = new Link(
			DataHelper.getShortUuid(eventSelectionDto.getEventUuid()),
			new ExternalResource(
				SormasUI.get().getPage().getLocation().getRawPath() + "#!" + EventDataView.VIEW_NAME + "/" + eventSelectionDto.getEventUuid()));
		linkUuidData.setTargetName("_blank");
		eventUuidLayout.addComponent(linkUuidData);
		eventLayout.addComponent(eventUuidLayout);

		VerticalLayout eventTitleLayout = new VerticalLayout();
		eventTitleLayout.setMargin(false);
		eventTitleLayout.setSpacing(false);
		Label eventTitleLabel = new Label(I18nProperties.getCaption(Captions.Event_eventTitle));
		eventTitleLabel.setStyleName(CssStyles.LABEL_BOLD);
		eventTitleLabel.setStyleName(CssStyles.VSPACE_NONE, true);
		eventTitleLayout.addComponent(eventTitleLabel);

		Label eventTitleData = new Label(eventSelectionDto.getEventTitle());
		eventTitleData.addStyleName(CssStyles.VSPACE_TOP_NONE);
		eventTitleLayout.addComponent(eventTitleData);
		eventLayout.addComponent(eventTitleLayout);

		return eventLayout;
	}

	public List<EventParticipantSelectionDto> mapEventParticipantToSelection(List<EventParticipantDto> eventParticipantDtos) {
		List<EventParticipantSelectionDto> eventParticipantSelectionDtos = new ArrayList<>();

		eventParticipantSelectionDtos = eventParticipantDtos.stream().map(eventParticipantDto -> {
			EventParticipantSelectionDto selectionDto =
				new EventParticipantSelectionDto(eventParticipantDto.getEvent().getUuid(), eventParticipantDto.getUuid());
			EventDto eventDto = FacadeProvider.getEventFacade().getEventByUuid(eventParticipantDto.getEvent().getUuid(), true);
			selectionDto.setEventUuid(eventDto.getUuid());
			selectionDto.setEventTitle(eventDto.getEventTitle());
			selectionDto.setPersonUuid(eventParticipantDto.getPerson().getUuid());
			selectionDto.setFirstName(eventParticipantDto.getPerson().getFirstName());
			selectionDto.setLastName(eventParticipantDto.getPerson().getLastName());
			selectionDto.setAgeAndBirthDate(
				PersonHelper.getAgeAndBirthdateString(
					eventParticipantDto.getPerson().getApproximateAge(),
					eventParticipantDto.getPerson().getApproximateAgeType(),
					eventParticipantDto.getPerson().getBirthdateDD(),
					eventParticipantDto.getPerson().getBirthdateMM(),
					eventParticipantDto.getPerson().getBirthdateYYYY()));
			selectionDto.setSex(eventParticipantDto.getPerson().getSex());

			DistrictDto districtDto = null;
			if (eventParticipantDto.getDistrict() != null) {
				districtDto = FacadeProvider.getDistrictFacade().getByUuid(eventParticipantDto.getDistrict().getUuid());
			} else if (eventDto.getEventLocation().getDistrict() != null) {
				districtDto = FacadeProvider.getDistrictFacade().getByUuid(eventDto.getEventLocation().getDistrict().getUuid());
			}
			selectionDto.setDistrictName(districtDto.getName());

			selectionDto.setInvolvementDescription(eventParticipantDto.getInvolvementDescription());
			selectionDto
				.setResultingCaseUuid(eventParticipantDto.getResultingCase() != null ? eventParticipantDto.getResultingCase().getUuid() : null);
			selectionDto.setContactCount(0);

			return selectionDto;
		}).collect(Collectors.toList());

		return eventParticipantSelectionDtos;
	}

}
