/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2026 SORMAS Foundation gGmbH
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

package de.symeda.sormas.backend.customizablefield;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Objects;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import de.symeda.sormas.api.customizablefield.CustomizableFieldContext;

/**
 * Verifies that for every {@link CustomizableFieldContext} enum value there is a
 * corresponding LIST partition of {@code customizablefieldvalue} declared in
 * {@code sormas_schema.sql}.
 * <p>
 * Convention: the partition table is named
 * {@code customizablefieldvalue_<context.name().toLowerCase()>}.
 * <p>
 * When a new context is added to the enum, this test will fail until the matching
 * {@code CREATE TABLE customizablefieldvalue_<name> PARTITION OF customizablefieldvalue
 * FOR VALUES IN ('<NAME>')} migration is added to the schema.
 */
class CustomizableFieldSchemaTest {

    private static final String PARTITION_TABLE_PREFIX = "customizablefieldvalue_";

    @ParameterizedTest
    @EnumSource(CustomizableFieldContext.class)
    void testPartitionTableExistsForContext(CustomizableFieldContext context) throws IOException, URISyntaxException {
        String schema = new String(
            Files.readAllBytes(Paths.get(Objects.requireNonNull(getClass().getClassLoader().getResource("sql/sormas_schema.sql")).toURI())));

        String expectedTableName = PARTITION_TABLE_PREFIX + context.name().toLowerCase();
        String expectedStatement = "CREATE TABLE " + expectedTableName;

        assertTrue(
            schema.contains(expectedStatement),
            "Missing partition table declaration in sormas_schema.sql for CustomizableFieldContext." + context.name() + ": expected to find '"
                + expectedStatement + "'. Add a PARTITION OF customizablefieldvalue FOR VALUES IN ('" + context.name()
                + "') migration alongside the new enum value.");
    }
}
