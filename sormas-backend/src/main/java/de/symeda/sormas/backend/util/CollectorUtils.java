package de.symeda.sormas.backend.util;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collector;

import javax.validation.constraints.NotNull;

public class CollectorUtils {

	private CollectorUtils() {
	}

	public static <T, K, U> Collector<T, ?, Map<K, U>> toNullSafeMap(
		@NotNull Function<? super T, ? extends K> keyMapper,
		@NotNull Function<? super T, ? extends U> valueMapper) {
		return Collector.of(
			HashMap::new,
			(m, item) -> m.put(keyMapper.apply(item), valueMapper.apply(item)),  // handles nulls
			(map1, map2) -> {
				map1.putAll(map2);
				return map1;
			});
	}

	public static <T, K, U> Collector<T, ?, Map<K, U>> toOrderedNullSafeMap(
		@NotNull Function<? super T, ? extends K> keyMapper,
		@NotNull Function<? super T, ? extends U> valueMapper) {
		return Collector.of(
			LinkedHashMap::new,
			(m, item) -> m.put(keyMapper.apply(item), valueMapper.apply(item)),  // handles nulls
			(map1, map2) -> {
				map1.putAll(map2);
				return map1;
			});
	}

	/**
	 * When you want a single response or none.
	 * 
	 * @return Optional: empty or with value if present
	 * @throws IllegalStateException
	 *             in case multiple matching elements are found.
	 * @param <T>
	 *            stream type
	 */
	public static <T> Collector<T, ?, Optional<T>> toOptionalSingle() {
		class Box {

			T value;
			boolean present;

			void add(T element) {
				if (present) {
					throw new IllegalStateException(String.format("Expected at most one element but found multiple: [%s] [%s]", value, element));
				}
				value = element;
				present = true;
			}

			Box merge(Box other) {
				if (this.present && other.present) {
					throw new IllegalStateException(
						String.format("Expected at most one element but found multiple: [%s] [%s]", this.value, other.value));
				}
				return this.present ? this : other;
			}

			Optional<T> finish() {
				return present ? Optional.of(value) : Optional.empty();
			}
		}

		return Collector.of(Box::new, Box::add, Box::merge, Box::finish);
	}

}
