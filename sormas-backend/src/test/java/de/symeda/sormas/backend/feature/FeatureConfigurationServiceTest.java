package de.symeda.sormas.backend.feature;

import org.junit.Test;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.feature.FeatureType;
import de.symeda.sormas.backend.AbstractBeanTest;
import de.symeda.sormas.backend.TestDataCreator.RDCF;
import de.symeda.sormas.backend.region.District;
import de.symeda.sormas.backend.region.Region;

public class FeatureConfigurationServiceTest extends AbstractBeanTest {

	@Test
	public void testCreateMissingFeatureConfigurations() {

		createConfigurations();
		FeatureConfigurationService featureConfigurationService = getBean(FeatureConfigurationService.class);
		featureConfigurationService.createMissingFeatureConfigurations();
	}

	@Test
	public void testUpdateFeatureConfigurations() {

		createConfigurations();
		FeatureConfigurationService featureConfigurationService = getBean(FeatureConfigurationService.class);

		/*
		 * update relies on that all serverFeature configurations are already present,
		 * that's why the createMissing needs to be run before.
		 */
		featureConfigurationService.createMissingFeatureConfigurations();
		featureConfigurationService.updateFeatureConfigurations();
	}

	private void createConfigurations() {

		// Some serverFeatures
		build(FeatureType.EVENT_SURVEILLANCE);
		build(FeatureType.TASK_MANAGEMENT);

		// Some features configured on district level
		RDCF rdcf = creator.createRDCF();
		Region region = getRegionService().getByUuid(rdcf.region.getUuid());
		build(FeatureType.LINE_LISTING, null, region, getDistrictService().getByUuid(rdcf.district.getUuid()));
		build(FeatureType.LINE_LISTING, null, region, creator.createDistrict("d2", region));
	}

	private FeatureConfiguration build(FeatureType type) {

		return build(type, null, null, null);
	}

	private FeatureConfiguration build(FeatureType type, Disease disease, Region region, District district) {

		FeatureConfigurationService featureConfigurationService = getBean(FeatureConfigurationService.class);

		FeatureConfiguration entity = new FeatureConfiguration();
		entity.setFeatureType(type);
		entity.setEnabled(type.isEnabledDefault());
		entity.setDisease(disease);
		entity.setRegion(region);
		entity.setDistrict(district);
		featureConfigurationService.ensurePersisted(entity);

		return entity;
	}
}
