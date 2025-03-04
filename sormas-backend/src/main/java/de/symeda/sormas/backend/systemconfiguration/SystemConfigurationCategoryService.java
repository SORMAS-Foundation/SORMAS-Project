package de.symeda.sormas.backend.systemconfiguration;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.From;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import de.symeda.sormas.api.systemconfiguration.SystemConfigurationCategoryCriteria;
import de.symeda.sormas.backend.common.AdoServiceWithUserFilterAndJurisdiction;
import de.symeda.sormas.backend.common.CriteriaBuilderHelper;

/**
 * Service class for managing system configuration categories.
 */
@Stateless
@LocalBean
public class SystemConfigurationCategoryService extends AdoServiceWithUserFilterAndJurisdiction<SystemConfigurationCategory> {

    public static final String DEFAULT_CATEGORY_NAME = "GENERAL_CATEGORY";

    /**
     * Default constructor.
     */
    public SystemConfigurationCategoryService() {
        this(SystemConfigurationCategory.class);
    }

    /**
     * Retrieves the default category.
     *
     * @return the default category 
     * @throws IllegalStateException if no default category is found
     */
    public SystemConfigurationCategory getDefaultCategory() {
        final SystemConfigurationCategory defaultCategory =
            getByPredicate((cb, root, cq) -> cb.equal(root.get(SystemConfigurationCategory.NAME_FIELD_NAME), DEFAULT_CATEGORY_NAME)).stream()
                .findFirst()
                .orElse(null);

        if (null == defaultCategory) {
            logger.error("No default category found with name: {}", DEFAULT_CATEGORY_NAME);
            throw new IllegalStateException("No default category found");
        }

        logger.debug("Default category retrieved: {}", defaultCategory);
        return defaultCategory;
    }

    /**
     * Constructor with element class parameter.
     *
     * @param elementClass
     *            the class of the element
     */
    protected SystemConfigurationCategoryService(Class<SystemConfigurationCategory> elementClass) {
        super(elementClass);
    }

    /**
     * Creates a user filter predicate.
     *
     * @param cb
     *            the criteria builder
     * @param cq
     *            the criteria query
     * @param from
     *            the from clause
     * @return the user filter predicate
     */
    @Override
    public Predicate createUserFilter(CriteriaBuilder cb, @SuppressWarnings("rawtypes") CriteriaQuery cq, From<?, SystemConfigurationCategory> from) {
        return null;
    }

    /**
     * Builds a criteria filter based on the provided criteria.
     *
     * @param criteria
     *            the criteria to filter by
     * @param cb
     *            the criteria builder
     * @param from
     *            the root entity
     * @return the criteria filter predicate
     */
    public Predicate buildCriteriaFilter(SystemConfigurationCategoryCriteria criteria, CriteriaBuilder cb, Root<SystemConfigurationCategory> from) {
        Predicate filter = cb.conjunction();

        if (criteria.getFreeTextFilter() != null) {
            String[] textFilters = criteria.getFreeTextFilter().split("\\s+");
            for (String textFilter : textFilters) {
                if (textFilter.isEmpty()) {
                    continue;
                }

                Predicate likeFilters = cb.or(
                    CriteriaBuilderHelper.unaccentedIlike(cb, from.get(SystemConfigurationCategory.NAME_FIELD_NAME), textFilter),
                    CriteriaBuilderHelper.unaccentedIlike(cb, from.get(SystemConfigurationCategory.DESCRIPTION_FIELD_NAME), textFilter));
                filter = CriteriaBuilderHelper.and(cb, filter, likeFilters);
            }
        }

        return filter;
    }
}
