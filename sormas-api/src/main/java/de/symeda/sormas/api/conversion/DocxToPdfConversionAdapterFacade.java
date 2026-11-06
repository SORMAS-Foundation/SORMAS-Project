package de.symeda.sormas.api.conversion;

import javax.ejb.Remote;

/**
 * Meant to use some internal tool instead of SORMAS default conversion tool.
 * <p>
 * Meant to be implemented by SORMAS adapters.
 */
@Remote
public interface DocxToPdfConversionAdapterFacade {

	void convertToPdf(String sourceDocxFilePath, String targetPdfFilePath);
}
