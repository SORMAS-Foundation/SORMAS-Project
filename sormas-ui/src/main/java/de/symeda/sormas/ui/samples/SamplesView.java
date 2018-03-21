package de.symeda.sormas.ui.samples;

import java.util.Date;

import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.FileDownloader;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.StreamResource;
import com.vaadin.ui.Button;
import com.vaadin.ui.themes.ValoTheme;

import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.ui.login.LoginHelper;
import de.symeda.sormas.ui.utils.AbstractView;
import de.symeda.sormas.ui.utils.DownloadUtil;

@SuppressWarnings("serial")
public class SamplesView extends AbstractView {

	public static final String VIEW_NAME = "samples";	
	
	private final SampleListComponent sampleListComponent;
	
	public SamplesView() {
    	super(VIEW_NAME);
    	
		sampleListComponent = new SampleListComponent();
		setSizeFull();
		addComponent(sampleListComponent);
		
		if (LoginHelper.hasUserRight(UserRight.SAMPLE_EXPORT)) {
			Button exportButton = new Button("Export");
			exportButton.addStyleName(ValoTheme.BUTTON_PRIMARY);
			exportButton.setIcon(FontAwesome.DOWNLOAD);
			
			StreamResource streamResource = DownloadUtil.createGridExportStreamResource(sampleListComponent.getGrid(), "sormas_samples", "sormas_samples_" + DateHelper.formatDateForExport(new Date()) + ".csv", "text/csv", SampleGrid.EDIT_BTN_ID);
			FileDownloader fileDownloader = new FileDownloader(streamResource);
			fileDownloader.extend(exportButton);
			
			addHeaderComponent(exportButton);
		}
	}
	
	@Override
	public void enter(ViewChangeEvent event) {
		sampleListComponent.reload();
	}

}
