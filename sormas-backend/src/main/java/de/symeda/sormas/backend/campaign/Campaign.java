package de.symeda.sormas.backend.campaign;

import de.symeda.auditlog.api.Audited;
import de.symeda.sormas.backend.common.CoreAdo;
import de.symeda.sormas.backend.user.User;

import javax.persistence.*;
import java.util.Date;

@Entity(name = "campaigns")
@Audited
public class Campaign extends CoreAdo {

	private static final long serialVersionUID = -2744033662114826543L;

	public static final String TABLE_NAME = "campaigns";

	public static final String NAME = "name";
	public static final String DESCRIPTION = "description";
	public static final String START_DATE = "startDate";
	public static final String END_DATE = "endDate";
	public static final String CREATING_USER = "creatingUser";
	public static final String ARCHIVED = "archived";

	private String name;
	private String description;
	private Date startDate;
	private Date endDate;
	private User creatingUser;
	private boolean archived;

	@Column(length = 255)
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Column(length = 512)
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Temporal(TemporalType.TIMESTAMP)
	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	@Temporal(TemporalType.TIMESTAMP)
	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	@ManyToOne(cascade = {})
	@JoinColumn(nullable = false)
	public User getCreatingUser() {
		return creatingUser;
	}

	public void setCreatingUser(User creatingUser) {
		this.creatingUser = creatingUser;
	}

	@Column
	public boolean isArchived() {
		return archived;
	}

	public void setArchived(boolean archived) {
		this.archived = archived;
	}
}
