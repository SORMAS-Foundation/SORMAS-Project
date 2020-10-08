package de.symeda.sormas.ui.configuration.infrastructure;

import com.vaadin.data.provider.DataProvider;
import com.vaadin.data.provider.ListDataProvider;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.ui.Button;
import com.vaadin.ui.Grid;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.i18n.Captions;
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
		addColumn(x -> x.toString()).setCaption("Document Title i18n");
		addComponentColumn(this::buildViewDocumentButton);
		addComponentColumn(this::buildDeleteButton);

		setSelectionMode(SelectionMode.NONE);
	}

	public void reload() {
		// This is bad practice but it works (unlike refreshAll), and in this case its sufficient
		setItems(FacadeProvider.getQuarantineOrderFacade().getAvailableTemplates());
		getDataProvider().refreshAll();
	}

	private Button buildDeleteButton(String s) {
		Button deleteButton = ButtonHelper.createIconButton(Captions.actionDelete, VaadinIcons.TRASH, e -> {
			VaadinUiUtil.showDeleteConfirmationWindow("Permanently delete \"" + s.toString() + "\"? (i18n required)", () -> {
				FacadeProvider.getQuarantineOrderFacade().deleteQuarantineTemplate(s.toString());
				reload();
			});
		});
		return deleteButton;
	}

	private Button buildViewDocumentButton(String s) {
		Button viewButton = ButtonHelper.createButton("View (i18n)", e -> {
		});
		return viewButton;
	}
}
