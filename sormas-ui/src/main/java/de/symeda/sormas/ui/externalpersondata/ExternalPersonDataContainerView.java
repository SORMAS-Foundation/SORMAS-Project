package de.symeda.sormas.ui.externalpersondata;

import static de.symeda.sormas.api.externalpersondata.ExternalPersonDataProviderFacade.EXTERNAL_PERSON_DATA_PROVIDER_MENU_NAME_KEY;

import com.vaadin.navigator.ViewChangeListener;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.ui.SubMenu;
import de.symeda.sormas.ui.utils.AbstractView;

public class ExternalPersonDataContainerView extends AbstractView {

	public static final String VIEW_NAME = ExternalPersonDataListView.VIEW_NAME + "/data";
	private static final long serialVersionUID = 1L;
	private ExternalPersonDataComponent data;

	public ExternalPersonDataContainerView() {
		super(VIEW_NAME);
		SubMenu menu = new SubMenu();
		menu.addView(
			ExternalPersonDataListView.VIEW_NAME,
			FacadeProvider.getSystemConfigurationValueFacade()
				.getValueOrDefaultTo(EXTERNAL_PERSON_DATA_PROVIDER_MENU_NAME_KEY, I18nProperties.getCaption(Captions.View_externalPersons)));
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
