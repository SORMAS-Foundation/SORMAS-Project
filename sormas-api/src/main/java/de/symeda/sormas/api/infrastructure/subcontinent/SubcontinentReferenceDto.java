package de.symeda.sormas.api.infrastructure.subcontinent;

import de.symeda.sormas.api.InfrastructureDataReferenceDto;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.statistics.StatisticsGroupingKey;

public class SubcontinentReferenceDto extends InfrastructureDataReferenceDto implements StatisticsGroupingKey {

	public SubcontinentReferenceDto() {
	}

	public SubcontinentReferenceDto(String uuid) {
		super(uuid);
	}

	public SubcontinentReferenceDto(String uuid, String caption, Long externalId) {
		super(uuid, caption, externalId);
	}

	@Override
	public String getCaption() {
		return I18nProperties.getSubcontinentName(super.getCaption());
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
