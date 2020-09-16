package de.symeda.sormas.api.docgeneneration;

import java.util.Properties;

import javax.ejb.Remote;

@Remote
public interface QuarantineOrderFacade {

	// Serializable?
	byte[] getGeneratedDocument(String templateName, String caseUuid, Properties extraProperties);
}
