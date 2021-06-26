DROP TABLE TASK IF EXISTS;

CREATE TABLE TASK (
    id NUMBER(10,0) NOT NULL AUTO_INCREMENT PRIMARY KEY ,
    title VARCHAR2(255) NOT NULL,
    description VARCHAR2(4096) NULL,
    importance VARCHAR2(10) DEFAULT('normal'),
    due DATE NULL,
    completedOn DATE NULL,
    owner VARCHAR2(30) NOT NULL,
    notneeded VARCHAR2(10) NULL
);

INSERT INTO TASK (title, description, due, owner) VALUES ('feed the dog', 'the dog likes to eat', '2017-12-12', 'joe');
INSERT INTO TASK (title, description, due, owner) VALUES ('rake leaves', '', '2017-12-13', 'joe');
INSERT INTO TASK (title, description, due, owner) VALUES ('water change', 'good for the fish', '2017-12-13', 'joe');
INSERT INTO TASK (title, description, due, owner) VALUES ('buy dog food', 'the dog likes to eat', '2017-12-14', 'joe');


