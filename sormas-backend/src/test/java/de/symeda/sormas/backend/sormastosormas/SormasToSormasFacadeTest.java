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

import static org.mockito.ArgumentMatchers.eq;

import java.io.File;
import java.security.cert.X509Certificate;
import java.util.Collections;
import java.util.Date;
import java.util.function.Consumer;

import org.mockito.Matchers;
import org.mockito.Mockito;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;

import de.symeda.sormas.api.caze.CaseReferenceDto;
import de.symeda.sormas.api.contact.ContactReferenceDto;
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
import de.symeda.sormas.api.sample.PathogenTestType;
import de.symeda.sormas.api.sample.SampleDto;
import de.symeda.sormas.api.sample.SampleMaterial;
import de.symeda.sormas.api.sample.SamplePurpose;
import de.symeda.sormas.api.sample.SampleSource;
import de.symeda.sormas.api.sormastosormas.SormasServerDescriptor;
import de.symeda.sormas.api.sormastosormas.SormasToSormasEncryptedDataDto;
import de.symeda.sormas.api.sormastosormas.SormasToSormasException;
import de.symeda.sormas.api.sormastosormas.SormasToSormasOriginInfoDto;
import de.symeda.sormas.api.sormastosormas.SormasToSormasSampleDto;
import de.symeda.sormas.api.sormastosormas.shareinfo.SormasToSormasShareInfoDto;
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
import de.symeda.sormas.backend.sormastosormas.share.shareinfo.SormasToSormasShareInfo;
import de.symeda.sormas.backend.user.User;

public abstract class SormasToSormasFacadeTest extends AbstractBeanTest {

	// values are set in server-list.csv located in serveraccessdefault and serveraccesssecond
	public static final String DEFAULT_SERVER_ID = "2.sormas.id.sormas_a";
	public static final SormasServerDescriptor DEFAULT_SERVER = new SormasServerDescriptor("2.sormas.id.sormas_a", "sormas_a", "sormas_a:6048");
	public static final String SECOND_SERVER_ID = "2.sormas.id.sormas_b";
	public static final SormasServerDescriptor SECOND_SERVER = new SormasServerDescriptor("2.sormas.id.sormas_b", "sormas_b", "sormas_b:6048");
	private ObjectMapper objectMapper;

	@Override
	public void init() {
		super.init();

		objectMapper = new ObjectMapper();

		objectMapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.NONE);
		objectMapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);

		mockDefaultServerAccess();
	}

	protected SormasToSormasOriginInfoDto createSormasToSormasOriginInfo(String serverId, boolean ownershipHandedOver) {
		SormasToSormasOriginInfoDto source = new SormasToSormasOriginInfoDto();
		source.setOrganizationId(serverId);
		source.setSenderName("John doe");
		source.setOwnershipHandedOver(ownershipHandedOver);

		return source;
	}

	protected PersonDto createPersonDto(MappableRdcf rdcf) {
		PersonDto person = PersonDto.build();
		person.setFirstName("John");
		person.setLastName("Smith");
		person.setSex(Sex.MALE);

		person.getAddress().setDistrict(rdcf.remoteRdcf.district);
		person.getAddress().setRegion(rdcf.remoteRdcf.region);
		person.getAddress().setCommunity(rdcf.remoteRdcf.community);

		return person;
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

		AdditionalTestDto additionalTest = AdditionalTestDto.build(sample.toReference());
		additionalTest.setTestDateTime(new Date());

		return new SormasToSormasSampleDto(sample, Collections.singletonList(pathogenTest), Collections.singletonList(additionalTest));
	}

	protected SormasToSormasShareInfo createShareInfo(
		User sender,
		String serverId,
		boolean ownershipHandedOver,
		Consumer<SormasToSormasShareInfo> setTarget) {
		SormasToSormasShareInfo shareInfo = new SormasToSormasShareInfo();

		shareInfo.setOwnershipHandedOver(ownershipHandedOver);
		shareInfo.setOrganizationId(serverId);
		shareInfo.setSender(sender);

		setTarget.accept(shareInfo);

		return shareInfo;
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
		Mockito.when(MockProducer.getSormasToSormasClient().get(Matchers.anyString(), eq("/sormasToSormas/cert"), Matchers.any()))
			.thenAnswer(invocation -> {
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

	protected MappableRdcf createRDCF(boolean withExternalId) {

		String regionName = "Region";
		String districtName = "District";
		String communityName = "Community";
		String facilityName = "Facility";
		String pointOfEntryName = "Point of Entry";

		Long regionExternalId = null;
		Long districtExternalId = null;
		Long communityExternalId = null;
		Long facilityExternalId = null;
		Long pointOfEntryExternalId = null;

		if (withExternalId) {
			/*
			regionExternalId = "RegionExtId";
			districtExternalId = "DistrictExtId";
			communityExternalId = "CommunityExtId";
			facilityExternalId = "FacilityExtId";
			pointOfEntryExternalId = "Point of EntryExtId";
			*/
			regionExternalId = Long.parseLong("1");
			districtExternalId = Long.parseLong("1");
			communityExternalId = Long.parseLong("1");
			facilityExternalId = Long.parseLong("1");
			pointOfEntryExternalId = Long.parseLong("1");
		}

		MappableRdcf rdcf = new MappableRdcf();
		rdcf.remoteRdcf = new TestDataCreator.RDCF(
			new RegionReferenceDto(DataHelper.createUuid(), withExternalId ? null : regionName, regionExternalId),
			new DistrictReferenceDto(DataHelper.createUuid(), withExternalId ? null : districtName, districtExternalId),
			new CommunityReferenceDto(DataHelper.createUuid(), withExternalId ? null : communityName, communityExternalId),
			new FacilityReferenceDto(DataHelper.createUuid(), withExternalId ? null : facilityName, facilityExternalId),
			new PointOfEntryReferenceDto(
				DataHelper.createUuid(),
				withExternalId ? null : pointOfEntryName,
				PointOfEntryType.AIRPORT,
				pointOfEntryExternalId));

		Region region = creator.createRegion(regionName, regionExternalId);
		District district = creator.createDistrict(districtName, region, districtExternalId);
		Community community = creator.createCommunity(communityName, district, communityExternalId);
		Facility facility = creator.createFacility(facilityName, FacilityType.HOSPITAL, region, district, community, facilityExternalId);
		PointOfEntry pointOfEntry = creator.createPointOfEntry(pointOfEntryName, region, district, pointOfEntryExternalId);

		rdcf.localRdcf = new TestDataCreator.RDCF(
			new RegionReferenceDto(region.getUuid(), region.getName(), region.getExternalID()),
			new DistrictReferenceDto(district.getUuid(), district.getName(), district.getExternalID()),
			new CommunityReferenceDto(community.getUuid(), community.getName(), community.getExternalID()),
			new FacilityReferenceDto(facility.getUuid(), facility.getName(), facility.getExternalID()),
			new PointOfEntryReferenceDto(pointOfEntry.getUuid(), pointOfEntry.getName(), PointOfEntryType.AIRPORT, pointOfEntry.getExternalID()));

		return rdcf;
	}

	public static class MappableRdcf {

		public TestDataCreator.RDCF remoteRdcf;
		public TestDataCreator.RDCF localRdcf;
	}
}
