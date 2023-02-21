/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2021 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

package de.symeda.sormas.api.sormastosormas.entities;

import java.util.List;

import javax.ejb.Remote;
import javax.validation.Valid;

import de.symeda.sormas.api.sormastosormas.DuplicateResult;
import de.symeda.sormas.api.sormastosormas.ShareTreeCriteria;
import de.symeda.sormas.api.sormastosormas.SormasToSormasEncryptedDataDto;
import de.symeda.sormas.api.sormastosormas.SormasToSormasException;
import de.symeda.sormas.api.sormastosormas.SormasToSormasOptionsDto;
import de.symeda.sormas.api.sormastosormas.SormasToSormasShareTree;
import de.symeda.sormas.api.sormastosormas.validation.SormasToSormasValidationException;

@Remote
public interface SormasToSormasEntityInterface {

	void share(List<String> entityUuids, @Valid SormasToSormasOptionsDto options) throws SormasToSormasException;

	void saveShareRequest(SormasToSormasEncryptedDataDto encryptedData) throws SormasToSormasException, SormasToSormasValidationException;

	DuplicateResult acceptShareRequest(String uuid, boolean checkDuplicates) throws SormasToSormasException, SormasToSormasValidationException;

	SormasToSormasEncryptedDataDto getDataForShareRequest(SormasToSormasEncryptedDataDto encryptedRequestUuid) throws SormasToSormasException;

	SormasToSormasEncryptedDataDto saveSharedEntities(SormasToSormasEncryptedDataDto encryptedData) throws SormasToSormasException, SormasToSormasValidationException;

	void syncShares(ShareTreeCriteria criteria);

	void saveSyncedEntity(SormasToSormasEncryptedDataDto encryptedData) throws SormasToSormasException, SormasToSormasValidationException;

	List<SormasToSormasShareTree> getAllShares(String uuid);

	SormasToSormasEncryptedDataDto getShareTrees(SormasToSormasEncryptedDataDto encryptedData) throws SormasToSormasException;

	boolean hasPendingRequest(List<String> entityUuids);
}
