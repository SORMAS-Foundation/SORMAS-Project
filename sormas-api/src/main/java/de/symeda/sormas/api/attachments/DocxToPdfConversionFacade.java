package de.symeda.sormas.api.attachments;

import javax.ejb.Remote;

/**
 * Check if API can be enhanced.
 */
@Remote
public interface DocxToPdfConversionFacade {

	void convertToPdf(String sourceDocxFilePath, String targetPdfFilePath);
}
