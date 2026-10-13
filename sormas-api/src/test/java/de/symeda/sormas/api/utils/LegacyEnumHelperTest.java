package de.symeda.sormas.api.utils;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

public class LegacyEnumHelperTest {

	enum Merged {

		@LegacyEnumNames({
			"OLD_A",
			"OLD_B" })
		SURVIVOR,
		UNTOUCHED
	}

	/** A legacy name that is still a live constant would be unreachable, because resolve() tries valueOf first. */
	enum ShadowsALiveConstant {

		@LegacyEnumNames("UNTOUCHED")
		SURVIVOR,
		UNTOUCHED
	}

	/** The same legacy name claimed twice would silently last-win in the alias map. */
	enum ClaimsTheSameNameTwice {

		@LegacyEnumNames("OLD")
		FIRST,
		@LegacyEnumNames("OLD")
		SECOND
	}

	enum NoLegacyNames {
		A,
		B
	}

	enum DeclaresAnEmptyLegacyNameList {

		@LegacyEnumNames({})
		A,
		B
	}

	/** The same legacy name twice on ONE constant is as much a declaration bug as twice on two. */
	enum ClaimsTheSameNameTwiceOnOneConstant {

		@LegacyEnumNames({
			"OLD",
			"OLD" })
		SURVIVOR
	}

	@Test
	public void resolvesCurrentNames() {
		assertThat(LegacyEnumHelper.resolve(Merged.class, "SURVIVOR"), is(Merged.SURVIVOR));
		assertThat(LegacyEnumHelper.resolve(Merged.class, "UNTOUCHED"), is(Merged.UNTOUCHED));
	}

	@Test
	public void resolvesRetiredNamesToTheConstantThatAbsorbedThem() {
		assertThat(LegacyEnumHelper.resolve(Merged.class, "OLD_A"), is(Merged.SURVIVOR));
		assertThat(LegacyEnumHelper.resolve(Merged.class, "OLD_B"), is(Merged.SURVIVOR));
	}

	@Test
	public void trimsSurroundingWhitespace() {
		assertThat(LegacyEnumHelper.resolve(Merged.class, "  OLD_A  "), is(Merged.SURVIVOR));
	}

	@Test
	public void rejectsBlankAndUnknownNames() {
		// never returns null: a blank enum value in untrusted JSON must be rejected, not coerced
		assertThrows(IllegalArgumentException.class, () -> LegacyEnumHelper.resolve(Merged.class, null));
		assertThrows(IllegalArgumentException.class, () -> LegacyEnumHelper.resolve(Merged.class, ""));
		assertThrows(IllegalArgumentException.class, () -> LegacyEnumHelper.resolve(Merged.class, "   "));
		assertThrows(IllegalArgumentException.class, () -> LegacyEnumHelper.resolve(Merged.class, "NOT_A_CONSTANT"));
	}

	@Test
	public void resolveOrNullDegradesInsteadOfThrowing() {
		// the Android Gson adapter relies on this: a name from a newer peer must not abort the whole sync batch
		assertThat(LegacyEnumHelper.resolveOrNull(Merged.class, "OLD_A"), is(Merged.SURVIVOR));
		assertThat(LegacyEnumHelper.resolveOrNull(Merged.class, "NOT_A_CONSTANT"), is(nullValue()));
		assertThat(LegacyEnumHelper.resolveOrNull(Merged.class, ""), is(nullValue()));
		assertThat(LegacyEnumHelper.resolveOrNull(Merged.class, null), is(nullValue()));
	}

	@Test
	public void hasLegacyNamesOnlyForEnumsThatDeclareThem() {
		assertThat(LegacyEnumHelper.hasLegacyNames(Merged.class), is(true));
		assertThat(LegacyEnumHelper.hasLegacyNames(NoLegacyNames.class), is(false));
		// nothing to translate, so the Gson adapter factory must fall through to Gson's own enum handling
		assertThat(LegacyEnumHelper.hasLegacyNames(DeclaresAnEmptyLegacyNameList.class), is(false));
	}

	@Test
	public void rejectsTheSameAliasDeclaredTwiceOnOneConstant() {
		assertThrows(IllegalStateException.class, () -> LegacyEnumHelper.resolve(ClaimsTheSameNameTwiceOnOneConstant.class, "WHATEVER"));
	}

	@Test
	public void rejectsAnAliasThatShadowsALiveConstant() {
		assertThrows(IllegalStateException.class, () -> LegacyEnumHelper.resolve(ShadowsALiveConstant.class, "WHATEVER"));
	}

	@Test
	public void rejectsTheSameAliasDeclaredOnTwoConstants() {
		assertThrows(IllegalStateException.class, () -> LegacyEnumHelper.resolve(ClaimsTheSameNameTwice.class, "WHATEVER"));
	}
}
