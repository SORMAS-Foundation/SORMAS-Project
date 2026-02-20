package de.symeda.sormas.patch.mapping.exceptions;

import java.text.ParseException;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

public class ErrorDescriptionMapper {

	// TODO: I18N
	private static final Map<Class<?>, Function<RuntimeException, String>> MAPPERS = Map.ofEntries(
		Map.entry(IllegalArgumentException.class, e -> formatIllegalArgument((IllegalArgumentException) e)),
		Map.entry(NumberFormatException.class, e -> "Invalid number format"),
		Map.entry(ParseException.class, e -> "Invalid date format"),
		Map.entry(IllegalStateException.class, e -> "Invalid operation state"),
		Map.entry(EnumConstantNotPresentException.class, e -> "Invalid enum member"),
		// Add more: ClassCastException, NoSuchMethodException, etc.
		Map.entry(RuntimeException.class, RuntimeException::getMessage) // final fallback
	);

	@SuppressWarnings("unchecked")
	public static String toUserDescription(RuntimeException e) {
		return findHandler(e.getClass()).orElse(RuntimeException::getMessage).apply(e);
	}

	private static Optional<Function<RuntimeException, String>> findHandler(Class<?> exceptionClass) {
		Class<?> current = exceptionClass;
		while (current != null) {
			if (MAPPERS.containsKey(current)) {
				return Optional.of((Function<RuntimeException, String>) MAPPERS.get(current));
			}
			current = current.getSuperclass();
		}
		return Optional.empty();
	}

	private static String formatIllegalArgument(IllegalArgumentException e) {
		return "Invalid input: " + extractKey(e.getMessage());
	}

	// Extract field name from common patterns like "Cannot map field 'age'"
	private static String extractKey(String msg) {
		return msg.replaceAll(".*['\"]([^'\"]*)['\"].*", "$1");
	}
}
