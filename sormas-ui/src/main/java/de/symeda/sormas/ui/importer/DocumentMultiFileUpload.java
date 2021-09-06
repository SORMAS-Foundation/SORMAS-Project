/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2021 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package de.symeda.sormas.ui.importer;

import com.wcs.wcslib.vaadin.widget.multifileupload.ui.MultiFileUpload;
import com.wcs.wcslib.vaadin.widget.multifileupload.ui.UploadFinishedHandler;
import com.wcs.wcslib.vaadin.widget.multifileupload.ui.UploadStartedHandler;
import com.wcs.wcslib.vaadin.widget.multifileupload.ui.UploadStatePanel;
import com.wcs.wcslib.vaadin.widget.multifileupload.ui.UploadStateWindow;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Validations;
import de.symeda.sormas.ui.SormasUI;

public class DocumentMultiFileUpload extends MultiFileUpload {

	public DocumentMultiFileUpload(UploadStartedHandler uploadStartedHandler, UploadFinishedHandler uploadFinishedHandler, UploadStateWindow uploadStateWindow, boolean multiple) {
		super(uploadStartedHandler, uploadFinishedHandler, uploadStateWindow, multiple);

		long fileSizeLimitMb = FacadeProvider.getConfigFacade().getDocumentUploadSizeLimitMb();
		setMaxFileSize(fileSizeLimitMb * 1_000_000);
		setSizeErrorMsgPattern(I18nProperties.getValidationError(Validations.fileTooBig, fileSizeLimitMb));

		// Need to enable Polling or nothing will happen after selecting a file with the MultiFileUpload input
		this.addAttachListener(e -> SormasUI.get().access(() -> SormasUI.get().setPollInterval(300)));
		this.addDetachListener(e -> SormasUI.get().access(() -> SormasUI.get().setPollInterval(-1)));
	}

	@Override
	protected UploadStatePanel createStatePanel(UploadStateWindow uploadStateWindow) {
		return new UploadStatePanel(uploadStateWindow, new DocumentUploadReceiver());
	}
}
