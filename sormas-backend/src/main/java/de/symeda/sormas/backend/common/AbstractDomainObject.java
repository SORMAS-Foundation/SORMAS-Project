/*******************************************************************************
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2018 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 *******************************************************************************/
package de.symeda.sormas.backend.common;

import java.io.Serializable;
import java.sql.Timestamp;
import java.time.Instant;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.SequenceGenerator;
import javax.persistence.Version;
import javax.validation.constraints.Size;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.annotations.TypeDef;
import org.hibernate.proxy.HibernateProxyHelper;

import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import com.vladmihalcea.hibernate.type.json.JsonType;

import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.api.uuid.HasUuid;
import de.symeda.sormas.backend.user.CurrentUserService;
import de.symeda.sormas.backend.user.User;

/**
 * Note: The hibernate-types article suggests to use JsonBinaryType for Postgres, but doesn't explain why.
 * Since JsonBinaryType does not work with H2, we are using JsonStringType instead
 */

@TypeDef(name = "json", typeClass = JsonType.class)
@TypeDef(name = "jsonb", typeClass = JsonBinaryType.class)
@MappedSuperclass
@EntityListeners(AbstractDomainObject.AdoListener.class)
public abstract class AbstractDomainObject implements Serializable, Cloneable, HasUuid {

	private static final long serialVersionUID = 3957437214306161226L;

	private static final String SEQ_GEN_NAME = "ENTITY_SEQ_GEN";
	private static final String SEQ_SQL_NAME = "ENTITY_SEQ";

	public static final String HISTORY_TABLE_SUFFIX = "_history";

	public static final String ID = "id";
	public static final String UUID = "uuid";
	public static final String CREATION_DATE = "creationDate";
	public static final String CHANGE_DATE = "changeDate";

	@NotExposedToApi
	private Long id;
	private String uuid;
	private Timestamp creationDate;
	private Timestamp changeDate;
	@NotExposedToApi
	private User changeUser;

	@Override
	public AbstractDomainObject clone() {
		try {
			return (AbstractDomainObject) super.clone();
		} catch (CloneNotSupportedException e) {
			throw new RuntimeException(e);
		}
	}

	@Id
	@SequenceGenerator(name = SEQ_GEN_NAME, allocationSize = 1, sequenceName = SEQ_SQL_NAME)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = SEQ_GEN_NAME)
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@Basic(optional = false)
	@Size(min = 20, max = 36)
	@Column(nullable = false, unique = true, length = 36)
	public String getUuid() {

		if (uuid == null) {
			/*
			 * New objects should automatically get a UUID.
			 * This should be returned already before saving via getUuid().
			 * The generation of UUIDs is relatively time-consuming. Most objects are loaded from the database.
			 * Therefore, no UUIDs should be created for these objects.
			 * This is not compatible with lazy instance loading:
			 * The UUIDs will not be overwritten later.
			 * Solution: getUuid () may create a UUID & the ADO Interceptor calls getUuid() before saving.
			 * Then this can be done immediately with getDate().
			 */
			uuid = DataHelper.createUuid();
		}
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	@Column(nullable = false, columnDefinition = "TIMESTAMP(3) not null")
	public Timestamp getCreationDate() {
		if (creationDate == null) {
			creationDate = Timestamp.from(Instant.now());
		}
		return creationDate;
	}

	public void setCreationDate(Timestamp creationDate) {
		this.creationDate = creationDate;
	}

	@Version
	@Column(nullable = false, columnDefinition = "TIMESTAMP(3) not null")
	public Timestamp getChangeDate() {
		return changeDate;
	}

	public void setChangeDate(Timestamp changeDate) {
		this.changeDate = changeDate;
	}

	@ManyToOne(cascade = CascadeType.REFRESH, fetch = FetchType.LAZY)
	@JoinColumn(name = "change_user_id")
	public User getChangeUser() {
		return changeUser;
	}

	public void setChangeUser(User changeUser) {
		this.changeUser = changeUser;
	}

	@Override
	public boolean equals(Object o) {

		if (this == o) {
			return true;
		}
		if (o == null) {
			return false;
		}

		if (HibernateProxyHelper.getClassWithoutInitializingProxy(o) == HibernateProxyHelper.getClassWithoutInitializingProxy(this)) {
			// this works, because we are using UUIDs
			AbstractDomainObject ado = (AbstractDomainObject) o;
			return getUuid().equals(ado.getUuid());

		} else {
			return false;
		}
	}

	@Override
	public int hashCode() {
		return getUuid().hashCode();
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + StringUtils.SPACE + getUuid();
	}

	static class AdoListener {

		private User getCurrentUser() {
			try {
				CurrentUserService currentUserService =
					(CurrentUserService) new InitialContext().lookup("java:global/sormas-ear/sormas-backend/CurrentUserService");
				return currentUserService.getCurrentUser();
			} catch (NamingException e) {
				throw new RuntimeException(e);
			}
		}

		@PrePersist
		@PreUpdate
		private void beforeAnyUpdate(AbstractDomainObject ado) {
			User currentUser = getCurrentUser();
			ado.setChangeUser(currentUser);
		}
	}
}
