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

import com.vaadin.icons.VaadinIcons;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.ClassResource;
import com.vaadin.server.ExternalResource;
import com.vaadin.server.FileDownloader;
import com.vaadin.server.StreamResource;
import com.vaadin.server.VaadinService;
import com.vaadin.server.VaadinServletRequest;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.CustomLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Link;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.caze.classification.ClassificationHtmlRenderer;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.utils.InfoProvider;
import de.symeda.sormas.ui.utils.ButtonHelper;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.DownloadUtil;

@SuppressWarnings("serial")
public class AboutView extends VerticalLayout implements View {

	public static final String VIEW_NAME = "about";

	public AboutView() {
		CustomLayout aboutContent = new CustomLayout("aboutview");
		aboutContent.setStyleName("about-content");

		// Info section
		VerticalLayout infoLayout = new VerticalLayout();
		infoLayout.setSpacing(false);
		infoLayout.setMargin(false);
		aboutContent.addComponent(infoLayout, "info");
		
		Label versionLabel = new Label(VaadinIcons.INFO_CIRCLE.getHtml()
						+ " "
						+ I18nProperties.getCaption(Captions.aboutSormasVersion)
						+ ": "
						+ InfoProvider.get().getVersion(), ContentMode.HTML);
		infoLayout.addComponent(versionLabel);
		
		Link whatsNewLink = new Link(I18nProperties.getCaption(Captions.aboutWhatsNew), new ExternalResource("https://github.com/hzi-braunschweig/SORMAS-Project/releases/tag/releases%2Fversion-" + InfoProvider.get().getBaseVersion()));
		whatsNewLink.setTargetName("_blank");
		infoLayout.addComponent(whatsNewLink);

		Link sormasWebsiteLink = new Link(I18nProperties.getCaption(Captions.aboutSormasWebsite), new ExternalResource("https://sormasorg.helmholtz-hzi.de/"));
		sormasWebsiteLink.setTargetName("_blank");
		infoLayout.addComponent(sormasWebsiteLink);

		Link sormasGithubLink = new Link("SORMAS Github", new ExternalResource("https://github.com/hzi-braunschweig/SORMAS-Project"));
		sormasGithubLink.setTargetName("_blank");
		infoLayout.addComponent(sormasGithubLink);

		Link changelogLink = new Link(I18nProperties.getCaption(Captions.aboutChangelog), new ExternalResource("https://github.com/hzi-braunschweig/SORMAS-Project/releases"));
		changelogLink.setTargetName("_blank");
		infoLayout.addComponent(changelogLink);
			
		// Documents section
		VerticalLayout documentsLayout = new VerticalLayout();
		documentsLayout.setSpacing(false);
		documentsLayout.setMargin(false);
		aboutContent.addComponent(documentsLayout, "documents");
		
		Button classificationDocumentButton = ButtonHelper.createButton(Captions.aboutCaseClassificationRules, null, ValoTheme.BUTTON_LINK, CssStyles.BUTTON_COMPACT);
		documentsLayout.addComponent(classificationDocumentButton);

		try {
			String serverUrl = new URL(((VaadinServletRequest) VaadinService.getCurrentRequest()).getHttpServletRequest().getRequestURL().toString()).getAuthority();
			StreamResource classificationResource = DownloadUtil.createStringStreamResource(
					ClassificationHtmlRenderer.createHtmlForDownload(serverUrl, FacadeProvider.getDiseaseConfigurationFacade().getAllDiseases(true, true, true)), "classification_rules.html", "text/html");
			new FileDownloader(classificationResource).extend(classificationDocumentButton);
		} catch (MalformedURLException e) {

		}

		Button dataDictionaryButton = ButtonHelper.createButton(Captions.aboutDataDictionary, null, ValoTheme.BUTTON_LINK, CssStyles.BUTTON_COMPACT);
		documentsLayout.addComponent(dataDictionaryButton);
		FileDownloader dataDictionaryDownloader = new FileDownloader(new ClassResource("/doc/SORMAS_Data_Dictionary.xlsx"));
		dataDictionaryDownloader.extend(dataDictionaryButton);

		Link technicalManualLink = new Link(I18nProperties.getCaption(Captions.aboutTechnicalManual), new ExternalResource("https://github.com/hzi-braunschweig/SORMAS-Project/files/2585973/SORMAS_Technical_Manual_Webversion_20180911.pdf"));
		technicalManualLink.setTargetName("_blank");
		documentsLayout.addComponent(technicalManualLink);

		setSizeFull();
		setStyleName("about-view");
		addComponent(aboutContent);
		setComponentAlignment(aboutContent, Alignment.MIDDLE_CENTER);
	}

	@Override
	public void enter(ViewChangeEvent event) {
	}

}
