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
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.validation.constraints.NotNull;

import de.symeda.sormas.api.ReferenceDto;
import de.symeda.sormas.api.caze.CaseReferenceDto;
import de.symeda.sormas.api.contact.ContactReferenceDto;
import de.symeda.sormas.api.event.EventParticipantReferenceDto;
import de.symeda.sormas.api.manualmessagelog.ManualMessageLogFacade;
import de.symeda.sormas.api.manualmessagelog.ManualMessageLogIndexDto;
import de.symeda.sormas.api.manualmessagelog.MessageType;
import de.symeda.sormas.api.person.PersonReferenceDto;
import de.symeda.sormas.api.travelentry.TravelEntryReferenceDto;
import de.symeda.sormas.backend.common.CoreAdo;
import de.symeda.sormas.backend.person.Person;
import de.symeda.sormas.backend.user.User;
import de.symeda.sormas.backend.user.UserService;
import de.symeda.sormas.backend.util.JurisdictionHelper;
import de.symeda.sormas.backend.util.ModelConstants;
import de.symeda.sormas.backend.util.Pseudonymizer;

@Stateless(name = "ManualMessageLogFacade")
public class ManualMessageLogFacadeEjb implements ManualMessageLogFacade {

    @PersistenceContext(unitName = ModelConstants.PERSISTENCE_UNIT_NAME)
    protected EntityManager em;

    @EJB
    private ManualMessageLogService manualMessageLogService;
    @EJB
    private UserService userService;

    @Override
    public List<ManualMessageLogIndexDto> getEmailsByPersonAndCase(PersonReferenceDto person, CaseReferenceDto caze) {
        return getEmailsByPersonAndCoreEntity(person, ManualMessageLog.CASE, caze);
    }

    @Override
    public List<ManualMessageLogIndexDto> getEmailsPersonAndContact(PersonReferenceDto person, ContactReferenceDto contact) {
        return getEmailsByPersonAndCoreEntity(person, ManualMessageLog.CONTACT, contact);
    }

    @Override
    public List<ManualMessageLogIndexDto> getEmailsByPersonAndEventParticipant(
            PersonReferenceDto person,
            EventParticipantReferenceDto eventParticipant) {
        return getEmailsByPersonAndCoreEntity(person, ManualMessageLog.EVENT_PARTICIPANT, eventParticipant);
    }

    @Override
    public List<ManualMessageLogIndexDto> getEmailsByPersonAndTravelEntry(PersonReferenceDto person, TravelEntryReferenceDto travelEntry) {
        return getEmailsByPersonAndCoreEntity(person, ManualMessageLog.TRAVEL_ENTRY, travelEntry);
    }

    private List<ManualMessageLogIndexDto> getEmailsByPersonAndCoreEntity(
            PersonReferenceDto personRef,
            String coreEntityField,
            ReferenceDto corEntityRef) {
        return getAllByPersonAndCoreEntity(personRef, coreEntityField, corEntityRef, MessageType.EMAIL);
    }

    public List<ManualMessageLogIndexDto> getAllByPersonAndCoreEntity(
            @NotNull PersonReferenceDto personRef,
            String coreEntityField,
            ReferenceDto coreEntityRef,
            MessageType messageType) {

        final CriteriaBuilder cb = em.getCriteriaBuilder();
        final CriteriaQuery<ManualMessageLogIndexDto> cq = cb.createQuery(ManualMessageLogIndexDto.class);
        final Root<ManualMessageLog> root = cq.from(ManualMessageLog.class);

        ManualMessageLogJoins joins = new ManualMessageLogJoins(root);

        cq.multiselect(
                root.get(ManualMessageLog.UUID),
                root.get(ManualMessageLog.MESSAGE_TYPE),
                root.get(ManualMessageLog.SENT_DATE),
                joins.getSendingUser().get(User.UUID),
                joins.getSendingUser().get(User.FIRST_NAME),
                joins.getSendingUser().get(User.LAST_NAME),
                root.get(ManualMessageLog.EMAIL_ADDRESS),
                root.get(ManualMessageLog.USED_TEMPLATE),
                JurisdictionHelper.booleanSelector(cb, userService.inJurisdictionOrOwned(cb, joins.getSendungUserJoins())),
                JurisdictionHelper.booleanSelector(cb, manualMessageLogService.inJurisdictionOrOwned(cq, cb, root)));

        final Predicate filter = cb.and(
                cb.equal(root.get(ManualMessageLog.RECIPIENT_PERSON).get(Person.UUID), personRef.getUuid()),
                cb.equal(root.get(coreEntityField).get(CoreAdo.UUID), coreEntityRef.getUuid()),
                cb.equal(root.get(ManualMessageLog.MESSAGE_TYPE), messageType));

        cq.where(filter);
        cq.orderBy(cb.desc(root.get(ManualMessageLog.SENT_DATE)));

        List<ManualMessageLogIndexDto> resultList = em.createQuery(cq).getResultList();

        Pseudonymizer pseudonymizer = Pseudonymizer.getDefault(userService::hasRight);
        pseudonymizer.pseudonymizeDtoCollection(
                ManualMessageLogIndexDto.class,
                resultList,
                ManualMessageLogIndexDto::isInJurisdiction,
                (m, inJurisdiction) -> {
                    if (!m.isSenderInJurisdiction()) {
                        m.setSendingUser(null);
                    }
                });

        return resultList;
    }
}
