package de.symeda.sormas.ui.contact;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.vaadin.navigator.View;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.caze.CaseReferenceDto;
import de.symeda.sormas.api.contact.ContactCriteria;
import de.symeda.sormas.api.contact.ContactIndexDetailedDto;
import de.symeda.sormas.api.feature.FeatureType;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.api.utils.SortProperty;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.UiUtil;
import de.symeda.sormas.ui.utils.ShowDetailsListener;
import de.symeda.sormas.ui.utils.UuidRenderer;
import de.symeda.sormas.ui.utils.ViewConfiguration;

public class ContactGridDetailed extends AbstractContactGrid<ContactIndexDetailedDto> {

	private static final long serialVersionUID = 3063406225342415037L;

	public ContactGridDetailed(ContactCriteria criteria, Class<? extends View> viewClass, Class<? extends ViewConfiguration> viewConfigurationClass) {
		super(ContactIndexDetailedDto.class, criteria, viewClass, viewConfigurationClass);
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
		List<String> columnList = super.getColumnList().collect(Collectors.toList());
		columnList.add(columnList.indexOf(ContactIndexDetailedDto.CONTACT_PROXIMITY) + 1, ContactIndexDetailedDto.RELATION_TO_CASE);
		return Stream.concat(columnList.stream(), Stream.of(ContactIndexDetailedDto.CAZE, ContactIndexDetailedDto.REPORTING_USER));
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

		if (!UiUtil.permitted(UserRight.EVENT_VIEW)) {
			return Stream.empty();
		}

		return Stream.of(ContactIndexDetailedDto.LATEST_EVENT_ID, ContactIndexDetailedDto.LATEST_EVENT_TITLE);
	}

	@Override
	@SuppressWarnings("unchecked")
	protected void initColumns() {

		super.initColumns();

		getColumn(ContactIndexDetailedDto.SEX).setWidth(80);
		getColumn(ContactIndexDetailedDto.APPROXIMATE_AGE).setWidth(50);
		if (UiUtil.enabled(FeatureType.HIDE_JURISDICTION_FIELDS)) {
			getColumn(ContactIndexDetailedDto.DISTRICT_NAME).setHidden(true);
		} else {
			getColumn(ContactIndexDetailedDto.DISTRICT_NAME).setWidth(150);
		}
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

		if (getEventColumns().findAny().isPresent()) {
			getColumn(ContactIndexDetailedDto.LATEST_EVENT_ID).setWidth(80).setSortable(false);
			getColumn(ContactIndexDetailedDto.LATEST_EVENT_TITLE).setWidth(150).setSortable(false);
			((Column<ContactIndexDetailedDto, String>) getColumn(ContactIndexDetailedDto.LATEST_EVENT_ID)).setRenderer(new UuidRenderer());
			addItemClickListener(
				new ShowDetailsListener<>(
					ContactIndexDetailedDto.LATEST_EVENT_ID,
					c -> ControllerProvider.getEventController().navigateToData(c.getLatestEventId())));
		}
	}
}
