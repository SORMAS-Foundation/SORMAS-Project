package de.symeda.sormas.backend.central;

import de.symeda.sormas.api.sormastosormas.SormasServerDescriptorDto;
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
		List<SormasServerDescriptorDto> result = MockProducer.getEtcdCentralClient().getWithPrefix("/mock", SormasServerDescriptorDto.class);
		// the descriptor is a simple class, suitable for testing
		final SormasServerDescriptorDto descriptor = result.get(0);
		assertEquals(descriptor.getId(), "myId");
		assertEquals(descriptor.getName(), "myName");
		assertEquals(descriptor.getHostName(), "example.org");

	}

	protected void testMock() throws IOException {
		Mockito.when(MockProducer.getEtcdCentralClient().getWithPrefix(eq("/mock"), eq(SormasServerDescriptorDto.class))).thenAnswer(invocation -> {
			List<SormasServerDescriptorDto> list = new ArrayList<>();
			SormasServerDescriptorDto descriptor = new SormasServerDescriptorDto("myId", "myName", "example.org");
			list.add(descriptor);
			return list;
		});
	}
}
