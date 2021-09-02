package de.symeda.sormas.backend.travelentry;

import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.Type;

import de.symeda.auditlog.api.Audited;
import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.contact.QuarantineType;
import de.symeda.sormas.api.disease.DiseaseVariant;
import de.symeda.sormas.api.travelentry.DeaContentEntry;
import de.symeda.sormas.api.utils.YesNoUnknown;
import de.symeda.sormas.backend.caze.Case;
import de.symeda.sormas.backend.common.CoreAdo;
import de.symeda.sormas.backend.disease.DiseaseVariantConverter;
import de.symeda.sormas.backend.infrastructure.pointofentry.PointOfEntry;
import de.symeda.sormas.backend.person.Person;
import de.symeda.sormas.backend.infrastructure.community.Community;
import de.symeda.sormas.backend.infrastructure.district.District;
import de.symeda.sormas.backend.infrastructure.region.Region;
import de.symeda.sormas.backend.user.User;
import de.symeda.sormas.backend.util.ModelConstants;

@Entity(name = "travelentry")
@Audited
public class TravelEntry extends CoreAdo {

	private static final long serialVersionUID = 8415313365918535184L;

	public static final String TABLE_NAME = "travelentry";

	public static final String PERSON = "person";
	public static final String REPORT_DATE = "reportDate";
	public static final String REPORTING_USER = "reportingUser";
	public static final String ARCHIVED = "archived";
	public static final String DELETED = "deleted";
	public static final String DISEASE = "disease";
	public static final String DISEASE_VARIANT = "diseaseVariant";
	public static final String RESPONSIBLE_REGION = "responsibleRegion";
	public static final String RESPONSIBLE_DISTRICT = "responsibleDistrict";
	public static final String RESPONSIBLE_COMMUNITY = "responsibleCommunity";
	public static final String POINT_OF_ENTRY_REGION = "pointOfEntryRegion";
	public static final String POINT_OF_ENTRY_DISTRICT = "pointOfEntryDistrict";
	public static final String POINT_OF_ENTRY = "pointOfEntry";
	public static final String POINT_OF_ENTRY_DETAILS = "pointOfEntryDetails";
	public static final String RESULTING_CASE = "resultingCase";
	public static final String EXTERNAL_ID = "externalId";
	public static final String RECOVERED = "recovered";
	public static final String VACCINATED = "vaccinated";
	public static final String TESTED_NEGATIVE = "testedNegative";
	public static final String QUARANTINE_TO = "quarantineTo";

	private Person person;
	private Date reportDate;
	private User reportingUser;
	private boolean archived;
	private boolean deleted;
	private Disease disease;
	private String diseaseDetails;
	private DiseaseVariant diseaseVariant;
	private Region responsibleRegion;
	private District responsibleDistrict;
	private Community responsibleCommunity;
	private Region pointOfEntryRegion;
	private District pointOfEntryDistrict;
	private PointOfEntry pointOfEntry;
	private String pointOfEntryDetails;
	private Case resultingCase;
	private String externalId;
	private boolean recovered;
	private boolean vaccinated;
	private boolean testedNegative;
	private List<DeaContentEntry> deaContent;

	private QuarantineType quarantine;
	private String quarantineTypeDetails;
	private Date quarantineFrom;
	private Date quarantineTo;
	private String quarantineHelpNeeded;
	private boolean quarantineOrderedVerbally;
	private boolean quarantineOrderedOfficialDocument;
	private Date quarantineOrderedVerballyDate;
	private Date quarantineOrderedOfficialDocumentDate;
	private YesNoUnknown quarantineHomePossible;
	private String quarantineHomePossibleComment;
	private YesNoUnknown quarantineHomeSupplyEnsured;
	private String quarantineHomeSupplyEnsuredComment;
	private boolean quarantineExtended;
	private boolean quarantineReduced;
	private boolean quarantineOfficialOrderSent;
	private Date quarantineOfficialOrderSentDate;

	@ManyToOne()
	@JoinColumn(nullable = false)
	public Person getPerson() {
		return person;
	}

	public void setPerson(Person person) {
		this.person = person;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(nullable = false)
	public Date getReportDate() {
		return reportDate;
	}

	public void setReportDate(Date reportDate) {
		this.reportDate = reportDate;
	}

	@ManyToOne()
	@JoinColumn(nullable = false)
	public User getReportingUser() {
		return reportingUser;
	}

	public void setReportingUser(User reportingUser) {
		this.reportingUser = reportingUser;
	}

	@Column
	public boolean isArchived() {
		return archived;
	}

	public void setArchived(boolean archived) {
		this.archived = archived;
	}

	@Override
	public boolean isDeleted() {
		return deleted;
	}

	@Override
	public void setDeleted(boolean deleted) {
		this.deleted = deleted;
	}

	@Enumerated(EnumType.STRING)
	public Disease getDisease() {
		return disease;
	}

	public void setDisease(Disease disease) {
		this.disease = disease;
	}

	@Column(columnDefinition = "text")
	public String getDiseaseDetails() {
		return diseaseDetails;
	}

	public void setDiseaseDetails(String diseaseDetails) {
		this.diseaseDetails = diseaseDetails;
	}

	@Column
	@Convert(converter = DiseaseVariantConverter.class)
	public DiseaseVariant getDiseaseVariant() {
		return diseaseVariant;
	}

	public void setDiseaseVariant(DiseaseVariant diseaseVariant) {
		this.diseaseVariant = diseaseVariant;
	}

	@ManyToOne()
	public Region getResponsibleRegion() {
		return responsibleRegion;
	}

	public void setResponsibleRegion(Region responsibleRegion) {
		this.responsibleRegion = responsibleRegion;
	}

	@ManyToOne()
	public District getResponsibleDistrict() {
		return responsibleDistrict;
	}

	public void setResponsibleDistrict(District responsibleDistrict) {
		this.responsibleDistrict = responsibleDistrict;
	}

	@ManyToOne()
	public Community getResponsibleCommunity() {
		return responsibleCommunity;
	}

	public void setResponsibleCommunity(Community responsibleCommunity) {
		this.responsibleCommunity = responsibleCommunity;
	}

	@ManyToOne()
	public Region getPointOfEntryRegion() {
		return pointOfEntryRegion;
	}

	public void setPointOfEntryRegion(Region pointOfEntryRegion) {
		this.pointOfEntryRegion = pointOfEntryRegion;
	}

	@ManyToOne()
	public District getPointOfEntryDistrict() {
		return pointOfEntryDistrict;
	}

	public void setPointOfEntryDistrict(District pointOfEntryDistrict) {
		this.pointOfEntryDistrict = pointOfEntryDistrict;
	}

	@ManyToOne()
	public PointOfEntry getPointOfEntry() {
		return pointOfEntry;
	}

	public void setPointOfEntry(PointOfEntry pointOfEntry) {
		this.pointOfEntry = pointOfEntry;
	}

	@Column(columnDefinition = "text")
	public String getPointOfEntryDetails() {
		return pointOfEntryDetails;
	}

	public void setPointOfEntryDetails(String pointOfEntryDetails) {
		this.pointOfEntryDetails = pointOfEntryDetails;
	}

	@ManyToOne()
	@JoinColumn
	public Case getResultingCase() {
		return resultingCase;
	}

	public void setResultingCase(Case resultingCase) {
		this.resultingCase = resultingCase;
	}

	@Column(columnDefinition = "text")
	public String getExternalId() {
		return externalId;
	}

	public void setExternalId(String externalId) {
		this.externalId = externalId;
	}

	@Column
	public boolean isRecovered() {
		return recovered;
	}

	public void setRecovered(boolean recovered) {
		this.recovered = recovered;
	}

	@Column
	public boolean isVaccinated() {
		return vaccinated;
	}

	public void setVaccinated(boolean vaccinated) {
		this.vaccinated = vaccinated;
	}

	@Column
	public boolean isTestedNegative() {
		return testedNegative;
	}

	public void setTestedNegative(boolean testedNegative) {
		this.testedNegative = testedNegative;
	}

	@Type(type = ModelConstants.HIBERNATE_TYPE_JSON)
	@Column(columnDefinition = ModelConstants.COLUMN_DEFINITION_JSON)
	public List<DeaContentEntry> getDeaContent() {
		return deaContent;
	}

	public void setDeaContent(List<DeaContentEntry> deaContent) {
		this.deaContent = deaContent;
	}

	@Enumerated(EnumType.STRING)
	public QuarantineType getQuarantine() {
		return quarantine;
	}

	public void setQuarantine(QuarantineType quarantine) {
		this.quarantine = quarantine;
	}

	@Column(columnDefinition = "text")
	public String getQuarantineTypeDetails() {
		return quarantineTypeDetails;
	}

	public void setQuarantineTypeDetails(String quarantineTypeDetails) {
		this.quarantineTypeDetails = quarantineTypeDetails;
	}

	@Temporal(TemporalType.TIMESTAMP)
	public Date getQuarantineFrom() {
		return quarantineFrom;
	}

	public void setQuarantineFrom(Date quarantineFrom) {
		this.quarantineFrom = quarantineFrom;
	}

	@Temporal(TemporalType.TIMESTAMP)
	public Date getQuarantineTo() {
		return quarantineTo;
	}

	public void setQuarantineTo(Date quarantineTo) {
		this.quarantineTo = quarantineTo;
	}

	@Column(columnDefinition = "text")
	public String getQuarantineHelpNeeded() {
		return quarantineHelpNeeded;
	}

	public void setQuarantineHelpNeeded(String quarantineHelpNeeded) {
		this.quarantineHelpNeeded = quarantineHelpNeeded;
	}

	@Column
	public boolean isQuarantineOrderedVerbally() {
		return quarantineOrderedVerbally;
	}

	public void setQuarantineOrderedVerbally(boolean quarantineOrderedVerbally) {
		this.quarantineOrderedVerbally = quarantineOrderedVerbally;
	}

	@Column
	public boolean isQuarantineOrderedOfficialDocument() {
		return quarantineOrderedOfficialDocument;
	}

	public void setQuarantineOrderedOfficialDocument(boolean quarantineOrderedOfficialDocument) {
		this.quarantineOrderedOfficialDocument = quarantineOrderedOfficialDocument;
	}

	@Temporal(TemporalType.TIMESTAMP)
	public Date getQuarantineOrderedVerballyDate() {
		return quarantineOrderedVerballyDate;
	}

	public void setQuarantineOrderedVerballyDate(Date quarantineOrderedVerballyDate) {
		this.quarantineOrderedVerballyDate = quarantineOrderedVerballyDate;
	}

	@Temporal(TemporalType.TIMESTAMP)
	public Date getQuarantineOrderedOfficialDocumentDate() {
		return quarantineOrderedOfficialDocumentDate;
	}

	public void setQuarantineOrderedOfficialDocumentDate(Date quarantineOrderedOfficialDocumentDate) {
		this.quarantineOrderedOfficialDocumentDate = quarantineOrderedOfficialDocumentDate;
	}

	@Enumerated(EnumType.STRING)
	public YesNoUnknown getQuarantineHomePossible() {
		return quarantineHomePossible;
	}

	public void setQuarantineHomePossible(YesNoUnknown quarantineHomePossible) {
		this.quarantineHomePossible = quarantineHomePossible;
	}

	@Column(columnDefinition = "text")
	public String getQuarantineHomePossibleComment() {
		return quarantineHomePossibleComment;
	}

	public void setQuarantineHomePossibleComment(String quarantineHomePossibleComment) {
		this.quarantineHomePossibleComment = quarantineHomePossibleComment;
	}

	@Enumerated(EnumType.STRING)
	public YesNoUnknown getQuarantineHomeSupplyEnsured() {
		return quarantineHomeSupplyEnsured;
	}

	public void setQuarantineHomeSupplyEnsured(YesNoUnknown quarantineHomeSupplyEnsured) {
		this.quarantineHomeSupplyEnsured = quarantineHomeSupplyEnsured;
	}

	@Column(columnDefinition = "text")
	public String getQuarantineHomeSupplyEnsuredComment() {
		return quarantineHomeSupplyEnsuredComment;
	}

	public void setQuarantineHomeSupplyEnsuredComment(String quarantineHomeSupplyEnsuredComment) {
		this.quarantineHomeSupplyEnsuredComment = quarantineHomeSupplyEnsuredComment;
	}

	@Column
	public boolean isQuarantineExtended() {
		return quarantineExtended;
	}

	public void setQuarantineExtended(boolean quarantineExtended) {
		this.quarantineExtended = quarantineExtended;
	}

	@Column
	public boolean isQuarantineReduced() {
		return quarantineReduced;
	}

	public void setQuarantineReduced(boolean quarantineReduced) {
		this.quarantineReduced = quarantineReduced;
	}

	@Column
	public boolean isQuarantineOfficialOrderSent() {
		return quarantineOfficialOrderSent;
	}

	public void setQuarantineOfficialOrderSent(boolean quarantineOfficialOrderSent) {
		this.quarantineOfficialOrderSent = quarantineOfficialOrderSent;
	}

	@Temporal(TemporalType.TIMESTAMP)
	public Date getQuarantineOfficialOrderSentDate() {
		return quarantineOfficialOrderSentDate;
	}

	public void setQuarantineOfficialOrderSentDate(Date quarantineOfficialOrderSentDate) {
		this.quarantineOfficialOrderSentDate = quarantineOfficialOrderSentDate;
	}

}
