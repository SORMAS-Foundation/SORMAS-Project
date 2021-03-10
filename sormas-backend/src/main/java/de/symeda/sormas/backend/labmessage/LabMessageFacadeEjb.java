package de.symeda.sormas.backend.labmessage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
import de.symeda.sormas.api.labmessage.ExternalMessageResult;
import de.symeda.sormas.api.labmessage.LabMessageCriteria;
import de.symeda.sormas.api.labmessage.LabMessageDto;
import de.symeda.sormas.api.labmessage.LabMessageFacade;
import de.symeda.sormas.api.labmessage.LabMessageFetchResult;
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

	public static final List<String> VALID_SORT_PROPERTY_NAMES = Arrays.asList(
		LabMessageIndexDto.UUID,
		LabMessageIndexDto.PERSON_FIRST_NAME,
		LabMessageIndexDto.PERSON_LAST_NAME,
		LabMessageIndexDto.MESSAGE_DATE_TIME,
		LabMessageIndexDto.PROCESSED,
		LabMessageIndexDto.TEST_RESULT,
		LabMessageIndexDto.TESTED_DISEASE);

	@PersistenceContext(unitName = ModelConstants.PERSISTENCE_UNIT_NAME)
	private EntityManager em;

	@EJB
	private LabMessageService labMessageService;
	@EJB
	private ConfigFacadeEjb.ConfigFacadeEjbLocal configFacade;
	@EJB
	private SystemEventFacadeEjb.SystemEventFacadeEjbLocal systemEventFacade;

	LabMessage fromDto(@NotNull LabMessageDto source, LabMessage target, boolean checkChangeDate) {

		target = DtoHelper.fillOrBuildEntity(source, target, LabMessage::new, checkChangeDate);

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
		target.setPersonPhone(source.getPersonPhone());
		target.setPersonEmail(source.getPersonEmail());
		target.setTestDateTime(source.getTestDateTime());
		target.setTestedDisease(source.getTestedDisease());
		target.setTestLabCity(source.getTestLabCity());
		target.setTestLabExternalId(source.getTestLabExternalId());
		target.setTestLabName(source.getTestLabName());
		target.setTestLabPostalCode(source.getTestLabPostalCode());
		target.setTestResult(source.getTestResult());
		target.setTestResultVerified(source.isTestResultVerified());
		target.setTestType(source.getTestType());
		target.setTestResultText(source.getTestResultText());

		return target;
	}

	@Override
	public LabMessageDto save(LabMessageDto dto) {

		LabMessage labMessage = labMessageService.getByUuid(dto.getUuid());

		labMessage = fromDto(dto, labMessage, true);
		labMessageService.ensurePersisted(labMessage);

		return toDto(labMessage);
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
		target.setPersonPhone(source.getPersonPhone());
		target.setPersonEmail(source.getPersonEmail());
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
		target.setTestResultVerified(source.isTestResultVerified());
		target.setTestType(source.getTestType());
		target.setTestResultText(source.getTestResultText());

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

		criteriaHandler(criteria, cb, cq, labMessage);

		cq.select(cb.countDistinct(labMessage));
		return em.createQuery(cq).getSingleResult();
	}

	private <T> void criteriaHandler(LabMessageCriteria criteria, CriteriaBuilder cb, CriteriaQuery<T> cq, Root<LabMessage> labMessage) {
		Predicate filter = null;
		if (criteria != null) {
			Predicate statusFilter = labMessageService.buildCriteriaFilter(cb, labMessage, criteria);
			filter = CriteriaBuilderHelper.and(cb, null, statusFilter);
		}
		if (filter != null) {
			cq.where(filter);
		}
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

		criteriaHandler(criteria, cb, cq, labMessage);

		// Distinct is necessary here to avoid duplicate results due to the user role join in taskService.createAssigneeFilter
		cq.distinct(true);

		List<Order> order = Optional.ofNullable(sortProperties)
			.orElseGet(ArrayList::new)
			.stream()
			.filter(sortProperty -> VALID_SORT_PROPERTY_NAMES.contains(sortProperty.propertyName))
			.map(sortProperty -> {
				Expression<?> expression = labMessage.get(sortProperty.propertyName);
				return sortProperty.ascending ? cb.asc(expression) : cb.desc(expression);
			})
			.collect(Collectors.toList());

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
	public LabMessageFetchResult fetchAndSaveExternalLabMessages() {
		Date startDate = new Date(DateHelper.now());
		SystemEventDto systemEvent = SystemEventDto.build();
		systemEvent.setType(SystemEventType.FETCH_LAB_MESSAGES);
		systemEvent.setStatus(SystemEventStatus.STARTED);
		systemEvent.setStartDate(startDate);
		systemEventFacade.saveSystemEvent(systemEvent);

		Date since = systemEventFacade.getLatestSuccessByType(SystemEventType.FETCH_LAB_MESSAGES);

		since = Optional.ofNullable(since).orElse(new Date(0));

		try {
			InitialContext ic = new InitialContext();
			String jndiName = configFacade.getDemisJndiName();
			ExternalLabResultsFacade labResultsFacade = (ExternalLabResultsFacade) ic.lookup(jndiName);
			ExternalMessageResult<List<LabMessageDto>> externalMessageResult = labResultsFacade.getExternalLabMessages(since);
			if (externalMessageResult.isSuccess()) {
				externalMessageResult.getValue().forEach(this::save);
				createFetchLabMessagesSystemEvent(startDate, SystemEventStatus.SUCCESS, null);
				return getSuccessfulFetchResult(externalMessageResult);
			} else {
				createFetchLabMessagesSystemEvent(startDate, SystemEventStatus.ERROR, null);
				return new LabMessageFetchResult(false, false, externalMessageResult.getError());
			}
		} catch (Exception e) {
			createFetchLabMessagesSystemEvent(startDate, SystemEventStatus.ERROR, e.getMessage());
			e.printStackTrace();
			return new LabMessageFetchResult(false, false, e.getMessage());
		}
	}

	private LabMessageFetchResult getSuccessfulFetchResult(ExternalMessageResult<List<LabMessageDto>> externalMessageResult) {
		if (isEmptyResult(externalMessageResult)) {
			return new LabMessageFetchResult(true, false, null);
		} else {
			return new LabMessageFetchResult(true, true, null);
		}
	}

	private boolean isEmptyResult(ExternalMessageResult<List<LabMessageDto>> externalMessageResult) {
		return externalMessageResult.getValue() == null || externalMessageResult.getValue().isEmpty();
	}

	private void createFetchLabMessagesSystemEvent(Date startDate, SystemEventStatus eventStatus, String additionalInfo) {
		SystemEventDto systemEvent = SystemEventDto.build();
		systemEvent.setStatus(eventStatus);
		systemEvent.setType(SystemEventType.FETCH_LAB_MESSAGES);
		systemEvent.setStartDate(startDate);
		Date end = new Date(DateHelper.now());
		systemEvent.setEndDate(end);
		systemEvent.setChangeDate(end);
		systemEvent.setAdditionalInfo(additionalInfo);
		systemEventFacade.saveSystemEvent(systemEvent);
	}

	@Override
	public boolean exists(String uuid) {
		return labMessageService.exists(uuid);
	}

	@LocalBean
	@Stateless
	public static class LabMessageFacadeEjbLocal extends LabMessageFacadeEjb {

	}
}
