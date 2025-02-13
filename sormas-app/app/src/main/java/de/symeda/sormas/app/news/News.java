package de.symeda.sormas.app.news;

import java.util.Date;

import de.symeda.sormas.api.news.NewsIndexDto;
import de.symeda.sormas.app.backend.common.HasLocalChangeDate;

public class News extends NewsIndexDto implements HasLocalChangeDate {

	@Override
	public Date getLocalChangeDate() {
		return getNewsDate();
	}
}
