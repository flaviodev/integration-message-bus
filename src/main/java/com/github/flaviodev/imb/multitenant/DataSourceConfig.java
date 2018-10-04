package com.github.flaviodev.imb.multitenant;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import org.hibernate.annotations.GenericGenerator;

import com.github.flaviodev.imb.model.base.EntityBase;
import com.github.flaviodev.imb.persistence.UUIDGenerator;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString
public @Getter @Setter class DataSourceConfig extends EntityBase<String> {

	private static final long serialVersionUID = -5145939165710384997L;

	@Id
	@GeneratedValue(generator = UUIDGenerator.NAME)
	@GenericGenerator(name = UUIDGenerator.NAME, strategy = UUIDGenerator.PACKAGE_PATH)
	@Column(name = "datasourceid", length = 32)
	private String id;

	private String name;

	private String driverClassName;

}