package de.symeda.sormas.backend.patch;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.when;

import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import de.symeda.sormas.api.systemconfiguration.SystemConfigurationValueFacade;
import de.symeda.sormas.backend.AbstractUnitTest;
import de.symeda.sormas.backend.patch.alias.PathAliasHelper;
import org.mockito.Mockito;

class PatchFieldHelperTest extends AbstractUnitTest {

	@InjectMocks
	private PatchFieldHelper victim;

	@Mock
	private PathAliasHelper pathAliasHelper;

	@Mock
	private SystemConfigurationValueFacade systemConfigurationValueFacade;

	@Mock
	private BusinessDtoFacade businessDtoFacade;

	@BeforeEach
	void setUp() {
		Mockito.lenient()
			.when(pathAliasHelper.supportedPrefixes())
			.thenReturn(Set.of("CaseData", "Person", "Symptoms", "Immunization", "Vaccination"));
		Mockito.lenient()
			.when(businessDtoFacade.fetchablePrefixes())
			.thenReturn(Set.of("CaseData", "Person", "Symptoms", "Immunization", "Vaccination"));
	}

	@ParameterizedTest
	@ValueSource(strings = {
		"Person.firstName_duplicate_",
		"Person._duplicate_firstName",
		"CaseData.symptoms_duplicate_.onsetDate",
		"Person.firstName_duplicate_2" })
	void checkIfPathIsInvalid_duplicateMarker_returnsDuplicateField(String path) {
		// EXECUTE
		PathFailureCause result = victim.checkIfPathIsInvalid(path);

		// CHECK
		assertEquals(PathFailureCause.DUPLICATE_FIELD, result);
	}

	@Test
	void checkIfPathIsInvalid_duplicateMarkerRelatedPatchCause_mapsToDataPatchFailureCause() {
		// PREPARE
		PathFailureCause cause = PathFailureCause.DUPLICATE_FIELD;

		// EXECUTE & CHECK
		assertEquals(de.symeda.sormas.api.patch.DataPatchFailureCause.DUPLICATE_FIELD, cause.getRelatedPatchFailureCause());
	}

	@Test
	void checkIfPathIsInvalid_duplicateMarkerRelatedRetrievalCause_mapsToPartialRetrievalFailureCause() {
		// PREPARE
		PathFailureCause cause = PathFailureCause.DUPLICATE_FIELD;

		// EXECUTE & CHECK
		assertEquals(
			de.symeda.sormas.api.patch.partial_retrieval.PartialRetrievalFailureCause.DUPLICATE_FIELD,
			cause.getRelatedRetrieveFailureCause());
	}

	@Test
	void checkIfPathIsInvalid_validPath_returnsNull() {
		// PREPARE
		String path = "Person.firstName";

		// EXECUTE
		PathFailureCause result = victim.checkIfPathIsInvalid(path);

		// CHECK
		assertNull(result);
	}

	@Test
	void checkIfPathIsInvalid_noDotAndDuplicateMarker_returnsInvalidPathFormat() {
		// PREPARE
		String path = "firstName_duplicate_";

		// EXECUTE
		PathFailureCause result = victim.checkIfPathIsInvalid(path);

		// CHECK
		assertEquals(PathFailureCause.INVALID_PATH_FORMAT, result);
	}

	@ParameterizedTest
	@ValueSource(strings = {
		"CaseData.uuid",
		"Person.deleted",
		"CaseData.reportingUser",
		"Immunization.relatedCase" })
	void checkIfPathIsInvalid_forbiddenField_returnsForbiddenField(String path) {
		// EXECUTE
		PathFailureCause result = victim.checkIfPathIsInvalid(path);

		// CHECK
		assertEquals(PathFailureCause.FORBIDDEN_FIELD, result);
	}
}
