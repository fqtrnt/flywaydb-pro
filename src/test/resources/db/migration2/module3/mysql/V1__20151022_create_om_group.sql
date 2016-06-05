create table om_group (
  id varchar(32) not null,
  name varchar(50) not null,
  description varchar(50),
  constraint pk_om_group_id primary key(id)
);
