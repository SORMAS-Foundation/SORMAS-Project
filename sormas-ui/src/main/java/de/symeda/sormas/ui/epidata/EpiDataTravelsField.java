package de.symeda.sormas.ui.epidata;

import java.util.function.Consumer;

import com.vaadin.ui.Table;
import com.vaadin.ui.Window;

import de.symeda.sormas.api.I18nProperties;
import de.symeda.sormas.api.epidata.EpiDataTravelDto;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.ui.caze.AbstractTableField;
import de.symeda.sormas.ui.utils.CommitDiscardWrapperComponent;
import de.symeda.sormas.ui.utils.CommitDiscardWrapperComponent.CommitListener;
import de.symeda.sormas.ui.utils.CommitDiscardWrapperComponent.DeleteListener;
import de.symeda.sormas.ui.utils.VaadinUiUtil;

@SuppressWarnings("serial")
public class EpiDataTravelsField extends AbstractTableField<EpiDataTravelDto> {

	private static final String PERIOD = "period";
	
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
					return "Unknown";
				} else {
					StringBuilder periodBuilder = new StringBuilder();
					periodBuilder.append(travel.getTravelDateFrom() != null ? DateHelper.formatLocalDate(travel.getTravelDateFrom()) : "?");
					periodBuilder.append(" - ");
					periodBuilder.append(travel.getTravelDateTo() != null ? DateHelper.formatLocalDate(travel.getTravelDateTo()) : "?");
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
			table.setColumnHeader(columnId, I18nProperties.getPrefixFieldCaption(EpiDataTravelDto.I18N_PREFIX, (String) columnId));
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
		EpiDataTravelEditForm editForm = new EpiDataTravelEditForm(UserRight.CASE_EDIT);
		editForm.setValue(entry);
		
		final CommitDiscardWrapperComponent<EpiDataTravelEditForm> editView = new CommitDiscardWrapperComponent<EpiDataTravelEditForm>(editForm, editForm.getFieldGroup());
		editView.getCommitButton().setCaption("done");
		
		Window popupWindow = VaadinUiUtil.showModalPopupWindow(editView, "Travel");

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
			}, I18nProperties.getFieldCaption("EpiDataTravel"));
		}
	}
	
}
