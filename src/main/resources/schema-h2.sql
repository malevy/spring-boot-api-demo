DROP TABLE TASK;

CREATE TABLE TASK (
    id NUMBER(10,0) NOT NULL AUTO_INCREMENT,
    title VARCHAR2(255) NOT NULL,
    description VARCHAR2(4096) NULL,
    importance VARCHAR2(10) DEFAULT("normal"),
    due DATE NULL,
    completedOn DATE NULL
);

