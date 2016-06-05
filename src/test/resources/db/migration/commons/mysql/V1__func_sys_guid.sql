drop function if exists sys_guid;

delimiter //

CREATE function sys_guid ()
returns char(32) NO SQL
BEGIN
  RETURN upper(REPLACE(uuid(),'-', ''));
END
//

delimiter ;
