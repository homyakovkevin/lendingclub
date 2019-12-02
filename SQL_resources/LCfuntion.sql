-- default_by_homeownership
use lendingclub;

drop function if exists default_by_homeownership;
DELIMITER //
CREATE FUNCTION default_by_homeownership(ho_index int) RETURNS float deterministic 
begin
declare searched_id int;
declare total int;
select
count(*) from loan where home_id = ho_index and loan_status = 3 into searched_id;
select count(*) from loan into total;
return searched_id/total;
END; 
//
DELIMITER ;
select default_by_homeownership(1);


-- default by grade
drop function if exists default_by_grade;
DELIMITER //
CREATE FUNCTION default_by_grade(grade_index int) RETURNS float deterministic 
begin
declare searched_id int;
declare total int;
select
count(*) from loan where grade = grade_index and loan_status = 3 into searched_id;
select count(*) from loan into total;
return searched_id/total;
END; 
//
DELIMITER ;
select default_by_grade(0);


-- average size of the loan by grade
drop function if exists average_loan_size_by_grade;
DELIMITER //
CREATE FUNCTION average_loan_size_by_grade(grade_index int) RETURNS float deterministic 
begin
declare average_amount int;

select
avg(loan_amount) from loan where grade = grade_index and loan_status = 3 into average_amount;

return average_amount;
END; 
//
DELIMITER ;
select average_loan_size_by_grade(0);


-- average interest rate of the loan by grade
drop function if exists average_int_rate_by_grade;
DELIMITER //
CREATE FUNCTION average_int_rate_by_grade(grade_index int) RETURNS float deterministic 
begin
declare average_amount int;

select
avg(int_rate) from loan where grade = grade_index and loan_status = 3 into average_amount;

return average_amount;
END; 
//
DELIMITER ;
select average_int_rate_by_grade(0);











