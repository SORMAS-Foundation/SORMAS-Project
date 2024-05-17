package de.symeda.sormas.ui.samples;

import java.util.function.Consumer;

import com.vaadin.ui.Button;

import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.UiUtil;
import de.symeda.sormas.ui.utils.components.sidecomponent.SideComponent;

@SuppressWarnings("serial")
public class AdditionalTestListComponent extends SideComponent {

	private AdditionalTestList list;
	private Button createButton;

	public AdditionalTestListComponent(String sampleUuid, Consumer<Runnable> actionCallback, boolean isEditAllowed) {
		super(I18nProperties.getString(Strings.headingAdditionalTests), actionCallback);

		list = new AdditionalTestList(sampleUuid, actionCallback, isEditAllowed);
		addComponent(list);
		list.reload();

		if (UiUtil.permitted(isEditAllowed, UserRight.ADDITIONAL_TEST_CREATE)) {
			addCreateButton(
				I18nProperties.getCaption(Captions.additionalTestNewTest),
				() -> ControllerProvider.getAdditionalTestController().openCreateComponent(sampleUuid, list::reload),
				UserRight.ADDITIONAL_TEST_CREATE);
		}
	}

	public void reload() {
		list.reload();
	}
}
