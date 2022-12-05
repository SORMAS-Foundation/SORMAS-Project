package de.symeda.sormas.ui.contact;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.ValoTheme;
import com.vaadin.v7.ui.CheckBox;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.caze.CaseReferenceDto;
import de.symeda.sormas.api.contact.ContactDto;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.utils.fieldaccess.UiFieldAccessCheckers;
import de.symeda.sormas.api.utils.fieldvisibility.FieldVisibilityCheckers;
import de.symeda.sormas.ui.location.LocationEditForm;
import de.symeda.sormas.ui.utils.ButtonHelper;
import de.symeda.sormas.ui.utils.VaadinUiUtil;

public class AdoptAddressLayout extends HorizontalLayout {

	ContactDto contact;
	CaseReferenceDto caseReference;
	CheckBox adoptAddress;
	Button showAddressButton;

	public AdoptAddressLayout() {
		adoptAddress = new CheckBox(I18nProperties.getCaption(Captions.adoptHomeAddressOfCasePerson));
		showAddressButton = ButtonHelper.createIconButton(VaadinIcons.EYE);
		showAddressButton.addClickListener(e -> showAddressOfCasePerson(contact.getCaze()));
		showAddressButton.setStyleName(ValoTheme.BUTTON_BORDERLESS);
		addComponents(adoptAddress, showAddressButton);
		setComponentAlignment(adoptAddress, Alignment.MIDDLE_CENTER);
	}

	public AdoptAddressLayout(CaseReferenceDto caseReference) {
		this.caseReference = caseReference;
		adoptAddress = new CheckBox(I18nProperties.getCaption(Captions.adoptHomeAddressOfCasePersonIfRelationMatches));
		showAddressButton = ButtonHelper.createIconButton(VaadinIcons.EYE);
		showAddressButton.addClickListener(e -> showAddressOfCasePerson(this.caseReference));
		showAddressButton.setStyleName(ValoTheme.BUTTON_BORDERLESS);
		addComponents(adoptAddress, showAddressButton);
		setComponentAlignment(adoptAddress, Alignment.MIDDLE_CENTER);
	}

	private void showAddressOfCasePerson(CaseReferenceDto caseReference) {
		LocationEditForm locationForm = new LocationEditForm(
			FieldVisibilityCheckers.withCountry(FacadeProvider.getConfigFacade().getCountryLocale()),
			UiFieldAccessCheckers.getNoop());
		locationForm.setValue(
			FacadeProvider.getPersonFacade()
				.getByUuid(FacadeProvider.getCaseFacade().getByUuid(caseReference.getUuid()).getPerson().getUuid())
				.getAddress());
		locationForm.setEnabled(false);
		HorizontalLayout layout = new HorizontalLayout(locationForm);
		layout.setMargin(true);
		Window window = VaadinUiUtil.showPopupWindow(layout);
		window.setCaption(I18nProperties.getCaption(Captions.casePersonAddress));
	}

	public boolean isAdoptAddress() {
		return adoptAddress.getValue();
	}

	public void setAdoptAddress(boolean adoptAddress) {
		this.adoptAddress.setValue(adoptAddress);
	}

	public void setCaseReference(CaseReferenceDto caseReference) {
		this.caseReference = caseReference;
	}

	public void setContact(ContactDto contact) {
		this.contact = contact;
	}
}
