package de.symeda.sormas.ui.importer;

import java.io.IOException;
import java.util.function.Function;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.server.ClassResource;
import com.vaadin.server.Extension;
import com.vaadin.server.FileDownloader;
import com.vaadin.server.Page;
import com.vaadin.server.Resource;
import com.vaadin.server.StreamResource;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.UI;
import com.vaadin.ui.themes.ValoTheme;
import com.vaadin.v7.ui.Upload;
import com.vaadin.v7.ui.VerticalLayout;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.importexport.ValueSeparator;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.ui.UiUtil;
import de.symeda.sormas.ui.utils.ButtonHelper;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.DownloadUtil;

@SuppressWarnings("serial")
public class AbstractImportLayout extends VerticalLayout {

	protected Button downloadErrorReportButton;
	protected Upload upload;
	protected final UserDto currentUser;
	protected final UI currentUI;
	protected ImportReceiver generatedReceiver;
	protected ComboBox separator;

	public AbstractImportLayout() {
		currentUser = UiUtil.getUser();
		currentUI = UI.getCurrent();
		setSpacing(false);
		setMargin(true);
		createSeparatorComboBox();
	}

	protected void addDownloadResourcesComponent(int step, ClassResource importGuideResource) {
		String headline = I18nProperties.getString(Strings.headingDownloadImportGuide);
		String infoText = I18nProperties.getString(Strings.infoDownloadImportGuide);
		Resource buttonIcon = VaadinIcons.FILE_PRESENTATION;
		String buttonCaption = I18nProperties.getCaption(Captions.importDownloadImportGuide);
		ImportLayoutComponent importGuideComponent = new ImportLayoutComponent(step, headline, infoText, buttonIcon, buttonCaption);
		FileDownloader importGuideDownloader = new FileDownloader(importGuideResource);
		importGuideDownloader.extend(importGuideComponent.getButton());
		addComponent(importGuideComponent);

		Button dataDictionaryButton = ButtonHelper.createIconButton(
			Captions.importDownloadDataDictionary,
			VaadinIcons.FILE_TABLE,
			null,
			ValoTheme.BUTTON_PRIMARY,
			CssStyles.VSPACE_TOP_3,
			CssStyles.VSPACE_2);
		DownloadUtil.attachDataDictionaryDownloader(dataDictionaryButton);
		addComponent(dataDictionaryButton);
	}

	protected void addDownloadImportTemplateComponent(int step, String templateFilePath, String templateFileName) {
		String headline = I18nProperties.getString(Strings.headingDownloadImportTemplate);
		String infoText = I18nProperties.getString(Strings.infoDownloadImportTemplate);
		Resource buttonIcon = VaadinIcons.DOWNLOAD;
		String buttonCaption = I18nProperties.getCaption(Captions.importDownloadImportTemplate);
		ImportLayoutComponent importTemplateComponent = new ImportLayoutComponent(step, headline, infoText, buttonIcon, buttonCaption);

		try {
			String content = FacadeProvider.getImportFacade().getImportTemplateContent(templateFilePath);
			StreamResource templateResource = DownloadUtil.createStringStreamResource(content, templateFileName, "text/csv");

			FileDownloader templateFileDownloader = new FileDownloader(templateResource);
			templateFileDownloader.extend(importTemplateComponent.getButton());

			CssStyles.style(importTemplateComponent, CssStyles.VSPACE_2);
			addComponent(importTemplateComponent);
		} catch (IOException e) {
			new Notification(
				I18nProperties.getString(Strings.headingTemplateNotAvailable),
				I18nProperties.getString(Strings.messageTemplateNotAvailable),
				Notification.Type.ERROR_MESSAGE,
				false).show(Page.getCurrent());
		}
	}

	protected void addImportCsvComponent(int step, ImportReceiver receiver) {
		String headline = I18nProperties.getString(Strings.headingImportCsvFile);
		String infoText = I18nProperties.getString(Strings.infoImportCsvFile);
		ImportLayoutComponent importCsvComponent = new ImportLayoutComponent(step, headline, infoText, null, null);
		CssStyles.style(importCsvComponent, CssStyles.VSPACE_3);
		addComponent(importCsvComponent);
		addComponent(separator);
		upload = new Upload("", receiver);
		upload.setButtonCaption(I18nProperties.getCaption(Captions.importImportData));
		CssStyles.style(upload, CssStyles.VSPACE_2);
		upload.addStartedListener(receiver);
		upload.addSucceededListener(receiver);
		addComponent(upload);
	}

	private ComboBox createSeparatorComboBox() {
		ComboBox comboBox = new ComboBox();
		comboBox.setCaption(I18nProperties.getCaption(Captions.importValueSeparator));
		comboBox.setItems(ValueSeparator.values());
		comboBox.setValue(ValueSeparator.DEFAULT);
		comboBox.setEmptySelectionAllowed(false);
		comboBox.setItemCaptionGenerator(item -> ((ValueSeparator) item).getCaption(FacadeProvider.getConfigFacade().getCsvSeparator()));
		separator = comboBox;
		return comboBox;
	}

	protected void addImportCsvComponentWithOverwrite(int step, Function<Boolean, ImportReceiver> receiverGenerator) {
		String headline = I18nProperties.getString(Strings.headingImportCsvFile);
		String infoText = I18nProperties.getString(Strings.infoImportCsvFile);
		ImportLayoutComponent importCsvComponent = new ImportLayoutComponent(step, headline, infoText, null, null);
		addComponent(importCsvComponent);
		generatedReceiver = receiverGenerator.apply(false);
		upload = new Upload("", generatedReceiver);
		upload.setButtonCaption(I18nProperties.getCaption(Captions.importImportData));
		CssStyles.style(upload, CssStyles.VSPACE_2);
		upload.addStartedListener(generatedReceiver);
		upload.addSucceededListener(generatedReceiver);

		HorizontalLayout checkboxBar = new HorizontalLayout();
		checkboxBar.setDefaultComponentAlignment(Alignment.MIDDLE_LEFT);
		checkboxBar.setDescription(I18nProperties.getString(Strings.infoImportInfrastructureAllowOverwrite));
		CssStyles.style(checkboxBar, CssStyles.VSPACE_TOP_3);
		CheckBox allowOverwrite = new CheckBox(I18nProperties.getCaption(Captions.infrastructureImportAllowOverwrite));
		allowOverwrite.setValue(false);
		checkboxBar.addComponent(allowOverwrite);
		Label labelInfo = new Label(VaadinIcons.INFO_CIRCLE.getHtml(), ContentMode.HTML);
		checkboxBar.addComponent(labelInfo);
		CssStyles.style(checkboxBar, CssStyles.VSPACE_3);
		addComponent(checkboxBar);
		addComponent(separator);
		addComponent(upload);

		allowOverwrite.addValueChangeListener(e -> {
			upload.removeSucceededListener(generatedReceiver);
			generatedReceiver = receiverGenerator.apply(e.getValue());
			upload.setReceiver(generatedReceiver);
			upload.addStartedListener(generatedReceiver);
			upload.addSucceededListener(generatedReceiver);
		});
	}

	protected void addDownloadErrorReportComponent(int step) {
		String headline = I18nProperties.getString(Strings.headingDownloadErrorReport);
		String infoText = I18nProperties.getString(Strings.infoDownloadErrorReport);
		Resource buttonIcon = VaadinIcons.DOWNLOAD;
		String buttonCaption = I18nProperties.getCaption(Captions.importDownloadErrorReport);
		ImportLayoutComponent errorReportComponent = new ImportLayoutComponent(step, headline, infoText, buttonIcon, buttonCaption);
		downloadErrorReportButton = errorReportComponent.getButton();
		errorReportComponent.getButton().setEnabled(false);
		addComponent(errorReportComponent);
	}

	protected void resetDownloadErrorReportButton() {
		downloadErrorReportButton.setEnabled(false);
		for (int i = 0; i < downloadErrorReportButton.getExtensions().size(); i++) {
			Extension ext = downloadErrorReportButton.getExtensions().iterator().next();
			downloadErrorReportButton.removeExtension(ext);
		}
	}

	protected void extendDownloadErrorReportButton(StreamResource streamResource) {
		FileDownloader fileDownloader = new FileDownloader(streamResource);
		fileDownloader.extend(downloadErrorReportButton);
		downloadErrorReportButton.setEnabled(true);
	}
}
