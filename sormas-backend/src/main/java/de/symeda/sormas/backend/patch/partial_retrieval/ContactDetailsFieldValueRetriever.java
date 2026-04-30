package de.symeda.sormas.backend.patch.partial_retrieval;

import de.symeda.sormas.api.EntityDto;
import de.symeda.sormas.api.patch.partial_retrieval.FieldInfo;

import java.util.Set;

public class ContactDetailsFieldValueRetriever implements SpecificFieldValueRetriever {
    @Override
    public FieldInfo getFieldInfo(String fieldName, EntityDto entityDto) {
        // TODO.
        return null;
    }

    @Override
    public Set<String> getSupportedFields() {
        return Set.of();
    }
}
