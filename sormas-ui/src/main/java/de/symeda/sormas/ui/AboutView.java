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
import com.vaadin.server.StreamResource;
import com.vaadin.server.ThemeResource;
import com.vaadin.server.VaadinService;
import com.vaadin.server.VaadinServletRequest;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Embedded;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Image;
import com.vaadin.ui.Label;
import com.vaadin.ui.Link;
import com.vaadin.ui.PopupView;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Video;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.ValoTheme;
import com.vaadin.v7.ui.Grid;
import com.vaadin.flow.component.dialog.Dialog;

import de.symeda.sormas.api.ConfigFacade;
import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.caze.classification.ClassificationHtmlRenderer;
import de.symeda.sormas.api.feature.FeatureType;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.utils.HtmlHelper;
import de.symeda.sormas.api.utils.InfoProvider;
import de.symeda.sormas.ui.utils.ButtonHelper;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.DownloadUtil;

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
			infoLayout.addStyleName(CssStyles.H1);

			// Documents section
			if (shouldShowDocumentsSection()) {
				// infoLayout.addComponent(createDocumentsSection());
			}

			aboutLayout.addComponent(infoLayout);
		}

		// Additional Info section
		VerticalLayout additionalInfoSection = createCustomHtmlSection(
				I18nProperties.getCaption(Captions.aboutAdditionalInfo), "additionalinfo.html");
		if (additionalInfoSection != null) {
			aboutLayout.addComponent(additionalInfoSection);
		}

		// Copyright section
		VerticalLayout copyrightSection = createCustomHtmlSection(I18nProperties.getCaption(Captions.aboutCopyright),
				"copyrightnotices.html");
		if (copyrightSection != null) {
		//	aboutLayout.addComponent(copyrightSection);
		}

		setSizeFull();
		setStyleName("about-view");
		addComponent(aboutLayout);
		setComponentAlignment(aboutLayout, Alignment.MIDDLE_LEFT);
	}

	@Override
	public void enter(ViewChangeEvent event) {

	}

	private VerticalLayout createInfoSection() {

		AboutViewVideoWindow newwind = null;

		VerticalLayout infoLayout = new VerticalLayout();
		infoLayout.setSpacing(false);
		infoLayout.setMargin(false);

		// ADD HEADER IMAGE TO THE ABOUT PAGE
		// Image image = new Image(null, new ThemeResource("img/About_us.jpg"));
		// infoLayout.addComponent(image); To

		Label aboutLabel = new Label(I18nProperties.getCaption(Captions.about), ContentMode.HTML);
		aboutLabel.addStyleName(CssStyles.H2);
		// setComponentAlignment(aboutLabel, Alignment.MIDDLE_CENTER);
		infoLayout.addComponent(aboutLabel);

		ConfigFacade configFacade = FacadeProvider.getConfigFacade();
		String infoLabelStr = configFacade.isCustomBranding()
				? String.format(I18nProperties.getCaption(Captions.aboutBrandedSormasVersion),
						configFacade.getCustomBrandingName())
				: "";
		Label infoLabel = new Label(
				I18nProperties.getCaption(Captions.aboutApmisVersion) + ": " + InfoProvider.InfoProvider_apmis(),
				ContentMode.HTML);
		Label aboutText = new Label(I18nProperties.getCaption(Captions.aboutText), ContentMode.TEXT);
		Label guideLabel = new Label(I18nProperties.getCaption(Captions.aboutGuides), ContentMode.HTML);
		guideLabel.addStyleName(CssStyles.H2);

		infoLayout.addComponent(infoLabel);
		infoLayout.addComponent(aboutText);

		infoLayout.addComponent(guideLabel);

		VerticalLayout apmisguideLayout = new VerticalLayout();

		ThemeResource resource = new ThemeResource("img/APMIS_User_Guide.pdf");
		Link link = new Link("User Guide", resource);
		apmisguideLayout.addComponent(link);

		VerticalLayout techguideLayout = new VerticalLayout();
		ThemeResource techguideresource = new ThemeResource("img/APMIS_Technical_Manual.pdf");
		Link apmisTechGuidelink = new Link("Technical Guide", techguideresource);
		apmisTechGuidelink.setTargetName("_blank");
		techguideLayout.addComponent(apmisTechGuidelink);

		/*
		 * VerticalLayout hziguideLayout = new VerticalLayout(); ThemeResource
		 * hziresource1 = new ThemeResource("img/4_Configuration.pdf"); Link hzilink1 =
		 * new Link("Configuration", hziresource1); link.setTargetName("_blank");
		 * hziguideLayout.addComponent(hzilink1);
		 * 
		 * 
		 * ThemeResource hziresource2 = new ThemeResource("img/5_User management.pdf");
		 * Link hzilink2 = new Link("User Management", hziresource2);
		 * hzilink2.setTargetName("_blank"); hziguideLayout.addComponent(hzilink2);
		 * 
		 * ThemeResource hziresource3 = new
		 * ThemeResource("img/3_l_Create_Campaign_Data_Form.pdf"); Link hzilink3 = new
		 * Link("Campaign Data Form", hziresource3); hzilink3.setTargetName("_blank");
		 * hziguideLayout.addComponent(hzilink3);
		 * 
		 * ThemeResource hziresource4 = new
		 * ThemeResource("img/3_k_Edit_CampaignFormData_new.pdf"); Link hzilink4 = new
		 * Link("Edit_CampaignFormData_new", hziresource4);
		 * hzilink4.setTargetName("_blank"); hziguideLayout.addComponent(hzilink4);
		 * 
		 * ThemeResource hziresource5 = new
		 * ThemeResource("img/3_j_Edit_ExistingCampaign_Configuration_new.pdf"); Link
		 * hzilink5 = new Link("Edit Existing Campaign Configuration", hziresource5);
		 * hzilink5.setTargetName("_blank"); hziguideLayout.addComponent(hzilink5);
		 * 
		 * ThemeResource hziresource6 = new
		 * ThemeResource("img/3_j_Edit_ExistingCampaign.pdf"); Link hzilink6 = new
		 * Link("Edit Existing Campaign", hziresource6);
		 * hzilink6.setTargetName("_blank"); hziguideLayout.addComponent(hzilink6);
		 * 
		 * ThemeResource hziresource7 = new
		 * ThemeResource("img/3_h_Create_New_Campaign.pdf"); Link hzilink7 = new
		 * Link("Create New Campaign", hziresource7); hzilink7.setTargetName("_blank");
		 * hziguideLayout.addComponent(hzilink7);
		 * 
		 * ThemeResource hziresource8 = new
		 * ThemeResource("img/3_g_Export_SpecificCampaign_Data.pdf"); Link hzilink8 =
		 * new Link("Export Specific Campaign Data", hziresource8);
		 * hzilink8.setTargetName("_blank"); hziguideLayout.addComponent(hzilink8);
		 * 
		 * ThemeResource hziresource9 = new
		 * ThemeResource("img/3_f_Export_AllCampaign_Data.pdf"); Link hzilink9 = new
		 * Link("Export All Campaign Data", hziresource9);
		 * hzilink9.setTargetName("_blank"); hziguideLayout.addComponent(hzilink9);
		 * 
		 * ThemeResource hziresource10 = new
		 * ThemeResource("img/3_e_Validate_Campaigns.pdf"); Link hzilink10 = new
		 * Link("Validate Campaigns", hziresource10); hzilink10.setTargetName("_blank");
		 * hziguideLayout.addComponent(hzilink10);
		 * 
		 * ThemeResource hziresource11 = new
		 * ThemeResource("img/3_d_Edit_Campaign_data.pdf"); Link hzilink11 = new
		 * Link("Edit Campaign Data", hziresource11); hzilink11.setTargetName("_blank");
		 * hziguideLayout.addComponent(hzilink11);
		 * 
		 * ThemeResource hziresource12 = new
		 * ThemeResource("img/3_c_ViewCampaignData.pdf"); Link hzilink12 = new
		 * Link("View Campaign Data", hziresource12); hzilink12.setTargetName("_blank");
		 * hziguideLayout.addComponent(hzilink12);
		 * 
		 * ThemeResource hziresource13 = new
		 * ThemeResource("img/3_b_SearchCampaigns.pdf"); Link hzilink13 = new
		 * Link("Search Campaigns", hziresource13); hzilink13.setTargetName("_blank");
		 * hziguideLayout.addComponent(hzilink13);
		 * 
		 * ThemeResource hziresource14 = new ThemeResource("img/3_a_ViewCampaigns.pdf");
		 * Link hzilink14 = new Link("View Campaigns", hziresource14);
		 * hzilink14.setTargetName("_blank"); hziguideLayout.addComponent(hzilink14);
		 * 
		 * ThemeResource hziresource15 = new ThemeResource("img/2_Dashboard.pdf"); Link
		 * hzilink15 = new Link("Dashboard", hziresource15);
		 * hzilink15.setTargetName("_blank"); hziguideLayout.addComponent(hzilink15);
		 * 
		 * ThemeResource hziresource16 = new
		 * ThemeResource("img/1_b_SettingsPersonalisation.pdf"); Link hzilink16 = new
		 * Link("Settings Personalisation", hziresource16);
		 * hzilink16.setTargetName("_blank"); hziguideLayout.addComponent(hzilink16);
		 * 
		 * ThemeResource hziresource17 = new ThemeResource("img/1_a_Login_logout.pdf");
		 * Link hzilink17 = new Link("Login and logout", hziresource17);
		 * hzilink17.setTargetName("_blank"); hziguideLayout.addComponent(hzilink17);
		 * 
		 * ThemeResource hziresource18 = new
		 * ThemeResource("img/0_OnePageInstructions_APMIS.pdf"); Link hzilink18 = new
		 * Link("One Page Instructions APMIS", hziresource18);
		 * hzilink18.setTargetName("_blank"); hziguideLayout.addComponent(hzilink18);
		 */

		HorizontalLayout abou_vid_tLayout = new HorizontalLayout();
		
		VerticalLayout videoguideLayout = new VerticalLayout();
		VerticalLayout videoguideLayout_ = new VerticalLayout();
		VerticalLayout videoguideLayout_a = new VerticalLayout();

		final Button videolink1 = new Button("User Management");
		videolink1.addClickListener(e -> {
			AboutViewVideoWindow as = new AboutViewVideoWindow("User Management",
					"img/3_a_ViewCampaigns_21032021_subtitles.mp4");
			UI.getCurrent().addWindow(as);
		});
		videoguideLayout.addComponent(videolink1);

		final Button videolink2 = new Button("Configuration");
		videolink2.addClickListener(e -> {
			AboutViewVideoWindow as = new AboutViewVideoWindow("Configuration",
					"img/4_Configuration_10052021_subtitles.mp4");
			UI.getCurrent().addWindow(as);
		});
		videoguideLayout.addComponent(videolink2);

		final Button videolink3 = new Button("Create Campaign Data Form");
		videolink3.addClickListener(e -> {
			AboutViewVideoWindow as = new AboutViewVideoWindow("Create Campaign Data Form",
					"img/3_l_CreateCampaignDataForm_21032021_subtitles.mp4");
			UI.getCurrent().addWindow(as);
		});
		videoguideLayout.addComponent(videolink3);

		final Button videolink4 = new Button("Edit Campaign Form Data");
		videolink4.addClickListener(e -> {
			AboutViewVideoWindow as = new AboutViewVideoWindow("Edit Campaign Form Data",
					"img/3_k_EditCampaignFormData_21032021_subtitles.mp4");
			UI.getCurrent().addWindow(as);
		});
		videoguideLayout.addComponent(videolink4);

		final Button videolink5 = new Button("Edit Existing Campaign Configuration");
		videolink5.addClickListener(e -> {
			AboutViewVideoWindow as = new AboutViewVideoWindow("Edit Existing Campaign Configuration",
					"img/3_j_EditExistingCampaignConfiguration_21032021_subtitles.mp4");
			UI.getCurrent().addWindow(as);
		});
		videoguideLayout.addComponent(videolink5);

		final Button videolink6 = new Button("Edit Existing Campaign");
		videolink6.addClickListener(e -> {
			AboutViewVideoWindow as = new AboutViewVideoWindow("Dashboard",
					"img/3_i_EditExistingCampaign_21032021_subtitles.mp4");
			UI.getCurrent().addWindow(as);
		});
		videoguideLayout.addComponent(videolink6);

		final Button opendx = new Button("Create New Campaign");
		opendx.addClickListener(e -> {
			AboutViewVideoWindow as = new AboutViewVideoWindow("Create New Campaign",
					"img/3_h_CreateNewCampaign_21032021_subtitles.mp4");
			UI.getCurrent().addWindow(as);
		});
		videoguideLayout_.addComponent(opendx);

		final Button videolink8 = new Button("Export Specific Campaign Data");
		videolink8.addClickListener(e -> {
			AboutViewVideoWindow as = new AboutViewVideoWindow("Export Specific Campaign Data",
					"img/3_g_Export_SpecificCampaign_Data.mp4");
			UI.getCurrent().addWindow(as);
		});
		videoguideLayout_.addComponent(videolink8);

		final Button videolink9 = new Button("Export All Campaign Data");
		videolink9.addClickListener(e -> {
			AboutViewVideoWindow as = new AboutViewVideoWindow("Export All Campaign Data",
					"img/3_f_ExportAllCampaignData_21032021_subtitles.mp4");
			UI.getCurrent().addWindow(as);
		});
		videoguideLayout_.addComponent(videolink9);

		final Button videolink10 = new Button("Validate Campaigns");
		videolink10.addClickListener(e -> {
			AboutViewVideoWindow as = new AboutViewVideoWindow("Validate Campaigns",
					"img/3_e_ValidateCampaigns_21032021_subtitles.mp4");
			UI.getCurrent().addWindow(as);
		});
		videoguideLayout_.addComponent(videolink10);

		final Button videolink11 = new Button("Edit Campaigns Data");
		videolink11.addClickListener(e -> {
			AboutViewVideoWindow as = new AboutViewVideoWindow("Edit Campaigns Data",
					"img/3_d_EditCampaignsData_21032021_subtitles.mp4");
			UI.getCurrent().addWindow(as);
		});
		videoguideLayout_.addComponent(videolink11);

		final Button opendxz = new Button("View Campaign Data");
		opendxz.addClickListener(e -> {
			AboutViewVideoWindow as = new AboutViewVideoWindow("View Campaign Data",
					"img/3_c_ViewCampaignData2_21032021_subtitles.mp4");
			UI.getCurrent().addWindow(as);
		});
		videoguideLayout_.addComponent(opendxz);
		

		final Button opends = new Button("Search Campaigns");
		opends.addClickListener(e -> {
			AboutViewVideoWindow as = new AboutViewVideoWindow("Search Campaigns",
					"img/3_b_SearchCampaigns_21032021_subtitles.mp4");
			UI.getCurrent().addWindow(as);
		});
		videoguideLayout_a.addComponent(opends);


		final Button opendz = new Button("View Campaigns");
		opendz.addClickListener(e -> {
			AboutViewVideoWindow as = new AboutViewVideoWindow("View Campaigns",
					"img/3_a_ViewCampaigns_21032021_subtitles.mp4");
			UI.getCurrent().addWindow(as);
		});
		videoguideLayout_a.addComponent(opendz);


		final Button opendxc = new Button("Dashboard");
		opendxc.addClickListener(e -> {
			AboutViewVideoWindow as = new AboutViewVideoWindow("Dashboard",
					"img/3_a_ViewCampaigns_21032021_subtitles.mp4");
			UI.getCurrent().addWindow(as);
		});
		videoguideLayout.addComponent(opendxc);

		final Button openx = new Button("Settings Personalisation");
		openx.addClickListener(e -> {
			AboutViewVideoWindow as = new AboutViewVideoWindow("Settings Personalisation",
					"img/1_b_SettingsPersonalisation_subtitles.mp4");
			UI.getCurrent().addWindow(as);
		});
		videoguideLayout_a.addComponent(openx);

		final Button open = new Button("Login and Logout");
		open.addClickListener(e -> {
			AboutViewVideoWindow as = new AboutViewVideoWindow("Login and Logout", "img/1_a_LoginLogout_subtitles.mp4");
			UI.getCurrent().addWindow(as);
		});

		videoguideLayout_a.addComponent(open);
		
		
		abou_vid_tLayout.addComponent(videoguideLayout);
		abou_vid_tLayout.addComponent(videoguideLayout_);
		abou_vid_tLayout.addComponent(videoguideLayout_a);
		
		

		TabSheet tabs = new TabSheet();
		tabs.addTab(apmisguideLayout, I18nProperties.getCaption(Captions.apmisaboutguides));
		// tabs.addTab(hziguideLayout,
		// I18nProperties.getCaption(Captions.apmishziguides));
		tabs.addTab(techguideLayout, I18nProperties.getCaption(Captions.abouttechguides));
		tabs.addTab(abou_vid_tLayout, I18nProperties.getCaption(Captions.aboutvideos));

		infoLayout.addComponent(tabs);

		// Label versionLabel =
		// new Label(I18nProperties.getCaption(Captions.aboutSormasVersion) + ": " +
		// InfoProvider.get().getVersion(), ContentMode.HTML);
		// CssStyles.style(versionLabel, CssStyles.VSPACE_3);
		// infoLayout.addComponent(versionLabel);

		// Label versionApmisLabel =
		// new Label(I18nProperties.getCaption(Captions.aboutApmisVersion) + ": " +
		// InfoProvider.InfoProvider_apmis(), ContentMode.HTML);
		// CssStyles.style(versionApmisLabel, CssStyles.VSPACE_3);
		// infoLayout.addComponent(versionApmisLabel);
		/*
		 * Link whatsNewLink = new Link(
		 * I18nProperties.getCaption(Captions.aboutWhatsNew), new ExternalResource(
		 * "https://github.com/hzi-braunschweig/SORMAS-Project/releases/tag/v" +
		 * InfoProvider.get().getBaseVersion())); whatsNewLink.setTargetName("_blank");
		 * infoLayout.addComponent(whatsNewLink);
		 * 
		 * Link sormasWebsiteLink = new
		 * Link(I18nProperties.getCaption(Captions.aboutSormasWebsite), new
		 * ExternalResource("https://sormasorg.helmholtz-hzi.de/"));
		 * sormasWebsiteLink.setTargetName("_blank");
		 * infoLayout.addComponent(sormasWebsiteLink);
		 * 
		 * Link sormasGithubLink = new Link("SORMAS Github", new
		 * ExternalResource("https://github.com/hzi-braunschweig/SORMAS-Project"));
		 * sormasGithubLink.setTargetName("_blank");
		 * infoLayout.addComponent(sormasGithubLink);
		 * 
		 * Link changelogLink = new Link(
		 * I18nProperties.getCaption(Captions.aboutChangelog), new ExternalResource(
		 * "https://github.com/hzi-braunschweig/SORMAS-Project/releases"));
		 * changelogLink.setTargetName("_blank");
		 * infoLayout.addComponent(changelogLink);
		 */
		return infoLayout;
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
				Button customDocumentButton = ButtonHelper.createButton(customDocument, null, ValoTheme.BUTTON_LINK,
						CssStyles.BUTTON_COMPACT);
				documentsLayout.addComponent(customDocumentButton);
				String customDocumentPath = getCustomDocumentsPath() + File.separator + customDocument;
				FileDownloader customDocumentDownloader = new FileDownloader(
						new FileResource(new File(customDocumentPath)));
				customDocumentDownloader.extend(customDocumentButton);
			});
		}

		if (shouldShowClassificationDocumentLink()) {
			Button classificationDocumentButton = ButtonHelper.createButton(Captions.aboutCaseClassificationRules, null,
					ValoTheme.BUTTON_LINK, CssStyles.BUTTON_COMPACT);
			documentsLayout.addComponent(classificationDocumentButton);

			try {
				String serverUrl = new URL(((VaadinServletRequest) VaadinService.getCurrentRequest())
						.getHttpServletRequest().getRequestURL().toString()).getAuthority();
				StreamResource classificationResource = DownloadUtil
						.createStringStreamResource(ClassificationHtmlRenderer.createHtmlForDownload(serverUrl,
								FacadeProvider.getDiseaseConfigurationFacade().getAllDiseases(true, true, true),
								I18nProperties.getUserLanguage()), "classification_rules.html", "text/html");
				new FileDownloader(classificationResource).extend(classificationDocumentButton);
			} catch (MalformedURLException e) {

			}
		}

		if (shouldShowDataDictionaryLink()) {
			Button dataDictionaryButton = ButtonHelper.createButton(Captions.aboutDataDictionary, null,
					ValoTheme.BUTTON_LINK, CssStyles.BUTTON_COMPACT);
			documentsLayout.addComponent(dataDictionaryButton);
			DownloadUtil.attachDataDictionaryDownloader(dataDictionaryButton);
		}

		// This link is hidden until an updated version of the document is provided
		/*
		 * Link technicalManualLink = new Link(
		 * I18nProperties.getCaption(Captions.aboutTechnicalManual), new
		 * ExternalResource(
		 * "https://github.com/hzi-braunschweig/SORMAS-Project/files/2585973/SORMAS_Technical_Manual_Webversion_20180911.pdf"
		 * )); technicalManualLink.setTargetName("_blank");
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
		return !listCustomDocumentsFiles().isEmpty() || shouldShowClassificationDocumentLink()
				|| shouldShowDataDictionaryLink();
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

	/*
	 * public void DialogHeader() { Dialog dialog = new Dialog();
	 * 
	 * Video v = new Video( "video" ); // Instantiate video player widget. //
	 * Specify a list of your video in one or more formats. // Different browsers
	 * support various different video formats. v.setSources( new ThemeResource(
	 * "img/1_a_LoginLogout_subtitles.mp4" )
	 * 
	 * ); v.setWidth( "640px" ); // Set size of the video player's display area
	 * on-screen. v.setHeight( "360px" ); videoguideLayout.addComponent( v ); // Add
	 * the component to the window or layout.
	 * 
	 * // Button closeButton = new Button(new Icon("lumo", "cross"), (e) ->
	 * dialog.close()); //
	 * closeButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY); //
	 * dialog.getHeader().add(closeButton);
	 * 
	 * Button button = new Button("Show dialog", e -> dialog.open()); add(dialog,
	 * button); }
	 */

}
