package de.symeda.sormas.api.docgeneneration;

import com.sun.org.apache.xalan.internal.xsltc.compiler.Template;
import de.symeda.sormas.api.region.DistrictCriteria;

import java.util.List;
import java.util.Properties;

import javax.ejb.Remote;

@Remote
public interface QuarantineOrderFacade {

	byte[] getGeneratedDocument(String templateName, String caseUuid, Properties extraProperties);

	List<String> getAvailableTemplates();

	void writeQuarantineTemplate(String fileName, byte[] document);

	// new Functions
	List<TemplateDto> getAvailableTemplateDtos();
	long count(TemplateCriteria criteria);
}
