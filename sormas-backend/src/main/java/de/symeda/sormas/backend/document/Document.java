/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2020 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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
package de.symeda.sormas.backend.document;

import static de.symeda.sormas.backend.document.Document.TABLE_NAME;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import de.symeda.sormas.api.document.DocumentRelatedEntityType;
import de.symeda.sormas.backend.common.AbstractDomainObject;
import de.symeda.sormas.backend.common.CronService;
import de.symeda.sormas.backend.user.User;

@Entity(name = TABLE_NAME)
public class Document extends AbstractDomainObject {

	public static final String TABLE_NAME = "documents";

	public static final String DELETED = "deleted";
	public static final String UPLOADING_USER = "uploadingUser";
	public static final String NAME = "name";
    public static final String MIME_TYPE = "mimeType";
	public static final String SIZE = "size";
	public static final String RELATED_ENTITY_UUID = "relatedEntityUuid";
	public static final String RELATED_ENTITY_TYPE = "relatedEntityType";

	private boolean deleted;
	private User uploadingUser;
	private String name;
	private String mimeType;
	private long size;
	private String storageReference;
	private String relatedEntityUuid;
	private DocumentRelatedEntityType relatedEntityType;

	/**
	 * Indicates whether the document is marked for deletion.
	 *
	 * <p>
	 * Documents aren't directly deleted. They're instead marked for deletion,
	 * and a nightly process actually deletes both the files and the document
	 * metadata (this entity).
	 * <p>
	 * This allows for non-atomic backups:
	 * <ol>
	 * <li>First backup the database, this will include "marked as deleted" documents;
	 * <li>Then backup the file storage, it might include files for documents added
	 * since the database backup, and files for documents marked as deleted might
	 * be <i>missing</i> as they could have already been cleaned up.
	 * </ol>
	 * <p>
	 * Once a document is marked for deletion, it will never be restored.
	 *
	 * @see CronService#cleanupDeletedDocuments()
	 */
	public boolean isDeleted() {
		return deleted;
	}

	public void setDeleted(boolean deleted) {
		this.deleted = deleted;
	}

	@ManyToOne(cascade = {}, fetch = FetchType.LAZY)
	@JoinColumn(name = "uploadinguser_id", nullable = false)
	public User getUploadingUser() {
		return uploadingUser;
	}

	public void setUploadingUser(User uploadingUser) {
		this.uploadingUser = uploadingUser;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Column(name = "mimetype")
	public String getMimeType() {
		return mimeType;
	}

	public void setMimeType(String mimeType) {
		this.mimeType = mimeType;
	}

	public long getSize() {
		return size;
	}

	public void setSize(long size) {
		this.size = size;
	}

	@Column(name = "storage_reference")
	public String getStorageReference() {
		return storageReference;
	}

	public void setStorageReference(String storageReference) {
		this.storageReference = storageReference;
	}

	@Column(name = "relatedentity_uuid")
	public String getRelatedEntityUuid() {
		return relatedEntityUuid;
	}

	public void setRelatedEntityUuid(String relatedEntityUuid) {
		this.relatedEntityUuid = relatedEntityUuid;
	}

	@Enumerated(EnumType.STRING)
	@Column(name = "relatedentity_type")
	public DocumentRelatedEntityType getRelatedEntityType() {
		return relatedEntityType;
	}

	public void setRelatedEntityType(DocumentRelatedEntityType relatedEntityType) {
		this.relatedEntityType = relatedEntityType;
	}
}
