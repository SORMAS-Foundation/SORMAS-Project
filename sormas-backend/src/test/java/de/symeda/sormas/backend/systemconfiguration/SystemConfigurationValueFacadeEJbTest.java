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
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import de.symeda.sormas.api.systemconfiguration.SystemConfigurationValueDto;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.api.utils.ValidationRuntimeException;
import de.symeda.sormas.backend.AbstractBeanTest;

class SystemConfigurationValueFacadeEJbTest extends AbstractBeanTest {

    @BeforeEach
    void setUp() {
        createSystemConfigurationDefaultCategory();
    }

    /**
     * Test the retrieval of a system configuration value by its UUID.
     */
    @Test
    void testGetSystemConfigurationValue() {

        final SystemConfigurationValue configValue = createSystemConfigurationValue("TEST_KEY");
        final SystemConfigurationValueDto configValueDto = getSystemConfigurationValueFacade().getByUuid(configValue.getUuid());

        assertThat(configValueDto.getValue(), is(configValue.getValue()));
        assertThat(configValueDto.getCategory().getUuid(), is(configValue.getCategory().getUuid()));
        assertThat(configValueDto.getEncrypt(), is(configValue.getEncrypt()));
        assertThat(configValueDto.getPattern(), is(configValue.getPattern()));
        assertThat(configValueDto.getValidationMessage(), is(configValue.getValidationMessage()));
    }

    /**
     * Test the update of a system configuration value.
     */
    @Test
    void testUpdateSystemConfigurationValue() {

        final SystemConfigurationValue configValue = createSystemConfigurationValue("TEST_KEY");

        SystemConfigurationValueDto configValueDto = getSystemConfigurationValueFacade().getByUuid(configValue.getUuid());
        assertThat(configValueDto.getValue(), is(configValue.getValue()));
        assertThat(configValueDto.getCategory().getUuid(), is(configValue.getCategory().getUuid()));

        configValueDto.setValue("updated-value");
        configValueDto.setEncrypt(true);
        configValueDto.setPattern("updated-pattern");
        configValueDto.setValidationMessage("updated-validation-message");

        final SystemConfigurationValueDto updatedConfigValue = getSystemConfigurationValueFacade().save(configValueDto);
        assertThat(updatedConfigValue.getValue(), is("updated-value"));
        assertThat(updatedConfigValue.getCategory().getUuid(), is(configValue.getCategory().getUuid()));
        assertThat(updatedConfigValue.getEncrypt(), is(true));
        assertThat(configValueDto.getPattern(), is("updated-pattern"));
        assertThat(updatedConfigValue.getValidationMessage(), is("updated-validation-message"));

        configValueDto = getSystemConfigurationValueFacade().getByUuid(configValue.getUuid());
        assertThat(configValueDto.getValue(), is("updated-value"));
        assertThat(configValueDto.getCategory().getUuid(), is(configValue.getCategory().getUuid()));
        assertThat(configValueDto.getEncrypt(), is(true));
        assertThat(configValueDto.getPattern(), is("updated-pattern"));
        assertThat(configValueDto.getValidationMessage(), is("updated-validation-message"));
    }

    /**
     * Test the validation of a system configuration value against an IP pattern.
     */
    @Test
    void testValidateSystemConfigurationValueIpPattern() {

        final String ipPattern = "^((25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$";

        final SystemConfigurationValue configValue = createSystemConfigurationValue("IP_PATTERN_KEY", "192.168.1.1", ipPattern);
        final SystemConfigurationValueDto configValueDto = getSystemConfigurationValueFacade().getByUuid(configValue.getUuid());

        // Validate against IP pattern
        final SystemConfigurationValueDto updatedConfigValue = getSystemConfigurationValueFacade().save(configValueDto);

        assertThat(updatedConfigValue.getKey(), is("IP_PATTERN_KEY"));
        assertThat(updatedConfigValue.getValue(), is("192.168.1.1"));
    }

    /**
     * Test the validation of a system configuration value against an IP pattern with an invalid value.
     */
    @Test
    void testValidateSystemConfigurationValueIpPatternFail() {

        final String ipPattern = "^((25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$";

        final SystemConfigurationValue configValue = createSystemConfigurationValue("IP_PATTERN_KEY", "invalid-ip", ipPattern);
        final SystemConfigurationValueDto configValueDto = getSystemConfigurationValueFacade().getByUuid(configValue.getUuid());

        // Validate against IP pattern and expect exception
        assertThrows(ValidationRuntimeException.class, () -> {
            getSystemConfigurationValueFacade().save(configValueDto);
        });
    }

    /**
     * Test the validation of a system configuration value against a Unix directory path pattern.
     */
    @Test
    void testValidateSystemConfigurationValueUnixDirPattern() {

        final String unixDirPattern = "^(/[^/ ]*)+/$";

        final SystemConfigurationValue configValue = createSystemConfigurationValue("UNIX_DIR_PATTERN_KEY", "/home/user/", unixDirPattern);
        final SystemConfigurationValueDto configValueDto = getSystemConfigurationValueFacade().getByUuid(configValue.getUuid());

        // Validate against Unix directory path pattern
        final SystemConfigurationValueDto updatedConfigValue = getSystemConfigurationValueFacade().save(configValueDto);

        assertThat(updatedConfigValue.getKey(), is("UNIX_DIR_PATTERN_KEY"));
        assertThat(updatedConfigValue.getValue(), is("/home/user/"));
    }

    /**
     * Test the validation of a system configuration value against a Unix directory path pattern with an invalid value.
     */
    @Test
    void testValidateSystemConfigurationValueUnixDirPatternFail() {

        final String unixDirPattern = "^(/[^/ ]*)+/$";

        final SystemConfigurationValue configValue = createSystemConfigurationValue("UNIX_DIR_PATTERN_KEY", "invalid-path", unixDirPattern);
        final SystemConfigurationValueDto configValueDto = getSystemConfigurationValueFacade().getByUuid(configValue.getUuid());

        // Validate against Unix directory path pattern and expect exception
        assertThrows(ValidationRuntimeException.class, () -> {
            getSystemConfigurationValueFacade().save(configValueDto);
        });
    }

    /**
     * Test the validation of a system configuration value against a digits-only pattern.
     */
    @Test
    void testValidateSystemConfigurationValueDigitsOnlyPattern() {

        final String digitsOnlyPattern = "^\\d+$";

        final SystemConfigurationValue configValue = createSystemConfigurationValue("DIGITS_ONLY_PATTERN_KEY", "123456", digitsOnlyPattern);
        final SystemConfigurationValueDto configValueDto = getSystemConfigurationValueFacade().getByUuid(configValue.getUuid());

        // Validate against digits-only pattern
        final SystemConfigurationValueDto updatedConfigValue = getSystemConfigurationValueFacade().save(configValueDto);

        assertThat(updatedConfigValue.getKey(), is("DIGITS_ONLY_PATTERN_KEY"));
        assertThat(updatedConfigValue.getValue(), is("123456"));
    }

    /**
     * Test the validation of a system configuration value against a digits-only pattern with an invalid value.
     */
    @Test
    void testValidateSystemConfigurationValueDigitsOnlyPatternFail() {

        final String digitsOnlyPattern = "^\\d+$";

        final SystemConfigurationValue configValue = createSystemConfigurationValue("DIGITS_ONLY_PATTERN_KEY", "invalid123", digitsOnlyPattern);
        final SystemConfigurationValueDto configValueDto = getSystemConfigurationValueFacade().getByUuid(configValue.getUuid());

        // Validate against digits-only pattern and expect exception
        assertThrows(ValidationRuntimeException.class, () -> {
            getSystemConfigurationValueFacade().save(configValueDto);
        });
    }

    /**
     * Test the validation of a system configuration value against a pipe-separated list of words pattern.
     */
    @Test
    void testValidateSystemConfigurationValuePipeSeparatedWordsPattern() {

        final String pipeSeparatedWordsPattern = "^([a-zA-Z]+\\|)*[a-zA-Z]+$";

        final SystemConfigurationValue configValue =
            createSystemConfigurationValue("PIPE_SEPARATED_WORDS_PATTERN_KEY", "word|word|word", pipeSeparatedWordsPattern);
        final SystemConfigurationValueDto configValueDto = getSystemConfigurationValueFacade().getByUuid(configValue.getUuid());

        // Validate against pipe-separated list of words pattern
        final SystemConfigurationValueDto updatedConfigValue = getSystemConfigurationValueFacade().save(configValueDto);

        assertThat(updatedConfigValue.getKey(), is("PIPE_SEPARATED_WORDS_PATTERN_KEY"));
        assertThat(updatedConfigValue.getValue(), is("word|word|word"));
    }

    /**
     * Test the validation of a system configuration value against a pipe-separated list of words pattern with an invalid value.
     */
    @Test
    void testValidateSystemConfigurationValuePipeSeparatedWordsPatternFail() {

        final String pipeSeparatedWordsPattern = "^([a-zA-Z]+\\|)*[a-zA-Z]+$";

        final SystemConfigurationValue configValue =
            createSystemConfigurationValue("PIPE_SEPARATED_WORDS_PATTERN_KEY", "234|678|123", pipeSeparatedWordsPattern);
        final SystemConfigurationValueDto configValueDto = getSystemConfigurationValueFacade().getByUuid(configValue.getUuid());

        // Validate against pipe-separated list of words pattern and expect exception
        assertThrows(ValidationRuntimeException.class, () -> {
            getSystemConfigurationValueFacade().save(configValueDto);
        });
    }

    /**
     * Create a new system configuration value with the specified key and value.
     *
     * @param key
     *            the key of the configuration value
     * @param value
     *            the value of the configuration value
     * @return the created SystemConfigurationValue
     */
    private SystemConfigurationValue createSystemConfigurationValue(final String key) {

        final SystemConfigurationValue configValue = new SystemConfigurationValue();
        configValue.setUuid(DataHelper.createUuid());
        configValue.setKey(key);
        configValue.setValue("test-value");
        configValue.setCategory(getOrCreateDefaultCategory());
        configValue.setEncrypt(false);
        configValue.setValidationMessage("validation-message");
        getSystemConfigurationValueService().ensurePersisted(configValue);
        return configValue;
    }

    /**
     * Create a new system configuration value with the specified key,value and pattern.
     *
     * @param key
     *            the key of the configuration value
     * @param value
     *            the value of the configuration value
     * @param pattern
     *            the pattern of the configuration value
     * @return the created SystemConfigurationValue
     */
    private SystemConfigurationValue createSystemConfigurationValue(final String key, final String value, final String pattern) {

        final SystemConfigurationValue configValue = new SystemConfigurationValue();
        configValue.setUuid(DataHelper.createUuid());
        configValue.setKey(key);
        configValue.setValue(value);
        configValue.setCategory(getOrCreateDefaultCategory());
        configValue.setPattern(pattern);
        getSystemConfigurationValueService().ensurePersisted(configValue);
        return configValue;
    }

    /**
     * Get or create the default system configuration category.
     *
     * @return the default SystemConfigurationCategory
     */
    private SystemConfigurationCategory getOrCreateDefaultCategory() {

        try {
            return getSystemConfigurationCategoryService().getDefaultCategory();
        } catch (final IllegalStateException e) {
            return createSystemConfigurationDefaultCategory();
        }
    }

    /**
     * Creates the default category.
     *
     * @return the default SystemConfigurationCategory
     */
    private SystemConfigurationCategory createSystemConfigurationDefaultCategory() {

        final SystemConfigurationCategory category = new SystemConfigurationCategory();
        category.setUuid(DataHelper.createUuid());
        category.setName(SystemConfigurationCategoryService.DEFAULT_CATEGORY_NAME);
        getSystemConfigurationCategoryService().ensurePersisted(category);
        return category;
    }
}
