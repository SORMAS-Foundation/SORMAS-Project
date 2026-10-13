package de.symeda.sormas.backend.news;

import java.time.ZonedDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;

import de.symeda.sormas.backend.common.AbstractDomainObject;

@Entity
public class EiosBoardConfig extends AbstractDomainObject {

	public static final String ENABLE = "enabled";
	private Long boardId;
	private ZonedDateTime startDateTime;
	private Boolean enabled;

	public Long getBoardId() {
		return boardId;
	}

	public void setBoardId(Long boardId) {
		this.boardId = boardId;
	}

	/**
	 * Point in time up to which the board's articles have been fetched; the next sync starts from here.
	 */
	@Column(name = "startdatetime")
	public ZonedDateTime getStartDateTime() {
		return startDateTime;
	}

	public void setStartDateTime(ZonedDateTime startDateTime) {
		this.startDateTime = startDateTime;
	}

	public Boolean getEnabled() {
		return enabled;
	}

	public void setEnabled(Boolean enabled) {
		this.enabled = enabled;
	}
}
