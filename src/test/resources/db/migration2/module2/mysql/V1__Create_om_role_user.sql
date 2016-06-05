create table om_role_user (
  id varchar(32) not null,
  user_id varchar(32) not null,
  role_id varchar(32) not null,
  constraint pk_om_role_user_id primary key(id)
);
