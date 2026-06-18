/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2025 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

package de.symeda.sormas.api.person.notifier;

import java.time.Instant;
import java.util.Date;

import javax.ejb.Remote;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import de.symeda.sormas.api.BaseFacade;

@Remote
public interface NotifierFacade extends BaseFacade<NotifierDto, NotifierIndexDto, NotifierReferenceDto, NotifierCriteria> {

    /**
     * Save a notifier entity.
     *
     * @param notifierDto
     *            The notifier data transfer object to save.
     * @return The saved notifier DTO.
     */
    NotifierDto save(@Valid @NotNull NotifierDto notifierDto);

    /**
     * Get a notifier by its unique identifier.
     *
     * @param uuid
     *            The unique identifier of the notifier.
     * @return The notifier DTO.
     */
    NotifierDto getByUuid(String uuid);

    /**
     * Get a notifier values from history at a specific time.
     * This method should return the notifier values at the specified time or current if no history is found.
     *
     * @param uuid
     *            The unique identifier of the notifier.
     * @param time
     *            The specific time to filter the notifier.
     * @return The notifier DTO.
     */
    NotifierDto getByUuidAndTime(String uuid, Instant time);

    /**
     * Get a notifier by its registration number.
     *
     * @param registrationNumber
     *            The registration number of the notifier.
     * @return The notifier DTO.
     */
    NotifierDto getByRegistrationNumber(String registrationNumber);

    /**
     * Update a notifier by its registration number and return the updated notifier.
     * The persistence update should only be done if there are differences between the existing and new values.
     * The persistence update should also be done if there is no existing notifier with the given registration number.
     *
     * @param notifierDto
     *            The notifier data transfer object containing updated information.
     * @return The updated notifier DTO.
     */
    NotifierDto updateAndGetByRegistrationNumber(NotifierDto notifierDto);

    /**
     * Update a notifier by its registration number and return the updated notifier reference.
     * The persistence update should only be done if there are differences between the existing and new values.
     * The persistence update should also be done if there is no existing notifier with the given registration number.
     *
     * @param notifierDto
     *            The notifier data transfer object containing updated information.
     * @return The updated notifier reference DTO.
     */
    NotifierReferenceDto updateAndGetReferenceByRegistrationNumber(NotifierDto notifierDto);

    /**
     * Update a notifier by its registration number.
     * The persistence update should only be done if there are differences between the existing and new values.
     * The persistence update should also be done if there is no existing notifier with the given registration number.
     *
     * @param notifierDto
     *            The notifier data transfer object containing updated information.
     */
    void updateByRegistrationNumber(NotifierDto notifierDto);

    /**
     * Retrieves a NotifierReferenceDto for a given UUID.
     * The NotifierReferenceDto should contain the most recent change date of the notifier.
     *
     * @param uuid
     *            The UUID of the notifier.
     * @return The corresponding NotifierReferenceDto, or null if no matching notifier is found.
     */
    NotifierReferenceDto getVersionReferenceByUuidAndDate(String uuid);

    /**
     * Get a version reference of a notifier by its unique identifier and a specific date.
     * The NotifierReferenceDto should contain the change date of the notifier.
     * If there is no history entry for the given date, the most recent change date should be used.
     *
     * @param uuid
     *            The unique identifier of the notifier.
     * @param versionDate
     *            The specific date to filter the version reference.
     * @return The notifier reference DTO.
     */
    NotifierReferenceDto getVersionReferenceByUuidAndDate(String uuid, Date versionDate);

}
