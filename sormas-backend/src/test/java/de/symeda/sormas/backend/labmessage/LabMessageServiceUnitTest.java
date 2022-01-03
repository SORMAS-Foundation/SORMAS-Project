package de.symeda.sormas.backend.labmessage;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import de.symeda.sormas.api.labmessage.LabMessageDto;
import de.symeda.sormas.api.labmessage.TestReportDto;
import de.symeda.sormas.api.sample.PathogenTestResultType;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import de.symeda.sormas.api.labmessage.LabMessageCriteria;
import de.symeda.sormas.api.labmessage.LabMessageStatus;

@RunWith(MockitoJUnitRunner.class)
public class LabMessageServiceUnitTest {

	@Mock
	private CriteriaBuilder cb;
	@Mock
	private Root<LabMessage> labMessage;
	@Mock
	private LabMessageCriteria criteria;
	@Mock
	private Path<Object> objectPath;
	@Mock
	private Predicate predicate;

	@Test
	public void testBuildCriteriaFilter() {

		LabMessageService sut = new LabMessageService();
		LabMessageStatus status = LabMessageStatus.PROCESSED;

		when(criteria.getLabMessageStatus()).thenReturn(status);
		when(labMessage.get(LabMessage.STATUS)).thenReturn(objectPath);

		when(cb.equal(objectPath, status)).thenReturn(predicate);

		Predicate result = sut.buildCriteriaFilter(cb, labMessage, criteria);

		assertEquals(predicate, result);
	}

	@Test
	public void testCreateDefaultFilter() {

		LabMessageService sut = new LabMessageService();
		when(cb.isFalse(any())).thenReturn(predicate);

		Predicate result = sut.createDefaultFilter(cb, labMessage);

		assertEquals(predicate, result);
	}

}
