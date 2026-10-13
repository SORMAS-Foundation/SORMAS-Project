package de.symeda.sormas.api.news.eios;

import java.time.ZonedDateTime;

import de.symeda.sormas.api.utils.criteria.BaseCriteria;

public class EiosArticleCriteria extends BaseCriteria {

	private ZonedDateTime timeSince;
	private ZonedDateTime timeUntil;
	private Integer start;
	private Integer limit;
	private Long boardId;

	public ZonedDateTime getTimeSince() {
		return timeSince;
	}

	public void setTimeSince(ZonedDateTime timeSince) {
		this.timeSince = timeSince;
	}

	public ZonedDateTime getTimeUntil() {
		return timeUntil;
	}

	public void setTimeUntil(ZonedDateTime timeUntil) {
		this.timeUntil = timeUntil;
	}

	public Integer getStart() {
		return start;
	}

	public void setStart(Integer start) {
		this.start = start;
	}

	public Integer getLimit() {
		return limit;
	}

	public void setLimit(Integer limit) {
		this.limit = limit;
	}

	public Long getBoardId() {
		return boardId;
	}

	public void setBoardId(Long boardId) {
		this.boardId = boardId;
	}
}
