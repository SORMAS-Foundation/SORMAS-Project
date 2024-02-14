package de.symeda.sormas.ui.events.eventParticipantMerge;

import java.util.List;
import java.util.stream.Stream;

import com.vaadin.data.provider.DataProvider;
import com.vaadin.data.provider.ListDataProvider;
import com.vaadin.server.ExternalResource;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Link;

import de.symeda.sormas.api.event.EventParticipantDto;
import de.symeda.sormas.api.event.EventParticipantSelectionDto;
import de.symeda.sormas.api.feature.FeatureType;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.location.LocationDto;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.ui.SormasUI;
import de.symeda.sormas.ui.UiUtil;
import de.symeda.sormas.ui.caze.CaseDataView;
import de.symeda.sormas.ui.events.EventParticipantDataView;
import de.symeda.sormas.ui.utils.UuidRenderer;

public class EventParticipantMergeSelectionGrid extends Grid<EventParticipantSelectionDto> {

	public static final String EVENT_PARTICIPANT_COLUMN_UUID = "eventParticipantUuidLink";
	public static final String EVENT_UUID = "eventUuidLink";
	public static final String RESULTING_CASE_UUID = "resultingCaseUuidLink";

	public EventParticipantMergeSelectionGrid(List<EventParticipantSelectionDto> eventParticipantSelectionDtos) {
		super(EventParticipantSelectionDto.class);
		buildGrid();

		setDataProvider(eventParticipantSelectionDtos.stream());
	}

	private void buildGrid() {
		setWidthFull();
		setSelectionMode(SelectionMode.SINGLE);
		setHeight(150, Unit.PIXELS);

		addComponentColumn(indexDto -> {
			Link link = new Link(
				DataHelper.getShortUuid(indexDto.getUuid()),
				new ExternalResource(
					SormasUI.get().getPage().getLocation().getRawPath() + "#!" + EventParticipantDataView.VIEW_NAME + "/" + indexDto.getUuid()));
			link.setTargetName("_blank");
			return link;
		}).setId(EVENT_PARTICIPANT_COLUMN_UUID);

		addComponentColumn(indexDto -> {
			Link link = new Link(
				DataHelper.getShortUuid(indexDto.getResultingCaseUuid()),
				new ExternalResource(
					SormasUI.get().getPage().getLocation().getRawPath() + "#!" + CaseDataView.VIEW_NAME + "/" + indexDto.getResultingCaseUuid()));
			link.setTargetName("_blank");
			return link;
		}).setId(RESULTING_CASE_UUID);

		setColumns(
			EVENT_PARTICIPANT_COLUMN_UUID,
			EventParticipantSelectionDto.PERSON_UUID,
			EventParticipantSelectionDto.FIRST_NAME,
			EventParticipantSelectionDto.LAST_NAME,
			EventParticipantSelectionDto.AGE_AND_BIRTH_DATE,
			EventParticipantSelectionDto.SEX,
			EventParticipantSelectionDto.DISTRICT_NAME,
			EventParticipantSelectionDto.INVOLVEMENT_DESCRIPTION,
			RESULTING_CASE_UUID,
			EventParticipantSelectionDto.CONTACT_COUNT);

		for (Column<EventParticipantSelectionDto, ?> column : getColumns()) {
			column.setCaption(
				I18nProperties.findPrefixCaptionWithDefault(
					column.getId(),
					column.getCaption(),
					EventParticipantSelectionDto.I18N_PREFIX,
					EventParticipantDto.I18N_PREFIX,
					PersonDto.I18N_PREFIX,
					LocationDto.I18N_PREFIX));
		}

		getColumn(EventParticipantSelectionDto.FIRST_NAME).setMinimumWidth(150);
		getColumn(EventParticipantSelectionDto.LAST_NAME).setMinimumWidth(150);
		((Column<EventParticipantSelectionDto, String>) getColumn(EventParticipantSelectionDto.PERSON_UUID)).setRenderer(new UuidRenderer());
		getColumn(EventParticipantSelectionDto.CONTACT_COUNT).setSortable(false);

		if (UiUtil.enabled(FeatureType.HIDE_JURISDICTION_FIELDS)) {
			getColumn(EventParticipantSelectionDto.DISTRICT_NAME).setHidden(true);
		}
	}

	/**
	 * @see DataProvider#fromStream
	 */
	public void setDataProvider(Stream<EventParticipantSelectionDto> items) {
		ListDataProvider<EventParticipantSelectionDto> dataProvider = DataProvider.fromStream(items);
		setDataProvider(dataProvider);
	}
}
