/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2022 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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
 */

package de.symeda.sormas.api.importexport;

import de.symeda.sormas.api.utils.DataHelper;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;

public class ImportRelatedObjectsMapper {

	private final List<Mapper<?>> mappers;

	public static class Builder {

		private final List<Mapper<?>> mappers = new ArrayList<>();

		public <T> Builder addMapper(Class<T> objectType, List<T> objects, Supplier<T> objectBuilder, CellValueMapper<T> cellValueMapper) {
			mappers.add(new Mapper<>(objectType, objects, objectBuilder, cellValueMapper));
			return this;
		}

		public ImportRelatedObjectsMapper build() {
			return new ImportRelatedObjectsMapper(mappers);
		}
	}

	private ImportRelatedObjectsMapper(List<Mapper<?>> mappers) {
		this.mappers = mappers;
	}

	public boolean map(ImportCellData cellData) throws InvalidColumnException, ImportErrorException {
		List<Mapper<?>> cellMappers = mappers.stream().filter(m -> m.isFor(cellData.getEntityClass())).collect(Collectors.toList());

		for (Mapper<?> cellMapper : cellMappers) {
			cellMapper.map(cellData);
		}

		return cellMappers.size() > 0;
	}

	private static class Mapper<T> {

		private String firstColumnName;

		private boolean currentObjectHasValues;

		private T currentObject;

		private final Class<T> objectType;

		private final List<T> objects;

		private final Supplier<T> objectBuilder;

		private final CellValueMapper<T> cellValueMapper;

		public Mapper(Class<T> objectType, List<T> objects, Supplier<T> objectBuilder, CellValueMapper<T> cellValueMapper) {
			this.objectType = objectType;
			this.objects = objects;
			this.objectBuilder = objectBuilder;
			this.cellValueMapper = cellValueMapper;
		}

		public boolean isFor(String entityClass) {
			return DataHelper.equal(entityClass, DataHelper.getHumanClassName(objectType));
		}

		public void map(ImportCellData cellData) throws InvalidColumnException, ImportErrorException {
			// If the current column belongs to a sample, set firstSampleColumnName if it's empty, add a new sample
			// to the list if the first column of a new sample has been reached and insert the entry of the cell into the sample
			String columnName = String.join(".", cellData.getEntityPropertyPath());

			if (firstColumnName == null) {
				firstColumnName = columnName;
			}

			if (columnName.equals(firstColumnName)) {
				currentObjectHasValues = false;
				currentObject = objectBuilder.get();
			}

			if (currentObject != null && !StringUtils.isEmpty(cellData.getValue())) {
				if (!currentObjectHasValues) {
					objects.add(currentObject);
					currentObjectHasValues = true;
				}

				cellValueMapper.map(currentObject, cellData.getValue(), cellData.getEntityPropertyPath());
			}
		}
	}

	public interface CellValueMapper<T> {

		void map(T object, String cellValue, String[] entityPropertyPath) throws InvalidColumnException, ImportErrorException;
	}
}
