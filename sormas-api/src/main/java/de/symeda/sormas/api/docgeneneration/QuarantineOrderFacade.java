package de.symeda.sormas.api.docgeneneration;

import java.io.ByteArrayInputStream;
import java.util.Properties;

import javax.ejb.Remote;

@Remote
public interface QuarantineOrderFacade {

	// Serializable?
	ByteArrayInputStream getGeneratedDocument(String templateName, String caseUuid, Properties extraProperties);
}
