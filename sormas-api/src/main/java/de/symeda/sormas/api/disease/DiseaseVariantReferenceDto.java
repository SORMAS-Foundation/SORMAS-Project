/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2021 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package de.symeda.sormas.api.disease;

import de.symeda.sormas.api.ReferenceDto;

public class DiseaseVariantReferenceDto extends ReferenceDto {

    private static final long serialVersionUID = 7980726625509183207L;

    public DiseaseVariantReferenceDto() {
    }

    public DiseaseVariantReferenceDto(String uuid) {
        super(uuid);
    }

    public DiseaseVariantReferenceDto(String uuid, String caption) {
        super(uuid, caption);
    }
}
