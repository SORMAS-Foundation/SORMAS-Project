package de.symeda.sormas.api.region;

import de.symeda.sormas.api.InfrastructureDataReferenceDto;
import de.symeda.sormas.api.statistics.StatisticsGroupingKey;

public class SubContinentReferenceDto extends InfrastructureDataReferenceDto implements StatisticsGroupingKey {
    public SubContinentReferenceDto() {
    }

    public SubContinentReferenceDto(String uuid) {
        super(uuid);
    }

    public SubContinentReferenceDto(String uuid, String caption, String externalId) {
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
        int captionComparison = this.getCaption().compareTo(((SubContinentReferenceDto) o).getCaption());
        if (captionComparison != 0) {
            return captionComparison;
        } else {
            return this.getUuid().compareTo(((SubContinentReferenceDto) o).getUuid());
        }
    }


}
