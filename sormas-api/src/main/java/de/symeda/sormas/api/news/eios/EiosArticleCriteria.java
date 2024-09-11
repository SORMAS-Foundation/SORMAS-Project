package de.symeda.sormas.api.news.eios;

import de.symeda.sormas.api.utils.criteria.BaseCriteria;

public class EiosArticleCriteria extends BaseCriteria {

	private Long timespan;
	private Long untilTimespan;
	private Integer start;
	private Integer limit;
	private Long boardId;

	public Long getTimespan() {
		return timespan;
	}

	public void setTimespan(Long timespan) {
		this.timespan = timespan;
	}

	public Long getUntilTimespan() {
		return untilTimespan;
	}

	public void setUntilTimespan(Long untilTimespan) {
		this.untilTimespan = untilTimespan;
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
