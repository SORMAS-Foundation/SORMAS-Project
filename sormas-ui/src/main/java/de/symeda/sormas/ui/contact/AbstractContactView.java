package de.symeda.sormas.ui.contact;

import com.vaadin.ui.Label;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.I18nProperties;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.contact.ContactDto;
import de.symeda.sormas.api.contact.ContactReferenceDto;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.ui.SubNavigationMenu;
import de.symeda.sormas.ui.caze.CaseContactsView;
import de.symeda.sormas.ui.utils.AbstractSubNavigationView;

@SuppressWarnings("serial")
public abstract class AbstractContactView extends AbstractSubNavigationView {

	private ContactReferenceDto contactRef;

	public static final String ROOT_VIEW_NAME = ContactsView.VIEW_NAME;
	
	protected AbstractContactView(String viewName) {
		super(viewName);
	}

	@Override
	public void refreshMenu(SubNavigationMenu menu, Label infoLabel, Label infoLabelSub, String params) {
		
		ContactDto contact = FacadeProvider.getContactFacade().getContactByUuid(params);
		contactRef = FacadeProvider.getContactFacade().getReferenceByUuid(contact.getUuid());
		
		menu.removeAllViews();
		menu.addView(ContactsView.VIEW_NAME, "Contacts list");
		menu.addView(CaseContactsView.VIEW_NAME, "Case contacts", contact.getCaze().getUuid(), true);
		menu.addView(ContactDataView.VIEW_NAME, I18nProperties.getFieldCaption(ContactDto.I18N_PREFIX), params);
		menu.addView(ContactPersonView.VIEW_NAME, I18nProperties.getPrefixFieldCaption(ContactDto.I18N_PREFIX, ContactDto.PERSON), params);
		menu.addView(ContactVisitsView.VIEW_NAME, I18nProperties.getPrefixFieldCaption(ContactDto.I18N_PREFIX, "visits"), params);
		
		infoLabel.setValue(contactRef.getCaption());
		CaseDataDto caseData = FacadeProvider.getCaseFacade().getCaseDataByUuid(contact.getCaze().getUuid());
		infoLabelSub.setValue(
				caseData.getDisease() != Disease.OTHER 
				? caseData.getDisease().toShortString()
				: DataHelper.toStringNullable(caseData.getDiseaseDetails()));
    }

	public ContactReferenceDto getContactRef() {
		return contactRef;
	}
}
