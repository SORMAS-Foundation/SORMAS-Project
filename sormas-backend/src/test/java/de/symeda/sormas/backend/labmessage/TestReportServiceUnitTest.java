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
	private CriteriaBuilder cb;
	@Mock
	private Root<TestReport> testReportRoot;
	@Mock
	private Predicate predicate;

	@Test
	public void testCreateDefaultFilter() {

		TestReportService sut = new TestReportService();

		when(cb.isFalse(any())).thenReturn(predicate);

		Predicate result = sut.createDefaultFilter(cb, testReportRoot);

		assertEquals(predicate, result);
	}
}
