/*
 * ******************************************************************************
 * * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * * Copyright © 2016-2020 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

package de.symeda.sormas.api.campaign.form;

import de.symeda.sormas.api.ReferenceDto;
import de.symeda.sormas.api.statistics.StatisticsGroupingKey;

public class CampaignFormReferenceDto extends ReferenceDto implements StatisticsGroupingKey {

	public CampaignFormReferenceDto() {
	}

	public CampaignFormReferenceDto(String uuid) {
		setUuid(uuid);
	}

	public CampaignFormReferenceDto(String uuid, String caption) {
		setUuid(uuid);
		setCaption(caption);
	}

	@Override
	public int keyCompareTo(StatisticsGroupingKey o) {

		if (o == null) {
			throw new NullPointerException("Can't compare to null.");
		}

		if (this.equals(o)) {
			return 0;
		}

		int captionComparison = this.getCaption().compareTo(((CampaignFormReferenceDto) o).getCaption());
		if (captionComparison != 0) {
			return captionComparison;
		} else {
			return this.getUuid().compareTo(((CampaignFormReferenceDto) o).getUuid());
		}
	}
}
