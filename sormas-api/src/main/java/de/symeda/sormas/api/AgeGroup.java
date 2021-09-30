package de.symeda.sormas.api;

import de.symeda.sormas.api.i18n.I18nProperties;

public enum AgeGroup {

	AGE_0_4,
	AGE_5_9,
	AGE_10_14,
	AGE_15_19,
	AGE_20_24,
	AGE_25_29,
	AGE_30_34,
	AGE_35_39,
	AGE_40_44,
	AGE_45_49,
	AGE_50_54,
	AGE_55_59,
	AGE_60_64,
	AGE_65_69,
	AGE_70_74,
	AGE_75_79,
	AGE_80_PLUS;

	public String toString() {
		return I18nProperties.getEnumCaption(this);
	}

	public static AgeGroup getAgeGroupFromIntegerRange(IntegerRange range) {

		if (range.getFrom() == 80 && range.getTo() == null) {
			return AGE_80_PLUS;
		}

		try {
			return AgeGroup.valueOf("AGE_" + range.getFrom() + "_" + range.getTo());
		} catch (IllegalArgumentException e) {
			return null;
		}
	}

	public IntegerRange toIntegerRange() {

		if (this == AGE_80_PLUS) {
			return new IntegerRange(80, null);
		}

		try {
			return new IntegerRange(
				Integer.valueOf(this.name().substring(this.name().indexOf("_") + 1, this.name().lastIndexOf("_"))),
				Integer.valueOf(this.name().substring(this.name().lastIndexOf("_") + 1, this.name().length())));
		} catch (NumberFormatException e) {
			return null;
		}
	}
}
