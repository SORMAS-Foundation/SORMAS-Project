package de.symeda.sormas.backend.labmessage;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.naming.InitialContext;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.validation.constraints.NotNull;

import de.symeda.sormas.api.labmessage.ExternalLabResultsFacade;
import de.symeda.sormas.api.labmessage.LabMessageCriteria;
import de.symeda.sormas.api.labmessage.LabMessageDto;
import de.symeda.sormas.api.labmessage.LabMessageFacade;
import de.symeda.sormas.api.labmessage.LabMessageIndexDto;
import de.symeda.sormas.api.systemevents.SystemEventDto;
import de.symeda.sormas.api.systemevents.SystemEventStatus;
import de.symeda.sormas.api.systemevents.SystemEventType;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.api.utils.SortProperty;
import de.symeda.sormas.backend.common.ConfigFacadeEjb;
import de.symeda.sormas.backend.common.CriteriaBuilderHelper;
import de.symeda.sormas.backend.systemevent.SystemEventFacadeEjb;
import de.symeda.sormas.backend.util.DtoHelper;
import de.symeda.sormas.backend.util.ModelConstants;

@Stateless(name = "LabMessageFacade")
public class LabMessageFacadeEjb implements LabMessageFacade {

	@PersistenceContext(unitName = ModelConstants.PERSISTENCE_UNIT_NAME)
	private EntityManager em;

	@EJB
	private LabMessageService labMessageService;
	@EJB
	private ConfigFacadeEjb.ConfigFacadeEjbLocal configFacade;
	@EJB
	private SystemEventFacadeEjb.SystemEventFacadeEjbLocal systemEventFacade;

	private LabMessage fromDto(@NotNull LabMessageDto source, LabMessage target, boolean checkChangeDate) {

		if (target == null) {
			target = new LabMessage();
			target.setUuid(source.getUuid());
		}

		DtoHelper.validateDto(source, target, checkChangeDate);

		target.setLabMessageDetails(source.getLabMessageDetails());
		target.setLabSampleId(source.getLabSampleId());
		target.setMessageDateTime(source.getMessageDateTime());
		target.setPersonBirthDateDD(source.getPersonBirthDateDD());
		target.setPersonBirthDateMM(source.getPersonBirthDateMM());
		target.setPersonBirthDateYYYY(source.getPersonBirthDateYYYY());
		target.setPersonCity(source.getPersonCity());
		target.setPersonFirstName(source.getPersonFirstName());
		target.setPersonHouseNumber(source.getPersonHouseNumber());
		target.setPersonLastName(source.getPersonLastName());
		target.setPersonPostalCode(source.getPersonPostalCode());
		target.setPersonSex(source.getPersonSex());
		target.setPersonStreet(source.getPersonStreet());
		target.setProcessed(source.isProcessed());
		target.setSampleDateTime(source.getSampleDateTime());
		target.setSampleMaterial(source.getSampleMaterial());
		target.setSampleReceivedDate(source.getSampleReceivedDate());
		target.setSpecimenCondition(source.getSpecimenCondition());
		target.setTestDateTime(source.getTestDateTime());
		target.setTestedDisease(source.getTestedDisease());
		target.setTestLabCity(source.getTestLabCity());
		target.setTestLabExternalId(source.getTestLabExternalId());
		target.setTestLabName(source.getTestLabName());
		target.setTestLabPostalCode(source.getTestLabPostalCode());
		target.setTestResult(source.getTestResult());
		target.setTestType(source.getTestType());

		return target;
	}

	@Override
	public void save(LabMessageDto dto) {

		LabMessage labMessage = labMessageService.getByUuid(dto.getUuid());

		labMessage = fromDto(dto, labMessage, true);
		labMessageService.ensurePersisted(labMessage);
	}

	public LabMessageDto toDto(LabMessage source) {

		if (source == null) {
			return null;
		}
		LabMessageDto target = new LabMessageDto();
		DtoHelper.fillDto(target, source);

		target.setLabMessageDetails(source.getLabMessageDetails());
		target.setLabSampleId(source.getLabSampleId());
		target.setMessageDateTime(source.getMessageDateTime());
		target.setPersonBirthDateDD(source.getPersonBirthDateDD());
		target.setPersonBirthDateMM(source.getPersonBirthDateMM());
		target.setPersonBirthDateYYYY(source.getPersonBirthDateYYYY());
		target.setPersonCity(source.getPersonCity());
		target.setPersonFirstName(source.getPersonFirstName());
		target.setPersonHouseNumber(source.getPersonHouseNumber());
		target.setPersonLastName(source.getPersonLastName());
		target.setPersonPostalCode(source.getPersonPostalCode());
		target.setPersonSex(source.getPersonSex());
		target.setPersonStreet(source.getPersonStreet());
		target.setProcessed(source.isProcessed());
		target.setSampleDateTime(source.getSampleDateTime());
		target.setSampleMaterial(source.getSampleMaterial());
		target.setSampleReceivedDate(source.getSampleReceivedDate());
		target.setSpecimenCondition(source.getSpecimenCondition());
		target.setTestDateTime(source.getTestDateTime());
		target.setTestedDisease(source.getTestedDisease());
		target.setTestLabCity(source.getTestLabCity());
		target.setTestLabExternalId(source.getTestLabExternalId());
		target.setTestLabName(source.getTestLabName());
		target.setTestLabPostalCode(source.getTestLabPostalCode());
		target.setTestResult(source.getTestResult());
		target.setTestType(source.getTestType());

		return target;
	}

	@Override
	public LabMessageDto getByUuid(String uuid) {
		return toDto(labMessageService.getByUuid(uuid));
	}

	@Override
	public long count(LabMessageCriteria criteria) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<LabMessage> labMessage = cq.from(LabMessage.class);

		Predicate filter = null;

		if (criteria != null) {
			Predicate statusFilter = labMessageService.buildCriteriaFilter(cb, labMessage, criteria);
			filter = CriteriaBuilderHelper.and(cb, filter, statusFilter);
		}

		if (filter != null) {
			cq.where(filter);
		}

		cq.select(cb.countDistinct(labMessage));
		return em.createQuery(cq).getSingleResult();
	}

	@Override
	public List<LabMessageIndexDto> getIndexList(LabMessageCriteria criteria, Integer first, Integer max, List<SortProperty> sortProperties) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<LabMessageIndexDto> cq = cb.createQuery(LabMessageIndexDto.class);
		Root<LabMessage> labMessage = cq.from(LabMessage.class);

		cq.multiselect(
			labMessage.get(LabMessage.UUID),
			labMessage.get(LabMessage.MESSAGE_DATE_TIME),
			labMessage.get(LabMessage.TESTED_DISEASE),
			labMessage.get(LabMessage.TEST_RESULT),
			labMessage.get(LabMessage.PERSON_FIRST_NAME),
			labMessage.get(LabMessage.PERSON_LAST_NAME),
			labMessage.get(LabMessage.PROCESSED));

		Predicate filter = null;
		if (criteria != null) {
			Predicate statusFilter = labMessageService.buildCriteriaFilter(cb, labMessage, criteria);
			filter = CriteriaBuilderHelper.and(cb, filter, statusFilter);
		}

		if (filter != null) {
			cq.where(filter);
		}

		// Distinct is necessary here to avoid duplicate results due to the user role join in taskService.createAssigneeFilter
		cq.distinct(true);

		List<Order> order = new ArrayList<Order>();
		if (sortProperties != null && sortProperties.size() > 0) {
			for (SortProperty sortProperty : sortProperties) {
				Expression<?> expression;
				switch (sortProperty.propertyName) {
				case LabMessageIndexDto.UUID:
				case LabMessageIndexDto.PERSON_FIRST_NAME:
				case LabMessageIndexDto.PERSON_LAST_NAME:
				case LabMessageIndexDto.MESSAGE_DATE_TIME:
				case LabMessageIndexDto.PROCESSED:
				case LabMessageIndexDto.TEST_RESULT:
				case LabMessageIndexDto.TESTED_DISEASE:
					expression = labMessage.get(sortProperty.propertyName);
					break;
				default:
					throw new IllegalArgumentException(sortProperty.propertyName);
				}
				order.add(sortProperty.ascending ? cb.asc(expression) : cb.desc(expression));
			}
		}
		order.add(cb.desc(labMessage.get(LabMessage.MESSAGE_DATE_TIME)));
		cq.orderBy(order);
		List<LabMessageIndexDto> labMessages;
		if (first != null && max != null) {
			labMessages = em.createQuery(cq).setFirstResult(first).setMaxResults(max).getResultList();
		} else {
			labMessages = em.createQuery(cq).getResultList();
		}

		return labMessages;
	}

	@Override
	public void fetchExternalLabMessages() {
		Date start = new Date(DateHelper.now());
		SystemEventDto systemEvent = SystemEventDto.build();
		systemEvent.setType(SystemEventType.FETCH_LAB_MESSAGES);
		systemEvent.setStatus(SystemEventStatus.STARTED);
		systemEvent.setStartDate(start);
		systemEventFacade.saveSystemEvent(systemEvent);

		Date since = systemEventFacade.getLatestSuccessByType(SystemEventType.FETCH_LAB_MESSAGES);

		if (since == null) {
			since = new Date(0);
		}

		try {
			InitialContext ic = new InitialContext();
			String jndiName = configFacade.getDemisJndiName();
			ExternalLabResultsFacade labResultsFacade = (ExternalLabResultsFacade) ic.lookup(jndiName);
			List<LabMessageDto> newMessages = labResultsFacade.getExternalLabMessages(since);
			if (newMessages != null) {
				newMessages.forEach(this::save);
			}
		} catch (Exception e) {
			systemEvent.setStatus(SystemEventStatus.ERROR);
			systemEvent.setAdditionalInfo(e.getMessage());
			Date end = new Date(DateHelper.now());
			systemEvent.setEndDate(end);
			systemEvent.setChangeDate(end);
			systemEventFacade.saveSystemEvent(systemEvent);
			e.printStackTrace();
			return;
		}
		systemEvent.setStatus(SystemEventStatus.SUCCESS);
		Date end = new Date(DateHelper.now());
		systemEvent.setEndDate(end);
		systemEvent.setChangeDate(end);
		systemEventFacade.saveSystemEvent(systemEvent);
	}

	@LocalBean
	@Stateless
	public static class LabMessageFacadeEjbLocal extends LabMessageFacadeEjb {

	}
}
