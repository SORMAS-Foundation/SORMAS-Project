package de.symeda.sormas.ui.fields;

import java.util.function.Consumer;

import com.vaadin.ui.Table;

import de.symeda.sormas.api.caze.PreviousHospitalizationDto;
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
	protected void updateColumns() {
		
		Table table = getTable();
	
		table.setVisibleColumns(
				PreviousHospitalizationDto.ADMISSION_DATE, PreviousHospitalizationDto.DISCHARGE_DATE,
				PreviousHospitalizationDto.HEALTH_FACILITY,
				PreviousHospitalizationDto.ISOLATION, PreviousHospitalizationDto.DESCRIPTION,
				 EDIT_COLUMN_ID);
	
		table.setColumnExpandRatio(PreviousHospitalizationDto.ADMISSION_DATE, 0);
		table.setColumnExpandRatio(PreviousHospitalizationDto.DISCHARGE_DATE, 0);
		table.setColumnExpandRatio(PreviousHospitalizationDto.HEALTH_FACILITY, 0);
		table.setColumnExpandRatio(PreviousHospitalizationDto.ISOLATION, 0);
		table.setColumnExpandRatio(PreviousHospitalizationDto.DESCRIPTION, 0);
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
		if (isModifiedObject(oldEntry.getIsolation(), newEntry.getIsolation()))
			return true;
		if (isModifiedObject(oldEntry.getDescription(), newEntry.getDescription()))
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
