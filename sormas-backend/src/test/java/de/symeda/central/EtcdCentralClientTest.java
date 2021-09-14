package de.symeda.central;

import de.symeda.sormas.backend.MockProducer;
import de.symeda.sormas.backend.central.EtcdCentralClient;
import org.junit.Test;

import org.mockito.Mockito;

import java.io.IOException;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.eq;

public class EtcdCentralClientTest {

	@Test
	public void testMockIsWorking() throws IOException {
		testMock();
		List<EtcdCentralClient.KeyValue> result = MockProducer.getEtcdCentralClient().getWithPrefix("/mock");
		assertEquals(result.get(0).getKey(), "foo");
		assertEquals(result.get(0).getValue(), "bar");

	}

	protected void testMock() throws IOException {
		Mockito.when(MockProducer.getEtcdCentralClient().getWithPrefix(eq("/mock"))).thenAnswer(invocation -> {
			List<EtcdCentralClient.KeyValue> list = new ArrayList<>();
			EtcdCentralClient.KeyValue keyValue = new EtcdCentralClient.KeyValue("foo", "bar");
			list.add(keyValue);
			return list;

		});

	}

}
