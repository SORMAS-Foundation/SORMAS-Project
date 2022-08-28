package de.symeda.sormas.ui.utils;

import de.symeda.sormas.api.CoreFacade;
import de.symeda.sormas.api.ReferenceDto;

public abstract class AbstractEditAllowedDetailView<R extends ReferenceDto> extends AbstractDetailView<R> {

	private R reference;
	private CoreFacade coreFacade;

	protected AbstractEditAllowedDetailView(String viewName, CoreFacade coreFacade) {
		super(viewName);
		this.coreFacade = coreFacade;
	}

	protected boolean isEditAllowed() {
		String uuid = getReference().getUuid();
		return coreFacade.isEditAllowed(uuid);
	}
}
