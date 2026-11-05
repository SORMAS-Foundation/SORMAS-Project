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

package de.symeda.sormas.backend.common;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.time.Duration;
import java.time.Instant;

import org.junit.jupiter.api.Test;

import de.symeda.sormas.api.importexport.ImportExportUtils;
import de.symeda.sormas.backend.AbstractBeanTest;
import de.symeda.sormas.backend.MockProducer;

public class CronServiceTest extends AbstractBeanTest {

	@Test
	public void cleanUpTemporaryFilesDeletesOldPrefixedFiles() throws IOException {
		Path tempFolder = Paths.get(MockProducer.TMP_PATH);
		Files.createDirectories(tempFolder);
		Path prefixed = Files.createTempFile(tempFolder, ImportExportUtils.TEMP_FILE_PREFIX + "_quarantine_x", ".docx");
		Path unprefixed = Files.createTempFile(tempFolder, "other_quarantine_x", ".docx");

		try {
			FileTime threeHoursAgo = FileTime.from(Instant.now().minus(Duration.ofHours(3)));
			Files.setLastModifiedTime(prefixed, threeHoursAgo);
			Files.setLastModifiedTime(unprefixed, threeHoursAgo);
			Files.setAttribute(prefixed, "basic:creationTime", threeHoursAgo);
			Files.setAttribute(unprefixed, "basic:creationTime", threeHoursAgo);
			assumeTrue(
				Files.readAttributes(prefixed, BasicFileAttributes.class)
					.creationTime()
					.toInstant()
					.isBefore(Instant.now().minus(Duration.ofHours(2))),
				"File system does not support setting the creation time");

			getBean(CronService.class).cleanUpTemporaryFiles();

			assertFalse(Files.exists(prefixed));
			assertTrue(Files.exists(unprefixed));
		} finally {
			Files.deleteIfExists(prefixed);
			Files.deleteIfExists(unprefixed);
		}
	}
}
