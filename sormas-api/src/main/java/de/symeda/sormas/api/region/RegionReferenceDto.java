package de.symeda.sormas.api.region;

import de.symeda.sormas.api.ReferenceDto;
import de.symeda.sormas.api.statistics.StatisticsGroupingKey;

public class RegionReferenceDto extends ReferenceDto implements StatisticsGroupingKey {

	private static final long serialVersionUID = -1610675328037466348L;

	public RegionReferenceDto() {

	}

	public RegionReferenceDto(String uuid) {
		setUuid(uuid);
	}

	public RegionReferenceDto(String uuid, String caption) {
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
		
		int captionComparison = this.getCaption().compareTo(((RegionReferenceDto) o) .getCaption());
		if (captionComparison != 0) {
			return captionComparison;
		} else {
			return this.getUuid().compareTo(((RegionReferenceDto) o).getUuid());
		}
	}

}
