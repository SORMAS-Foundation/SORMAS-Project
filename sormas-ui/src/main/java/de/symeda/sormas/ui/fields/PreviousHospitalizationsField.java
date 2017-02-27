package de.symeda.sormas.ui.fields;

import java.util.Date;
import java.util.function.Consumer;

import com.vaadin.data.Property;
import com.vaadin.ui.Table;

import de.symeda.sormas.api.I18nProperties;
import de.symeda.sormas.api.caze.PreviousHospitalizationDto;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.ui.caze.PreviousHospitalizationEditForm;
import de.symeda.sormas.ui.utils.CommitDiscardWrapperComponent;
import de.symeda.sormas.ui.utils.CommitDiscardWrapperComponent.CommitListener;
import de.symeda.sormas.ui.utils.CommitDiscardWrapperComponent.DeleteListener;
import de.symeda.sormas.ui.utils.VaadinUiUtil;

@SuppressWarnings("serial")
public class PreviousHospitalizationsField extends AbstractTableField<PreviousHospitalizationDto> {

	@Override
	public Class<PreviousHospitalizationDto> getEntryType() {
		return PreviousHospitalizationDto.class;
	}

	@Override
	protected Table createTable() {

		final Table table = new Table() {
			@Override
			protected String formatPropertyValue(Object rowId, Object colId, Property<?> property) {
				Object value = property.getValue();
				switch((String)colId) {
				case PreviousHospitalizationDto.ADMISSION_DATE:
				case PreviousHospitalizationDto.DISCHARGE_DATE:
					return DateHelper.formatDMY((Date)value);
				default:
					return super.formatPropertyValue(rowId, colId, property);
				}
			}
		};

		table.setEditable(false);
		table.setSelectable(false);
		table.setSizeFull();

		createEditColumn(table);

		return table;
	}
	
	@Override
	protected void updateColumns() {
		
		Table table = getTable();
	
		table.setVisibleColumns(
				PreviousHospitalizationDto.ADMISSION_DATE, PreviousHospitalizationDto.DISCHARGE_DATE,
				PreviousHospitalizationDto.HEALTH_FACILITY,
				PreviousHospitalizationDto.ISOLATED,
				 EDIT_COLUMN_ID);
	
		table.setColumnExpandRatio(PreviousHospitalizationDto.ADMISSION_DATE, 0);
		table.setColumnExpandRatio(PreviousHospitalizationDto.DISCHARGE_DATE, 0);
		table.setColumnExpandRatio(PreviousHospitalizationDto.HEALTH_FACILITY, 0);
		table.setColumnExpandRatio(PreviousHospitalizationDto.ISOLATED, 0);
		
		for (Object columnId : table.getVisibleColumns()) {
			if (!EDIT_COLUMN_ID.equals(columnId)) {
				table.setColumnHeader(columnId, I18nProperties.getPrefixFieldCaption(PreviousHospitalizationDto.I18N_PREFIX, (String)columnId));
			}
		}
	}

	@Override
	protected boolean isEmpty(PreviousHospitalizationDto entry) {
		return entry.getAdmissionDate() == null 
				&& entry.getDischargeDate() == null
				&& entry.getHealthFacility() == null;
	}

	@Override
	protected boolean isModified(PreviousHospitalizationDto oldEntry, PreviousHospitalizationDto newEntry) {
		
		if (isModifiedObject(oldEntry.getAdmissionDate(), newEntry.getAdmissionDate()))
			return true;
		if (isModifiedObject(oldEntry.getDischargeDate(), newEntry.getDischargeDate()))
			return true;
		if (isModifiedObject(oldEntry.getHealthFacility(), newEntry.getHealthFacility()))
			return true;
		if (isModifiedObject(oldEntry.getIsolated(), newEntry.getIsolated()))
			return true;
		
		return false;
	}

	@Override
	protected void editEntry(PreviousHospitalizationDto entry, Consumer<PreviousHospitalizationDto> commitCallback) {

		PreviousHospitalizationEditForm editForm = new PreviousHospitalizationEditForm();
		editForm.setValue(entry);
		
		final CommitDiscardWrapperComponent<PreviousHospitalizationEditForm> editView = new CommitDiscardWrapperComponent<PreviousHospitalizationEditForm>(editForm, editForm.getFieldGroup());
		
        editView.addCommitListener(new CommitListener() {
        	
        	@Override
        	public void onCommit() {
        		if (editForm.getFieldGroup().isValid()) {
        			commitCallback.accept(editForm.getValue());
        		}
        	}
        });
        
        if (!isEmpty(entry)) {
	        editView.addDeleteListener(new DeleteListener() {
				@Override
				public void onDelete() {
					PreviousHospitalizationsField.this.removeEntry(entry);
				}
			});
        }
        
		VaadinUiUtil.showModalPopupWindow(editView, "Edit previous hospitalization");
		
	}
}
