create table om_role (
  id varchar(32) not null,
  name varchar(50) not null,
  description varchar(50),
  constraint pk_om_role_id primary key(id)
);