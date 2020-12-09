package de.symeda.sormas.ui.labmessage;

import com.vaadin.ui.VerticalLayout;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.labmessage.LabMessageDto;
import de.symeda.sormas.ui.utils.VaadinUiUtil;

public class LabMessageController {

	public LabMessageController() {

	}

	public void show(String uuid) {

		LabMessageDto newDto = FacadeProvider.getLabMessageFacade().getByUuid(uuid);

		LabMessageEditForm form = new LabMessageEditForm(true);
		form.setValue(newDto);
		VerticalLayout layout = new VerticalLayout(form);
		layout.setMargin(true);
		VaadinUiUtil.showPopupWindow(layout, I18nProperties.getString(Strings.headingShowLabMessage));
	}
}
