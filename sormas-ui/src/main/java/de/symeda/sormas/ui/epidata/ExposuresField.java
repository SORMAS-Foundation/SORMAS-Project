/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2020 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package de.symeda.sormas.ui.epidata;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import com.vaadin.v7.data.Validator;
import com.vaadin.v7.data.util.converter.Converter;
import com.vaadin.v7.ui.Table;

import de.symeda.sormas.api.EntityDto;
import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.event.TypeOfPlace;
import de.symeda.sormas.api.exposure.ExposureDto;
import de.symeda.sormas.api.exposure.ExposureType;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.location.LocationDto;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.api.utils.fieldaccess.UiFieldAccessCheckers;
import de.symeda.sormas.api.utils.fieldvisibility.FieldVisibilityCheckers;
import de.symeda.sormas.ui.caze.AbstractTableField;
import de.symeda.sormas.ui.utils.DateFormatHelper;

@SuppressWarnings("serial")
public class ExposuresField extends AbstractTableField<ExposureDto> {

	private static final String COLUMN_EXPOSURE_TYPE = ExposureDto.EXPOSURE_TYPE;
	private static final String COLUMN_TYPE_OF_PLACE = ExposureDto.TYPE_OF_PLACE;
	private static final String COLUMN_DATE = Captions.date;
	private static final String COLUMN_REGION = LocationDto.REGION;
	private static final String COLUMN_DISTRICT = LocationDto.DISTRICT;
	private static final String COLUMN_ADDRESS = Captions.address;
	private static final String COLUMN_SOURCE_CASE_NAME = Captions.exposureSourceCaseName;

	private FieldVisibilityCheckers fieldVisibilityCheckers;
	private Map<String, String> sourceCaseNames = new HashMap<>();

	public ExposuresField(FieldVisibilityCheckers fieldVisibilityCheckers, UiFieldAccessCheckers fieldAccessCheckers) {
		super(fieldAccessCheckers);
		this.fieldVisibilityCheckers = fieldVisibilityCheckers;
	}

	@Override
	protected void updateColumns() {
		Table table = getTable();

		addGeneratedColumns(table);

		table.setVisibleColumns(
			EDIT_COLUMN_ID,
			COLUMN_EXPOSURE_TYPE,
			COLUMN_TYPE_OF_PLACE,
			COLUMN_DATE,
			ExposureDto.RISK_AREA,
			COLUMN_REGION,
			COLUMN_DISTRICT,
			COLUMN_ADDRESS,
			ExposureDto.DESCRIPTION,
			COLUMN_SOURCE_CASE_NAME);

		for (Object columnId : table.getVisibleColumns()) {
			if (!columnId.equals(EDIT_COLUMN_ID)) {
				table.setColumnHeader(columnId, I18nProperties.getPrefixCaption(ExposureDto.I18N_PREFIX, (String) columnId));
			}
		}
	}

	private void addGeneratedColumns(Table table) {
		table.addGeneratedColumn(COLUMN_EXPOSURE_TYPE, (Table.ColumnGenerator) (source, itemId, columnId) -> {
			ExposureDto exposure = (ExposureDto) itemId;
			return exposure.getExposureType() != ExposureType.OTHER ? exposure.getExposureType().toString() : exposure.getExposureTypeDetails();
		});

		table.addGeneratedColumn(COLUMN_TYPE_OF_PLACE, (Table.ColumnGenerator) (source, itemId, columnId) -> {
			ExposureDto exposure = (ExposureDto) itemId;
			return exposure.getTypeOfPlace() != TypeOfPlace.OTHER ? exposure.getTypeOfPlace().toString() : exposure.getTypeOfPlaceDetails();
		});

		table.addGeneratedColumn(COLUMN_DATE, (Table.ColumnGenerator) (source, itemId, columnId) -> {
			ExposureDto exposure = (ExposureDto) itemId;
			return DateFormatHelper.buildPeriodString(exposure.getStartDate(), exposure.getEndDate());
		});

		table.addGeneratedColumn(COLUMN_REGION, (Table.ColumnGenerator) (source, itemId, columnId) -> {
			ExposureDto exposure = (ExposureDto) itemId;
			return DataHelper.toStringNullable(exposure.getLocation().getRegion().toString());
		});

		table.addGeneratedColumn(COLUMN_DISTRICT, (Table.ColumnGenerator) (source, itemId, columnId) -> {
			ExposureDto exposure = (ExposureDto) itemId;
			return DataHelper.toStringNullable(exposure.getLocation().getDistrict().toString());
		});

		table.addGeneratedColumn(COLUMN_ADDRESS, (Table.ColumnGenerator) (source, itemId, columnId) -> {
			ExposureDto exposure = (ExposureDto) itemId;
			return DataHelper.toStringNullable(exposure.getLocation().buildAddressCaption());
		});

		table.addGeneratedColumn(COLUMN_SOURCE_CASE_NAME, (Table.ColumnGenerator) (source, itemId, columnId) -> {
			ExposureDto exposure = (ExposureDto) itemId;
			return DataHelper.toStringNullable(sourceCaseNames.get(exposure.getUuid()));
		});
	}

	@Override
	protected boolean isEmpty(ExposureDto entry) {
		return false;
	}

	@Override
	protected boolean isModified(ExposureDto oldEntry, ExposureDto newEntry) {
		return isModifiedObject(oldEntry.getExposureType(), newEntry.getExposureType())
			|| isModifiedObject(oldEntry.getExposureTypeDetails(), newEntry.getExposureTypeDetails())
			|| isModifiedObject(oldEntry.getTypeOfPlace(), newEntry.getTypeOfPlace())
			|| isModifiedObject(oldEntry.getTypeOfPlaceDetails(), newEntry.getTypeOfPlaceDetails())
			|| isModifiedObject(oldEntry.getStartDate(), newEntry.getStartDate())
			|| isModifiedObject(oldEntry.getEndDate(), newEntry.getEndDate())
			|| isModifiedObject(oldEntry.getRiskArea(), newEntry.getRiskArea())
			|| isModifiedObject(oldEntry.getLocation(), newEntry.getLocation())
			|| isModifiedObject(oldEntry.getDescription(), newEntry.getDescription())
			|| isModifiedObject(oldEntry.getContactToCase(), newEntry.getContactToCase());
	}

	@Override
	public Class<ExposureDto> getEntryType() {
		return ExposureDto.class;
	}

	@Override
	protected void editEntry(ExposureDto entry, boolean create, Consumer<ExposureDto> commitCallback) {

	}

	@Override
	protected ExposureDto createEntry() {
		return super.createEntry();
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void setValue(Collection newFieldValue, boolean repaintIsNotNeeded, boolean ignoreReadOnly)
		throws ReadOnlyException, Converter.ConversionException, Validator.InvalidValueException {
		super.setValue(newFieldValue, repaintIsNotNeeded, ignoreReadOnly);

		sourceCaseNames = FacadeProvider.getEpiDataFacade()
			.getExposureSourceCaseNames(((Collection<ExposureDto>) newFieldValue).stream().map(EntityDto::getUuid).collect(Collectors.toList()));
	}
}
