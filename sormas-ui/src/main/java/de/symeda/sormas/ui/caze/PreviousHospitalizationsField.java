package de.symeda.sormas.ui.caze;

import java.util.Iterator;
import java.util.function.Consumer;

import com.vaadin.ui.Grid.Column;
import com.vaadin.ui.Table;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.I18nProperties;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.caze.PreviousHospitalizationDto;
import de.symeda.sormas.api.facility.FacilityDto;
import de.symeda.sormas.api.facility.FacilityReferenceDto;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.ui.utils.CommitDiscardWrapperComponent;
import de.symeda.sormas.ui.utils.CommitDiscardWrapperComponent.CommitListener;
import de.symeda.sormas.ui.utils.CommitDiscardWrapperComponent.DeleteListener;
import de.symeda.sormas.ui.utils.VaadinUiUtil;

@SuppressWarnings("serial")
public class PreviousHospitalizationsField extends AbstractTableField<PreviousHospitalizationDto> {

	private static final String PERIOD = "period";
	private static final String WARD = "ward";
	private static final String LGA = "lga";
	
	@Override
	public Class<PreviousHospitalizationDto> getEntryType() {
		return PreviousHospitalizationDto.class;
	}

	@Override
	protected void updateColumns() {
		
		Table table = getTable();
		
		table.addGeneratedColumn(PERIOD, new Table.ColumnGenerator() {
			@Override
			public Object generateCell(Table source, Object itemId, Object columnId) {
				PreviousHospitalizationDto prevHospitalization = (PreviousHospitalizationDto) itemId;
				StringBuilder periodBuilder = new StringBuilder();
				periodBuilder.append(DateHelper.formatDDMMYYYY(prevHospitalization.getAdmissionDate()));
				periodBuilder.append(" - ");
				periodBuilder.append(DateHelper.formatDDMMYYYY(prevHospitalization.getDischargeDate()));
				return periodBuilder.toString();
			}
		});
		
		table.addGeneratedColumn(WARD, new Table.ColumnGenerator() {
			@Override
			public Object generateCell(Table source, Object itemId, Object columnId) {
				PreviousHospitalizationDto prevHospitalization = (PreviousHospitalizationDto) itemId;
				FacilityReferenceDto facilityRef = prevHospitalization.getHealthFacility();
				FacilityDto facilityDto = FacadeProvider.getFacilityFacade().getByUuid(facilityRef.getUuid());
				return facilityDto.getFacilityCommunity();
			}
		});
		
		table.addGeneratedColumn(LGA, new Table.ColumnGenerator() {
			@Override
			public Object generateCell(Table source, Object itemId, Object columnId) {
				PreviousHospitalizationDto prevHospitalization = (PreviousHospitalizationDto) itemId;
				FacilityReferenceDto facilityRef = prevHospitalization.getHealthFacility();
				FacilityDto facilityDto = FacadeProvider.getFacilityFacade().getByUuid(facilityRef.getUuid());
				return facilityDto.getFacilityDistrict();
			}
		});
		
		table.setVisibleColumns(
				PERIOD,
				PreviousHospitalizationDto.HEALTH_FACILITY,
				WARD,
				LGA,
				PreviousHospitalizationDto.ISOLATED,
				EDIT_COLUMN_ID);
	
		table.setColumnExpandRatio(PERIOD, 0);
		table.setColumnExpandRatio(PreviousHospitalizationDto.HEALTH_FACILITY, 0);
		table.setColumnExpandRatio(WARD, 0);
		table.setColumnExpandRatio(LGA, 0);
		table.setColumnExpandRatio(PreviousHospitalizationDto.ISOLATED, 0);
		table.setColumnExpandRatio(EDIT_COLUMN_ID, 0);
		
		table.setColumnHeader(PERIOD, I18nProperties.getPrefixFieldCaption(PreviousHospitalizationDto.I18N_PREFIX, PERIOD));
		table.setColumnHeader(PreviousHospitalizationDto.HEALTH_FACILITY, I18nProperties.getPrefixFieldCaption(PreviousHospitalizationDto.I18N_PREFIX, PreviousHospitalizationDto.HEALTH_FACILITY));
		table.setColumnHeader(WARD, I18nProperties.getPrefixFieldCaption(PreviousHospitalizationDto.I18N_PREFIX, WARD));
		table.setColumnHeader(LGA, I18nProperties.getPrefixFieldCaption(PreviousHospitalizationDto.I18N_PREFIX, LGA));
		table.setColumnHeader(PreviousHospitalizationDto.ISOLATED, I18nProperties.getPrefixFieldCaption(PreviousHospitalizationDto.I18N_PREFIX, PreviousHospitalizationDto.ISOLATED));
		table.setColumnHeader(EDIT_COLUMN_ID, I18nProperties.getPrefixFieldCaption(PreviousHospitalizationDto.I18N_PREFIX, EDIT_COLUMN_ID));
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
