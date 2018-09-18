create table employee (
	employeeid varchar(32) not null
		constraint employee_pkey primary key,
	name varchar(100),
	version int
);