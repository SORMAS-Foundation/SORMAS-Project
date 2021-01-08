package de.symeda.sormas.api.region;

import de.symeda.sormas.api.ReferenceDto;
import de.symeda.sormas.api.statistics.StatisticsGroupingKey;

public class CountryReferenceDto extends ReferenceDto implements StatisticsGroupingKey {

	private static final long serialVersionUID = -7477992903590074568L;

	private String isoCode;

	public CountryReferenceDto() {
	}

	public CountryReferenceDto(String uuid, String isoCode) {
		super(uuid);
		this.isoCode = isoCode;
	}

	public CountryReferenceDto(String uuid, String caption, String isoCode) {
		setUuid(uuid);
		setCaption(caption);
		this.isoCode = isoCode;
	}

	public String getIsoCode() {
		return isoCode;
	}

	public void setIsoCode(String isoCode) {
		this.isoCode = isoCode;
	}

	@Override
	public int keyCompareTo(StatisticsGroupingKey o) {

		if (o == null) {
			throw new NullPointerException("Can't compare to null.");
		}

		if (this.equals(o)) {
			return 0;
		}

		int captionComparison = this.getCaption().compareTo(((CountryReferenceDto) o).getCaption());
		if (captionComparison != 0) {
			return captionComparison;
		} else {
			return this.getUuid().compareTo(((CountryReferenceDto) o).getUuid());
		}
	}
}
