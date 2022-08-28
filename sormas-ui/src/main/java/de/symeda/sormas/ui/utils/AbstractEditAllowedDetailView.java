package de.symeda.sormas.ui.utils;

import com.vaadin.ui.Component;
import de.symeda.sormas.api.CoreFacade;
import de.symeda.sormas.api.ReferenceDto;

/**
 * A detail view shows specific details of an object identified by the URL parameter.
 *
 * In addition to that, it contains generic code to check if the object is editable.
 *
 * @param <R>
 *            {@link ReferenceDto} with the uuid as parsed from the URL.
 */
public abstract class AbstractEditAllowedDetailView<R extends ReferenceDto> extends AbstractDetailView<R> {

	private CoreFacade coreFacade;

	protected AbstractEditAllowedDetailView(String viewName, CoreFacade coreFacade) {
		super(viewName);
		this.coreFacade = coreFacade;
	}

	protected boolean isEditAllowed() {
		String uuid = getReference().getUuid();
		return coreFacade.isEditAllowed(uuid);
	}

	protected void setEditPermission(Component component){
		if (!isEditAllowed()) {
			component.setEnabled(false);
		}
	}
}
