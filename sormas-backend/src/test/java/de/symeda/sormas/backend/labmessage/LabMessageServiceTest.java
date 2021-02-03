package de.symeda.sormas.backend.labmessage;

import de.symeda.sormas.api.labmessage.LabMessageCriteria;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class LabMessageServiceTest {

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
    public void buildCriteriaFilter() {
        LabMessageService sut = new LabMessageService();
        boolean isProcessed = true;
        when(criteria.getProcessed()).thenReturn(isProcessed);
        when(labMessage.get(LabMessage.PROCESSED)).thenReturn(objectPath);

        when(cb.equal(objectPath, isProcessed)).thenReturn(predicate);

        Predicate result = sut.buildCriteriaFilter(cb, labMessage, criteria);

        assertEquals(predicate, result);
    }
}