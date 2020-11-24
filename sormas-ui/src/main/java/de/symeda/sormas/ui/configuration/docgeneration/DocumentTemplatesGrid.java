package de.symeda.sormas.ui.configuration.docgeneration;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;

import com.vaadin.data.provider.DataProvider;
import com.vaadin.data.provider.ListDataProvider;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.server.FileDownloader;
import com.vaadin.server.Page;
import com.vaadin.server.StreamResource;
import com.vaadin.shared.ui.grid.HeightMode;
import com.vaadin.ui.Button;
import com.vaadin.ui.Grid;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Notification;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.docgeneneration.DocumentWorkflow;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.ui.utils.ButtonHelper;
import de.symeda.sormas.ui.utils.VaadinUiUtil;

public class DocumentTemplatesGrid extends Grid<String> {

	private static final long serialVersionUID = 2589713987152595369L;

	private final DocumentWorkflow documentWorkflow;

	public DocumentTemplatesGrid(DocumentWorkflow documentWorkflow) {
		super(String.class);
		this.documentWorkflow = documentWorkflow;
		setSizeFull();

		List<String> availableTemplates = FacadeProvider.getDocumentTemplateFacade().getAvailableTemplates(documentWorkflow);
		ListDataProvider<String> dataProvider = DataProvider.fromStream(availableTemplates.stream());
		setDataProvider(dataProvider);

		removeAllColumns();
		addColumn(String::toString).setCaption(I18nProperties.getString(Strings.fileName)).setExpandRatio(1);
		addComponentColumn(this::buildActionButtons).setCaption(I18nProperties.getCaption(Captions.eventActionsView))
			.setWidth(100)
			.setStyleGenerator(item -> "v-align-center");

		setSelectionMode(SelectionMode.NONE);
		setHeightMode(HeightMode.ROW);
		setHeightByRows(availableTemplates.size());
	}

	public void reload() {
		// This is bad practice but it works (unlike refreshAll), and in this case its sufficient
		List<String> availableTemplates = FacadeProvider.getDocumentTemplateFacade().getAvailableTemplates(documentWorkflow);
		setItems(availableTemplates);
		getDataProvider().refreshAll();
		setHeightByRows(availableTemplates.size());
	}

	private Button buildDeleteButton(String templateFileName) {
		return ButtonHelper.createIconButton(
			"",
			VaadinIcons.TRASH,
			e -> VaadinUiUtil
				.showDeleteConfirmationWindow(String.format(I18nProperties.getString(Strings.confirmationDeleteFile), templateFileName), () -> {
					try {
						FacadeProvider.getDocumentTemplateFacade().deleteDocumentTemplate(documentWorkflow, templateFileName);
					} catch (IllegalArgumentException ex) {
						new Notification(
							I18nProperties.getString(Strings.errorDeletingDocumentTemplate),
							ex.getMessage(),
							Notification.Type.ERROR_MESSAGE,
							false).show(Page.getCurrent());
					}
					reload();
				}));
	}

	private Button buildViewDocumentButton(String templateFileName) {
		Button viewButton = new Button(VaadinIcons.DOWNLOAD);

		StreamResource streamResource = new StreamResource((StreamResource.StreamSource) () -> {
			try {
				return new ByteArrayInputStream(FacadeProvider.getDocumentTemplateFacade().getDocumentTemplate(documentWorkflow, templateFileName));
			} catch (IOException | IllegalArgumentException e) {
				new Notification(
					String.format(I18nProperties.getString(Strings.errorReadingTemplate), templateFileName),
					e.getMessage(),
					Notification.Type.ERROR_MESSAGE,
					false).show(Page.getCurrent());
				return null;
			}
		}, templateFileName);
		FileDownloader fileDownloader = new FileDownloader(streamResource);
		fileDownloader.extend(viewButton);
		fileDownloader.setFileDownloadResource(streamResource);

		return viewButton;
	}

	private HorizontalLayout buildActionButtons(String s) {
		HorizontalLayout horizontalLayout = new HorizontalLayout();

		horizontalLayout.addComponent(buildViewDocumentButton(s));
		horizontalLayout.addComponent(buildDeleteButton(s));

		horizontalLayout.setSpacing(false);
		horizontalLayout.setMargin(false);
		horizontalLayout.setWidth("100px");

		return horizontalLayout;
	}
}
