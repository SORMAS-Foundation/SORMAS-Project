package de.symeda.sormas.backend.labmessage;

import static java.util.stream.Collectors.toList;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.security.RolesAllowed;
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
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import de.symeda.sormas.api.user.UserRight;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.symeda.sormas.api.ReferenceDto;
import de.symeda.sormas.api.caze.CaseReferenceDto;
import de.symeda.sormas.api.common.Page;
import de.symeda.sormas.api.contact.ContactReferenceDto;
import de.symeda.sormas.api.event.EventParticipantReferenceDto;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.i18n.Validations;
import de.symeda.sormas.api.labmessage.ExternalLabResultsFacade;
import de.symeda.sormas.api.labmessage.ExternalMessageResult;
import de.symeda.sormas.api.labmessage.LabMessageCriteria;
import de.symeda.sormas.api.labmessage.LabMessageDto;
import de.symeda.sormas.api.labmessage.LabMessageFacade;
import de.symeda.sormas.api.labmessage.LabMessageFetchResult;
import de.symeda.sormas.api.labmessage.LabMessageIndexDto;
import de.symeda.sormas.api.labmessage.LabMessageReferenceDto;
import de.symeda.sormas.api.labmessage.LabMessageStatus;
import de.symeda.sormas.api.labmessage.NewMessagesState;
import de.symeda.sormas.api.labmessage.TestReportDto;
import de.symeda.sormas.api.sample.SampleReferenceDto;
import de.symeda.sormas.api.systemevents.SystemEventDto;
import de.symeda.sormas.api.systemevents.SystemEventType;
import de.symeda.sormas.api.user.UserReferenceDto;
import de.symeda.sormas.api.utils.SortProperty;
import de.symeda.sormas.backend.common.ConfigFacadeEjb;
import de.symeda.sormas.backend.sample.SampleService;
import de.symeda.sormas.backend.systemevent.sync.SyncFacadeEjb;
import de.symeda.sormas.backend.user.User;
import de.symeda.sormas.backend.user.UserService;
import de.symeda.sormas.backend.util.DtoHelper;
import de.symeda.sormas.backend.util.ModelConstants;
import de.symeda.sormas.backend.util.QueryHelper;

@Stateless(name = "LabMessageFacade")
@RolesAllowed(UserRight._LAB_MESSAGES)
public class LabMessageFacadeEjb implements LabMessageFacade {

	public static final List<String> VALID_SORT_PROPERTY_NAMES = Arrays.asList(
		LabMessageIndexDto.UUID,
		LabMessageIndexDto.TYPE,
		LabMessageIndexDto.PERSON_FIRST_NAME,
		LabMessageIndexDto.PERSON_LAST_NAME,
		LabMessageIndexDto.PERSON_POSTAL_CODE,
		LabMessageIndexDto.LAB_NAME,
		LabMessageIndexDto.LAB_POSTAL_CODE,
		LabMessageIndexDto.MESSAGE_DATE_TIME,
		LabMessageIndexDto.STATUS,
		LabMessageIndexDto.SAMPLE_OVERALL_TEST_RESULT,
		LabMessageIndexDto.TESTED_DISEASE);

	@PersistenceContext(unitName = ModelConstants.PERSISTENCE_UNIT_NAME)
	private EntityManager em;

	private final Logger logger = LoggerFactory.getLogger(getClass());

	@EJB
	private LabMessageService labMessageService;
	@EJB
	private TestReportFacadeEjb.TestReportFacadeEjbLocal testReportFacade;
	@EJB
	private ConfigFacadeEjb.ConfigFacadeEjbLocal configFacade;
	@EJB
	private SyncFacadeEjb.SyncFacadeEjbLocal syncFacadeEjb;
	@EJB
	private SampleService sampleService;
	@EJB
	private UserService userService;

	LabMessage fromDto(@NotNull LabMessageDto source, LabMessage target, boolean checkChangeDate) {

		target = DtoHelper.fillOrBuildEntity(source, target, LabMessage::new, checkChangeDate);

		target.setType(source.getType());
		target.setLabMessageDetails(source.getLabMessageDetails());
		target.setLabSampleId(source.getLabSampleId());
		target.setTestedDisease(source.getTestedDisease());
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
		target.setStatus(source.getStatus());
		target.setSampleDateTime(source.getSampleDateTime());
		target.setSampleMaterial(source.getSampleMaterial());
		target.setSampleMaterialText(source.getSampleMaterialText());
		target.setSampleReceivedDate(source.getSampleReceivedDate());
		target.setSpecimenCondition(source.getSpecimenCondition());
		target.setPersonPhone(source.getPersonPhone());
		target.setPersonEmail(source.getPersonEmail());
		target.setLabCity(source.getLabCity());
		target.setLabExternalId(source.getLabExternalId());
		target.setLabName(source.getLabName());
		target.setLabPostalCode(source.getLabPostalCode());
		if (source.getTestReports() != null) {
			List<TestReport> testReports = new ArrayList<>();
			for (TestReportDto t : source.getTestReports()) {
				TestReport testReport = testReportFacade.fromDto(t, target, false);
				testReports.add(testReport);
			}
			target.setTestReports(testReports);
		}
		target.setReportId(source.getReportId());
		target.setSampleOverallTestResult(source.getSampleOverallTestResult());
		if (source.getAssignee() != null) {
			target.setAssignee(userService.getByReferenceDto(source.getAssignee()));
		} else {
			target.setAssignee(null);
		}
		if (source.getSample() != null) {
			target.setSample(sampleService.getByReferenceDto(source.getSample()));
		}
		return target;
	}

	@Override
	public LabMessageDto save(@Valid LabMessageDto dto) {

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

		target.setType(source.getType());
		target.setLabMessageDetails(source.getLabMessageDetails());
		target.setLabSampleId(source.getLabSampleId());
		target.setTestedDisease(source.getTestedDisease());
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
		target.setLabCity(source.getLabCity());
		target.setLabExternalId(source.getLabExternalId());
		target.setLabName(source.getLabName());
		target.setLabPostalCode(source.getLabPostalCode());
		target.setStatus(source.getStatus());
		target.setSampleDateTime(source.getSampleDateTime());
		target.setSampleMaterial(source.getSampleMaterial());
		target.setSampleMaterialText(source.getSampleMaterialText());
		target.setSampleReceivedDate(source.getSampleReceivedDate());
		target.setSpecimenCondition(source.getSpecimenCondition());
		if (source.getTestReports() != null) {
			target.setTestReports(source.getTestReports().stream().map(t -> TestReportFacadeEjb.toDto(t)).collect(toList()));
		}
		target.setReportId(source.getReportId());
		target.setSampleOverallTestResult(source.getSampleOverallTestResult());
		if (source.getSample() != null) {
			target.setSample(source.getSample().toReference());
		}
		if (source.getAssignee() != null) {
			target.setAssignee(source.getAssignee().toReference());
		}

		return target;
	}

	@Override
	public LabMessageDto getByUuid(String uuid) {
		return toDto(labMessageService.getByUuid(uuid));
	}

	@Override
	public void deleteLabMessage(String uuid) {
		labMessageService.deletePermanent(labMessageService.getByUuid(uuid));
	}

	@Override
	public void deleteLabMessages(List<String> uuids) {
		List<LabMessage> labMessages = labMessageService.getByUuids(uuids);
		for (LabMessage labMessage : labMessages) {
			if (labMessage.getStatus() != LabMessageStatus.PROCESSED) {
				labMessageService.deletePermanent(labMessage);
			}
		}
	}

	@Override
	public void bulkAssignLabMessages(List<String> uuids, UserReferenceDto userRef) {
		List<LabMessage> labMessages = labMessageService.getByUuids(uuids);
		User user = userService.getByReferenceDto(userRef);
		for (LabMessage labMessage : labMessages) {
			labMessage.setAssignee(user);
			labMessageService.ensurePersisted(labMessage);
		}
	}

	@Override
	public List<LabMessageDto> getForSample(SampleReferenceDto sample) {

		List<LabMessage> labMessages = labMessageService.getForSample(sample);

		return labMessages.stream().map(this::toDto).collect(Collectors.toList());

	}

	@Override
	public Boolean isProcessed(String uuid) {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Boolean> cq = cb.createQuery(Boolean.class);
		Root<LabMessage> from = cq.from(LabMessage.class);

		Predicate filter = cb.and(cb.equal(from.get(LabMessage.UUID), uuid));

		cq.where(filter);
		cq.select(cb.equal(from.get(LabMessage.STATUS), LabMessageStatus.PROCESSED));

		return QueryHelper.getSingleResult(em, cq);
	}

	@Override
	public long count(LabMessageCriteria criteria) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<LabMessage> labMessage = cq.from(LabMessage.class);

		Predicate filter = null;
		if (criteria != null) {
			filter = labMessageService.buildCriteriaFilter(cb, labMessage, criteria);
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
		Join<LabMessage, User> userJoin = labMessage.join(LabMessage.ASSIGNEE, JoinType.LEFT);

		cq.multiselect(
			labMessage.get(LabMessage.UUID),
			labMessage.get(LabMessage.TYPE),
			labMessage.get(LabMessage.MESSAGE_DATE_TIME),
			labMessage.get(LabMessage.LAB_NAME),
			labMessage.get(LabMessage.LAB_POSTAL_CODE),
			labMessage.get(LabMessage.TESTED_DISEASE),
			labMessage.get(LabMessage.SAMPLE_OVERALL_TEST_RESULT),
			labMessage.get(LabMessage.PERSON_FIRST_NAME),
			labMessage.get(LabMessage.PERSON_LAST_NAME),
			labMessage.get(LabMessage.PERSON_BIRTH_DATE_YYYY),
			labMessage.get(LabMessage.PERSON_BIRTH_DATE_MM),
			labMessage.get(LabMessage.PERSON_BIRTH_DATE_DD),
			labMessage.get(LabMessage.PERSON_POSTAL_CODE),
			labMessage.get(LabMessage.STATUS),
			userJoin.get(User.UUID),
			userJoin.get(User.FIRST_NAME),
			userJoin.get(User.LAST_NAME));

		Predicate filter = null;

		if (criteria != null) {
			filter = labMessageService.buildCriteriaFilter(cb, labMessage, criteria);
		}

		if (filter != null) {
			cq.where(filter);
		}

		// Distinct is necessary here to avoid duplicate results due to the user role join in taskService.createAssigneeFilter
		cq.distinct(true);

		List<Order> order = new ArrayList<>();

		if (!CollectionUtils.isEmpty(sortProperties)) {
			for (SortProperty sortProperty : sortProperties) {
				if (LabMessageIndexDto.PERSON_BIRTH_DATE.equals(sortProperty.propertyName)) {
					Expression<?> birthdateYYYY = labMessage.get(LabMessage.PERSON_BIRTH_DATE_YYYY);
					order.add(sortProperty.ascending ? cb.asc(birthdateYYYY) : cb.desc(birthdateYYYY));
					Expression<?> birthdateMM = labMessage.get(LabMessage.PERSON_BIRTH_DATE_MM);
					order.add(sortProperty.ascending ? cb.asc(birthdateMM) : cb.desc(birthdateMM));
					Expression<?> birthdateDD = labMessage.get(LabMessage.PERSON_BIRTH_DATE_DD);
					order.add(sortProperty.ascending ? cb.asc(birthdateDD) : cb.desc(birthdateDD));
				} else if (VALID_SORT_PROPERTY_NAMES.contains(sortProperty.propertyName)) {
					Expression<?> expression = labMessage.get(sortProperty.propertyName);
					order.add(sortProperty.ascending ? cb.asc(expression) : cb.desc(expression));
				}
			}
		}

		order.add(cb.desc(labMessage.get(LabMessage.MESSAGE_DATE_TIME)));
		cq.orderBy(order);

		return QueryHelper.getResultList(em, cq, first, max);
	}

	public Page<LabMessageIndexDto> getIndexPage(LabMessageCriteria criteria, Integer offset, Integer size, List<SortProperty> sortProperties) {
		List<LabMessageIndexDto> labMessageIndexList = getIndexList(criteria, offset, size, sortProperties);
		long totalElementCount = count(criteria);
		return new Page<>(labMessageIndexList, offset, size, totalElementCount);
	}

	/**
	 * This method marks the previously unfinished system events as UNCLEAR(if any exists) and creates a new event with status STARTED.
	 * If the fetching succeeds, the status of the currentSync is changed to SUCCESS.
	 * In case of any Exception, the status of the currentSync is changed to ERROR.
	 *
	 * @return An indication whether the fetching of new labMessage was successful. If it was not, an error message meant for UI users.
	 */
	@Override
	@RolesAllowed({
		UserRight._SYSTEM,
		UserRight._LAB_MESSAGES })
	public LabMessageFetchResult fetchAndSaveExternalLabMessages(Date since) {

		SystemEventDto currentSync = syncFacadeEjb.startSyncFor(SystemEventType.FETCH_LAB_MESSAGES);

		try {
			return fetchAndSaveExternalLabMessages(currentSync, since);
		} catch (CannotProceedException e) {
			syncFacadeEjb.reportSyncErrorWithTimestamp(currentSync, e.getMessage());
			return new LabMessageFetchResult(false, NewMessagesState.UNCLEAR, e.getMessage());
		} catch (NamingException e) {
			syncFacadeEjb.reportSyncErrorWithTimestamp(currentSync, e.getMessage());
			return new LabMessageFetchResult(false, NewMessagesState.UNCLEAR, I18nProperties.getString(Strings.errorLabResultsAdapterNotFound));
		} catch (Exception t) {
			syncFacadeEjb.reportSyncErrorWithTimestamp(currentSync, t.getMessage());
			throw t;
		}
	}

	protected LabMessageFetchResult fetchAndSaveExternalLabMessages(SystemEventDto currentSync, Date since) throws NamingException {
		if (since == null) {
			since = syncFacadeEjb.findLastSyncDateFor(SystemEventType.FETCH_LAB_MESSAGES);
		}
		ExternalMessageResult<List<LabMessageDto>> externalMessageResult = fetchExternalMessages(since);
		if (externalMessageResult.isSuccess()) {
			externalMessageResult.getValue().forEach(this::save);
			// we have successfully completed our synchronization
			syncFacadeEjb.reportSuccessfulSyncWithTimestamp(currentSync, externalMessageResult.getSynchronizationDate());

			return getSuccessfulFetchResult(externalMessageResult);
		} else {
			throw new CannotProceedException(externalMessageResult.getError());
		}
	}

	protected ExternalMessageResult<List<LabMessageDto>> fetchExternalMessages(Date since) throws NamingException {
		ExternalLabResultsFacade labResultsFacade = getExternalLabResultsFacade();
		return labResultsFacade.getExternalLabMessages(since);
	}

	private ExternalLabResultsFacade getExternalLabResultsFacade() throws NamingException {
		InitialContext ic = new InitialContext();
		String jndiName = configFacade.getDemisJndiName();

		if (jndiName == null) {
			throw new CannotProceedException(I18nProperties.getValidationError(Validations.externalMessageConfigError));
		}

		return (ExternalLabResultsFacade) ic.lookup(jndiName);
	}

	@Override
	public String getLabMessagesAdapterVersion() throws NamingException {
		ExternalLabResultsFacade labResultsFacade = getExternalLabResultsFacade();
		return labResultsFacade.getVersion();
	}

	private LabMessageFetchResult getSuccessfulFetchResult(ExternalMessageResult<List<LabMessageDto>> externalMessageResult) {
		if (isEmptyResult(externalMessageResult)) {
			return new LabMessageFetchResult(true, NewMessagesState.NO_NEW_MESSAGES, null);
		} else {
			return new LabMessageFetchResult(true, NewMessagesState.NEW_MESSAGES, null);
		}
	}

	private boolean isEmptyResult(ExternalMessageResult<List<LabMessageDto>> externalMessageResult) {
		return externalMessageResult.getValue() == null || externalMessageResult.getValue().isEmpty();
	}

	@Override
	public boolean exists(String uuid) {
		return labMessageService.exists(uuid);
	}

	@Override
	public boolean existsLabMessageForEntity(ReferenceDto entityRef) {
		if (CaseReferenceDto.class.equals(entityRef.getClass())) {
			return labMessageService.countForCase(entityRef.getUuid()) > 0;
		} else if (ContactReferenceDto.class.equals(entityRef.getClass())) {
			return labMessageService.countForContact(entityRef.getUuid()) > 0;
		} else if (EventParticipantReferenceDto.class.equals(entityRef.getClass())) {
			return labMessageService.countForEventParticipant(entityRef.getUuid()) > 0;
		} else {
			throw new UnsupportedOperationException("Reference class" + entityRef.getClass() + " is not supported.");
		}
	}

	@Override
	public List<LabMessageDto> getByReportId(String reportId) {

		if (reportId == null) {
			return Collections.emptyList();
		}

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<LabMessage> cq = cb.createQuery(LabMessage.class);
		Root<LabMessage> from = cq.from(LabMessage.class);

		cq.where(cb.equal(from.get(LabMessage.REPORT_ID), reportId));

		return em.createQuery(cq).getResultList().stream().map(this::toDto).collect(toList());
	}

	@Override
	public boolean existsForwardedLabMessageWith(String reportId) {

		List<LabMessageDto> relatedLabMessages = getByReportId(reportId);

		for (LabMessageDto labMessage : relatedLabMessages) {
			if (LabMessageStatus.FORWARDED.equals(labMessage.getStatus())) {
				return true;
			}
		}
		return false;
	}

	public static LabMessageReferenceDto toReferenceDto(LabMessage entity) {

		if (entity == null) {
			return null;
		}

		return new LabMessageReferenceDto(entity.getUuid(), entity.toString());
	}

	@LocalBean
	@Stateless
	public static class LabMessageFacadeEjbLocal extends LabMessageFacadeEjb {

	}
}
