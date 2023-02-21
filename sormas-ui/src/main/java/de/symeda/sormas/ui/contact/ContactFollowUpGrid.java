package de.symeda.sormas.ui.contact;

import static de.symeda.sormas.ui.utils.FollowUpUtils.getVisitResultCssStyle;
import static de.symeda.sormas.ui.utils.FollowUpUtils.getVisitResultDescription;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import com.vaadin.data.provider.DataProvider;
import com.vaadin.navigator.View;
import com.vaadin.shared.data.sort.SortDirection;
import com.vaadin.ui.DescriptionGenerator;
import com.vaadin.ui.Label;
import com.vaadin.ui.StyleGenerator;
import com.vaadin.ui.renderers.DateRenderer;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.contact.ContactCriteria;
import de.symeda.sormas.api.contact.ContactDto;
import de.symeda.sormas.api.contact.ContactFollowUpDto;
import de.symeda.sormas.api.contact.ContactIndexDto;
import de.symeda.sormas.api.contact.ContactLogic;
import de.symeda.sormas.api.followup.FollowUpDto;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.api.utils.SortProperty;
import de.symeda.sormas.api.visit.VisitResultDto;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.utils.DateFormatHelper;
import de.symeda.sormas.ui.utils.FieldAccessColumnStyleGenerator;
import de.symeda.sormas.ui.utils.FilteredGrid;
import de.symeda.sormas.ui.utils.ShowDetailsListener;
import de.symeda.sormas.ui.utils.UuidRenderer;

@SuppressWarnings("serial")
public class ContactFollowUpGrid extends FilteredGrid<ContactFollowUpDto, ContactCriteria> {

	private List<Date> dates = new ArrayList<>();
	private List<String> dateColumnIds = new ArrayList<>();

	@SuppressWarnings("unchecked")
	public <V extends View> ContactFollowUpGrid(ContactCriteria criteria, Class<V> viewClass) {

		super(ContactFollowUpDto.class);
		setSizeFull();

		setColumns(
			FollowUpDto.UUID,
			FollowUpDto.FIRST_NAME,
			FollowUpDto.LAST_NAME,
			ContactFollowUpDto.CONTACT_OFFICER,
			ContactFollowUpDto.LAST_CONTACT_DATE,
			FollowUpDto.REPORT_DATE,
			FollowUpDto.FOLLOW_UP_UNTIL,
			FollowUpDto.SYMPTOM_JOURNAL_STATUS);

		setVisitColumns(criteria);

		((Column<ContactFollowUpDto, String>) getColumn(ContactFollowUpDto.UUID)).setRenderer(new UuidRenderer());
		((Column<ContactFollowUpDto, Date>) getColumn(ContactFollowUpDto.LAST_CONTACT_DATE))
			.setRenderer(new DateRenderer(DateFormatHelper.getDateFormat()));
		((Column<ContactFollowUpDto, Date>) getColumn(FollowUpDto.REPORT_DATE)).setRenderer(new DateRenderer(DateFormatHelper.getDateFormat()));
		((Column<ContactFollowUpDto, Date>) getColumn(FollowUpDto.FOLLOW_UP_UNTIL)).setRenderer(new DateRenderer(DateFormatHelper.getDateFormat()));

		if (!FacadeProvider.getConfigFacade().isExternalJournalActive()) {
			getColumn(ContactIndexDto.SYMPTOM_JOURNAL_STATUS).setHidden(true);
		}

		for (Column<ContactFollowUpDto, ?> column : getColumns()) {
			final String columnId = column.getId();
			column.setCaption(
				I18nProperties.findPrefixCaptionWithDefault(columnId, column.getCaption(), ContactDto.I18N_PREFIX, FollowUpDto.I18N_PREFIX));
			if (!dateColumnIds.contains(columnId)) {
				column.setStyleGenerator(FieldAccessColumnStyleGenerator.getDefault(getBeanType(), columnId));
			}
		}

		addItemClickListener(
			new ShowDetailsListener<>(ContactFollowUpDto.UUID, e -> ControllerProvider.getContactController().navigateToData(e.getUuid())));
	}

	public void setVisitColumns(ContactCriteria criteria) {
		Date referenceDate = criteria.getFollowUpVisitsTo();
		int interval = criteria.getFollowUpVisitsInterval();

		setDataProvider(referenceDate, interval - 1);
		setCriteria(criteria);
		dates.forEach(date -> removeColumn(DateFormatHelper.formatDate(date)));

		setDates(referenceDate, interval);
		dateColumnIds.clear();

		for (int i = 0; i < interval; i++) {
			String columnId = DateFormatHelper.formatDate(dates.get(i));
			dateColumnIds.add(columnId);
			addComponentColumn(followUpDto -> new Label("")).setId(columnId);

			final int index = i;
			getColumn(columnId).setCaption(columnId).setSortable(false).setStyleGenerator((StyleGenerator<ContactFollowUpDto>) item -> {
				final VisitResultDto visitResult = item.getVisitResults()[index];
				final Date date = dates.get(index);
				return getVisitResultCssStyle(
					visitResult,
					date,
					ContactLogic.getStartDate(item.getLastContactDate(), item.getReportDate()),
					item.getFollowUpUntil());
			}).setDescriptionGenerator((DescriptionGenerator<ContactFollowUpDto>) item -> {
				final VisitResultDto visitResult = item.getVisitResults()[index];
				final Date date = dates.get(index);
				return getVisitResultDescription(
					visitResult,
					date,
					ContactLogic.getStartDate(item.getLastContactDate(), item.getReportDate()),
					item.getFollowUpUntil());
			});
		}
	}

	public void reload() {
		getDataProvider().refreshAll();
	}

	public void setDataProvider(Date referenceDate, int interval) {

		setLazyDataProvider(
			(criteria, first, max, sortProperties) -> FacadeProvider.getContactFacade()
				.getContactFollowUpList(criteria, referenceDate, interval, first, max, sortProperties),
			FacadeProvider.getContactFacade()::count);
	}

	private void setDates(Date referenceDate, int interval) {

		dates.clear();
		for (int i = 0; i < interval; i++) {
			dates.add(DateHelper.subtractDays(referenceDate, interval - i - 1));
		}
	}
}
