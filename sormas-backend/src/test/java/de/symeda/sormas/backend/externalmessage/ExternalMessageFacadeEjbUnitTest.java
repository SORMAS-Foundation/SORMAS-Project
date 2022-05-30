package de.symeda.sormas.backend.externalmessage;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Root;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import de.symeda.sormas.api.externalmessage.ExternalMessageCriteria;
import de.symeda.sormas.api.externalmessage.ExternalMessageDto;
import de.symeda.sormas.api.externalmessage.ExternalMessageIndexDto;
import de.symeda.sormas.api.systemevents.SystemEventDto;
import de.symeda.sormas.api.systemevents.SystemEventStatus;
import de.symeda.sormas.api.systemevents.SystemEventType;
import de.symeda.sormas.api.utils.SortProperty;
import de.symeda.sormas.backend.systemevent.SystemEventFacadeEjb;
import de.symeda.sormas.backend.systemevent.sync.SyncFacadeEjb;

@RunWith(MockitoJUnitRunner.class)
public class ExternalMessageFacadeEjbUnitTest {

	@Mock
	private EntityManager em;
	@Mock
	private ExternalMessageService externalMessageService;
	@Mock
	private SystemEventFacadeEjb.SystemEventFacadeEjbLocal systemEventFacade;

	@InjectMocks
	private ExternalMessageFacadeEjb sut;

	@InjectMocks
	private SyncFacadeEjb.SyncFacadeEjbLocal syncFacadeEjb;

	@Mock
	private CriteriaBuilder criteriaBuilder;
	@Mock
	private CriteriaQuery<ExternalMessageIndexDto> labMessageIndexDtoCriteriaQuery;
	@Mock
	private CriteriaQuery<Long> longCriteriaQuery;
	@Mock
	private Root<ExternalMessage> labMessage;
	@Mock
	private TypedQuery<ExternalMessageIndexDto> labMessageIndexDtoTypedQuery;
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
		when(longCriteriaQuery.from(ExternalMessage.class)).thenReturn(labMessage);
		when(criteriaBuilder.countDistinct(labMessage)).thenReturn(longExpression);
		when(em.createQuery(longCriteriaQuery)).thenReturn(longTypedQuery);
		long expected = 1L;
		when(longTypedQuery.getSingleResult()).thenReturn(expected);

		long result = sut.count(new ExternalMessageCriteria());
		assertEquals(expected, result);
	}

	@Test
	public void testGetIndexList() {

		int first = 1;
		int max = 1;

		when(em.getCriteriaBuilder()).thenReturn(criteriaBuilder);
		when(criteriaBuilder.createQuery(ExternalMessageIndexDto.class)).thenReturn(labMessageIndexDtoCriteriaQuery);
		when(labMessageIndexDtoCriteriaQuery.from(ExternalMessage.class)).thenReturn(labMessage);
		when(em.createQuery(labMessageIndexDtoCriteriaQuery)).thenReturn(labMessageIndexDtoTypedQuery);
		when(labMessageIndexDtoTypedQuery.setFirstResult(first)).thenReturn(labMessageIndexDtoTypedQuery);
		when(labMessageIndexDtoTypedQuery.setMaxResults(max)).thenReturn(labMessageIndexDtoTypedQuery);
		ArrayList<ExternalMessageIndexDto> expectedResult = new ArrayList<>();
		when(labMessageIndexDtoTypedQuery.getResultList()).thenReturn(expectedResult);
		when(labMessage.join(ExternalMessage.ASSIGNEE, JoinType.LEFT)).thenReturn(userJoin);
		when(userJoin.get((String) any())).thenReturn(null);
		ArrayList<SortProperty> sortProperties = new ArrayList<>();
		sortProperties.add(new SortProperty(ExternalMessageIndexDto.UUID));
		sortProperties.add(new SortProperty("No Valid Property"));
		List<ExternalMessageIndexDto> result = sut.getIndexList(new ExternalMessageCriteria(), first, max, sortProperties);

		verify(labMessageIndexDtoCriteriaQuery).orderBy(orderListArgumentCaptor.capture());
		assertEquals(2, orderListArgumentCaptor.getValue().size());
		assertEquals(expectedResult, result);
	}

	@Test
	public void testSave() {

		ExternalMessageDto externalMessageDto = new ExternalMessageDto();
		String testUuid = "Test UUID";
		externalMessageDto.setUuid(testUuid);
		ExternalMessage externalMessage = new ExternalMessage();

		when(externalMessageService.getByUuid(testUuid)).thenReturn(externalMessage);
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
