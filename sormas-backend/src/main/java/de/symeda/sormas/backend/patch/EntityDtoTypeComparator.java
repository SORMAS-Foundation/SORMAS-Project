package de.symeda.sormas.backend.patch;

import java.util.Comparator;
import java.util.Map;

import de.symeda.sormas.api.EntityDto;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.epidata.EpiDataDto;
import de.symeda.sormas.api.hospitalization.HospitalizationDto;
import de.symeda.sormas.api.immunization.ImmunizationDto;
import de.symeda.sormas.api.person.PersonDto;

/**
 * Orders {@link AttachedEntityWrapper}s so that {@link CaseDataDto} comes first, {@link PersonDto} second,
 * and any other DTO type last.
 */
public class EntityDtoTypeComparator implements Comparator<EntityDto> {

	private static final Map<Class<? extends EntityDto>, Integer> WEIGHT_BY_CLASS =
		Map.of(CaseDataDto.class, 1, PersonDto.class, 2, EpiDataDto.class, 3, ImmunizationDto.class, 4, HospitalizationDto.class, 5);

	private static final int DEFAULT_WEIGHT = WEIGHT_BY_CLASS.size() + 1;

	@Override
	public int compare(EntityDto first, EntityDto second) {
		return Integer.compare(weightOf(first), weightOf(second));
	}

	private static int weightOf(EntityDto entityDto) {
		return WEIGHT_BY_CLASS.getOrDefault(entityDto.getClass(), DEFAULT_WEIGHT);
	}
}
