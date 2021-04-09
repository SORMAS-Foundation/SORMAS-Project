/*
 * ******************************************************************************
 * * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * * Copyright © 2016-2021 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 * *
 * * This program is free software: you can redistribute it and/or modify
 * * it under the terms of the GNU General Public License as published by
 * * the Free Software Foundation, either version 3 of the License, or
 * * (at your option) any later version.
 * *
 * * This program is distributed in the hope that it will be useful,
 * * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * * GNU General Public License for more details.
 * *
 * * You should have received a copy of the GNU General Public License
 * * along with this program. If not, see <https://www.gnu.org/licenses/>.
 * ******************************************************************************
 */

package de.symeda.sormas.ui.ActivityAsCase;

import java.util.Collection;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;

import com.vaadin.ui.Window;
import com.vaadin.v7.data.Property;
import com.vaadin.v7.shared.ui.label.ContentMode;
import com.vaadin.v7.ui.Label;
import com.vaadin.v7.ui.Table;

import de.symeda.sormas.api.activityascase.ActivityAsCaseDto;
import de.symeda.sormas.api.activityascase.ActivityAsCaseType;
import de.symeda.sormas.api.event.TypeOfPlace;
import de.symeda.sormas.api.exposure.ExposureDto;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.api.utils.fieldaccess.UiFieldAccessCheckers;
import de.symeda.sormas.api.utils.fieldvisibility.FieldVisibilityCheckers;
import de.symeda.sormas.ui.SormasUI;
import de.symeda.sormas.ui.caze.AbstractTableField;
import de.symeda.sormas.ui.utils.CommitDiscardWrapperComponent;
import de.symeda.sormas.ui.utils.DateFormatHelper;
import de.symeda.sormas.ui.utils.FieldAccessCellStyleGenerator;
import de.symeda.sormas.ui.utils.VaadinUiUtil;

@SuppressWarnings("serial")
public class ActivityAsCaseField extends AbstractTableField<ActivityAsCaseDto> {

	private static final String COLUMN_ACTIVITY_AS_CASE_TYPE = ActivityAsCaseDto.ACTIVITY_AS_CASE_TYPE;
	private static final String COLUMN_TYPE_OF_PLACE = ActivityAsCaseDto.TYPE_OF_PLACE;
	private static final String COLUMN_DATE = Captions.date;
	private static final String COLUMN_ADDRESS = Captions.address;
	private static final String COLUMN_DESCRIPTION = ActivityAsCaseDto.DESCRIPTION;

	private final FieldVisibilityCheckers fieldVisibilityCheckers;
	private boolean isPseudonymized;

	public ActivityAsCaseField(FieldVisibilityCheckers fieldVisibilityCheckers, UiFieldAccessCheckers fieldAccessCheckers) {

		super(fieldAccessCheckers);
		this.fieldVisibilityCheckers = fieldVisibilityCheckers;
	}

	@Override
	protected void updateColumns() {
		Table table = getTable();

		addGeneratedColumns(table);

		table.setVisibleColumns(EDIT_COLUMN_ID, COLUMN_ACTIVITY_AS_CASE_TYPE, COLUMN_TYPE_OF_PLACE, COLUMN_DATE, COLUMN_ADDRESS, COLUMN_DESCRIPTION);

		table.setCellStyleGenerator(
			FieldAccessCellStyleGenerator.withFieldAccessCheckers(ActivityAsCaseDto.class, UiFieldAccessCheckers.forSensitiveData(isPseudonymized)));

		for (Object columnId : table.getVisibleColumns()) {
			if (!columnId.equals(EDIT_COLUMN_ID)) {
				table.setColumnHeader(columnId, I18nProperties.getPrefixCaption(ActivityAsCaseDto.I18N_PREFIX, (String) columnId));
			}
		}
	}

	private void addGeneratedColumns(Table table) {
		table.addGeneratedColumn(COLUMN_ACTIVITY_AS_CASE_TYPE, (Table.ColumnGenerator) (source, itemId, columnId) -> {
			ActivityAsCaseDto activityAsCaseDto = (ActivityAsCaseDto) itemId;
			String activityAsCaseString = ActivityAsCaseType.OTHER != activityAsCaseDto.getActivityAsCaseType()
				? activityAsCaseDto.getActivityAsCaseType().toString()
				: activityAsCaseDto.getActivityAsCaseTypeDetails();

			return new Label(activityAsCaseString, ContentMode.HTML);
		});

		table.addGeneratedColumn(COLUMN_TYPE_OF_PLACE, (Table.ColumnGenerator) (source, itemId, columnId) -> {
			ActivityAsCaseDto activityAsCase = (ActivityAsCaseDto) itemId;
			return activityAsCase.getTypeOfPlace() != null
				? TypeOfPlace.OTHER != activityAsCase.getTypeOfPlace()
					? activityAsCase.getTypeOfPlace().toString()
					: activityAsCase.getTypeOfPlaceDetails()
				: "";
		});

		table.addGeneratedColumn(COLUMN_DATE, (Table.ColumnGenerator) (source, itemId, columnId) -> {
			ActivityAsCaseDto activityAsCase = (ActivityAsCaseDto) itemId;
			return DateFormatHelper.buildPeriodString(activityAsCase.getStartDate(), activityAsCase.getEndDate());
		});

		table.addGeneratedColumn(COLUMN_ADDRESS, (Table.ColumnGenerator) (source, itemId, columnId) -> {
			ActivityAsCaseDto activityAsCase = (ActivityAsCaseDto) itemId;
			String region = DataHelper.toStringNullable(activityAsCase.getLocation().getRegion());
			String district = DataHelper.toStringNullable(activityAsCase.getLocation().getDistrict());
			String address = DataHelper.toStringNullable(activityAsCase.getLocation().buildAddressCaption());

			return Stream.of(region, district, address).filter(StringUtils::isNotBlank).collect(Collectors.joining(", "));
		});

		table.addGeneratedColumn(COLUMN_DESCRIPTION, (Table.ColumnGenerator) (source, itemId, columnId) -> {
			ActivityAsCaseDto activityAsCase = (ActivityAsCaseDto) itemId;

			Label descriptionLabel = new Label(StringUtils.abbreviate(activityAsCase.getDescription(), 75));
			if (StringUtils.isNotBlank(activityAsCase.getDescription())) {
				descriptionLabel.setDescription(activityAsCase.getDescription());
			}

			return descriptionLabel;
		});
	}

	@Override
	protected boolean isEmpty(ActivityAsCaseDto entry) {
		return false;
	}

	@Override
	protected boolean isModified(ActivityAsCaseDto oldEntry, ActivityAsCaseDto newEntry) {
		return isModifiedObject(oldEntry.getActivityAsCaseType(), newEntry.getActivityAsCaseType())
			|| isModifiedObject(oldEntry.getActivityAsCaseTypeDetails(), newEntry.getActivityAsCaseTypeDetails())
			|| isModifiedObject(oldEntry.getTypeOfPlace(), newEntry.getTypeOfPlace())
			|| isModifiedObject(oldEntry.getTypeOfPlaceDetails(), newEntry.getTypeOfPlaceDetails())
			|| isModifiedObject(oldEntry.getStartDate(), newEntry.getStartDate())
			|| isModifiedObject(oldEntry.getEndDate(), newEntry.getEndDate())
			|| isModifiedObject(oldEntry.getLocation(), newEntry.getLocation())
			|| isModifiedObject(oldEntry.getDescription(), newEntry.getDescription());
	}

	@Override
	public Class<ActivityAsCaseDto> getEntryType() {
		return ActivityAsCaseDto.class;
	}

	@Override
	protected void editEntry(ActivityAsCaseDto entry, boolean create, Consumer<ActivityAsCaseDto> commitCallback) {
		if (create) {
			entry.setUuid(DataHelper.createUuid());
		}

		ActivityAsCaseForm activityAsCaseForm = new ActivityAsCaseForm(create, fieldVisibilityCheckers, fieldAccessCheckers);
		activityAsCaseForm.setValue(entry);

		final CommitDiscardWrapperComponent<ActivityAsCaseForm> component = new CommitDiscardWrapperComponent<>(
			activityAsCaseForm,
			((SormasUI) getUI()).getUserProvider().hasUserRight(UserRight.CASE_EDIT),
			activityAsCaseForm.getFieldGroup());
		component.getCommitButton().setCaption(I18nProperties.getString(Strings.done));

		Window popupWindow = VaadinUiUtil.showModalPopupWindow(component, I18nProperties.getString(Strings.entityActivityAsCase));
		popupWindow.setHeight(90, Unit.PERCENTAGE);

		component.addCommitListener(() -> {
			if (!activityAsCaseForm.getFieldGroup().isModified()) {
				commitCallback.accept(activityAsCaseForm.getValue());
			}
		});

		if (!create) {
			component.addDeleteListener(() -> {
				popupWindow.close();
				ActivityAsCaseField.this.removeEntry(entry);

			}, I18nProperties.getCaption(ExposureDto.I18N_PREFIX));
		}
	}

	@Override
	protected ActivityAsCaseDto createEntry() {
		UserDto user = ((SormasUI) getUI()).getUserProvider().getUser();
		ActivityAsCaseDto activityAsCase = ActivityAsCaseDto.build(null);
		activityAsCase.getLocation().setRegion(user.getRegion());
		activityAsCase.getLocation().setDistrict(user.getDistrict());
		activityAsCase.getLocation().setCommunity(user.getCommunity());
		activityAsCase.setReportingUser(user.toReference());
		return activityAsCase;
	}

	@Override
	public void setPropertyDataSource(Property newDataSource) {
		super.setPropertyDataSource(newDataSource);
	}

	public void setPseudonymized(boolean isPseudonymized) {
		this.isPseudonymized = isPseudonymized;
	}

	@Override
	public Property<Collection<ActivityAsCaseDto>> getPropertyDataSource() {
		getAddButton().setVisible(!isPseudonymized);
		return super.getPropertyDataSource();
	}
}
