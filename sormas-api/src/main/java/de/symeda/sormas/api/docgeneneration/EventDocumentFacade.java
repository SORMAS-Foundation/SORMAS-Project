package de.symeda.sormas.api.docgeneneration;

import java.io.IOException;
import java.util.Properties;

import javax.ejb.Remote;

import de.symeda.sormas.api.event.EventReferenceDto;

@Remote
public interface EventDocumentFacade extends WorkflowDocumentFacade {

	String getGeneratedDocument(String templateName, EventReferenceDto eventReference, Properties extraProperties) throws IOException;
}
