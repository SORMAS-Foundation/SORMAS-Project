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

package de.symeda.sormas.ui.externalmessage;

import java.io.ByteArrayInputStream;
import java.util.Collections;
import java.util.Date;
import java.util.function.Consumer;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import com.vaadin.data.provider.DataProviderListener;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.server.FileDownloader;
import com.vaadin.server.StreamResource;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.Grid;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.renderers.DateRenderer;
import com.vaadin.ui.renderers.HtmlRenderer;
import com.vaadin.ui.themes.ValoTheme;

import de.symeda.sormas.api.CountryHelper;
import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.externalmessage.ExternalMessageCriteria;
import de.symeda.sormas.api.externalmessage.ExternalMessageDto;
import de.symeda.sormas.api.externalmessage.ExternalMessageIndexDto;
import de.symeda.sormas.api.externalmessage.ExternalMessageType;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.UserProvider;
import de.symeda.sormas.ui.ViewModelProviders;
import de.symeda.sormas.ui.utils.ButtonHelper;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.FilteredGrid;
import de.symeda.sormas.ui.utils.ShowDetailsListener;
import de.symeda.sormas.ui.utils.UuidRenderer;
import de.symeda.sormas.ui.utils.ViewConfiguration;

public class ExternalMessageGrid extends FilteredGrid<ExternalMessageIndexDto, ExternalMessageCriteria> {

	private static final long serialVersionUID = 1772731113092823534L;

	private static final String COLUMN_DOWNLOAD = "download";
	private static final String COLUMN_PROCESS = "process";
	private static final String SHOW_MESSAGE = "show_message";
	private static final String EDIT_ASSIGNEE = "edit_assignee";

	private static final String PLACEHOLDER_SPACE = String.join("", Collections.nCopies(35, "&nbsp"));
	private static final String PDF_FILENAME_FORMAT = "sormas_lab_message_%s_%s.pdf";

	private DataProviderListener<ExternalMessageIndexDto> dataProviderListener;

	@SuppressWarnings("unchecked")
	public ExternalMessageGrid(ExternalMessageCriteria criteria) {
		super(ExternalMessageIndexDto.class);
		setSizeFull();

		ViewConfiguration viewConfiguration = ViewModelProviders.of(ExternalMessagesView.class).get(ViewConfiguration.class);
		setInEagerMode(viewConfiguration.isInEagerMode());

		if (isInEagerMode()) {
			setCriteria(criteria);
			setEagerDataProvider();
		} else {
			setLazyDataProvider();
			setCriteria(criteria);
		}

		addShowColumn(e -> ControllerProvider.getExternalMessageController().showExternalMessage(e.getUuid(), true, this::reload));

		addComponentColumn(this::buildAssigneeLayout).setId(EDIT_ASSIGNEE)
			.setCaption(I18nProperties.getPrefixCaption(ExternalMessageDto.I18N_PREFIX, ExternalMessageDto.ASSIGNEE))
			.setSortable(false);

		addComponentColumn(this::buildProcessComponent).setId(COLUMN_PROCESS).setSortable(false);
		addComponentColumn(this::buildDownloadButton).setId(COLUMN_DOWNLOAD).setSortable(false);

		String[] columns = new String[] {
			SHOW_MESSAGE,
			ExternalMessageIndexDto.UUID };
		if (!FacadeProvider.getConfigFacade().isConfiguredCountry(CountryHelper.COUNTRY_CODE_LUXEMBOURG)) {
			columns = ArrayUtils.add(columns, ExternalMessageIndexDto.TYPE);
		}
		columns = ArrayUtils.addAll(
			columns,
			ExternalMessageIndexDto.MESSAGE_DATE_TIME,
			ExternalMessageIndexDto.REPORTER_NAME,
			ExternalMessageIndexDto.REPORTER_POSTAL_CODE,
			ExternalMessageIndexDto.DISEASE,
			ExternalMessageIndexDto.DISEASE_VARIANT,
			ExternalMessageIndexDto.PERSON_FIRST_NAME,
			ExternalMessageIndexDto.PERSON_LAST_NAME,
			ExternalMessageIndexDto.PERSON_BIRTH_DATE,
			ExternalMessageIndexDto.PERSON_POSTAL_CODE,
			ExternalMessageIndexDto.STATUS,
			EDIT_ASSIGNEE,
			COLUMN_PROCESS,
			COLUMN_DOWNLOAD);
		setColumns(columns);

		((Column<ExternalMessageIndexDto, String>) getColumn(ExternalMessageIndexDto.UUID)).setRenderer(new UuidRenderer());
		((Column<ExternalMessageIndexDto, Date>) getColumn(ExternalMessageIndexDto.MESSAGE_DATE_TIME))
			.setRenderer(new DateRenderer(DateHelper.getLocalDateTimeFormat(I18nProperties.getUserLanguage())));
		((Column<ExternalMessageIndexDto, Date>) getColumn(ExternalMessageIndexDto.PERSON_BIRTH_DATE))
			.setRenderer(new DateRenderer(DateHelper.getLocalDateFormat(I18nProperties.getUserLanguage())));

		for (Grid.Column<?, ?> column : getColumns()) {
			column.setCaption(I18nProperties.getPrefixCaption(ExternalMessageIndexDto.I18N_PREFIX, column.getId(), column.getCaption()));
			if (StringUtils.isBlank(column.getCaption())) {
				column.setCaption("\uFEFF");
			}
		}
	}

	protected void addShowColumn(Consumer<ExternalMessageIndexDto> handler) {

		Column<ExternalMessageIndexDto, String> editColumn = addColumn(entry -> VaadinIcons.EYE.getHtml(), new HtmlRenderer());
		editColumn.setId(SHOW_MESSAGE);
		editColumn.setSortable(false);
		editColumn.setWidth(20);

		addItemClickListener(new ShowDetailsListener<>(SHOW_MESSAGE, handler));
	}

	public void setEagerDataProvider() {
		setEagerDataProvider(FacadeProvider.getExternalMessageFacade()::getIndexList);
	}

	public void setLazyDataProvider() {
		setLazyDataProvider(FacadeProvider.getExternalMessageFacade()::getIndexList, FacadeProvider.getExternalMessageFacade()::count);
	}

	public void reload() {
		if (getSelectionModel().isUserSelectionAllowed()) {
			deselectAll();
		}

		if (ViewModelProviders.of(ExternalMessagesView.class).get(ViewConfiguration.class).isInEagerMode()) {
			setEagerDataProvider();
		}

		getDataProvider().refreshAll();
	}

	public void updateProcessColumnVisibility(boolean visible) {
		getColumn(ExternalMessageGrid.COLUMN_PROCESS).setHidden(!visible);
	}

	private HorizontalLayout buildAssigneeLayout(ExternalMessageIndexDto externalMessage) {
		HorizontalLayout layout = new HorizontalLayout();

		if (externalMessage.getAssignee() != null) {
			Label label = new Label(externalMessage.getAssignee().getCaption());
			layout.addComponent(label);
		}

		if (UserProvider.getCurrent().hasUserRight(UserRight.EXTERNAL_MESSAGE_PROCESS)) {
			Button button = new Button();
			CssStyles.style(button, ValoTheme.BUTTON_LINK, CssStyles.BUTTON_COMPACT);
			if (externalMessage.getAssignee() == null) {
				button.setCaption(I18nProperties.getCaption(Captions.assign));
			} else {
				button.setIcon((VaadinIcons.ELLIPSIS_DOTS_V));
				CssStyles.style(button, CssStyles.ALIGN_RIGHT);
			}
			button.addClickListener(e -> ControllerProvider.getExternalMessageController().editAssignee(externalMessage.getUuid()));
			layout.addComponent(button);
		}
		return layout;
	}

	private Component buildProcessComponent(ExternalMessageIndexDto indexDto) {
		if (UserProvider.getCurrent().hasUserRight(UserRight.EXTERNAL_MESSAGE_PROCESS)
			&& indexDto.getStatus().isProcessable()
			&& (indexDto.getType() != ExternalMessageType.PHYSICIANS_REPORT
				|| UserProvider.getCurrent().hasAllUserRights(UserRight.CASE_CREATE, UserRight.CASE_EDIT))) {
			// build process button
			return ButtonHelper.createButton(Captions.externalMessageProcess, e -> {
				if (ExternalMessageType.LAB_MESSAGE == indexDto.getType()) {
					ControllerProvider.getExternalMessageController().processLabMessage(indexDto.getUuid());
				} else if (ExternalMessageType.PHYSICIANS_REPORT == indexDto.getType()) {
					ControllerProvider.getExternalMessageController().processPhysiciansReport(indexDto.getUuid());
				}
			}, ValoTheme.BUTTON_PRIMARY);
		} else {
			// build placeholder necessary to circumvent a vaadin scaling issue (see #7681)
			Label placeholder = new Label(PLACEHOLDER_SPACE);
			placeholder.setContentMode(ContentMode.HTML);
			return placeholder;
		}
	}

	private Button buildDownloadButton(ExternalMessageIndexDto labMessage) {
		Button downloadButton = new Button(VaadinIcons.DOWNLOAD);
		downloadButton.setDescription(I18nProperties.getString(Strings.headingExternalMessageDownload));
		final String fileName =
			String.format(PDF_FILENAME_FORMAT, DataHelper.getShortUuid(labMessage.getUuid()), DateHelper.formatDateForExport(new Date()));

		StreamResource streamResource = new StreamResource(
			() -> ControllerProvider.getExternalMessageController().convertToPDF(labMessage.getUuid()).map(ByteArrayInputStream::new).orElse(null),
			fileName);
		streamResource.setMIMEType("text/pdf");

		FileDownloader fileDownloader = new FileDownloader(streamResource);
		fileDownloader.extend(downloadButton);
		fileDownloader.setFileDownloadResource(streamResource);

		return downloadButton;
	}
}
