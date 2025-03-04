/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2026 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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
package de.symeda.sormas.backend.systemconfiguration;

import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.security.PermitAll;
import javax.ejb.LocalBean;
import javax.ejb.Lock;
import javax.ejb.LockType;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.symeda.sormas.api.systemconfiguration.SystemConfigurationCategoryCriteria;
import de.symeda.sormas.api.systemconfiguration.SystemConfigurationCategoryDto;
import de.symeda.sormas.api.systemconfiguration.SystemConfigurationCategoryFacade;
import de.symeda.sormas.api.systemconfiguration.SystemConfigurationCategoryIndexDto;
import de.symeda.sormas.api.systemconfiguration.SystemConfigurationCategoryReferenceDto;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.api.utils.SortProperty;
import de.symeda.sormas.api.utils.ValidationRuntimeException;
import de.symeda.sormas.backend.common.AbstractBaseEjb;
import de.symeda.sormas.backend.common.CriteriaBuilderHelper;
import de.symeda.sormas.backend.util.DtoHelper;
import de.symeda.sormas.backend.util.Pseudonymizer;
import de.symeda.sormas.backend.util.QueryHelper;
import de.symeda.sormas.backend.util.RightsAllowed;

/**
 * EJB for managing system configuration categories.
 * This class provides methods to save, count, retrieve, validate, and convert system configuration categories.
 */
@Stateless(name = "SystemConfigurationCategoryFacade")
@PermitAll
@RightsAllowed(UserRight._SYSTEM_CONFIGURATION)
public class SystemConfigurationCategoryEjb
    extends
    AbstractBaseEjb<SystemConfigurationCategory, SystemConfigurationCategoryDto, SystemConfigurationCategoryIndexDto, SystemConfigurationCategoryReferenceDto, SystemConfigurationCategoryService, SystemConfigurationCategoryCriteria>
    implements SystemConfigurationCategoryFacade {

    private static final Logger LOGGER = LoggerFactory.getLogger(SystemConfigurationCategoryEjb.class);

    /**
     * Constructor for SystemConfigurationCategoryEjb.
     */
    public SystemConfigurationCategoryEjb() {
        super();
    }

    @PermitAll
    @Inject
    public void setService(SystemConfigurationCategoryService service) {
        this.service = service;
    }

    /**
     * Save a system configuration category.
     *
     * @param dto
     *            the system configuration category DTO
     * @return the saved system configuration category DTO
     */
    @Lock(LockType.READ)
    @RightsAllowed(UserRight._SYSTEM_CONFIGURATION)
    @Override
    public SystemConfigurationCategoryDto save(final SystemConfigurationCategoryDto dto) {
        if (dto == null) {
            return null;
        }

        validate(dto);

        final SystemConfigurationCategory existing = service.getByUuid(dto.getUuid());

        final SystemConfigurationCategory newValue = fillOrBuildEntity(dto, existing, true);
        service.ensurePersisted(newValue);

        return toDto(newValue);
    }

    /**
     * Count the number of system configuration categories matching the criteria.
     *
     * @param criteria
     *            the criteria to match
     * @return the count of matching system configuration categories
     */
    @Lock(LockType.READ)
    @RightsAllowed(UserRight._SYSTEM_CONFIGURATION)
    @Override
    public long count(final SystemConfigurationCategoryCriteria criteria) {
        final CriteriaBuilder cb = em.getCriteriaBuilder();
        final CriteriaQuery<Long> cq = cb.createQuery(Long.class);
        final Root<SystemConfigurationCategory> root = cq.from(SystemConfigurationCategory.class);

        final Predicate filter = CriteriaBuilderHelper.and(cb, service.buildCriteriaFilter(criteria, cb, root));
        if (filter != null) {
            cq.where(filter);
        }

        cq.select(cb.count(root));
        return em.createQuery(cq).getSingleResult();
    }

    /**
     * Get system configuration categories by their UUIDs.
     *
     * @param uuids
     *            the list of UUIDs
     * @return the list of matching system configuration category DTOs
     */
    @Lock(LockType.READ)
    @RightsAllowed(UserRight._SYSTEM_CONFIGURATION)
    @Override
    public List<SystemConfigurationCategoryDto> getByUuids(final List<String> uuids) {
        return service.getByUuids(uuids).stream().map(this::toDto).collect(Collectors.toList());
    }

    /**
     * Get all UUIDs of system configuration categories.
     *
     * @return the list of all UUIDs
     */
    @Lock(LockType.READ)
    @RightsAllowed(UserRight._SYSTEM_CONFIGURATION)
    @Override
    public List<String> getAllUuids() {
        return service.getAllUuids();
    }

    /**
     * Get a list of system configuration category index DTOs matching the criteria.
     *
     * @param criteria
     *            the criteria to match
     * @param first
     *            the first result to retrieve
     * @param max
     *            the maximum number of results to retrieve
     * @param sortProperties
     *            the properties to sort by
     * @return the list of matching system configuration category index DTOs
     */
    @Lock(LockType.READ)
    @RightsAllowed(UserRight._SYSTEM_CONFIGURATION)
    @Override
    public List<SystemConfigurationCategoryIndexDto> getIndexList(
        final SystemConfigurationCategoryCriteria criteria,
        final Integer first,
        final Integer max,
        final List<SortProperty> sortProperties) {

        LOGGER.debug(
            "Retrieving SystemConfigurationCategoryIndexDto list with criteria: {}, first: {}, max: {}, sortProperties: {}",
            criteria,
            first,
            max,
            sortProperties);
        final CriteriaBuilder cb = em.getCriteriaBuilder();
        final CriteriaQuery<SystemConfigurationCategory> cq = cb.createQuery(SystemConfigurationCategory.class);
        final Root<SystemConfigurationCategory> root = cq.from(SystemConfigurationCategory.class);
        Predicate filter = null;
        if (criteria != null) {
            filter = service.buildCriteriaFilter(criteria, cb, root);
        }
        if (filter != null) {
            cq.where(filter);
        }
        cq.orderBy(
            cb.asc(root.get(SystemConfigurationCategory.NAME_FIELD_NAME)),
            cb.asc(root.get(SystemConfigurationCategory.DESCRIPTION_FIELD_NAME)));
        if (!sortProperties.isEmpty()) {
            final List<Order> order = sortProperties.stream().map(sortProperty -> {
                final Expression<?> expression;
                switch (sortProperty.propertyName) {
                case SystemConfigurationCategory.NAME_FIELD_NAME:
                    expression = root.get(sortProperty.propertyName);
                    break;
                case SystemConfigurationCategory.DESCRIPTION_FIELD_NAME:
                    expression = cb.lower(root.get(sortProperty.propertyName));
                    break;
                default:
                    LOGGER.error("Invalid sort property {}.", sortProperty.propertyName);
                    throw new IllegalArgumentException(sortProperty.propertyName);
                }
                return sortProperty.ascending ? cb.asc(expression) : cb.desc(expression);
            }).collect(Collectors.toList());
            cq.orderBy(order);
        }
        cq.select(root);
        final List<SystemConfigurationCategoryIndexDto> result = QueryHelper.getResultList(em, cq, first, max, this::toIndexDto);
        LOGGER.debug("Retrieved {} SystemConfigurationCategoryIndexDto objects", result.size());
        return result;
    }

    /**
     * Validate a system configuration category DTO.
     *
     * @param dto
     *            the system configuration category DTO
     * @throws ValidationRuntimeException
     *             if validation fails
     */
    @Lock(LockType.READ)
    @RightsAllowed(UserRight._SYSTEM_CONFIGURATION)
    @Override
    public void validate(@Valid final SystemConfigurationCategoryDto dto) throws ValidationRuntimeException {
        LOGGER.debug("Validating SystemConfigurationCategoryDto: {}", dto);
        // No validation needed
    }

    /**
     * Fill or build a system configuration category entity from a DTO.
     *
     * @param source
     *            the source DTO
     * @param target
     *            the target entity
     * @param checkChangeDate
     *            whether to check the change date
     * @return the filled or built entity
     */
    @Override
    protected SystemConfigurationCategory fillOrBuildEntity(
        @NotNull final SystemConfigurationCategoryDto source,
        SystemConfigurationCategory target,
        final boolean checkChangeDate) {
        target = DtoHelper.fillOrBuildEntity(source, target, SystemConfigurationCategory::new, checkChangeDate);

        target.setName(source.getName());
        target.setDescription(source.getDescription());

        return target;
    }

    /**
     * Convert a system configuration category entity to a DTO.
     *
     * @param source
     *            the source entity
     * @return the converted DTO
     */
    @Override
    protected SystemConfigurationCategoryDto toDto(final SystemConfigurationCategory source) {
        if (source == null) {
            return null;
        }

        final SystemConfigurationCategoryDto target = new SystemConfigurationCategoryDto();
        DtoHelper.fillDto(target, source);

        target.setName(source.getName());
        target.setDescription(source.getDescription());
        target.setCaption(source.getCaption());

        return target;
    }

    /**
     * Convert a system configuration category entity to a reference DTO.
     *
     * @param source
     *            the source entity
     * @return the converted reference DTO
     */
    @Override
    protected SystemConfigurationCategoryReferenceDto toRefDto(final SystemConfigurationCategory source) {
        if (source == null) {
            return null;
        }

        return new SystemConfigurationCategoryReferenceDto(source.getUuid());
    }

    /**
     * Pseudonymize a system configuration category DTO.
     *
     * @param source
     *            the source entity
     * @param dto
     *            the DTO to pseudonymize
     * @param pseudonymizer
     *            the pseudonymizer to use
     * @param inJurisdiction
     *            whether the pseudonymization is in jurisdiction
     */
    @Override
    protected void pseudonymizeDto(
        final SystemConfigurationCategory source,
        final SystemConfigurationCategoryDto dto,
        final Pseudonymizer<SystemConfigurationCategoryDto> pseudonymizer,
        final boolean inJurisdiction) {
        // No anonymization required
        LOGGER.debug("Pseudonymizing SystemConfigurationCategory ignored: {}", source);
    }

    /**
     * Restore a pseudonymized system configuration category DTO.
     *
     * @param dto
     *            the DTO to restore
     * @param existingDto
     *            the existing DTO
     * @param entity
     *            the entity
     * @param pseudonymizer
     *            the pseudonymizer to use
     */
    @Override
    protected void restorePseudonymizedDto(
        final SystemConfigurationCategoryDto dto,
        final SystemConfigurationCategoryDto existingDto,
        final SystemConfigurationCategory entity,
        final Pseudonymizer<SystemConfigurationCategoryDto> pseudonymizer) {
        // No anonymization required
        LOGGER.debug("Restoring pseudonymized SystemConfigurationCategory ignored: {}", dto);
    }

    /**
     * Convert a system configuration category entity to an index DTO.
     *
     * @param entity
     *            the entity to convert
     * @return the converted index DTO
     */
    private SystemConfigurationCategoryIndexDto toIndexDto(final SystemConfigurationCategory entity) {
        if (entity == null) {
            return null;
        }

        final SystemConfigurationCategoryIndexDto dto = new SystemConfigurationCategoryIndexDto();
        DtoHelper.fillDto(dto, entity);

        dto.setName(entity.getName());
        dto.setDescription(entity.getDescription());
        dto.setCaption(entity.getCaption());

        return dto;
    }

    /**
     * Get the default system configuration category DTO.
     *
     * @return the default system configuration category DTO
     */
    @Lock(LockType.READ)
    @RightsAllowed(UserRight._SYSTEM_CONFIGURATION)
    @Override
    public SystemConfigurationCategoryDto getDefaultCategoryDto() {
        return toDto(service.getDefaultCategory());
    }

    /**
     * Get the default system configuration category reference DTO.
     *
     * @return the default system configuration category reference DTO
     */
    @Lock(LockType.READ)
    @RightsAllowed(UserRight._SYSTEM_CONFIGURATION)
    @Override
    public SystemConfigurationCategoryReferenceDto getDefaultCategoryReferenceDto() {
        return toRefDto(service.getDefaultCategory());
    }

    @LocalBean
    @Stateless
    public static class SystemConfigurationCategoryEjbLocal extends SystemConfigurationCategoryEjb {

    }
}
