package de.symeda.sormas.ui.configuration.docgeneration;

import java.io.ByteArrayInputStream;

import com.vaadin.data.provider.DataProvider;
import com.vaadin.data.provider.ListDataProvider;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.server.FileDownloader;
import com.vaadin.server.Page;
import com.vaadin.server.StreamResource;
import com.vaadin.ui.Button;
import com.vaadin.ui.Grid;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Notification;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.docgeneneration.QuarantineOrderFacade;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.utils.ValidationException;
import de.symeda.sormas.ui.utils.ButtonHelper;
import de.symeda.sormas.ui.utils.VaadinUiUtil;

public class QuarantineTemplatesGrid extends Grid<String> {

	private static final long serialVersionUID = 2589713987152595369L;

	public QuarantineTemplatesGrid() {
		super(String.class);
		setSizeFull();

		ListDataProvider<String> dataProvider = DataProvider.fromStream(FacadeProvider.getQuarantineOrderFacade().getAvailableTemplates().stream());
		setDataProvider(dataProvider);

		removeAllColumns();
		addColumn(x -> x.toString()).setCaption(I18nProperties.getString(Strings.fileName)).setExpandRatio(1);
		addComponentColumn(this::buildActionButtons).setCaption(I18nProperties.getCaption(Captions.eventActionsView))
			.setWidth(100)
			.setStyleGenerator(item -> "v-align-center");

		setSelectionMode(SelectionMode.NONE);
	}

	public void reload() {
		// This is bad practice but it works (unlike refreshAll), and in this case its sufficient
		setItems(FacadeProvider.getQuarantineOrderFacade().getAvailableTemplates());
		getDataProvider().refreshAll();
	}

	private Button buildDeleteButton(String s) {
		Button deleteButton = ButtonHelper.createIconButton("", VaadinIcons.TRASH, e -> {
			VaadinUiUtil.showDeleteConfirmationWindow(String.format(I18nProperties.getString(Strings.confirmationDeleteFile), s), () -> {
				try {
					FacadeProvider.getQuarantineOrderFacade().deleteQuarantineTemplate(s.toString());
				} catch (ValidationException ex) {
					new Notification("header i18n delete failed", "content i18n" + ex.getMessage(), Notification.Type.ERROR_MESSAGE, false)
						.show(Page.getCurrent());
				}
				reload();
			});
		});
		return deleteButton;
	}

	private Button buildViewDocumentButton(String s) {
		Button viewButton = ButtonHelper.createIconButton("", VaadinIcons.FILE_TEXT, e -> {
			try {
				FacadeProvider.getQuarantineOrderFacade().getTemplate(s.toString());
			} catch (ValidationException ex) {
				new Notification("header i18n view failed", "content i18n " + ex.getMessage(), Notification.Type.ERROR_MESSAGE, false)
					.show(Page.getCurrent());
			}
		});

		StreamResource streamResource = new StreamResource((StreamResource.StreamSource) () -> {
			QuarantineOrderFacade quarantineOrderFacade = FacadeProvider.getQuarantineOrderFacade();
			try {
				return new ByteArrayInputStream(quarantineOrderFacade.getTemplate(s));
			} catch (ValidationException e) {
				e.printStackTrace();
				return null;
			}
		}, s);
		FileDownloader fileDownloader = new FileDownloader(streamResource);
		fileDownloader.extend(viewButton);
		fileDownloader.setFileDownloadResource(streamResource);

		return viewButton;
	}

	private HorizontalLayout buildActionButtons(String s) {
		HorizontalLayout lay = new HorizontalLayout();

		Button delBut = buildDeleteButton(s);
		Button viewBut = buildViewDocumentButton(s);
		lay.addComponent(viewBut);
		lay.addComponent(delBut);

		lay.setSpacing(false);
		lay.setMargin(false);
		lay.setWidth("100px");

		return lay;
	}
}
