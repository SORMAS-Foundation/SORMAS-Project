/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2026 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package de.symeda.sormas.backend.systemconfiguration;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import de.symeda.sormas.api.systemconfiguration.SystemConfigurationCategoryDto;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.backend.AbstractBeanTest;

/**
 * Test class for the SystemConfigurationCategoryFacade EJB.
 */
class SystemConfigurationCategoryFacadeEJbTest extends AbstractBeanTest {

    /**
     * Set up the test environment by creating the default category.
     */
    @BeforeEach
    void setUp() {
        createDefaultCategory();
    }

    /**
     * Test to verify that the default category can be retrieved.
     */
    @Test
    void testGetDefaultCategory() {

        SystemConfigurationCategoryDto categoryDto = getSystemConfigurationCategoryFacade().getDefaultCategoryDto();

        assertThat(categoryDto.getName(), is(SystemConfigurationCategoryService.DEFAULT_CATEGORY_NAME));
    }

    /**
     * Test to verify that a category can be updated.
     */
    @Test
    void testUpdateCategory() {

        SystemConfigurationCategoryDto categoryDto = getSystemConfigurationCategoryFacade().getDefaultCategoryDto();
        assertThat(categoryDto.getName(), is(SystemConfigurationCategoryService.DEFAULT_CATEGORY_NAME));

        categoryDto.setDescription("updated-description");

        SystemConfigurationCategoryDto updatedCategory = getSystemConfigurationCategoryFacade().save(categoryDto);
        assertThat(updatedCategory.getDescription(), is("updated-description"));

        categoryDto = getSystemConfigurationCategoryFacade().getDefaultCategoryDto();
        assertThat(categoryDto.getName(), is(SystemConfigurationCategoryService.DEFAULT_CATEGORY_NAME));
        assertThat(categoryDto.getDescription(), is("updated-description"));
    }

    /**
     * Create the default category for testing purposes.
     * 
     * @return the created SystemConfigurationCategory
     */
    private SystemConfigurationCategory createDefaultCategory() {

        SystemConfigurationCategory category = new SystemConfigurationCategory();
        category.setUuid(DataHelper.createUuid());
        category.setName(SystemConfigurationCategoryService.DEFAULT_CATEGORY_NAME);
        getSystemConfigurationCategoryService().ensurePersisted(category);
        return category;
    }
}
