package de.symeda.sormas.api.user;

import de.symeda.sormas.api.ReferenceDto;
import de.symeda.sormas.api.statistics.StatisticsGroupingKey;

public class UserRoleReferenceDto extends ReferenceDto implements StatisticsGroupingKey {

	public UserRoleReferenceDto(String uuid, String caption) {
		super(uuid, caption);
	}

	@Override
	public int keyCompareTo(StatisticsGroupingKey o) {

		if (o == null) {
			throw new NullPointerException("Can't compare to null.");
		}

		if (this.equals(o)) {
			return 0;
		}

		int captionComparison = this.getCaption().compareTo(((UserRoleReferenceDto) o).getCaption());
		if (captionComparison != 0) {
			return captionComparison;
		} else {
			return this.getUuid().compareTo(((UserRoleReferenceDto) o).getUuid());
		}
	}
}
