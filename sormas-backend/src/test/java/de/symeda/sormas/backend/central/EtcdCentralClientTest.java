package de.symeda.sormas.backend.central;

import de.symeda.sormas.api.sormastosormas.SormasServerDescriptor;
import de.symeda.sormas.backend.MockProducer;
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
		List<SormasServerDescriptor> result = MockProducer.getEtcdCentralClient().getWithPrefix("/mock", SormasServerDescriptor.class);
		// the descriptor is a simple class, suitable for testing
		final SormasServerDescriptor descriptor = result.get(0);
		assertEquals(descriptor.getId(), "myId");
		assertEquals(descriptor.getName(), "myName");
		assertEquals(descriptor.getHostName(), "example.org");

	}

	protected void testMock() throws IOException {
		Mockito.when(MockProducer.getEtcdCentralClient().getWithPrefix(eq("/mock"), eq(SormasServerDescriptor.class))).thenAnswer(invocation -> {
			List<SormasServerDescriptor> list = new ArrayList<>();
			SormasServerDescriptor descriptor = new SormasServerDescriptor("myId", "myName", "example.org");
			list.add(descriptor);
			return list;
		});
	}
}
