package de.symeda.sormas.api.user;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;

class UserRightCaptionsTest {

	@Test
	void everyUserRightHasACaption() {
		ResourceBundle captions = ResourceBundle.getBundle("enum", Locale.ROOT);

		List<String> missing = Arrays.stream(UserRight.values())
			.map(right -> "UserRight." + right.name())
			.filter(key -> !captions.containsKey(key))
			.collect(Collectors.toList());

		assertEquals(List.of(), missing);
	}
}
