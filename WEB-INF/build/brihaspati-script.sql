use brihaspati;

insert into TURBINE_GROUP (GROUP_ID,GROUP_NAME) values (2,'author');
insert into TURBINE_GROUP (GROUP_ID,GROUP_NAME) values (3,'institute_admin');
INSERT INTO TURBINE_GROUP (GROUP_ID,GROUP_NAME) VALUES (4,'general');
INSERT INTO TURBINE_GROUP (GROUP_ID,GROUP_NAME) VALUES (5,'instituteWise');


insert into TURBINE_ROLE (ROLE_ID,ROLE_NAME) values (2,'instructor');
insert into TURBINE_ROLE (ROLE_ID,ROLE_NAME) values (3,'student');
insert into TURBINE_ROLE (ROLE_ID,ROLE_NAME) values (4,'group_admin');
insert into TURBINE_ROLE (ROLE_ID,ROLE_NAME) values (5,'author');
insert into TURBINE_ROLE (ROLE_ID,ROLE_NAME) values (6,'user');
insert into TURBINE_ROLE (ROLE_ID,ROLE_NAME) values (7,'institute_admin');
insert into TURBINE_ROLE (ROLE_ID,ROLE_NAME) values (8,'teacher_assistant');
insert into TURBINE_ROLE (ROLE_ID,ROLE_NAME) values (9,'parent');

insert into TURBINE_PERMISSION (PERMISSION_ID,PERMISSION_NAME) values (2,'read');
insert into TURBINE_PERMISSION (PERMISSION_ID,PERMISSION_NAME) values (3,'write');
insert into TURBINE_PERMISSION (PERMISSION_ID,PERMISSION_NAME) values (4,'update_user');
insert into TURBINE_PERMISSION (PERMISSION_ID,PERMISSION_NAME) values (5,'add_user');
insert into TURBINE_PERMISSION (PERMISSION_ID,PERMISSION_NAME) values (6,'delete_user');
insert into TURBINE_PERMISSION (PERMISSION_ID,PERMISSION_NAME) values (7,'add');
insert into TURBINE_PERMISSION (PERMISSION_ID,PERMISSION_NAME) values (8,'update');
insert into TURBINE_PERMISSION (PERMISSION_ID,PERMISSION_NAME) values (9,'delete');
insert into TURBINE_PERMISSION (PERMISSION_ID,PERMISSION_NAME) values (10,'traverse');
insert into TURBINE_PERMISSION (PERMISSION_ID,PERMISSION_NAME) values (11,'backup');
insert into TURBINE_PERMISSION (PERMISSION_ID,PERMISSION_NAME) values (12,'restore');

insert into TURBINE_ROLE_PERMISSION values (4,1);
insert into TURBINE_ROLE_PERMISSION values (2,2);
insert into TURBINE_ROLE_PERMISSION values (2,3);
insert into TURBINE_ROLE_PERMISSION values (2,4);
insert into TURBINE_ROLE_PERMISSION values (2,5);
insert into TURBINE_ROLE_PERMISSION values (2,6);
insert into TURBINE_ROLE_PERMISSION values (2,7);
insert into TURBINE_ROLE_PERMISSION values (2,8);
insert into TURBINE_ROLE_PERMISSION values (2,9);
insert into TURBINE_ROLE_PERMISSION values (2,10);
insert into TURBINE_ROLE_PERMISSION values (2,11);
insert into TURBINE_ROLE_PERMISSION values (2,12);
insert into TURBINE_ROLE_PERMISSION values (6,2);
insert into TURBINE_ROLE_PERMISSION values (3,2);
insert into TURBINE_ROLE_PERMISSION values (3,7);
insert into TURBINE_ROLE_PERMISSION values (3,8);
insert into TURBINE_ROLE_PERMISSION values (3,9);
insert into TURBINE_ROLE_PERMISSION values (6,10);

update TURBINE_USER set LOGIN_NAME='admin',PASSWORD_VALUE= SHA1('admin') where USER_ID=1;
insert into TURBINE_USER(USER_ID,LOGIN_NAME,PASSWORD_VALUE,FIRST_NAME,LAST_NAME) values(0,'guest',SHA1('guest'),'guest','');
insert into PROGRAM (ID, PROGRAM_CODE, PROGRAM_NAME) values(0,'RWP', 'RegistrationWithoutProgram');
INSERT INTO USER_PREF (USER_ID, USER_LANG) SELECT USER_ID, USER_LANG FROM TURBINE_USER;
update USER_PREF set ACTIVATION='ACTIVATE' where ACTIVATION='null';
#---------------------------------------------------------------------------
# Hint questions for forget password module
#---------------------------------------------------------------------------

INSERT INTO HINT_QUESTION VALUES (1,"What is your pet's name?");
INSERT INTO HINT_QUESTION VALUES (2,"What was the name of your first school?");
INSERT INTO HINT_QUESTION VALUES (3,"Who was your child hero?");
INSERT INTO HINT_QUESTION VALUES (4,"What is your favorite past time?");
INSERT INTO HINT_QUESTION VALUES (5,"What is your all time favorite sports team?");
INSERT INTO HINT_QUESTION VALUES (6,"What is your father's middle name?");
INSERT INTO HINT_QUESTION VALUES (7,"What is your high school mascot?");
INSERT INTO HINT_QUESTION VALUES (8,"What was your first bike or car?");
INSERT INTO HINT_QUESTION VALUES (9,"Where did you first meet your girlfriend/boyfriend?");
INSERT INTO HINT_QUESTION VALUES (10,"What is your favorite food?");

#---------------------------------------------------------------------------
# Course Module List for TA Module
#---------------------------------------------------------------------------

insert into COURSE_MODULE (module_id, module_name) VALUES (1, 'Assignment');
insert into COURSE_MODULE (module_id, module_name) VALUES (2, 'Backup');
insert into COURSE_MODULE (module_id, module_name) VALUES (3, 'Communication');
insert into COURSE_MODULE (module_id, module_name) VALUES (4, 'CourseCalendar');
insert into COURSE_MODULE (module_id, module_name) VALUES (5, 'CourseManagement');
insert into COURSE_MODULE (module_id, module_name) VALUES (6, 'GroupManagement');
insert into COURSE_MODULE (module_id, module_name) VALUES (7, 'OnlineExaminationSystem');
insert into COURSE_MODULE (module_id, module_name) VALUES (8, 'InstructorManagement');
insert into COURSE_MODULE (module_id, module_name) VALUES (9, 'StudentManagement');
insert into COURSE_MODULE (module_id, module_name) VALUES (10, 'MarksUpload');
insert into COURSE_MODULE (module_id, module_name) VALUES (11, 'Wiki');
insert into COURSE_MODULE (module_id, module_name) VALUES (12, 'TrackingReport');
INSERT INTO COURSE_MODULE (module_id, module_name) VALUES (13, "AttendanceManagement");

