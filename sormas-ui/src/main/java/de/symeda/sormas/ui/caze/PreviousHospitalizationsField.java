package de.symeda.sormas.ui.caze;

import java.util.function.Consumer;

import com.vaadin.ui.Table;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.I18nProperties;
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
				if (facilityRef != null) {
					FacilityDto facilityDto = FacadeProvider.getFacilityFacade().getByUuid(facilityRef.getUuid());
					return facilityDto.getCommunity();
				}
				return null;
			}
		});

		table.addGeneratedColumn(LGA, new Table.ColumnGenerator() {
			@Override
			public Object generateCell(Table source, Object itemId, Object columnId) {
				PreviousHospitalizationDto prevHospitalization = (PreviousHospitalizationDto) itemId;
				FacilityReferenceDto facilityRef = prevHospitalization.getHealthFacility();
				if (facilityRef != null) {
					FacilityDto facilityDto = FacadeProvider.getFacilityFacade().getByUuid(facilityRef.getUuid());
					return facilityDto.getDistrict();
				}
				return null;
			}
		});

		table.setVisibleColumns(PERIOD, PreviousHospitalizationDto.HEALTH_FACILITY, WARD, LGA,
				PreviousHospitalizationDto.ISOLATED, EDIT_COLUMN_ID);

		table.setColumnExpandRatio(PERIOD, 0);
		table.setColumnExpandRatio(PreviousHospitalizationDto.HEALTH_FACILITY, 0);
		table.setColumnExpandRatio(WARD, 0);
		table.setColumnExpandRatio(LGA, 0);
		table.setColumnExpandRatio(PreviousHospitalizationDto.ISOLATED, 0);
		table.setColumnExpandRatio(EDIT_COLUMN_ID, 0);

		for (Object columnId : table.getVisibleColumns()) {
			table.setColumnHeader(columnId,
					I18nProperties.getPrefixFieldCaption(PreviousHospitalizationDto.I18N_PREFIX, (String) columnId));
		}
	}

	@Override
	protected boolean isEmpty(PreviousHospitalizationDto entry) {
		return false; // has required fields, no empty objects possible
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
		if (isModifiedObject(oldEntry.getDescription(), newEntry.getDescription()))
			return true;

		return false;
	}

	@Override
	protected void editEntry(PreviousHospitalizationDto entry, Consumer<PreviousHospitalizationDto> commitCallback) {

		PreviousHospitalizationEditForm editForm = new PreviousHospitalizationEditForm();
		editForm.setValue(entry);

		final CommitDiscardWrapperComponent<PreviousHospitalizationEditForm> editView = new CommitDiscardWrapperComponent<PreviousHospitalizationEditForm>(
				editForm, editForm.getFieldGroup());
		editView.getCommitButton().setCaption("done");

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

		VaadinUiUtil.showModalPopupWindow(editView, "Previous hospitalization information");

	}
}
