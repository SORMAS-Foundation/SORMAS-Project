package de.symeda.sormas.backend.news;

import javax.persistence.Entity;

import de.symeda.sormas.backend.common.AbstractDomainObject;

@Entity
public class EiosBoardConfig extends AbstractDomainObject {

	public static final String ENABLE = "enabled";
	private Long boardId;
	private Long startTimeStamp;
	private Boolean enabled;

	public Long getBoardId() {
		return boardId;
	}

	public void setBoardId(Long boardId) {
		this.boardId = boardId;
	}

	public Long getStartTimeStamp() {
		return startTimeStamp;
	}

	public void setStartTimeStamp(Long startTimeStamp) {
		this.startTimeStamp = startTimeStamp;
	}

	public Boolean getEnabled() {
		return enabled;
	}

	public void setEnabled(Boolean enabled) {
		this.enabled = enabled;
	}
}
