package de.symeda.sormas.backend.patch.customizablefield;

import java.util.function.BiConsumer;

import de.symeda.sormas.api.customizablefield.CustomizableFieldValueDto;

/**
 * Typed Bi-Consumer to ease reading.
 * 
 * @param <T>
 *            targetType.
 */
@FunctionalInterface
public interface CustomizableFieldSetter<T> extends BiConsumer<CustomizableFieldValueDto, T> {

}
