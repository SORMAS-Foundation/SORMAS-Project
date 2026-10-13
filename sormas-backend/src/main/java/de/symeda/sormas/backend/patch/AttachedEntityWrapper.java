package de.symeda.sormas.backend.patch;

import java.util.Objects;

import javax.validation.constraints.NotNull;

import de.symeda.sormas.api.EntityDto;

/**
 * It's required to know if the DTO was already "attached" (known in Persistence Context / EntityManager).
 */
public class AttachedEntityWrapper {

	@NotNull
	private EntityDto entityDto;

	/**
	 * True if already persisted: false otherwise to indicate the entity must be merged.
	 */
	private boolean attached = true;

	public static AttachedEntityWrapper attached(EntityDto entityDto) {
		AttachedEntityWrapper attachedEntityWrapper = new AttachedEntityWrapper();
		attachedEntityWrapper.setAttached(true);
		attachedEntityWrapper.setEntityDto(entityDto);
		return attachedEntityWrapper;
	}

	public static AttachedEntityWrapper notYetAttached(EntityDto entityDto) {
		AttachedEntityWrapper attachedEntityWrapper = new AttachedEntityWrapper();
		attachedEntityWrapper.setAttached(false);
		attachedEntityWrapper.setEntityDto(entityDto);
		return attachedEntityWrapper;
	}

	@NotNull
	public EntityDto getEntityDto() {
		return entityDto;
	}

	public AttachedEntityWrapper setEntityDto(EntityDto entityDto) {
		this.entityDto = entityDto;
		return this;
	}

	public boolean isAttached() {
		return attached;
	}

	public AttachedEntityWrapper setAttached(boolean attached) {
		this.attached = attached;
		return this;
	}

	@Override
	public boolean equals(Object o) {
		if (o == null || getClass() != o.getClass())
			return false;
		AttachedEntityWrapper that = (AttachedEntityWrapper) o;
		return attached == that.attached && Objects.equals(entityDto, that.entityDto);
	}

	@Override
	public int hashCode() {
		return Objects.hash(entityDto, attached);
	}
}
