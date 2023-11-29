/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2023 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package de.symeda.sormas.backend.manualmessagelog;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.validation.constraints.NotNull;

import de.symeda.sormas.api.manualmessagelog.MessageType;
import de.symeda.sormas.backend.common.BaseAdoService;
import de.symeda.sormas.backend.person.Person;
import de.symeda.sormas.backend.person.PersonJoins;
import de.symeda.sormas.backend.person.PersonService;
import de.symeda.sormas.backend.user.User;
import de.symeda.sormas.backend.user.UserService;

@Stateless
@LocalBean
public class ManualMessageLogService extends BaseAdoService<ManualMessageLog> {

	public static final int MANUAL_MESSAGE_LOG_LIMIT = 5;

    @EJB
    private UserService userService;
    @EJB
    private PersonService personService;

	public ManualMessageLogService() {
		super(ManualMessageLog.class);
	}

	public List<ManualMessageLog> getByPersonUuid(@NotNull String personUuid, MessageType messageType) {

		final CriteriaBuilder cb = em.getCriteriaBuilder();
		final CriteriaQuery<ManualMessageLog> cq = cb.createQuery(ManualMessageLog.class);
		final Root<ManualMessageLog> manualMessageLogRoot = cq.from(ManualMessageLog.class);

		final Predicate filter = cb.and(
			cb.equal(manualMessageLogRoot.get(ManualMessageLog.RECIPIENT_PERSON).get(Person.UUID), personUuid),
			cb.equal(manualMessageLogRoot.get(ManualMessageLog.MESSAGE_TYPE), messageType));

		cq.where(filter);
		cq.orderBy(cb.desc(manualMessageLogRoot.get(ManualMessageLog.SENT_DATE)));

		return em.createQuery(cq).setMaxResults(MANUAL_MESSAGE_LOG_LIMIT).getResultList();
	}

	public List<ManualMessageLog> getByPersonUuid(@NotNull String personUuid) {

		final CriteriaBuilder cb = em.getCriteriaBuilder();
		final CriteriaQuery<ManualMessageLog> cq = cb.createQuery(ManualMessageLog.class);
		final Root<ManualMessageLog> manualMessageLogRoot = cq.from(ManualMessageLog.class);

		cq.where(cb.equal(manualMessageLogRoot.get(ManualMessageLog.RECIPIENT_PERSON).get(Person.UUID), personUuid));
		cq.orderBy(cb.desc(manualMessageLogRoot.get(ManualMessageLog.SENT_DATE)));

		return em.createQuery(cq).setMaxResults(MANUAL_MESSAGE_LOG_LIMIT).getResultList();
	}

    public Predicate inJurisdictionOrOwned(CriteriaQuery<?> cq, CriteriaBuilder cb, Root<ManualMessageLog> root) {
        final User currentUser = userService.getCurrentUser();
        return new ManualMessageJurisdictionPredicateValidator(
                cq,
                cb,
                currentUser,
                new PersonJoins(root.join(ManualMessageLog.RECIPIENT_PERSON)),
                personService.getPermittedAssociations()).inJurisdictionOrOwned();
    }
}
