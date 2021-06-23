package de.symeda.sormas.backend.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

import de.symeda.sormas.api.externaldata.ExternalDataDto;
import de.symeda.sormas.api.externaldata.ExternalDataUpdateException;
import de.symeda.sormas.api.externaldata.HasExternalData;
import de.symeda.sormas.backend.common.AbstractDomainObject;

public class ExternalDataUtil {

	public static <T extends AbstractDomainObject & HasExternalData> void updateExternalData(
		List<ExternalDataDto> externalData,
		Function<List<String>, List<T>> entityRetrieveFunction,
		Consumer<T> saveEntityConsumer)
		throws ExternalDataUpdateException {
		if (externalData.isEmpty()) {
			return;
		}

		Map<String, ExternalDataDto> externalDataDtoMap =
			externalData.stream().collect(Collectors.toMap(ExternalDataDto::getUuid, Function.identity()));
		List<String> uuids = new ArrayList<>(externalDataDtoMap.keySet());

		List<T> entitiesToUpdate = entityRetrieveFunction.apply(uuids);
		for (T externalDataEntity : entitiesToUpdate) {
			ExternalDataDto externalDataUpdate = externalDataDtoMap.get(externalDataEntity.getUuid());
			if ((externalDataEntity.getExternalId() != null && externalDataUpdate.getExternalId() != null)
				|| (externalDataEntity.getExternalToken() != null && externalDataUpdate.getExternalToken() != null)) {
				throw new ExternalDataUpdateException(String.format("Entity with uuid %s already has externalId or externalToken", externalDataEntity.getUuid()));
			}

			if (externalDataUpdate.getExternalId() != null) {
				externalDataEntity.setExternalId(externalDataUpdate.getExternalId());
			}
			if (externalDataUpdate.getExternalToken() != null) {
				externalDataEntity.setExternalToken(externalDataUpdate.getExternalToken());
			}
			saveEntityConsumer.accept(externalDataEntity);
		}

	}

}
