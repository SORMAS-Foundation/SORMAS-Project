package de.symeda.sormas.ui.caze;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Paths;
import java.util.Date;

import com.vaadin.server.FileDownloader;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.Page;
import com.vaadin.server.Resource;
import com.vaadin.server.StreamResource;
import com.vaadin.ui.Button;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.Upload;
import com.vaadin.ui.Upload.Receiver;
import com.vaadin.ui.Upload.SucceededEvent;
import com.vaadin.ui.Upload.SucceededListener;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.importexport.InvalidColumnException;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.ui.login.LoginHelper;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.DownloadUtil;

@SuppressWarnings("serial")
public class CaseImportLayout extends VerticalLayout {

	private Button downloadErrorReportButton;
	
	public CaseImportLayout() {
		setMargin(true);
		
		// Step 1: Download SORMAS Import Guide
		String headline = "Download and read the SORMAS Import Guide";
		String infoText = "If this is your first time importing data into SORMAS, we strongly recommend to read the import guide first.";
		Resource buttonIcon = FontAwesome.DOWNLOAD;
		String buttonCaption = "Download Import Guide";
		CaseImportLayoutComponent importGuideComponent = new CaseImportLayoutComponent(1, headline, infoText, buttonIcon, buttonCaption);
		String importGuideFilePath = FacadeProvider.getImportExportFacade().getSormasImportGuideFilePath().toString();
		StreamResource importGuideResource = DownloadUtil.createStreamResource(null, importGuideFilePath, "SORMAS_Import_Guide.pdf", "application/pdf", "Import guide not available",
				"The SORMAS Import Guide can not be found on the server. Please contact an admin and tell them about this issue.");
		FileDownloader importGuideDownloader = new FileDownloader(importGuideResource);
		importGuideDownloader.extend(importGuideComponent.getButton());
		CssStyles.style(importGuideComponent, CssStyles.VSPACE_2);
		addComponent(importGuideComponent);
		
		// Step 2: Download case import template
		headline = "Download the case import template";
		infoText = "You can use this template .csv file to bring your data into a format SORMAS can read. Please do this every time you import data,"
				+ " never use a file you have downloaded before.";
		buttonCaption = "Download Case Import Template";
		CaseImportLayoutComponent importTemplateComponent = new CaseImportLayoutComponent(2, headline, infoText, buttonIcon, buttonCaption);
		String templateFilePath = FacadeProvider.getImportExportFacade().getCaseImportTemplateFilePath().toString();
		StreamResource templateResource = DownloadUtil.createStreamResource(null, templateFilePath, "sormas_import_case_template.csv", "text/csv",
				"Template not available", "The template file is not available. Please contact an admin and tell them about this issue.");
		FileDownloader templateFileDownloader = new FileDownloader(templateResource);
		templateFileDownloader.extend(importTemplateComponent.getButton());
		CssStyles.style(importTemplateComponent, CssStyles.VSPACE_2);
		addComponent(importTemplateComponent);
		
		// Step 3: Upload .csv file
		headline = "Import .csv file";
		infoText = "Depending on the amount of cases you want to import, this may take a while. You will receive a notification when the import process"
				+ "has finished.";
		CaseImportLayoutComponent importCasesComponent = new CaseImportLayoutComponent(3, headline, infoText, null, null);
		addComponent(importCasesComponent);
		CaseImportUploader receiver = new CaseImportUploader();
		Upload upload = new Upload("", receiver);
		upload.setButtonCaption("Upload Case List");
		CssStyles.style(upload, ValoTheme.BUTTON_PRIMARY, CssStyles.VSPACE_2);
		upload.addSucceededListener(receiver);
		addComponent(upload);
		
		// Step 4: Download error report
		headline = "Download error report";
		infoText = "If there were any cases that could not be imported, you will be offered a .csv file containing all these cases "
				+ " as well as the error descriptions.";
		buttonIcon = FontAwesome.DOWNLOAD;
		buttonCaption = "Download Error Report";
		CaseImportLayoutComponent errorReportComponent = new CaseImportLayoutComponent(4, headline, infoText, buttonIcon, buttonCaption);
		downloadErrorReportButton = errorReportComponent.getButton();
		errorReportComponent.getButton().setEnabled(false);
		errorReportComponent.getButton().addClickListener(e -> {
			
		});
		addComponent(errorReportComponent);
	}
	
	private class CaseImportLayoutComponent extends VerticalLayout {
		
		private Label headlineLabel;
		private Label infoTextLabel;
		private Button button;
		
		public CaseImportLayoutComponent(int step, String headline, String infoText, Resource buttonIcon, String buttonCaption) {
			setSpacing(false);
			
			headlineLabel = new Label("Step " + step + ": " + headline);
			CssStyles.style(headlineLabel, CssStyles.H3);
			addComponent(headlineLabel);
			
			infoTextLabel = new Label(infoText);
			addComponent(infoTextLabel);
			
			if (buttonCaption != null) {
				button = new Button(buttonCaption, buttonIcon);
				CssStyles.style(button, ValoTheme.BUTTON_PRIMARY, CssStyles.VSPACE_TOP_3);
				addComponent(button);
			}
		}
		
		public Button getButton() {
			return button;
		}
		
	}
	
	private class CaseImportUploader implements Receiver, SucceededListener {
		private File file;
		@Override
		public OutputStream receiveUpload(String fileName, String mimeType) {
			// Reject empty files
			if (fileName == null || fileName.isEmpty()) {
				new Notification("No file", "You have not selected a file to upload. Please select a .csv file containing the cases you want to import from your computer.", Type.ERROR_MESSAGE, false).show(Page.getCurrent());
				// Workaround because returning null here throws an uncatchable UploadException
				return new ByteArrayOutputStream();
			}
			// Reject all files except .csv files - we also need to accept excel files here
			if (!(mimeType.equals("text/csv") || mimeType.equals("application/vnd.ms-excel"))) {
				new Notification("Wrong file type", "Please provide a .csv file containing the cases you want to import. It's recommended to use the case import template file as a starting point.", Type.ERROR_MESSAGE, false).show(Page.getCurrent());
				// Workaround because returning null here throws an uncatchable UploadException
				return new ByteArrayOutputStream();
			}
			
			FileOutputStream fos = null;
			try {
				String newFileName = "sormas_case_import_" + DateHelper.formatDateForExport(new Date()) + "_" + DataHelper.getShortUuid(LoginHelper.getCurrentUser().getUuid()) + ".csv";
				file = new File(Paths.get(FacadeProvider.getConfigFacade().getTempFilesPath()).resolve(newFileName).toString());
				fos = new FileOutputStream(file);
			} catch (FileNotFoundException e) {
				new Notification("Import error", "Could not import file.", Type.ERROR_MESSAGE, false).show(Page.getCurrent());
				// Workaround because returning null here throws an uncatchable UploadException
				return new ByteArrayOutputStream();
			}
			
			downloadErrorReportButton.setEnabled(false);
			return fos;
		}
		
		@Override
		public void uploadSucceeded(SucceededEvent event) {
			if (file == null) {
				return;
			}
			
			try {
				String errorReportFilePath = FacadeProvider.getImportExportFacade().importCasesFromCsvFile(file.getPath(), LoginHelper.getCurrentUser().getUuid());
				if (errorReportFilePath == null) {
					new Notification("Import successful", "All cases have been imported. You can now close this window.", Type.HUMANIZED_MESSAGE, false).show(Page.getCurrent());
				} else {
					new Notification("Import successful", "The import has been successful, but some of the cases could not be imported due to malformed data. Please download the error report below.", Type.WARNING_MESSAGE, false).show(Page.getCurrent());

					StreamResource streamResource = DownloadUtil.createStreamResource(null, errorReportFilePath, "sormas_import_error_report.csv", "text/csv",
							"Error report not available", "The error report file is not available. Please contact an admin and tell them about this issue.");
					FileDownloader fileDownloader = new FileDownloader(streamResource);
					fileDownloader.extend(downloadErrorReportButton);
					downloadErrorReportButton.setEnabled(true);
				}
			} catch (IOException e) {
				new Notification("Import failed", "The import failed due to a critical error. Please contact your admin and inform them about this issue.", Type.ERROR_MESSAGE, false).show(Page.getCurrent());
			} catch (InvalidColumnException e) {
				new Notification("Invalid column", "The column \"" + e.getColumnName() + "\" is not part of the case or one of its connected entities. Please remove it from the .csv file and upload it again.", Type.ERROR_MESSAGE, false).show(Page.getCurrent());
			}
		}
		
	}
	
}
