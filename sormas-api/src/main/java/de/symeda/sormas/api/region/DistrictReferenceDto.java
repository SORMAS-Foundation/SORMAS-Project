package de.symeda.sormas.api.region;

import de.symeda.sormas.api.ReferenceDto;
import de.symeda.sormas.api.statistics.StatisticsGroupingKey;

public class DistrictReferenceDto extends ReferenceDto implements StatisticsGroupingKey {

	private static final long serialVersionUID = 8990957700033431836L;

	public DistrictReferenceDto() {
		
	}
	
	public DistrictReferenceDto(String uuid) {
		setUuid(uuid);
	}
	
	public DistrictReferenceDto(String uuid, String caption) {
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
		int captionComparison = this.getCaption().compareTo(((DistrictReferenceDto) o) .getCaption());
		if (captionComparison != 0) {
			return captionComparison;
		} else {
			return this.getUuid().compareTo(((DistrictReferenceDto) o).getUuid());
		}
	}
	
} 
