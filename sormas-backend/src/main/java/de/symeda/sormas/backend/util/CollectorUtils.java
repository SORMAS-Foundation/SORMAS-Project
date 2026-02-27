package de.symeda.sormas.backend.util;

import java.util.HashMap;
import java.util.Map;
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

}
