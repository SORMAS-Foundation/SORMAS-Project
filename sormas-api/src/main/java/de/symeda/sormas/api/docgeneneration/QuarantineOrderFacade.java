package de.symeda.sormas.api.docgeneneration;

import java.io.IOException;
import java.util.List;
import java.util.Properties;

import javax.ejb.Remote;

import de.symeda.sormas.api.ReferenceDto;

@Remote
public interface QuarantineOrderFacade {

	byte[] getGeneratedDocument(String templateName, ReferenceDto rootEntityReference, Properties extraProperties) throws IOException;

	List<String> getAvailableTemplates();

	List<String> getAdditionalVariables(String templateName) throws IOException;
}
