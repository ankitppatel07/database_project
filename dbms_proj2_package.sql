/* drops the sequence so that the logs_sequence is preserved for the correct output*/
DROP SEQUENCE logs_sequence;
/*generates values for log# whenever new log records are inserted*/
CREATE sequence logs_sequence INCREMENT BY 1 START WITH 100;
/*Initially dropping all the triggers*/

DROP TRIGGER enroll_stud_trigger;
DROP TRIGGER drop_stud_enrollment_trigger;
DROP TRIGGER delete_stud_trigger;
DROP TRIGGER delete_stud_trigger1;
DROP TRIGGER delete_stud_trigger2;
DROP TRIGGER stud_delete_log_entry_trigger;
DROP TRIGGER enroll_ins_log_trigger;
DROP TRIGGER enroll_del_log_trigger;

/*Creating a package which contains all the procedures required for the project*/
CREATE or REPLACE package dbms_proj2_package AS
     	type ref_cursor IS ref cursor;
    	PROCEDURE show_students(output_message OUT varchar, cursor1 IN OUT ref_cursor);
    	PROCEDURE show_tas(output_message OUT varchar, cursor1 IN OUT ref_cursor);
    	PROCEDURE show_courses(output_message OUT varchar, cursor1 IN OUT ref_cursor);
    	PROCEDURE show_classes(output_message OUT varchar, cursor1 IN OUT ref_cursor);
    	PROCEDURE show_enrollments(output_message OUT varchar, cursor1 IN OUT ref_cursor);
    	PROCEDURE show_prerequisites(output_message OUT varchar, cursor1 IN OUT ref_cursor);
	PROCEDURE show_logs(output_message OUT varchar, cursor1 IN OUT ref_cursor);
	PROCEDURE find_ta(v_cid IN classes.classid%type, output_message OUT varchar, cursor1 IN OUT ref_cursor);
	PROCEDURE return_all_prereq(v_dept_code IN varchar, v_course# IN number, output_message OUT varchar);
	PROCEDURE enroll_stud(v_B# IN VARCHAR, v_class_id IN VARCHAR, output_message OUT varchar);
	PROCEDURE drop_stud_enrollment(v_B# IN VARCHAR, v_class_id IN VARCHAR, output_message OUT varchar);
	PROCEDURE delete_stud(v_B# IN VARCHAR, output_message OUT varchar);
   END;
   /

/*defining the package body*/
CREATE OR replace package body dbms_proj2_package AS

/*procedure to display students table with an OUT parameter for error messages and an IN OUT cursor for retrieving the results*/
PROCEDURE show_students(output_message OUT varchar, cursor1 IN OUT ref_cursor)
IS
BEGIN
/*open cursor for fetching the rows into it*/
  OPEN cursor1 FOR
  SELECT B#, first_name, last_name, status, gpa, email, bdate, deptname FROM students;

EXCEPTION/*upon occurrence of an error this block will be executed*/
WHEN no_data_found THEN 
	output_message := 'No data in students table';
	RAISE;
END;
  
/*procedure to display tas table with an OUT parameter for error messages and an IN OUT cursor for retrieving the results*/
PROCEDURE show_tas(output_message OUT varchar, cursor1 IN OUT ref_cursor)
IS
BEGIN
/*open cursor for fetching the rows into it*/
  OPEN cursor1 FOR
  SELECT B#, ta_level, office FROM tas;

EXCEPTION/*upon occurrence of an error this block will be executed*/
WHEN no_data_found THEN 
	output_message := 'No data in tas table';
	RAISE;
END;

/*procedure to display courses table with an OUT parameter for error messages and an IN OUT cursor for retrieving the results*/
PROCEDURE show_courses(output_message OUT varchar, cursor1 IN OUT ref_cursor)
IS
BEGIN
/*open cursor for fetching the rows into it*/
  OPEN cursor1 FOR
  SELECT dept_code, course#, title FROM courses;

EXCEPTION/*upon occurrence of an error this block will be executed*/
WHEN no_data_found THEN 
	output_message := 'No data in courses table';
	RAISE;
END;

/*procedure to display classes table with an OUT parameter for error messages and an IN OUT cursor for retrieving the results*/
PROCEDURE show_classes(output_message OUT varchar, cursor1 IN OUT ref_cursor)
IS
BEGIN
/*open cursor for fetching the rows into it*/
  OPEN cursor1 FOR
  SELECT classid, dept_code, course#, sect#, year, semester, limit, class_size, room, ta_B# FROM classes;

EXCEPTION/*upon occurrence of an error this block will be executed*/
WHEN no_data_found THEN 
	output_message := 'No data in classes table';
	RAISE;
END;


/*procedure to display enrollments table with an OUT parameter for error messages and an IN OUT cursor for retrieving the results*/
PROCEDURE show_enrollments(output_message OUT varchar, cursor1 IN OUT ref_cursor)
IS
BEGIN
/*open cursor for fetching the rows into it*/
  OPEN cursor1 FOR
  SELECT B#, classid, lgrade FROM enrollments;

EXCEPTION/*upon occurrence of an error this block will be executed*/
WHEN no_data_found THEN 
	output_message := 'No data in enrollments table';
	RAISE;
END;

/*procedure to display prerequisites table with an OUT parameter for error messages and an IN OUT cursor for retrieving the results*/
PROCEDURE show_prerequisites(output_message OUT varchar, cursor1 IN OUT ref_cursor)
IS
BEGIN
/*open cursor for fetching the rows into it*/
  OPEN cursor1 FOR
  SELECT dept_code, course#, pre_dept_code, pre_course# FROM prerequisites;

EXCEPTION/*upon occurrence of an error this block will be executed*/
WHEN no_data_found THEN 
	output_message := 'No data in prerequisites table';
	RAISE;
END;


/*procedure to display logs table with an OUT parameter for error messages and an IN OUT cursor for retrieving the results*/
PROCEDURE show_logs(output_message OUT varchar, cursor1 IN OUT ref_cursor)
IS
BEGIN
/*open cursor for fetching the rows into it*/
  OPEN cursor1 FOR
  SELECT log#, op_name, op_time, table_name, operation, key_value FROM logs;

EXCEPTION/*upon occurrence of an error this block will be executed*/
WHEN no_data_found THEN 
	output_message := 'No data in logs table';
	RAISE;
END;


/*procedure to find TA of a class with an IN parameter for class id, OUT parameter for error messages and an IN OUT cursor for retrieving the results*/
PROCEDURE find_ta(v_cid IN classes.classid%type, output_message OUT varchar, cursor1 IN OUT ref_cursor)
IS/*declaring variables*/
v_ta_B# classes.ta_B#%type;
v_first_name students.first_name%type;
v_last_name students.last_name%type;
x_cid classes.classid%type;
count_ta number;

BEGIN
/*selecting the class with the entered class id*/
SELECT count(*) INTO x_cid FROM classes WHERE classid = v_cid;
if (x_cid = 0) THEN
	output_message := 'The classid is invalid';
	return;
END if;

/*checking TA B# with students table for the given class id*/
SELECT count(*) INTO count_ta FROM students s, classes c
	WHERE c.classid = v_cid AND s.B# = c.ta_B# ;
if(count_ta = 0) THEN	
	output_message := 'The class has no TA';
ELSE
/*selecting B# first name and last name of the TA for the given class id*/
/*open cursor for fetching the rows into it*/
	OPEN cursor1 FOR
	SELECT c.ta_B#, s.first_name, s.last_name INTO v_ta_B#, v_first_name, v_last_name FROM students s, classes c
	WHERE c.classid = v_cid AND s.B# = c.ta_B# ;
END if;

EXCEPTION/*upon occurrence of an error this block will be executed*/
WHEN no_data_found THEN 
	output_message := 'The class has no TA';
	RAISE;
END;


/*procedure to return the prerequisites of a course with 2 IN parameters for dept code and course#, OUT parameter for error messages and prints */
PROCEDURE return_all_prereq(v_dept_code IN varchar, v_course# IN number, output_message OUT varchar)
IS
prereq_rows1 number;
pdc1 prerequisites.pre_dept_code%type;
pc1 prerequisites.pre_course#%type;


BEGIN
/*select rows for the given input that are the prerequisites for them*/
SELECT count(*) INTO prereq_rows1 FROM prerequisites WHERE dept_code = v_dept_code AND course# = v_course#;
/*condition if the course has no prerequisites*/
IF (prereq_rows1 = 0)THEN
	output_message := 'Prerequisites are ' || v_dept_code || v_course# || ' does not exist';

ELSE
FOR x IN (SELECT * FROM prerequisites WHERE dept_code = v_dept_code AND course# = v_course#)
loop
		
	FOR y IN (SELECT * FROM prerequisites WHERE dept_code = x.pre_dept_code AND course# =  x.pre_course#)
	loop
	output_message := x.pre_dept_code || x.pre_course# || ' and ' || y.pre_dept_code || y.pre_course#;
	END LOOP;
END LOOP;

END IF;

EXCEPTION
WHEN no_data_found THEN 
	output_message := 'No Data Found';
	RAISE;
END;



/*procedure to enroll a student with 2 IN parameters for B# and class id, OUT parameter for error messages and prints*/
PROCEDURE enroll_stud(v_B# IN VARCHAR, v_class_id IN VARCHAR, output_message OUT varchar)
IS/*variable declaration*/
class_in_fall18_check number;
valid_B# number;
valid_classid number;
v_limit classes.limit%type;
v_class_size classes.class_size%type;
check_stud_reg number;
token boolean;
enrolled_courses number;
prereq_grade_check enrollments.lgrade%type;

BEGIN
token := true;

/*select student from students table by verifying the B#*/
SELECT count(B#) INTO valid_B# FROM Students
WHERE B# = v_B#;
IF valid_B# = 0 THEN
	output_message := 'The B# is invalid';
	token := false;
	return;
END IF;

/*check if the class with the given class id exist*/
SELECT count(classid) INTO valid_classid FROM Classes 
WHERE classid = v_class_id;
IF valid_classid = 0 THEN
	output_message := 'The classid is invalid';
	token := false;
	return;
END IF;

/*check if the class is offered for FALL semester and 2018 year*/
SELECT count(*) INTO class_in_fall18_check FROM Classes 
WHERE classid = v_class_id AND semester = 'Fall' AND year = 2018;
IF class_in_fall18_check = 0 THEN
	output_message := 'Cannot enroll into a class from a previous semester.';
	token := false;
	return;
END IF;

/*check whether the class is full or not*/
SELECT limit, class_size INTO v_limit, v_class_size FROM Classes
WHERE classid = v_class_id;
IF v_limit = v_class_size THEN
	output_message := 'The classid is already full';
	token := false;
	return;
END IF;

/*check if the student is already registered for the class*/
SELECT count(*) INTO check_stud_reg FROM Enrollments 
WHERE B# = v_B# AND classid = v_class_id;
IF check_stud_reg >= 1 THEN
        output_message := 'The student is already in the class';
	token := false;
	return;
END IF;

/*check whether the student can enroll into a new class*/
/*performing valid grade check for a prerequisite for the class*/
SELECT lgrade INTO prereq_grade_check FROM Enrollments 
WHERE B# = v_B# AND classid IN (SELECT classid FROM Classes
WHERE class_size < limit AND (dept_code,course#) IN (SELECT pre_dept_code, pre_course# FROM Prerequisites 
WHERE (dept_code,course#) IN (SELECT dept_code,course# FROM classes 
WHERE classid = v_class_id)));

SELECT COUNT(e.classid) INTO enrolled_courses FROM Enrollments e, Classes c 
WHERE e.B# = v_B# AND e.classid = c.classid;

IF enrolled_courses = 4 AND prereq_grade_check IN ('A','A-','B+','B','B-','C+','C') THEN      
	output_message := 'The student will be overloaded with the new enrollment.';
	INSERT INTO Enrollments VALUES(v_B#, v_class_id, null);
	token := false;
	return;

ELSIF enrolled_courses = 5 THEN
	output_message := 'Students cannot be enrolled in more than five classes in the same semester.';
	token := false;
	return;

ELSIF prereq_grade_check IN ('A','A-','B+','B','B-','C+','C')  THEN
	token := false;
        INSERT INTO enrollments VALUES (v_B#, v_class_id, null);
	return;
ELSE			
	token := false;
        output_message := 'Prerequisite not satisfied';
	return;

END IF;


IF token = true THEN
	INSERT INTO enrollments VALUES (v_B#, v_class_id, null);
END IF;


END;



/*procedure to drop a student from an enrolled class with 2 IN parameters B# and class id, OUT parameter for error messages and prints*/
PROCEDURE drop_stud_enrollment(v_B# IN VARCHAR, v_class_id IN VARCHAR, output_message OUT varchar)
IS/*variable declaration*/
valid_B# number;
token boolean;
valid_classid number;
valid_enrollment number;
class_in_fall18_check number;
requisite_check number;
check_if_enrolled number;
check_capacity number;

BEGIN
token := true;
/*check if the student exist in students table*/
SELECT count(B#) INTO valid_B# FROM Students
WHERE B# = v_B#;
IF valid_B# = 0 THEN
	output_message := 'The B# is invalid';
	token := false;
	return;
END IF;

/*check for valid classid*/
SELECT count(classid) INTO valid_classid FROM Classes 
WHERE classid = v_class_id;
IF valid_classid = 0 THEN
	output_message := 'The classid is invalid';
	token := false;
	return;
END IF;

/*check whether the student is enrolled in the class*/
SELECT count(*) INTO valid_enrollment FROM Enrollments
WHERE B# = v_B# AND classid = v_class_id;
IF valid_enrollment = 0 THEN
	output_message := 'The student is not enrolled in the class.';
	token := false;
	return;
END IF;

/*check if the student is dropping a FALL 2018 class*/
SELECT count(classid) INTO class_in_fall18_check FROM Classes 
WHERE classid = v_class_id AND semester = 'Fall' AND year = 2018;
IF class_in_fall18_check = 0 THEN
	output_message := 'Only enrollment in the current semester can be dropped.';
	token := false;
	return;
END IF;


/*check if the class to be dropped from has a prerequisite class*/
SELECT count(*) INTO requisite_check FROM Enrollments 
WHERE B# = v_B# AND classid IN 
(SELECT classid FROM classes
WHERE semester = 'Fall' AND year = 2018 AND (dept_code,course#) IN 
(SELECT dept_code, course# FROM prerequisites 
WHERE (pre_dept_code,pre_course#) IN
(SELECT dept_code, course# FROM classes 
WHERE semester = 'Fall' AND year = 2018 AND classid IN
(SELECT classid FROM Enrollments 
WHERE  B# = v_B# AND classid = v_class_id))));

IF requisite_check > 0 THEN
	output_message := 'The drop is not permitted because another class the student registered uses it as a prerequisite.';
	return;
ELSE/*perform the deletion*/
	DELETE FROM enrollments WHERE B# = v_B# AND classid = v_class_id;
	
	SELECT count(*) INTO check_if_enrolled FROM enrollments WHERE B# = v_B#;
	IF check_if_enrolled = 0 THEN
		output_message := 'This student is not enrolled in any classes';
	END IF;
	SELECT count(*) INTO check_capacity FROM enrollments WHERE classid = v_class_id;	
	IF check_capacity = 0 THEN
		output_message := 'The class now has no students';	
		return;
	END IF;
return;
END IF;

END;


/*procedure to delete a student with an IN parameter for B# OUT parameter for error messages and prints*/
PROCEDURE delete_stud(v_B# IN VARCHAR, output_message OUT varchar)
IS
valid_B# number;
token boolean;

BEGIN
token := true;
/*check if the B# is valid*/
SELECT count(B#) INTO valid_B# FROM Students
WHERE B# = v_B#;
IF valid_B# = 0 THEN
	output_message := 'The B# is invalid';
	token := false;
	return;
END IF;
/*if B# is valid then delete the student*/
IF token = true THEN	
	DELETE FROM students WHERE B# = v_B#;
END IF;

END;

END;
/


/*trigger to be fired after insert in an enrollments table*/
CREATE OR REPLACE TRIGGER enroll_stud_trigger 
AFTER
INSERT ON Enrollments 
FOR each ROW

DECLARE
class_id varchar2(50);

BEGIN
class_id := :new.classid;

UPDATE classes SET 
class_size = class_size+1 WHERE
classid = :NEW.classid;

END;
/

/*trigger to be fired after dropping a student from enrollments table*/
CREATE OR REPLACE TRIGGER drop_stud_enrollment_trigger 
AFTER
DELETE ON enrollments
FOR EACH ROW

DECLARE
class_id varchar2(50);

BEGIN
class_id := :new.classid;

UPDATE classes SET
class_size = class_size-1 WHERE
classid = :OLD.classid;

END;
/

/*trigger to be fired after deleting a student from students table*/
CREATE OR REPLACE TRIGGER delete_stud_trigger 
BEFORE
DELETE ON students
FOR EACH ROW

BEGIN

DELETE FROM enrollments WHERE B# = :old.B#;

END;
/

/*trigger to be fired after deleting a student from students table*/
CREATE OR REPLACE TRIGGER delete_stud_trigger1 
BEFORE
DELETE ON students
FOR EACH ROW

BEGIN

DELETE FROM tas WHERE B# = :old.B#;

END;
/


/*trigger to be fired after deleting a tuple from tas table*/
CREATE OR REPLACE TRIGGER delete_stud_trigger2 
BEFORE
DELETE ON tas
FOR EACH ROW

BEGIN

update classes set class_size = class_size - 1
where classid in (select classid from enrollments where B# = :old.B#);

END;
/


/*trigger to be fired after deleting a student from students table and making a valid log entry*/
CREATE OR REPLACE TRIGGER stud_delete_log_entry_trigger 
AFTER
DELETE ON students
FOR EACH ROW
DECLARE
db_user varchar(50);

BEGIN
SELECT user INTO db_user FROM dual;

INSERT INTO logs VALUES(logs_sequence.nextval, db_user, SYSDATE, 'students', 'Delete', :OLD.B#);

END;
/


/*trigger to be fired after inserting in enrollments table and making a valid log entry*/
CREATE OR REPLACE TRIGGER enroll_ins_log_trigger
AFTER
INSERT ON enrollments
FOR EACH ROW
DECLARE
db_user varchar(50);

BEGIN
SELECT user INTO db_user FROM dual;

INSERT INTO logs VALUES(logs_sequence.nextval, db_user, SYSDATE, 'enrollments','Insert', :NEW.B#||','||:NEW.classid);

END;
/

/*trigger to be fired after deleting in enrollments table and making a valid log entry*/
CREATE OR REPLACE TRIGGER enroll_del_log_trigger
AFTER
DELETE ON enrollments
FOR EACH ROW
DECLARE
db_user varchar(50);

BEGIN
SELECT user INTO db_user FROM dual;

INSERT INTO logs VALUES(logs_sequence.nextval, db_user, SYSDATE, 'enrollments','Delete', :OLD.B#||','||:OLD.classid);

END;
/






