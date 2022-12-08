/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2022 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

package de.symeda.sormas.backend.sormastosormas.share.outgoing;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;

import de.symeda.sormas.api.sormastosormas.share.incoming.ShareRequestDataType;
import de.symeda.sormas.api.sormastosormas.share.incoming.ShareRequestStatus;
import de.symeda.sormas.api.utils.FieldConstraints;
import de.symeda.sormas.backend.common.AbstractDomainObject;
import de.symeda.sormas.backend.user.User;

@Entity(name = "sharerequestinfo")
public class ShareRequestInfo extends AbstractDomainObject {

	private static final long serialVersionUID = 6983296810004546738L;

	public static final String TABLE_NAME = "sharerequestinfo";

	public static final String SHARE_REQUEST_INFO_SHARE_INFO_TABLE = "sharerequestinfo_shareinfo";
	public static final String SHARE_REQUEST_INFO_SHARE_INFO_JOIN_COLUMN = "sharerequestinfo_id";
	public static final String SHARE_REQUEST_INFO_SHARE_INFO_INVERS_JOIN_COLUMN = "shareinfo_id";

	public static final String DATA_TYPE = "dataType";
	public static final String SHARES = "shares";
	public static final String REQUEST_STATUS = "requestStatus";
	public static final String SENDER = "sender";

	public static final String COMMENT = "comment";

	private ShareRequestDataType dataType;

	private List<SormasToSormasShareInfo> shares;

	private ShareRequestStatus requestStatus;

	private User sender;

	private boolean withAssociatedContacts;

	private boolean withSamples;

	private boolean withEventParticipants;

	private boolean withImmunizations;

	private boolean withSurveillanceReports;

	private boolean pseudonymizedPersonalData;

	private boolean pseudonymizedSensitiveData;

	private String comment;

	private String responseComment;

	private boolean ownershipHandedOver;

	@Enumerated(EnumType.STRING)
	public ShareRequestDataType getDataType() {
		return dataType;
	}

	public void setDataType(ShareRequestDataType dataType) {
		this.dataType = dataType;
	}

	@ManyToMany(cascade = CascadeType.ALL)
	@JoinTable(name = SHARE_REQUEST_INFO_SHARE_INFO_TABLE,
		joinColumns = @JoinColumn(name = SHARE_REQUEST_INFO_SHARE_INFO_JOIN_COLUMN),
		inverseJoinColumns = @JoinColumn(name = SHARE_REQUEST_INFO_SHARE_INFO_INVERS_JOIN_COLUMN))
	public List<SormasToSormasShareInfo> getShares() {
		return shares;
	}

	public void setShares(List<SormasToSormasShareInfo> shares) {
		this.shares = shares;
	}

	@Enumerated(EnumType.STRING)
	public ShareRequestStatus getRequestStatus() {
		return requestStatus;
	}

	public void setRequestStatus(ShareRequestStatus requestStatus) {
		this.requestStatus = requestStatus;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(nullable = false)
	public User getSender() {
		return sender;
	}

	public void setSender(User sender) {
		this.sender = sender;
	}

	@Column
	public boolean isWithAssociatedContacts() {
		return withAssociatedContacts;
	}

	public void setWithAssociatedContacts(boolean withAssociatedContacts) {
		this.withAssociatedContacts = withAssociatedContacts;
	}

	@Column
	public boolean isWithSamples() {
		return withSamples;
	}

	public void setWithSamples(boolean withSamples) {
		this.withSamples = withSamples;
	}

	@Column
	public boolean isWithEventParticipants() {
		return withEventParticipants;
	}

	public void setWithEventParticipants(boolean withEventParticipants) {
		this.withEventParticipants = withEventParticipants;
	}

	@Column
	public boolean isWithImmunizations() {
		return withImmunizations;
	}

	public void setWithImmunizations(boolean withImmunizations) {
		this.withImmunizations = withImmunizations;
	}

	@Column
	public boolean isWithSurveillanceReports() {
		return withSurveillanceReports;
	}

	public void setWithSurveillanceReports(boolean withSurveillanceReports) {
		this.withSurveillanceReports = withSurveillanceReports;
	}

	@Column
	public boolean isPseudonymizedPersonalData() {
		return pseudonymizedPersonalData;
	}

	public void setPseudonymizedPersonalData(boolean pseudonymizedPersonalData) {
		this.pseudonymizedPersonalData = pseudonymizedPersonalData;
	}

	@Column
	public boolean isPseudonymizedSensitiveData() {
		return pseudonymizedSensitiveData;
	}

	public void setPseudonymizedSensitiveData(boolean pseudonymizedSensitiveData) {
		this.pseudonymizedSensitiveData = pseudonymizedSensitiveData;
	}

	@Column(length = FieldConstraints.CHARACTER_LIMIT_BIG)
	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	@Column(length = FieldConstraints.CHARACTER_LIMIT_BIG)
	public String getResponseComment() {
		return responseComment;
	}

	public void setResponseComment(String responseComment) {
		this.responseComment = responseComment;
	}

	@Column
	public boolean isOwnershipHandedOver() {
		return ownershipHandedOver;
	}

	public void setOwnershipHandedOver(boolean ownershipHandedOver) {
		this.ownershipHandedOver = ownershipHandedOver;
	}

	public List<AbstractDomainObject> extractSharedMainEntities() {
		return shares.stream().map(this::extractSharedMainEntity).filter(Objects::nonNull).collect(Collectors.toList());
	}

	private AbstractDomainObject extractSharedMainEntity(SormasToSormasShareInfo shareInfo) {
		switch (dataType) {
		case CASE:
			return shareInfo.getCaze();
		case CONTACT:
			return shareInfo.getContact();
		case EVENT:
			return shareInfo.getEvent();
		default:
			throw new RuntimeException("Unknown share data type [" + dataType + "]");
		}
	}
}
