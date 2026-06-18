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

import java.sql.Timestamp;
import java.time.Instant;
import java.util.Date;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.From;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import de.symeda.sormas.api.person.notifier.NotifierCriteria;
import de.symeda.sormas.api.person.notifier.NotifierReferenceDto;
import de.symeda.sormas.backend.common.AdoServiceWithUserFilterAndJurisdiction;

@Stateless
@LocalBean
public class NotifierService extends AdoServiceWithUserFilterAndJurisdiction<Notifier> {

    @PersistenceContext
    private EntityManager em;

    public NotifierService() {
        super(Notifier.class);
    }

    public Notifier createNotifier() {
        return new Notifier();
    }

    @Override
    public Predicate createUserFilter(CriteriaBuilder cb, CriteriaQuery cq, From<?, Notifier> from) {
        return null;
    }

    /**
     * Builds a criteria filter based on the provided criteria.
     *
     * @param criteria
     *            the criteria
     * @param cb
     *            the criteria builder
     * @param from
     *            the root
     * @return the predicate
     */
    public Predicate buildCriteriaFilter(NotifierCriteria criteria, CriteriaBuilder cb, Root<Notifier> from) {
        Predicate filter = cb.conjunction();
        return filter;
    }

    /**
     * Retrieves a Notifier entity based on its UUID and a specific timestamp.
     * If the timestamp is null, it retrieves the latest version of the Notifier by UUID.
     *
     * @param uuid
     *            the unique identifier of the Notifier
     * @param time
     *            the timestamp to filter the Notifier history; if null, retrieves the latest version
     * @return the Notifier entity matching the UUID and timestamp, or null if no match is found
     */
    public Notifier getByUuidAndTime(String uuid, Instant time) {
        if (uuid == null) {
            return null;
        }

        if (time == null) {
            return getByUuid(uuid);
        }

        final Timestamp timestamp = Timestamp.from(time);

        final String sql = "SELECT " + "a.id AS id, a.uuid AS uuid," + "COALESCE(b.changedate, a.changedate) AS changedate,"
            + "a.creationdate AS creationdate, a.change_user_id AS change_user_id,"
            + "COALESCE(b.registrationnumber, a.registrationnumber) AS registrationnumber," + "COALESCE(b.firstname, a.firstname) AS firstname,"
            + "COALESCE(b.lastname, a.lastname) AS lastname," + "COALESCE(b.address, a.address) AS address," + "COALESCE(b.email, a.email) AS email,"
            + "COALESCE(b.phone, a.phone) AS phone, "+ "COALESCE(b.agentfirstname, a.agentfirstname) AS agentfirstname, "
            + "COALESCE(b.agentlastname, a.agentlastname) AS agentlastname "+ "FROM notifier a " + "LEFT JOIN " + "(SELECT * FROM notifier_history "
            + "WHERE uuid = :uuid AND changedate <= CAST(:changeDate AS TIMESTAMP) " + "ORDER BY changedate DESC LIMIT 1" + ") b ON a.uuid = b.uuid "
            + "WHERE a.uuid = :uuid";

        Query query = em.createNativeQuery(sql, Notifier.class);
        query.setParameter("uuid", uuid);
        query.setParameter("changeDate", timestamp);

        return (Notifier) query.getSingleResult();
    }

    public Notifier getVersionByReferenceDto(NotifierReferenceDto dto) {
        if (dto == null || dto.getUuid() == null) {
            return null;
        }
        if (dto.getVersionDate() == null) {
            return getByUuid(dto.getUuid());
        }
        return getByUuidAndTime(dto.getUuid(), dto.getVersionDate().toInstant());
    }

    public Date getVersionDateByReferenceDto(NotifierReferenceDto dto) {
        if (dto == null || dto.getUuid() == null) {
            return null;
        }
        if (dto.getVersionDate() == null) {
            final Notifier notifier = getByUuid(dto.getUuid());
            if (notifier == null) {
                return null;
            }
            return notifier.getChangeDate();
        }
        final Notifier notifier = getByUuidAndTime(dto.getUuid(), dto.getVersionDate().toInstant());
        if (notifier == null) {
            return null;
        }
        return notifier.getChangeDate();
    }

}
