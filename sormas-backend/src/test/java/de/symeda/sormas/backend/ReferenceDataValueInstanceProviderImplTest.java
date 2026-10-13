package de.symeda.sormas.backend;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import de.symeda.sormas.api.ReferenceDto;
import de.symeda.sormas.api.infrastructure.country.CountryFacade;
import de.symeda.sormas.api.infrastructure.country.CountryReferenceDto;
import de.symeda.sormas.api.infrastructure.region.RegionFacade;
import de.symeda.sormas.api.infrastructure.region.RegionReferenceDto;
import de.symeda.sormas.backend.util.InstanceProvider;

class ReferenceDataValueInstanceProviderImplTest extends AbstractUnitTest {

	private ReferenceDataValueInstanceProviderImpl sut;

	@BeforeEach
	void setUp() throws Exception {
		sut = new ReferenceDataValueInstanceProviderImpl();
		Method init = ReferenceDataValueInstanceProviderImpl.class.getDeclaredMethod("init");
		init.setAccessible(true);
		init.invoke(sut);
	}

	@Test
	void getAll_knownReferenceType_returnsList() {
		// PREPARE
		CountryFacade mockFacade = mock(CountryFacade.class);
		CountryReferenceDto country = new CountryReferenceDto("ABCD-1234-EFGH-5678-IJKL-MNOP", "France", "FR");
		when(mockFacade.getAllActiveAsReference()).thenReturn(List.of(country));

		try (MockedStatic<InstanceProvider> mockedStatic = mockStatic(InstanceProvider.class)) {
			mockedStatic.when(() -> InstanceProvider.getInstanceFor(CountryFacade.class)).thenReturn(mockFacade);

			// EXECUTE
			List<CountryReferenceDto> result = sut.getAll(CountryReferenceDto.class);

			// CHECK
			assertEquals(List.of(country), result);
		}
	}

	@Test
	void getAll_unknownReferenceType_throwsIllegalStateException() {
		// PREPARE (unregistered type — no mock needed)

		// EXECUTE & CHECK
		assertThrows(IllegalStateException.class, () -> sut.getAll(UnregisteredReferenceDto.class));
	}

	@Test
	void getOne_matchByCaption_returnsMatch() {
		// PREPARE
		CountryFacade mockFacade = mock(CountryFacade.class);
		CountryReferenceDto country = new CountryReferenceDto("ABCD-1234-EFGH-5678-IJKL-MNOP", "France", "FR");
		when(mockFacade.getAllActiveAsReference()).thenReturn(List.of(country));

		try (MockedStatic<InstanceProvider> mockedStatic = mockStatic(InstanceProvider.class)) {
			mockedStatic.when(() -> InstanceProvider.getInstanceFor(CountryFacade.class)).thenReturn(mockFacade);

			// EXECUTE
			Optional<CountryReferenceDto> result = sut.getOne("France", CountryReferenceDto.class);

			// CHECK
			assertTrue(result.isPresent());
			assertEquals(country, result.get());
		}
	}

	@Test
	void getOne_matchByCaptionCaseInsensitive_returnsMatch() {
		// PREPARE
		CountryFacade mockFacade = mock(CountryFacade.class);
		CountryReferenceDto country = new CountryReferenceDto("ABCD-1234-EFGH-5678-IJKL-MNOP", "France", "FR");
		when(mockFacade.getAllActiveAsReference()).thenReturn(List.of(country));

		try (MockedStatic<InstanceProvider> mockedStatic = mockStatic(InstanceProvider.class)) {
			mockedStatic.when(() -> InstanceProvider.getInstanceFor(CountryFacade.class)).thenReturn(mockFacade);

			// EXECUTE
			Optional<CountryReferenceDto> result = sut.getOne("FRANCE", CountryReferenceDto.class);

			// CHECK
			assertTrue(result.isPresent());
			assertEquals(country, result.get());
		}
	}

	@Test
	void getOne_matchByCaptionWithAccents_returnsMatch() {
		// PREPARE
		CountryFacade mockFacade = mock(CountryFacade.class);
		CountryReferenceDto country = new CountryReferenceDto("ABCD-1234-EFGH-5678-IJKL-MNOP", "Côte d'Ivoire", "CI");
		when(mockFacade.getAllActiveAsReference()).thenReturn(List.of(country));

		try (MockedStatic<InstanceProvider> mockedStatic = mockStatic(InstanceProvider.class)) {
			mockedStatic.when(() -> InstanceProvider.getInstanceFor(CountryFacade.class)).thenReturn(mockFacade);

			// EXECUTE
			Optional<CountryReferenceDto> result = sut.getOne("Cote d'Ivoire", CountryReferenceDto.class);

			// CHECK
			assertTrue(result.isPresent());
			assertEquals(country, result.get());
		}
	}

	@Test
	void getOne_matchByExternalId_returnsMatch() {
		// PREPARE
		RegionFacade mockFacade = mock(RegionFacade.class);
		RegionReferenceDto region = new RegionReferenceDto("ABCD-1234-EFGH-5678-IJKL-MNOP", "North Region", "EXT-001");
		when(mockFacade.getAllActiveAsReference()).thenReturn(List.of(region));

		try (MockedStatic<InstanceProvider> mockedStatic = mockStatic(InstanceProvider.class)) {
			mockedStatic.when(() -> InstanceProvider.getInstanceFor(RegionFacade.class)).thenReturn(mockFacade);

			// EXECUTE
			Optional<RegionReferenceDto> result = sut.getOne("EXT-001", RegionReferenceDto.class);

			// CHECK
			assertTrue(result.isPresent());
			assertEquals(region, result.get());
		}
	}

	@Test
	void getOne_matchByExternalIdCaseInsensitive_returnsMatch() {
		// PREPARE
		RegionFacade mockFacade = mock(RegionFacade.class);
		RegionReferenceDto region = new RegionReferenceDto("ABCD-1234-EFGH-5678-IJKL-MNOP", "North Region", "EXT-001");
		when(mockFacade.getAllActiveAsReference()).thenReturn(List.of(region));

		try (MockedStatic<InstanceProvider> mockedStatic = mockStatic(InstanceProvider.class)) {
			mockedStatic.when(() -> InstanceProvider.getInstanceFor(RegionFacade.class)).thenReturn(mockFacade);

			// EXECUTE
			Optional<RegionReferenceDto> result = sut.getOne("ext-001", RegionReferenceDto.class);

			// CHECK
			assertTrue(result.isPresent());
			assertEquals(region, result.get());
		}
	}

	@Test
	void getOne_noMatch_returnsEmpty() {
		// PREPARE
		CountryFacade mockFacade = mock(CountryFacade.class);
		CountryReferenceDto country = new CountryReferenceDto("ABCD-1234-EFGH-5678-IJKL-MNOP", "France", "FR");
		when(mockFacade.getAllActiveAsReference()).thenReturn(List.of(country));

		try (MockedStatic<InstanceProvider> mockedStatic = mockStatic(InstanceProvider.class)) {
			mockedStatic.when(() -> InstanceProvider.getInstanceFor(CountryFacade.class)).thenReturn(mockFacade);

			// EXECUTE
			Optional<CountryReferenceDto> result = sut.getOne("Germany", CountryReferenceDto.class);

			// CHECK
			assertTrue(result.isEmpty());
		}
	}

	@Test
	void getOne_emptyList_returnsEmpty() {
		// PREPARE
		CountryFacade mockFacade = mock(CountryFacade.class);
		when(mockFacade.getAllActiveAsReference()).thenReturn(List.of());

		try (MockedStatic<InstanceProvider> mockedStatic = mockStatic(InstanceProvider.class)) {
			mockedStatic.when(() -> InstanceProvider.getInstanceFor(CountryFacade.class)).thenReturn(mockFacade);

			// EXECUTE
			Optional<CountryReferenceDto> result = sut.getOne("France", CountryReferenceDto.class);

			// CHECK
			assertTrue(result.isEmpty());
		}
	}

	private static class UnregisteredReferenceDto extends ReferenceDto {
	}
}
