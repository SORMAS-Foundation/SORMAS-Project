package de.symeda.sormas.api.sample;

import java.io.Serializable;
import java.util.Date;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.DiseaseHelper;
import de.symeda.sormas.api.caze.CaseClassification;
import de.symeda.sormas.api.caze.CaseOutcome;
import de.symeda.sormas.api.facility.FacilityHelper;
import de.symeda.sormas.api.location.LocationDto;
import de.symeda.sormas.api.person.ApproximateAgeType;
import de.symeda.sormas.api.person.ApproximateAgeType.ApproximateAgeHelper;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.person.Sex;

public class SampleExportDto implements Serializable {

	private static final long serialVersionUID = -3027326087594387560L;

	public static final String I18N_PREFIX = "SampleExport";

	private long id;
	private String uuid;
	private String labSampleID;
	private String epidNumber;
	private String caseName;
	private String disease;
	private Date sampleDateTime;
	private String sampleMaterial;
	private SampleSource sampleSource;
	private String laboratory;
	private PathogenTestResultType pathogenTestResult;
	private Boolean pathogenTestingRequested;
	private String requestedPathogenTests;
	private Boolean additionalTestingRequested;
	private String requestedAdditionalTests;
	private boolean shipped;
	private Date shipmentDate;
	private String shipmentDetails;
	private boolean received;
	private Date receivedDate;
	private SpecimenCondition specimenCondition;
	private String noTestPossibleReason;
	private String comment;
	private String referredToUuid;
	private String caseUuid;
	private String caseAge;
	private Sex caseSex;
	private String caseAddress;
	private Date caseReportDate;
	private CaseClassification caseClassification;
	private CaseOutcome caseOutcome;
	private String caseRegion;
	private String caseDistrict;
	private String caseCommunity;
	private String caseFacility;
	private String pathogenTestType1;
	private String pathogenTestDisease1;
	private Date pathogenTestDateTime1;
	private String pathogenTestLab1;
	private PathogenTestResultType pathogenTestResult1;
	private boolean pathogenTestVerified1;
	private String pathogenTestType2;
	private String pathogenTestDisease2;
	private Date pathogenTestDateTime2;
	private String pathogenTestLab2;
	private PathogenTestResultType pathogenTestResult2;
	private boolean pathogenTestVerified2;
	private String pathogenTestType3;
	private String pathogenTestDisease3;
	private Date pathogenTestDateTime3;
	private String pathogenTestLab3;
	private PathogenTestResultType pathogenTestResult3;
	private boolean pathogenTestVerified3;
	private String otherPathogenTestsDetails;
	private AdditionalTestDto additionalTest;
	private String otherAdditionalTestsDetails;

	public SampleExportDto(long id, String uuid, String labSampleId, String epidNumber, String firstName, String lastName, 
			Disease disease, String diseaseDetails, Date sampleDateTime, SampleMaterial sampleMaterial, String sampleMaterialDetails,
			SampleSource sampleSource, String laboratoryUuid, String laboratory, String laboratoryDetails,
			PathogenTestResultType pathogenTestResult, Boolean pathogenTestingRequested, String requestedPathogenTests,
			Boolean additionalTestingRequested, String requestedAdditionalTests, boolean shipped, Date shipmentDate,
			String shipmentDetails, boolean received, Date receivedDate, SpecimenCondition specimenCondition,
			String noTestPossibleReason, String comment, String referredToUuid, String caseUuid, Integer approximateAge,
			ApproximateAgeType approximateAgeType, Sex caseSex, LocationDto caseAddress, Date caseReportDate,
			CaseClassification caseClassification, CaseOutcome caseOutcome, String caseRegion, String caseDistrict,
			String caseCommunity, String caseFacility) {
		this.id = id;
		this.uuid = uuid;
		this.labSampleID = labSampleId;
		this.epidNumber = epidNumber;
		this.caseName = PersonDto.buildCaption(firstName, lastName);
		this.disease = DiseaseHelper.toString(disease, diseaseDetails);
		this.sampleDateTime = sampleDateTime;
		this.sampleMaterial = SampleMaterial.toString(sampleMaterial, sampleMaterialDetails);
		this.sampleSource = sampleSource;
		this.laboratory = FacilityHelper.buildFacilityString(laboratoryUuid, laboratory, laboratoryDetails);
		this.pathogenTestResult = pathogenTestResult;
		this.pathogenTestingRequested = pathogenTestingRequested;
		this.requestedPathogenTests = requestedPathogenTests;
		this.additionalTestingRequested = additionalTestingRequested;
		this.requestedAdditionalTests = requestedAdditionalTests;
		this.shipped = shipped;
		this.shipmentDate = shipmentDate;
		this.shipmentDetails = shipmentDetails;
		this.received = received;
		this.receivedDate = receivedDate;
		this.specimenCondition = specimenCondition;
		this.noTestPossibleReason = noTestPossibleReason;
		this.comment = comment;
		this.referredToUuid = referredToUuid;
		this.caseUuid = caseUuid;
		this.caseAge = ApproximateAgeHelper.formatApproximateAge(approximateAge, approximateAgeType);
		this.caseSex = caseSex;
		this.caseAddress = caseAddress.toString();
		this.caseReportDate = caseReportDate;
		this.caseClassification = caseClassification;
		this.caseOutcome = caseOutcome;
		this.caseRegion = caseRegion;
		this.caseDistrict = caseDistrict;
		this.caseCommunity = caseCommunity;
		this.caseFacility = caseFacility;
	}
}
