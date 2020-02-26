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
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.api.utils.SortProperty;
import de.symeda.sormas.api.visit.VisitResult;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.UserProvider;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.FilteredGrid;
import de.symeda.sormas.ui.utils.UuidRenderer;

@SuppressWarnings("serial")
public class ContactFollowUpGrid extends FilteredGrid<ContactFollowUpDto, ContactCriteria> {

	public static final String DAY_1_RESULT = "day1ResultLoc";
	public static final String DAY_2_RESULT = "day2ResultLoc";
	public static final String DAY_3_RESULT = "day3ResultLoc";
	public static final String DAY_4_RESULT = "day4ResultLoc";
	public static final String DAY_5_RESULT = "day5ResultLoc";
	public static final String DAY_6_RESULT = "day6ResultLoc";
	public static final String DAY_7_RESULT = "day7ResultLoc";
	public static final String DAY_8_RESULT = "day8ResultLoc";

	@SuppressWarnings("unchecked")
	public <V extends View> ContactFollowUpGrid(ContactCriteria criteria, Class<V> viewClass) {
		super(ContactFollowUpDto.class);
		setSizeFull();

		setDataProvider();
		setCriteria(criteria);

		addComponentColumn(followUpDto -> {
			return new Label("");
		}).setId(DAY_1_RESULT);
		addComponentColumn(followUpDto -> {
			return new Label("");
		}).setId(DAY_2_RESULT);
		addComponentColumn(followUpDto -> {
			return new Label("");
		}).setId(DAY_3_RESULT);
		addComponentColumn(followUpDto -> {
			return new Label("");
		}).setId(DAY_4_RESULT);
		addComponentColumn(followUpDto -> {
			return new Label("");
		}).setId(DAY_5_RESULT);
		addComponentColumn(followUpDto -> {
			return new Label("");
		}).setId(DAY_6_RESULT);
		addComponentColumn(followUpDto -> {
			return new Label("");
		}).setId(DAY_7_RESULT);
		addComponentColumn(followUpDto -> {
			return new Label("");
		}).setId(DAY_8_RESULT);

		setColumns(ContactFollowUpDto.UUID, ContactFollowUpDto.PERSON, ContactFollowUpDto.CONTACT_OFFICER,
				ContactFollowUpDto.LAST_CONTACT_DATE, ContactFollowUpDto.FOLLOW_UP_UNTIL, DAY_1_RESULT,
				DAY_2_RESULT, DAY_3_RESULT, DAY_4_RESULT, DAY_5_RESULT, DAY_6_RESULT, DAY_7_RESULT, DAY_8_RESULT);

		((Column<ContactFollowUpDto, String>) getColumn(ContactFollowUpDto.UUID)).setRenderer(new UuidRenderer());
		((Column<ContactFollowUpDto, Date>) getColumn(ContactFollowUpDto.LAST_CONTACT_DATE)).setRenderer(new DateRenderer(DateHelper.getLocalShortDateFormat()));
		((Column<ContactFollowUpDto, Date>) getColumn(ContactFollowUpDto.FOLLOW_UP_UNTIL)).setRenderer(new DateRenderer(DateHelper.getLocalShortDateFormat()));

		for (Column<?, ?> column : getColumns()) {
			column.setCaption(I18nProperties.getPrefixCaption(
					ContactFollowUpDto.I18N_PREFIX, column.getId().toString(), column.getCaption()));
		}

		Date now = new Date();
		getColumn(DAY_1_RESULT).setCaption(DateHelper.formatLocalShortDate(DateHelper.subtractDays(now, 7))).setSortable(false).setStyleGenerator(
				new StyleGenerator<ContactFollowUpDto>() {
					@Override
					public String apply(ContactFollowUpDto item) {
						return getVisitResultCssStyle(item.getDay1Result());
					}
				});
		getColumn(DAY_2_RESULT).setCaption(DateHelper.formatLocalShortDate(DateHelper.subtractDays(now, 6))).setSortable(false).setStyleGenerator(
				new StyleGenerator<ContactFollowUpDto>() {
					@Override
					public String apply(ContactFollowUpDto item) {
						return getVisitResultCssStyle(item.getDay2Result());
					}
				});
		getColumn(DAY_3_RESULT).setCaption(DateHelper.formatLocalShortDate(DateHelper.subtractDays(now, 5))).setSortable(false).setStyleGenerator(
				new StyleGenerator<ContactFollowUpDto>() {
					@Override
					public String apply(ContactFollowUpDto item) {
						return getVisitResultCssStyle(item.getDay3Result());
					}
				});
		getColumn(DAY_4_RESULT).setCaption(DateHelper.formatLocalShortDate(DateHelper.subtractDays(now, 4))).setSortable(false).setStyleGenerator(
				new StyleGenerator<ContactFollowUpDto>() {
					@Override
					public String apply(ContactFollowUpDto item) {
						return getVisitResultCssStyle(item.getDay4Result());
					}
				});
		getColumn(DAY_5_RESULT).setCaption(DateHelper.formatLocalShortDate(DateHelper.subtractDays(now, 3))).setSortable(false).setStyleGenerator(
				new StyleGenerator<ContactFollowUpDto>() {
					@Override
					public String apply(ContactFollowUpDto item) {
						return getVisitResultCssStyle(item.getDay5Result());
					}
				});
		getColumn(DAY_6_RESULT).setCaption(DateHelper.formatLocalShortDate(DateHelper.subtractDays(now, 2))).setSortable(false).setStyleGenerator(
				new StyleGenerator<ContactFollowUpDto>() {
					@Override
					public String apply(ContactFollowUpDto item) {
						return getVisitResultCssStyle(item.getDay6Result());
					}
				});
		getColumn(DAY_7_RESULT).setCaption(DateHelper.formatLocalShortDate(DateHelper.subtractDays(now, 1))).setSortable(false).setStyleGenerator(
				new StyleGenerator<ContactFollowUpDto>() {
					@Override
					public String apply(ContactFollowUpDto item) {
						return getVisitResultCssStyle(item.getDay7Result());
					}
				});
		getColumn(DAY_8_RESULT).setCaption(DateHelper.formatLocalShortDate(now)).setSortable(false).setStyleGenerator(
				new StyleGenerator<ContactFollowUpDto>() {
					@Override
					public String apply(ContactFollowUpDto item) {
						return getVisitResultCssStyle(item.getDay8Result());
					}
				});

		addItemClickListener(e ->  {
			if ((e.getColumn() != null && ContactFollowUpDto.UUID.equals(e.getColumn().getId()))
					|| e.getMouseEventDetails().isDoubleClick()) {
				ControllerProvider.getContactController().navigateToData(e.getItem().getUuid());
			}
		});
	}

	private String getVisitResultCssStyle(VisitResult result) {
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
						UserProvider.getCurrent().getUuid(), query.getFilter().orElse(null), query.getOffset(), query.getLimit(), 
						query.getSortOrders().stream().map(sortOrder -> new SortProperty(sortOrder.getSorted(), sortOrder.getDirection() == SortDirection.ASCENDING))
						.collect(Collectors.toList())).stream(),
				query -> {
					return (int) FacadeProvider.getContactFacade().count(
							UserProvider.getCurrent().getUuid(), query.getFilter().orElse(null));
				});
		setDataProvider(dataProvider);
		setSelectionMode(SelectionMode.NONE);
	}

}
