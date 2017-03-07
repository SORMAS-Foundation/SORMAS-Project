package de.symeda.sormas.ui.epidata;

import java.util.function.Consumer;

import com.vaadin.ui.Table;

import de.symeda.sormas.api.I18nProperties;
import de.symeda.sormas.api.epidata.EpiDataGatheringDto;
import de.symeda.sormas.api.location.LocationDto;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.ui.caze.AbstractTableField;
import de.symeda.sormas.ui.login.LoginHelper;
import de.symeda.sormas.ui.utils.CommitDiscardWrapperComponent;
import de.symeda.sormas.ui.utils.CommitDiscardWrapperComponent.CommitListener;
import de.symeda.sormas.ui.utils.CommitDiscardWrapperComponent.DeleteListener;
import de.symeda.sormas.ui.utils.VaadinUiUtil;

@SuppressWarnings("serial")
public class EpiDataGatheringsField extends AbstractTableField<EpiDataGatheringDto> {
	
	private static final String CITY = "city";
	private static final String LGA = "lga";
	
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
		
		table.addGeneratedColumn(LGA, new Table.ColumnGenerator() {
			@Override
			public Object generateCell(Table source, Object itemId, Object columnId) {
				EpiDataGatheringDto gathering = (EpiDataGatheringDto) itemId;
				LocationDto location = gathering.getGatheringAddress();
				return location.getDistrict();
			}
		});
		
		table.setVisibleColumns(
				EpiDataGatheringDto.DESCRIPTION,
				EpiDataGatheringDto.GATHERING_DATE,
				CITY,
				LGA,
				EDIT_COLUMN_ID);
		
		table.setColumnExpandRatio(EpiDataGatheringDto.DESCRIPTION, 0);
		table.setColumnExpandRatio(EpiDataGatheringDto.GATHERING_DATE, 0);
		table.setColumnExpandRatio(CITY, 0);
		table.setColumnExpandRatio(LGA, 0);
		table.setColumnExpandRatio(EDIT_COLUMN_ID, 0);
		
		for (Object columnId : table.getVisibleColumns()) {
			table.setColumnHeader(columnId, I18nProperties.getPrefixFieldCaption(EpiDataGatheringDto.I18N_PREFIX, (String) columnId));
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
	protected void editEntry(EpiDataGatheringDto entry, Consumer<EpiDataGatheringDto> commitCallback) {
		EpiDataGatheringEditForm editForm = new EpiDataGatheringEditForm();
		editForm.setValue(entry);
		
		final CommitDiscardWrapperComponent<EpiDataGatheringEditForm> editView = new CommitDiscardWrapperComponent<EpiDataGatheringEditForm>(editForm, editForm.getFieldGroup());
		
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
					EpiDataGatheringsField.this.removeEntry(entry);
				}
			});
		}
		
		VaadinUiUtil.showModalPopupWindow(editView, "Edit gathering");
	}
	
	@Override
	protected EpiDataGatheringDto createEntry() {
		EpiDataGatheringDto gathering = new EpiDataGatheringDto();
		gathering.setUuid(DataHelper.createUuid());
		LocationDto location = new LocationDto();
		location.setUuid(DataHelper.createUuid());
		location.setRegion(LoginHelper.getCurrentUser().getRegion());
		gathering.setGatheringAddress(location);
		return gathering;
	}
}
