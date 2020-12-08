package de.symeda.sormas.backend.labmessage;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.validation.constraints.NotNull;

import de.symeda.sormas.api.labmessage.LabMessageDto;
import de.symeda.sormas.api.labmessage.LabMessageFacade;
import de.symeda.sormas.backend.util.DtoHelper;

@Stateless(name = "LabMessageFacade")
public class LabMessageFacadeEjb implements LabMessageFacade {

	private LabMessage fromDto(@NotNull LabMessageDto source, LabMessage target) {

		if (target == null) {
			target = new LabMessage();
			target.setUuid(source.getUuid());
		}

		DtoHelper.validateDto(source, target);

		target.setPersonBirthDateDD(source.getPersonBirthDateDD());

		return target;
	}

	public LabMessageDto toDto(LabMessage entity) {

		if (entity == null) {
			return null;
		}
		LabMessageDto target = new LabMessageDto();
		DtoHelper.fillDto(target, entity);

		return target;
	}

	@LocalBean
	@Stateless
	public static class LabMessageFacadeEjbLocal extends LabMessageFacadeEjb {

	}
}
