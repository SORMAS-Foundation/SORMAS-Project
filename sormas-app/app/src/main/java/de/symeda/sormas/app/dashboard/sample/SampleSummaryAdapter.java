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

package de.symeda.sormas.app.dashboard.sample;

import android.content.Context;

import de.symeda.sormas.app.component.visualization.ViewTypeHelper.ViewTypeEnum;
import de.symeda.sormas.app.core.adapter.multiview.AdapterConfiguration;
import de.symeda.sormas.app.core.adapter.multiview.EnumMapDataBinderAdapter;
import de.symeda.sormas.app.core.adapter.multiview.IAdapterConfiguration;

/**
 * Created by Orson on 01/12/2017.
 */

public class SampleSummaryAdapter extends EnumMapDataBinderAdapter<ViewTypeEnum> {

	private Context context;

	public SampleSummaryAdapter(Context context) {
		this.context = context;
	}

	@Override
	public ViewTypeEnum getEnumFromPosition(int position) {
		ViewTypeEnum viewType;

		switch (position) {
		case PositionHelper.TOTAL_SAMPLES: {
			viewType = ViewTypeEnum.TOTAL;
			break;
		}
		case PositionHelper.POSITIVE_RESULTS:
		case PositionHelper.NEGATIVE_RESULTS:
		case PositionHelper.PENDING_RESULTS:
		case PositionHelper.INDETERMINATE_RESULTS:
		case PositionHelper.INADEQUATE_SPECIMEN: {
			viewType = ViewTypeEnum.SINGLE_CIRCULAR_PROGRESS;
			break;
		}
		default:
			throw new IllegalArgumentException("The value of position is invalid.");
		}

		return viewType;
	}

	@Override
	public ViewTypeEnum getEnumFromOrdinal(int ordinal) {
		return ViewTypeEnum.values()[ordinal];
	}

	public IAdapterConfiguration startConfig() {
		return new AdapterConfiguration<ViewTypeEnum>(this.context, this);
	}

	static class PositionHelper {

		static final int TOTAL_SAMPLES = 0;
		static final int POSITIVE_RESULTS = 1;
		static final int NEGATIVE_RESULTS = 2;
		static final int PENDING_RESULTS = 3;
		static final int INDETERMINATE_RESULTS = 4;
		static final int INADEQUATE_SPECIMEN = 5;
	}
}
