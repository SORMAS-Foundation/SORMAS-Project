package de.symeda.sormas.ui.contact;

import java.util.List;
import java.util.stream.Stream;

import com.vaadin.navigator.View;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.caze.CaseReferenceDto;
import de.symeda.sormas.api.contact.ContactCriteria;
import de.symeda.sormas.api.contact.ContactIndexDetailedDto;
import de.symeda.sormas.api.utils.SortProperty;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.utils.ShowDetailsListener;
import de.symeda.sormas.ui.utils.UuidRenderer;

public class ContactGridDetailed extends AbstractContactGrid<ContactIndexDetailedDto> {

	private static final long serialVersionUID = 3063406225342415037L;

	public <V extends View> ContactGridDetailed(ContactCriteria criteria, Class<V> viewClass) {
		super(ContactIndexDetailedDto.class, criteria, viewClass);
	}

	protected List<ContactIndexDetailedDto> getGridData(
		ContactCriteria contactCriteria,
		Integer first,
		Integer max,
		List<SortProperty> sortProperties) {

		return FacadeProvider.getContactFacade().getIndexDetailedList(contactCriteria, first, max, sortProperties);
	}

	@Override
	protected Stream<String> getColumnList() {
		return Stream.concat(super.getColumnList(), Stream.of(ContactIndexDetailedDto.CAZE, ContactIndexDetailedDto.REPORTING_USER));
	}

	@Override
	protected Stream<String> getPersonColumns() {

		return Stream.concat(
			super.getPersonColumns(),
			Stream.of(
				ContactIndexDetailedDto.SEX,
				ContactIndexDetailedDto.APPROXIMATE_AGE,
				ContactIndexDetailedDto.DISTRICT_NAME,
				ContactIndexDetailedDto.POSTAL_CODE,
				ContactIndexDetailedDto.CITY,
				ContactIndexDetailedDto.STREET,
				ContactIndexDetailedDto.HOUSE_NUMBER,
				ContactIndexDetailedDto.ADDITIONAL_INFORMATION,
				ContactIndexDetailedDto.PHONE));
	}

	@Override
	public Stream<String> getEventColumns() {
		return Stream.of(ContactIndexDetailedDto.LATEST_EVENT_ID, ContactIndexDetailedDto.LATEST_EVENT_TITLE);
	}

	@Override
	@SuppressWarnings("unchecked")
	protected void initColumns() {

		super.initColumns();

		getColumn(ContactIndexDetailedDto.SEX).setWidth(80);
		getColumn(ContactIndexDetailedDto.APPROXIMATE_AGE).setWidth(50);
		getColumn(ContactIndexDetailedDto.DISTRICT_NAME).setWidth(150);
		getColumn(ContactIndexDetailedDto.POSTAL_CODE).setWidth(100);
		getColumn(ContactIndexDetailedDto.CITY).setWidth(150);
		getColumn(ContactIndexDetailedDto.STREET).setWidth(150);
		getColumn(ContactIndexDetailedDto.HOUSE_NUMBER).setWidth(50);
		getColumn(ContactIndexDetailedDto.ADDITIONAL_INFORMATION).setWidth(200);
		getColumn(ContactIndexDetailedDto.PHONE).setWidth(100);
		((Column<ContactIndexDetailedDto, CaseReferenceDto>) getColumn(ContactIndexDetailedDto.CAZE)).setWidth(150)
			.setRenderer(entry -> entry != null ? entry.getUuid() : null, new UuidRenderer());
		getColumn(ContactIndexDetailedDto.REPORTING_USER).setWidth(150);

		addItemClickListener(new ShowDetailsListener<>(ContactIndexDetailedDto.CAZE, false, e -> {
			CaseReferenceDto caze = e.getCaze();
			if (caze != null && caze.getUuid() != null) {
				ControllerProvider.getCaseController().navigateToCase(caze.getUuid());
			}
		}));

		getColumn(ContactIndexDetailedDto.LATEST_EVENT_ID).setWidth(80).setSortable(false);
		getColumn(ContactIndexDetailedDto.LATEST_EVENT_TITLE).setWidth(150).setSortable(false);
		((Column<ContactIndexDetailedDto, String>) getColumn(ContactIndexDetailedDto.LATEST_EVENT_ID)).setRenderer(new UuidRenderer());
		addItemClickListener(
			new ShowDetailsListener<>(
				ContactIndexDetailedDto.LATEST_EVENT_ID,
				c -> ControllerProvider.getEventController().navigateToData(c.getLatestEventId())));

	}
}
