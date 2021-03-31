package de.symeda.sormas.api.region;

import de.symeda.sormas.api.InfrastructureDataReferenceDto;
import de.symeda.sormas.api.statistics.StatisticsGroupingKey;

public class SubcontinentReferenceDto extends InfrastructureDataReferenceDto implements StatisticsGroupingKey {

	public SubcontinentReferenceDto() {
	}

	public SubcontinentReferenceDto(String uuid) {
		super(uuid);
	}

	public SubcontinentReferenceDto(String uuid, String caption, String externalId) {
		super(uuid, caption, externalId);
	}

	@Override
	public int keyCompareTo(StatisticsGroupingKey o) {
		if (o == null) {
			throw new NullPointerException("Can't compare to null.");
		}

		if (this.equals(o)) {
			return 0;
		}
		int captionComparison = this.getCaption().compareTo(((SubcontinentReferenceDto) o).getCaption());
		if (captionComparison != 0) {
			return captionComparison;
		} else {
			return this.getUuid().compareTo(((SubcontinentReferenceDto) o).getUuid());
		}
	}

}
