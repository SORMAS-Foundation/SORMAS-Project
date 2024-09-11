package de.symeda.sormas.ui.news;

import java.util.Date;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.ui.renderers.DateRenderer;
import com.vaadin.ui.renderers.HtmlRenderer;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.news.NewsCriteria;
import de.symeda.sormas.api.news.NewsIndexDto;
import de.symeda.sormas.api.news.NewsReferenceDto;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.api.utils.HtmlHelper;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.UserProvider;
import de.symeda.sormas.ui.utils.FilteredGrid;
import de.symeda.sormas.ui.utils.ShowDetailsListener;
import de.symeda.sormas.ui.utils.UuidRenderer;
import elemental.json.JsonValue;

public class NewsGrid extends FilteredGrid<NewsIndexDto, NewsCriteria> {

	private final static String CREATE_EVENT_ACTION = "createEventAction";
	private boolean bulkEditMode;

	public NewsGrid(NewsCriteria criteria) {
		super(NewsIndexDto.class);
		setSizeFull();
		setLazyDataProvider();
		setCriteria(criteria);
		initColumns();

		setBulkEditMode(isInEagerMode());
	}

	private void initColumns() {
		Column<NewsIndexDto, String> createEvent = addColumn(entry -> VaadinIcons.PLUS_CIRCLE.getHtml(), new HtmlRenderer());
		createEvent.setId(CREATE_EVENT_ACTION);
		createEvent.setCaption(I18nProperties.getCaption(Captions.createEvent));
		createEvent.setWidth(100);

		setColumns(
			NewsIndexDto.UUID,
			NewsIndexDto.TITLE,
			NewsIndexDto.DISEASE,
			NewsIndexDto.DESCRIPTION,
			NewsIndexDto.REGION,
			NewsIndexDto.DISTRICT,
			NewsIndexDto.COMMUNITY,
			NewsIndexDto.NEWS_DATE,
			NewsIndexDto.RISK_LEVEL,
			CREATE_EVENT_ACTION,
			NewsIndexDto.STATUS);
		((Column<NewsIndexDto, String>) getColumn(NewsIndexDto.UUID)).setRenderer(new UuidRenderer());
		getColumn(NewsIndexDto.UUID).setWidth(60);
		getColumn(NewsIndexDto.DESCRIPTION).setWidth(600);
		getColumn(NewsIndexDto.REGION).setWidth(80);
		getColumn(NewsIndexDto.DISTRICT).setWidth(80);
		getColumn(NewsIndexDto.RISK_LEVEL).setWidth(80);
		getColumn(NewsIndexDto.DISEASE).setWidth(80);

		getColumn(CREATE_EVENT_ACTION).setSortable(false);
		((Column<NewsIndexDto, String>) getColumn(NewsIndexDto.TITLE)).setRenderer(new HtmlRenderer() {

			@Override
			public JsonValue encode(String value) {
				return super.encode(HtmlHelper.buildHyperlinkTitle(value, value));
			}
		});
		((Column<NewsIndexDto, Date>) getColumn(NewsIndexDto.NEWS_DATE))
			.setRenderer(new DateRenderer(DateHelper.getLocalDateTimeFormat(I18nProperties.getUserLanguage())));

		addItemClickListener(new ShowDetailsListener<>(NewsIndexDto.TITLE, e -> {
			if (UserProvider.getCurrent().hasUserRight(UserRight.EDIT_NEWS)) {
				ControllerProvider.getNewsController().updateNewsPopUP(e);
			} else {
				getUI().getPage().open(e.getNewsLink(), "_blank");
			}
		}));
		addItemClickListener(new ShowDetailsListener<>(NewsIndexDto.UUID, e -> ControllerProvider.getNewsController().navigateToData(e.getUuid())));

		if (UserProvider.getCurrent().hasUserRight(UserRight.EVENT_CREATE)) {
			addItemClickListener(
				new ShowDetailsListener<>(
					CREATE_EVENT_ACTION,
					e -> ControllerProvider.getEventController().create(new NewsReferenceDto(e.getUuid()))));
		} else {
			removeColumn(CREATE_EVENT_ACTION);
		}
		if (!UserProvider.getCurrent().hasUserRight(UserRight.EDIT_NEWS)) {
			removeColumn(NewsIndexDto.STATUS);
		}
	}

	protected void setBulkEditMode(boolean bulkEditMode) {
		this.bulkEditMode = bulkEditMode;
		if (bulkEditMode && UserProvider.getCurrent().hasUserRight(UserRight.PERFORM_BULK_OPERATIONS)) {
			setSelectionMode(SelectionMode.MULTI);
		} else {
			setSelectionMode(SelectionMode.NONE);
		}
	}

	public void reload() {
		if (getSelectionModel().isUserSelectionAllowed()) {
			deselectAll();
		}
		getDataProvider().refreshAll();
	}

	public void setLazyDataProvider() {
		setLazyDataProvider(FacadeProvider.getNewsFacade()::getIndexList, FacadeProvider.getNewsFacade()::count);
	}

	public void setEagerDataProvider() {
		setEagerDataProvider(FacadeProvider.getNewsFacade()::getIndexList);
	}
}
