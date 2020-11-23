package de.symeda.sormas.api.region;

import de.symeda.sormas.api.ReferenceDto;
import de.symeda.sormas.api.statistics.StatisticsGroupingKey;

public class CountryReferenceDto extends ReferenceDto implements StatisticsGroupingKey {

	private static final long serialVersionUID = -7477992903590074568L;

	public CountryReferenceDto() {
	}

    public CountryReferenceDto(String uuid) {
        setUuid(uuid);
    }

    public CountryReferenceDto(String uuid, String caption) {
        setUuid(uuid);
        setCaption(caption);
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
