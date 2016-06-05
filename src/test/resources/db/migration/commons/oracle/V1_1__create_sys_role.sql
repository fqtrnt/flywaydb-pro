CREATE TABLE SYS_ROLE (
	ID VARCHAR2(32) NOT NULL,
	NAME VARCHAR2(255) NOT NULL,
	ROLE VARCHAR2(100) NOT NULL,
	IS_USED NUMBER(1) DEFAULT 1,
	DESCRIPTION VARCHAR2(500),
	CONSTRAINT PK_SYS_ROLE PRIMARY KEY(ID),
	CONSTRAINT UNIQUE_SYS_ROLE_NAME UNIQUE(NAME),
	CONSTRAINT UNIQUE_SYS_ROLE_ROLE UNIQUE(ROLE)
);
CREATE INDEX IDX_SYS_ROLE_IS_USED ON SYS_ROLE(IS_USED);

-- COMMENT ON TABLE SYS_ROLE IS 'ÓÃ»§½ÇÉ«ÐÅÏ¢±í';
-- COMMENT ON COLUMN SYS_ROLE.ID IS '±êÊ¶';
-- COMMENT ON COLUMN SYS_ROLE.NAME IS '½ÇÉ«Ãû³Æ';
-- COMMENT ON COLUMN SYS_ROLE.ROLE IS '½ÇÉ«ÄÚ²¿²Ù×÷Ãû³Æ';
-- COMMENT ON COLUMN SYS_ROLE.IS_USED IS 'ÊÇ·ñÊ¹ÓÃ¡£0£º·ñ£¬1£ºÊÇ';
-- COMMENT ON COLUMN SYS_ROLE.DESCRIPTION IS '±¸×¢';
