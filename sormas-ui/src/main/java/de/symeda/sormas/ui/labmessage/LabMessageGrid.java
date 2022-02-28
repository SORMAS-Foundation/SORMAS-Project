/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2021 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package de.symeda.sormas.ui.labmessage;

import java.io.ByteArrayInputStream;
import java.util.Date;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import com.vaadin.data.provider.DataProvider;
import com.vaadin.data.provider.DataProviderListener;
import com.vaadin.data.provider.ListDataProvider;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.server.FileDownloader;
import com.vaadin.server.StreamResource;
import com.vaadin.shared.data.sort.SortDirection;
import com.vaadin.ui.Button;
import com.vaadin.ui.Grid;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.renderers.DateRenderer;
import com.vaadin.ui.renderers.HtmlRenderer;
import com.vaadin.ui.themes.ValoTheme;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.labmessage.LabMessageCriteria;
import de.symeda.sormas.api.labmessage.LabMessageDto;
import de.symeda.sormas.api.labmessage.LabMessageIndexDto;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.api.utils.SortProperty;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.ViewModelProviders;
import de.symeda.sormas.ui.utils.ButtonHelper;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.FilteredGrid;
import de.symeda.sormas.ui.utils.ShowDetailsListener;
import de.symeda.sormas.ui.utils.UuidRenderer;
import de.symeda.sormas.ui.utils.ViewConfiguration;

public class LabMessageGrid extends FilteredGrid<LabMessageIndexDto, LabMessageCriteria> {

	private static final long serialVersionUID = 1772731113092823534L;

	private static final String COLUMN_DOWNLOAD = "download";
	private static final String COLUMN_PROCESS = "process";
	private static final String SHOW_MESSAGE = "show_message";
	private static final String EDIT_ASSIGNEE = "edit_assignee";

	private static final String PDF_FILENAME_FORMAT = "sormas_lab_message_%s_%s.pdf";

	private DataProviderListener<LabMessageIndexDto> dataProviderListener;

	@SuppressWarnings("unchecked")
	public LabMessageGrid(LabMessageCriteria criteria) {
		super(LabMessageIndexDto.class);
		setSizeFull();

		ViewConfiguration viewConfiguration = ViewModelProviders.of(LabMessagesView.class).get(ViewConfiguration.class);
		setInEagerMode(viewConfiguration.isInEagerMode());

		if (isInEagerMode()) {
			setCriteria(criteria);
			setEagerDataProvider();
		} else {
			setLazyDataProvider();
			setCriteria(criteria);
		}

		addShowColumn(e -> ControllerProvider.getLabMessageController().showLabMessage(e.getUuid(), true, this::reload));

		addComponentColumn(this::buildAssigneeLayout).setId(EDIT_ASSIGNEE)
			.setCaption(I18nProperties.getPrefixCaption(LabMessageDto.I18N_PREFIX, LabMessageDto.ASSIGNEE))
			.setSortable(false);

		addComponentColumn(
			indexDto -> indexDto.getStatus().isProcessable()
				? ButtonHelper.createButton(
					Captions.labMessageProcess,
					e -> ControllerProvider.getLabMessageController().processLabMessage(indexDto.getUuid()),
					ValoTheme.BUTTON_PRIMARY)
				: null).setId(COLUMN_PROCESS).setMinimumWidth(100);

		addComponentColumn(this::buildDownloadButton).setId(COLUMN_DOWNLOAD);

		setColumns(
			SHOW_MESSAGE,
			LabMessageIndexDto.UUID,
			LabMessageIndexDto.MESSAGE_DATE_TIME,
			LabMessageIndexDto.LAB_NAME,
			LabMessageIndexDto.LAB_POSTAL_CODE,
			LabMessageIndexDto.TESTED_DISEASE,
			LabMessageIndexDto.SAMPLE_OVERALL_TEST_RESULT,
			LabMessageIndexDto.PERSON_FIRST_NAME,
			LabMessageIndexDto.PERSON_LAST_NAME,
			LabMessageIndexDto.PERSON_BIRTH_DATE,
			LabMessageIndexDto.PERSON_POSTAL_CODE,
			LabMessageIndexDto.STATUS,
			EDIT_ASSIGNEE,
			COLUMN_PROCESS,
			COLUMN_DOWNLOAD);

		((Column<LabMessageIndexDto, String>) getColumn(LabMessageIndexDto.UUID)).setRenderer(new UuidRenderer());
		((Column<LabMessageIndexDto, Date>) getColumn(LabMessageIndexDto.MESSAGE_DATE_TIME))
			.setRenderer(new DateRenderer(DateHelper.getLocalDateTimeFormat(I18nProperties.getUserLanguage())));
		((Column<LabMessageIndexDto, Date>) getColumn(LabMessageIndexDto.PERSON_BIRTH_DATE))
			.setRenderer(new DateRenderer(DateHelper.getLocalDateFormat(I18nProperties.getUserLanguage())));

		getColumn(COLUMN_PROCESS).setSortable(false);
		getColumn(COLUMN_DOWNLOAD).setSortable(false);

		for (Grid.Column<?, ?> column : getColumns()) {
			column.setCaption(I18nProperties.getPrefixCaption(LabMessageIndexDto.I18N_PREFIX, column.getId(), column.getCaption()));
		}
	}

	protected void addShowColumn(Consumer<LabMessageIndexDto> handler) {

		Column<LabMessageIndexDto, String> editColumn = addColumn(entry -> VaadinIcons.EYE.getHtml(), new HtmlRenderer());
		editColumn.setId(SHOW_MESSAGE);
		editColumn.setSortable(false);
		editColumn.setWidth(20);

		addItemClickListener(new ShowDetailsListener<>(SHOW_MESSAGE, handler));
	}

	public void setEagerDataProvider() {

		ListDataProvider<LabMessageIndexDto> dataProvider =
			DataProvider.fromStream(FacadeProvider.getLabMessageFacade().getIndexList(getCriteria(), null, null, null).stream());
		setDataProvider(dataProvider);

		setSelectionMode(SelectionMode.MULTI);

		if (dataProviderListener != null) {
			dataProvider.addDataProviderListener(dataProviderListener);
		}
	}

	public void setLazyDataProvider() {
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
		setSelectionMode(SelectionMode.NONE);

		if (dataProviderListener != null) {
			dataProvider.addDataProviderListener(dataProviderListener);
		}
	}

	public void reload() {
		if (getSelectionModel().isUserSelectionAllowed()) {
			deselectAll();
		}

		if (ViewModelProviders.of(LabMessagesView.class).get(ViewConfiguration.class).isInEagerMode()) {
			setEagerDataProvider();
		}

		getDataProvider().refreshAll();
	}

	public void updateProcessColumnVisibility(boolean visible) {
		getColumn(LabMessageGrid.COLUMN_PROCESS).setHidden(!visible);
	}

	private HorizontalLayout buildAssigneeLayout(LabMessageIndexDto labMessage) {
		HorizontalLayout layout = new HorizontalLayout();
		Button button = new Button();
		CssStyles.style(button, ValoTheme.BUTTON_LINK, CssStyles.BUTTON_COMPACT);
		if (labMessage.getAssignee() == null) {
			button.setCaption(I18nProperties.getCaption(Captions.assign));
		} else {
			Label label = new Label(labMessage.getAssignee().getCaption());
			layout.addComponent(label);
			button.setIcon((VaadinIcons.ELLIPSIS_DOTS_V));
			CssStyles.style(button, CssStyles.ALIGN_RIGHT);
		}
		button.addClickListener(e -> ControllerProvider.getLabMessageController().editAssignee(labMessage.getUuid()));
		layout.addComponent(button);
		return layout;
	}

	private Button buildDownloadButton(LabMessageIndexDto labMessage) {
		Button downloadButton = new Button(VaadinIcons.DOWNLOAD);
		downloadButton.setDescription(I18nProperties.getString(Strings.headingLabMessageDownload));
		final String fileName =
			String.format(PDF_FILENAME_FORMAT, DataHelper.getShortUuid(labMessage.getUuid()), DateHelper.formatDateForExport(new Date()));

		StreamResource streamResource = new StreamResource(
			(StreamResource.StreamSource) () -> ControllerProvider.getLabMessageController()
				.convertToPDF(labMessage.getUuid())
				.map(ByteArrayInputStream::new)
				.orElse(null),
			fileName);
		streamResource.setMIMEType("text/pdf");

		FileDownloader fileDownloader = new FileDownloader(streamResource);
		fileDownloader.extend(downloadButton);
		fileDownloader.setFileDownloadResource(streamResource);

		return downloadButton;
	}

	public void setDataProviderListener(DataProviderListener<LabMessageIndexDto> dataProviderListener) {
		this.dataProviderListener = dataProviderListener;
	}
}
