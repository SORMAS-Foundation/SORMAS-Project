package de.symeda.sormas.ui.docgeneration;

import de.symeda.sormas.api.event.EventReferenceDto;

public class EventDocumentsComponent extends AbstractDocumentGenerationComponent {

	public static final String DOCGENERATION_LOC = "docgeneration";

	public EventDocumentsComponent(EventReferenceDto eventReferenceDto) {
		super();
		// TODO: I18N
		addDocumentBar(() -> new EventDocumentLayout(eventReferenceDto), "Ereignis-Formular erstellen");
	}

	@Override
	protected String getComponentLabel() {
		// TODO: I18N
		return "Dokumente";
	}
}
