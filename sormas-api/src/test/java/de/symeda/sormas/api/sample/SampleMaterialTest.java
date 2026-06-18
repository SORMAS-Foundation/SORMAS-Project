package de.symeda.sormas.api.sample;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;

import java.util.Arrays;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;

public class SampleMaterialTest {

	@Test
	public void testActiveMaterialsMatchUpgradedSpec() {

		List<SampleMaterial> active = Arrays.stream(SampleMaterial.values()).filter(m -> !m.isDeprecated()).collect(Collectors.toList());

		// Issue #13948 (#1): 44 from #13560 + 4 new (cord blood, lung tissue, placenta, ulcer swab)
		// + 4 un-deprecated (biopsy, duodenum/intestinal fluid, lower respiratory tract)
		// + oropharyngeal swab un-deprecated, throat swab now deprecated = 52 active specimen types
		assertThat(active, hasSize(52));
	}

	@Test
	public void testDeprecatedMaterials() {

		List<SampleMaterial> deprecated = Arrays.stream(SampleMaterial.values()).filter(SampleMaterial::isDeprecated).collect(Collectors.toList());

		assertThat(
			deprecated,
			containsInAnyOrder(
				SampleMaterial.URINE_PM,
				SampleMaterial.CORNEA_PM,
				SampleMaterial.NASAL_SWAB,
				SampleMaterial.NASOPHARYNGEAL_LAVAGE,
				SampleMaterial.THROAT_SWAB,
				SampleMaterial.THROAT_ASPIRATE,
				SampleMaterial.SOFT_TISSUE,
				SampleMaterial.BONE_AND_JOINT,
				SampleMaterial.OTHER));
	}

	@Test
	public void testSnomedCodesForActiveMaterials() {

		Map<SampleMaterial, String> expected = expectedSnomedCodes();

		for (SampleMaterial material : SampleMaterial.values()) {
			if (material.isDeprecated()) {
				continue;
			}
			assertThat("SNOMED code for " + material.name(), material.getSnomedCode(), is(expected.get(material)));
		}
	}

	private static Map<SampleMaterial, String> expectedSnomedCodes() {

		EnumMap<SampleMaterial, String> map = new EnumMap<>(SampleMaterial.class);
		map.put(SampleMaterial.ABSCESS_SWAB, "258497007");
		map.put(SampleMaterial.AMNIOTIC_FLUID, "119373006");
		map.put(SampleMaterial.BIOPSY, "86273004");
		map.put(SampleMaterial.ANTERIOR_NARES_SWAB, "697989009");
		map.put(SampleMaterial.ASPIRATE, "119295008");
		map.put(SampleMaterial.BLOOD, "119297000");
		map.put(SampleMaterial.BONE, "258417006");
		map.put(SampleMaterial.BONE_MARROW, "119359002");
		map.put(SampleMaterial.BRAIN_TISSUE, "256865009");
		map.put(SampleMaterial.BRONCHOALVEOLAR_LAVAGE, "258607008");
		map.put(SampleMaterial.CATHETER_EXIT_SITE, "16227651000119100");
		map.put(SampleMaterial.CEREBROSPINAL_FLUID, "258450006");
		map.put(SampleMaterial.CLINICAL_SAMPLE, "123038009");
		map.put(SampleMaterial.CONJUNCTIVAL_SWAB, "258498002");
		map.put(SampleMaterial.CORD_BLOOD, "122556008");
		map.put(SampleMaterial.CRUST, "1332490003");
		map.put(SampleMaterial.DRY_BLOOD, "440500007");
		map.put(SampleMaterial.DUODENUM_FLUID, "20779001");
		map.put(SampleMaterial.EDTA_WHOLE_BLOOD, "258580003");
		map.put(SampleMaterial.ENDOTRACHEAL_ASPIRATE, "119307008");
		map.put(SampleMaterial.EYE, "119399004");
		map.put(SampleMaterial.STOOL, "119339001");
		map.put(SampleMaterial.GASTRIC_FLUID, "258459007");
		map.put(SampleMaterial.GENITAL_SWAB, "258508008");
		// INTESTINAL_FLUID is active but has no canonical SNOMED-CT code on the master list
		map.put(SampleMaterial.INTESTINAL_FLUID, null);
		map.put(SampleMaterial.LOWER_RESPIRATORY_TRACT, "258606004");
		map.put(SampleMaterial.LUNG_TISSUE, "399492000");
		map.put(SampleMaterial.MIDDLE_EAR_FLUID, "258466008");
		map.put(SampleMaterial.NP_ASPIRATE, "429931000124105");
		map.put(SampleMaterial.NP_SWAB, "258500001");
		map.put(SampleMaterial.NUCHAL_SKIN_BIOPSY, "309066003");
		map.put(SampleMaterial.OP_ASPIRATE, "258412000");
		map.put(SampleMaterial.OROPHARYNGEAL_SWAB, "258529004");
		map.put(SampleMaterial.PERITONEAL_FLUID, "168139001");
		map.put(SampleMaterial.PLACENTA, "122736005");
		map.put(SampleMaterial.PLASMA, "50863008");
		map.put(SampleMaterial.PLEURAL_FLUID, "418564007");
		map.put(SampleMaterial.PUS, "258502009");
		map.put(SampleMaterial.RECTAL_SWAB, "258528007");
		map.put(SampleMaterial.SALIVA, "119342007");
		map.put(SampleMaterial.SEMEN, "734846002");
		map.put(SampleMaterial.SERA, "119364003");
		map.put(SampleMaterial.SKIN, "119325001");
		map.put(SampleMaterial.SPUTUM, "119334006");
		map.put(SampleMaterial.SWAB_UNSPECIFIED, "257261003");
		map.put(SampleMaterial.SYNOVIAL_FLUID, "264380007");
		map.put(SampleMaterial.TEARS, "122594008");
		map.put(SampleMaterial.TISSUE, "119376003");
		map.put(SampleMaterial.ULCER_SWAB, "472871003");
		map.put(SampleMaterial.URINE, "122575003");
		map.put(SampleMaterial.WOUND, "119365002");
		// UNKNOWN has no canonical SNOMED-CT code
		map.put(SampleMaterial.UNKNOWN, null);
		return map;
	}

	@Test
	public void testToStringRendersDetailsForClinicalSample() {

		assertThat(SampleMaterial.toString(SampleMaterial.CLINICAL_SAMPLE, "swab from lesion"), is("swab from lesion"));
		// Without details the caption is shown
		assertThat(SampleMaterial.toString(SampleMaterial.CLINICAL_SAMPLE, null), is(SampleMaterial.CLINICAL_SAMPLE.toString()));
		// Legacy OTHER keeps its details-only rendering
		assertThat(SampleMaterial.toString(SampleMaterial.OTHER, "free text"), is("free text"));
	}
}
