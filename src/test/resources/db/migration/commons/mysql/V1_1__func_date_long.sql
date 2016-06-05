drop FUNCTION if exists GET_DATE_LONG;

delimiter //
CREATE FUNCTION GET_DATE_LONG(P_DATE DATETIME, TRUNC_TYPE VARCHAR(20) BINARY)
  RETURNS LONG NO SQL
BEGIN
  DECLARE retDate varchar(30);
  if P_DATE is null then
    return 0;
  end if;
  if TRUNC_TYPE is null then
	RETURN unix_timestamp(P_DATE) * 1000;
  end if;
  set @truncType = upper(TRUNC_TYPE);
  case
  when 'YYYY' = @truncType then set retDate = concat(date_format(P_DATE, '%Y'), '-01-01');
  when 'Q' = @truncType then set retDate = concat(date_format(LAST_DAY(MAKEDATE(EXTRACT(YEAR FROM  P_DATE),1) + interval QUARTER(P_DATE)*3-3 month),'%Y-%m-'),'01');
  when 'MM' = @truncType then set retDate = concat(date_format(LAST_DAY(P_DATE),'%Y-%m-'),'01');
  when 'D' = @truncType then set retDate = date_format(date_sub(P_DATE,INTERVAL WEEKDAY(P_DATE) + 1 DAY), '%Y-%m-%d');
  when 'DD' = @truncType then set retDate = date_format(P_DATE, '%Y-%m-%d');
  end case;
  return unix_timestamp(str_to_date(retDate, '%Y-%m-%d %H:%i:%s')) * 1000;
END;
//

delimiter ;

drop FUNCTION if exists LONG_TO_DATE;

delimiter //
CREATE FUNCTION LONG_TO_DATE(P_DATE LONG)
  RETURNS DATETIME
BEGIN
  if P_DATE is null then
    return null;
  else
	if P_DATE <= 0 then
		return null;
	end if;
  end if;
  RETURN FROM_UNIXTIME(P_DATE / 1000, '%Y-%m-%d %H:%i:%S');
END;
//

delimiter ;