/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2023 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package de.symeda.sormas.backend.environment.environmentsample;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.emptyString;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.junit.jupiter.api.Test;

import de.symeda.sormas.api.common.DeletionDetails;
import de.symeda.sormas.api.common.DeletionReason;
import de.symeda.sormas.api.environment.EnvironmentDto;
import de.symeda.sormas.api.environment.EnvironmentMedia;
import de.symeda.sormas.api.environment.environmentsample.EnvironmentSampleCriteria;
import de.symeda.sormas.api.environment.environmentsample.EnvironmentSampleDto;
import de.symeda.sormas.api.environment.environmentsample.EnvironmentSampleMaterial;
import de.symeda.sormas.api.environment.environmentsample.EnvironmentSampleReferenceDto;
import de.symeda.sormas.api.infrastructure.facility.FacilityDto;
import de.symeda.sormas.api.infrastructure.facility.FacilityType;
import de.symeda.sormas.api.person.PersonAddressType;
import de.symeda.sormas.api.user.DefaultUserRole;
import de.symeda.sormas.api.user.JurisdictionLevel;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.api.utils.AccessDeniedException;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.api.utils.YesNoUnknown;
import de.symeda.sormas.backend.AbstractBeanTest;
import de.symeda.sormas.backend.TestDataCreator;

public class EnvironmentSampleFacadeEjbTest extends AbstractBeanTest {

	private TestDataCreator.RDCF rdcf;
	private FacilityDto lab;
	private UserDto reportingUser;
	private EnvironmentDto environment;

	private TestDataCreator.RDCF rdcf2;
	private UserDto userInDifferentJurisdiction;

	@Override
	public void init() {
		super.init();

		rdcf = creator.createRDCF();
		lab = creator.createFacility("Lab", rdcf.region, rdcf.district, rdcf.community, FacilityType.LABORATORY);
		reportingUser = creator.createUser(rdcf, creator.getUserRoleReference(DefaultUserRole.ENVIRONMENTAL_SURVEILLANCE_USER));
		environment = creator.createEnvironment("Test env", EnvironmentMedia.WATER, reportingUser.toReference(), rdcf);

		rdcf2 = creator.createRDCF();
		userInDifferentJurisdiction =
			creator.createUser(rdcf2, "Env", "Surv2", creator.getUserRoleReference(DefaultUserRole.ENVIRONMENTAL_SURVEILLANCE_USER));

	}

	@Test
	public void testSave() {
		EnvironmentSampleDto dto = creator.createEnvironmentSample(environment.toReference(), reportingUser.toReference(), lab.toReference(), s -> {
			s.setSampleMaterial(EnvironmentSampleMaterial.WATER);
			s.setLaboratory(lab.toReference());
			s.setFieldSampleId("123");
			s.setChlorineResiduals(1.0f);
			s.setPhValue(8);
			s.setHeavyRain(YesNoUnknown.YES);
			s.setDispatched(true);
			s.setReceived(false);
			s.setGeneralComment("General comment");
			s.getLocation().setAddressType(PersonAddressType.OTHER_ADDRESS);
			s.getLocation().setCity("City");
			s.getLocation().setStreet("Street");
		});

		EnvironmentSampleDto createdDto = getEnvironmentSampleFacade().save(dto);

		// returned dto should match the one we sent
		assertThat(createdDto.getUuid(), is(dto.getUuid()));
		assertThat(createdDto.getEnvironment(), is(dto.getEnvironment()));
		assertThat(createdDto.getReportingUser(), is(dto.getReportingUser()));
		assertThat(createdDto.getSampleDateTime(), is(dto.getSampleDateTime()));
		assertThat(createdDto.getSampleMaterial(), is(dto.getSampleMaterial()));
		assertThat(createdDto.getLaboratory(), is(dto.getLaboratory()));
		assertThat(createdDto.getFieldSampleId(), is(dto.getFieldSampleId()));
		assertThat(createdDto.getChlorineResiduals(), is(dto.getChlorineResiduals()));
		assertThat(createdDto.getPhValue(), is(dto.getPhValue()));
		assertThat(createdDto.getHeavyRain(), is(dto.getHeavyRain()));
		assertThat(createdDto.isDispatched(), is(dto.isDispatched()));
		assertThat(createdDto.isReceived(), is(dto.isReceived()));
		assertThat(createdDto.getGeneralComment(), is(dto.getGeneralComment()));
		assertThat(createdDto.getLocation().getUuid(), is(dto.getLocation().getUuid()));
		assertThat(createdDto.getLocation().getAddressType(), is(dto.getLocation().getAddressType()));
		assertThat(createdDto.getLocation().getCity(), is(dto.getLocation().getCity()));
		assertThat(createdDto.getLocation().getStreet(), is(dto.getLocation().getStreet()));

		EnvironmentSample entity = getEnvironmentSampleService().getByUuid(dto.getUuid());

		// saved entity should match the dto we sent
		assertThat(entity.getUuid(), is(dto.getUuid()));
		assertThat(entity.getEnvironment().getUuid(), is(dto.getEnvironment().getUuid()));
		assertThat(entity.getReportingUser().getUuid(), is(dto.getReportingUser().getUuid()));
		assertThat(entity.getSampleDateTime().getTime(), is(dto.getSampleDateTime().getTime()));
		assertThat(entity.getSampleMaterial(), is(dto.getSampleMaterial()));
		assertThat(entity.getLaboratory().getUuid(), is(dto.getLaboratory().getUuid()));
		assertThat(entity.getFieldSampleId(), is(dto.getFieldSampleId()));
		assertThat(entity.getChlorineResiduals(), is(dto.getChlorineResiduals()));
		assertThat(entity.getPhValue(), is(dto.getPhValue()));
		assertThat(entity.getHeavyRain(), is(dto.getHeavyRain()));
		assertThat(entity.isDispatched(), is(dto.isDispatched()));
		assertThat(entity.isReceived(), is(dto.isReceived()));
		assertThat(entity.getGeneralComment(), is(dto.getGeneralComment()));
		assertThat(entity.getLocation().getUuid(), is(dto.getLocation().getUuid()));
		assertThat(entity.getLocation().getAddressType(), is(dto.getLocation().getAddressType()));
		assertThat(entity.getLocation().getCity(), is(dto.getLocation().getCity()));
		assertThat(entity.getLocation().getStreet(), is(dto.getLocation().getStreet()));

		dto.setReceived(true);
		Date receivalDate = new Date();
		dto.setReceivalDate(receivalDate);

		EnvironmentSampleDto updatedDto = getEnvironmentSampleFacade().save(dto);

		assertThat(updatedDto.isReceived(), is(true));
		assertThat(updatedDto.getReceivalDate(), is(receivalDate));
	}

	@Test
	public void testGetByUuid() {
		EnvironmentSampleDto originalDto =
			creator.createEnvironmentSample(environment.toReference(), reportingUser.toReference(), lab.toReference(), s -> {
				s.setSampleMaterial(EnvironmentSampleMaterial.WATER);
				s.setLaboratory(lab.toReference());
				s.setFieldSampleId("123");
				s.setChlorineResiduals(1.0f);
				s.setPhValue(8);
				s.setHeavyRain(YesNoUnknown.YES);
				s.setDispatched(true);
				s.setReceived(false);
				s.setGeneralComment("General comment");
				s.getLocation().setAddressType(PersonAddressType.OTHER_ADDRESS);
				s.getLocation().setCity("City");
				s.getLocation().setStreet("Street");
			});

		EnvironmentSampleDto returnedDto = getEnvironmentSampleFacade().getByUuid(originalDto.getUuid());

		assertThat(returnedDto.getUuid(), is(returnedDto.getUuid()));
		assertThat(returnedDto.getEnvironment(), is(returnedDto.getEnvironment()));
		assertThat(returnedDto.getReportingUser(), is(returnedDto.getReportingUser()));
		assertThat(returnedDto.getSampleDateTime(), is(returnedDto.getSampleDateTime()));
		assertThat(returnedDto.getSampleMaterial(), is(returnedDto.getSampleMaterial()));
		assertThat(returnedDto.getLaboratory(), is(returnedDto.getLaboratory()));
		assertThat(returnedDto.getFieldSampleId(), is(returnedDto.getFieldSampleId()));
		assertThat(returnedDto.getChlorineResiduals(), is(returnedDto.getChlorineResiduals()));
		assertThat(returnedDto.getPhValue(), is(returnedDto.getPhValue()));
		assertThat(returnedDto.getHeavyRain(), is(returnedDto.getHeavyRain()));
		assertThat(returnedDto.isDispatched(), is(returnedDto.isDispatched()));
		assertThat(returnedDto.isReceived(), is(returnedDto.isReceived()));
		assertThat(returnedDto.getGeneralComment(), is(returnedDto.getGeneralComment()));
		assertThat(returnedDto.getLocation().getUuid(), is(returnedDto.getLocation().getUuid()));
		assertThat(returnedDto.getLocation().getAddressType(), is(returnedDto.getLocation().getAddressType()));
		assertThat(returnedDto.getLocation().getCity(), is(returnedDto.getLocation().getCity()));
		assertThat(returnedDto.getLocation().getStreet(), is(returnedDto.getLocation().getStreet()));
	}

	@Test
	public void testGetReferenceByUuid() {
		EnvironmentSampleDto dto = creator.createEnvironmentSample(environment.toReference(), reportingUser.toReference(), lab.toReference(), s -> {
			s.setSampleMaterial(EnvironmentSampleMaterial.AIR);
		});

		EnvironmentSampleReferenceDto referenceDto = getEnvironmentSampleFacade().getReferenceByUuid(dto.getUuid());

		assertThat(referenceDto.getUuid(), is(dto.getUuid()));
		assertThat(referenceDto.getCaption(), containsString(EnvironmentSampleMaterial.AIR.toString()));
		assertThat(referenceDto.getCaption(), containsString(DataHelper.getShortUuid(environment.getUuid())));
	}

	@Test
	public void testPseudonymization() {
		EnvironmentSampleDto sample =
			creator.createEnvironmentSample(environment.toReference(), reportingUser.toReference(), lab.toReference(), s -> {
				s.setSampleMaterial(EnvironmentSampleMaterial.OTHER);
				s.setOtherSampleMaterial("Test material");
				s.setFieldSampleId("Test id");
				s.setLaboratoryDetails("Test lab details");
				s.setOtherRequestedPathogenTests("Test pathogen tests");
				s.setDispatched(true);
				s.setDispatchDetails("Test dispatch details");
				s.setLabSampleId("Test lab sample id");
				s.setGeneralComment("Test comment");
				s.getLocation().setCity("Test city");
			});

		assertThat(sample.isPseudonymized(), is(false));
		assertThat(sample.getOtherSampleMaterial(), is("Test material"));
		assertThat(sample.getFieldSampleId(), is("Test id"));
		assertThat(sample.getLaboratoryDetails(), is("Test lab details"));
		assertThat(sample.getOtherRequestedPathogenTests(), is("Test pathogen tests"));
		assertThat(sample.getDispatchDetails(), is("Test dispatch details"));
		assertThat(sample.getLabSampleId(), is("Test lab sample id"));
		assertThat(sample.getGeneralComment(), is("Test comment"));
		assertThat(sample.getLocation().getCity(), is("Test city"));
		assertThat(sample.getLaboratory(), is(lab.toReference()));
		assertThat(sample.getReportingUser(), is(reportingUser.toReference()));
		assertThat(sample.getEnvironment().getCaption(), is(environment.toReference().getCaption()));

		loginWith(userInDifferentJurisdiction);

		EnvironmentSampleDto returnedSample = getEnvironmentSampleFacade().getByUuid(sample.getUuid());

		assertThat(returnedSample.isPseudonymized(), is(true));
		assertThat(returnedSample.getOtherSampleMaterial(), is(emptyString()));
		assertThat(returnedSample.getFieldSampleId(), is(emptyString()));
		assertThat(returnedSample.getLaboratoryDetails(), is(emptyString()));
		assertThat(returnedSample.getOtherRequestedPathogenTests(), is(emptyString()));
		assertThat(returnedSample.getDispatchDetails(), is(emptyString()));
		assertThat(returnedSample.getLabSampleId(), is(emptyString()));
		assertThat(returnedSample.getGeneralComment(), is(emptyString()));
		assertThat(returnedSample.getLocation().getCity(), is(emptyString()));
		assertThat(returnedSample.getLaboratory(), is(nullValue()));
		assertThat(returnedSample.getReportingUser(), is(nullValue()));
		assertThat(returnedSample.getEnvironment().getCaption(), is(emptyString()));
	}

	@Test
	public void testGetAllAfter() {
		EnvironmentSampleDto sampleOwned =
			creator.createEnvironmentSample(environment.toReference(), reportingUser.toReference(), lab.toReference(), s -> {
				s.getLocation().setRegion(rdcf.region);
				s.getLocation().setDistrict(rdcf.district);
			});

		EnvironmentSampleDto sampleWithEmptyLocation =
			creator.createEnvironmentSample(environment.toReference(), userInDifferentJurisdiction.toReference(), lab.toReference(), null);

		EnvironmentSampleDto sampleInJurisdiction =
			creator.createEnvironmentSample(environment.toReference(), userInDifferentJurisdiction.toReference(), lab.toReference(), s -> {
				s.getLocation().setRegion(rdcf.region);
				s.getLocation().setDistrict(rdcf.district);
			});

		EnvironmentSampleDto sampleOutsideJurisdiction =
			creator.createEnvironmentSample(environment.toReference(), userInDifferentJurisdiction.toReference(), lab.toReference(), s -> {
				s.getLocation().setRegion(rdcf2.region);
				s.getLocation().setDistrict(rdcf2.district);
			});

		loginWith(reportingUser);

		List<EnvironmentSampleDto> all = getEnvironmentSampleFacade().getAllAfter(new Date(0));
		// 3 samples in jurisdiction + 1 sample outside jurisdiction
		assertThat(all, hasSize(3));
		assertThat(all.stream().filter(s -> s.getUuid().equals(sampleOwned.getUuid())).findFirst().get().isPseudonymized(), is(false));
		assertThat(all.stream().filter(s -> s.getUuid().equals(sampleWithEmptyLocation.getUuid())).findFirst().get().isPseudonymized(), is(false));
		assertThat(all.stream().filter(s -> s.getUuid().equals(sampleInJurisdiction.getUuid())).findFirst().get().isPseudonymized(), is(false));
	}

	@Test
	public void testUpdatePesudonymized() {
		EnvironmentSampleDto sample =
			creator.createEnvironmentSample(environment.toReference(), reportingUser.toReference(), lab.toReference(), s -> {
				s.setSampleMaterial(EnvironmentSampleMaterial.OTHER);
				s.setOtherSampleMaterial("Test material");
				s.setFieldSampleId("Test id");
				s.setLaboratoryDetails("Test lab details");
				s.setOtherRequestedPathogenTests("Test pathogen tests");
				s.setDispatched(true);
				s.setDispatchDetails("Test dispatch details");
				s.setLabSampleId("Test lab sample id");
				s.setGeneralComment("Test comment");
				s.getLocation().setCity("Test city");
			});

		UserDto noSensitiveUser = creator.createUser(
			rdcf.region.getUuid(),
			rdcf.district.getUuid(),
			null,
			"District",
			"NoSensitive",
			"SurvNoSensitive",
			JurisdictionLevel.DISTRICT,
			UserRight.ENVIRONMENT_SAMPLE_VIEW,
			UserRight.ENVIRONMENT_SAMPLE_EDIT,
			UserRight.ENVIRONMENT_SAMPLE_EDIT_DISPATCH);
		loginWith(noSensitiveUser);

		EnvironmentSampleDto pseudonymizedSample = getEnvironmentSampleFacade().getByUuid(sample.getUuid());

		assertThat(pseudonymizedSample.isPseudonymized(), is(true));

		pseudonymizedSample.setOtherSampleMaterial("Updated material");
		pseudonymizedSample.getLocation().setCity("Updated city");

		EnvironmentSampleDto updatedSample = getEnvironmentSampleFacade().save(pseudonymizedSample);

		assertThat(updatedSample.isPseudonymized(), is(true));

		loginWith(reportingUser);

		EnvironmentSampleDto sampleForReporter = getEnvironmentSampleFacade().getByUuid(updatedSample.getUuid());
		assertThat(sampleForReporter.isPseudonymized(), is(false));
		assertThat(sampleForReporter.getOtherSampleMaterial(), is("Test material"));
		assertThat(sampleForReporter.getLocation().getCity(), is("Test city"));
	}

	@Test
	public void testUpdateDispatchStatusWithoutRight() {
		EnvironmentSampleDto sample =
			creator.createEnvironmentSample(environment.toReference(), reportingUser.toReference(), lab.toReference(), null);

		UserDto noDispatchUser = creator.createUser(
			rdcf.region.getUuid(),
			rdcf.district.getUuid(),
			null,
			"District",
			"NoSensitive",
			"SurvNoSensitive",
			JurisdictionLevel.DISTRICT,
			UserRight.ENVIRONMENT_SAMPLE_VIEW,
			UserRight.ENVIRONMENT_SAMPLE_EDIT);
		loginWith(noDispatchUser);

		sample.setDispatched(true);
		sample.setDispatchDate(new Date());

		assertThrows(AccessDeniedException.class, () -> getEnvironmentSampleFacade().save(sample));
	}

	@Test
	public void testUpdateReceivalStatusWithoutRight() {
		EnvironmentSampleDto sample =
			creator.createEnvironmentSample(environment.toReference(), reportingUser.toReference(), lab.toReference(), null);

		UserDto noDispatchUser = creator.createUser(
			rdcf.region.getUuid(),
			rdcf.district.getUuid(),
			null,
			"District",
			"NoSensitive",
			"SurvNoSensitive",
			JurisdictionLevel.DISTRICT,
			UserRight.ENVIRONMENT_SAMPLE_VIEW,
			UserRight.ENVIRONMENT_SAMPLE_EDIT);
		loginWith(noDispatchUser);

		sample.setReceived(true);

		assertThrows(AccessDeniedException.class, () -> getEnvironmentSampleFacade().save(sample));
	}

	@Test
	public void testUpdateWithUserOutsideJurisdiction() {
		EnvironmentSampleDto sample =
			creator.createEnvironmentSample(environment.toReference(), reportingUser.toReference(), lab.toReference(), null);

		loginWith(userInDifferentJurisdiction);

		sample.setLabSampleId("Updated lab sample id");

		assertThrows(AccessDeniedException.class, () -> getEnvironmentSampleFacade().save(sample));
	}

	@Test
	public void testCount() {
		creator.createEnvironmentSample(environment.toReference(), reportingUser.toReference(), lab.toReference(), null);
		creator.createEnvironmentSample(environment.toReference(), reportingUser.toReference(), lab.toReference(), null);
		creator.createEnvironmentSample(environment.toReference(), reportingUser.toReference(), lab.toReference(), null);

		assertThat(getEnvironmentSampleFacade().count(null), is(3L));
		assertThat(getEnvironmentSampleFacade().count(new EnvironmentSampleCriteria()), is(3L));
	}

	@Test
	public void testDelete() {
		EnvironmentSampleDto sample =
			creator.createEnvironmentSample(environment.toReference(), reportingUser.toReference(), lab.toReference(), null);

		getEnvironmentSampleFacade().delete(sample.getUuid(), new DeletionDetails(DeletionReason.OTHER_REASON, "Test reason"));

		assertThat(getEnvironmentSampleFacade().count(null), is(0L));
		assertThat(getEnvironmentSampleFacade().isDeleted(sample.getUuid()), is(true));
		assertThat(getEnvironmentSampleFacade().getAllAfter(new Date(0)), hasSize(0));

		// delete multiple samples
		EnvironmentSampleDto sample1 =
			creator.createEnvironmentSample(environment.toReference(), reportingUser.toReference(), lab.toReference(), null);
		EnvironmentSampleDto sample2 =
			creator.createEnvironmentSample(environment.toReference(), reportingUser.toReference(), lab.toReference(), null);

		EnvironmentSampleDto deletedSample =
			creator.createEnvironmentSample(environment.toReference(), reportingUser.toReference(), lab.toReference(), null);
		getEnvironmentSampleFacade().delete(deletedSample.getUuid(), new DeletionDetails(DeletionReason.OTHER_REASON, "Test reason"));

		List<String> deletedUuids = getEnvironmentSampleFacade().delete(
			Arrays.asList(deletedSample.getUuid(), sample1.getUuid(), sample2.getUuid()),
			new DeletionDetails(DeletionReason.OTHER_REASON, "Test reason"));
		assertThat(deletedUuids, not(containsInAnyOrder(deletedSample.getUuid())));
		assertThat(deletedUuids, containsInAnyOrder(sample1.getUuid(), sample2.getUuid()));

		assertThat(getEnvironmentSampleFacade().count(null), is(0L));
	}

	@Test
	public void testRestore() {
		EnvironmentSampleDto sample =
			creator.createEnvironmentSample(environment.toReference(), reportingUser.toReference(), lab.toReference(), null);
		getEnvironmentSampleFacade().delete(sample.getUuid(), new DeletionDetails(DeletionReason.OTHER_REASON, "Test reason"));

		getEnvironmentSampleFacade().restore(sample.getUuid());

		assertThat(getEnvironmentSampleFacade().count(null), is(1L));
		assertThat(getEnvironmentSampleFacade().isDeleted(sample.getUuid()), is(false));
		assertThat(getEnvironmentSampleFacade().getAllAfter(new Date(0)), hasSize(1));

		// restore multiple samples
		EnvironmentSampleDto sample1 =
			creator.createEnvironmentSample(environment.toReference(), reportingUser.toReference(), lab.toReference(), null);
		EnvironmentSampleDto sample2 =
			creator.createEnvironmentSample(environment.toReference(), reportingUser.toReference(), lab.toReference(), null);

		getEnvironmentSampleFacade()
			.delete(Arrays.asList(sample1.getUuid(), sample2.getUuid()), new DeletionDetails(DeletionReason.OTHER_REASON, "Test reason"));

		List<String> restoredUuids = getEnvironmentSampleFacade().restore(Arrays.asList(sample.getUuid(), sample1.getUuid(), sample2.getUuid()));
		assertThat(restoredUuids, not(containsInAnyOrder(sample.getUuid())));
		assertThat(restoredUuids, containsInAnyOrder(sample1.getUuid(), sample2.getUuid()));

		assertThat(getEnvironmentSampleFacade().count(null), is(3L));
	}
}
