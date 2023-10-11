/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2021 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

package de.symeda.sormas.backend.sormastosormas;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;

import java.io.File;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.function.Consumer;

import org.junit.jupiter.api.AfterEach;
import org.mockito.Mockito;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.caze.CaseReferenceDto;
import de.symeda.sormas.api.contact.ContactReferenceDto;
import de.symeda.sormas.api.feature.FeatureConfigurationIndexDto;
import de.symeda.sormas.api.feature.FeatureType;
import de.symeda.sormas.api.infrastructure.community.CommunityReferenceDto;
import de.symeda.sormas.api.infrastructure.district.DistrictReferenceDto;
import de.symeda.sormas.api.infrastructure.facility.FacilityReferenceDto;
import de.symeda.sormas.api.infrastructure.facility.FacilityType;
import de.symeda.sormas.api.infrastructure.pointofentry.PointOfEntryReferenceDto;
import de.symeda.sormas.api.infrastructure.pointofentry.PointOfEntryType;
import de.symeda.sormas.api.infrastructure.region.RegionReferenceDto;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.person.Sex;
import de.symeda.sormas.api.sample.AdditionalTestDto;
import de.symeda.sormas.api.sample.PathogenTestDto;
import de.symeda.sormas.api.sample.PathogenTestResultType;
import de.symeda.sormas.api.sample.PathogenTestType;
import de.symeda.sormas.api.sample.SampleDto;
import de.symeda.sormas.api.sample.SampleMaterial;
import de.symeda.sormas.api.sample.SamplePurpose;
import de.symeda.sormas.api.sample.SampleSource;
import de.symeda.sormas.api.sormastosormas.SormasServerDescriptor;
import de.symeda.sormas.api.sormastosormas.SormasToSormasEncryptedDataDto;
import de.symeda.sormas.api.sormastosormas.SormasToSormasException;
import de.symeda.sormas.api.sormastosormas.SormasToSormasOriginInfoDto;
import de.symeda.sormas.api.sormastosormas.entities.sample.SormasToSormasSampleDto;
import de.symeda.sormas.api.sormastosormas.share.incoming.ShareRequestDataType;
import de.symeda.sormas.api.sormastosormas.share.incoming.ShareRequestStatus;
import de.symeda.sormas.api.sormastosormas.share.outgoing.SormasToSormasShareInfoDto;
import de.symeda.sormas.api.user.DefaultUserRole;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.api.user.UserReferenceDto;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.backend.AbstractBeanTest;
import de.symeda.sormas.backend.MockProducer;
import de.symeda.sormas.backend.TestDataCreator;
import de.symeda.sormas.backend.common.ConfigFacadeEjb;
import de.symeda.sormas.backend.infrastructure.community.Community;
import de.symeda.sormas.backend.infrastructure.district.District;
import de.symeda.sormas.backend.infrastructure.facility.Facility;
import de.symeda.sormas.backend.infrastructure.pointofentry.PointOfEntry;
import de.symeda.sormas.backend.infrastructure.region.Region;
import de.symeda.sormas.backend.sormastosormas.share.outgoing.ShareRequestInfo;
import de.symeda.sormas.backend.sormastosormas.share.outgoing.SormasToSormasShareInfo;
import de.symeda.sormas.backend.user.User;

public abstract class SormasToSormasTest extends AbstractBeanTest {

	// values are set in server-list.csv located in serveraccessdefault and serveraccesssecond
	public static final String DEFAULT_SERVER_ID = "2.sormas.id.sormas_a";
	public static final String SECOND_SERVER_ID = "2.sormas.id.sormas_b";
	private ObjectMapper objectMapper;
	protected TestDataCreator.RDCF rdcf;
	protected UserDto s2sClientUser;

	@Override
	public void init() {
		super.init();

		objectMapper = new ObjectMapper();
		objectMapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.NONE);
		objectMapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);

		mockDefaultServerAccess();

		FeatureConfigurationIndexDto featureConfiguration =
			new FeatureConfigurationIndexDto(DataHelper.createUuid(), null, null, null, null, null, isAcceptRejectFeatureEnabled(), null);
		getFeatureConfigurationFacade().saveFeatureConfiguration(featureConfiguration, FeatureType.SORMAS_TO_SORMAS_ACCEPT_REJECT);
		// in S2S we use external IDs
		rdcf = createRDCF("ExtId").centralRdcf;

		s2sClientUser = creator.createUser(
			rdcf,
			"S2S",
			"Client",
			creator.getUserRoleReference(DefaultUserRole.NATIONAL_USER),
			creator.getUserRoleReference(DefaultUserRole.SORMAS_TO_SORMAS_CLIENT));

		getFacilityService().createConstantFacilities();
		getPointOfEntryService().createConstantPointsOfEntry();

		Mockito.when(MockProducer.getSormasToSormasDiscoveryService().getSormasServerDescriptorById(eq(DEFAULT_SERVER_ID)))
			.thenReturn(new SormasServerDescriptor(DEFAULT_SERVER_ID, "SORMAS A", "https://sormas-a.com"));

		Mockito.when(MockProducer.getSormasToSormasDiscoveryService().getSormasServerDescriptorById(eq(SECOND_SERVER_ID)))
			.thenReturn(new SormasServerDescriptor(SECOND_SERVER_ID, "SORMAS B", "https://sormas-b.com"));
	}

	@AfterEach
	public void teardown() {
		FeatureConfigurationIndexDto featureConfiguration =
			new FeatureConfigurationIndexDto(DataHelper.createUuid(), null, null, null, null, null, false, null);
		getFeatureConfigurationFacade().saveFeatureConfiguration(featureConfiguration, FeatureType.SORMAS_TO_SORMAS_ACCEPT_REJECT);
	}

	protected boolean isAcceptRejectFeatureEnabled() {
		return false;
	}

	protected SormasToSormasOriginInfoDto createSormasToSormasOriginInfoDto(String serverId, boolean ownershipHandedOver) {
		SormasToSormasOriginInfoDto source = new SormasToSormasOriginInfoDto();
		source.setOrganizationId(serverId);
		source.setSenderName("John doe");
		source.setOwnershipHandedOver(ownershipHandedOver);
		source.setComment("Test comment");

		return source;
	}

	protected SormasToSormasOriginInfoDto createAndSaveSormasToSormasOriginInfo(
		String serverId,
		boolean ownershipHandedOver,
		Consumer<SormasToSormasOriginInfoDto> extraConfig) {
		return creator.createSormasToSormasOriginInfo(serverId, ownershipHandedOver, extraConfig);
	}

	protected PersonDto createPersonDto(TestDataCreator.RDCF rdcf) {
		PersonDto person = PersonDto.build();
		person.setFirstName("John");
		person.setLastName("Smith");
		person.setSex(Sex.MALE);

		person.getAddress().setDistrict(rdcf.district);
		person.getAddress().setRegion(rdcf.region);
		person.getAddress().setCommunity(rdcf.community);

		return person;
	}

	protected CaseDataDto createCaseDto(TestDataCreator.RDCF remoteRdcf, PersonDto person) {
		CaseDataDto caze = CaseDataDto.build(person.toReference(), Disease.CORONAVIRUS);
		caze.setResponsibleRegion(remoteRdcf.region);
		caze.setResponsibleDistrict(remoteRdcf.district);
		caze.setResponsibleCommunity(remoteRdcf.community);
		caze.setHealthFacility(remoteRdcf.facility);
		caze.setFacilityType(FacilityType.HOSPITAL);
		return caze;
	}

	protected SormasToSormasSampleDto createRemoteSampleDtoWithTests(
		TestDataCreator.RDCF rdcf,
		CaseReferenceDto caseRef,
		ContactReferenceDto contactRef) {
		UserReferenceDto userRef = UserDto.build().toReference();

		SampleDto sample;
		if (caseRef != null) {
			sample = SampleDto.build(userRef, caseRef);
		} else {
			sample = SampleDto.build(userRef, contactRef);
		}

		sample.setLab(rdcf.facility);
		sample.setSampleDateTime(new Date());
		sample.setSampleMaterial(SampleMaterial.BLOOD);
		sample.setSampleSource(SampleSource.HUMAN);
		sample.setSamplePurpose(SamplePurpose.INTERNAL);

		PathogenTestDto pathogenTest = PathogenTestDto.build(sample.toReference(), userRef);
		pathogenTest.setTestDateTime(new Date());
		pathogenTest.setTestResultVerified(true);
		pathogenTest.setTestType(PathogenTestType.RAPID_TEST);
		pathogenTest.setLab(rdcf.facility);
		pathogenTest.setTestedDisease(Disease.CORONAVIRUS);
		pathogenTest.setTestResult(PathogenTestResultType.PENDING);

		AdditionalTestDto additionalTest = AdditionalTestDto.build(sample.toReference());
		additionalTest.setTestDateTime(new Date());

		return new SormasToSormasSampleDto(
			sample,
			Collections.singletonList(pathogenTest),
			Collections.singletonList(additionalTest),
			Collections.emptyList());
	}

	protected SormasToSormasShareInfo createShareInfo(String serverId, boolean ownershipHandedOver, Consumer<SormasToSormasShareInfo> setTarget) {

		SormasToSormasShareInfo shareInfo = new SormasToSormasShareInfo();

		shareInfo.setOwnershipHandedOver(ownershipHandedOver);
		shareInfo.setOrganizationId(serverId);

		setTarget.accept(shareInfo);

		return shareInfo;
	}

	protected ShareRequestInfo createShareRequestInfo(
		ShareRequestDataType dataType,
		User sender,
		String serverId,
		boolean ownershipHandedOver,
		Consumer<SormasToSormasShareInfo> setTarget) {
		return createShareRequestInfo(dataType, sender, serverId, ownershipHandedOver, ShareRequestStatus.PENDING, setTarget);
	}

	protected ShareRequestInfo createShareRequestInfo(
		ShareRequestDataType dataType,
		User sender,
		String serverId,
		boolean ownershipHandedOver,
		ShareRequestStatus status,
		Consumer<SormasToSormasShareInfo> setTarget) {

		SormasToSormasShareInfo shareInfo = createShareInfo(serverId, ownershipHandedOver, setTarget);

		ShareRequestInfo requestInfo = new ShareRequestInfo();
		requestInfo.setUuid(DataHelper.createUuid());
		requestInfo.setDataType(dataType);
		requestInfo.setSender(sender);
		requestInfo.setRequestStatus(status);
		requestInfo.setShares(new ArrayList<>());
		requestInfo.getShares().add(shareInfo);

		return requestInfo;
	}

	protected SormasToSormasShareInfoDto createShareInfoDto(UserReferenceDto sender, String serverId, boolean ownershipHandedOver) {
		SormasToSormasShareInfoDto shareInfo = new SormasToSormasShareInfoDto();

		shareInfo.setOwnershipHandedOver(ownershipHandedOver);
		shareInfo.setTargetDescriptor(new SormasServerDescriptor(serverId));
		shareInfo.setSender(sender);

		return shareInfo;
	}

	protected SormasToSormasEncryptedDataDto encryptShareDataAsArray(Object shareData) throws SormasToSormasException {
		return encryptShareData(Collections.singletonList(shareData));
	}

	protected SormasToSormasEncryptedDataDto encryptShareData(Object shareData) throws SormasToSormasException {
		mockS2Snetwork();
		mockDefaultServerAccess();

		SormasToSormasEncryptedDataDto encryptedData = getSormasToSormasEncryptionFacade().signAndEncrypt(shareData, SECOND_SERVER_ID);

		mockSecondServerAccess();

		return encryptedData;
	}

	protected <T> T decryptSharesData(byte[] data, Class<T> dataType) throws SormasToSormasException {
		mockSecondServerAccess();

		T decryptData = getSormasToSormasEncryptionFacade().decryptAndVerify(new SormasToSormasEncryptedDataDto(DEFAULT_SERVER_ID, data), dataType);

		mockDefaultServerAccess();

		return decryptData;
	}

	protected void mockS2Snetwork() throws SormasToSormasException {
		Mockito.when(MockProducer.getSormasToSormasClient().get(anyString(), eq("/sormasToSormas/cert"), any())).thenAnswer(invocation -> {
			if (invocation.getArgument(0, String.class).equals(DEFAULT_SERVER_ID)) {
				mockDefaultServerAccess();
			} else {
				mockSecondServerAccess();
			}
			X509Certificate cert = getSormasToSormasEncryptionFacade().loadOwnCertificate();
			if (invocation.getArgument(0, String.class).equals(DEFAULT_SERVER_ID)) {
				mockSecondServerAccess();
			} else {
				mockDefaultServerAccess();
			}
			return cert.getEncoded();
		});
	}

	protected void mockDefaultServerAccess() {
		File file = new File("src/test/java/de/symeda/sormas/backend/sormastosormas/serveraccessdefault");

		MockProducer.getProperties().setProperty(ConfigFacadeEjb.SORMAS2SORMAS_FILES_PATH, file.getAbsolutePath());
		MockProducer.getProperties().setProperty(ConfigFacadeEjb.SORMAS2SORMAS_ID, DEFAULT_SERVER_ID);
		MockProducer.getProperties().setProperty(ConfigFacadeEjb.SORMAS2SORMAS_KEYSTORE_NAME, "sormas_a.sormas2sormas.keystore.p12");
		MockProducer.getProperties().setProperty(ConfigFacadeEjb.SORMAS2SORMAS_KEYSTORE_PASSWORD, "1234");
		MockProducer.getProperties().setProperty(ConfigFacadeEjb.SORMAS2SORMAS_TRUSTSTORE_NAME, "sormas2sormas.truststore.p12");
		MockProducer.getProperties().setProperty(ConfigFacadeEjb.SORMAS2SORMAS_TRUSTSTORE_PASS, "password");
		MockProducer.getProperties().setProperty(ConfigFacadeEjb.SORMAS2SORMAS_ROOT_CA_ALIAS, "S2SCA");
	}

	protected void mockSecondServerAccess() {
		File file = new File("src/test/java/de/symeda/sormas/backend/sormastosormas/serveraccesssecond");

		MockProducer.getProperties().setProperty(ConfigFacadeEjb.SORMAS2SORMAS_FILES_PATH, file.getAbsolutePath());
		MockProducer.getProperties().setProperty(ConfigFacadeEjb.SORMAS2SORMAS_ID, SECOND_SERVER_ID);
		MockProducer.getProperties().setProperty(ConfigFacadeEjb.SORMAS2SORMAS_KEYSTORE_NAME, "sormas_b.sormas2sormas.keystore.p12");
		MockProducer.getProperties().setProperty(ConfigFacadeEjb.SORMAS2SORMAS_KEYSTORE_PASSWORD, "1234");
		MockProducer.getProperties().setProperty(ConfigFacadeEjb.SORMAS2SORMAS_TRUSTSTORE_NAME, "sormas2sormas.truststore.p12");
		MockProducer.getProperties().setProperty(ConfigFacadeEjb.SORMAS2SORMAS_TRUSTSTORE_PASS, "password");
		MockProducer.getProperties().setProperty(ConfigFacadeEjb.SORMAS2SORMAS_ROOT_CA_ALIAS, "S2SCA");

	}

	protected MappableRdcf createRDCF(String externalIdSuffix) {

		String regionName = "Region";
		String districtName = "District";
		String communityName = "Community";
		String facilityName = "Facility";
		String pointOfEntryName = "Point of Entry";

		String regionExternalId = null;
		String districtExternalId = null;
		String communityExternalId = null;
		String facilityExternalId = null;
		String pointOfEntryExternalId = null;

		boolean withExternalId = externalIdSuffix != null;
		if (withExternalId) {
			regionExternalId = "Region" + externalIdSuffix;
			districtExternalId = "District" + externalIdSuffix;
			communityExternalId = "Community" + externalIdSuffix;
			facilityExternalId = "Facility" + externalIdSuffix;
			pointOfEntryExternalId = "Point of Entry" + externalIdSuffix;
		}

		MappableRdcf rdcf = new MappableRdcf();
		rdcf.invalidLocalRdcf = new TestDataCreator.RDCF(
			new RegionReferenceDto(DataHelper.createUuid(), withExternalId ? null : regionName, regionExternalId),
			new DistrictReferenceDto(DataHelper.createUuid(), withExternalId ? null : districtName, districtExternalId),
			new CommunityReferenceDto(DataHelper.createUuid(), withExternalId ? null : communityName, communityExternalId),
			new FacilityReferenceDto(DataHelper.createUuid(), withExternalId ? null : facilityName, facilityExternalId),
			new PointOfEntryReferenceDto(
				DataHelper.createUuid(),
				withExternalId ? null : pointOfEntryName,
				PointOfEntryType.AIRPORT,
				pointOfEntryExternalId));

		Region region = creator.createRegionCentrally(regionName + "Central", regionExternalId);
		District district = creator.createDistrictCentrally(districtName + "Central", region, districtExternalId);
		Community community = creator.createCommunityCentrally(communityName + "Central", district, communityExternalId);
		Facility facility = creator.createFacility(facilityName + "Central", FacilityType.HOSPITAL, region, district, community, facilityExternalId);
		PointOfEntry pointOfEntry = creator.createPointOfEntry(pointOfEntryName + "Central", region, district, pointOfEntryExternalId);

		rdcf.centralRdcf = new TestDataCreator.RDCF(
			new RegionReferenceDto(region.getUuid(), region.getName(), region.getExternalID()),
			new DistrictReferenceDto(district.getUuid(), district.getName(), district.getExternalID()),
			new CommunityReferenceDto(community.getUuid(), community.getName(), community.getExternalID()),
			new FacilityReferenceDto(facility.getUuid(), facility.getName(), facility.getExternalID()),
			new PointOfEntryReferenceDto(pointOfEntry.getUuid(), pointOfEntry.getName(), PointOfEntryType.AIRPORT, pointOfEntry.getExternalID()));

		return rdcf;
	}

	public static class MappableRdcf {

		// IMPORTANT: This is used to simulate a setup where central infra sync is enabled and all instances are
		// guaranteed to have the same infra with the same global uuids.
		public TestDataCreator.RDCF centralRdcf;

		// This will only be used in the future to simulate and test for sync errors and recovery.
		public TestDataCreator.RDCF invalidLocalRdcf;
	}
}
