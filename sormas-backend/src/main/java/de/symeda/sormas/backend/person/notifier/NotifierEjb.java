/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2026 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package de.symeda.sormas.backend.person.notifier;

import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.security.PermitAll;
import javax.ejb.LocalBean;
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

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Validations;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.person.notifier.NotifierCriteria;
import de.symeda.sormas.api.person.notifier.NotifierDto;
import de.symeda.sormas.api.person.notifier.NotifierFacade;
import de.symeda.sormas.api.person.notifier.NotifierIndexDto;
import de.symeda.sormas.api.person.notifier.NotifierReferenceDto;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.api.utils.SortProperty;
import de.symeda.sormas.api.utils.ValidationRuntimeException;
import de.symeda.sormas.backend.common.AbstractBaseEjb;
import de.symeda.sormas.backend.common.CriteriaBuilderHelper;
import de.symeda.sormas.backend.util.DtoHelper;
import de.symeda.sormas.backend.util.Pseudonymizer;
import de.symeda.sormas.backend.util.QueryHelper;
import de.symeda.sormas.backend.util.RightsAllowed;

/**
 * Implementation of the {@link NotifierFacade} interface.
 * Provides methods to manage notifier entities.
 */
@Stateless(name = "NotifierFacade")
@RightsAllowed(UserRight._CASE_EDIT)
public class NotifierEjb extends AbstractBaseEjb<Notifier, NotifierDto, NotifierIndexDto, NotifierReferenceDto, NotifierService, NotifierCriteria>
    implements NotifierFacade {

    @LocalBean
    @Stateless
    public static class NotifierEjbLocal extends NotifierEjb {

    }

    private static final long serialVersionUID = 1L;

    private static final Logger LOGGER = LoggerFactory.getLogger(NotifierEjb.class);

    @PermitAll
    @Inject
    public void setService(final NotifierService service) {
        this.service = service;
    }

    /**
     * Retrieves notifier entities by their UUIDs.
     *
     * @param uuids
     *            The list of UUIDs.
     * @return The list of matching notifier DTOs.
     */
    @PermitAll
    @Override
    public List<NotifierDto> getByUuids(List<String> uuids) {
        return service.getByUuids(uuids).stream().map(this::toDto).collect(Collectors.toList());
    }

    /**
     * Saves a notifier entity.
     *
     * @param dto
     *            The notifier DTO to save.
     * @return The saved notifier DTO.
     */
    @RightsAllowed(UserRight._CASE_EDIT)
    @Override
    public NotifierDto save(@Valid @NotNull NotifierDto dto) {

        validate(dto);
        Notifier existing = service.getByUuid(dto.getUuid());
        Notifier notifier = fillOrBuildEntity(dto, existing, true);
        service.ensurePersisted(notifier);
        return toDto(notifier);
    }

    @Override
    public void validate(@Valid NotifierDto source) {

        if (StringUtils.isBlank(source.getRegistrationNumber())) {
            throw new ValidationRuntimeException(I18nProperties.getValidationError(Validations.specifyRegistrationNumber));
        }
        if (source.getEmail() != null && !source.getEmail().isBlank() && !DataHelper.isValidEmailAddress(source.getEmail())) {
            throw new ValidationRuntimeException(
                I18nProperties.getValidationError(
                    Validations.validEmailAddress,
                    I18nProperties.getPrefixCaption(PersonDto.I18N_PREFIX, PersonDto.EMAIL_ADDRESS)));
        }
        if (source.getPhone() != null && !source.getPhone().isBlank() && !DataHelper.isValidPhoneNumber(source.getPhone())) {
            throw new ValidationRuntimeException(
                I18nProperties
                    .getValidationError(Validations.validPhoneNumber, I18nProperties.getPrefixCaption(PersonDto.I18N_PREFIX, PersonDto.PHONE)));
        }
    }

    @Override
    public long count(NotifierCriteria criteria) {

        final CriteriaBuilder cb = em.getCriteriaBuilder();
        final CriteriaQuery<Long> cq = cb.createQuery(Long.class);
        final Root<Notifier> root = cq.from(Notifier.class);

        if (criteria != null) {
            final Predicate filter = CriteriaBuilderHelper.and(cb, service.buildCriteriaFilter(criteria, cb, root));
            if (filter != null) {
                cq.where(filter);
            }
        }

        cq.select(cb.count(root));
        return em.createQuery(cq).getSingleResult();
    }

    /**
     * Retrieves a list of NotifierIndexDto objects based on the specified criteria, pagination, and sorting properties.
     *
     * @param criteria
     *            The criteria to filter the notifiers.
     * @param first
     *            The starting index for pagination.
     * @param max
     *            The maximum number of results to retrieve.
     * @param sortProperties
     *            The list of properties to sort the results by.
     * @return A list of NotifierIndexDto objects matching the criteria.
     */
    @Override
    public List<NotifierIndexDto> getIndexList(NotifierCriteria criteria, Integer first, Integer max, List<SortProperty> sortProperties) {

        LOGGER.debug(
            "Retrieving NotifierIndexDto list with criteria: {}, first: {}, max: {}, sortProperties: {}",
            criteria,
            first,
            max,
            sortProperties);
        final CriteriaBuilder cb = em.getCriteriaBuilder();
        final CriteriaQuery<Notifier> cq = cb.createQuery(Notifier.class);
        final Root<Notifier> root = cq.from(Notifier.class);

        Predicate filter = null;
        if (criteria != null) {
            filter = service.buildCriteriaFilter(criteria, cb, root);
        }
        if (filter != null) {
            cq.where(filter);
        }
        cq.orderBy(cb.asc(root.get(Notifier.FIRST_NAME_FIELD_NAME)), cb.asc(root.get(Notifier.LAST_NAME_FIELD_NAME)));
        if (!sortProperties.isEmpty()) {
            final List<Order> order = sortProperties.stream().map(sortProperty -> {
                final Expression<?> expression;
                switch (sortProperty.propertyName) {
                case Notifier.FIRST_NAME_FIELD_NAME:
                    expression = cb.lower(root.get(sortProperty.propertyName));
                    break;
                case Notifier.LAST_NAME_FIELD_NAME:
                    expression = root.get(sortProperty.propertyName);
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
        final List<NotifierIndexDto> result = QueryHelper.getResultList(em, cq, first, max, this::toIndexDto);
        LOGGER.debug("Retrieved {} NotifierIndexDto objects", result.size());
        return result;
    }

    /**
     * Retrieves a notifier entity by its registration number.
     *
     * @param registrationNumber
     *            The registration number of the notifier.
     * @return The corresponding NotifierDto, or null if no notifier is found.
     */
    public NotifierDto getByRegistrationNumber(String registrationNumber) {

        if (StringUtils.isBlank(registrationNumber)) {
            return null;
        }
        List<NotifierDto> notifiers =
            service.getByPredicate((cb, root, cq) -> cb.equal(root.get(Notifier.REGISTRATION_NUMBER_FIELD_NAME), registrationNumber))
                .stream()
                .map(this::toPseudonymizedDto)
                .collect(Collectors.toList());
        if (notifiers.size() > 1) {
            LOGGER.error("Multiple notifiers found with the same registration number: {}", registrationNumber);
            throw new IllegalStateException("Multiple notifiers found with the same registration number: " + registrationNumber);
        }
        return notifiers.isEmpty() ? null : notifiers.get(0);
    }

    /**
     * Updates a notifier entity based on its registration number and returns the updated notifier.
     * If a notifier with the given registration number does not exist, it creates a new one.
     *
     * @param notifierDto
     *            The notifier DTO containing the data to update or create the notifier.
     * @return The updated or newly created NotifierDto.
     */
    @Override
    @PermitAll
    public NotifierDto updateAndGetByRegistrationNumber(NotifierDto notifierDto) {
        if (notifierDto == null || notifierDto.getRegistrationNumber() == null || notifierDto.getRegistrationNumber().isBlank()) {
            throw new IllegalArgumentException("NotifierDto or its registration number cannot be null or empty");
        }

        final NotifierDto existingNotifier = getByRegistrationNumber(notifierDto.getRegistrationNumber());
        if (existingNotifier == null) {
            return save(notifierDto);
        }

        if (areNotifierIdentical(notifierDto, existingNotifier)) {
            return existingNotifier;
        }
        existingNotifier.setFirstName(notifierDto.getFirstName());
        existingNotifier.setLastName(notifierDto.getLastName());
        existingNotifier.setAddress(notifierDto.getAddress());
        existingNotifier.setPhone(notifierDto.getPhone());
        existingNotifier.setEmail(notifierDto.getEmail());
        existingNotifier.setAgentLastName(notifierDto.getAgentLastName());
        existingNotifier.setAgentFirstName(notifierDto.getAgentFirstName());
        return save(existingNotifier);
    }

    protected boolean areNotifierIdentical(NotifierDto notifierDto, NotifierDto existingNotifier) {
        return (notifierDto.getFirstName() == null || notifierDto.getFirstName().equals(existingNotifier.getFirstName()))
            && (notifierDto.getLastName() == null || notifierDto.getLastName().equals(existingNotifier.getLastName()))
            && (notifierDto.getAddress() == null || notifierDto.getAddress().equals(existingNotifier.getAddress()))
            && (notifierDto.getPhone() == null || notifierDto.getPhone().equals(existingNotifier.getPhone()))
            && (notifierDto.getEmail() == null || notifierDto.getEmail().equals(existingNotifier.getEmail()))
            && (notifierDto.getAgentFirstName() == null || notifierDto.getAgentFirstName().equals(existingNotifier.getAgentFirstName()))
            && (notifierDto.getAgentLastName() == null || notifierDto.getAgentLastName().equals(existingNotifier.getAgentLastName()));
    }

    /**
     * Updates a notifier entity based on its registration number.
     * If a notifier with the given registration number does not exist, it creates a new one.
     *
     * @param notifierDto
     *            The notifier DTO containing the data to update or create the notifier.
     */
    @PermitAll
    public void updateByRegistrationNumber(NotifierDto notifierDto) {
        updateAndGetByRegistrationNumber(notifierDto);
    }

    @Override
    @PermitAll
    public NotifierReferenceDto updateAndGetReferenceByRegistrationNumber(NotifierDto notifierDto) {
        final NotifierDto updatedNotifier = updateAndGetByRegistrationNumber(notifierDto);
        return toRefDto(service.getByUuidAndTime(updatedNotifier.getUuid(), updatedNotifier.getChangeDate().toInstant()));
    }

    /**
     * Fills an existing Notifier entity or builds a new one based on the provided NotifierDto.
     *
     * @param source
     *            The NotifierDto containing the data to populate the entity.
     * @param target
     *            The existing Notifier entity to update, or null to create a new one.
     * @param checkChangeDate
     *            Whether to check the change date for updates.
     * @return The populated Notifier entity.
     */
    @Override
    protected Notifier fillOrBuildEntity(@NotNull NotifierDto source, Notifier target, boolean checkChangeDate) {

        target = DtoHelper.fillOrBuildEntity(source, target, Notifier::new, checkChangeDate);
        target.setRegistrationNumber(source.getRegistrationNumber());
        target.setFirstName(source.getFirstName());
        target.setLastName(source.getLastName());
        target.setAddress(source.getAddress());
        target.setPhone(source.getPhone());
        target.setEmail(source.getEmail());
        target.setAgentFirstName(source.getAgentFirstName());
        target.setAgentLastName(source.getAgentLastName());
        return target;
    }

    /**
     * Converts a Notifier entity to a NotifierDto.
     *
     * @param source
     *            The Notifier entity to convert.
     * @return The corresponding NotifierDto, or null if the source is null.
     */
    @Override
    protected NotifierDto toDto(Notifier source) {

        if (source == null) {
            return null;
        }

        return DtoHelper.createAndFillDto(NotifierDto::new, source, target -> {
            target.setRegistrationNumber(source.getRegistrationNumber());
            target.setFirstName(source.getFirstName());
            target.setLastName(source.getLastName());
            target.setAddress(source.getAddress());
            target.setPhone(source.getPhone());
            target.setEmail(source.getEmail());
            target.setAgentFirstName(source.getAgentFirstName());
            target.setAgentLastName(source.getAgentLastName());
        });
    }

    /**
     * Converts a Notifier entity to a NotifierIndexDto.
     *
     * @param source
     *            The Notifier entity to convert.
     * @return The corresponding NotifierIndexDto, or null if the source is null.
     */
    protected NotifierIndexDto toIndexDto(Notifier source) {

        if (source == null) {
            return null;
        }
        NotifierIndexDto target = new NotifierIndexDto(source.getUuid());
        target.setFirstName(source.getFirstName());
        target.setLastName(source.getLastName());
        target.setRegistrationNumber(source.getRegistrationNumber());
        target.setAddress(source.getAddress());
        target.setPhone(source.getPhone());
        target.setEmail(source.getEmail());
        return target;
    }

    /**
     * Retrieves a NotifierReferenceDto for a given UUID.
     * The NotifierReferenceDto will contain the most recent change date of the notifier.
     *
     * @param uuid
     *            The UUID of the notifier.
     * @return The corresponding NotifierReferenceDto, or null if no matching notifier is found.
     */
    public NotifierReferenceDto getVersionReferenceByUuidAndDate(String uuid) {
        return getVersionReferenceByUuidAndDate(uuid, null);
    }

    /**
     * Retrieves a NotifierReferenceDto for a given UUID and version date.
     *
     * @param uuid
     *            The UUID of the notifier.
     * @param versionDate
     *            The version date to retrieve the notifier reference for.
     * @return The corresponding NotifierReferenceDto, or null if no matching notifier is found.
     */
    public NotifierReferenceDto getVersionReferenceByUuidAndDate(String uuid, Date versionDate) {

        if (uuid == null || uuid.isBlank()) {
            return null;
        }

        if (versionDate == null) {
            return NotifierDtoHelper.toVersionReferenceDto(service.getByUuid(uuid), null);
        }

        Notifier source = service.getByUuidAndTime(uuid, versionDate.toInstant());
        return NotifierDtoHelper.toVersionReferenceDto(source, versionDate);
    }

    @Override
    protected NotifierReferenceDto toRefDto(Notifier source) {

        if (source == null) {
            return null;
        }
        return NotifierDtoHelper.toReferenceDto(source);
    }

    @Override
    protected void pseudonymizeDto(Notifier source, NotifierDto dto, Pseudonymizer<NotifierDto> pseudonymizer, boolean inJurisdiction) {
        // FIXME Auto-generated method stub
    }

    @Override
    protected void restorePseudonymizedDto(NotifierDto dto, NotifierDto existingDto, Notifier entity, Pseudonymizer<NotifierDto> pseudonymizer) {
        // FIXME Auto-generated method stub
    }

    @Override
    public NotifierDto getByUuidAndTime(String uuid, Instant time) {

        final Notifier notifier = service.getByUuidAndTime(uuid, time);
        return toPseudonymizedDto(notifier);
    }

}
