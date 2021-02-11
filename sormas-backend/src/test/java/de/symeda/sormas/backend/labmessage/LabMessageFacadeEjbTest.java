package de.symeda.sormas.backend.labmessage;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Root;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import de.symeda.sormas.api.labmessage.LabMessageCriteria;
import de.symeda.sormas.api.labmessage.LabMessageDto;
import de.symeda.sormas.api.labmessage.LabMessageIndexDto;
import de.symeda.sormas.api.systemevents.SystemEventDto;
import de.symeda.sormas.api.utils.SortProperty;
import de.symeda.sormas.backend.systemevent.SystemEventFacadeEjb;

@RunWith(MockitoJUnitRunner.class)
public class LabMessageFacadeEjbTest {

	@Mock
	private EntityManager em;
	@Mock
	private LabMessageService labMessageService;
	@Mock
	private SystemEventFacadeEjb.SystemEventFacadeEjbLocal systemEventFacade;

	@InjectMocks
	private LabMessageFacadeEjb sut;

	@Mock
	private CriteriaBuilder criteriaBuilder;
	@Mock
	private CriteriaQuery<LabMessageIndexDto> labMessageIndexDtoCriteriaQuery;
	@Mock
	private CriteriaQuery<Long> longCriteriaQuery;
	@Mock
	private Root<LabMessage> labMessage;
	@Mock
	private TypedQuery<LabMessageIndexDto> labMessageIndexDtoTypedQuery;
	@Mock

	private TypedQuery<Long> longTypedQuery;
	@Mock
	private Expression<Long> longExpression;

	@Captor
	private ArgumentCaptor<List<Order>> orderListArgumentCaptor;

	@Test
	public void count() {
		when(em.getCriteriaBuilder()).thenReturn(criteriaBuilder);
		when(criteriaBuilder.createQuery(Long.class)).thenReturn(longCriteriaQuery);
		when(longCriteriaQuery.from(LabMessage.class)).thenReturn(labMessage);
		when(criteriaBuilder.countDistinct(labMessage)).thenReturn(longExpression);
		when(em.createQuery(longCriteriaQuery)).thenReturn(longTypedQuery);
		long expected = 1L;
		when(longTypedQuery.getSingleResult()).thenReturn(expected);

		long result = sut.count(new LabMessageCriteria());
		assertEquals(expected, result);
	}

	@Test
	public void getIndexList() {
		int first = 1;
		int max = 1;

		when(em.getCriteriaBuilder()).thenReturn(criteriaBuilder);
		when(criteriaBuilder.createQuery(LabMessageIndexDto.class)).thenReturn(labMessageIndexDtoCriteriaQuery);
		when(labMessageIndexDtoCriteriaQuery.from(LabMessage.class)).thenReturn(labMessage);
		when(em.createQuery(labMessageIndexDtoCriteriaQuery)).thenReturn(labMessageIndexDtoTypedQuery);
		when(labMessageIndexDtoTypedQuery.setFirstResult(first)).thenReturn(labMessageIndexDtoTypedQuery);
		when(labMessageIndexDtoTypedQuery.setMaxResults(max)).thenReturn(labMessageIndexDtoTypedQuery);
		ArrayList<LabMessageIndexDto> expectedResult = new ArrayList<>();
		when(labMessageIndexDtoTypedQuery.getResultList()).thenReturn(expectedResult);
		ArrayList<SortProperty> sortProperties = new ArrayList<>();
		sortProperties.add(new SortProperty(LabMessageIndexDto.UUID));
		sortProperties.add(new SortProperty("No Valid Property"));
		List<LabMessageIndexDto> result = sut.getIndexList(new LabMessageCriteria(), first, max, sortProperties);

		verify(labMessageIndexDtoCriteriaQuery).orderBy(orderListArgumentCaptor.capture());
		assertEquals(2, orderListArgumentCaptor.getValue().size());
		assertEquals(expectedResult, result);
	}

	@Test
	public void fetchAndSaveExternalLabMessages() {
		// since InitialContext is not mockable, it's hard to do any more testing here.
		sut.fetchAndSaveExternalLabMessages();

		verify(systemEventFacade, times(2)).saveSystemEvent(any(SystemEventDto.class));
	}

	@Test
	public void save() {
		LabMessageDto labMessageDto = new LabMessageDto();
		String testUuid = "Test UUID";
		labMessageDto.setUuid(testUuid);
		LabMessage labMessage = new LabMessage();

		when(labMessageService.getByUuid(testUuid)).thenReturn(labMessage);
		sut.save(labMessageDto);

		verify(labMessageService).ensurePersisted(labMessage);
	}

	@Test
	public void getByUuid() {
		String testUuid = "test UUID";
		LabMessage labMessage = new LabMessage();
		when(labMessageService.getByUuid(testUuid)).thenReturn(labMessage);
		LabMessageDto result = sut.getByUuid(testUuid);
		assertEquals(sut.toDto(labMessage), result);
	}
}
