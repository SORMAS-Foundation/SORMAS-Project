package de.symeda.sormas.app.rest;

import de.symeda.sormas.api.caze.CriteriaWithSorting;
import de.symeda.sormas.api.common.Page;
import de.symeda.sormas.api.news.NewsCriteria;
import de.symeda.sormas.app.news.News;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface NewsFacadeRetro {

	@POST("news/indexList")
	Call<Page<News>> pullNewsIndexList(
		@Body CriteriaWithSorting<NewsCriteria> criteriaWithSorting,
		@Query("offset") int offset,
		@Query("size") int size);
}
