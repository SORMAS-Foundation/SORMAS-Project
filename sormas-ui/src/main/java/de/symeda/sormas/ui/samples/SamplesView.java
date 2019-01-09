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
import de.symeda.sormas.ui.CurrentUser;
import de.symeda.sormas.ui.utils.AbstractView;
import de.symeda.sormas.ui.utils.DownloadUtil;

@SuppressWarnings("serial")
public class SamplesView extends AbstractView {

	public static final String VIEW_NAME = "samples";	
	
	private final SampleGridComponent sampleListComponent;
	
	public SamplesView() {
    	super(VIEW_NAME);
    	
		sampleListComponent = new SampleGridComponent(getViewTitleLabel());
		setSizeFull();
		addComponent(sampleListComponent);
		
		if (CurrentUser.getCurrent().hasUserRight(UserRight.SAMPLE_EXPORT)) {
			Button exportButton = new Button("Export");
			exportButton.addStyleName(ValoTheme.BUTTON_PRIMARY);
			exportButton.setIcon(FontAwesome.DOWNLOAD);
			
			StreamResource streamResource = DownloadUtil.createGridExportStreamResource(sampleListComponent.getGrid().getContainerDataSource(), sampleListComponent.getGrid().getColumns(), "sormas_samples", "sormas_samples_" + DateHelper.formatDateForExport(new Date()) + ".csv", SampleGrid.EDIT_BTN_ID);
			FileDownloader fileDownloader = new FileDownloader(streamResource);
			fileDownloader.extend(exportButton);
			
			addHeaderComponent(exportButton);
		}
	}
	
	@Override
	public void enter(ViewChangeEvent event) {
		sampleListComponent.reload();
		sampleListComponent.updateActiveStatusButtonCaption();
	}

}
