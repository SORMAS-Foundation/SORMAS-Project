package de.symeda.sormas.ui.events;

import java.util.List;

import com.vaadin.v7.data.util.BeanItemContainer;
import com.vaadin.v7.data.util.GeneratedPropertyContainer;
import com.vaadin.v7.shared.ui.grid.HeightMode;
import com.vaadin.v7.ui.Grid;

import de.symeda.sormas.api.event.EventParticipantDto;
import de.symeda.sormas.api.event.EventParticipantIndexDto;
import de.symeda.sormas.api.event.SimilarEventParticipantDto;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.person.SimilarPersonDto;

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

		BeanItemContainer<SimilarEventParticipantDto> container = new BeanItemContainer<>(SimilarEventParticipantDto.class);
		GeneratedPropertyContainer generatedContainer = new GeneratedPropertyContainer(container);
		setContainerDataSource(generatedContainer);

//		setColumns(
//			SimilarEventParticipantDto.FIRST_NAME,
//			SimilarEventParticipantDto.LAST_NAME,
//			SimilarEventParticipantDto.UUID,
//			SimilarEventParticipantDto.CAZE,
//			SimilarEventParticipantDto.CASE_ID_EXTERNAL_SYSTEM,
//			SimilarEventParticipantDto.LAST_CONTACT_DATE,
//			SimilarEventParticipantDto.CONTACT_PROXIMITY,
//			SimilarEventParticipantDto.CONTACT_CLASSIFICATION,
//			SimilarEventParticipantDto.CONTACT_STATUS,
//			SimilarEventParticipantDto.FOLLOW_UP_STATUS);

		for (Column column : getColumns()) {
			column.setHeaderCaption(
				I18nProperties.findPrefixCaption(
					column.getPropertyId().toString(),
					SimilarPersonDto.I18N_PREFIX,
					EventParticipantIndexDto.I18N_PREFIX,
					EventParticipantDto.I18N_PREFIX));
		}

//		getColumn(SimilarEventParticipantDto.FIRST_NAME).setMinimumWidth(150);
//		getColumn(SimilarEventParticipantDto.LAST_NAME).setMinimumWidth(150);
	}

	private BeanItemContainer<SimilarEventParticipantDto> getContainer() {
		GeneratedPropertyContainer container = (GeneratedPropertyContainer) super.getContainerDataSource();
		return (BeanItemContainer<SimilarEventParticipantDto>) container.getWrappedContainer();
	}
}
