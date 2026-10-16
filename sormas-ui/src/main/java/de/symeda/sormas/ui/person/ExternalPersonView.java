package de.symeda.sormas.ui.person;

import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.ui.Button;

import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.ui.SormasUI;
import de.symeda.sormas.ui.utils.AbstractView;

public class ExternalPersonView extends AbstractView {

	public static final String VIEW_NAME = ExternalPersonsView.VIEW_NAME + "/data";
	private static final long serialVersionUID = 1L;
	private ExternalPersonDataComponent data;

	public ExternalPersonView() {
		super(VIEW_NAME);
		addHeaderComponent(
			new Button(
				I18nProperties.getCaption("mainMenuExternalPersons"),
				event -> SormasUI.get().getNavigator().navigateTo(ExternalPersonsView.VIEW_NAME)));
	}

	@Override
	public void enter(ViewChangeListener.ViewChangeEvent event) {
		if (data != null) {
			removeComponent(data);
		}
		data = new ExternalPersonDataComponent(event.getParameters());
		addComponent(data);
	}
}
