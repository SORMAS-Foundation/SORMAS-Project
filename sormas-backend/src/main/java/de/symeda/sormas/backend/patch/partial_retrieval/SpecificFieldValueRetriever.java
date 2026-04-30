package de.symeda.sormas.backend.patch.partial_retrieval;

import de.symeda.sormas.api.patch.partial_retrieval.FieldInfo;
import de.symeda.sormas.api.utils.OrderedRegisterable;

import javax.validation.constraints.NotNull;
import java.util.Set;

public interface SpecificFieldValueRetriever {

    FieldInfo getFieldInfo(String fieldName);

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

    default String toFieldName(String prefix, String fieldName) {
        return prefix + '.' + fieldName;
    }
}
