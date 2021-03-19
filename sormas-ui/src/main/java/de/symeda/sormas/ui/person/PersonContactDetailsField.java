package de.symeda.sormas.ui.person;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Predicate;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.ui.Label;
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
import de.symeda.sormas.ui.utils.ConfirmationComponent;
import de.symeda.sormas.ui.utils.FieldAccessCellStyleGenerator;
import de.symeda.sormas.ui.utils.VaadinUiUtil;

public class PersonContactDetailsField extends AbstractTableField<PersonContactDetailDto> {

	private static final String COLUMN_PRIMARY = PersonContactDetailDto.PRIMARY_CONTACT;
	private static final String COLUMN_OWNER = Captions.personContactDetailOwner;
	private static final String COLUMN_OWNER_NAME = Captions.personContactDetailOwnerName;
	private static final String THIS_PERSON = Captions.personContactDetailThisPerson;

	private FieldVisibilityCheckers fieldVisibilityCheckers;
	private PersonDto thisPerson;
	private boolean isPseudonymized;

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

		PersonContactDetailEditForm editForm = new PersonContactDetailEditForm(fieldVisibilityCheckers, fieldAccessCheckers);
		editForm.setValue(entry);

		final CommitDiscardWrapperComponent<PersonContactDetailEditForm> editView =
			new CommitDiscardWrapperComponent<>(editForm, true, editForm.getFieldGroup());
		editView.getCommitButton().setCaption(I18nProperties.getString(Strings.done));

		Window popupWindow = VaadinUiUtil.showModalPopupWindow(editView, I18nProperties.getString(Strings.entityPersonContactDetail));

		editView.addCommitListener(() -> {
			if (!editForm.getFieldGroup().isModified()) {

				final Predicate<PersonContactDetailDto> sameTypePrimaryPredicate =
					pcd -> pcd.getPersonContactDetailType() == entry.getPersonContactDetailType()
						&& !entry.getUuid().equals(pcd.getUuid())
						&& pcd.isPrimaryContact();

				if (entry.isPrimaryContact()) {
					Optional<PersonContactDetailDto> existingPrimaryContactDetails =
						getContainer().getItemIds().stream().filter(sameTypePrimaryPredicate).findFirst();

					if (existingPrimaryContactDetails.isPresent()) {
						VaadinUiUtil.showConfirmationPopup(
							I18nProperties.getString(Strings.headingUpdatePersonContactDetails),
							new Label(I18nProperties.getString(Strings.messagePersonContactDetailsPrimaryDuplicate)),
							questionWindow -> {
								ConfirmationComponent confirmationComponent = new ConfirmationComponent(false) {

									private static final long serialVersionUID = 1L;

									@Override
									protected void onConfirm() {
										existingPrimaryContactDetails.get().setPrimaryContact(false);
										commitCallback.accept(editForm.getValue());
										questionWindow.close();
									}

									@Override
									protected void onCancel() {
										entry.setPrimaryContact(false);
										commitCallback.accept(editForm.getValue());
										questionWindow.close();
									}
								};

								confirmationComponent.getConfirmButton().setCaption(I18nProperties.getCaption(Captions.actionConfirm));
								confirmationComponent.getCancelButton().setCaption(I18nProperties.getCaption(Captions.actionCancel));

								return confirmationComponent;
							},
							null);
					} else {
						commitCallback.accept(editForm.getValue());
					}
				} else {
					commitCallback.accept(editForm.getValue());
				}
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

		table.addGeneratedColumn(COLUMN_PRIMARY, (Table.ColumnGenerator) (source, itemId, columnId) -> {
			PersonContactDetailDto personContactDetailDto = (PersonContactDetailDto) itemId;
			return new Label(personContactDetailDto.isPrimaryContact() ? VaadinIcons.CHECK_CIRCLE.getHtml() : "", ContentMode.HTML);
		});
		table.addGeneratedColumn(COLUMN_OWNER, (Table.ColumnGenerator) (source, itemId, columnId) -> {
			PersonContactDetailDto personContactDetailDto = (PersonContactDetailDto) itemId;
			return personContactDetailDto.isThirdParty() ? personContactDetailDto.getThirdPartyRole() : I18nProperties.getCaption(THIS_PERSON);
		});
		table.addGeneratedColumn(COLUMN_OWNER_NAME, (Table.ColumnGenerator) (source, itemId, columnId) -> {
			PersonContactDetailDto personContactDetailDto = (PersonContactDetailDto) itemId;
			return personContactDetailDto.isThirdParty() ? personContactDetailDto.getThirdPartyName() : thisPerson.toReference().getCaption();
		});

		table.setVisibleColumns(
			EDIT_COLUMN_ID,
			COLUMN_PRIMARY,
			COLUMN_OWNER,
			COLUMN_OWNER_NAME,
			PersonContactDetailDto.PERSON_CONTACT_DETAILS_TYPE,
			PersonContactDetailDto.CONTACT_INFORMATION);

		table.setCellStyleGenerator(
			FieldAccessCellStyleGenerator
				.withFieldAccessCheckers(PersonContactDetailDto.class, UiFieldAccessCheckers.forSensitiveData(isPseudonymized)));

		table.setColumnExpandRatio(EDIT_COLUMN_ID, 0);
		table.setColumnExpandRatio(COLUMN_PRIMARY, 0);
		table.setColumnExpandRatio(COLUMN_OWNER, 0);
		table.setColumnExpandRatio(COLUMN_OWNER_NAME, 0);
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

	public void setPseudonymized(boolean isPseudonymized) {
		this.isPseudonymized = isPseudonymized;
	}

	public void setThisPerson(PersonDto thisPerson) {
		this.thisPerson = thisPerson;
	}
}
