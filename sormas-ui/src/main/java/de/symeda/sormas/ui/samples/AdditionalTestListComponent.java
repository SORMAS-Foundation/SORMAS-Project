package de.symeda.sormas.ui.samples;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.UserProvider;
import de.symeda.sormas.ui.utils.ButtonHelper;
import de.symeda.sormas.ui.utils.CssStyles;

@SuppressWarnings("serial")
public class AdditionalTestListComponent extends VerticalLayout {

	private AdditionalTestList list;
	private Button createButton;
	
	public AdditionalTestListComponent(String sampleUuid) {
		setWidth(100, Unit.PERCENTAGE);
		
		HorizontalLayout componentHeader = new HorizontalLayout();
		componentHeader.setWidth(100, Unit.PERCENTAGE);
		addComponent(componentHeader);
		
		list = new AdditionalTestList(sampleUuid);
		addComponent(list);
		list.reload();
		
		Label testsHeader = new Label(I18nProperties.getString(Strings.headingAdditionalTests));
		testsHeader.addStyleName(CssStyles.H3);
		componentHeader.addComponent(testsHeader);
		
		if (UserProvider.getCurrent().hasUserRight(UserRight.ADDITIONAL_TEST_CREATE)) {
			createButton = ButtonHelper.createIconButton(Captions.additionalTestNewTest, VaadinIcons.PLUS_CIRCLE, e -> {
				ControllerProvider.getAdditionalTestController().openCreateComponent(sampleUuid, list::reload);
			}, ValoTheme.BUTTON_PRIMARY);

			componentHeader.addComponent(createButton);
			componentHeader.setComponentAlignment(createButton, Alignment.MIDDLE_RIGHT);
		}
	}
	
	public void reload() {
		list.reload();
	}
	
}