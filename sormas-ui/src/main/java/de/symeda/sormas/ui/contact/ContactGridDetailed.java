package de.symeda.sormas.ui.contact;

import com.vaadin.navigator.View;
import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.contact.ContactCriteria;
import de.symeda.sormas.api.contact.ContactIndexDetailedDto;
import de.symeda.sormas.api.utils.SortProperty;
import org.apache.commons.lang3.ArrayUtils;

import java.util.List;
import java.util.stream.Stream;

public class ContactGridDetailed extends AbstractContactGrid<ContactIndexDetailedDto> {

	public <V extends View> ContactGridDetailed(ContactCriteria criteria, Class<V> viewClass) {
		super(ContactIndexDetailedDto.class, criteria, viewClass);
	}

	protected List<ContactIndexDetailedDto> getGridData(ContactCriteria contactCriteria, Integer first, Integer max,
														List<SortProperty> sortProperties) {
		return FacadeProvider.getContactFacade().getIndexDetailedList(contactCriteria, first, max, sortProperties);
	}

	@Override
	protected Stream<String> getColumnList() {
		return Stream.concat(super.getColumnList(), Stream.of(ContactIndexDetailedDto.CAZE, ContactIndexDetailedDto.REPORTING_USER));
	}

	@Override
	protected Stream<String> getPersonColumns() {
		return Stream.concat(super.getPersonColumns(), Stream.of(
				ContactIndexDetailedDto.SEX, ContactIndexDetailedDto.APPROXIMATE_AGE,
				ContactIndexDetailedDto.DISTRICT_NAME, ContactIndexDetailedDto.CITY, ContactIndexDetailedDto.ADDRESS, ContactIndexDetailedDto.POSTAL_CODE,
				ContactIndexDetailedDto.PHONE
		));
	}
}
