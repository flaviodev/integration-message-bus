create table datasourceconfig (
	datasourceid varchar(32) not null
		constraint datasourceconfig_pkey primary key,
	driverclassname varchar(255),
	name varchar(255),
	url varchar(255)
);