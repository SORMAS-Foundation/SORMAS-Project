package de.symeda.sormas.api.contact;

import de.symeda.sormas.api.visit.VisitStatus;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class VisitsExportDto implements Serializable {

	private static final long serialVersionUID = 7066530434713936967L;

	public static final String I18N_PREFIX = "ContactVisitExport";

    private Long personId;
    private Integer maximumFollowUpVisits;

    private String uuid;
    private String firstName;
    private String lastName;
    private Date lastContactDate;
    private Date followUpUntil;
    private List<VisitDetailsExportDto> visitDetails;

    public VisitsExportDto(String uuid, Long personId, String firstName, String lastName, Date lastContactDate, Date followUpUntil) {
        this.uuid = uuid;
        this.personId = personId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.lastContactDate = lastContactDate;
        this.followUpUntil = followUpUntil;
    }

    public Long getPersonId() {
        return personId;
    }

    public Date getLastContactDate() {
        return lastContactDate;
    }

    public Date getFollowUpUntil() {
        return followUpUntil;
    }

    public Integer getMaximumFollowUpVisits() {
        return maximumFollowUpVisits;
    }

    public void setMaximumFollowUpVisits(Integer maximumFollowUpVisits) {
        this.maximumFollowUpVisits = maximumFollowUpVisits;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public List<VisitDetailsExportDto> getVisitDetails() {
        return visitDetails;
    }

    public void setVisitDetails(List<VisitDetailsExportDto> visitDetails) {
        this.visitDetails = visitDetails;
    }

    public static class VisitDetailsExportDto implements Serializable {
		private static final long serialVersionUID = -4677902897777543789L;
		
		private Date visitDateTime;
        private VisitStatus visitStatus;
        private String symptoms;

        public VisitDetailsExportDto(Date visitDateTime, VisitStatus visitStatus, String symptoms) {
            this.visitDateTime = visitDateTime;
            this.visitStatus = visitStatus;
            this.symptoms = symptoms;
        }

        public Date getVisitDateTime() {
            return visitDateTime;
        }

        public void setVisitDateTime(Date visitDateTime) {
            this.visitDateTime = visitDateTime;
        }

        public VisitStatus getVisitStatus() {
            return visitStatus;
        }

        public void setVisitStatus(VisitStatus visitStatus) {
            this.visitStatus = visitStatus;
        }

        public String getSymptoms() {
            return symptoms;
        }

        public void setSymptoms(String symptoms) {
            this.symptoms = symptoms;
        }
    }
}
