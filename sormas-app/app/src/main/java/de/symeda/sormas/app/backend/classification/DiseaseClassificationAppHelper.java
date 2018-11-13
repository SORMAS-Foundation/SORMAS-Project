/*
 * This file is part of SORMAS®.
 *
 * SORMAS® is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * SORMAS® is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with SORMAS®.  If not, see <http://www.gnu.org/licenses/>.
 */

package de.symeda.sormas.app.backend.classification;

import android.util.Log;

import java.util.Date;

import de.symeda.sormas.api.caze.classification.ClassificationHtmlRenderer;
import de.symeda.sormas.api.caze.classification.DiseaseClassificationCriteria;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.app.backend.common.DaoException;
import de.symeda.sormas.app.backend.common.DatabaseHelper;

public class DiseaseClassificationAppHelper {

    public static void saveClassificationToDatabase(DiseaseClassificationCriteria classificationCriteria) {
        DiseaseClassification classification = new DiseaseClassification();

        classification.setDisease(classificationCriteria.getDisease());
        classification.setChangeDate(classificationCriteria.getChangeDate());
        classification.setCreationDate(new Date());
        classification.setUuid(DataHelper.createUuid());
        classification.setSuspectCriteria(ClassificationHtmlRenderer.createSuspectHtmlString(classificationCriteria));
        classification.setProbableCriteria(ClassificationHtmlRenderer.createProbableHtmlString(classificationCriteria));
        classification.setConfirmedCriteria(ClassificationHtmlRenderer.createConfirmedHtmlString(classificationCriteria));

        try {
            DatabaseHelper.getDiseaseClassificationDao().saveAndSnapshot(classification);
        } catch (DaoException e) {
            Log.e(DiseaseClassificationAppHelper.class.getName(), "Could not save disease classification to database");
        }
    }

}
