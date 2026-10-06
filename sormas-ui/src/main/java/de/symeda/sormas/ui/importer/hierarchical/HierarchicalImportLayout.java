package de.symeda.sormas.ui.importer.hierarchical;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
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
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Grid;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.ProgressBar;
import com.vaadin.ui.UI;
import com.vaadin.ui.Upload;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.importexport.ValueSeparator;
import de.symeda.sormas.api.importexport.hierarchical.BlockOutcome;
import de.symeda.sormas.api.importexport.hierarchical.BlockStatus;
import de.symeda.sormas.api.importexport.hierarchical.FieldChangeDto;
import de.symeda.sormas.api.importexport.hierarchical.HierarchicalImportFacade;
import de.symeda.sormas.api.importexport.hierarchical.ImportIssueDto;
import de.symeda.sormas.api.importexport.hierarchical.ImportPreviewDto;
import de.symeda.sormas.api.importexport.hierarchical.ImportReportDto;
import de.symeda.sormas.api.importexport.hierarchical.ImportSheet;
import de.symeda.sormas.api.importexport.hierarchical.ImportTemplateDto;
import de.symeda.sormas.api.importexport.hierarchical.OperationType;
import de.symeda.sormas.api.importexport.hierarchical.RecordOperationDto;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.api.utils.CSVUtils;
import de.symeda.sormas.ui.UiUtil;
import de.symeda.sormas.ui.utils.ButtonHelper;

public class HierarchicalImportLayout extends VerticalLayout {

	private static final long serialVersionUID = 1L;
	private static final int COMMIT_CHUNK_SIZE = 20;
	private static final Logger LOGGER = LoggerFactory.getLogger(HierarchicalImportLayout.class);

	private final HierarchicalImportFacade facade = FacadeProvider.getHierarchicalImportFacade();
	private final ImportWizardModel model = new ImportWizardModel();
	private final String definitionId;
	private final ImportTemplateDto template;
	private final UI ui = UI.getCurrent();
	private final UserDto currentUser = UiUtil.getUser();

	private final ComboBox<ValueSeparator> separator = new ComboBox<>(I18nProperties.getCaption(Captions.importValueSeparator));
	private final Label summary = new Label("", ContentMode.TEXT);
	private final Grid<ImportIssueDto> issueGrid = new Grid<>();
	private final Grid<ChangeRow> changeGrid = new Grid<>();
	private final ProgressBar progress = new ProgressBar(0f);
	private final Button importButton = ButtonHelper.createButton(Captions.actionImport, e -> startCommit(), ValoTheme.BUTTON_PRIMARY);
	private final Button cancelButton = ButtonHelper.createButton(Captions.actionCancel, e -> model.requestCancel());
	private final Button errorFileButton = ButtonHelper.createButton(Captions.hierarchicalImportDownloadErrors, (Button.ClickListener) null);
	private final FileDownloader errorFileDownloader = new FileDownloader(new StreamResource(() -> null, "errors.csv"));

	public HierarchicalImportLayout(String definitionId) {
		this.definitionId = definitionId;
		this.template = facade.getTemplate(definitionId);

		separator.setItems(Arrays.asList(ValueSeparator.values()));
		separator.setValue(ValueSeparator.DEFAULT);
		separator.setEmptySelectionAllowed(false);

		issueGrid.addColumn(ImportIssueDto::getLineNumber).setCaption(I18nProperties.getCaption(Captions.hierarchicalImportLine));
		issueGrid.addColumn(ImportIssueDto::getColumn).setCaption(I18nProperties.getCaption(Captions.hierarchicalImportColumn));
		issueGrid.addColumn(ImportIssueDto::getMessage).setCaption(I18nProperties.getCaption(Captions.hierarchicalImportProblem));
		issueGrid.setWidth(100, Unit.PERCENTAGE);
		issueGrid.setHeightByRows(6);

		changeGrid.addColumn(row -> row.record).setCaption(I18nProperties.getCaption(Captions.hierarchicalImportRecord));
		changeGrid.addColumn(row -> row.field).setCaption(I18nProperties.getCaption(Captions.hierarchicalImportField));
		changeGrid.addColumn(row -> row.change).setCaption(I18nProperties.getCaption(Captions.hierarchicalImportChange));
		changeGrid.setWidth(100, Unit.PERCENTAGE);
		changeGrid.setHeightByRows(6);

		errorFileDownloader.extend(errorFileButton);

		Upload upload = new Upload(I18nProperties.getCaption(Captions.hierarchicalImportUpload), null);
		SheetUploadReceiver receiver = new SheetUploadReceiver(this::onFileUploaded);
		upload.setReceiver(receiver);
		upload.addSucceededListener(receiver);

		addComponents(
			templateButton(),
			separator,
			upload,
			summary,
			issueGrid,
			changeGrid,
			progress,
			new HorizontalLayout(errorFileButton, cancelButton, importButton));
		setWidth(900, Unit.PIXELS);
		refresh();
	}

	private Button templateButton() {
		Button button = ButtonHelper.createButton(Captions.hierarchicalImportDownloadTemplate, (Button.ClickListener) null);
		new FileDownloader(new StreamResource(() -> csv(List.of(
			template.getHeaders().toArray(new String[0]),
			template.getCaptions().toArray(new String[0]))), "sormas_import_cases_samples_tests.csv")).extend(button);
		return button;
	}

	private void onFileUploaded(File file, String fileName) {
		try (InputStream input = Files.newInputStream(file.toPath())) {
			ImportSheet sheet = SheetReader.read(input, fileName, csvSeparator(), template.captionsByHeader());
			model.previewed(sheet, facade.dryRun(definitionId, sheet));
		} catch (IOException e) {
			Notification.show(
				I18nProperties.getString(Strings.headingImportFailed),
				I18nProperties.getString(Strings.messageImportFailed),
				Notification.Type.ERROR_MESSAGE);
		} finally {
			file.delete();
		}
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
					chunk -> facade.commit(definitionId, model.sheet(), new HashMap<>(chunk)),
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
		importButton.setVisible(model.step() == ImportWizardModel.Step.PREVIEWED);
		importButton.setEnabled(model.canCommit());
		if (previewed) {
			importButton.setCaption(
				I18nProperties.getCaption(Captions.actionImport) + " (" + preview.countBlocks(BlockStatus.READY) + ")");
		}
		cancelButton.setVisible(model.step() == ImportWizardModel.Step.COMMITTING);
		progress.setVisible(model.step() == ImportWizardModel.Step.COMMITTING || model.step() == ImportWizardModel.Step.DONE);
		issueGrid.setVisible(previewed);
		changeGrid.setVisible(previewed);
		errorFileButton.setVisible(previewed && (!model.failedBlocks().isEmpty() || !model.looseIssues().isEmpty()));
		if (!previewed) {
			summary.setValue("");
			return;
		}
		summary.setValue(summaryText(preview));
		issueGrid.setItems(preview.allIssues());
		changeGrid.setItems(changeRows(preview));
		errorFileDownloader.setFileDownloadResource(
			new StreamResource(
				() -> csv(ErrorFile.rows(model.sheet(), model.failedBlocks(), model.looseIssues())),
				"sormas_import_errors.csv"));
	}

	private String summaryText(ImportPreviewDto preview) {
		if (!preview.getFileIssues().isEmpty()) {
			return I18nProperties.getString(Strings.messageHierarchicalImportFileErrors);
		}
		if (model.step() == ImportWizardModel.Step.DONE) {
			ImportReportDto report = model.report();
			return String.format(
				I18nProperties.getString(Strings.messageHierarchicalImportReport),
				report.count(BlockOutcome.COMMITTED),
				report.count(BlockOutcome.UNCHANGED),
				report.count(BlockOutcome.FAILED),
				report.count(BlockOutcome.CHANGED_SINCE_PREVIEW));
		}
		return String.format(
			I18nProperties.getString(Strings.messageHierarchicalImportSummary),
			preview.countBlocks(BlockStatus.READY),
			preview.countBlocks(BlockStatus.UNCHANGED),
			preview.countBlocks(BlockStatus.ERROR)) + "\n"
			+ String.format(
				I18nProperties.getString(Strings.messageHierarchicalImportCounts),
				preview.countOperations("case", OperationType.CREATE),
				preview.countOperations("case", OperationType.UPDATE),
				preview.countOperations("sample", OperationType.CREATE),
				preview.countOperations("sample", OperationType.UPDATE),
				preview.countOperations("test", OperationType.CREATE),
				preview.countOperations("test", OperationType.UPDATE));
	}

	private static List<ChangeRow> changeRows(ImportPreviewDto preview) {
		List<ChangeRow> rows = new ArrayList<>();
		preview.getBlocks().stream().filter(block -> block.getStatus() == BlockStatus.READY).forEach(block -> {
			for (RecordOperationDto operation : block.getOperations()) {
				for (FieldChangeDto change : operation.getChanges()) {
					rows.add(new ChangeRow(operation.getLabel(), change.getField(), change.getOldValue() + " → " + change.getNewValue()));
				}
			}
		});
		return rows;
	}

	private char csvSeparator() {
		return ValueSeparator.getSeparator(separator.getValue(), FacadeProvider.getConfigFacade().getCsvSeparator());
	}

	private InputStream csv(List<String[]> rows) {
		StringWriter out = new StringWriter();
		try (CSVWriter writer = CSVUtils.createCSVWriter(out, csvSeparator())) {
			rows.forEach(writer::writeNext);
		} catch (IOException e) {
			throw new IllegalStateException(e);
		}
		return new ByteArrayInputStream(out.toString().getBytes(StandardCharsets.UTF_8));
	}

	private static final class ChangeRow {

		private final String record;
		private final String field;
		private final String change;

		private ChangeRow(String record, String field, String change) {
			this.record = record;
			this.field = field;
			this.change = change;
		}
	}
}
