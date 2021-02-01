package de.symeda.sormas.ui.person;

import java.util.function.Consumer;

import com.vaadin.ui.Window;
import com.vaadin.v7.ui.Table;

import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.location.LocationDto;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.api.utils.fieldaccess.UiFieldAccessCheckers;
import de.symeda.sormas.api.utils.fieldvisibility.FieldVisibilityCheckers;
import de.symeda.sormas.ui.caze.AbstractTableField;
import de.symeda.sormas.ui.location.LocationEditForm;
import de.symeda.sormas.ui.utils.CommitDiscardWrapperComponent;
import de.symeda.sormas.ui.utils.VaadinUiUtil;

public class LocationsField extends AbstractTableField<LocationDto> {

	private static final String STREET = Captions.Location_street;

	private FieldVisibilityCheckers fieldVisibilityCheckers;

	public LocationsField(FieldVisibilityCheckers fieldVisibilityCheckers, UiFieldAccessCheckers fieldAccessCheckers) {
		super(fieldAccessCheckers);
		this.fieldVisibilityCheckers = fieldVisibilityCheckers;
	}

	@Override
	public Class<LocationDto> getEntryType() {
		return LocationDto.class;
	}

	@Override
	protected void editEntry(LocationDto entry, boolean create, Consumer<LocationDto> commitCallback) {

		if (create && entry.getUuid() == null) {
			entry.setUuid(DataHelper.createUuid());
		}

		LocationEditForm editForm = new LocationEditForm(fieldVisibilityCheckers, fieldAccessCheckers);
		editForm.showAddressType();
		editForm.setValue(entry);

		final CommitDiscardWrapperComponent<LocationEditForm> editView =
			new CommitDiscardWrapperComponent<>(editForm, true, editForm.getFieldGroup());
		editView.getCommitButton().setCaption(I18nProperties.getString(Strings.done));

		Window popupWindow = VaadinUiUtil.showModalPopupWindow(editView, I18nProperties.getCaption(LocationDto.I18N_PREFIX));

		editView.addCommitListener(() -> {
			if (!editForm.getFieldGroup().isModified()) {
				commitCallback.accept(editForm.getValue());
			}
		});

		if (!isEmpty(entry)) {
			editView.addDeleteListener(() -> {
				popupWindow.close();
				LocationsField.this.removeEntry(entry);
			}, I18nProperties.getCaption(LocationDto.I18N_PREFIX));
		}
	}

	@Override
	protected void updateColumns() {

		Table table = getTable();

		table.addGeneratedColumn(STREET, (Table.ColumnGenerator) (source, itemId, columnId) -> {
			LocationDto locationDto = (LocationDto) itemId;
			if (!locationDto.isPseudonymized()) {
				StringBuilder streetBuilder = new StringBuilder();
				streetBuilder.append(locationDto.getStreet() != null ? locationDto.getStreet() + " " : "");
				streetBuilder.append(locationDto.getHouseNumber() != null ? locationDto.getHouseNumber() : "");
				return streetBuilder.toString();
			} else {
				return I18nProperties.getCaption(Captions.inaccessibleValue);
			}
		});

		table.setVisibleColumns(
			EDIT_COLUMN_ID,
			LocationDto.ADDRESS_TYPE,
			STREET,
			LocationDto.CITY,
			LocationDto.POSTAL_CODE,
			LocationDto.DISTRICT,
			LocationDto.COMMUNITY);

		table.setColumnExpandRatio(EDIT_COLUMN_ID, 0);
		table.setColumnExpandRatio(LocationDto.ADDRESS_TYPE, 0);
		table.setColumnExpandRatio(STREET, 0);
		table.setColumnExpandRatio(LocationDto.CITY, 0);
		table.setColumnExpandRatio(LocationDto.POSTAL_CODE, 0);
		table.setColumnExpandRatio(LocationDto.DISTRICT, 0);
		table.setColumnExpandRatio(LocationDto.COMMUNITY, 0);

		for (Object columnId : table.getVisibleColumns()) {
			if (!columnId.equals(EDIT_COLUMN_ID)) {
				table.setColumnHeader(columnId, I18nProperties.getPrefixCaption(LocationDto.I18N_PREFIX, (String) columnId));
			}
		}
	}

	@Override
	protected boolean isEmpty(LocationDto entry) {
		return false;
	}

	@Override
	protected boolean isModified(LocationDto oldEntry, LocationDto newEntry) {

		if (isModifiedObject(oldEntry.getAdditionalInformation(), newEntry.getAdditionalInformation()))
			return true;
		if (isModifiedObject(oldEntry.getAddressType(), newEntry.getAddressType()))
			return true;
		if (isModifiedObject(oldEntry.getAreaType(), newEntry.getAreaType()))
			return true;
		if (isModifiedObject(oldEntry.getCity(), newEntry.getCity()))
			return true;
		if (isModifiedObject(oldEntry.getCommunity(), newEntry.getCommunity()))
			return true;
		if (isModifiedObject(oldEntry.getDetails(), newEntry.getDetails()))
			return true;
		if (isModifiedObject(oldEntry.getDistrict(), newEntry.getDistrict()))
			return true;
		if (isModifiedObject(oldEntry.getHouseNumber(), newEntry.getHouseNumber()))
			return true;
		if (isModifiedObject(oldEntry.getLatitude(), newEntry.getLatitude()))
			return true;
		if (isModifiedObject(oldEntry.getLatLonAccuracy(), newEntry.getLatLonAccuracy()))
			return true;
		if (isModifiedObject(oldEntry.getLongitude(), newEntry.getLongitude()))
			return true;
		if (isModifiedObject(oldEntry.getPostalCode(), newEntry.getPostalCode()))
			return true;
		if (isModifiedObject(oldEntry.getRegion(), newEntry.getRegion()))
			return true;
		if (isModifiedObject(oldEntry.getStreet(), newEntry.getStreet()))
			return true;

		return false;
	}

	@Override
	protected boolean isAccessible(Object columnId) {
		if (STREET.equals(columnId)) {
			columnId = LocationDto.STREET;
		}
		return fieldAccessCheckers.isAccessible(getEntryType(), columnId.toString());
	}
}
