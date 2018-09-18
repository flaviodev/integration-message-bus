package com.github.flaviodev.employee.model.base;

import java.io.Serializable;

import javax.persistence.MappedSuperclass;
import javax.persistence.Version;

import lombok.Getter;

@SuppressWarnings("rawtypes")
@MappedSuperclass
public abstract class EntityBase<I extends Serializable> implements Serializable {

	private static final long serialVersionUID = 7373447920782854276L;

	@Version
	private @Getter Integer version;

	public abstract I getId();

	public abstract void setId(I id);

	public boolean isTransient() {
		return getId() == null;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((getId() == null) ? 0 : getId().hashCode());

		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;

		if (obj == null)
			return false;

		if (getClass() != obj.getClass())
			return false;

		EntityBase other = (EntityBase) obj;
		if (getId() == null) {
			if (other.getId() != null)
				return false;

		} else if (!getId().equals(other.getId())) {
			return false;
		}

		return true;
	}
}