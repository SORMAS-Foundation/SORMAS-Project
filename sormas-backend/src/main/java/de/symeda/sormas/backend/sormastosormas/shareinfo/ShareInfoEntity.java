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

package de.symeda.sormas.backend.sormastosormas.shareinfo;

import javax.persistence.DiscriminatorColumn;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import de.symeda.sormas.backend.common.AbstractDomainObject;

@Table(name = "sormastosormasshareinfo_entities")
@MappedSuperclass
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "type")
public class ShareInfoEntity<T extends AbstractDomainObject> {

	private static final String SEQ_GEN_NAME = "ENTITY_SEQ_GEN";
	private static final String SEQ_SQL_NAME = "ENTITY_SEQ";

	public static final String CAZE = "caze";
	public static final String CONTACT = "contact";
	public static final String SAMPLE = "sample";
	public static final String EVENT = "event";
	public static final String EVENT_PARTICIPANT = "eventParticipant";

	private Long id;

	private SormasToSormasShareInfo shareInfo;

	private T entity;

	public ShareInfoEntity() {
	}

	protected ShareInfoEntity(SormasToSormasShareInfo shareInfo, T entity) {
		this.shareInfo = shareInfo;
		this.entity = entity;
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

	@ManyToOne
	public SormasToSormasShareInfo getShareInfo() {
		return shareInfo;
	}

	public void setShareInfo(SormasToSormasShareInfo shareInfo) {
		this.shareInfo = shareInfo;
	}

	@ManyToOne
	public T getEntity() {
		return entity;
	}

	public void setEntity(T entity) {
		this.entity = entity;
	}
}
