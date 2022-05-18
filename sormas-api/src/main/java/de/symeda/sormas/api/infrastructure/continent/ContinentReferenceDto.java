package de.symeda.sormas.api.infrastructure.continent;

import de.symeda.sormas.api.InfrastructureDataReferenceDto;
import de.symeda.sormas.api.feature.FeatureType;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.statistics.StatisticsGroupingKey;
import de.symeda.sormas.api.utils.DependingOnFeatureType;

@DependingOnFeatureType(featureType = {
	FeatureType.CASE_SURVEILANCE,
	FeatureType.EVENT_SURVEILLANCE,
	FeatureType.AGGREGATE_REPORTING })
public class ContinentReferenceDto extends InfrastructureDataReferenceDto implements StatisticsGroupingKey {

	public ContinentReferenceDto() {
	}

	public ContinentReferenceDto(String uuid) {
		super(uuid);
	}

	public ContinentReferenceDto(String uuid, String caption, String externalId) {
		super(uuid, caption, externalId);
	}

	@Override
	public String getCaption() {
		return I18nProperties.getContinentName(super.getCaption());
	}

	@Override
	public int keyCompareTo(StatisticsGroupingKey o) {
		if (o == null) {
			throw new NullPointerException("Can't compare to null.");
		}

		if (this.equals(o)) {
			return 0;
		}
		int captionComparison = this.getCaption().compareTo(((ContinentReferenceDto) o).getCaption());
		if (captionComparison != 0) {
			return captionComparison;
		} else {
			return this.getUuid().compareTo(((ContinentReferenceDto) o).getUuid());
		}
	}
}
