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
import org.apache.commons.lang3.StringUtils;

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
			if (StringUtils.isNotBlank(externalDataEntity.getExternalId()) && StringUtils.isNotBlank(externalDataUpdate.getExternalId())) {
				throw new ExternalDataUpdateException(String.format("Entity with uuid %s already has externalId", externalDataEntity.getUuid()));
			} else if (StringUtils.isNotBlank(externalDataUpdate.getExternalId())) {
				externalDataEntity.setExternalId(externalDataUpdate.getExternalId());
			}

			if (StringUtils.isNotBlank(externalDataEntity.getExternalToken()) && StringUtils.isNotBlank(externalDataUpdate.getExternalToken())) {
				throw new ExternalDataUpdateException(String.format("Entity with uuid %s already has externalToken", externalDataEntity.getUuid()));
			} else if (StringUtils.isNotBlank(externalDataUpdate.getExternalToken())) {
				externalDataEntity.setExternalToken(externalDataUpdate.getExternalToken());
			}

			saveEntityConsumer.accept(externalDataEntity);
		}

	}

}
