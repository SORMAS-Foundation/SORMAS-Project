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

import org.apache.commons.io.IOUtils;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.ExternalResource;
import com.vaadin.server.FileDownloader;
import com.vaadin.server.FileResource;
import com.vaadin.server.StreamResource;
import com.vaadin.server.VaadinService;
import com.vaadin.server.VaadinServletRequest;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.AbstractComponent;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Link;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import de.symeda.sormas.api.ConfigFacade;
import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.caze.classification.ClassificationHtmlRenderer;
import de.symeda.sormas.api.feature.FeatureType;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.api.utils.HtmlHelper;
import de.symeda.sormas.api.utils.InfoProvider;
import de.symeda.sormas.ui.utils.ButtonHelper;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.DownloadUtil;
import de.symeda.sormas.ui.utils.ExportEntityName;

@SuppressWarnings("serial")
public class AboutView extends VerticalLayout implements View {

	public static final String VIEW_NAME = "about";

	public AboutView() {

		// Info section
		HorizontalLayout aboutLayout = new HorizontalLayout();
		{
			VerticalLayout infoLayout = new VerticalLayout();
			infoLayout.setMargin(new MarginInfo(true, false, false, false));
			infoLayout.addComponent(createInfoSection());

			// Documents section
			if (shouldShowDocumentsSection()) {
				infoLayout.addComponent(createDocumentsSection());
			}

			aboutLayout.addComponent(infoLayout);
		}

		// Additional Info section
		VerticalLayout additionalInfoSection =
			createCustomHtmlSection(I18nProperties.getCaption(Captions.aboutAdditionalInfo), "additionalinfo.html");
		if (additionalInfoSection != null) {
			aboutLayout.addComponent(additionalInfoSection);
		}

		// Copyright section
		VerticalLayout copyrightSection = createCustomHtmlSection(I18nProperties.getCaption(Captions.aboutCopyright), "copyrightnotices.html");
		if (copyrightSection != null) {
			aboutLayout.addComponent(copyrightSection);
		}

		setSizeFull();
		setStyleName("about-view");
		addComponent(aboutLayout);
		setComponentAlignment(aboutLayout, Alignment.MIDDLE_CENTER);
	}

	@Override
	public void enter(ViewChangeEvent event) {

	}

	private VerticalLayout createInfoSection() {

		VerticalLayout infoLayout = new VerticalLayout();
		infoLayout.setSpacing(false);
		infoLayout.setMargin(false);

		Label aboutLabel = new Label(I18nProperties.getCaption(Captions.about), ContentMode.HTML);
		aboutLabel.addStyleName(CssStyles.H1);
		infoLayout.addComponent(aboutLabel);

		ConfigFacade configFacade = FacadeProvider.getConfigFacade();
		String infoLabelStr = configFacade.isCustomBranding()
			? String.format(I18nProperties.getCaption(Captions.aboutBrandedSormasVersion), configFacade.getCustomBrandingName())
			: "SORMAS";
		Label infoLabel = new Label(VaadinIcons.INFO_CIRCLE.getHtml() + " " + infoLabelStr, ContentMode.HTML);
		infoLayout.addComponent(infoLabel);

		Label versionLabel = new Label(I18nProperties.getCaption(Captions.aboutVersion) + ": " + InfoProvider.get().getVersion(), ContentMode.HTML);
		CssStyles.style(versionLabel, CssStyles.VSPACE_3);
		infoLayout.addComponent(versionLabel);

		if (InfoProvider.get().isSnapshotVersion()) {
			Link commitLink = new Link(
				String.format(
					"%s (%s)",
					versionLabel.getValue(),
					InfoProvider.get()
						.getLastCommitShortId()),
				new ExternalResource(InfoProvider.get().getLastCommitHistoryUrl()));
			commitLink.setTargetName("_blank");
			CssStyles.style(commitLink, CssStyles.VSPACE_3);
			infoLayout.replaceComponent(versionLabel, commitLink);
		}

		if (FacadeProvider.getFeatureConfigurationFacade().isFeatureEnabled(FeatureType.LAB_MESSAGES)) {
			addExternalServiceVersion(
				Captions.aboutLabMessageAdapter,
				() -> FacadeProvider.getLabMessageFacade().getLabMessagesAdapterVersion(),
				infoLayout);
		}

		if (FacadeProvider.getExternalSurveillanceToolFacade().isFeatureEnabled()) {
//			addExternalServiceVersion(
//				Captions.aboutExternalSurveillanceToolGateway,
//				() -> FacadeProvider.getExternalSurveillanceToolFacade().getVersion(),
//				infoLayout);
		}

		Link whatsNewLink = new Link(
			I18nProperties.getCaption(Captions.aboutWhatsNew),
			new ExternalResource("https://github.com/hzi-braunschweig/SORMAS-Project/releases/tag/v" + InfoProvider.get().getBaseVersion()));
		whatsNewLink.setTargetName("_blank");
		infoLayout.addComponent(whatsNewLink);

		Link sormasWebsiteLink =
			new Link(I18nProperties.getCaption(Captions.aboutSormasWebsite), new ExternalResource("https://sormasorg.helmholtz-hzi.de/"));
		sormasWebsiteLink.setTargetName("_blank");
		infoLayout.addComponent(sormasWebsiteLink);

		Link sormasGithubLink = new Link("SORMAS Github", new ExternalResource("https://github.com/hzi-braunschweig/SORMAS-Project"));
		sormasGithubLink.setTargetName("_blank");
		infoLayout.addComponent(sormasGithubLink);

		Link changelogLink = new Link(
			I18nProperties.getCaption(Captions.aboutChangelog),
			new ExternalResource("https://github.com/hzi-braunschweig/SORMAS-Project/releases"));
		changelogLink.setTargetName("_blank");
		infoLayout.addComponent(changelogLink);

		return infoLayout;
	}

	interface ExternalServiceVersionSupplier {

		String get() throws Exception;
	}

	private void addExternalServiceVersion(String captionTag, ExternalServiceVersionSupplier versionSupplier, VerticalLayout infoLayout) {
		String version;
		Label availabilityLabel;

		try {
			version = versionSupplier.get();
			availabilityLabel = new Label(VaadinIcons.CHECK.getHtml(), ContentMode.HTML);
			availabilityLabel.addStyleName(CssStyles.LABEL_POSITIVE);
		} catch (Exception e) {
			version = I18nProperties.getCaption(Captions.aboutServiceNotAvailable);
			availabilityLabel = new Label(VaadinIcons.CLOSE.getHtml(), ContentMode.HTML);
			availabilityLabel.addStyleName(CssStyles.LABEL_CRITICAL);
		}

		availabilityLabel.addStyleName(CssStyles.HSPACE_LEFT_4);

		Label caption = new Label(I18nProperties.getCaption(captionTag));
		HorizontalLayout captionLayout = new HorizontalLayout(caption, availabilityLabel);
		captionLayout.setSpacing(false);
		infoLayout.addComponent(captionLayout);

		Label versionLabel = new Label(I18nProperties.getCaption(Captions.aboutVersion) + ": " + version, ContentMode.HTML);
		CssStyles.style(versionLabel, CssStyles.VSPACE_3);
		infoLayout.addComponent(versionLabel);
	}

	private VerticalLayout createDocumentsSection() {

		VerticalLayout documentsLayout = new VerticalLayout();
		documentsLayout.setSpacing(false);
		documentsLayout.setMargin(new MarginInfo(true, false, false, false));

		Label documentsLabel = new Label(I18nProperties.getCaption(Captions.aboutDocuments), ContentMode.HTML);
		documentsLabel.addStyleName(CssStyles.H1);
		documentsLayout.addComponent(documentsLabel);

		List<String> customDocuments = listCustomDocumentsFiles();
		if (!customDocuments.isEmpty()) {
			customDocuments.stream().forEach(customDocument -> {
				Button customDocumentButton = ButtonHelper.createButton(customDocument, null, ValoTheme.BUTTON_LINK, CssStyles.BUTTON_COMPACT);
				documentsLayout.addComponent(customDocumentButton);
				String customDocumentPath = getCustomDocumentsPath() + File.separator + customDocument;
				FileDownloader customDocumentDownloader = new FileDownloader(new FileResource(new File(customDocumentPath)));
				customDocumentDownloader.extend(customDocumentButton);
			});
		}

		if (shouldShowClassificationDocumentLink()) {
			Button classificationDocumentButton =
				ButtonHelper.createButton(Captions.aboutCaseClassificationRules, null, ValoTheme.BUTTON_LINK, CssStyles.BUTTON_COMPACT);
			documentsLayout.addComponent(classificationDocumentButton);

			try {
				String serverUrl =
					new URL(((VaadinServletRequest) VaadinService.getCurrentRequest()).getHttpServletRequest().getRequestURL().toString())
						.getAuthority();
				StreamResource classificationResource = DownloadUtil.createStringStreamResource(
					ClassificationHtmlRenderer.createHtmlForDownload(
						serverUrl,
						FacadeProvider.getDiseaseConfigurationFacade().getAllDiseases(true, true, true),
						I18nProperties.getUserLanguage(),
						FacadeProvider.getCaseClassificationFacade(),
						FacadeProvider.getConfigFacade()),
					"classification_rules.html",
					"text/html");
				new FileDownloader(classificationResource).extend(classificationDocumentButton);
			} catch (MalformedURLException e) {

			}
		}

		if (shouldShowDataDictionaryLink()) {
			Button dataDictionaryButton =
				ButtonHelper.createButton(Captions.aboutDataDictionary, null, ValoTheme.BUTTON_LINK, CssStyles.BUTTON_COMPACT);
			documentsLayout.addComponent(dataDictionaryButton);
			DownloadUtil.attachDataDictionaryDownloader(dataDictionaryButton);
		}

		if (FacadeProvider.getInfoFacade().isGenerateDataProtectionDictionaryAllowed()) {
			Button dataProtectionButton =
				ButtonHelper.createButton(Captions.aboutDataProtectionDictionary, null, ValoTheme.BUTTON_LINK, CssStyles.BUTTON_COMPACT);
			documentsLayout.addComponent(dataProtectionButton);
			attachDataProtectionDictionaryDownloader(dataProtectionButton);
		}

		// This link is hidden until an updated version of the document is provided
		/*
		 * Link technicalManualLink = new Link(
		 * I18nProperties.getCaption(Captions.aboutTechnicalManual),
		 * new ExternalResource(
		 * "https://github.com/hzi-braunschweig/SORMAS-Project/files/2585973/SORMAS_Technical_Manual_Webversion_20180911.pdf"));
		 * technicalManualLink.setTargetName("_blank");
		 * documentsLayout.addComponent(technicalManualLink);
		 */

		return documentsLayout;
	}

	private VerticalLayout createCustomHtmlSection(String caption, String fileName) {

		String htmlContentString = "";

		Path customHtmlDirectory = Paths.get(FacadeProvider.getConfigFacade().getCustomFilesPath());
		Path customFilePath = customHtmlDirectory.resolve(fileName);

		try {
			byte[] encoded = Files.readAllBytes(customFilePath);
			htmlContentString = HtmlHelper.cleanHtmlRelaxed(new String(encoded, StandardCharsets.UTF_8));
			if (htmlContentString.isEmpty()) {
				return null;
			}
		} catch (IOException e) {
			return null;
		}

		VerticalLayout layout = new VerticalLayout();
		layout.setSpacing(false);
		layout.setMargin(new MarginInfo(true, false, false, false));

		// Header
		Label headerLabel = new Label(caption, ContentMode.HTML);
		headerLabel.addStyleName(CssStyles.H1);
		layout.addComponent(headerLabel);

		// Label, loaded from custom file
		Label htmlContent = new Label(htmlContentString);
		htmlContent.setContentMode(ContentMode.HTML);
		layout.addComponent(htmlContent);

		return layout;
	}

	private boolean shouldShowDocumentsSection() {
		return !listCustomDocumentsFiles().isEmpty() || shouldShowClassificationDocumentLink() || shouldShowDataDictionaryLink();
	}

	private List<String> listCustomDocumentsFiles() {
		File customDocumentsDir = new File(getCustomDocumentsPath());
		if (!customDocumentsDir.exists() || !customDocumentsDir.isDirectory()) {
			return Collections.emptyList();
		}
		File[] customDocuments = customDocumentsDir.listFiles();
		if (customDocuments == null) {
			return Collections.emptyList();
		}
		return Arrays.stream(customDocuments).map(File::getName).sorted(String::compareTo).collect(Collectors.toList());
	}

	private boolean shouldShowClassificationDocumentLink() {
		return FacadeProvider.getConfigFacade().isFeatureAutomaticCaseClassification()
			&& FacadeProvider.getFeatureConfigurationFacade().isFeatureEnabled(FeatureType.CASE_SURVEILANCE);
	}

	private boolean shouldShowDataDictionaryLink() {
		return FacadeProvider.getFeatureConfigurationFacade().isFeatureEnabled(FeatureType.CASE_SURVEILANCE)
			|| FacadeProvider.getFeatureConfigurationFacade().isFeatureEnabled(FeatureType.EVENT_SURVEILLANCE);
	}

	private String getCustomDocumentsPath() {
		return FacadeProvider.getConfigFacade().getCustomFilesPath() + "aboutfiles";
	}

	public void attachDataProtectionDictionaryDownloader(AbstractComponent target) {
		new FileDownloader(new StreamResource(() -> new DownloadUtil.DelayedInputStream((out) -> {
			String documentPath = FacadeProvider.getInfoFacade().generateDataProtectionDictionary();
			IOUtils.copy(Files.newInputStream(new File(documentPath).toPath()), out);
		}, (e) -> {
		}), DownloadUtil.createFileNameWithCurrentDate(ExportEntityName.DATA_PROTECTION_DICTIONARY, ".xlsx"))).extend(target);
	}
}
