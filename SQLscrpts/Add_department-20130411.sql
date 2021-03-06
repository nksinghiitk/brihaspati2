use brihaspati;

ALTER TABLE DEPARTMENT ADD INSTITUTE_ID INTEGER AFTER FLOORS_COUNT;
ALTER TABLE DEPARTMENT MODIFY DEPARTMENT_ID INT AUTO_INCREMENT ;
insert into DEPARTMENT (DEPARTMENT_CODE, NAME, NICK_NAME) value (1, 'Aerospace Engineering', 'AE');
insert into DEPARTMENT (DEPARTMENT_CODE, NAME, NICK_NAME) value (2, 'Chemical Engineering', 'ChE');
insert into DEPARTMENT (DEPARTMENT_CODE, NAME, NICK_NAME) value (3, 'Civil Engineering', 'CE');
insert into DEPARTMENT (DEPARTMENT_CODE, NAME, NICK_NAME) value (4, 'Electrical Engineering', 'EE');
insert into DEPARTMENT (DEPARTMENT_CODE, NAME, NICK_NAME) value (5, 'Mechanical Engineering', 'ME');
insert into DEPARTMENT (DEPARTMENT_CODE, NAME, NICK_NAME) value (6,'Materials Science & Engineering','MSE');
insert into DEPARTMENT (DEPARTMENT_CODE, NAME, NICK_NAME) value (7, 'Chemistry', 'Chem');
insert into DEPARTMENT (DEPARTMENT_CODE, NAME, NICK_NAME) value (8, 'Mathematics', 'Math');
insert into DEPARTMENT (DEPARTMENT_CODE, NAME, NICK_NAME) value (9, 'Physics', 'Phy');
insert into DEPARTMENT (DEPARTMENT_CODE, NAME, NICK_NAME) value (10, 'Humanities & Social Sciences', 'HSS');
insert into DEPARTMENT (DEPARTMENT_CODE, NAME, NICK_NAME) value (11, 'Computer Science & Engineering', 'CSE');
insert into DEPARTMENT (DEPARTMENT_CODE, NAME, NICK_NAME) value (12,'Materials Science Programme','MSP');
insert into DEPARTMENT (DEPARTMENT_CODE, NAME, NICK_NAME) value (13, 'Statistics', 'Stat');
insert into DEPARTMENT (DEPARTMENT_CODE, NAME, NICK_NAME) value (14, 'Industrial & Management Engineering', 'IME');
insert into DEPARTMENT (DEPARTMENT_CODE, NAME, NICK_NAME) value (15, 'Nuclear Engineering & Technology', 'NET');
insert into DEPARTMENT (DEPARTMENT_CODE, NAME, NICK_NAME) value (16, 'Laser Technology', 'LT');
insert into DEPARTMENT (DEPARTMENT_CODE, NAME, NICK_NAME) value (17, 'Biological Sciences & Bioengineering', 'BSBE');
insert into DEPARTMENT (DEPARTMENT_CODE, NAME, NICK_NAME) value (18,'Design Programme','DES');
insert into DEPARTMENT (DEPARTMENT_CODE, NAME, NICK_NAME) value (19,'Environmental Engineering & Management','EEM');

