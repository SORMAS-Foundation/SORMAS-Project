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
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.annotation.security.DenyAll;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import de.symeda.sormas.api.CoreFacade;
import de.symeda.sormas.api.EditPermissionType;
import de.symeda.sormas.api.EntityDto;
import de.symeda.sormas.api.ReferenceDto;
import de.symeda.sormas.api.common.DeletableEntityType;
import de.symeda.sormas.api.common.DeletionDetails;
import de.symeda.sormas.api.common.DeletionReason;
import de.symeda.sormas.api.deletionconfiguration.DeletionInfoDto;
import de.symeda.sormas.api.deletionconfiguration.DeletionReference;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.utils.AccessDeniedException;
import de.symeda.sormas.api.utils.criteria.BaseCriteria;
import de.symeda.sormas.backend.deletionconfiguration.DeletionConfiguration;
import de.symeda.sormas.backend.deletionconfiguration.DeletionConfigurationService;
import de.symeda.sormas.backend.feature.FeatureConfigurationFacadeEjb;
import de.symeda.sormas.backend.util.Pseudonymizer;

public abstract class AbstractCoreFacadeEjb<ADO extends CoreAdo, DTO extends EntityDto, INDEX_DTO extends Serializable, REF_DTO extends ReferenceDto, SRV extends AbstractCoreAdoService<ADO, ? extends QueryJoins<ADO>>, CRITERIA extends BaseCriteria>
	extends AbstractBaseEjb<ADO, DTO, INDEX_DTO, REF_DTO, SRV, CRITERIA>
	implements CoreFacade<DTO, INDEX_DTO, REF_DTO, CRITERIA> {

	@Inject
	private DeletionConfigurationService deletionConfigurationService;
	@Inject
	private FeatureConfigurationFacadeEjb.FeatureConfigurationFacadeEjbLocal featureConfigurationFacade;

	protected AbstractCoreFacadeEjb() {
	}

	protected AbstractCoreFacadeEjb(Class<ADO> adoClass, Class<DTO> dtoClass, SRV service) {
		super(adoClass, dtoClass, service);
	}

	@DenyAll
	public DTO doSave(@Valid @NotNull DTO dto) {
		ADO existingAdo = dto.getUuid() != null ? service.getByUuid(dto.getUuid()) : null;

		if (existingAdo != null && !service.isEditAllowed(existingAdo)) {
			throw new AccessDeniedException(I18nProperties.getString(Strings.errorEntityNotEditable));
		}

		DTO existingDto = toDto(existingAdo);

		Pseudonymizer pseudonymizer = createPseudonymizer();
		restorePseudonymizedDto(dto, existingDto, existingAdo, pseudonymizer);

		validate(dto);

		existingAdo = fillOrBuildEntity(dto, existingAdo, true);
		service.ensurePersisted(existingAdo);

		return toPseudonymizedDto(existingAdo, pseudonymizer);
	}

	public boolean exists(String uuid) {
		return service.exists(uuid);
	}

	@DenyAll
	public void delete(String uuid, DeletionDetails deletionDetails) {
		ADO ado = service.getByUuid(uuid);
		service.delete(ado, deletionDetails);
	}

	@DenyAll
	public void restore(String uuid) {
		ADO ado = service.getByUuid(uuid);
		if (ado == null) {
			throw new IllegalArgumentException("Cannot restore non existing entity: [" + getCoreEntityType() + "] - " + uuid);
		}
		service.restore(ado);
	}

	public boolean isArchived(String uuid) {
		return service.isArchived(uuid);
	}

	public boolean isDeleted(String uuid) {
		return service.isDeleted(uuid);
	}

	public List<String> getUuidsForAutomaticDeletion(DeletionConfiguration entityConfig) {
		return service.getUuidsForAutomaticDeletion(entityConfig);
	}

	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void doAutomaticDeletion(List<String> toDeleteUuids, boolean deletePermanent) {

		toDeleteUuids.forEach(uuid -> {
			ADO ado = service.getByUuid(uuid);
			if (deletePermanent) {
				service.deletePermanent(ado);
			} else {
				service.delete(ado, new DeletionDetails(DeletionReason.OTHER_REASON, I18nProperties.getString(Strings.entityAutomaticSoftDeletion)));
			}
		});
	}

	@Override
	public DeletionInfoDto getAutomaticDeletionInfo(String uuid) {
		return service.getAutomaticDeletionInfo(uuid);
	}

	@Override
	public DeletionInfoDto getManuallyDeletionInfo(String uuid) {
		return service.getManuallyDeletionInfo(uuid);
	}

	protected String getDeleteReferenceField(DeletionReference deletionReference) {

		switch (deletionReference) {
		case CREATION:
			return AbstractDomainObject.CREATION_DATE;
		case END:
			return CoreAdo.END_OF_PROCESSING_DATE;
		case MANUAL_DELETION:
			return AbstractDomainObject.CHANGE_DATE;
		default:
			throw new IllegalArgumentException("deletion reference " + deletionReference + " not supported in " + getClass().getSimpleName());
		}
	}

	protected abstract DeletableEntityType getCoreEntityType();

	@DenyAll
	public void archive(String entityUuid, Date endOfProcessingDate) {
		service.archive(entityUuid, endOfProcessingDate);
	}

	@DenyAll
	public void archive(List<String> entityUuids) {
		service.archive(entityUuids);
	}

	@DenyAll
	public void dearchive(List<String> entityUuids, String dearchiveReason) {
		service.dearchive(entityUuids, dearchiveReason);
	}

	public Date calculateEndOfProcessingDate(String entityUuid) {
		return service.calculateEndOfProcessingDate(Collections.singletonList(entityUuid)).get(entityUuid);
	}

	@Override
	public EditPermissionType getEditPermissionType(String uuid) {
		return service.getEditPermissionType(service.getByUuid(uuid));
	}

	@Override
	public boolean isEditAllowed(String uuid) {
		return service.isEditAllowed(service.getByUuid(uuid));
	}
}
