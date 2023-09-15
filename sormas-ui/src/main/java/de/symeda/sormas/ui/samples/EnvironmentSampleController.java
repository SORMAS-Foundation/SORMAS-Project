/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2023 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

package de.symeda.sormas.ui.samples;

import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;

import org.apache.commons.collections.CollectionUtils;

import de.symeda.sormas.api.environment.environmentsample.EnvironmentSampleIndexDto;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.utils.DeleteRestoreHandlers;

public class EnvironmentSampleController {

	public EnvironmentSampleController() {
		// do nothing
	}

	public void deleteAllSelectedItems(
		Collection<EnvironmentSampleIndexDto> selectedRows,
		EnvironmentSampleGrid sampleGrid,
		Runnable noEntriesRemainingCallback) {

		ControllerProvider.getDeleteRestoreController()
			.deleteAllSelectedItems(
				selectedRows,
				DeleteRestoreHandlers.forEnvironmentSample(),
				bulkOperationCallback(sampleGrid, noEntriesRemainingCallback));

	}

	public void restoreSelectedSamples(
		Collection<EnvironmentSampleIndexDto> selectedRows,
		EnvironmentSampleGrid sampleGrid,
		Runnable noEntriesRemainingCallback) {

		ControllerProvider.getDeleteRestoreController()
			.restoreSelectedItems(
				selectedRows,
				DeleteRestoreHandlers.forEnvironmentSample(),
				bulkOperationCallback(sampleGrid, noEntriesRemainingCallback));
	}

	private Consumer<List<EnvironmentSampleIndexDto>> bulkOperationCallback(EnvironmentSampleGrid sampleGrid, Runnable noEntriesRemainingCallback) {
		return remainingSamples -> {
			sampleGrid.reload();
			if (CollectionUtils.isNotEmpty(remainingSamples)) {
				sampleGrid.asMultiSelect().selectItems(remainingSamples.toArray(new EnvironmentSampleIndexDto[0]));
			} else {
				noEntriesRemainingCallback.run();
			}
		};
	}

}
