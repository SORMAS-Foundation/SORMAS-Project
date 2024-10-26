/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2024 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package de.symeda.sormas.backend.adverseeventsfollowingimmunization;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.validation.constraints.NotNull;

import de.symeda.sormas.api.adverseeventsfollowingimmunization.AdverseEventsDto;
import de.symeda.sormas.backend.adverseeventsfollowingimmunization.entity.AdverseEvents;
import de.symeda.sormas.backend.util.DtoHelper;

@LocalBean
@Stateless(name = "AdverseEventsMapper")
public class AdverseEventsMapper {

	public static AdverseEventsDto toDto(AdverseEvents source) {

		if (source == null) {
			return null;
		}

		AdverseEventsDto target = new AdverseEventsDto();
		DtoHelper.fillDto(target, source);

		target.setSevereLocalReaction(source.getSevereLocalReaction());
		target.setSevereLocalReactionMoreThanThreeDays(source.isSevereLocalReactionMoreThanThreeDays());
		target.setSevereLocalReactionBeyondNearestJoint(source.isSevereLocalReactionBeyondNearestJoint());
		target.setSeizures(source.getSeizures());
		target.setSeizureType(source.getSeizureType());
		target.setAbscess(source.getAbscess());
		target.setSepsis(source.getSepsis());
		target.setEncephalopathy(source.getEncephalopathy());
		target.setToxicShockSyndrome(source.getToxicShockSyndrome());
		target.setThrombocytopenia(source.getThrombocytopenia());
		target.setAnaphylaxis(source.getAnaphylaxis());
		target.setFeverishFeeling(source.getFeverishFeeling());
		target.setOtherAdverseEventDetails(source.getOtherAdverseEventDetails());

		return target;
	}

	public AdverseEvents fillOrBuildEntity(@NotNull AdverseEventsDto source, AdverseEvents target, boolean checkChangeDate) {
		if (source == null) {
			return null;
		}

		target = DtoHelper.fillOrBuildEntity(source, target, AdverseEvents::new, checkChangeDate);

		target.setSevereLocalReaction(source.getSevereLocalReaction());
		target.setSevereLocalReactionMoreThanThreeDays(source.isSevereLocalReactionMoreThanThreeDays());
		target.setSevereLocalReactionBeyondNearestJoint(source.isSevereLocalReactionBeyondNearestJoint());
		target.setSeizures(source.getSeizures());
		target.setSeizureType(source.getSeizureType());
		target.setAbscess(source.getAbscess());
		target.setSepsis(source.getSepsis());
		target.setEncephalopathy(source.getEncephalopathy());
		target.setToxicShockSyndrome(source.getToxicShockSyndrome());
		target.setThrombocytopenia(source.getThrombocytopenia());
		target.setAnaphylaxis(source.getAnaphylaxis());
		target.setFeverishFeeling(source.getFeverishFeeling());
		target.setOtherAdverseEventDetails(source.getOtherAdverseEventDetails());

		return target;
	}
}
