package de.symeda.sormas.ui.docgeneration;

import de.symeda.sormas.api.event.EventDto;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.document.DocumentListComponent;

public class EventDocumentsComponent extends AbstractDocumentGenerationComponent {

	public static final String DOCGENERATION_LOC = "docgeneration";

	public EventDocumentsComponent(EventDto event, DocumentListComponent documentListComponent) {
		super();
		if (DocGenerationHelper.isDocGenerationAllowed()) {
			addDocumentBar(
				() -> ControllerProvider.getDocGenerationController().showEventDocumentDialog(event, documentListComponent),
				I18nProperties.getCaption(Captions.DocumentTemplate_EventHandout_create));
		}
	}
}
