package de.symeda.sormas.ui.utils;

import de.symeda.sormas.api.BaseCriteria;

public interface V7AbstractGrid<C extends BaseCriteria> {

	C getCriteria();

	void setCriteria(C criteria);
}
