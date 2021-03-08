package de.symeda.sormas.ui.person;

import java.util.function.Consumer;

import com.vaadin.ui.Window;
import com.vaadin.v7.ui.Table;

import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.person.PersonContactDetailDto;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.api.utils.fieldaccess.UiFieldAccessCheckers;
import de.symeda.sormas.api.utils.fieldvisibility.FieldVisibilityCheckers;
import de.symeda.sormas.ui.caze.AbstractTableField;
import de.symeda.sormas.ui.utils.CommitDiscardWrapperComponent;
import de.symeda.sormas.ui.utils.VaadinUiUtil;

public class PersonContactDetailsField extends AbstractTableField<PersonContactDetailDto> {

	private static final String OWNER = Captions.PersonContactDetail_owner;
	private static final String OWNER_NAME = Captions.PersonContactDetail_ownerName;
	private static final String THIS_PERSON = "This person";

	private FieldVisibilityCheckers fieldVisibilityCheckers;
	private PersonDto thisPerson;

	public PersonContactDetailsField(
		PersonDto thisPerson,
		FieldVisibilityCheckers fieldVisibilityCheckers,
		UiFieldAccessCheckers fieldAccessCheckers) {
		super(fieldAccessCheckers);
		this.fieldVisibilityCheckers = fieldVisibilityCheckers;
		this.thisPerson = thisPerson;
	}

	@Override
	public Class<PersonContactDetailDto> getEntryType() {
		return PersonContactDetailDto.class;
	}

	@Override
	protected void editEntry(PersonContactDetailDto entry, boolean create, Consumer<PersonContactDetailDto> commitCallback) {

		if (create && entry.getUuid() == null) {
			entry.setUuid(DataHelper.createUuid());
		}

		PersonContactDetailEditForm editForm =
			new PersonContactDetailEditForm(fieldVisibilityCheckers, fieldAccessCheckers, getContainer().getItemIds(), (ok, existingDuplicatePCD) -> {
				if (ok) {
					existingDuplicatePCD.setPrimaryContact(false);
				} else {
					entry.setPrimaryContact(false);
				}
			});
		editForm.setValue(entry);

		final CommitDiscardWrapperComponent<PersonContactDetailEditForm> editView =
			new CommitDiscardWrapperComponent<>(editForm, true, editForm.getFieldGroup());
		editView.getCommitButton().setCaption(I18nProperties.getString(Strings.done));

		Window popupWindow = VaadinUiUtil.showModalPopupWindow(editView, I18nProperties.getCaption(PersonContactDetailDto.I18N_PREFIX));

		editView.addCommitListener(() -> {
			if (!editForm.getFieldGroup().isModified()) {
				commitCallback.accept(editForm.getValue());
			}
		});

		if (!isEmpty(entry)) {
			editView.addDeleteListener(() -> {
				popupWindow.close();
				PersonContactDetailsField.this.removeEntry(entry);
			}, I18nProperties.getCaption(PersonContactDetailDto.I18N_PREFIX));
		}
	}

	@SuppressWarnings("deprecation")
	@Override
	protected void updateColumns() {
		Table table = getTable();

		table.addGeneratedColumn(OWNER, (Table.ColumnGenerator) (source, itemId, columnId) -> {
			PersonContactDetailDto personContactDetailDto = (PersonContactDetailDto) itemId;
			return personContactDetailDto.isThirdParty() ? personContactDetailDto.getThirdPartyRole() : THIS_PERSON;
		});
		table.addGeneratedColumn(OWNER_NAME, (Table.ColumnGenerator) (source, itemId, columnId) -> {
			PersonContactDetailDto personContactDetailDto = (PersonContactDetailDto) itemId;
			return personContactDetailDto.isThirdParty() ? personContactDetailDto.getThirdPartyName() : thisPerson.toReference().getCaption();
		});

		table.setVisibleColumns(
			EDIT_COLUMN_ID,
			PersonContactDetailDto.PRIMARY,
			OWNER,
			OWNER_NAME,
			PersonContactDetailDto.PERSON_CONTACT_DETAILS_TYPE,
			PersonContactDetailDto.CONTACT_INFORMATION);

		table.setColumnExpandRatio(EDIT_COLUMN_ID, 0);
		table.setColumnExpandRatio(PersonContactDetailDto.PRIMARY, 0);
		table.setColumnExpandRatio(OWNER, 0);
		table.setColumnExpandRatio(OWNER_NAME, 0);
		table.setColumnExpandRatio(PersonContactDetailDto.PERSON_CONTACT_DETAILS_TYPE, 0);
		table.setColumnExpandRatio(PersonContactDetailDto.CONTACT_INFORMATION, 0);

		for (Object columnId : table.getVisibleColumns()) {
			if (columnId.equals(EDIT_COLUMN_ID)) {
				table.setColumnHeader(columnId, "&nbsp");
			} else {
				table.setColumnHeader(columnId, I18nProperties.getPrefixCaption(PersonContactDetailDto.I18N_PREFIX, (String) columnId));
			}
		}
	}

	@Override
	protected boolean isEmpty(PersonContactDetailDto entry) {
		return false;
	}

	@Override
	protected boolean isModified(PersonContactDetailDto oldEntry, PersonContactDetailDto newEntry) {
		if (isModifiedObject(oldEntry.getPerson(), newEntry.getPerson()))
			return true;
		if (isModifiedObject(oldEntry.getPersonContactDetailType(), newEntry.getPersonContactDetailType()))
			return true;
		if (isModifiedObject(oldEntry.getPhoneNumberType(), newEntry.getPhoneNumberType()))
			return true;
		if (isModifiedObject(oldEntry.getContactInformation(), newEntry.getContactInformation()))
			return true;
		if (isModifiedObject(oldEntry.getAdditionalInformation(), newEntry.getAdditionalInformation()))
			return true;
		if (isModifiedObject(oldEntry.isThirdParty(), newEntry.isThirdParty()))
			return true;
		if (isModifiedObject(oldEntry.getThirdPartyName(), newEntry.getThirdPartyName()))
			return true;
		if (isModifiedObject(oldEntry.getThirdPartyRole(), newEntry.getThirdPartyRole()))
			return true;

		return false;
	}

	public void setThisPerson(PersonDto thisPerson) {
		this.thisPerson = thisPerson;
	}
}
