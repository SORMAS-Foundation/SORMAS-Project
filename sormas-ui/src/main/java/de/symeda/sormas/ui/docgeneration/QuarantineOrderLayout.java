package de.symeda.sormas.ui.docgeneration;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;

import com.vaadin.server.Page;
import com.vaadin.server.StreamResource;
import com.vaadin.server.StreamResource.StreamSource;
import com.vaadin.ui.Notification;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.ReferenceDto;
import de.symeda.sormas.api.docgeneneration.QuarantineOrderFacade;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;

public class QuarantineOrderLayout extends AbstractDocgenerationLayout {

	private final ReferenceDto caseReferenceDto;

	public QuarantineOrderLayout(ReferenceDto referenceDto) {
		super(I18nProperties.getCaption(Captions.DocumentTemplate_QuarantineOrder));
		this.caseReferenceDto = referenceDto;
	}

	@Override
	protected List<String> getAvailableTemplates() {
		return FacadeProvider.getQuarantineOrderFacade().getAvailableTemplates();
	}

	@Override
	protected String generateFilename(String templateFile) {
		String uuid = caseReferenceDto.getUuid();
		return uuid.substring(0, Math.min(5, uuid.length())) + "_" + templateFile;
	}

	@Override
	protected List<String> getAdditionalVariables(String templateFile) throws IOException {
		return FacadeProvider.getQuarantineOrderFacade().getAdditionalVariables(templateFile);
	}

	@Override
	protected StreamResource createStreamResource(String templateFile, String filename) {
		return new StreamResource((StreamSource) () -> {
			QuarantineOrderFacade quarantineOrderFacade = FacadeProvider.getQuarantineOrderFacade();
			try {
				return new ByteArrayInputStream(
					quarantineOrderFacade.getGeneratedDocument(templateFile, caseReferenceDto, readAdditionalVariables()));
			} catch (IOException | IllegalArgumentException e) {
				new Notification("Document generation failed", e.getMessage(), Notification.Type.ERROR_MESSAGE).show(Page.getCurrent());
				return null;
			}
		}, filename);
	}
}
