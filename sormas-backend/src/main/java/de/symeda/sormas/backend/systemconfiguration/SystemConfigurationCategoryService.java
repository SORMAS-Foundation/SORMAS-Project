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
     * Constructor with element class parameter.
     *
     * @param elementClass
     *            the class of the element
     */
    protected SystemConfigurationCategoryService(final Class<SystemConfigurationCategory> elementClass) {
        super(elementClass);
    }

    /**
     * Retrieves the default category.
     *
     * @return the default category
     * @throws IllegalStateException
     *             if no default category is found
     */
    public SystemConfigurationCategory getDefaultCategory() {

        final SystemConfigurationCategory defaultCategory = getCategoryByName(DEFAULT_CATEGORY_NAME);
        if (null == defaultCategory) {
            logger.error("No default category found with name: {}", DEFAULT_CATEGORY_NAME);
            throw new IllegalStateException("No default category found");
        }

        logger.debug("Default category retrieved: {}", defaultCategory);
        return defaultCategory;
    }

    /**
     * Retrieves a category by its name.
     *
     * @param name
     *            the name of the category
     * @return the category with the specified name, or null if no such category exists
     */
    public SystemConfigurationCategory getCategoryByName(final String name) {

        final SystemConfigurationCategory category =
            getByPredicate((cb, root, cq) -> cb.equal(root.get(SystemConfigurationCategory.NAME_FIELD_NAME), name)).stream().findFirst().orElse(null);

        if (null == category) {
            logger.debug("No category found with name: {}", name);
            return null;
        }

        return category;
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
    public Predicate createUserFilter(final CriteriaBuilder cb, @SuppressWarnings("rawtypes") final CriteriaQuery cq, final From<?, SystemConfigurationCategory> from) {
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
    public Predicate buildCriteriaFilter(final SystemConfigurationCategoryCriteria criteria, final CriteriaBuilder cb, final Root<SystemConfigurationCategory> from) {

        Predicate filter = cb.conjunction();
        if (criteria.getFreeTextFilter() != null) {
            final String[] textFilters = criteria.getFreeTextFilter().split("\\s+");
            for (final String textFilter : textFilters) {
                if (textFilter.isEmpty()) {
                    continue;
                }

                final Predicate likeFilters = cb.or(
                    CriteriaBuilderHelper.unaccentedIlike(cb, from.get(SystemConfigurationCategory.NAME_FIELD_NAME), textFilter),
                    CriteriaBuilderHelper.unaccentedIlike(cb, from.get(SystemConfigurationCategory.DESCRIPTION_FIELD_NAME), textFilter));
                filter = CriteriaBuilderHelper.and(cb, filter, likeFilters);
            }
        }

        return filter;
    }
}
