package de.symeda.sormas.backend.externalmessage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Tuple;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Root;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import de.symeda.sormas.api.externalmessage.ExternalMessageCriteria;
import de.symeda.sormas.api.externalmessage.ExternalMessageDto;
import de.symeda.sormas.api.systemevents.SystemEventDto;
import de.symeda.sormas.api.systemevents.SystemEventStatus;
import de.symeda.sormas.api.systemevents.SystemEventType;
import de.symeda.sormas.backend.caze.surveillancereport.SurveillanceReportService;
import de.symeda.sormas.backend.infrastructure.country.CountryService;
import de.symeda.sormas.backend.infrastructure.facility.FacilityService;
import de.symeda.sormas.backend.systemevent.SystemEventFacadeEjb;
import de.symeda.sormas.backend.systemevent.sync.SyncFacadeEjb;

@ExtendWith(MockitoExtension.class)
public class ExternalMessageFacadeEjbUnitTest {

	@Mock
	private EntityManager em;
	@Mock
	private ExternalMessageService externalMessageService;
	@Mock
	private SurveillanceReportService surveillanceReportService;
	@Mock
	private CountryService countryService;
	@Mock
	private FacilityService facilityService;
	@Mock
	private SystemEventFacadeEjb.SystemEventFacadeEjbLocal systemEventFacade;

	@InjectMocks
	private ExternalMessageFacadeEjb sut;

	@InjectMocks
	private SyncFacadeEjb.SyncFacadeEjbLocal syncFacadeEjb;

	@Mock
	private CriteriaBuilder criteriaBuilder;
	@Mock
	private CriteriaQuery<Tuple> labMessageIndexDtoCriteriaQuery;
	@Mock
	private CriteriaQuery<Long> longCriteriaQuery;
	@Mock
	private CriteriaQuery<Tuple> labMessageIndexIdsTupleCriteriaQuery;
	@Mock
	private Root<ExternalMessage> labMessageRoot;
	@Mock
	private TypedQuery<Tuple> labMessageIndexDtoTypedQuery;
	@Mock
	private TypedQuery<Tuple> labMessageIndexIdsTypedQuery;
	@Mock
	Join<Object, Object> userJoin;
	@Mock
	private TypedQuery<Long> longTypedQuery;
	@Mock
	private Expression<Long> longExpression;
	@Captor
	private ArgumentCaptor<List<Order>> orderListArgumentCaptor;

	@Test
	public void testCount() {

		when(em.getCriteriaBuilder()).thenReturn(criteriaBuilder);
		when(criteriaBuilder.createQuery(Long.class)).thenReturn(longCriteriaQuery);
		when(longCriteriaQuery.from(ExternalMessage.class)).thenReturn(labMessageRoot);
		when(criteriaBuilder.countDistinct(labMessageRoot)).thenReturn(longExpression);
		when(em.createQuery(longCriteriaQuery)).thenReturn(longTypedQuery);
		long expected = 1L;
		when(longTypedQuery.getSingleResult()).thenReturn(expected);

		long result = sut.count(new ExternalMessageCriteria());
		assertEquals(expected, result);
	}

	@Test
	public void testSave() {
		ExternalMessageDto externalMessageDto = new ExternalMessageDto();
		String testUuid = "Test UUID";
		externalMessageDto.setUuid(testUuid);
		ExternalMessage externalMessage = new ExternalMessage();
		externalMessage.setUuid(testUuid);

		when(externalMessageService.getByUuid(testUuid)).thenReturn(externalMessage);
		when(countryService.getByReferenceDto(null)).thenReturn(null);
		when(facilityService.getByReferenceDto(null)).thenReturn(null);
		sut.save(externalMessageDto);

		verify(externalMessageService).ensurePersisted(externalMessage);
	}

	@Test
	public void testGetByUuid() {
		String testUuid = "test UUID";
		ExternalMessage externalMessage = new ExternalMessage();
		when(externalMessageService.getByUuid(testUuid)).thenReturn(externalMessage);
		ExternalMessageDto result = sut.getByUuid(testUuid);
		assertEquals(sut.toDto(externalMessage), result);
	}

	@Test
	public void testInitializeUpdateDateWithNoPreviousSuccess() {
		assertEquals(syncFacadeEjb.findLastSyncDateFor(SystemEventType.FETCH_EXTERNAL_MESSAGES), new Date(0));
	}

	@Test
	public void testInitializeUpdateDateWithPreviousSuccessAndParseableDetails() {

		SystemEventDto systemEvent = SystemEventDto.build();
		Date first = new Date(100, 0, 1);
		Date second = new Date(100, 0, 2);
		systemEvent.setStatus(SystemEventStatus.SUCCESS);
		systemEvent.setType(SystemEventType.FETCH_EXTERNAL_MESSAGES);
		systemEvent.setAdditionalInfo("Last synchronization date: " + first.getTime());
		systemEvent.setStartDate(second);
		when(systemEventFacade.getLatestSuccessByType(SystemEventType.FETCH_EXTERNAL_MESSAGES)).thenReturn(systemEvent);
		assertEquals(syncFacadeEjb.findLastSyncDateFor(SystemEventType.FETCH_EXTERNAL_MESSAGES), first);
	}

	@Test
	public void testInitializeUpdateDateWithPreviousSuccessAndNotParseableDetails() {

		SystemEventDto systemEvent = SystemEventDto.build();
		Date date = new Date(100, 0, 1);
		systemEvent.setStatus(SystemEventStatus.SUCCESS);
		systemEvent.setType(SystemEventType.FETCH_EXTERNAL_MESSAGES);
		systemEvent.setAdditionalInfo("The cake is a lie");
		systemEvent.setStartDate(date);
		when(systemEventFacade.getLatestSuccessByType(SystemEventType.FETCH_EXTERNAL_MESSAGES)).thenReturn(systemEvent);
		assertEquals(syncFacadeEjb.findLastSyncDateFor(SystemEventType.FETCH_EXTERNAL_MESSAGES), date);
	}

	@Test
	public void testInitializeFetchEventTest() {
		SystemEventDto systemEventDto = syncFacadeEjb.startSyncFor(SystemEventType.FETCH_EXTERNAL_MESSAGES);
		// must be null for parsing the notification last update date
		assertEquals(systemEventDto.getAdditionalInfo(), null);
		assertEquals(systemEventDto.getStatus(), SystemEventStatus.STARTED);
		assertEquals(systemEventDto.getType(), SystemEventType.FETCH_EXTERNAL_MESSAGES);
	}
}
