package de.symeda.sormas.api.docgeneneration;

import java.io.IOException;
import java.util.Properties;

import javax.ejb.Remote;

import de.symeda.sormas.api.ReferenceDto;
import de.symeda.sormas.api.sample.PathogenTestReferenceDto;
import de.symeda.sormas.api.sample.SampleReferenceDto;
import de.symeda.sormas.api.user.UserReferenceDto;

@Remote
public interface QuarantineOrderFacade extends WorkflowDocumentFacade {

	byte[] getGeneratedDocument(
		String templateName,
		ReferenceDto rootEntityReference,
		UserReferenceDto userReference,
		SampleReferenceDto sampleReference,
		PathogenTestReferenceDto pathogenTest,
		Properties extraProperties)
		throws IOException;
}
