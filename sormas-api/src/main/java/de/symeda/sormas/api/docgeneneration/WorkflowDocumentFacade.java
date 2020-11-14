package de.symeda.sormas.api.docgeneneration;

import java.io.IOException;
import java.util.List;

public interface WorkflowDocumentFacade {

	List<String> getAvailableTemplates();

	List<String> getAdditionalVariables(String templateName) throws IOException;
}
