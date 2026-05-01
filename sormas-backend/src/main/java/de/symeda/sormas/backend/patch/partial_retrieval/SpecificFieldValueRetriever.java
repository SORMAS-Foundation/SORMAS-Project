package de.symeda.sormas.backend.patch.partial_retrieval;

import de.symeda.sormas.api.EntityDto;
import de.symeda.sormas.api.patch.partial_retrieval.FieldInfo;
import de.symeda.sormas.api.utils.OrderedRegisterable;

import javax.validation.constraints.NotNull;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;

// TODO: check for adequate entityDto / generics usage to make it nicer.
public interface SpecificFieldValueRetriever {

    /**
     *
     * @param fieldName
     * @param entityDto
     * @return
     */
    FieldInfo getFieldInfo(String fieldName, EntityDto entityDto);

    /**
     * Meant to be implemented by classes implementing this {@link OrderedRegisterable} contract but to be used.
     * For usages prefer {@link #supports(String)}.
     *
     * @return types that are supported by this class.
     */
    @NotNull
    Set<String> getSupportedFields();

    /**
     * Specifies if the targetType is supported by this class.
     *
     * @param targetFieldName
     *            can be a child class.
     * @return true if the class will be able to perform some action with this type.
     */
    default boolean supports(@NotNull String targetFieldName) {
        return getSupportedFields().contains(targetFieldName);
    }
}
