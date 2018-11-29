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
package de.symeda.sormas.ui.caze;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Paths;
import java.util.Date;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import com.vaadin.server.ClassResource;
import com.vaadin.server.Extension;
import com.vaadin.server.FileDownloader;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.Page;
import com.vaadin.server.Resource;
import com.vaadin.server.StreamResource;
import com.vaadin.ui.Button;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.UI;
import com.vaadin.ui.Upload;
import com.vaadin.ui.Upload.Receiver;
import com.vaadin.ui.Upload.SucceededEvent;
import com.vaadin.ui.Upload.SucceededListener;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.ValoTheme;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.I18nProperties;
import de.symeda.sormas.api.importexport.ImportExportUtils;
import de.symeda.sormas.api.importexport.InvalidColumnException;
import de.symeda.sormas.api.person.PersonIndexDto;
import de.symeda.sormas.api.user.UserReferenceDto;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.ui.importer.CaseImportProgressLayout;
import de.symeda.sormas.ui.importer.CaseImportResult;
import de.symeda.sormas.ui.importer.CaseImporter;
import de.symeda.sormas.ui.importer.ImportPersonSelectField;
import de.symeda.sormas.ui.importer.ImportResultStatus;
import de.symeda.sormas.ui.importer.ImportSimilarityInput;
import de.symeda.sormas.ui.importer.ImportSimilarityResult;
import de.symeda.sormas.ui.login.LoginHelper;
import de.symeda.sormas.ui.utils.CommitDiscardWrapperComponent;
import de.symeda.sormas.ui.utils.CommitDiscardWrapperComponent.CommitListener;
import de.symeda.sormas.ui.utils.CommitDiscardWrapperComponent.DiscardListener;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.DownloadUtil;
import de.symeda.sormas.ui.utils.VaadinUiUtil;

@SuppressWarnings("serial")
public class CaseImportLayout extends VerticalLayout {

	private Button downloadErrorReportButton;
	private final UserReferenceDto currentUser;
	private final UI currentUI;

	public CaseImportLayout() {
		currentUser = LoginHelper.getCurrentUserAsReference();
		currentUI = UI.getCurrent();
		
		setMargin(true);

		// Step 1: Download SORMAS Import Guide
		String headline = "Download and read the SORMAS Import Guide and the Data Dictionary";
		String infoText = "If this is your first time importing data into SORMAS, we strongly recommend to read the import guide first.";
		Resource buttonIcon = FontAwesome.FILE_PDF_O;
		String buttonCaption = "Download Import Guide";
		CaseImportLayoutComponent importGuideComponent = new CaseImportLayoutComponent(1, headline, infoText, buttonIcon, buttonCaption);
		FileDownloader importGuideDownloader = new FileDownloader(new ClassResource("/SORMAS_Import_Guide.pdf"));
		importGuideDownloader.extend(importGuideComponent.getButton());
		addComponent(importGuideComponent);

		Button dataDictionaryButton = new Button("Download Data Dictionary", FontAwesome.FILE_EXCEL_O);
		CssStyles.style(dataDictionaryButton, ValoTheme.BUTTON_PRIMARY, CssStyles.VSPACE_TOP_3);
		FileDownloader dataDictionaryDownloader = new FileDownloader(new ClassResource("/doc/SORMAS_Data_Dictionary.xlsx"));
		dataDictionaryDownloader.extend(dataDictionaryButton);
		addComponent(dataDictionaryButton);
		CssStyles.style(dataDictionaryButton, CssStyles.VSPACE_2);

		// Step 2: Download case import template
		headline = "Download the case import template";
		infoText = "You can use this template .csv file to bring your data into a format SORMAS can read. Please do this every time you import data,"
				+ " never use a file you have downloaded before.";
		buttonIcon = FontAwesome.DOWNLOAD;
		buttonCaption = "Download Case Import Template";
		CaseImportLayoutComponent importTemplateComponent = new CaseImportLayoutComponent(2, headline, infoText, buttonIcon, buttonCaption);
		String templateFilePath = FacadeProvider.getImportFacade().getCaseImportTemplateFilePath().toString();
		StreamResource templateResource = DownloadUtil.createFileStreamResource(templateFilePath, "sormas_import_case_template.csv", "text/csv",
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
		CssStyles.style(upload, CssStyles.VSPACE_2);
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
				file = null;
				new Notification("No file", "You have not selected a file to upload. Please select a .csv file containing the cases you want to import from your computer.", Type.ERROR_MESSAGE, false).show(Page.getCurrent());
				// Workaround because returning null here throws an uncatchable UploadException
				return new ByteArrayOutputStream();
			}
			// Reject all files except .csv files - we also need to accept excel files here
			if (!(mimeType.equals("text/csv") || mimeType.equals("application/vnd.ms-excel"))) {
				file = null;
				new Notification("Wrong file type", "Please provide a .csv file containing the cases you want to import. It's recommended to use the case import template file as a starting point.", Type.ERROR_MESSAGE, false).show(Page.getCurrent());
				// Workaround because returning null here throws an uncatchable UploadException
				return new ByteArrayOutputStream();
			}

			FileOutputStream fos = null;
			try {
				String newFileName = ImportExportUtils.TEMP_FILE_PREFIX + "_case_import_" + DateHelper.formatDateForExport(new Date()) + "_" + DataHelper.getShortUuid(LoginHelper.getCurrentUser().getUuid()) + ".csv";
				file = new File(Paths.get(FacadeProvider.getConfigFacade().getTempFilesPath()).resolve(newFileName).toString());
				fos = new FileOutputStream(file);
			} catch (FileNotFoundException e) {
				file = null;
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

			// Remove FileDownloader extension from "Download Error Report" button
			for (int i = 0; i < downloadErrorReportButton.getExtensions().size(); i++) {
				Extension ext = downloadErrorReportButton.getExtensions().iterator().next();
				downloadErrorReportButton.removeExtension(ext);
			}

			try {
				final CaseImporter caseImporter = new CaseImporter(file.getPath(), currentUser);
				final CaseImportProgressLayout progressLayout = new CaseImportProgressLayout(caseImporter.getNumberOfCases(), new Runnable() {
					@Override
					public void run() {
						caseImporter.cancelImport();
					}
				});

				BiConsumer<ImportSimilarityInput, Consumer<ImportSimilarityResult>> similarityCallback = (input, resultConsumer) -> {
					handleSimilarityCallback(input, resultConsumer);
				};

				Consumer<CaseImportResult> caseImportedCallback = new Consumer<CaseImportResult>() {
					@Override
					public void accept(CaseImportResult result) {
						progressLayout.updateProgress(result);
					}
				};

				Window popup = VaadinUiUtil.createPopupWindow();
				popup.setCaption("Case Import");
				popup.setWidth(800, Unit.PIXELS);
				popup.setContent(progressLayout);
				popup.setClosable(false);
				currentUI.addWindow(popup);

				ImportThread importThread = new ImportThread(caseImporter, similarityCallback, caseImportedCallback, popup, progressLayout);
				importThread.start();
			} catch (IOException e) {
				new Notification("Import failed", "The import failed due to a critical error. Please contact your admin and inform them about this issue.", Type.ERROR_MESSAGE, false).show(Page.getCurrent());
			}
		}
	}
	
	private void handleSimilarityCallback(ImportSimilarityInput input, Consumer<ImportSimilarityResult> resultConsumer) {
		currentUI.accessSynchronously(new Runnable() {
			@Override
			public void run() {
				ImportPersonSelectField personSelect = new ImportPersonSelectField(input.getPersons(), input.getCaze(), input.getPerson(), currentUser);
				personSelect.setWidth(1024, Unit.PIXELS);
				personSelect.selectBestMatch();

				final CommitDiscardWrapperComponent<ImportPersonSelectField> selectOrCreateComponent =
						new CommitDiscardWrapperComponent<>(personSelect);

				selectOrCreateComponent.addCommitListener(new CommitListener() {
					@Override
					public void onCommit() {
						PersonIndexDto person = personSelect.getValue();
						if (person != null) {
							resultConsumer.accept(new ImportSimilarityResult(person, personSelect.getSelectedMatchingCase(), 
									personSelect.isUsePerson(), personSelect.isMergeCase(), false, false));
						} else {
							resultConsumer.accept(new ImportSimilarityResult(null, null, false, false, false, false));
						}
					}});

				DiscardListener discardListener = new DiscardListener() {
					@Override
					public void onDiscard() {
						resultConsumer.accept(new ImportSimilarityResult(null, null, false, false, false, true));
					}
				};
				selectOrCreateComponent.addDiscardListener(discardListener);
				selectOrCreateComponent.getDiscardButton().setCaption(I18nProperties.getText("cancel"));

				Button skipButton = new Button(I18nProperties.getText("skip"));
				skipButton.addClickListener(e -> {
					currentUI.accessSynchronously(new Runnable() {
						@Override
						public void run() {
							selectOrCreateComponent.removeDiscardListener(discardListener);
							selectOrCreateComponent.discard();
							resultConsumer.accept(new ImportSimilarityResult(null, null, false, false, true, false));
						}
					});
				});
				selectOrCreateComponent.getButtonsPanel().addComponentAsFirst(skipButton);

				personSelect.setSelectionChangeCallback((commitAllowed) -> {
					selectOrCreateComponent.getCommitButton().setEnabled(commitAllowed);
				});

				VaadinUiUtil.showModalPopupWindow(selectOrCreateComponent, "Pick or create person");
			}});
	}
	
	private class ImportThread extends Thread {
		private CaseImporter caseImporter;
		private BiConsumer<ImportSimilarityInput, Consumer<ImportSimilarityResult>> similarityCallback;
		private Consumer<CaseImportResult> caseImportedCallback;
		private Window popup;
		private CaseImportProgressLayout progressLayout;
		
		public ImportThread(CaseImporter caseImporter, BiConsumer<ImportSimilarityInput, Consumer<ImportSimilarityResult>> similarityCallback,
				Consumer<CaseImportResult> caseImportedCallback, Window popup, CaseImportProgressLayout progressLayout) {
			this.caseImporter = caseImporter;
			this.similarityCallback = similarityCallback;
			this.caseImportedCallback = caseImportedCallback;
			this.popup = popup;
			this.progressLayout = progressLayout;
		}
		
		@Override
		public void run() {
			try {
				currentUI.setPollInterval(50);

				ImportResultStatus importResult = caseImporter.importAllCases(similarityCallback, caseImportedCallback);
				String errorReportFilePath = caseImporter.getErrorReportFilePath();

				currentUI.access(new Runnable() {
					@Override
					public void run() {
						popup.setClosable(true);
						progressLayout.makeClosable(() -> {
							popup.close();
						});

						if (importResult == ImportResultStatus.COMPLETED) {
							progressLayout.displaySuccessIcon();
							progressLayout.setInfoLabelText("<b>Import successful!</b><br/>All cases have been imported. You can now close this window and the \"Import Cases\" dialog.");
						} else if (importResult == ImportResultStatus.COMPLETED_WITH_ERRORS) {
							progressLayout.displayWarningIcon();
							progressLayout.setInfoLabelText("<b>Import partially successful!</b><br/>The import has been successful, but some of the cases could not be imported due to malformed data. Please close this window and download the error report in the \"Import Cases\" dialog.");
						} else if (importResult == ImportResultStatus.CANCELED) {
							progressLayout.displaySuccessIcon();
							progressLayout.setInfoLabelText("<b>Import canceled!</b><br/>The import has been canceled. All already processed cases have been successfully imported. You can now close this window and the \"Import Cases\" dialog.");
						} else {
							progressLayout.displayWarningIcon();
							progressLayout.setInfoLabelText("<b>Import canceled!</b><br/>The import has been canceled. Some of the already processed cases could not be imported due to malformed data. Please close this window and download the error report in the \"Import Cases\" dialog.");
						}								

						popup.addCloseListener(e -> {
							if (importResult == ImportResultStatus.COMPLETED_WITH_ERRORS || importResult == ImportResultStatus.CANCELED_WITH_ERRORS) {
								StreamResource streamResource = DownloadUtil.createFileStreamResource(errorReportFilePath, "sormas_import_error_report.csv", "text/csv",
										"Error report not available", "The error report file is not available. Please contact an admin and tell them about this issue.");
								FileDownloader fileDownloader = new FileDownloader(streamResource);
								fileDownloader.extend(downloadErrorReportButton);
								downloadErrorReportButton.setEnabled(true);
							}
						});

						currentUI.setPollInterval(-1);
					}
				});
			} catch (IOException | InterruptedException e) {
				currentUI.access(new Runnable() {
					@Override
					public void run() {
						popup.setClosable(true);
						progressLayout.makeClosable(() -> {
							popup.close();
						});
						progressLayout.displayErrorIcon();
						progressLayout.setInfoLabelText("<b>Import failed!</b><br/>The import failed due to a critical error. Please contact your admin and inform them about this issue.");
						currentUI.setPollInterval(-1);
					}
				});
			} catch (InvalidColumnException e) {
				currentUI.access(new Runnable() {
					@Override
					public void run() {
						popup.setClosable(true);
						progressLayout.makeClosable(() -> {
							popup.close();
						});
						progressLayout.displayErrorIcon();
						progressLayout.setInfoLabelText("<b>Invalid column!</b><br/>The column \"" + e.getColumnName() + "\" is not part of the case or one of its connected entities. Please remove it from the .csv file and upload it again.");
						currentUI.setPollInterval(-1);
					}
				});
			}
		}
	}

}
