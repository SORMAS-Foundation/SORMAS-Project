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

import de.symeda.sormas.api.epidata.EpiDataTravelDto;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.ui.caze.AbstractTableField;
import de.symeda.sormas.ui.utils.CommitDiscardWrapperComponent;
import de.symeda.sormas.ui.utils.CommitDiscardWrapperComponent.CommitListener;
import de.symeda.sormas.ui.utils.CommitDiscardWrapperComponent.DeleteListener;
import de.symeda.sormas.ui.utils.DateFormatHelper;
import de.symeda.sormas.ui.utils.VaadinUiUtil;

@SuppressWarnings("serial")
public class EpiDataTravelsField extends AbstractTableField<EpiDataTravelDto> {

	private static final String PERIOD = Captions.EpiDataTravel_travelPeriod;

	@Override
	public Class<EpiDataTravelDto> getEntryType() {
		return EpiDataTravelDto.class;
	}

	@Override
	protected void updateColumns() {
		Table table = getTable();

		table.addGeneratedColumn(PERIOD, new Table.ColumnGenerator() {
			@Override
			public Object generateCell(Table source, Object itemId, Object columnId) {
				EpiDataTravelDto travel = (EpiDataTravelDto) itemId;
				if (travel.getTravelDateFrom() == null && travel.getTravelDateTo() == null) {
					return I18nProperties.getString(Strings.unknown);
				} else {
					StringBuilder periodBuilder = new StringBuilder();
					periodBuilder.append(travel.getTravelDateFrom() != null ? DateFormatHelper.formatDate(travel.getTravelDateFrom()) : "?");
					periodBuilder.append(" - ");
					periodBuilder.append(travel.getTravelDateTo() != null ? DateFormatHelper.formatDate(travel.getTravelDateTo()) : "?");
					return periodBuilder.toString();
				}
			}
		});

		table.setVisibleColumns(
				EDIT_COLUMN_ID,
				EpiDataTravelDto.TRAVEL_TYPE,
				EpiDataTravelDto.TRAVEL_DESTINATION,
				PERIOD);

		table.setColumnExpandRatio(EDIT_COLUMN_ID, 0);
		table.setColumnExpandRatio(EpiDataTravelDto.TRAVEL_TYPE, 0);
		table.setColumnExpandRatio(EpiDataTravelDto.TRAVEL_DESTINATION, 0);
		table.setColumnExpandRatio(PERIOD, 0);
		
		for (Object columnId : table.getVisibleColumns()) {
			if (!columnId.equals(EDIT_COLUMN_ID)) {
				table.setColumnHeader(columnId, I18nProperties.getPrefixCaption(EpiDataTravelDto.I18N_PREFIX, (String) columnId));
			}
		}
	}

	@Override
	protected boolean isEmpty(EpiDataTravelDto entry) {
		return false;
	}

	@Override
	protected boolean isModified(EpiDataTravelDto oldEntry, EpiDataTravelDto newEntry) {
		if (isModifiedObject(oldEntry.getTravelType(), newEntry.getTravelType()))
			return true;
		if (isModifiedObject(oldEntry.getTravelDestination(), newEntry.getTravelDestination()))
			return true;
		if (isModifiedObject(oldEntry.getTravelDateFrom(), newEntry.getTravelDateFrom()))
			return true;
		if (isModifiedObject(oldEntry.getTravelDateTo(), newEntry.getTravelDateTo()))
			return true;

		return false;
	}

	@Override
	protected void editEntry(EpiDataTravelDto entry, boolean create, Consumer<EpiDataTravelDto> commitCallback) {
		if (create && entry.getUuid() == null) {
			entry.setUuid(DataHelper.createUuid());
		}
		
		EpiDataTravelEditForm editForm = new EpiDataTravelEditForm(UserRight.CASE_EDIT);
		editForm.setValue(entry);

		final CommitDiscardWrapperComponent<EpiDataTravelEditForm> editView = new CommitDiscardWrapperComponent<EpiDataTravelEditForm>(editForm, editForm.getFieldGroup());
		editView.getCommitButton().setCaption(I18nProperties.getString(Strings.done));

		Window popupWindow = VaadinUiUtil.showModalPopupWindow(editView, I18nProperties.getString(Strings.entityTravel));

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
					EpiDataTravelsField.this.removeEntry(entry);
				}
			}, I18nProperties.getCaption(EpiDataTravelDto.I18N_PREFIX));
		}
	}

	@Override
	protected EpiDataTravelDto createEntry() {
		EpiDataTravelDto travel = EpiDataTravelDto.build();
		return travel;
	}
}
