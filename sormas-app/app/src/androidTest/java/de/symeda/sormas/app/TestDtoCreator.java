package de.symeda.sormas.app;

import java.util.Date;

import de.symeda.sormas.api.DataTransferObject;
import de.symeda.sormas.api.utils.DataHelper;

/**
 * Created by Martin Wahnschaffe on 05.12.2017.
 */

public final class TestDtoCreator {

    public static DataTransferObject fillNewDto(DataTransferObject dto) {
        dto.setUuid(DataHelper.createUuid());
        dto.setCreationDate(new Date()); // now
        dto.setChangeDate(new Date());
        return dto;
    }
}
