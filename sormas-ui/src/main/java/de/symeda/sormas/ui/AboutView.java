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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *******************************************************************************/
package de.symeda.sormas.ui;

import java.net.MalformedURLException;
import java.net.URL;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.ClassResource;
import com.vaadin.server.FileDownloader;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.StreamResource;
import com.vaadin.server.VaadinService;
import com.vaadin.server.VaadinServletRequest;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.CustomLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import de.symeda.sormas.api.caze.classification.ClassificationHtmlRenderer;
import de.symeda.sormas.api.utils.InfoProvider;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.DownloadUtil;

@SuppressWarnings("serial")
public class AboutView extends VerticalLayout implements View {

	public static final String VIEW_NAME = "about";

	public AboutView() {
		CustomLayout aboutContent = new CustomLayout("aboutview");
		aboutContent.setStyleName("about-content");

		// Info section
		aboutContent.addComponent(
				new Label(FontAwesome.INFO_CIRCLE.getHtml()
						+ " SORMAS version: "
						+ InfoProvider.get().getVersion(), ContentMode.HTML), "info");

		// Documents section
		VerticalLayout documentsLayout = new VerticalLayout();
		aboutContent.addComponent(documentsLayout, "documents");
		
		Button classificationDocumentButton = new Button("Case Classification Rules (HTML)");
		CssStyles.style(classificationDocumentButton, ValoTheme.BUTTON_LINK, CssStyles.BUTTON_COMPACT);
		documentsLayout.addComponent(classificationDocumentButton);

		try {
			String serverUrl = new URL(((VaadinServletRequest) VaadinService.getCurrentRequest()).getHttpServletRequest().getRequestURL().toString()).getAuthority();
			StreamResource classificationResource = DownloadUtil.createStringStreamResource(
					ClassificationHtmlRenderer.createHtmlForDownload(serverUrl), "classification_rules.html", "text/html");
			new FileDownloader(classificationResource).extend(classificationDocumentButton);
		} catch (MalformedURLException e) {

		}

		Button dataDictionaryButton = new Button("Data Dictionary (XLSX)");
		CssStyles.style(dataDictionaryButton, ValoTheme.BUTTON_LINK, CssStyles.BUTTON_COMPACT);
		documentsLayout.addComponent(dataDictionaryButton);
		FileDownloader dataDictionaryDownloader = new FileDownloader(new ClassResource("/doc/SORMAS_Data_Dictionary.xlsx"));
		dataDictionaryDownloader.extend(dataDictionaryButton);
		
		Button technicalManualButton = new Button("Technical Manual (PDF)");
		CssStyles.style(technicalManualButton, ValoTheme.BUTTON_LINK, CssStyles.BUTTON_COMPACT);
		documentsLayout.addComponent(technicalManualButton);
		technicalManualButton.addClickListener(e -> {
			getUI().getPage().open("https://github.com/hzi-braunschweig/SORMAS-Project/files/2585973/SORMAS_Technical_Manual_Webversion_20180911.pdf", "_self");
		});

		setSizeFull();
		setStyleName("about-view");
		addComponent(aboutContent);
		setComponentAlignment(aboutContent, Alignment.MIDDLE_CENTER);
	}

	@Override
	public void enter(ViewChangeEvent event) {
	}

}
