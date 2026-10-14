package de.symeda.sormas.ui.importer.hierarchical;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.opencsv.CSVWriter;
import com.vaadin.server.FileDownloader;
import com.vaadin.server.StreamResource;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.ui.Button;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.Grid;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.ProgressBar;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.ValoTheme;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.importexport.hierarchical.BlockOutcome;
import de.symeda.sormas.api.importexport.hierarchical.BlockStatus;
import de.symeda.sormas.api.importexport.hierarchical.HierarchicalImportFacade;
import de.symeda.sormas.api.importexport.hierarchical.ImportIssueDto;
import de.symeda.sormas.api.importexport.hierarchical.ImportPreviewDto;
import de.symeda.sormas.api.importexport.hierarchical.ImportReportDto;
import de.symeda.sormas.api.importexport.hierarchical.ImportSheet;
import de.symeda.sormas.api.importexport.hierarchical.OperationType;
import de.symeda.sormas.api.importexport.hierarchical.RecordOperationDto;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.api.utils.CSVUtils;
import de.symeda.sormas.ui.UiUtil;
import de.symeda.sormas.ui.utils.ButtonHelper;
import de.symeda.sormas.ui.utils.CssStyles;

public class HierarchicalImportLayout extends VerticalLayout {

	private static final long serialVersionUID = 1L;
	private static final int COMMIT_CHUNK_SIZE = 20;
	private static final String NBSP = "\u00A0";
	private static final Logger LOGGER = LoggerFactory.getLogger(HierarchicalImportLayout.class);

	private final HierarchicalImportFacade facade = FacadeProvider.getHierarchicalImportFacade();
	private final ImportWizardModel model = new ImportWizardModel();
	private final String definitionId;
	private final UI ui = UI.getCurrent();
	private final UserDto currentUser = UiUtil.getUser();

	private final Label nestedNotice = new Label(I18nProperties.getString(Strings.messageHierarchicalImportNestedRows), ContentMode.TEXT);
	private final Label ignoredColumnsNotice = new Label("", ContentMode.TEXT);
	private final Label readyCount = new Label("", ContentMode.TEXT);
	private final Label unchangedCount = new Label("", ContentMode.TEXT);
	private final Label errorCount = new Label("", ContentMode.TEXT);
	private final CssLayout blockCounts = new CssLayout(readyCount, unchangedCount, errorCount);
	private final Label statusMessage = new Label("", ContentMode.TEXT);
	private final Label countsLine = new Label("", ContentMode.TEXT);
	private final Label readyHeading = new Label("", ContentMode.TEXT);
	private final Label readyMessage = new Label(I18nProperties.getString(Strings.messageHierarchicalImportNothingToReview), ContentMode.TEXT);
	private final VerticalLayout readyPanel = new VerticalLayout(readyHeading, readyMessage);
	private final Grid<ImportIssueDto> issueGrid = new Grid<>();
	private final Grid<PreviewPresentation.ChangeRow> changeGrid = new Grid<>();
	private final Grid<RecordOperationDto> newRecordGrid = new Grid<>();
	private final TabSheet resultTabs = new TabSheet();
	private final ProgressBar progress = new ProgressBar(0f);
	private final Button importButton = ButtonHelper.createButton(Captions.actionImport, e -> startCommit(), ValoTheme.BUTTON_PRIMARY);
	private final Button cancelButton = ButtonHelper.createButton(Captions.actionCancel, e -> model.requestCancel());
	private final Button errorFileButton = ButtonHelper.createButton(Captions.hierarchicalImportDownloadErrors, (Button.ClickListener) null);
	private final FileDownloader errorFileDownloader = new FileDownloader(new StreamResource(() -> null, "errors.csv"));
	private char csvSeparator = FacadeProvider.getConfigFacade().getCsvSeparator();

	public HierarchicalImportLayout(String definitionId) {
		this.definitionId = definitionId;

		issueGrid.addColumn(ImportIssueDto::getLineNumber).setCaption(I18nProperties.getCaption(Captions.hierarchicalImportLine));
		issueGrid.addColumn(ImportIssueDto::getColumn).setCaption(I18nProperties.getCaption(Captions.hierarchicalImportColumn));
		issueGrid.addColumn(ImportIssueDto::getMessage)
			.setCaption(I18nProperties.getCaption(Captions.hierarchicalImportProblem))
			.setDescriptionGenerator(ImportIssueDto::getMessage);

		changeGrid.addColumn(PreviewPresentation.ChangeRow::getRecord).setCaption(I18nProperties.getCaption(Captions.hierarchicalImportRecord));
		changeGrid.addColumn(row -> PreviewPresentation.matchLabel(row.getMatchReason()))
			.setCaption(I18nProperties.getCaption(Captions.hierarchicalImportMatch));
		changeGrid.addColumn(PreviewPresentation.ChangeRow::getField).setCaption(I18nProperties.getCaption(Captions.hierarchicalImportField));
		changeGrid.addColumn(PreviewPresentation.ChangeRow::getChange)
			.setCaption(I18nProperties.getCaption(Captions.hierarchicalImportChange))
			.setDescriptionGenerator(PreviewPresentation.ChangeRow::getChange);

		newRecordGrid.addColumn(RecordOperationDto::getLineNumber).setCaption(I18nProperties.getCaption(Captions.hierarchicalImportLine));
		newRecordGrid.addColumn(operation -> recordType(operation.getLevel()))
			.setCaption(I18nProperties.getCaption(Captions.hierarchicalImportRecordType));
		newRecordGrid.addColumn(RecordOperationDto::getLabel).setCaption(I18nProperties.getCaption(Captions.hierarchicalImportRecord));
		newRecordGrid.addColumn(operation -> PreviewPresentation.matchLabel(operation.getMatchReason()))
			.setCaption(I18nProperties.getCaption(Captions.hierarchicalImportMatch));

		for (Grid<?> grid : List.of(issueGrid, changeGrid, newRecordGrid)) {
			grid.setWidth(100, Unit.PERCENTAGE);
			resultTabs.addTab(grid);
		}
		resultTabs.setWidth(100, Unit.PERCENTAGE);

		readyHeading.addStyleName(ValoTheme.LABEL_SUCCESS);
		readyHeading.addStyleName(ValoTheme.LABEL_BOLD);
		readyPanel.setMargin(false);
		readyPanel.setSpacing(false);
		for (Label count : List.of(readyCount, unchangedCount, errorCount, statusMessage)) {
			count.addStyleName(ValoTheme.LABEL_BOLD);
		}

		errorFileDownloader.extend(errorFileButton);

		ignoredColumnsNotice.addStyleName(CssStyles.LABEL_WARNING);
		ignoredColumnsNotice.setWidth(100, Unit.PERCENTAGE);
		setMargin(false);

		addComponents(
			nestedNotice,
			ignoredColumnsNotice,
			blockCounts,
			statusMessage,
			countsLine,
			readyPanel,
			resultTabs,
			progress,
			new HorizontalLayout(errorFileButton, cancelButton, importButton));
		setWidth(100, Unit.PERCENTAGE);
		refresh();
	}

	public void separator(char csvSeparator) {
		this.csvSeparator = csvSeparator;
	}

	public void preview(ImportSheet sheet) {
		model.previewed(sheet, facade.checkImport(definitionId, sheet));
		refresh();
	}

	private void startCommit() {
		List<Map<Integer, String>> chunks = model.commitChunks(COMMIT_CHUNK_SIZE);
		ui.setPollInterval(500);
		refresh();
		new Thread(() -> {
			Optional<RuntimeException> failure = Optional.empty();
			try {
				I18nProperties.setUserLanguage(currentUser.getLanguage());
				FacadeProvider.getI18nFacade().setUserLanguage(currentUser.getLanguage());
				failure = ChunkedCommit.run(
					chunks,
					chunk -> facade.saveImport(definitionId, model.sheet(), new HashMap<>(chunk)),
					(report, done) -> ui.access(() -> {
						model.chunkCommitted(report);
						progress.setValue(done);
					}),
					model::isCancelRequested);
			} catch (RuntimeException e) {
				failure = Optional.of(e);
			} finally {
				Optional<RuntimeException> outcome = failure;
				ui.access(() -> finishCommit(outcome));
			}
		}).start();
	}

	private void finishCommit(Optional<RuntimeException> failure) {
		model.finished();
		ui.setPollInterval(-1);
		refresh();
		failure.ifPresent(e -> {
			LOGGER.error("Nested import commit failed", e);
			Notification.show(
				I18nProperties.getString(Strings.headingImportFailed),
				I18nProperties.getString(Strings.messageImportFailedFull),
				Notification.Type.ERROR_MESSAGE);
		});
	}

	private void refresh() {
		ImportPreviewDto preview = model.preview();
		boolean previewed = preview != null;
		ImportWizardModel.Step step = model.step();

		nestedNotice.setVisible(previewed);
		String ignoredColumns = previewed ? PreviewPresentation.ignoredColumnsMessage(preview) : "";
		ignoredColumnsNotice.setValue(ignoredColumns);
		ignoredColumnsNotice.setVisible(!ignoredColumns.isEmpty());

		importButton.setVisible(step == ImportWizardModel.Step.PREVIEWED);
		importButton.setEnabled(model.canCommit());
		cancelButton.setVisible(step == ImportWizardModel.Step.COMMITTING);
		progress.setVisible(step == ImportWizardModel.Step.COMMITTING || step == ImportWizardModel.Step.DONE);
		errorFileButton.setVisible(previewed && (!model.failedBlocks().isEmpty() || !model.looseIssues().isEmpty()));

		blockCounts.setVisible(false);
		statusMessage.setVisible(false);
		countsLine.setVisible(previewed && preview.getFileIssues().isEmpty());
		readyPanel.setVisible(false);
		resultTabs.setVisible(false);
		if (previewed) {
			showPreview(preview, step);
		}
		recenterWindow();
	}

	private void showPreview(ImportPreviewDto preview, ImportWizardModel.Step step) {
		long ready = preview.countBlocks(BlockStatus.READY);
		importButton.setCaption(I18nProperties.getCaption(Captions.actionImport) + " (" + ready + ")");
		showStatus(preview, step);
		countsLine.setValue(countsText(preview));

		PreviewPresentation presentation = new PreviewPresentation(preview, model.report());
		readyHeading.setValue(String.format(I18nProperties.getString(Strings.headingHierarchicalImportReady), ready));
		readyPanel.setVisible(step == ImportWizardModel.Step.PREVIEWED && model.canCommit() && presentation.nothingToReview());

		showTab(issueGrid, Captions.hierarchicalImportProblems, presentation.problems());
		resultTabs.getTab(issueGrid).setStyleName(presentation.hasProblems() ? CssStyles.LABEL_CRITICAL : null);
		showTab(changeGrid, Captions.hierarchicalImportChanges, presentation.changes());
		showTab(newRecordGrid, Captions.hierarchicalImportNewRecords, presentation.newRecords());
		resultTabs.setVisible(anyTabVisible());
		selectFirstVisibleTab();

		errorFileDownloader.setFileDownloadResource(
			new StreamResource(() -> csv(ErrorFile.rows(model.sheet(), model.failedBlocks(), model.looseIssues())), "sormas_import_errors.csv"));
	}

	private <T> void showTab(Grid<T> grid, String captionKey, List<T> rows) {
		grid.setItems(rows);
		grid.setHeightByRows(PreviewPresentation.gridRows(rows.size()));
		TabSheet.Tab tab = resultTabs.getTab(grid);
		tab.setCaption(I18nProperties.getCaption(captionKey) + " (" + rows.size() + ")");
		tab.setVisible(!rows.isEmpty());
	}

	private boolean anyTabVisible() {
		for (int i = 0; i < resultTabs.getComponentCount(); i++) {
			if (resultTabs.getTab(i).isVisible()) {
				return true;
			}
		}
		return false;
	}

	private void selectFirstVisibleTab() {
		if (resultTabs.getTab(resultTabs.getSelectedTab()).isVisible()) {
			return;
		}
		for (int i = 0; i < resultTabs.getComponentCount(); i++) {
			if (resultTabs.getTab(i).isVisible()) {
				resultTabs.setSelectedTab(i);
				return;
			}
		}
	}

	private void recenterWindow() {
		Window window = findAncestor(Window.class);
		if (window != null) {
			window.center();
		}
	}

	private void showStatus(ImportPreviewDto preview, ImportWizardModel.Step step) {
		if (!preview.getFileIssues().isEmpty()) {
			showStatusMessage(I18nProperties.getString(Strings.messageHierarchicalImportFileErrors), true);
			return;
		}
		if (step == ImportWizardModel.Step.DONE) {
			ImportReportDto report = model.report();
			long failed = report.count(BlockOutcome.FAILED) + report.count(BlockOutcome.CHANGED_SINCE_PREVIEW);
			showStatusMessage(
				String.format(
					I18nProperties.getString(Strings.messageHierarchicalImportReport),
					report.count(BlockOutcome.COMMITTED),
					report.count(BlockOutcome.UNCHANGED),
					report.count(BlockOutcome.FAILED),
					report.count(BlockOutcome.CHANGED_SINCE_PREVIEW)),
				failed > 0);
			return;
		}
		long errors = preview.countBlocks(BlockStatus.ERROR);
		readyCount.setValue(String.format(I18nProperties.getString(Strings.messageHierarchicalImportReadyCount), preview.countBlocks(BlockStatus.READY)) + NBSP);
		unchangedCount
			.setValue(String.format(I18nProperties.getString(Strings.messageHierarchicalImportUnchangedCount), preview.countBlocks(BlockStatus.UNCHANGED)) + NBSP);
		errorCount.setValue(String.format(I18nProperties.getString(Strings.messageHierarchicalImportErrorCount), errors));
		markCritical(errorCount, errors > 0);
		blockCounts.setVisible(true);
	}

	private void showStatusMessage(String message, boolean critical) {
		statusMessage.setValue(message);
		markCritical(statusMessage, critical);
		statusMessage.setVisible(true);
	}

	private static void markCritical(Label label, boolean critical) {
		if (critical) {
			label.addStyleName(CssStyles.LABEL_CRITICAL);
		} else {
			label.removeStyleName(CssStyles.LABEL_CRITICAL);
		}
	}

	private static String countsText(ImportPreviewDto preview) {
		return String.format(
			I18nProperties.getString(Strings.messageHierarchicalImportCounts),
			preview.countOperations("case", OperationType.CREATE),
			preview.countOperations("case", OperationType.UPDATE),
			preview.countOperations("sample", OperationType.CREATE),
			preview.countOperations("sample", OperationType.UPDATE),
			preview.countOperations("test", OperationType.CREATE),
			preview.countOperations("test", OperationType.UPDATE));
	}

	private static String recordType(String level) {
		switch (level) {
		case "case":
			return I18nProperties.getString(Strings.entityCase);
		case "sample":
			return I18nProperties.getString(Strings.entitySample);
		default:
			return I18nProperties.getCaption(Captions.PathogenTest);
		}
	}

	private InputStream csv(List<String[]> rows) {
		StringWriter out = new StringWriter();
		try (CSVWriter writer = CSVUtils.createCSVWriter(out, csvSeparator)) {
			rows.forEach(writer::writeNext);
		} catch (IOException e) {
			throw new IllegalStateException(e);
		}
		return new ByteArrayInputStream(out.toString().getBytes(StandardCharsets.UTF_8));
	}
}
