package de.symeda.sormas.ui.contact;

import java.util.Date;
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
import de.symeda.sormas.ui.UserProvider;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.DateFormatHelper;
import de.symeda.sormas.ui.utils.FilteredGrid;
import de.symeda.sormas.ui.utils.UuidRenderer;

@SuppressWarnings("serial")
public class ContactFollowUpGrid extends FilteredGrid<ContactFollowUpDto, ContactCriteria> {

	private static final String[] DAY_RESULTS = new String[] {
			"day1ResultLoc", "day2ResultLoc", "day3ResultLoc", "day4ResultLoc",
			"day5ResultLoc", "day6ResultLoc", "day7ResultLoc", "day8ResultLoc"
	};
	
	private Date referenceDate;
	private Date[] dates;
	
	@SuppressWarnings("unchecked")
	public <V extends View> ContactFollowUpGrid(ContactCriteria criteria, Date referenceDate, Class<V> viewClass) {
		super(ContactFollowUpDto.class);
		setSizeFull();
		
		this.referenceDate = referenceDate;

		setDataProvider();
		setCriteria(criteria);
		setDates();

		setColumns(ContactFollowUpDto.UUID, ContactFollowUpDto.PERSON, ContactFollowUpDto.CONTACT_OFFICER,
				ContactFollowUpDto.LAST_CONTACT_DATE, ContactFollowUpDto.REPORT_DATE_TIME, ContactFollowUpDto.FOLLOW_UP_UNTIL);
		
		for (int i = 0; i < DAY_RESULTS.length; i++) {
			addComponentColumn(followUpDto -> {
				return new Label("");
			}).setId(DAY_RESULTS[i]);
			
			final int index = i;
			getColumn(DAY_RESULTS[i]).setCaption(DateFormatHelper.formatDate(dates[i])).setSortable(false).setStyleGenerator(
					new StyleGenerator<ContactFollowUpDto>() {
						@Override
						public String apply(ContactFollowUpDto item) {
							return getVisitResultCssStyle(item.getVisitResults()[index], dates[index], ContactLogic.getStartDate(item.getLastContactDate(), item.getReportDateTime()), item.getFollowUpUntil());
						}
					});
		}

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

	public void setDataProvider() {
		DataProvider<ContactFollowUpDto, ContactCriteria> dataProvider = DataProvider.fromFilteringCallbacks(
				query -> FacadeProvider.getContactFacade().getContactFollowUpList(
						 query.getFilter().orElse(null), referenceDate, query.getOffset(), query.getLimit(),
						query.getSortOrders().stream().map(sortOrder -> new SortProperty(sortOrder.getSorted(), sortOrder.getDirection() == SortDirection.ASCENDING))
						.collect(Collectors.toList())).stream(),
				query -> (int) FacadeProvider.getContactFacade().count(query.getFilter().orElse(null)));
		setDataProvider(dataProvider);
		setSelectionMode(SelectionMode.NONE);
	}

	public void setReferenceDate(Date referenceDate) {
		this.referenceDate = referenceDate;

		setDates();
		setColumnCaptions();
	}
	
	public Date getReferenceDate() {
		return referenceDate;
	}
	
	private void setColumnCaptions() {
		for (int i = 0; i < DAY_RESULTS.length; i++) {
			getColumn(DAY_RESULTS[i]).setCaption(DateFormatHelper.formatDate(dates[i]));
		}
	}
	
	private void setDates() {
		dates = new Date[]{DateHelper.subtractDays(referenceDate, 7), DateHelper.subtractDays(referenceDate, 6), DateHelper.subtractDays(referenceDate, 5), 
				DateHelper.subtractDays(referenceDate, 4), DateHelper.subtractDays(referenceDate, 3), DateHelper.subtractDays(referenceDate, 2), 
				DateHelper.subtractDays(referenceDate, 1), referenceDate}; 
	}
	
}
