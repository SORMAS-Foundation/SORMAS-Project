/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2021 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

package de.symeda.sormas.backend.common;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import de.symeda.sormas.api.EntityDto;
import de.symeda.sormas.api.ReferenceDto;
import de.symeda.sormas.api.utils.ValidationRuntimeException;
import de.symeda.sormas.api.utils.criteria.BaseCriteria;
import de.symeda.sormas.backend.user.UserService;
import de.symeda.sormas.backend.util.Pseudonymizer;

public abstract class AbstractCoreEjb<ADO extends CoreAdo, DTO extends EntityDto, INDEX_DTO extends Serializable, REF_DTO extends ReferenceDto, SRV extends AbstractCoreAdoService<ADO>, CRITERIA extends BaseCriteria>
	extends AbstractBaseEjb<ADO, DTO, INDEX_DTO, REF_DTO, SRV, CRITERIA> {

	public AbstractCoreEjb() {
	}

	public AbstractCoreEjb(Class<ADO> adoClass, Class<DTO> dtoClass, SRV service, UserService userService) {
		super(adoClass, dtoClass, service, userService);
	}

	@Override
	public DTO getByUuid(String uuid) {
		Pseudonymizer pseudonymizer = Pseudonymizer.getDefaultWithInaccessibleValuePlaceHolder(userService::hasRight);
		return convertToDto(service.getByUuid(uuid), pseudonymizer);
	}

	@Override
	public List<DTO> getByUuids(List<String> uuids) {
		Pseudonymizer pseudonymizer = Pseudonymizer.getDefaultWithInaccessibleValuePlaceHolder(userService::hasRight);
		return service.getByUuids(uuids).stream().map(c -> convertToDto(c, pseudonymizer)).collect(Collectors.toList());
	}

	@Override
	public List<DTO> getAllAfter(Date date) {
		return getAllAfter(date, null, null);
	}

	public List<DTO> getAllAfter(Date date, Integer batchSize, String lastSynchronizedUuid) {
		Pseudonymizer pseudonymizer = Pseudonymizer.getDefaultWithInaccessibleValuePlaceHolder(userService::hasRight);
		return service.getAllActiveAfter(date, batchSize, lastSynchronizedUuid)
			.stream()
			.map(c -> convertToDto(c, pseudonymizer))
			.collect(Collectors.toList());
	}

	@Override
	public DTO save(DTO dto) {
		ADO existingAdo = dto.getUuid() != null ? service.getByUuid(dto.getUuid()) : null;
		DTO existingDto = toDto(existingAdo);

		Pseudonymizer pseudonymizer = Pseudonymizer.getDefault(userService::hasRight);
		restorePseudonymizedDto(dto, existingDto, existingAdo, pseudonymizer);

		validate(dto);

		existingAdo = fillOrBuildEntity(dto, existingAdo, true);
		service.ensurePersisted(existingAdo);

		return convertToDto(existingAdo, pseudonymizer);
	}

	public boolean exists(String uuid) {
		return service.exists(uuid);
	}

	@Override
	public void archive(String uuid) {
		ADO ado = service.getByUuid(uuid);
		if (ado != null) {
			ado.setArchived(true);
			service.ensurePersisted(ado);
		}
	}

	public void dearchive(String uuid) {
		ADO ado = service.getByUuid(uuid);
		if (ado != null) {
			ado.setArchived(false);
			service.ensurePersisted(ado);
		}
	}

	public DTO convertToDto(ADO source, Pseudonymizer pseudonymizer) {

		DTO dto = toDto(source);
		pseudonymizeDto(source, dto, pseudonymizer);
		return dto;
	}

	protected abstract void pseudonymizeDto(ADO source, DTO dto, Pseudonymizer pseudonymizer);

	protected abstract void restorePseudonymizedDto(DTO dto, DTO existingDto, ADO immunization, Pseudonymizer pseudonymizer);

	public abstract void validate(DTO dto) throws ValidationRuntimeException;
}
