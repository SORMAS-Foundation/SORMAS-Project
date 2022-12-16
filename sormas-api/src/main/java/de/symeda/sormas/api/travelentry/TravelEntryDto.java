package de.symeda.sormas.api.travelentry;

import de.symeda.sormas.api.common.DeletionReason;
import de.symeda.sormas.api.feature.FeatureType;
import de.symeda.sormas.api.utils.DependingOnFeatureType;
import java.util.Date;
import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.Size;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.caze.CaseReferenceDto;
import de.symeda.sormas.api.contact.QuarantineType;
import de.symeda.sormas.api.disease.DiseaseVariant;
import de.symeda.sormas.api.i18n.Validations;
import de.symeda.sormas.api.infrastructure.community.CommunityReferenceDto;
import de.symeda.sormas.api.infrastructure.district.DistrictReferenceDto;
import de.symeda.sormas.api.infrastructure.pointofentry.PointOfEntryReferenceDto;
import de.symeda.sormas.api.infrastructure.region.RegionReferenceDto;
import de.symeda.sormas.api.person.PersonReferenceDto;
import de.symeda.sormas.api.user.UserReferenceDto;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.api.utils.EmbeddedPersonalData;
import de.symeda.sormas.api.utils.FieldConstraints;
import de.symeda.sormas.api.utils.Required;
import de.symeda.sormas.api.utils.SensitiveData;
import de.symeda.sormas.api.utils.YesNoUnknown;
import de.symeda.sormas.api.utils.pseudonymization.PseudonymizableDto;
import io.swagger.v3.oas.annotations.media.Schema;

@DependingOnFeatureType(featureType = FeatureType.TRAVEL_ENTRIES)
@Schema(
	description = "Data transfer object for travel entries used for tracking travellers into a country, depending on said countries pandemic regulations.")
public class TravelEntryDto extends PseudonymizableDto {

	private static final long serialVersionUID = 4503438472222204446L;

	public static final String I18N_PREFIX = "TravelEntry";

	public static final String PERSON = "person";
	public static final String RESPONSIBLE_REGION = "responsibleRegion";
	public static final String RESPONSIBLE_DISTRICT = "responsibleDistrict";
	public static final String RESPONSIBLE_COMMUNITY = "responsibleCommunity";
	public static final String POINT_OF_ENTRY_REGION = "pointOfEntryRegion";
	public static final String POINT_OF_ENTRY_DISTRICT = "pointOfEntryDistrict";

	public static final String REPORT_DATE = "reportDate";
	public static final String REPORTING_USER = "reportingUser";
	public static final String EXTERNAL_ID = "externalId";
	public static final String DISEASE = "disease";
	public static final String DISEASE_VARIANT = "diseaseVariant";
	public static final String DISEASE_DETAILS = "diseaseDetails";
	public static final String DISEASE_VARIANT_DETAILS = "diseaseVariantDetails";

	public static final String RECOVERED = "recovered";
	public static final String VACCINATED = "vaccinated";
	public static final String TESTED_NEGATIVE = "testedNegative";

	public static final String REGION = "pointOfEntryRegion";
	public static final String DISTRICT = "pointOfEntryDistrict";

	public static final String POINT_OF_ENTRY = "pointOfEntry";
	public static final String POINT_OF_ENTRY_DETAILS = "pointOfEntryDetails";

	public static final String QUARANTINE_HOME_POSSIBLE = "quarantineHomePossible";
	public static final String QUARANTINE_HOME_POSSIBLE_COMMENT = "quarantineHomePossibleComment";
	public static final String QUARANTINE_HOME_SUPPLY_ENSURED = "quarantineHomeSupplyEnsured";
	public static final String QUARANTINE_HOME_SUPPLY_ENSURED_COMMENT = "quarantineHomeSupplyEnsuredComment";
	public static final String QUARANTINE = "quarantine";
	public static final String QUARANTINE_FROM = "quarantineFrom";
	public static final String QUARANTINE_TO = "quarantineTo";
	public static final String QUARANTINE_EXTENDED = "quarantineExtended";
	public static final String QUARANTINE_REDUCED = "quarantineReduced";
	public static final String QUARANTINE_TYPE_DETAILS = "quarantineTypeDetails";
	public static final String QUARANTINE_ORDERED_VERBALLY = "quarantineOrderedVerbally";
	public static final String QUARANTINE_ORDERED_VERBALLY_DATE = "quarantineOrderedVerballyDate";
	public static final String QUARANTINE_ORDERED_OFFICIAL_DOCUMENT = "quarantineOrderedOfficialDocument";
	public static final String QUARANTINE_ORDERED_OFFICIAL_DOCUMENT_DATE = "quarantineOrderedOfficialDocumentDate";
	public static final String QUARANTINE_OFFICIAL_ORDER_SENT = "quarantineOfficialOrderSent";
	public static final String QUARANTINE_OFFICIAL_ORDER_SENT_DATE = "quarantineOfficialOrderSentDate";
	public static final String QUARANTINE_HELP_NEEDED = "quarantineHelpNeeded";
	public static final String DATE_OF_ARRIVAL = "dateOfArrival";
	public static final String DELETION_REASON = "deletionReason";
	public static final String OTHER_DELETION_REASON = "otherDeletionReason";

	@Required
	@EmbeddedPersonalData
	private PersonReferenceDto person;
	private Date reportDate;
	private UserReferenceDto reportingUser;
	@Schema(description = "Whether this travel entry has been archived")
	private boolean archived;
	@Schema(description = "Whether this travel entry has been deleted")
	private boolean deleted;
	private Disease disease;
	@SensitiveData
	@Size(max = FieldConstraints.CHARACTER_LIMIT_DEFAULT, message = Validations.textTooLong)
	private String diseaseDetails;
	private DiseaseVariant diseaseVariant;
	@SensitiveData
	@Size(max = FieldConstraints.CHARACTER_LIMIT_DEFAULT, message = Validations.textTooLong)
	private String diseaseVariantDetails;
	private RegionReferenceDto responsibleRegion;
	private DistrictReferenceDto responsibleDistrict;
	private CommunityReferenceDto responsibleCommunity;
	private RegionReferenceDto pointOfEntryRegion;
	private DistrictReferenceDto pointOfEntryDistrict;
	private PointOfEntryReferenceDto pointOfEntry;
	@SensitiveData
	@Size(max = FieldConstraints.CHARACTER_LIMIT_TEXT, message = Validations.textTooLong)
	@Schema(description = "Free text description with details on the point-of-entry into the country")
	private String pointOfEntryDetails;
	@EmbeddedPersonalData
	private CaseReferenceDto resultingCase;
	@Size(max = FieldConstraints.CHARACTER_LIMIT_SMALL, message = Validations.textTooLong)
	@Schema(description = "TBD_RESTAPI_SWAGGER_DOC")
	private String externalId;
	@Schema(description = "Whether the subject has recovered from the disease")
	private boolean recovered;
	@Schema(description = "Whether the subject is vaccinated against the disease")
	private boolean vaccinated;
	@Schema(description = "Whether the subject tested negative for the disease")
	private boolean testedNegative;
	@Valid
	@Schema(description = "List of special remarks regarding the Drug Enforcement Administration (DEA)")
	private List<DeaContentEntry> deaContent;

	private QuarantineType quarantine;
	@SensitiveData
	@Size(max = FieldConstraints.CHARACTER_LIMIT_TEXT, message = Validations.textTooLong)
	@Schema(description = "Free text description detailing the quarantine situation")
	private String quarantineTypeDetails;
	@Schema(description = "Date when the quarantine began")
	private Date quarantineFrom;
	@Schema(description = "Date when the quarantine ends/ended")
	private Date quarantineTo;
	@SensitiveData
	@Size(max = FieldConstraints.CHARACTER_LIMIT_TEXT, message = Validations.textTooLong)
	@Schema(description = "Description of measures taken to ensure the basic care and supply of a quarantined person")
	private String quarantineHelpNeeded;
	@Schema(description = "Indicates whether the quarantine is ordered verbally")
	private boolean quarantineOrderedVerbally;
	@Schema(description = "Indicates whether the quarantine is ordered via a written document")
	private boolean quarantineOrderedOfficialDocument;
	@Schema(description = "Date when the quarantine is ordered verbally")
	private Date quarantineOrderedVerballyDate;
	@Schema(description = "Date when the quarantine is ordered via written document")
	private Date quarantineOrderedOfficialDocumentDate;
	@Schema(description = "Whether quarantining at home of the person is possible")
	private YesNoUnknown quarantineHomePossible;
	@SensitiveData
	@Size(max = FieldConstraints.CHARACTER_LIMIT_TEXT, message = Validations.textTooLong)
	@Schema(description = "Comment about quarantining at home")
	private String quarantineHomePossibleComment;
	@Schema(description = "Whether the supply of a person quarantined at home is taken care of")
	private YesNoUnknown quarantineHomeSupplyEnsured;
	@SensitiveData
	@Size(max = FieldConstraints.CHARACTER_LIMIT_TEXT, message = Validations.textTooLong)
	@Schema(description = "Free text description detailing the supply situation for quarantine at home")
	private String quarantineHomeSupplyEnsuredComment;
	@Schema(description = "Indicates wether the quarantine duration has been extended")
	private boolean quarantineExtended;
	@Schema(description = "Indicates wether the quarantine duration has been reduced")
	private boolean quarantineReduced;
	@Schema(description = "Indicates wether a official quarantine order has been issued")
	private boolean quarantineOfficialOrderSent;
	@Schema(description = "Date when the official quarantine order is issued")
	private Date quarantineOfficialOrderSentDate;
	@Schema(description = "Date when the official quarantine order was received")
	private Date dateOfArrival;

	private DeletionReason deletionReason;
	@Size(max = FieldConstraints.CHARACTER_LIMIT_TEXT, message = Validations.textTooLong)
	@Schema(description = "Detailed deletion reason other than proposed reasons")
	private String otherDeletionReason;

	public static TravelEntryDto build(PersonReferenceDto person) {

		final TravelEntryDto travelEntry = new TravelEntryDto();
		travelEntry.setUuid(DataHelper.createUuid());
		travelEntry.setPerson(person);
		travelEntry.setReportDate(new Date());
		return travelEntry;
	}

	public TravelEntryReferenceDto toReference() {
		return new TravelEntryReferenceDto(getUuid(), getExternalId(), getPerson().getFirstName(), getPerson().getLastName());
	}

	public PersonReferenceDto getPerson() {
		return person;
	}

	public void setPerson(PersonReferenceDto person) {
		this.person = person;
	}

	public Date getReportDate() {
		return reportDate;
	}

	public void setReportDate(Date reportDate) {
		this.reportDate = reportDate;
	}

	public UserReferenceDto getReportingUser() {
		return reportingUser;
	}

	public void setReportingUser(UserReferenceDto reportingUser) {
		this.reportingUser = reportingUser;
	}

	public boolean isArchived() {
		return archived;
	}

	public void setArchived(boolean archived) {
		this.archived = archived;
	}

	public boolean isDeleted() {
		return deleted;
	}

	public void setDeleted(boolean deleted) {
		this.deleted = deleted;
	}

	public Disease getDisease() {
		return disease;
	}

	public void setDisease(Disease disease) {
		this.disease = disease;
	}

	public String getDiseaseDetails() {
		return diseaseDetails;
	}

	public void setDiseaseDetails(String diseaseDetails) {
		this.diseaseDetails = diseaseDetails;
	}

	public DiseaseVariant getDiseaseVariant() {
		return diseaseVariant;
	}

	public void setDiseaseVariant(DiseaseVariant diseaseVariant) {
		this.diseaseVariant = diseaseVariant;
	}

	public String getDiseaseVariantDetails() {
		return diseaseVariantDetails;
	}

	public void setDiseaseVariantDetails(String diseaseVariantDetails) {
		this.diseaseVariantDetails = diseaseVariantDetails;
	}

	public RegionReferenceDto getResponsibleRegion() {
		return responsibleRegion;
	}

	public void setResponsibleRegion(RegionReferenceDto responsibleRegion) {
		this.responsibleRegion = responsibleRegion;
	}

	public DistrictReferenceDto getResponsibleDistrict() {
		return responsibleDistrict;
	}

	public void setResponsibleDistrict(DistrictReferenceDto responsibleDistrict) {
		this.responsibleDistrict = responsibleDistrict;
	}

	public CommunityReferenceDto getResponsibleCommunity() {
		return responsibleCommunity;
	}

	public void setResponsibleCommunity(CommunityReferenceDto responsibleCommunity) {
		this.responsibleCommunity = responsibleCommunity;
	}

	public RegionReferenceDto getPointOfEntryRegion() {
		return pointOfEntryRegion;
	}

	public void setPointOfEntryRegion(RegionReferenceDto pointOfEntryRegion) {
		this.pointOfEntryRegion = pointOfEntryRegion;
	}

	public DistrictReferenceDto getPointOfEntryDistrict() {
		return pointOfEntryDistrict;
	}

	public void setPointOfEntryDistrict(DistrictReferenceDto pointOfEntryDistrict) {
		this.pointOfEntryDistrict = pointOfEntryDistrict;
	}

	public PointOfEntryReferenceDto getPointOfEntry() {
		return pointOfEntry;
	}

	public void setPointOfEntry(PointOfEntryReferenceDto pointOfEntry) {
		this.pointOfEntry = pointOfEntry;
	}

	public String getPointOfEntryDetails() {
		return pointOfEntryDetails;
	}

	public void setPointOfEntryDetails(String pointOfEntryDetails) {
		this.pointOfEntryDetails = pointOfEntryDetails;
	}

	public CaseReferenceDto getResultingCase() {
		return resultingCase;
	}

	public void setResultingCase(CaseReferenceDto resultingCase) {
		this.resultingCase = resultingCase;
	}

	public String getExternalId() {
		return externalId;
	}

	public void setExternalId(String externalId) {
		this.externalId = externalId;
	}

	public boolean isRecovered() {
		return recovered;
	}

	public void setRecovered(boolean recovered) {
		this.recovered = recovered;
	}

	public boolean isVaccinated() {
		return vaccinated;
	}

	public void setVaccinated(boolean vaccinated) {
		this.vaccinated = vaccinated;
	}

	public boolean isTestedNegative() {
		return testedNegative;
	}

	public void setTestedNegative(boolean testedNegative) {
		this.testedNegative = testedNegative;
	}

	public List<DeaContentEntry> getDeaContent() {
		return deaContent;
	}

	public void setDeaContent(List<DeaContentEntry> deaContent) {
		this.deaContent = deaContent;
	}

	public QuarantineType getQuarantine() {
		return quarantine;
	}

	public void setQuarantine(QuarantineType quarantine) {
		this.quarantine = quarantine;
	}

	public String getQuarantineTypeDetails() {
		return quarantineTypeDetails;
	}

	public void setQuarantineTypeDetails(String quarantineTypeDetails) {
		this.quarantineTypeDetails = quarantineTypeDetails;
	}

	public Date getQuarantineFrom() {
		return quarantineFrom;
	}

	public void setQuarantineFrom(Date quarantineFrom) {
		this.quarantineFrom = quarantineFrom;
	}

	public Date getQuarantineTo() {
		return quarantineTo;
	}

	public void setQuarantineTo(Date quarantineTo) {
		this.quarantineTo = quarantineTo;
	}

	public String getQuarantineHelpNeeded() {
		return quarantineHelpNeeded;
	}

	public void setQuarantineHelpNeeded(String quarantineHelpNeeded) {
		this.quarantineHelpNeeded = quarantineHelpNeeded;
	}

	public boolean isQuarantineOrderedVerbally() {
		return quarantineOrderedVerbally;
	}

	public void setQuarantineOrderedVerbally(boolean quarantineOrderedVerbally) {
		this.quarantineOrderedVerbally = quarantineOrderedVerbally;
	}

	public boolean isQuarantineOrderedOfficialDocument() {
		return quarantineOrderedOfficialDocument;
	}

	public void setQuarantineOrderedOfficialDocument(boolean quarantineOrderedOfficialDocument) {
		this.quarantineOrderedOfficialDocument = quarantineOrderedOfficialDocument;
	}

	public Date getQuarantineOrderedVerballyDate() {
		return quarantineOrderedVerballyDate;
	}

	public void setQuarantineOrderedVerballyDate(Date quarantineOrderedVerballyDate) {
		this.quarantineOrderedVerballyDate = quarantineOrderedVerballyDate;
	}

	public Date getQuarantineOrderedOfficialDocumentDate() {
		return quarantineOrderedOfficialDocumentDate;
	}

	public void setQuarantineOrderedOfficialDocumentDate(Date quarantineOrderedOfficialDocumentDate) {
		this.quarantineOrderedOfficialDocumentDate = quarantineOrderedOfficialDocumentDate;
	}

	public YesNoUnknown getQuarantineHomePossible() {
		return quarantineHomePossible;
	}

	public void setQuarantineHomePossible(YesNoUnknown quarantineHomePossible) {
		this.quarantineHomePossible = quarantineHomePossible;
	}

	public String getQuarantineHomePossibleComment() {
		return quarantineHomePossibleComment;
	}

	public void setQuarantineHomePossibleComment(String quarantineHomePossibleComment) {
		this.quarantineHomePossibleComment = quarantineHomePossibleComment;
	}

	public YesNoUnknown getQuarantineHomeSupplyEnsured() {
		return quarantineHomeSupplyEnsured;
	}

	public void setQuarantineHomeSupplyEnsured(YesNoUnknown quarantineHomeSupplyEnsured) {
		this.quarantineHomeSupplyEnsured = quarantineHomeSupplyEnsured;
	}

	public String getQuarantineHomeSupplyEnsuredComment() {
		return quarantineHomeSupplyEnsuredComment;
	}

	public void setQuarantineHomeSupplyEnsuredComment(String quarantineHomeSupplyEnsuredComment) {
		this.quarantineHomeSupplyEnsuredComment = quarantineHomeSupplyEnsuredComment;
	}

	public boolean isQuarantineExtended() {
		return quarantineExtended;
	}

	public void setQuarantineExtended(boolean quarantineExtended) {
		this.quarantineExtended = quarantineExtended;
	}

	public boolean isQuarantineReduced() {
		return quarantineReduced;
	}

	public void setQuarantineReduced(boolean quarantineReduced) {
		this.quarantineReduced = quarantineReduced;
	}

	public boolean isQuarantineOfficialOrderSent() {
		return quarantineOfficialOrderSent;
	}

	public void setQuarantineOfficialOrderSent(boolean quarantineOfficialOrderSent) {
		this.quarantineOfficialOrderSent = quarantineOfficialOrderSent;
	}

	public Date getQuarantineOfficialOrderSentDate() {
		return quarantineOfficialOrderSentDate;
	}

	public void setQuarantineOfficialOrderSentDate(Date quarantineOfficialOrderSentDate) {
		this.quarantineOfficialOrderSentDate = quarantineOfficialOrderSentDate;
	}

	public Date getDateOfArrival() {
		return dateOfArrival;
	}

	public void setDateOfArrival(Date dateOfArrival) {
		this.dateOfArrival = dateOfArrival;
	}

	public DeletionReason getDeletionReason() {
		return deletionReason;
	}

	public void setDeletionReason(DeletionReason deletionReason) {
		this.deletionReason = deletionReason;
	}

	public String getOtherDeletionReason() {
		return otherDeletionReason;
	}

	public void setOtherDeletionReason(String otherDeletionReason) {
		this.otherDeletionReason = otherDeletionReason;
	}
}
