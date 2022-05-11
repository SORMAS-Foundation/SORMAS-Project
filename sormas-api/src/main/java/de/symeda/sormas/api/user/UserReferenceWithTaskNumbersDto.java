package de.symeda.sormas.api.user;

public class UserReferenceWithTaskNumbersDto extends UserReferenceDto {

	private Long numberOfTasks;

	public UserReferenceWithTaskNumbersDto(String uuid, String firstName, String lastName, Long numberOfTasks) {
		super(uuid, firstName, lastName);
		this.numberOfTasks = numberOfTasks == null ? 0 : numberOfTasks;
	}

	public UserReferenceWithTaskNumbersDto(UserReferenceDto userReferenceDto, Long numberOfTasks) {
		super(userReferenceDto.getUuid(), userReferenceDto.getFirstName(), userReferenceDto.getLastName(), userReferenceDto.getShortCaption());
		this.numberOfTasks = numberOfTasks == null ? 0 : numberOfTasks;
	}

	public Long getNumberOfTasks() {
		return numberOfTasks;
	}

	public void setNumberOfTasks(Long numberOfTasks) {
		this.numberOfTasks = numberOfTasks;
	}
}
