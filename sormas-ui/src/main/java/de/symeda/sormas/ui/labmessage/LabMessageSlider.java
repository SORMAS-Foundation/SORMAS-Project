package de.symeda.sormas.ui.labmessage;

import java.util.List;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.server.Sizeable;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.VerticalLayout;

import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.labmessage.LabMessageDto;
import de.symeda.sormas.ui.utils.ButtonHelper;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.VaadinUiUtil;

public class LabMessageSlider {

	private final List<LabMessageDto> labMessages;
	private final LabMessageForm form;
	private final Button topLeftButton;
	private final Button topRightButton;
	private final Button bottomLeftButton;
	private final Button bottomRightButton;
	private int index;

	public LabMessageSlider(List<LabMessageDto> labMessages) {
		VerticalLayout layout = new VerticalLayout();
		layout.setMargin(true);
		VaadinUiUtil.showPopupWindow(layout, I18nProperties.getString(Strings.headingShowLabMessage));

		this.index = 0;
		this.labMessages = labMessages;

		this.topLeftButton = ButtonHelper.createIconButton("", VaadinIcons.ANGLE_LEFT, (clickEvent) -> this.previous());
		this.topRightButton = ButtonHelper.createIconButton("", VaadinIcons.ANGLE_RIGHT, (clickEvent) -> this.next());

		HorizontalLayout topNavigator = new HorizontalLayout();
		topNavigator.setStyleName(CssStyles.FLOAT_RIGHT);
		topNavigator.addComponent(topLeftButton);
		topNavigator.addComponent(topRightButton);

		layout.addComponent(topNavigator);

		LabMessageForm form = new LabMessageForm();
		form.setWidth(550, Sizeable.Unit.PIXELS);
		form.setValue(labMessages.get(index));
		this.form = form;
		layout.addComponent(this.form);
		layout.addStyleName("lab-message-slider");

		this.bottomLeftButton = ButtonHelper.createIconButton("", VaadinIcons.ANGLE_LEFT, (clickEvent) -> this.previous());
		this.bottomRightButton = ButtonHelper.createIconButton("", VaadinIcons.ANGLE_RIGHT, (clickEvent) -> this.next());
		HorizontalLayout bottomNavigator = new HorizontalLayout();
		bottomNavigator.setStyleName(CssStyles.FLOAT_RIGHT);
		bottomNavigator.addComponent(bottomLeftButton);
		bottomNavigator.addComponent(bottomRightButton);

		layout.addComponent(bottomNavigator);

		this.enableButtons();
	}

	private void next() {
		this.index++;
		this.form.setValue(this.labMessages.get(this.index));
		this.enableButtons();
	}

	private void previous() {
		this.index--;
		this.form.setValue(this.labMessages.get(this.index));
		this.enableButtons();
	}

	private void enableButtons() {
		this.topRightButton.setEnabled(this.index < this.labMessages.size() - 1);
		this.topLeftButton.setEnabled(this.index > 0);

		this.bottomRightButton.setEnabled(this.index < this.labMessages.size() - 1);
		this.bottomLeftButton.setEnabled(this.index > 0);
	}

}
