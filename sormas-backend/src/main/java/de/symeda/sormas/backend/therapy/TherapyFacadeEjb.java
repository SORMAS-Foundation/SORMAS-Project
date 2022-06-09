package de.symeda.sormas.backend.therapy;

import java.sql.Timestamp;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.validation.constraints.NotNull;

import de.symeda.sormas.api.therapy.TherapyDto;
import de.symeda.sormas.api.therapy.TherapyFacade;
import de.symeda.sormas.api.therapy.TherapyReferenceDto;
import de.symeda.sormas.backend.user.UserService;
import de.symeda.sormas.backend.util.DtoHelper;
import de.symeda.sormas.backend.util.ModelConstants;

@Stateless(name = "TherapyFacade")
public class TherapyFacadeEjb implements TherapyFacade {

	@PersistenceContext(unitName = ModelConstants.PERSISTENCE_UNIT_NAME)
	private EntityManager em;

	@EJB
	private TherapyService service;
	@EJB
	private UserService userService;

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

		return target;
	}

	public Therapy fromDto(@NotNull TherapyDto source, boolean checkChangeDate) {

		Therapy target = DtoHelper.fillOrBuildEntity(source, service.getByUuid(source.getUuid()), () -> {
			Therapy newTherapy = new Therapy();
			if (source.getChangeDate() != null) {
				newTherapy.setChangeDate(new Timestamp(source.getChangeDate().getTime()));
			}

			return newTherapy;
		}, checkChangeDate);

		return target;
	}

	@LocalBean
	@Stateless
	public static class TherapyFacadeEjbLocal extends TherapyFacadeEjb {

	}
}
