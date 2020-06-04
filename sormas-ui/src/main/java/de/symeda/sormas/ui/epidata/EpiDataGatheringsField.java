/*******************************************************************************
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2018 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *******************************************************************************/
package de.symeda.sormas.ui.epidata;

import java.util.function.Consumer;

import com.vaadin.ui.Window;
import com.vaadin.v7.ui.Table;

import de.symeda.sormas.api.epidata.EpiDataGatheringDto;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.location.LocationDto;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.ui.UserProvider;
import de.symeda.sormas.ui.caze.AbstractTableField;
import de.symeda.sormas.ui.utils.CommitDiscardWrapperComponent;
import de.symeda.sormas.ui.utils.CommitDiscardWrapperComponent.CommitListener;
import de.symeda.sormas.ui.utils.CommitDiscardWrapperComponent.DeleteListener;
import de.symeda.sormas.ui.utils.DateFormatHelper;
import de.symeda.sormas.ui.utils.VaadinUiUtil;

@SuppressWarnings("serial")
public class EpiDataGatheringsField extends AbstractTableField<EpiDataGatheringDto> {

	private static final String CITY = Captions.city;
	private static final String DISTRICT = Captions.district;
	private static final String GATHERING_DAY = Captions.EpiDataGathering_gatheringDay;

	@Override
	public Class<EpiDataGatheringDto> getEntryType() {
		return EpiDataGatheringDto.class;
	}

	@Override
	protected void updateColumns() {
		Table table = getTable();

		table.addGeneratedColumn(CITY, new Table.ColumnGenerator() {
			@Override
			public Object generateCell(Table source, Object itemId, Object columnId) {
				EpiDataGatheringDto gathering = (EpiDataGatheringDto) itemId;
				LocationDto location = gathering.getGatheringAddress();
				return location.getCity();
			}
		});

		table.addGeneratedColumn(DISTRICT, new Table.ColumnGenerator() {
			@Override
			public Object generateCell(Table source, Object itemId, Object columnId) {
				EpiDataGatheringDto gathering = (EpiDataGatheringDto) itemId;
				LocationDto location = gathering.getGatheringAddress();
				return location.getDistrict();
			}
		});

		table.addGeneratedColumn(GATHERING_DAY, new Table.ColumnGenerator() {
			@Override
			public Object generateCell(Table source, Object itemId, Object columnId) {
				EpiDataGatheringDto gathering = (EpiDataGatheringDto) itemId;
				if (gathering.getGatheringDate() != null) {
					return DateFormatHelper.formatDate(gathering.getGatheringDate());
				} else {
					return I18nProperties.getString(Strings.unknown);
				}
			}
		});

		table.setVisibleColumns(
				EDIT_COLUMN_ID,
				EpiDataGatheringDto.DESCRIPTION,
				GATHERING_DAY,
				CITY,
				DISTRICT);

		table.setColumnExpandRatio(EDIT_COLUMN_ID, 0);
		table.setColumnExpandRatio(EpiDataGatheringDto.DESCRIPTION, 0);
		table.setColumnExpandRatio(GATHERING_DAY, 0);
		table.setColumnExpandRatio(CITY, 0);
		table.setColumnExpandRatio(DISTRICT, 0);

		for (Object columnId : table.getVisibleColumns()) {
			if (!columnId.equals(EDIT_COLUMN_ID)) {
				table.setColumnHeader(columnId, I18nProperties.getPrefixCaption(EpiDataGatheringDto.I18N_PREFIX, (String) columnId));
			}
		}
	}

	@Override
	protected boolean isEmpty(EpiDataGatheringDto entry) {
		return false;
	}

	@Override
	protected boolean isModified(EpiDataGatheringDto oldEntry, EpiDataGatheringDto newEntry) {
		if (isModifiedObject(oldEntry.getDescription(), newEntry.getDescription()))
			return true;
		if (isModifiedObject(oldEntry.getGatheringDate(), newEntry.getGatheringDate()))
			return true;
		if (isModifiedObject(oldEntry.getGatheringAddress(), newEntry.getGatheringAddress()))
			return true;

		return false;
	}

	@Override
	protected void editEntry(EpiDataGatheringDto entry, boolean create, Consumer<EpiDataGatheringDto> commitCallback) {
		if (create && entry.getUuid() == null) {
			entry.setUuid(DataHelper.createUuid());
		}
		
		EpiDataGatheringEditForm editForm = new EpiDataGatheringEditForm(UserRight.CASE_EDIT);
		editForm.setValue(entry);

		final CommitDiscardWrapperComponent<EpiDataGatheringEditForm> editView = new CommitDiscardWrapperComponent<EpiDataGatheringEditForm>(editForm, editForm.getFieldGroup());
		editView.getCommitButton().setCaption(I18nProperties.getString(Strings.done));

		Window popupWindow = VaadinUiUtil.showModalPopupWindow(editView, I18nProperties.getString(Strings.entityGathering));

		editView.addCommitListener(new CommitListener() {
			@Override
			public void onCommit() {
				if (!editForm.getFieldGroup().isModified()) {
					commitCallback.accept(editForm.getValue());
				}
			}
		});

		if (!isEmpty(entry)) {
			editView.addDeleteListener(new DeleteListener() {
				@Override
				public void onDelete() {
					popupWindow.close();
					EpiDataGatheringsField.this.removeEntry(entry);
				}
			}, I18nProperties.getCaption(EpiDataGatheringDto.I18N_PREFIX));
		}
	}

	@Override
	protected EpiDataGatheringDto createEntry() {
		EpiDataGatheringDto gathering = EpiDataGatheringDto.build();
		gathering.getGatheringAddress().setRegion(UserProvider.getCurrent().getUser().getRegion());
		return gathering;
	}
}
