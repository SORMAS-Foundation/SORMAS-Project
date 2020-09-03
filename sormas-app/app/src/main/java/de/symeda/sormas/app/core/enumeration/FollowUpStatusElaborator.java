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

import de.symeda.sormas.api.contact.FollowUpStatus;
import de.symeda.sormas.app.R;

public class FollowUpStatusElaborator implements StatusElaborator {

	private FollowUpStatus status = null;

	public FollowUpStatusElaborator(FollowUpStatus status) {
		this.status = status;
	}

	@Override
	public String getFriendlyName(Context context) {
		if (status != null) {
			return status.toShortString();
		}
		return "";
	}

	@Override
	public int getColorIndicatorResource() {
		if (status == FollowUpStatus.FOLLOW_UP) {
			return R.color.indicatorContactFollowUp;
		} else if (status == FollowUpStatus.COMPLETED) {
			return R.color.indicatorContactCompletedFollowUp;
		} else if (status == FollowUpStatus.CANCELED) {
			return R.color.indicatorContactCanceledFollowUp;
		} else if (status == FollowUpStatus.LOST) {
			return R.color.indicatorContactLostFollowUp;
		} else if (status == FollowUpStatus.NO_FOLLOW_UP) {
			return R.color.indicatorContactNoFollowUp;
		}

		return R.color.noColor;
	}

	@Override
	public Enum getValue() {
		return this.status;
	}

	@Override
	public int getIconResourceId() {
		switch (status) {
		case FOLLOW_UP:
			return R.drawable.ic_lp_ongoing_followup_192dp;
		case COMPLETED:
			return R.drawable.ic_lp_completed_followup_192dp;
		case CANCELED:
			return R.drawable.ic_lp_cancelled_followup_192dp;
		case LOST:
			return R.drawable.ic_lp_lost_to_followup_192dp;
		case NO_FOLLOW_UP:
			return R.drawable.ic_lp_no_followup_192dp;
		default:
			throw new IllegalArgumentException(status.toString());
		}
	}
}
