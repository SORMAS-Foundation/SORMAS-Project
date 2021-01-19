package de.symeda.sormas.ui.labmessage;

import java.util.Date;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import com.vaadin.data.provider.DataProvider;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.shared.data.sort.SortDirection;
import com.vaadin.ui.Grid;
import com.vaadin.ui.renderers.DateRenderer;
import com.vaadin.ui.renderers.HtmlRenderer;
import com.vaadin.ui.themes.ValoTheme;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.labmessage.LabMessageCriteria;
import de.symeda.sormas.api.labmessage.LabMessageIndexDto;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.api.utils.SortProperty;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.utils.ButtonHelper;
import de.symeda.sormas.ui.utils.FilteredGrid;
import de.symeda.sormas.ui.utils.LabMessageStatusRenderer;
import de.symeda.sormas.ui.utils.ShowDetailsListener;
import de.symeda.sormas.ui.utils.UuidRenderer;

public class LabMessageGrid extends FilteredGrid<LabMessageIndexDto, LabMessageCriteria> {

	public static final String SHOW_MESSAGE = "show_message";
	public static final String COLUMN_PROCESS = "process";

	public LabMessageGrid(LabMessageCriteria criteria) {
		super(LabMessageIndexDto.class);
		setSizeFull();

		setSelectionMode(SelectionMode.NONE);

		DataProvider<LabMessageIndexDto, LabMessageCriteria> dataProvider = DataProvider.fromFilteringCallbacks(
			query -> FacadeProvider.getLabMessageFacade()
				.getIndexList(
					query.getFilter().orElse(null),
					query.getOffset(),
					query.getLimit(),
					query.getSortOrders()
						.stream()
						.map(sortOrder -> new SortProperty(sortOrder.getSorted(), sortOrder.getDirection() == SortDirection.ASCENDING))
						.collect(Collectors.toList()))
				.stream(),
			query -> (int) FacadeProvider.getLabMessageFacade().count(query.getFilter().orElse(null)));
		setDataProvider(dataProvider);

		setCriteria(criteria);

		addShowColumn(e -> ControllerProvider.getLabMessageController().show(e.getUuid()));

		addComponentColumn(indexDto -> indexDto.isProcessed() ? null : ButtonHelper.createButton(Captions.labMessageProcess, e -> {
			ControllerProvider.getLabMessageController().process(indexDto.getUuid());
		}, ValoTheme.BUTTON_PRIMARY)).setId(COLUMN_PROCESS);

		setColumns(
			SHOW_MESSAGE,
			LabMessageIndexDto.UUID,
			LabMessageIndexDto.MESSAGE_DATE_TIME,
			LabMessageIndexDto.TESTED_DISEASE,
			LabMessageIndexDto.TEST_RESULT,
			LabMessageIndexDto.PERSON_FIRST_NAME,
			LabMessageIndexDto.PERSON_LAST_NAME,
			LabMessageIndexDto.PROCESSED,
			COLUMN_PROCESS);

		((Column<LabMessageIndexDto, String>) getColumn(LabMessageIndexDto.UUID)).setRenderer(new UuidRenderer());
		((Column<LabMessageIndexDto, Date>) getColumn(LabMessageIndexDto.MESSAGE_DATE_TIME))
			.setRenderer(new DateRenderer(DateHelper.getLocalDateTimeFormat(I18nProperties.getUserLanguage())));
		((Column<LabMessageIndexDto, Boolean>) getColumn(LabMessageIndexDto.PROCESSED)).setRenderer(new LabMessageStatusRenderer());

		for (Grid.Column<?, ?> column : getColumns()) {
			column.setCaption(I18nProperties.getPrefixCaption(LabMessageIndexDto.I18N_PREFIX, column.getId(), column.getCaption()));
		}
	}

	protected void addShowColumn(Consumer<LabMessageIndexDto> handler) {

		Column<LabMessageIndexDto, String> editColumn = addColumn(entry -> VaadinIcons.EYE.getHtml(), new HtmlRenderer());
		editColumn.setId(SHOW_MESSAGE);
		editColumn.setSortable(false);
		editColumn.setWidth(20);

		addItemClickListener(new ShowDetailsListener<>(SHOW_MESSAGE, e -> handler.accept(e)));
	}

	public void reload() {
		if (getSelectionModel().isUserSelectionAllowed()) {
			deselectAll();
		}

		getDataProvider().refreshAll();
	}
}
