package de.symeda.sormas.backend.therapy;

import java.sql.Timestamp;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.validation.constraints.NotNull;

import de.symeda.sormas.api.therapy.TherapyDto;
import de.symeda.sormas.api.therapy.TherapyFacade;
import de.symeda.sormas.api.therapy.TherapyReferenceDto;
import de.symeda.sormas.backend.util.DtoHelper;

@Stateless(name = "TherapyFacade")
public class TherapyFacadeEjb implements TherapyFacade {

	public static TherapyReferenceDto toReferenceDto(Therapy entity) {

		if (entity == null) {
			return null;
		}

		TherapyReferenceDto dto = new TherapyReferenceDto(entity.getUuid(), entity.toString());
		return dto;
	}

	public static TherapyDto toDto(Therapy source) {
		if (source == null) {
			return null;
		}
		TherapyDto target = new TherapyDto();
		DtoHelper.fillDto(target, source);

		target.setDirectlyObservedTreatment(source.isDirectlyObservedTreatment());
		target.setMdrXdrTuberculosis(source.isMdrXdrTuberculosis());
		target.setBeijingLineage(source.isBeijingLineage());

		return target;
	}

	public Therapy fillOrBuildEntity(@NotNull TherapyDto source, Therapy entity, boolean checkChangeDate) {
		entity = DtoHelper.fillOrBuildEntity(source, entity, () -> {
			Therapy newTherapy = new Therapy();
			if (source.getChangeDate() != null) {
				newTherapy.setChangeDate(new Timestamp(source.getChangeDate().getTime()));
			}

			return newTherapy;
		}, checkChangeDate);

		entity.setDirectlyObservedTreatment(source.isDirectlyObservedTreatment());
		entity.setMdrXdrTuberculosis(source.isMdrXdrTuberculosis());
		entity.setBeijingLineage(source.isBeijingLineage());

		return entity;
	}

	@LocalBean
	@Stateless
	public static class TherapyFacadeEjbLocal extends TherapyFacadeEjb {

	}
}
