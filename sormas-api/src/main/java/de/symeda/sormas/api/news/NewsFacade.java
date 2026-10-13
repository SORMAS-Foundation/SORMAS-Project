package de.symeda.sormas.api.news;

import java.util.List;

import javax.ejb.Remote;

import de.symeda.sormas.api.BaseFacade;
import de.symeda.sormas.api.EditPermissionFacade;
import de.symeda.sormas.api.common.Page;
import de.symeda.sormas.api.utils.SortProperty;

@Remote
public interface NewsFacade extends BaseFacade<NewsDto, NewsIndexDto, NewsReferenceDto, NewsCriteria>, EditPermissionFacade {

	void markApprove(NewsReferenceDto newsRef);

	void markUnUseful(NewsReferenceDto newsRef);

	Page<NewsIndexDto> getNewsPage(NewsCriteria newsCriteria, int offset, int size, List<SortProperty> sortProperties);
}
