package de.symeda.sormas.ui.contact;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import com.vaadin.data.provider.DataProvider;
import com.vaadin.navigator.View;
import com.vaadin.shared.data.sort.SortDirection;
import com.vaadin.ui.Label;
import com.vaadin.ui.StyleGenerator;
import com.vaadin.ui.renderers.DateRenderer;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.contact.ContactCriteria;
import de.symeda.sormas.api.contact.ContactFollowUpDto;
import de.symeda.sormas.api.contact.ContactLogic;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.api.utils.SortProperty;
import de.symeda.sormas.api.visit.VisitResult;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.DateFormatHelper;
import de.symeda.sormas.ui.utils.FilteredGrid;
import de.symeda.sormas.ui.utils.UuidRenderer;

@SuppressWarnings("serial")
public class ContactFollowUpGrid extends FilteredGrid<ContactFollowUpDto, ContactCriteria> {

	private List<Date> dates = new ArrayList<>();

	@SuppressWarnings("unchecked")
	public <V extends View> ContactFollowUpGrid(ContactCriteria criteria, Date referenceDate, int interval, Class<V> viewClass) {
		super(ContactFollowUpDto.class);
		setSizeFull();
		
		setDataProvider(referenceDate, interval-1);
		setCriteria(criteria);

		setColumns(ContactFollowUpDto.UUID, ContactFollowUpDto.PERSON, ContactFollowUpDto.CONTACT_OFFICER,
				ContactFollowUpDto.LAST_CONTACT_DATE, ContactFollowUpDto.REPORT_DATE_TIME, ContactFollowUpDto.FOLLOW_UP_UNTIL);

		addVisitColumns(referenceDate, interval);

		((Column<ContactFollowUpDto, String>) getColumn(ContactFollowUpDto.UUID)).setRenderer(new UuidRenderer());
		((Column<ContactFollowUpDto, Date>) getColumn(ContactFollowUpDto.LAST_CONTACT_DATE)).setRenderer(new DateRenderer(DateFormatHelper.getDateFormat()));
		((Column<ContactFollowUpDto, Date>) getColumn(ContactFollowUpDto.REPORT_DATE_TIME)).setRenderer(new DateRenderer(DateFormatHelper.getDateFormat()));
		((Column<ContactFollowUpDto, Date>) getColumn(ContactFollowUpDto.FOLLOW_UP_UNTIL)).setRenderer(new DateRenderer(DateFormatHelper.getDateFormat()));

		for (Column<?, ?> column : getColumns()) {
			column.setCaption(I18nProperties.getPrefixCaption(
					ContactFollowUpDto.I18N_PREFIX, column.getId().toString(), column.getCaption()));
		}

		addItemClickListener(e ->  {
			if ((e.getColumn() != null && ContactFollowUpDto.UUID.equals(e.getColumn().getId()))
					|| e.getMouseEventDetails().isDoubleClick()) {
				ControllerProvider.getContactController().navigateToData(e.getItem().getUuid());
			}
		});
	}

	public void addVisitColumns(Date referenceDate, int interval) {

		setDataProvider(referenceDate, interval-1);
		dates.forEach(date -> removeColumn(DateFormatHelper.formatDate(date)));

		setDates(referenceDate, interval);

		for (int i = 0; i < interval; i++) {
			String columnId = DateFormatHelper.formatDate(dates.get(i));
			addComponentColumn(followUpDto -> {
				return new Label("");
			}).setId(columnId);

			final int index = i;
			getColumn(columnId).setCaption(columnId).setSortable(false).setStyleGenerator(
					(StyleGenerator<ContactFollowUpDto>) item -> {
							final VisitResult visitResult = item.getVisitResults()[index];
							final Date date = dates.get(index);
							return getVisitResultCssStyle(visitResult, date, ContactLogic.getStartDate(item.getLastContactDate(), item.getReportDateTime()), item.getFollowUpUntil());
					});
		}
	}

	private String getVisitResultCssStyle(VisitResult result, Date date, Date contactDate, Date followUpUntil) {
		if (!DateHelper.isBetween(date, DateHelper.getStartOfDay(contactDate), DateHelper.getEndOfDay(followUpUntil))) {
			return "";
		}
		
		switch (result) {
		case NOT_SYMPTOMATIC:
			return CssStyles.GRID_CELL_NOT_SYMPTOMATIC;
		case SYMPTOMATIC:
			return CssStyles.GRID_CELL_SYMPTOMATIC;
		case NOT_PERFORMED:
			return CssStyles.GRID_CELL_NOT_PERFORMED;
		case UNAVAILABLE:
			return CssStyles.GRID_CELL_UNAVAILABLE;
		case UNCOOPERATIVE:
			return CssStyles.GRID_CELL_UNCOOPERATIVE;
		default:
			return null;
		}
	}

	public void reload() {
		getDataProvider().refreshAll();
	}

	public void setDataProvider(Date referenceDate, int interval) {
		DataProvider<ContactFollowUpDto, ContactCriteria> dataProvider = DataProvider.fromFilteringCallbacks(
				query -> FacadeProvider.getContactFacade().getContactFollowUpList(
						 query.getFilter().orElse(null), referenceDate, interval,  query.getOffset(), query.getLimit(),
						query.getSortOrders().stream().map(sortOrder -> new SortProperty(sortOrder.getSorted(), sortOrder.getDirection() == SortDirection.ASCENDING))
						.collect(Collectors.toList())).stream(),
				query -> (int) FacadeProvider.getContactFacade().count(query.getFilter().orElse(null)));
		setDataProvider(dataProvider);
		setSelectionMode(SelectionMode.NONE);
	}

	private void setDates(Date referenceDate, int interval) {
		dates.clear();
		for (int i = 0; i < interval; i++) {
			dates.add(DateHelper.subtractDays(referenceDate, interval - i - 1));
		}
	}
}
