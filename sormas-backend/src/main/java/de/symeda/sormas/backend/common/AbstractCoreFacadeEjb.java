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
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import de.symeda.sormas.api.CoreFacade;
import de.symeda.sormas.api.EditPermissionType;
import de.symeda.sormas.api.EntityDto;
import de.symeda.sormas.api.ReferenceDto;
import de.symeda.sormas.api.common.CoreEntityType;
import de.symeda.sormas.api.common.DeletionDetails;
import de.symeda.sormas.api.common.DeletionReason;
import de.symeda.sormas.api.deletionconfiguration.DeletionInfoDto;
import de.symeda.sormas.api.deletionconfiguration.DeletionReference;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.utils.AccessDeniedException;
import de.symeda.sormas.api.utils.DateHelper;
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
	public void undelete(String uuid) {
		ADO ado = service.getByUuid(uuid);
		if (ado == null) {
			throw new IllegalArgumentException("Cannot undelete non existing entity: [" + getCoreEntityType() + "] - " + uuid);
		}
		service.undelete(ado);
	}

	public boolean isArchived(String uuid) {
		return service.isArchived(uuid);
	}

	public boolean isDeleted(String uuid) {
		return service.isDeleted(uuid);
	}

	public List<String> getUuidsForAutomaticDeletion(DeletionConfiguration entityConfig) {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<ADO> from = cq.from(adoClass);

		Date referenceDeletionDate = DateHelper.subtractDays(new Date(), entityConfig.getDeletionPeriod());

		Predicate filter = cb.lessThanOrEqualTo(from.get(getDeleteReferenceField(entityConfig.getDeletionReference())), referenceDeletionDate);
		if (entityConfig.getDeletionReference() == DeletionReference.MANUAL_DELETION) {
			filter = CriteriaBuilderHelper.and(cb, filter, cb.isTrue(from.get(DeletableAdo.DELETED)));
		}
		cq.where(filter);

		cq.select(from.get(DeletableAdo.UUID));
		cq.distinct(true);

		List<String> toDeleteUuids = em.createQuery(cq).getResultList();
		return toDeleteUuids;
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

		DeletionConfiguration deletionConfiguration = deletionConfigurationService.getCoreEntityTypeConfig(getCoreEntityType());

		if (deletionConfiguration == null
			|| deletionConfiguration.getDeletionPeriod() == null
			|| deletionConfiguration.getDeletionReference() == null) {
			return null;
		}

		Date referenceDate = getDeletionReferenceDate(uuid, deletionConfiguration);
		Date deletiondate = DateHelper.addDays(referenceDate, deletionConfiguration.getDeletionPeriod());
		String deletionReferenceField = getDeleteReferenceField(deletionConfiguration.getDeletionReference());
		return new DeletionInfoDto(deletiondate, referenceDate, deletionConfiguration.getDeletionPeriod(), deletionReferenceField);
	}

	@Override
	public DeletionInfoDto getManuallyDeletionInfo(String uuid) {

		DeletionConfiguration deletionConfiguration = deletionConfigurationService.getCoreEntityTypeManualDeletionConfig(getCoreEntityType());

		if (deletionConfiguration == null
			|| deletionConfiguration.getDeletionPeriod() == null
			|| deletionConfiguration.getDeletionReference() == null) {
			return null;
		}

		Date referenceDate = getDeletionReferenceDate(uuid, deletionConfiguration);
		Date deletiondate = DateHelper.addDays(referenceDate, deletionConfiguration.getDeletionPeriod());
		String deletionReferenceField = getDeleteReferenceField(deletionConfiguration.getDeletionReference());
		return new DeletionInfoDto(deletiondate, referenceDate, deletionConfiguration.getDeletionPeriod(), deletionReferenceField);
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

	private Date getDeletionReferenceDate(String uuid, DeletionConfiguration entityConfig) {

		if (entityConfig.getDeletionReference() == null) {
			return null;
		}

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Object[]> cq = cb.createQuery(Object[].class);
		Root<ADO> from = cq.from(adoClass);

		cq.select(from.get(getDeleteReferenceField(entityConfig.getDeletionReference())));
		cq.where(cb.equal(from.get(AbstractDomainObject.UUID), uuid));

		Object result = em.createQuery(cq).getSingleResult();
		return (Date) result;
	}

	protected abstract CoreEntityType getCoreEntityType();

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
