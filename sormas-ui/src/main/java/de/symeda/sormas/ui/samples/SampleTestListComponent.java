package de.symeda.sormas.ui.samples;

import com.vaadin.server.FontAwesome;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import de.symeda.sormas.api.sample.SampleReferenceDto;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.login.LoginHelper;
import de.symeda.sormas.ui.utils.LayoutUtil;

@SuppressWarnings("serial")
public class SampleTestListComponent extends VerticalLayout {

	private SampleTestList list;
	private Button createButton;

	public SampleTestListComponent(SampleReferenceDto sampleRef) {
		setWidth(100, Unit.PERCENTAGE);

		HorizontalLayout componentHeader = new HorizontalLayout();
		componentHeader.setWidth(100, Unit.PERCENTAGE);
		addComponent(componentHeader);

		list = new SampleTestList(sampleRef);
		addComponent(list);
		list.reload();

		Label tasksHeader = new Label(LayoutUtil.h3("Tests"), ContentMode.HTML);
		componentHeader.addComponent(tasksHeader);

		if (LoginHelper.hasUserRight(UserRight.SAMPLE_CREATE)) {
			createButton = new Button("New test");
			createButton.addStyleName(ValoTheme.BUTTON_PRIMARY);
			createButton.setIcon(FontAwesome.PLUS_CIRCLE);
			createButton.addClickListener(
					e -> ControllerProvider.getSampleTestController().create(sampleRef, 0, list::reload));
			componentHeader.addComponent(createButton);
			componentHeader.setComponentAlignment(createButton, Alignment.MIDDLE_RIGHT);
		}
	}

	public void reload() {
		list.reload();
	}
}
