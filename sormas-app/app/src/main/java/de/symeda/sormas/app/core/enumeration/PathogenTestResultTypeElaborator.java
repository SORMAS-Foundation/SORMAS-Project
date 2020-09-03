/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2018 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

package de.symeda.sormas.app.core.enumeration;

import android.content.Context;

import de.symeda.sormas.api.sample.PathogenTestResultType;
import de.symeda.sormas.app.R;

public class PathogenTestResultTypeElaborator implements StatusElaborator {

	private PathogenTestResultType resultType = null;

	public PathogenTestResultTypeElaborator(PathogenTestResultType resultType) {
		this.resultType = resultType;
	}

	@Override
	public String getFriendlyName(Context context) {
		return resultType.toString();
	}

	@Override
	public int getColorIndicatorResource() {
		switch (resultType) {
		case POSITIVE:
			return R.color.samplePositive;
		case NEGATIVE:
			return R.color.sampleNegative;
		case PENDING:
			return R.color.samplePending;
		case INDETERMINATE:
			return R.color.sampleIndeterminate;
		default:
			throw new IllegalArgumentException(resultType.toString());
		}
	}

	@Override
	public Enum getValue() {
		return this.resultType;
	}

	@Override
	public int getIconResourceId() {
		switch (resultType) {
		case POSITIVE:
			return R.drawable.ic_add_24dp;
		case NEGATIVE:
			return R.drawable.ic_remove_24dp;
		case PENDING:
			return R.drawable.ic_pending_24dp;
		case INDETERMINATE:
			return R.drawable.ic_do_not_disturb_on_24dp;
		default:
			throw new IllegalArgumentException(resultType.toString());
		}
	}
}
