package de.symeda.sormas.ui.contact;

import java.util.List;

import com.vaadin.v7.data.util.BeanItemContainer;
import com.vaadin.v7.data.util.GeneratedPropertyContainer;
import com.vaadin.v7.shared.ui.grid.HeightMode;
import com.vaadin.v7.ui.Grid;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.contact.ContactDto;
import de.symeda.sormas.api.contact.ContactIndexDto;
import de.symeda.sormas.api.contact.ContactSimilarityCriteria;
import de.symeda.sormas.api.contact.SimilarContactDto;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.person.SimilarPersonDto;

public class ContactSelectionGrid extends Grid {

	private static final long serialVersionUID = 1766319542062167849L;

	public ContactSelectionGrid(ContactSimilarityCriteria criteria) {
		buildGrid();
		loadData(criteria);
	}

	public ContactSelectionGrid(List<SimilarContactDto> contacts) {
		buildGrid();
		setContainerData(contacts);
	}

	private void buildGrid() {
		setSizeFull();
		setSelectionMode(SelectionMode.SINGLE);
		setHeightMode(HeightMode.ROW);

		BeanItemContainer<SimilarContactDto> container = new BeanItemContainer<>(SimilarContactDto.class);
		GeneratedPropertyContainer generatedContainer = new GeneratedPropertyContainer(container);
		setContainerDataSource(generatedContainer);

		setColumns(
			SimilarContactDto.FIRST_NAME,
			SimilarContactDto.LAST_NAME,
			SimilarContactDto.UUID,
			SimilarContactDto.CAZE,
			SimilarContactDto.CASE_ID_EXTERNAL_SYSTEM,
			SimilarContactDto.LAST_CONTACT_DATE,
			SimilarContactDto.CONTACT_PROXIMITY,
			SimilarContactDto.CONTACT_CLASSIFICATION,
			SimilarContactDto.CONTACT_STATUS,
			SimilarContactDto.FOLLOW_UP_STATUS);

		for (Column column : getColumns()) {
			column.setHeaderCaption(
				I18nProperties.findPrefixCaption(
					column.getPropertyId().toString(),
					SimilarPersonDto.I18N_PREFIX,
					ContactIndexDto.I18N_PREFIX,
					ContactDto.I18N_PREFIX));
		}

		getColumn(SimilarContactDto.FIRST_NAME).setMinimumWidth(150);
		getColumn(SimilarContactDto.LAST_NAME).setMinimumWidth(150);
	}

	private void loadData(ContactSimilarityCriteria criteria) {
		final List<SimilarContactDto> similarContacts = FacadeProvider.getContactFacade().getMatchingContacts(criteria);
		setContainerData(similarContacts);
	}

	private void setContainerData(List<SimilarContactDto> similarContacts) {
		getContainer().removeAllItems();
		getContainer().addAll(similarContacts);
		setHeightByRows(similarContacts.size() > 0 ? (similarContacts.size() <= 10 ? similarContacts.size() : 10) : 1);
	}

	@SuppressWarnings("unchecked")
	private BeanItemContainer<SimilarContactDto> getContainer() {
		GeneratedPropertyContainer container = (GeneratedPropertyContainer) super.getContainerDataSource();
		return (BeanItemContainer<SimilarContactDto>) container.getWrappedContainer();
	}
}
