/*******************************************************************************
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2018 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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
 *******************************************************************************/
package de.symeda.sormas.backend.visit;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.validation.constraints.NotNull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.symeda.sormas.api.contact.ContactReferenceDto;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.person.PersonReferenceDto;
import de.symeda.sormas.api.symptoms.SymptomsHelper;
import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.api.utils.SortProperty;
import de.symeda.sormas.api.visit.DashboardVisitDto;
import de.symeda.sormas.api.visit.VisitCriteria;
import de.symeda.sormas.api.visit.VisitDto;
import de.symeda.sormas.api.visit.VisitFacade;
import de.symeda.sormas.api.visit.VisitIndexDto;
import de.symeda.sormas.api.visit.VisitReferenceDto;
import de.symeda.sormas.api.visit.VisitStatus;
import de.symeda.sormas.backend.caze.Case;
import de.symeda.sormas.backend.common.MessageType;
import de.symeda.sormas.backend.common.MessagingService;
import de.symeda.sormas.backend.common.NotificationDeliveryFailedException;
import de.symeda.sormas.backend.contact.Contact;
import de.symeda.sormas.backend.contact.ContactService;
import de.symeda.sormas.backend.person.Person;
import de.symeda.sormas.backend.person.PersonFacadeEjb;
import de.symeda.sormas.backend.person.PersonService;
import de.symeda.sormas.backend.symptoms.Symptoms;
import de.symeda.sormas.backend.symptoms.SymptomsFacadeEjb;
import de.symeda.sormas.backend.symptoms.SymptomsFacadeEjb.SymptomsFacadeEjbLocal;
import de.symeda.sormas.backend.user.User;
import de.symeda.sormas.backend.user.UserFacadeEjb;
import de.symeda.sormas.backend.user.UserService;
import de.symeda.sormas.backend.util.DtoHelper;
import de.symeda.sormas.backend.util.ModelConstants;

@Stateless(name = "VisitFacade")
public class VisitFacadeEjb implements VisitFacade {

	@PersistenceContext(unitName = ModelConstants.PERSISTENCE_UNIT_NAME)
	protected EntityManager em;

	@EJB
	private VisitService visitService;	
	@EJB
	private ContactService contactService;
	@EJB
	private PersonService personService;
	@EJB
	private UserService userService;
	@EJB
	private SymptomsFacadeEjbLocal symptomsFacade;
	@EJB
	private MessagingService messagingService;

	private static final Logger logger = LoggerFactory.getLogger(VisitFacadeEjb.class);

	@Override
	public List<String> getAllActiveUuids(String userUuid) {
		User user = userService.getByUuid(userUuid);

		if (user == null) {
			return Collections.emptyList();
		}

		return visitService.getAllActiveUuids(user);
	}

	@Override
	public List<VisitDto> getAllActiveVisitsAfter(Date date, String userUuid) {
		User user = userService.getByUuid(userUuid);

		if (user == null) {
			return Collections.emptyList();
		}

		return visitService.getAllActiveVisitsAfter(date, user).stream()
				.map(c -> toDto(c))
				.collect(Collectors.toList());
	}

	@Override
	public List<VisitDto> getByUuids(List<String> uuids) {
		return visitService.getByUuids(uuids)
				.stream()
				.map(c -> toDto(c))
				.collect(Collectors.toList());
	}

	@Override
	public List<VisitDto> getAllByContact(ContactReferenceDto contactRef) {

		Contact contact = contactService.getByReferenceDto(contactRef);

		return visitService.getAllByContact(contact).stream()
				.map(c -> toDto(c))
				.collect(Collectors.toList());
	}

	@Override
	public List<VisitDto> getAllByPerson(PersonReferenceDto personRef) {

		Person person = personService.getByReferenceDto(personRef);

		return visitService.getAllByPerson(person).stream()
				.map(c -> toDto(c))
				.collect(Collectors.toList());
	}

	@Override
	public VisitDto getVisitByUuid(String uuid) {
		return toDto(visitService.getByUuid(uuid));
	}

	@Override
	public VisitReferenceDto getReferenceByUuid(String uuid) {
		return toReferenceDto(visitService.getByUuid(uuid));
	}

	@Override
	public VisitDto saveVisit(VisitDto dto) {
		VisitDto existingVisit = toDto(visitService.getByUuid(dto.getUuid()));

		SymptomsHelper.updateIsSymptomatic(dto.getSymptoms());
		Visit entity = fromDto(dto);
		visitService.ensurePersisted(entity);

		onVisitChanged(existingVisit, entity);

		return toDto(entity);
	}

	@Override
	public void deleteVisit(VisitReferenceDto visitRef, String userUuid) {
		User user = userService.getByUuid(userUuid);
		if (!user.getUserRoles().contains(UserRole.ADMIN)) {
			throw new UnsupportedOperationException("Only admins are allowed to delete entities.");
		}

		Visit visit = visitService.getByReferenceDto(visitRef);
		visitService.delete(visit);
	}

	@Override
	public int getNumberOfVisits(ContactReferenceDto contactRef, VisitStatus visitStatus) {
		Contact contact = contactService.getByReferenceDto(contactRef);

		return visitService.getVisitCount(contact, null);
	}

	@Override
	public List<DashboardVisitDto> getDashboardVisitsByContact(ContactReferenceDto contactRef, Date from, Date to) {
		Contact contact = contactService.getByReferenceDto(contactRef);

		return visitService.getDashboardVisitsByContact(contact, from, to);
	}

	@Override
	public List<VisitIndexDto> getIndexList(VisitCriteria visitCriteria, int first, int max, List<SortProperty> sortProperties) {
		if (visitCriteria == null || visitCriteria.getContact() == null) {
			return new ArrayList<>(); // Retrieving an index list independent of a contact is not possible
		}

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<VisitIndexDto> cq = cb.createQuery(VisitIndexDto.class);
		Root<Visit> visit = cq.from(Visit.class);

		Join<Visit, Symptoms> symptoms = visit.join(Visit.SYMPTOMS, JoinType.LEFT);

		cq.multiselect(visit.get(Visit.UUID),
				visit.get(Visit.VISIT_DATE_TIME),
				visit.get(Visit.VISIT_STATUS),
				visit.get(Visit.VISIT_REMARKS),
				visit.get(Visit.DISEASE),
				symptoms.get(Symptoms.SYMPTOMATIC),
				symptoms.get(Symptoms.TEMPERATURE),
				symptoms.get(Symptoms.TEMPERATURE_SOURCE));

		Predicate filter = visitService.buildCriteriaFilter(visitCriteria, cb, visit);
		cq.where(filter);

		if (sortProperties != null && sortProperties.size() > 0) {
			List<Order> order = new ArrayList<>(sortProperties.size());
			for (SortProperty sortProperty : sortProperties) {
				Expression<?> expression;
				switch (sortProperty.propertyName) {
				case VisitIndexDto.VISIT_DATE_TIME:
				case VisitIndexDto.VISIT_STATUS:
				case VisitIndexDto.VISIT_REMARKS:
				case VisitIndexDto.DISEASE:
					expression = visit.get(sortProperty.propertyName);
					break;
				case VisitIndexDto.SYMPTOMATIC:
				case VisitIndexDto.TEMPERATURE:
					expression = symptoms.get(sortProperty.propertyName);
					break;
				default:
					throw new IllegalArgumentException(sortProperty.propertyName);
				}
				order.add(sortProperty.ascending ? cb.asc(expression) : cb.desc(expression));
			}
			cq.orderBy(order);
		} else {
			cq.orderBy(cb.desc(visit.get(Visit.VISIT_DATE_TIME)));
		}
		
		List<VisitIndexDto> resultList = em.createQuery(cq).setFirstResult(first).setMaxResults(max).getResultList();
		return resultList;
	}
	
	@Override
	public long count(VisitCriteria visitCriteria) {
		if (visitCriteria == null || visitCriteria.getContact() == null) {
			return 0L; // Retrieving a list count independent of a contact is not possible
		}
		
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<Visit> root = cq.from(Visit.class);
		Predicate filter = visitService.buildCriteriaFilter(visitCriteria, cb, root);
		cq.where(filter);
		cq.select(cb.count(root));
		return em.createQuery(cq).getSingleResult();
	}

	public Visit fromDto(@NotNull VisitDto source) {

		Visit target = visitService.getByUuid(source.getUuid());
		if (target == null) {
			target = new Visit();
			target.setUuid(source.getUuid());
			if (source.getCreationDate() != null) {
				target.setCreationDate(new Timestamp(source.getCreationDate().getTime()));
			}
		}
		DtoHelper.validateDto(source, target);

		target.setDisease(source.getDisease());
		target.setPerson(personService.getByReferenceDto(source.getPerson()));
		target.setSymptoms(symptomsFacade.fromDto(source.getSymptoms()));
		target.setVisitDateTime(source.getVisitDateTime());
		target.setVisitRemarks(source.getVisitRemarks());
		target.setVisitStatus(source.getVisitStatus());
		target.setVisitUser(userService.getByReferenceDto(source.getVisitUser()));

		target.setReportLat(source.getReportLat());
		target.setReportLon(source.getReportLon());
		target.setReportLatLonAccuracy(source.getReportLatLonAccuracy());

		return target;
	}

	public static VisitReferenceDto toReferenceDto(Visit source) {
		if (source == null) {
			return null;
		}
		VisitReferenceDto target = new VisitReferenceDto(source.getUuid(), source.toString());
		return target;
	}	

	public static VisitDto toDto(Visit source) {
		if (source == null) {
			return null;
		}
		VisitDto target = new VisitDto();
		DtoHelper.fillDto(target, source);

		target.setDisease(source.getDisease());
		target.setPerson(PersonFacadeEjb.toReferenceDto(source.getPerson()));
		target.setSymptoms(SymptomsFacadeEjb.toDto(source.getSymptoms()));
		target.setVisitDateTime(source.getVisitDateTime());
		target.setVisitRemarks(source.getVisitRemarks());
		target.setVisitStatus(source.getVisitStatus());
		target.setVisitUser(UserFacadeEjb.toReferenceDto(source.getVisitUser()));

		target.setReportLat(source.getReportLat());
		target.setReportLon(source.getReportLon());
		target.setReportLatLonAccuracy(source.getReportLatLonAccuracy());

		return target;
	}

	private void onVisitChanged(VisitDto existingVisit, Visit newVisit) {
		// Send an email to all responsible supervisors when the contact has become symptomatic
		boolean previousSymptomaticStatus = existingVisit != null && Boolean.TRUE.equals(existingVisit.getSymptoms().getSymptomatic());
		if (previousSymptomaticStatus == false && Boolean.TRUE.equals(newVisit.getSymptoms().getSymptomatic())) {
			Set<Contact> contacts = new HashSet<>(contactService.getAllByVisit(visitService.getByUuid(newVisit.getUuid())));
			for (Contact contact : contacts) {
				Case contactCase = contact.getCaze();
				// Skip if there is already a symptomatic visit for this contact
				if (visitService.getSymptomaticCountByContact(contact) > 1) {
					continue;
				}

				List<User> messageRecipients = userService.getAllByRegionAndUserRoles(contactCase.getRegion(), 
						UserRole.SURVEILLANCE_SUPERVISOR, UserRole.CONTACT_SUPERVISOR);
				for (User recipient : messageRecipients) {
					try { 
						messagingService.sendMessage(recipient, I18nProperties.getString(MessagingService.SUBJECT_CONTACT_SYMPTOMATIC), 
								String.format(I18nProperties.getString(MessagingService.CONTENT_CONTACT_SYMPTOMATIC), DataHelper.getShortUuid(contact.getUuid()), DataHelper.getShortUuid(contactCase.getUuid())), 
								MessageType.EMAIL, MessageType.SMS);
					} catch (NotificationDeliveryFailedException e) {
						logger.error(String.format("EmailDeliveryFailedException when trying to notify supervisors about a contact that has become symptomatic. "
								+ "Failed to send " + e.getMessageType() + " to user with UUID %s.", recipient.getUuid()));
					}
				}
			}
		}

		contactService.updateFollowUpUntilAndStatusByVisit(newVisit);
	}

	@LocalBean
	@Stateless
	public static class VisitFacadeEjbLocal extends VisitFacadeEjb {
	}	
}
