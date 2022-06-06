package de.symeda.sormas.backend.externalmessage;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import de.symeda.sormas.api.externalmessage.ExternalMessageCriteria;
import de.symeda.sormas.api.externalmessage.ExternalMessageStatus;

@RunWith(MockitoJUnitRunner.class)
public class ExternalMessageServiceUnitTest {

	@Mock
	private CriteriaBuilder cb;
	@Mock
	private Root<ExternalMessage> labMessage;
	@Mock
	private ExternalMessageCriteria criteria;
	@Mock
	private Path<Object> objectPath;
	@Mock
	private Predicate predicate;

	@Test
	public void testBuildCriteriaFilter() {

		ExternalMessageService sut = new ExternalMessageService();
		ExternalMessageStatus status = ExternalMessageStatus.PROCESSED;

		when(criteria.getExternalMessageStatus()).thenReturn(status);
		when(labMessage.get(ExternalMessage.STATUS)).thenReturn(objectPath);

		when(cb.equal(objectPath, status)).thenReturn(predicate);

		Predicate result = sut.buildCriteriaFilter(cb, labMessage, criteria);

		assertEquals(predicate, result);
	}

}
