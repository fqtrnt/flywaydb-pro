create table om_user (
  id varchar(32) not null,
  name varchar(50) not null,
  password varchar(32) not null,
  email varchar(50),
  constraint pk_om_user_id primary key(id)
);