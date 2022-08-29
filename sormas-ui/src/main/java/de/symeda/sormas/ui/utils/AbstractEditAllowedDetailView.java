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


	protected AbstractEditAllowedDetailView(String viewName) {
		super(viewName);

	}

	protected abstract CoreFacade getCoreFacade();

	protected boolean isEditAllowed() {
		String uuid = getReference().getUuid();
		return getCoreFacade().isEditAllowed(uuid);
	}

	protected void setEditPermission(Component component){
		if (!isEditAllowed()) {
			component.setEnabled(false);
		}
	}
}
