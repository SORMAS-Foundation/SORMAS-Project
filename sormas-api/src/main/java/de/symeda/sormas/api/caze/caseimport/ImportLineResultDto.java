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

package de.symeda.sormas.api.caze.caseimport;

import java.io.Serializable;

public class ImportLineResultDto<E> implements Serializable {

	private static final long serialVersionUID = -9004769653154669800L;

	private final ImportLineResult result;
	private final String message;
	private final E importEntities;

	private ImportLineResultDto(ImportLineResult result, String message, E importEntities) {
		this.result = result;
		this.message = message;
		this.importEntities = importEntities;
	}

	public ImportLineResult getResult() {
		return result;
	}

	public String getMessage() {
		return message;
	}

	public E getImportEntities() {
		return importEntities;
	}

	public boolean isError() {
		return result == ImportLineResult.ERROR;
	}

	public boolean isDuplicate() {
		return result == ImportLineResult.DUPLICATE;
	}

	public boolean isSuccess() {
		return result == ImportLineResult.SUCCESS;
	}

	public static <E> ImportLineResultDto<E> successResult() {
		return new ImportLineResultDto<>(ImportLineResult.SUCCESS, null, null);
	}

	public static <E> ImportLineResultDto<E> errorResult(String message) {
		return new ImportLineResultDto<>(ImportLineResult.ERROR, message, null);
	}

	public static <E> ImportLineResultDto<E> duplicateResult(E entities) {
		return new ImportLineResultDto<>(ImportLineResult.DUPLICATE, null, entities);
	}
}
