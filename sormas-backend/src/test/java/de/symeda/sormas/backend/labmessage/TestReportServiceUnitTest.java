package de.symeda.sormas.backend.labmessage;

import de.symeda.sormas.backend.AbstractBeanTest;
import de.symeda.sormas.backend.common.AbstractDomainObject;

import de.symeda.sormas.api.labmessage.LabMessageDto;
import de.symeda.sormas.api.labmessage.TestReportDto;
import de.symeda.sormas.api.sample.PathogenTestReferenceDto;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import javax.persistence.criteria.Path;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.Silent.class)
public class TestReportServiceUnitTest extends AbstractBeanTest {

	@Mock
	private EntityManager em;
	@Mock
	private CriteriaBuilder cb;
	@Mock
	private CriteriaQuery<TestReport> cq;
	@Mock
	private Root<TestReport> testReportRoot;
	@Mock
	private Predicate predicate;
	@Mock
	private Join testReportJoin;
	@Mock
	private TypedQuery typedQuery;
	@Mock
	private Path path;
	@InjectMocks
	private TestReportService sut;

	@Test
	public void createDefaultFilter() {

		TestReportService sut = new TestReportService();

		when(cb.isFalse(any())).thenReturn(predicate);

		Predicate result = sut.createDefaultFilter(cb, testReportRoot);

		assertEquals(predicate, result);
	}

	@Test
	public void getByPathogenTestUuids() {

		LabMessageDto labMessage = creator.createLabMessage(null);
		PathogenTestReferenceDto pathogenTest = new PathogenTestReferenceDto("UUID");
		TestReportDto report = creator.createTestReport(pathogenTest, labMessage.toReference());

		ArrayList expectedResult = new ArrayList();
		expectedResult.add(report);

		ArrayList uuidList = new ArrayList();
		uuidList.add(pathogenTest.getUuid());

		when(em.getCriteriaBuilder()).thenReturn(cb);
		when(cb.createQuery(TestReport.class)).thenReturn(cq);
		when(cq.from(TestReport.class)).thenReturn(testReportRoot);
		when(testReportRoot.join(TestReport.PATHOGEN_TEST, JoinType.LEFT)).thenReturn(testReportJoin);
		when(testReportJoin.get(AbstractDomainObject.UUID)).thenReturn(path);
		when(em.createQuery(cq)).thenReturn(typedQuery);
		when(typedQuery.getResultList()).thenReturn(expectedResult);

		List<TestReport> result = sut.getByPathogenTestUuidsBatched(uuidList, false);
		assertEquals(expectedResult, result);

	}

	@Test
	public void getByPathogenTestUuid() {

		LabMessageDto labMessage = creator.createLabMessage(null);
		PathogenTestReferenceDto pathogenTest = new PathogenTestReferenceDto("UUID");
		TestReportDto report = creator.createTestReport(pathogenTest, labMessage.toReference());

		ArrayList expectedResult = new ArrayList();
		expectedResult.add(report);

		when(em.getCriteriaBuilder()).thenReturn(cb);
		when(cb.createQuery(TestReport.class)).thenReturn(cq);
		when(cq.from(TestReport.class)).thenReturn(testReportRoot);
		when(testReportRoot.join(TestReport.PATHOGEN_TEST, JoinType.LEFT)).thenReturn(testReportJoin);
		when(testReportJoin.get(AbstractDomainObject.UUID)).thenReturn(path);
		when(em.createQuery(cq)).thenReturn(typedQuery);
		when(typedQuery.getResultList()).thenReturn(expectedResult);

		List<TestReport> result = sut.getByPathogenTestUuid(pathogenTest.getUuid(), false);
		assertEquals(expectedResult, result);

	}
}
