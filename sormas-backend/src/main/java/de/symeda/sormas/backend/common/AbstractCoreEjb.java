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

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import de.symeda.sormas.api.EntityDto;
import de.symeda.sormas.api.ReferenceDto;
import de.symeda.sormas.api.deletionconfiguration.DeletionReference;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.api.utils.ValidationRuntimeException;
import de.symeda.sormas.api.utils.criteria.BaseCriteria;
import de.symeda.sormas.backend.deletionconfiguration.DeletionConfiguration;
import de.symeda.sormas.backend.user.UserService;
import de.symeda.sormas.backend.util.Pseudonymizer;
import de.symeda.sormas.backend.util.QueryHelper;

public abstract class AbstractCoreEjb<ADO extends CoreAdo, DTO extends EntityDto, INDEX_DTO extends Serializable, REF_DTO extends ReferenceDto, SRV extends AbstractCoreAdoService<ADO>, CRITERIA extends BaseCriteria>
	extends AbstractBaseEjb<ADO, DTO, INDEX_DTO, REF_DTO, SRV, CRITERIA> {

	public AbstractCoreEjb() {
	}

	public AbstractCoreEjb(Class<ADO> adoClass, Class<DTO> dtoClass, SRV service, UserService userService) {
		super(adoClass, dtoClass, service, userService);
	}

	@Override
	public DTO getByUuid(String uuid) {
		Pseudonymizer pseudonymizer = Pseudonymizer.getDefault(userService::hasRight);
		return convertToDto(service.getByUuid(uuid), pseudonymizer);
	}

	@Override
	public List<DTO> getByUuids(List<String> uuids) {
		Pseudonymizer pseudonymizer = Pseudonymizer.getDefault(userService::hasRight);
		return service.getByUuids(uuids).stream().map(c -> convertToDto(c, pseudonymizer)).collect(Collectors.toList());
	}

	@Override
	public List<DTO> getAllAfter(Date date) {
		return getAllAfter(date, null, null);
	}

	public List<DTO> getAllAfter(Date date, Integer batchSize, String lastSynchronizedUuid) {
		Pseudonymizer pseudonymizer = Pseudonymizer.getDefault(userService::hasRight);
		return service.getAllAfter(date, batchSize, lastSynchronizedUuid)
			.stream()
			.map(c -> convertToDto(c, pseudonymizer))
			.collect(Collectors.toList());
	}

	@Override
	public DTO save(@Valid @NotNull DTO dto) {
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

	public boolean isArchived(String uuid) {
		return service.isArchived(uuid);
	}

	public DTO convertToDto(ADO source, Pseudonymizer pseudonymizer) {

		DTO dto = toDto(source);
		pseudonymizeDto(source, dto, pseudonymizer);
		return dto;
	}

	public void executeAutomaticDeletion(DeletionConfiguration entityConfig) {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<ADO> cq = cb.createQuery(adoClass);
		Root<ADO> from = cq.from(adoClass);

		Date referenceDeletionDate = DateHelper.subtractDays(new Date(), entityConfig.getDeletionPeriod());
		cq.where(cb.lessThanOrEqualTo(from.get(getDeleteReferenceField(entityConfig.getDeletionReference())), referenceDeletionDate));

		List<ADO> toDeleteEntities = QueryHelper.getResultList(em, cq, null, null);

		toDeleteEntities.forEach(entity -> {
			delete(entity);
		});
	}

	protected void delete(ADO entity) {
		service.delete(entity);
	}

	protected String getDeleteReferenceField(DeletionReference deletionReference) {
		switch (deletionReference) {
			case CREATION:
				return AbstractDomainObject.CREATION_DATE;
			case END:
				return AbstractDomainObject.CHANGE_DATE;
			default:
				throw new IllegalArgumentException("deletion reference " + deletionReference + " not supported in " + getClass().getSimpleName());
		}
	}

	protected abstract void pseudonymizeDto(ADO source, DTO dto, Pseudonymizer pseudonymizer);

	protected abstract void restorePseudonymizedDto(DTO dto, DTO existingDto, ADO entity, Pseudonymizer pseudonymizer);

	public abstract void validate(DTO dto) throws ValidationRuntimeException;
}
