/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2026 SORMAS Foundation gGmbH
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

package de.symeda.sormas.backend.audit;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.metamodel.EntityType;
import javax.transaction.Transactional;

import de.symeda.sormas.api.audit.AuditIgnore;
import de.symeda.sormas.backend.common.AbstractDomainObject;
import de.symeda.sormas.backend.util.ModelConstants;

/**
 * Stateless service for updating audit records in a new transaction context.
 * 
 * This service provides functionality to update entity audit information
 * such as change user and change date in a separate transaction. This ensures
 * that audit updates are isolated from the main business transaction and
 * can be committed independently.
 * 
 * <p>
 * The service operates with:
 * <ul>
 * <li>A new transaction context to avoid interfering with main business logic</li>
 * <li>Direct JPQL updates for efficient audit record modifications</li>
 * <li>JPA metamodel introspection to resolve entity names</li>
 * </ul>
 */
@Stateless
@AuditIgnore
public class AuditRecordService {

    //@formatter:off
    private static final String JPQL_UPDATE_AUDIT_RECORD = "UPDATE %s e " 
                                                            + "SET e." + AbstractDomainObject.CHANGE_USER + ".id = :changeUserId " 
                                                            + "WHERE e.id = :entityId";
    //@formatter:on

    @PersistenceContext(unitName = ModelConstants.PERSISTENCE_UNIT_NAME)
    private EntityManager em;

    /**
     * Updates the audit record for a specific entity in a new transaction.
     * 
     * This method executes a JPQL update query to set the changeUser.id and
     * changeDate fields for the specified entity. The operation runs in a
     * new transaction context to ensure audit updates are isolated from
     * the main business transaction.
     * 
     * <p>
     * The method:
     * <ul>
     * <li>Sets the change user ID to the specified value</li>
     * <li>Sets the change date to the current system time</li>
     * <li>Only updates the entity with the matching ID</li>
     * <li>Skips the update if entityId or changeUserId is null</li>
     * </ul>
     *
     * @param <T>
     *            the type of the entity
     * @param entityClass
     *            the class of the entity to update
     * @param entityId
     *            the ID of the entity to update
     * @param changeUserId
     *            the ID of the user making the change
     */
    @Transactional(Transactional.TxType.REQUIRES_NEW)
    public <T> void updateAuditRecord(Class<T> entityClass, Long entityId, Long changeUserId) {
        if (entityId == null || changeUserId == null) {
            return;
        }
        final String jpqlUpdate = String.format(JPQL_UPDATE_AUDIT_RECORD, getEntityName(entityClass));

        final Query updateQuery = em.createQuery(jpqlUpdate);
        updateQuery.setParameter("changeUserId", changeUserId);
        updateQuery.setParameter("entityId", entityId);
        updateQuery.executeUpdate();
    }

    /**
     * Retrieves the JPA entity name for a given entity class.
     * 
     * This method searches through the JPA metamodel to find the entity name
     * that should be used in JPQL queries. The entity name may differ from
     * the simple class name if explicitly specified in JPA annotations.
     *
     * @param entityClass
     *            the entity class to get the name for
     * @return the entity name used in JPQL queries
     * @throws IllegalArgumentException
     *             if the class is not a recognized JPA entity
     */
    private String getEntityName(Class<?> entityClass) {
        for (EntityType<?> entityType : em.getMetamodel().getEntities()) {
            if (entityType.getJavaType().equals(entityClass)) {
                return entityType.getName(); // This returns the name used in JPQL
            }
        }
        throw new IllegalArgumentException("Class " + entityClass.getName() + " is not a recognized JPA entity.");
    }
}
