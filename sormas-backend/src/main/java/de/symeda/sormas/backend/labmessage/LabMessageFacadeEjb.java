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
import javax.naming.CannotProceedException;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.transaction.Transactional;
import javax.validation.constraints.NotNull;

import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

	private final Logger logger = LoggerFactory.getLogger(getClass());

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
		target.setTestDateTime(source.getTestDateTime());
		target.setTestedDisease(source.getTestedDisease());
		target.setTestLabCity(source.getTestLabCity());
		target.setTestLabExternalId(source.getTestLabExternalId());
		target.setTestLabName(source.getTestLabName());
		target.setTestLabPostalCode(source.getTestLabPostalCode());
		target.setTestResult(source.getTestResult());
		target.setTestType(source.getTestType());
		target.setTestResultText(source.getTestResultText());

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

	/**
	 * The creation of the currentSystemEvent is in this method. All the rest is moved to the private fetchAndSaveExternalLabMessage,
	 * because it shall be done in one transaction. In case of uncaught exceptions, this leaves the systemEvent with status STARTED
	 * and falls back to standard exception handling.
	 * 
	 * @return An indication whether the fetching of new labMessage was successful. If it was not, an error message meant for UI users.
	 */
	@Override
	public LabMessageFetchResult fetchAndSaveExternalLabMessages() {
		SystemEventDto currentSystemEvent = initializeFetchEvent();
		try {
			return fetchAndSaveExternalLabMessages(currentSystemEvent);
		} catch (CannotProceedException e) {
			systemEventFacade.reportError(currentSystemEvent, e.getMessage(), new Date(DateHelper.now()));
			return new LabMessageFetchResult(false, e.getMessage());
		} catch (NamingException e) {
			systemEventFacade.reportError(currentSystemEvent, e.getMessage(), new Date(DateHelper.now()));
			return new LabMessageFetchResult(false, I18nProperties.getString(Strings.errorLabResultsAdapterNotFound));
		}
	}

	@Transactional
	private LabMessageFetchResult fetchAndSaveExternalLabMessages(SystemEventDto currentSystemEvent) throws NamingException {
		LabMessageFetchResult fetchResult = new LabMessageFetchResult(true, null);
		Date since = initializeLastUpdateDate();
		ExternalMessageResult<List<LabMessageDto>> externalMessageResult = fetchExternalMessages(since);
		if (externalMessageResult.isSuccess()) {
			if (externalMessageResult.getValue() != null) {
				externalMessageResult.getValue().forEach(this::save);
			}
			String message = "Last synchronization date: " + externalMessageResult.getSynchronizationDate().toString();
			systemEventFacade.reportSuccess(currentSystemEvent, new Date(DateHelper.now()));
			return fetchResult;
		} else {
			throw new CannotProceedException(externalMessageResult.getError());
		}
	}

	private ExternalMessageResult<List<LabMessageDto>> fetchExternalMessages(Date since) throws NamingException {
		InitialContext ic = new InitialContext();
		String jndiName = configFacade.getDemisJndiName();
		ExternalLabResultsFacade labResultsFacade = (ExternalLabResultsFacade) ic.lookup(jndiName);
		return labResultsFacade.getExternalLabMessages(since);
	}

	private SystemEventDto initializeFetchEvent() {
		Date start = new Date(DateHelper.now());
		SystemEventDto systemEvent = SystemEventDto.build();
		systemEvent.setType(SystemEventType.FETCH_LAB_MESSAGES);
		systemEvent.setStatus(SystemEventStatus.STARTED);
		systemEvent.setStartDate(start);
		systemEventFacade.saveSystemEvent(systemEvent);
		return systemEvent;
	}

	private Date initializeLastUpdateDate() {
		SystemEventDto latestSuccess = systemEventFacade.getLatestSuccessByType(SystemEventType.FETCH_LAB_MESSAGES);
		Long millis;
		if (latestSuccess != null) {
			millis = handleLatestSuccess(latestSuccess);
		} else {
			logger.warn("No previous successful attempt to fetch external lab message could be found. Trying to fetch all messages now");
			millis = 0L;
		}
		return new Date(millis);
	}

	private long handleLatestSuccess(SystemEventDto latestSuccess) {
		String info = latestSuccess.getAdditionalInfo();
		if (info != null) {
			try {
				//parse last synchronization date
				return Long.parseLong(info.replace("Last synchronization date: ", ""));
			} catch (NumberFormatException e) {
				logger.error("Synchronization date could not be parsed for the last successful lab message retrieval. Falling back to start date.");
				return latestSuccess.getStartDate().getTime();
			}
		} else {
			logger.warn("Synchronization date could not be found for the last successful lab message retrieval. Falling back to start date.");
			return latestSuccess.getStartDate().getTime();
		}
	}

	@LocalBean
	@Stateless
	public static class LabMessageFacadeEjbLocal extends LabMessageFacadeEjb {

	}
}
