package de.symeda.sormas.ui.labmessage;

import java.util.List;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.server.Sizeable;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.labmessage.LabMessageDto;
import de.symeda.sormas.ui.utils.ButtonHelper;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.VaadinUiUtil;

public class LabMessageSlider {

	private final List<LabMessageDto> labMessages;
	private final LabMessageEditForm form;
	private final Button leftButton;
	private final Button rightButton;
	private int index;

	public LabMessageSlider(Runnable onShare, List<LabMessageDto> labMessages) {
		VerticalLayout layout = new VerticalLayout();
		layout.setMargin(true);
		Window window = VaadinUiUtil.showPopupWindow(layout, I18nProperties.getString(Strings.headingShowLabMessage));

		this.index = 0;
		this.labMessages = labMessages;
		LabMessageEditForm form = new LabMessageEditForm(true, true, () -> {
			window.close();
			onShare.run();
		});
		this.form = form;
		form.setWidth(550, Sizeable.Unit.PIXELS);
		form.setValue(labMessages.get(index));

		layout.addComponent(this.form);

		this.leftButton = ButtonHelper.createIconButton("", VaadinIcons.ANGLE_LEFT, (clickEvent) -> this.previous());
		this.rightButton = ButtonHelper.createIconButton("", VaadinIcons.ANGLE_RIGHT, (clickEvent) -> this.next());
		this.enableButtons();

		HorizontalLayout navigator = new HorizontalLayout();
		navigator.setStyleName(CssStyles.FLOAT_RIGHT);
		navigator.addComponent(leftButton);
		navigator.addComponent(rightButton);

		layout.addComponent(navigator);
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
		this.rightButton.setEnabled(this.index < this.labMessages.size() - 1);
		this.leftButton.setEnabled(this.index > 0);
	}

}
