package de.symeda.sormas.ui.immunization.components.fields.pickorcreate;

import com.vaadin.server.ThemeResource;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Image;
import com.vaadin.v7.shared.ui.label.ContentMode;
import com.vaadin.v7.ui.Label;

public class InfoLayout extends HorizontalLayout {

	protected InfoLayout(String text) {
		setWidth(100, Unit.PERCENTAGE);
		setSpacing(true);
		Image icon = new Image(null, new ThemeResource("img/info-icon.png"));
		icon.setHeight(35, Unit.PIXELS);
		icon.setWidth(35, Unit.PIXELS);
		addComponent(icon);
		Label infoLabel = new Label(text);
		infoLabel.setContentMode(ContentMode.HTML);
		addComponent(infoLabel);
		setExpandRatio(infoLabel, 1);
	}
}
