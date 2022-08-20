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
package de.symeda.sormas.ui;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.ExternalResource;
import com.vaadin.server.FileDownloader;
import com.vaadin.server.FileResource;
import com.vaadin.server.Page;
import com.vaadin.server.StreamResource;
import com.vaadin.server.VaadinService;
import com.vaadin.server.VaadinServletRequest;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Link;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.ValoTheme;

import de.symeda.sormas.api.ConfigFacade;
import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.caze.classification.ClassificationHtmlRenderer;
import de.symeda.sormas.api.feature.FeatureType;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.utils.HtmlHelper;
import de.symeda.sormas.api.utils.InfoProvider;
import de.symeda.sormas.ui.login.LoginHelper;
import de.symeda.sormas.ui.user.UserSettingsForm;
import de.symeda.sormas.ui.utils.ButtonHelper;
import de.symeda.sormas.ui.utils.CommitDiscardWrapperComponent;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.DownloadUtil;
import de.symeda.sormas.ui.utils.VaadinUiUtil;

@SuppressWarnings("serial")
public class LogoutView extends VerticalLayout implements View {

	public static final String VIEW_NAME = "dashboard_logout";

	public LogoutView() {

		// Info section
		HorizontalLayout aboutLayout = new HorizontalLayout();
		{
			VerticalLayout infoLayout = new VerticalLayout();
			infoLayout.setMargin(new MarginInfo(true, false, false, false));
			infoLayout.addStyleName(CssStyles.H1);

			// display counter here to count down and run logout code on confirm and timer
			// end
			showConfirmPopup();
			// LoginHelper.logout();
			aboutLayout.addComponent(infoLayout);
		}

		setSizeFull();
		setStyleName("about-view");
		addComponent(aboutLayout);
		setComponentAlignment(aboutLayout, Alignment.MIDDLE_CENTER);
	}

	private void showConfirmPopup() {

		Window window = VaadinUiUtil.createPopupWindow();
		// window.setCaption("Log Out");
		window.setModal(true);

		VerticalLayout popupContent = new VerticalLayout();
		Label caption = new Label("Are you sure you want to logout?");
		popupContent.addComponent(caption);
		HorizontalLayout buttonContent = new HorizontalLayout();
		Button button1 = new Button("Confirm");
		button1.addStyleNames(ValoTheme.BUTTON_DANGER);
		button1.addClickListener(clickEvent -> {
			Page.getCurrent().getJavaScript()
					.execute("var url = window.location.toString();\r\n"
							+ "if (url.includes(\"dashboard_logout\")); {\r\n"
							+ "    window.location = url.replace(/dashboard_logout/, '');}\r\n" + "");
			LoginHelper.logout();
		});
		Button button2 = new Button("Cancel");
		button2.addClickListener(clickEvent -> {
			window.close();
			Page.getCurrent().getJavaScript().execute("history.back();"); //MainScreen
		});

		buttonContent.addComponent(button1);
		buttonContent.addComponent(button2);
		popupContent.addComponent(buttonContent);

		window.setContent(popupContent);
		UI.getCurrent().addWindow(window);
	}
}
