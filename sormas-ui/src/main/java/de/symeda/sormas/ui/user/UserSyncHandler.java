/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2020 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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
 */

package de.symeda.sormas.ui.user;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.List;
import java.util.function.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.opencsv.CSVWriter;
import com.vaadin.server.Sizeable;
import com.vaadin.server.StreamResource;
import com.vaadin.ui.UI;
import com.vaadin.ui.Window;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.importexport.ImportExportUtils;
import de.symeda.sormas.api.user.UserCriteria;
import de.symeda.sormas.api.user.UserReferenceDto;
import de.symeda.sormas.api.user.UserSyncResult;
import de.symeda.sormas.api.utils.CSVUtils;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.ui.importer.ImportResultStatus;
import de.symeda.sormas.ui.utils.DownloadUtil;
import de.symeda.sormas.ui.utils.VaadinUiUtil;

/**
 * @author Alex Vidrean
 * @since 11-Dec-20
 */
public class UserSyncHandler {

	protected static final String ERROR_COLUMN_NAME = I18nProperties.getCaption(Captions.importErrorDescription);
	protected static final String USERNAME_COLUMN_NAME = I18nProperties.getCaption(Captions.User_uuid);

	protected final Logger logger = LoggerFactory.getLogger(getClass());

	/**
	 * The file path to the generated error report file that lists all problems that occurred during the sync.
	 */
	protected String errorReportFilePath;
	protected String errorReportFileName = "sormas_sync_error_report.csv";
	/**
	 * Called whenever one user has been processed. Used e.g. to update the progress bar.
	 */
	private Consumer<UserSyncProgressLayout.SyncResult> userSyncCallback;
	/**
	 * Whether the sync should be canceled after the current line.
	 */
	private boolean cancelAfterCurrent;
	/**
	 * Whether or not the current import has resulted in at least one error.
	 */
	private boolean hasImportError;
	/**
	 * CSV separator used in the file
	 */
	private final char csvSeparator;
	/**
	 * Used to populate the sync error report
	 */
	private CSVWriter errorReportCsvWriter;

	public UserSyncHandler(UserReferenceDto currentUser) {
		Path exportDirectory = Paths.get(FacadeProvider.getConfigFacade().getTempFilesPath());
		Path errorReportFilePath = exportDirectory.resolve(
			ImportExportUtils.TEMP_FILE_PREFIX + "_error_report_" + DataHelper.getShortUuid(currentUser.getUuid()) + "_"
				+ DateHelper.formatDateForExport(new Date()) + ".csv");
		this.errorReportFilePath = errorReportFilePath.toString();

		this.csvSeparator = FacadeProvider.getConfigFacade().getCsvSeparator();
	}

	public void startSync(Consumer<StreamResource> errorReportConsumer, UI currentUI) {

		long totalCount = FacadeProvider.getUserFacade().count(new UserCriteria());

		UserSyncProgressLayout progressLayout = new UserSyncProgressLayout(totalCount, currentUI, this::cancelImport);

		userSyncCallback = progressLayout::updateProgress;

		Window window = VaadinUiUtil.createPopupWindow();
		window.setCaption(I18nProperties.getString(Strings.headingSyncUsers));
		window.setWidth(800, Sizeable.Unit.PIXELS);
		window.setContent(progressLayout);
		window.setClosable(false);
		currentUI.addWindow(window);

		Thread importThread = new Thread(() -> {
			try {
				currentUI.setPollInterval(300);

				ImportResultStatus importResult = runUserSync();

				// Display a window presenting the import result
				currentUI.access(() -> {
					window.setClosable(true);
					progressLayout.makeClosable(window::close);

					if (importResult == ImportResultStatus.COMPLETED) {
						progressLayout.displaySuccessIcon();
						progressLayout.setInfoLabelText(I18nProperties.getString(Strings.messageUserSyncSuccessful));
					} else if (importResult == ImportResultStatus.COMPLETED_WITH_ERRORS) {
						progressLayout.displayWarningIcon();
						progressLayout.setInfoLabelText(I18nProperties.getString(Strings.messageUserSyncPartiallySuccessful));
					} else if (importResult == ImportResultStatus.CANCELED) {
						progressLayout.displaySuccessIcon();
						progressLayout.setInfoLabelText(I18nProperties.getString(Strings.messageUserSyncCanceled));
					} else {
						progressLayout.displayWarningIcon();
						progressLayout.setInfoLabelText(I18nProperties.getString(Strings.messageUserSyncCanceledErrors));
					}

					window.addCloseListener(e -> {
						if (importResult == ImportResultStatus.COMPLETED_WITH_ERRORS || importResult == ImportResultStatus.CANCELED_WITH_ERRORS) {
							StreamResource streamResource = createErrorReportStreamResource();
							errorReportConsumer.accept(streamResource);
						}
					});

					currentUI.setPollInterval(-1);
				});
			} catch (Exception e) {
				logger.error(e.getMessage(), e);

				currentUI.access(() -> {
					window.setClosable(true);
					progressLayout.makeClosable(window::close);
					progressLayout.displayErrorIcon();
					progressLayout.setInfoLabelText(I18nProperties.getString(Strings.messageUserSyncFailedFull));
					currentUI.setPollInterval(-1);
				});
			}
		});

		importThread.start();

	}

	public ImportResultStatus runUserSync() throws IOException {
		logger.debug("runUserSync");

		long t0 = System.currentTimeMillis();

		try {
			errorReportCsvWriter = CSVUtils.createCSVWriter(createErrorReportWriter(), this.csvSeparator);

			// Write first line to the error report writer
			errorReportCsvWriter.writeNext(new String[] {ERROR_COLUMN_NAME, USERNAME_COLUMN_NAME});

			// Sync all users
			List<String> userUuids = FacadeProvider.getUserFacade().getAllUuids();
			for(String uuid: userUuids) {
				logger.debug("runSync - uuid {}", uuid);

				UserSyncProgressLayout.SyncResult syncResult = syncUser(uuid);

				if (userSyncCallback != null) {
					userSyncCallback.accept(syncResult);
				}
				if (cancelAfterCurrent) {
					break;
				}
			}

			int usersCount = userUuids.size();

			if (logger.isDebugEnabled()) {
				logger.debug("runSync - done");
				long dt = System.currentTimeMillis() - t0;
				logger.debug("sync of {} users took {} ms ({} ms/line)", usersCount, dt, usersCount > 0 ? dt / usersCount : -1);
			}

			if (cancelAfterCurrent) {
				if (!hasImportError) {
					return ImportResultStatus.CANCELED;
				} else {
					return ImportResultStatus.CANCELED_WITH_ERRORS;
				}
			} else if (hasImportError) {
				return ImportResultStatus.COMPLETED_WITH_ERRORS;
			} else {
				return ImportResultStatus.COMPLETED;
			}
		} finally {
			if (errorReportCsvWriter != null) {
				errorReportCsvWriter.close();
			}
		}
	}

	private UserSyncProgressLayout.SyncResult syncUser(String userUuid) {

		try {
			UserSyncResult result = FacadeProvider.getUserFacade().syncUser(userUuid);
			if (result.isSuccess()) {
				return UserSyncProgressLayout.SyncResult.SUCCESS;
			} else {
				writeImportError(userUuid, result.getErrorMessage());
				return UserSyncProgressLayout.SyncResult.ERROR;
			}
		} catch (Exception e) {
			writeImportError(userUuid, e.getMessage());
			return UserSyncProgressLayout.SyncResult.ERROR;
		}
	}

	public void cancelImport() {
		cancelAfterCurrent = true;
	}

	protected Writer createErrorReportWriter() throws IOException {
		File errorReportFile = new File(errorReportFilePath);
		if (errorReportFile.exists()) {
			errorReportFile.delete();
		}

		return new FileWriter(errorReportFile.getPath());
	}

	protected StreamResource createErrorReportStreamResource() {
		return DownloadUtil.createFileStreamResource(
			errorReportFilePath,
			errorReportFileName,
			"text/csv",
			I18nProperties.getString(Strings.headingErrorReportNotAvailable),
			I18nProperties.getString(Strings.messageErrorReportNotAvailable));
	}

	protected void writeImportError(String userUuid, String message) {
		hasImportError = true;
		errorReportCsvWriter.writeNext(new String[] {message, userUuid});
	}
}
