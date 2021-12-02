package de.symeda.sormas.app.backend.sormastosormas;

import static de.symeda.sormas.api.utils.FieldConstraints.CHARACTER_LIMIT_BIG;
import static de.symeda.sormas.api.utils.FieldConstraints.CHARACTER_LIMIT_DEFAULT;

import javax.persistence.Column;
import javax.persistence.Entity;

import com.j256.ormlite.table.DatabaseTable;

import de.symeda.sormas.app.backend.common.AbstractDomainObject;
import de.symeda.sormas.app.backend.common.EmbeddedAdo;

@Entity(name = SormasToSormasOriginInfo.TABLE_NAME)
@DatabaseTable(tableName = SormasToSormasOriginInfo.TABLE_NAME)
@EmbeddedAdo(nullable = true)
public class SormasToSormasOriginInfo extends AbstractDomainObject {

	public static final String TABLE_NAME = "sormasToSormasOriginInfo";
	private static final String I18N_PREFIX = "SormasToSormasOriginInfo";

	@Column(length = CHARACTER_LIMIT_DEFAULT)
	private String organizationId;

	@Column(length = CHARACTER_LIMIT_DEFAULT)
	private String senderName;

	@Column(length = CHARACTER_LIMIT_DEFAULT)
	private String senderEmail;

	@Column(length = CHARACTER_LIMIT_DEFAULT)
	private String senderPhoneNumber;

	@Column
	private boolean ownershipHandedOver;

	@Column
	private boolean withAssociatedContacts;

	@Column
	private boolean withSamples;

	@Column
	private boolean withEventParticipants;

	@Column(length = CHARACTER_LIMIT_BIG)
	private String comment;

	@Override
	public String getI18nPrefix() {
		return I18N_PREFIX;
	}

	public String getOrganizationId() {
		return organizationId;
	}

	public void setOrganizationId(String organizationId) {
		this.organizationId = organizationId;
	}

	public String getSenderName() {
		return senderName;
	}

	public void setSenderName(String senderName) {
		this.senderName = senderName;
	}

	public String getSenderEmail() {
		return senderEmail;
	}

	public void setSenderEmail(String senderEmail) {
		this.senderEmail = senderEmail;
	}

	public String getSenderPhoneNumber() {
		return senderPhoneNumber;
	}

	public void setSenderPhoneNumber(String senderPhoneNumber) {
		this.senderPhoneNumber = senderPhoneNumber;
	}

	public boolean isOwnershipHandedOver() {
		return ownershipHandedOver;
	}

	public void setOwnershipHandedOver(boolean ownershipHandedOver) {
		this.ownershipHandedOver = ownershipHandedOver;
	}

	public boolean isWithAssociatedContacts() {
		return withAssociatedContacts;
	}

	public void setWithAssociatedContacts(boolean withAssociatedContacts) {
		this.withAssociatedContacts = withAssociatedContacts;
	}

	public boolean isWithSamples() {
		return withSamples;
	}

	public void setWithSamples(boolean withSamples) {
		this.withSamples = withSamples;
	}

	public boolean isWithEventParticipants() {
		return withEventParticipants;
	}

	public void setWithEventParticipants(boolean withEventParticipants) {
		this.withEventParticipants = withEventParticipants;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}
}
