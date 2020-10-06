package de.symeda.sormas.api.docgeneneration;

import de.symeda.sormas.api.BaseCriteria;
import de.symeda.sormas.api.utils.IgnoreForUrl;

import java.io.Serializable;

public class TemplateCriteria extends BaseCriteria implements Serializable, Cloneable{

    private static final long serialVersionUID = 7818514340250141255L;

    private String nameEpidLike;

    @IgnoreForUrl
    public String getNameEpidLike() {
        return nameEpidLike;
    }

    public TemplateCriteria nameEpidLike(String nameEpidLike) {
        this.nameEpidLike = nameEpidLike;
        return this;
    }
}
