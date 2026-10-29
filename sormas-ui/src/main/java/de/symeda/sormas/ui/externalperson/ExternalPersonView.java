package de.symeda.sormas.ui.externalperson;

import com.vaadin.navigator.ViewChangeListener;

import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.ui.SubMenu;
import de.symeda.sormas.ui.utils.AbstractView;

public class ExternalPersonView extends AbstractView {

	public static final String VIEW_NAME = ExternalPersonsView.VIEW_NAME + "/data";
	private static final long serialVersionUID = 1L;
	private ExternalPersonDataComponent data;

	public ExternalPersonView() {
		super(VIEW_NAME);
		SubMenu menu = new SubMenu();
		menu.addView(ExternalPersonsView.VIEW_NAME, I18nProperties.getCaption(Captions.mainMenuExternalPersons));
		addComponent(menu);
		setExpandRatio(menu, 0);
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
