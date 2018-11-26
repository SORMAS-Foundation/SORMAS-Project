package de.symeda.sormas.ui.importer;

import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.ProgressBar;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

import de.symeda.sormas.api.I18nProperties;
import de.symeda.sormas.ui.utils.CssStyles;

@SuppressWarnings("serial")
public class CaseImportProgressLayout extends VerticalLayout {

	// Components
	private ProgressBar progressBar;
	private Label processedCasesLabel;
	private Label importedCasesLabel;
	private Label importErrorsLabel;
	private Label importSkipsLabel;
	
	// Counts
	private int processedCasesCount;
	private int importedCasesCount;
	private int importErrorsCount;
	private int importSkipsCount;
	private int totalCasesCount;
	
	public CaseImportProgressLayout(int totalCasesCount, Runnable cancelCallback) {
		this.totalCasesCount = totalCasesCount;
		
		setWidth(100, Unit.PERCENTAGE);
		setMargin(true);

		// Info text
		Label infoLabel = new Label(String.format(I18nProperties.getText("importInfo"), totalCasesCount), ContentMode.HTML);
		infoLabel.setContentMode(ContentMode.HTML);
		addComponent(infoLabel);
		
		// Progress bar
		progressBar = new ProgressBar(0.0f);
		CssStyles.style(progressBar, CssStyles.VSPACE_TOP_3);
		addComponent(progressBar);
		progressBar.setWidth(100, Unit.PERCENTAGE);
		
		// Progress info
		HorizontalLayout progressInfoLayout = new HorizontalLayout();
		CssStyles.style(progressInfoLayout, CssStyles.VSPACE_TOP_5);
		progressInfoLayout.setSpacing(true);
		processedCasesLabel = new Label(String.format(I18nProperties.getText("importsProcessed"), 0, totalCasesCount));
		progressInfoLayout.addComponent(processedCasesLabel);
		importedCasesLabel = new Label(String.format(I18nProperties.getText("importImports"), 0));
		CssStyles.style(importedCasesLabel, CssStyles.LABEL_POSITIVE);
		progressInfoLayout.addComponent(importedCasesLabel);
		importErrorsLabel = new Label(String.format(I18nProperties.getText("importErrors"), 0));
		CssStyles.style(importErrorsLabel, CssStyles.LABEL_CRITICAL);
		progressInfoLayout.addComponent(importErrorsLabel);
		importSkipsLabel = new Label(String.format(I18nProperties.getText("importSkips"), 0));
		CssStyles.style(importSkipsLabel, CssStyles.LABEL_MINOR);
		progressInfoLayout.addComponent(importSkipsLabel);
		addComponent(progressInfoLayout);
		setComponentAlignment(progressInfoLayout, Alignment.TOP_RIGHT);
		
		// Cancel button
		Button cancelButton = new Button("Cancel");
		CssStyles.style(cancelButton, CssStyles.VSPACE_TOP_2);
		cancelButton.addClickListener(e -> {
			cancelCallback.run();
		});
		addComponent(cancelButton);
		setComponentAlignment(cancelButton, Alignment.MIDDLE_RIGHT);
	}
	
	public void updateProgress(CaseImportResult result) {		
		UI.getCurrent().access(new Runnable() {
			@Override
			public void run() {
				processedCasesCount++;
				if (result == CaseImportResult.SUCCESS) {
					importedCasesCount++;
					importedCasesLabel.setValue(String.format(I18nProperties.getText("importImports"), importedCasesCount));
				} else if (result == CaseImportResult.ERROR) {
					importErrorsCount++;
					importErrorsLabel.setValue(String.format(I18nProperties.getText("importErrors"), importErrorsCount));
				} else {
					importSkipsCount++;
					importSkipsLabel.setValue(String.format(I18nProperties.getText("importSkips"), importSkipsCount));
				}
				processedCasesLabel.setValue(String.format(I18nProperties.getText("importsProcessed"), processedCasesCount, totalCasesCount));
				progressBar.setValue((float) processedCasesCount / (float) totalCasesCount);
			}
		});
	}
	
}
