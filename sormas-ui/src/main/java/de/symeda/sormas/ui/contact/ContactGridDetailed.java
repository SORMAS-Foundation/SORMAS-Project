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
				ContactIndexDetailedDto.CITY,
				ContactIndexDetailedDto.ADDRESS,
				ContactIndexDetailedDto.POSTAL_CODE,
				ContactIndexDetailedDto.PHONE));
	}

	@Override
	@SuppressWarnings("unchecked")
	protected void initColumns() {

		super.initColumns();

		getColumn(ContactIndexDetailedDto.SEX).setWidth(80);
		getColumn(ContactIndexDetailedDto.APPROXIMATE_AGE).setWidth(50);
		getColumn(ContactIndexDetailedDto.DISTRICT_NAME).setWidth(150);
		getColumn(ContactIndexDetailedDto.CITY).setWidth(150);
		getColumn(ContactIndexDetailedDto.ADDRESS).setWidth(200);
		getColumn(ContactIndexDetailedDto.POSTAL_CODE).setWidth(100);
		getColumn(ContactIndexDetailedDto.PHONE).setWidth(100);
		((Column<ContactIndexDetailedDto, CaseReferenceDto>) getColumn(ContactIndexDetailedDto.CAZE)).setWidth(150)
			.setRenderer(entry -> entry != null ? entry.getUuid() : null, new UuidRenderer());
		getColumn(ContactIndexDetailedDto.REPORTING_USER).setWidth(150);

		addItemClickListener(e -> {
			if ((e.getColumn() != null && ContactIndexDetailedDto.CAZE.equals(e.getColumn().getId()))) {
				CaseReferenceDto caze = e.getItem().getCaze();

				if (caze != null && caze.getUuid() != null) {
					ControllerProvider.getCaseController().navigateToCase(caze.getUuid());
				}
			}
		});
	}
}
