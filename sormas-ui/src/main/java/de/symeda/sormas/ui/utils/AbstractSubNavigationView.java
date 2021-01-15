/*******************************************************************************
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2018 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 *******************************************************************************/
package de.symeda.sormas.ui.utils;

import java.util.Optional;

import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import de.symeda.sormas.ui.SubMenu;

@SuppressWarnings("serial")
public abstract class AbstractSubNavigationView extends AbstractView {

	private String params;

	private SubMenu subNavigationMenu;
	private HorizontalLayout buttonsLayout;
	private VerticalLayout infoLayout;
	private Label infoLabel;
	private Label infoLabelSub;
	private Component subComponent;

	protected AbstractSubNavigationView(String viewName) {
		super(viewName);

		subNavigationMenu = new SubMenu();
		addComponent(subNavigationMenu);
		setExpandRatio(subNavigationMenu, 0);

		createButtonsLayout().ifPresent(l -> {
			buttonsLayout = l;
			addHeaderComponent(l);
		});

		createInfoLayout().ifPresent(l -> {
			infoLayout = l;
			addHeaderComponent(l);
		});
	}

	protected Optional<HorizontalLayout> createButtonsLayout() {
		HorizontalLayout buttonsLayout = new HorizontalLayout();
		buttonsLayout.setMargin(false);

		return Optional.of(buttonsLayout);
	}

	protected Optional<VerticalLayout> createInfoLayout() {

		VerticalLayout infoLayout = new VerticalLayout();
		infoLayout.setMargin(false);
		infoLayout.setSpacing(false);
		infoLayout.setSizeUndefined();
		CssStyles.stylePrimary(infoLayout, CssStyles.CALLOUT);
		infoLabel = new Label("");
		infoLabelSub = new Label("");
		CssStyles.style(infoLabelSub, ValoTheme.LABEL_SMALL);
		infoLayout.addComponent(infoLabel);
		infoLayout.addComponent(infoLabelSub);

		return Optional.of(infoLayout);
	}

	@Override
	public void enter(ViewChangeEvent event) {

		params = event.getParameters();
		refreshMenu(subNavigationMenu, infoLabel, infoLabelSub, params);
		selectInMenu();
	}

	public abstract void refreshMenu(SubMenu menu, Label infoLabel, Label infoLabelSub, String params);

	protected void setSubComponent(Component newComponent) {

		if (subComponent != null) {
			removeComponent(subComponent);
		}
		subComponent = newComponent;
		if (subComponent != null) {
			// Make sure that the sub component is always the first component below the navigation
			addComponent(subComponent, 2);
			setExpandRatio(subComponent, 1);
		}
	}

	protected void hideInfoLabel() {
		infoLayout.setVisible(false);
	}

	public void selectInMenu() {
		subNavigationMenu.setActiveView(viewName);
	}

	public HorizontalLayout getButtonsLayout() {
		return buttonsLayout;
	}
}
