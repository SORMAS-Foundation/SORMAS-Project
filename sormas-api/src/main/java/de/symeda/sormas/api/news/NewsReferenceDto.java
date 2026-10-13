package de.symeda.sormas.api.news;

import de.symeda.sormas.api.ReferenceDto;

public class NewsReferenceDto extends ReferenceDto {

	public NewsReferenceDto() {

	}

	public NewsReferenceDto(String uuid) {
		super(uuid);
	}

	public NewsReferenceDto(String uuid, String caption) {
		super(uuid, caption);
	}
}
