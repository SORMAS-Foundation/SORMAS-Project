package de.symeda.sormas.api.docgeneneration;

import java.util.List;
import java.util.Properties;

import javax.ejb.Remote;

import de.symeda.sormas.api.ReferenceDto;
import de.symeda.sormas.api.utils.ValidationException;

@Remote
public interface QuarantineOrderFacade {

	byte[] getGeneratedDocument(String templateName, ReferenceDto rootEntityReference, Properties extraProperties) throws ValidationException;

	byte[] getTemplate(String templateName) throws ValidationException;

	List<String> getAvailableTemplates();

	boolean isExistingTemplate(String templateName);

	List<String> getAdditionalVariables(String templateName) throws ValidationException;

	void writeQuarantineTemplate(String fileName, byte[] document) throws ValidationException;

	boolean deleteQuarantineTemplate(String fileName) throws ValidationException;
}
