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

import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Validations;
import de.symeda.sormas.api.systemconfiguration.SystemConfigurationValueCriteria;
import de.symeda.sormas.api.systemconfiguration.SystemConfigurationValueDataProvider;
import de.symeda.sormas.api.systemconfiguration.SystemConfigurationValueDto;
import de.symeda.sormas.api.systemconfiguration.SystemConfigurationValueFacade;
import de.symeda.sormas.api.systemconfiguration.SystemConfigurationValueHelper;
import de.symeda.sormas.api.systemconfiguration.SystemConfigurationValueIndexDto;
import de.symeda.sormas.api.systemconfiguration.SystemConfigurationValueReferenceDto;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.api.utils.SortProperty;
import de.symeda.sormas.api.utils.ValidationRuntimeException;
import de.symeda.sormas.backend.common.AbstractBaseEjb;
import de.symeda.sormas.backend.common.AbstractDomainObject;
import de.symeda.sormas.backend.common.CriteriaBuilderHelper;
import de.symeda.sormas.backend.util.DtoHelper;
import de.symeda.sormas.backend.util.Pseudonymizer;
import de.symeda.sormas.backend.util.QueryHelper;
import de.symeda.sormas.backend.util.RightsAllowed;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.annotation.security.PermitAll;
import javax.ejb.DependsOn;
import javax.ejb.EJB;
import javax.ejb.Lock;
import javax.ejb.LockType;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.inject.Inject;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Optional;
import java.util.TreeMap;
import java.util.stream.Collectors;

/**
 * Implementation of the {@link SystemConfigurationValueFacade} interface.
 * Provides methods to manage system configuration settings.
 */
@Singleton(name = "SystemConfigurationValueFacade")
@Startup
@DependsOn("StartupShutdownService")
@TransactionManagement(TransactionManagementType.CONTAINER)
@RightsAllowed(UserRight._SYSTEM_CONFIGURATION)
public class SystemConfigurationValueEjb
        extends
        AbstractBaseEjb<SystemConfigurationValue, SystemConfigurationValueDto, SystemConfigurationValueIndexDto, SystemConfigurationValueReferenceDto, SystemConfigurationValueService, SystemConfigurationValueCriteria>
        implements SystemConfigurationValueFacade {

    private static final long serialVersionUID = 1L;

    private static final Logger LOGGER = LoggerFactory.getLogger(SystemConfigurationValueEjb.class);

    private final TreeMap<String, String> configurationValuesByKey = new TreeMap<>();

    private SystemConfigurationCategoryService categoryService;

    @EJB
    private SystemConfigurationCategoryEjb.SystemConfigurationCategoryEjbLocal categoryFacade;

    /**
     * Constructor for SystemConfigurationValueEjb.
     */
    public SystemConfigurationValueEjb() {
        super();
    }

    @PermitAll
    @Inject
    public void setCategoryService(final SystemConfigurationCategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @PermitAll
    @Inject
    public void setCategoryFacade(final SystemConfigurationCategoryEjb.SystemConfigurationCategoryEjbLocal categoryFacade) {
        this.categoryFacade = categoryFacade;
    }

    @PermitAll
    @Inject
    public void setService(final SystemConfigurationValueService service) {
        this.service = service;
    }

    /**
     * Retrieves a configuration value associated with the given key.
     *
     * @param key The key of the configuration value to retrieve.
     * @return An {@link Optional} containing the value if found, or an empty {@link Optional} if not found.
     */
    @PermitAll
    @Override
    public String getValue(final String key) {

        if (configurationValuesByKey.isEmpty()) {
            loadData();
        }

        return configurationValuesByKey.get(key);
    }

    @PermitAll
    public boolean exists(final String key) {
        return configurationValuesByKey.containsKey(key);
    }

    /**
     * Saves a system configuration value.
     *
     * @param dto The {@link SystemConfigurationValueDto} to save.
     * @return The saved {@link SystemConfigurationValueDto}.
     */
    @Lock(LockType.READ)
    @RightsAllowed(UserRight._SYSTEM_CONFIGURATION)
    @Override
    public SystemConfigurationValueDto save(final SystemConfigurationValueDto dto) {

        if (dto == null) {
            return null;
        }

        validate(dto);

        final SystemConfigurationValue existing = service.getByUuid(dto.getUuid());

        final SystemConfigurationValue newValue = fillOrBuildEntity(dto, existing, true);
        service.ensurePersisted(newValue);

        // Reset cache since values have been changed
        loadData();

        return toDto(newValue);
    }

    /**
     * Counts the number of system configuration values matching the given criteria.
     *
     * @param criteria The {@link SystemConfigurationValueCriteria} to match.
     * @return The count of matching system configuration values.
     */
    @Lock(LockType.READ)
    @RightsAllowed(UserRight._SYSTEM_CONFIGURATION)
    @Override
    public long count(final SystemConfigurationValueCriteria criteria) {

        final CriteriaBuilder cb = em.getCriteriaBuilder();
        final CriteriaQuery<Long> cq = cb.createQuery(Long.class);
        final Root<SystemConfigurationValue> root = cq.from(SystemConfigurationValue.class);
        final SystemConfigurationValueJoins joins = new SystemConfigurationValueJoins(root);

        final Predicate filter = CriteriaBuilderHelper.and(cb, service.buildCriteriaFilter(criteria, cb, root, joins));
        if (filter != null) {
            cq.where(filter);
        }

        cq.select(cb.count(root));
        return em.createQuery(cq).getSingleResult();
    }

    /**
     * Retrieves system configuration values by their UUIDs.
     *
     * @param uuids The list of UUIDs to retrieve.
     * @return A list of {@link SystemConfigurationValueDto} objects.
     */
    @Lock(LockType.READ)
    @RightsAllowed(UserRight._SYSTEM_CONFIGURATION)
    @Override
    public List<SystemConfigurationValueDto> getByUuids(final List<String> uuids) {
        return service.getByUuids(uuids).stream().map(this::toDto).collect(Collectors.toList());
    }

    /**
     * Retrieves all UUIDs of system configuration values.
     *
     * @return A list of UUIDs.
     */
    @Lock(LockType.READ)
    @RightsAllowed(UserRight._SYSTEM_CONFIGURATION)
    @Override
    public List<String> getAllUuids() {
        return service.getAllUuids();
    }

    /**
     * Retrieves a list of {@link SystemConfigurationValueIndexDto} objects for frontend filtering and display.
     * This method applies filtering, sorting, and pagination based on the provided criteria.
     *
     * @param criteria       The filtering criteria, or {@code null} for no filtering.
     * @param first          The index of the first result to retrieve (for pagination), or {@code null} to start from the beginning.
     * @param max            The maximum number of results to retrieve (for pagination), or {@code null} for no limit.
     * @param sortProperties The list of sort properties to apply to the results.
     * @return A list of {@link SystemConfigurationValueIndexDto} objects matching the criteria.
     */
    @Lock(LockType.READ)
    @RightsAllowed(UserRight._SYSTEM_CONFIGURATION)
    @Override
    public List<SystemConfigurationValueIndexDto> getIndexList(
            final SystemConfigurationValueCriteria criteria,
            final Integer first,
            final Integer max,
            final List<SortProperty> sortProperties) {

        LOGGER.debug(
                "Retrieving SystemConfigurationValueIndexDto list with criteria: {}, first: {}, max: {}, sortProperties: {}",
                criteria,
                first,
                max,
                sortProperties);
        final CriteriaBuilder cb = em.getCriteriaBuilder();
        final CriteriaQuery<SystemConfigurationValue> cq = cb.createQuery(SystemConfigurationValue.class);
        final Root<SystemConfigurationValue> root = cq.from(SystemConfigurationValue.class);
        final SystemConfigurationValueJoins joins = new SystemConfigurationValueJoins(root);

        Predicate filter = null;
        if (criteria != null) {
            filter = service.buildCriteriaFilter(criteria, cb, root, joins);
        }
        if (filter != null) {
            cq.where(filter);
        }
        cq.orderBy(cb.asc(root.get(SystemConfigurationValue.KEY_FIELD_NAME)), cb.asc(root.get(SystemConfigurationValue.VALUE_FIELD_NAME)));
        if (!sortProperties.isEmpty()) {
            final List<Order> order = sortProperties.stream().map(sortProperty -> {
                final Expression<?> expression;
                switch (sortProperty.propertyName) {
                    case SystemConfigurationValue.KEY_FIELD_NAME:
                        expression = cb.lower(root.get(sortProperty.propertyName));
                        break;
                    case SystemConfigurationValue.VALUE_FIELD_NAME:
                        expression = root.get(sortProperty.propertyName);
                        break;
                    case SystemConfigurationValue.DESCRIPTION:
                        expression = cb.lower(root.get(sortProperty.propertyName));
                        break;
                    default:
                        LOGGER.error("Invalid sort property {}.", sortProperty.propertyName);
                        throw new IllegalArgumentException(sortProperty.propertyName);
                }
                return sortProperty.ascending ? cb.asc(expression) : cb.desc(expression);
            }).collect(Collectors.toList());
            cq.orderBy(order);
        } else {
            cq.orderBy(cb.asc(root.get(SystemConfigurationValue.CATEGORY_FIELD_NAME)), cb.asc(root.get(AbstractDomainObject.ID)));
        }
        cq.select(root);
        final List<SystemConfigurationValueIndexDto> result = QueryHelper.getResultList(em, cq, first, max, this::toIndexDto);
        LOGGER.debug("Retrieved {} SystemConfigurationValueIndexDto objects", result.size());
        return result;

    }

    /**
     * Validates a {@link SystemConfigurationValueDto} data transfer object.
     * This method checks the validity of the key and value within the DTO using {@link SystemConfigurationValueHelper}.
     * The method also checks if the value matches the pattern if a pattern is provided.
     * If the value is encrypted, the validation for the value will not be performed.
     *
     * @param dto The {@link SystemConfigurationValueDto} data transfer object to validate.
     * @throws ValidationRuntimeException if the key or value in the DTO is invalid.
     */
    @RightsAllowed(UserRight._SYSTEM_CONFIGURATION)
    @Override
    public void validate(@Valid final SystemConfigurationValueDto dto) throws ValidationRuntimeException {

        LOGGER.debug("Validating SystemConfigurationValueDto: {}", dto);
        if (!SystemConfigurationValueHelper.isConfigurationKeyValid(dto.getKey())) {
            LOGGER.warn("Invalid key in SystemConfigurationValueDto: {}", dto);
            throw new ValidationRuntimeException(I18nProperties.getValidationError(Validations.systemConfigurationValueInvalidKey));
        }

        if (Boolean.TRUE.equals(dto.getEncrypt())) {
            LOGGER.debug("Not validating encrypted configuration value SystemConfigurationValueDto: {}", dto);
            return;
        }

        if ((null == dto.getOptional() || !dto.getOptional()) && !SystemConfigurationValueHelper.isConfigurationValueValid(dto.getValue())) {
            LOGGER.warn("Invalid value in SystemConfigurationValueDto: {}", dto);
            throw new ValidationRuntimeException(I18nProperties.getValidationError(Validations.systemConfigurationValueInvalidValue));
        }

        // Do not attempt to match patterns for optional values
        if ((null != dto.getOptional() && dto.getOptional()) && (null == dto.getValue() || dto.getValue().isBlank())) {
            return;
        }

        if (dto.getPattern() != null
                && !dto.getPattern().isBlank()
                && !SystemConfigurationValueHelper.isConfigurationValueMatchingPattern(dto.getValue(), dto.getPattern())) {
            LOGGER.warn("Invalid value in SystemConfigurationValueDto: {}", dto);

            String message = null;
            if (null != dto.getValidationMessage() && !dto.getValidationMessage().isEmpty()) {
                message = I18nProperties.getValidationError(dto.getValidationMessage().replaceFirst("i18n/", ""), dto.getValue());
            }
            if (null == message || message.isEmpty()) {
                message = I18nProperties.getValidationError(Validations.systemConfigurationValuePatternNotMatched, dto.getPattern());
            }
            throw new ValidationRuntimeException(message);
        }
    }

    /**
     * Loads system configuration data into the cache.
     * This method initializes the cache by fetching all system configuration values from the service,
     * clearing the existing cache, and then populating it with the retrieved data.
     * Duplicate keys will be replaced with the latest value retrieved from the service.
     */
    @PermitAll
    @Override
    @PostConstruct
    public void loadData() {

        LOGGER.info("Loading SystemConfiguration data into cache");
        configurationValuesByKey.clear();

        service.getAll().forEach(value -> configurationValuesByKey.put(value.getKey(), value.getValue()));

        LOGGER.info("SystemConfiguration data loaded into cache successfully");
    }

    /**
     * Converts a {@link SystemConfigurationValueDto} data transfer object to a {@link SystemConfigurationValue} entity.
     * If a target entity is provided, it will be filled with the data from the DTO; otherwise, a new entity will be created.
     *
     * @param source          The {@link SystemConfigurationValueDto} data transfer object to convert. Must not be {@code null}.
     * @param target          The target {@link SystemConfigurationValue} entity to fill, or {@code null} to create a new entity.
     * @param checkChangeDate Whether to check the change date during the fill or build process.
     * @return The filled or newly created {@link SystemConfigurationValue} entity.
     * @throws NullPointerException if the source DTO is {@code null}.
     */
    @Override
    protected SystemConfigurationValue fillOrBuildEntity(
            @NotNull final SystemConfigurationValueDto source,
            SystemConfigurationValue target,
            final boolean checkChangeDate) {

        target = DtoHelper.fillOrBuildEntity(source, target, SystemConfigurationValue::new, checkChangeDate);

        target.setKey(source.getKey());
        target.setValue(source.getValue());
        target.setDescription(source.getDescription());
        target.setCategory(categoryService.getByReferenceDto(source.getCategory()));
        target.setOptional(source.getOptional() != null ? source.getOptional() : Boolean.FALSE);
        target.setEncrypt(source.getEncrypt());
        target.setPattern(source.getPattern());
        target.setDataProvider(source.getDataProvider() != null ? source.getDataProvider().getClass().getName() : null);
        target.setValidationMessage(source.getValidationMessage());

        // other fields are only to be set from db init scripts

        return target;
    }

    /**
     * Converts a {@link SystemConfigurationValue} entity to a {@link SystemConfigurationValueDto} data transfer object.
     *
     * @param source The {@link SystemConfigurationValue} entity to convert.
     * @return The corresponding {@link SystemConfigurationValueDto} data transfer object, or {@code null} if the source entity is
     * {@code null}.
     */
    @Override
    protected SystemConfigurationValueDto toDto(final SystemConfigurationValue source) {

        if (source == null) {
            return null;
        }

        final SystemConfigurationValueDto target = new SystemConfigurationValueDto();
        DtoHelper.fillDto(target, source);

        target.setKey(source.getKey());
        target.setValue(source.getValue());
        target.setDescription(source.getDescription());
        target.setCategory(
                source.getCategory() != null
                        ? categoryFacade.getReferenceByUuid(source.getCategory().getUuid())
                        : categoryFacade.getDefaultCategoryReferenceDto());
        target.setOptional(source.getOptional() != null ? source.getOptional() : Boolean.FALSE);
        target.setPattern(source.getPattern());
        target.setEncrypt(source.getEncrypt());

        // Instantiate SystemConfigurationValueDataProvider based on class name
        if (source.getDataProvider() != null) {
            try {
                final Class<?> clazz = Class.forName(source.getDataProvider());
                final SystemConfigurationValueDataProvider dataProvider =
                        (SystemConfigurationValueDataProvider) clazz.getDeclaredConstructor().newInstance();
                target.setDataProvider(dataProvider);
            } catch (final Exception e) {
                LOGGER.error("Failed to instantiate SystemConfigurationValueDataProvider", e);
            }
        }

        target.setValidationMessage(source.getValidationMessage());

        return target;
    }

    /**
     * Converts a {@link SystemConfigurationValue} entity to a {@link SystemConfigurationValueReferenceDto}.
     *
     * @param source The {@link SystemConfigurationValue} entity to convert.
     * @return The corresponding {@link SystemConfigurationValueReferenceDto}, or {@code null} if the source is {@code null}.
     */
    @Override
    protected SystemConfigurationValueReferenceDto toRefDto(final SystemConfigurationValue source) {

        if (source == null) {
            return null;
        }

        return new SystemConfigurationValueReferenceDto(source.getUuid());
    }

    /**
     * Pseudonymizes a {@link SystemConfigurationValueDto} data transfer object.
     * This method is currently not required for system configuration values.
     *
     * @param source         The {@link SystemConfigurationValue} entity to pseudonymize.
     * @param dto            The {@link SystemConfigurationValueDto} data transfer object to pseudonymize.
     * @param pseudonymizer  The pseudonymizer to use.
     * @param inJurisdiction Whether the pseudonymization is within jurisdiction.
     */
    @Override
    protected void pseudonymizeDto(
            final SystemConfigurationValue source,
            final SystemConfigurationValueDto dto,
            final Pseudonymizer<SystemConfigurationValueDto> pseudonymizer,
            final boolean inJurisdiction) {
        LOGGER.debug("Pseudonymizing SystemConfigurationValue ignored: {}", source);
    }

    /**
     * Restores a pseudonymized {@link SystemConfigurationValueDto} data transfer object.
     * This method is currently not required for system configuration values.
     *
     * @param dto           The {@link SystemConfigurationValueDto} data transfer object to restore.
     * @param existingDto   The existing {@link SystemConfigurationValueDto} data transfer object.
     * @param entity        The {@link SystemConfigurationValue} entity.
     * @param pseudonymizer The pseudonymizer to use.
     */
    @Override
    protected void restorePseudonymizedDto(
            final SystemConfigurationValueDto dto,
            final SystemConfigurationValueDto existingDto,
            final SystemConfigurationValue entity,
            final Pseudonymizer<SystemConfigurationValueDto> pseudonymizer) {
        LOGGER.debug("Restoring pseudonymized SystemConfigurationValue ignored: {}", dto);
    }

    /**
     * Converts a {@link SystemConfigurationValue} entity to a {@link SystemConfigurationValueIndexDto}.
     *
     * @param entity The {@link SystemConfigurationValue} entity to convert.
     * @return The corresponding {@link SystemConfigurationValueIndexDto}, or {@code null} if the entity is {@code null}.
     */
    private SystemConfigurationValueIndexDto toIndexDto(final SystemConfigurationValue entity) {

        if (entity == null) {
            return null;
        }

        final SystemConfigurationValueIndexDto dto = new SystemConfigurationValueIndexDto();
        DtoHelper.fillDto(dto, entity);

        dto.setValue(entity.getValue());
        dto.setKey(entity.getKey());
        dto.setDescription(entity.getDescription());
        dto.setEncrypted(entity.getEncrypt()); // encrypt needed for list view
        dto.setCategoryName(entity.getCategory() != null ? entity.getCategory().getName() : null);
        dto.setCategoryCaption(entity.getCategory() != null ? entity.getCategory().getCaption() : null);
        dto.setCategoryDescription(entity.getCategory() != null ? entity.getCategory().getDescription() : null);

        return dto;
    }

}
