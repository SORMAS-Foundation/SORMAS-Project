package de.symeda.sormas.ui.travelentry;

import java.util.Collections;
import java.util.List;

import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.ui.Component;

import de.symeda.sormas.api.EditPermissionType;
import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.person.PersonReferenceDto;
import de.symeda.sormas.api.travelentry.TravelEntryCriteria;
import de.symeda.sormas.api.travelentry.TravelEntryDto;
import de.symeda.sormas.api.travelentry.TravelEntryIndexDto;
import de.symeda.sormas.api.travelentry.TravelEntryReferenceDto;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.SubMenu;
import de.symeda.sormas.ui.utils.AbstractDetailView;
import de.symeda.sormas.ui.utils.DirtyStateComponent;

public abstract class AbstractTravelEntryView extends AbstractDetailView<TravelEntryReferenceDto> {

	public static final String ROOT_VIEW_NAME = TravelEntriesView.VIEW_NAME;

	protected AbstractTravelEntryView(String viewName) {
		super(viewName);
	}

	@Override
	public void enter(ViewChangeListener.ViewChangeEvent event) {

		super.enter(event);
		initOrRedirect(event);
	}

	@Override
	protected String getRootViewName() {
		return ROOT_VIEW_NAME;
	}

	@Override
	public void refreshMenu(SubMenu menu, String params) {
		if (!findReferenceByParams(params)) {
			return;
		}

		menu.removeAllViews();
		menu.addView(TravelEntriesView.VIEW_NAME, I18nProperties.getCaption(Captions.travelEntryTravelEntriesList));
		menu.addView(TravelEntryDataView.VIEW_NAME, I18nProperties.getCaption(TravelEntryDto.I18N_PREFIX), params);
		menu.addView(TravelEntryPersonView.VIEW_NAME, I18nProperties.getPrefixCaption(TravelEntryDto.I18N_PREFIX, TravelEntryDto.PERSON), params);

		setMainHeaderComponent(ControllerProvider.getTravelEntryController().getTravelEntryViewTitleLayout(getReference().getUuid()));
	}

	public TravelEntryReferenceDto getTravelEntryRef() {
		return getReference();
	}

	public void setTravelEntryEditPermission(Component component) {
		boolean isTravelEntryEditAllowed = isTravelEntryEditAllowed();

		if (!isTravelEntryEditAllowed) {
			component.setEnabled(false);
		}
	}

	protected Boolean isTravelEntryEditAllowed() {
		return FacadeProvider.getTravelEntryFacade().isTravelEntryEditAllowed(getReference().getUuid()).equals(EditPermissionType.ALLOWED);
	}

	@Override
	protected void setSubComponent(DirtyStateComponent newComponent) {
		super.setSubComponent(newComponent);

		if (FacadeProvider.getTravelEntryFacade().isDeleted(getReference().getUuid())) {
			newComponent.setEnabled(false);
		}
	}

	@Override
	protected TravelEntryReferenceDto getReferenceByUuid(String uuid) {

		final TravelEntryReferenceDto reference;
		if (FacadeProvider.getTravelEntryFacade().exists(uuid)) {
			reference = FacadeProvider.getTravelEntryFacade().getReferenceByUuid(uuid);
		} else if (FacadeProvider.getPersonFacade().isValidPersonUuid(uuid)) {
			PersonReferenceDto person = FacadeProvider.getPersonFacade().getReferenceByUuid(uuid);
			TravelEntryCriteria criteria = new TravelEntryCriteria();
			criteria.setPerson(person);
			List<TravelEntryIndexDto> personTravelEntries =
				FacadeProvider.getTravelEntryFacade().getIndexList(criteria, null, null, Collections.emptyList());
			if (personTravelEntries != null) {
				reference = FacadeProvider.getTravelEntryFacade().getReferenceByUuid(personTravelEntries.get(0).getUuid());
			} else {
				reference = null;
			}
		} else {
			reference = null;
		}
		return reference;
	}
}
