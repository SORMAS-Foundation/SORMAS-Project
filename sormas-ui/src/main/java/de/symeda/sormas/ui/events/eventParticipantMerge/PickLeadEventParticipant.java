package de.symeda.sormas.ui.events.eventParticipantMerge;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import com.vaadin.server.ExternalResource;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Link;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.v7.ui.CustomField;

import de.symeda.sormas.api.event.EventParticipantSelectionDto;
import de.symeda.sormas.api.event.EventReferenceDto;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.ui.SormasUI;
import de.symeda.sormas.ui.events.EventDataView;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.VaadinUiUtil;

public class PickLeadEventParticipant extends CustomField<List<String>> {

	private Map<String, EventParticipantSelectGrid> eventParticipantSelectGrids;
	private PickOrMerge pickOrMerge = PickOrMerge.PICK;

	protected VerticalLayout mainLayout;

	public PickLeadEventParticipant(List<EventParticipantSelectionDto> eventParticipantSelectionDtos) {
		Map<EventReferenceDto, List<EventParticipantSelectionDto>> eventParticipantsByEventSelectionDtos =
			eventParticipantSelectionDtos.stream().collect(Collectors.groupingBy(EventParticipantSelectionDto::getEvent));

		eventParticipantSelectGrids = initializeEventParticipantsSelectGrids(eventParticipantsByEventSelectionDtos);
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

		eventParticipantSelectGrids.forEach((key, value) -> {
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
		return eventParticipantSelectGrids.values().stream().map(eventParticipantSelectGrid -> {
			Set<EventParticipantSelectionDto> selectedRow = eventParticipantSelectGrid.eventParticipantMergeSelectionGrid.getSelectedItems();
			if (!selectedRow.isEmpty()) {
				return selectedRow.iterator().next().getUuid();
			}
			return null;
		}).filter(Objects::nonNull).collect(Collectors.toList());
	}

	protected Map<String, EventParticipantSelectGrid> initializeEventParticipantsSelectGrids(
		Map<EventReferenceDto, List<EventParticipantSelectionDto>> eventParticipantByEventDtos) {

		Map<String, EventParticipantSelectGrid> ret = new HashMap<>();

		eventParticipantByEventDtos.forEach((key, value) -> {

			EventParticipantMergeSelectionGrid newGrid = new EventParticipantMergeSelectionGrid(value);
			Component eventComponent = buildEventComponent(key.getUuid(), key.getCaption());

			EventParticipantSelectGrid eventParticipantSelectGrid = new EventParticipantSelectGrid();
			eventParticipantSelectGrid.setEventTitle(eventComponent);
			eventParticipantSelectGrid.setEventParticipantMergeSelectionGrid(newGrid);

			ret.put(key.getUuid(), eventParticipantSelectGrid);

		});
		return ret;
	}

	private static class EventParticipantSelectGrid {

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

	private Component buildEventComponent(String eventUuid, String eventTitle) {
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
			DataHelper.getShortUuid(eventUuid),
			new ExternalResource(SormasUI.get().getPage().getLocation().getRawPath() + "#!" + EventDataView.VIEW_NAME + "/" + eventUuid));
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

		Label eventTitleData = new Label(eventTitle);
		eventTitleData.addStyleName(CssStyles.VSPACE_TOP_NONE);
		eventTitleLayout.addComponent(eventTitleData);
		eventLayout.addComponent(eventTitleLayout);

		return eventLayout;
	}

	public PickOrMerge getPickOrMerge() {
		return pickOrMerge;
	}

	public void setPickOrMerge(PickOrMerge pickOrMerge) {
		this.pickOrMerge = pickOrMerge;
	}

	public enum PickOrMerge {
		PICK,
		MERGE
	}

}
