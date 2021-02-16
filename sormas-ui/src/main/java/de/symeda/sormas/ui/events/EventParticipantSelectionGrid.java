package de.symeda.sormas.ui.events;

import java.util.List;

import com.vaadin.v7.data.util.BeanItemContainer;
import com.vaadin.v7.data.util.GeneratedPropertyContainer;
import com.vaadin.v7.shared.ui.grid.HeightMode;
import com.vaadin.v7.ui.Grid;

import de.symeda.sormas.api.event.EventDto;
import de.symeda.sormas.api.event.SimilarEventParticipantDto;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.person.SimilarPersonDto;
import de.symeda.sormas.ui.utils.V7UuidRenderer;

public class EventParticipantSelectionGrid extends Grid {

	public EventParticipantSelectionGrid(List<SimilarEventParticipantDto> eventParticipants) {
		buildGrid();
		setContainerData(eventParticipants);
	}

	private void setContainerData(List<SimilarEventParticipantDto> similarEventParticipants) {
		getContainer().removeAllItems();
		if (similarEventParticipants != null) {
			getContainer().addAll(similarEventParticipants);
			setHeightByRows(similarEventParticipants.size() > 0 ? (similarEventParticipants.size() <= 10 ? similarEventParticipants.size() : 10) : 1);
		}
	}

	private void buildGrid() {
		setSizeFull();
		setSelectionMode(SelectionMode.SINGLE);
		setHeightMode(HeightMode.ROW);

		BeanItemContainer<SimilarEventParticipantDto> container = new BeanItemContainer(SimilarEventParticipantDto.class);
		GeneratedPropertyContainer generatedContainer = new GeneratedPropertyContainer(container);
		setContainerDataSource(generatedContainer);

		setColumns(
			SimilarEventParticipantDto.UUID,
			SimilarEventParticipantDto.FIRST_NAME,
			SimilarEventParticipantDto.LAST_NAME,
			SimilarEventParticipantDto.INVOLVEMENT_DESCRIPTION,
			SimilarEventParticipantDto.EVENT_UUID,
			SimilarEventParticipantDto.EVENT_STATUS,
			SimilarEventParticipantDto.EVENT_TITLE,
			SimilarEventParticipantDto.START_DATE);

		for (Column column : getColumns()) {
			column.setHeaderCaption(
				I18nProperties.findPrefixCaption(
					column.getPropertyId().toString(),
					SimilarEventParticipantDto.I18N_PREFIX,
					SimilarPersonDto.I18N_PREFIX,
					EventDto.I18N_PREFIX));
		}

		getColumn(SimilarEventParticipantDto.UUID).setRenderer(new V7UuidRenderer());
		getColumn(SimilarEventParticipantDto.FIRST_NAME).setMinimumWidth(150);
		getColumn(SimilarEventParticipantDto.LAST_NAME).setMinimumWidth(150);
		getColumn(SimilarEventParticipantDto.EVENT_UUID).setRenderer(new V7UuidRenderer());
	}

	private BeanItemContainer<SimilarEventParticipantDto> getContainer() {
		GeneratedPropertyContainer container = (GeneratedPropertyContainer) super.getContainerDataSource();
		return (BeanItemContainer<SimilarEventParticipantDto>) container.getWrappedContainer();
	}
}
