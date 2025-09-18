-- Comprehensive SQL Syntax and Types Sample
-- This file demonstrates various SQL constructs for syntax highlighting

-- Data Definition Language (DDL)
CREATE SCHEMA IF NOT EXISTS sample_schema;
USE sample_schema;

-- Table creation with various data types
CREATE TABLE employees (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    employee_code VARCHAR(20) UNIQUE NOT NULL,
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    email VARCHAR(255) UNIQUE,
    phone_number CHAR(15),
    hire_date DATE DEFAULT CURRENT_DATE,
    birth_date DATE,
    salary DECIMAL(10,2) CHECK (salary > 0),
    bonus FLOAT,
    is_active BOOLEAN DEFAULT TRUE,
    profile_data JSON,
    resume_text TEXT,
    profile_image BLOB,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME ON UPDATE CURRENT_TIMESTAMP,
    department_id INT,
    manager_id BIGINT,
    FOREIGN KEY (department_id) REFERENCES departments(id),
    FOREIGN KEY (manager_id) REFERENCES employees(id),
    INDEX idx_email (email),
    INDEX idx_name (last_name, first_name),
    FULLTEXT INDEX ft_resume (resume_text)
);

CREATE TABLE departments (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL UNIQUE,
    budget DECIMAL(15,2),
    location VARCHAR(200),
    created_date DATE DEFAULT CURRENT_DATE
);

CREATE TABLE projects (
    id BIGINT PRIMARY KEY,
    name VARCHAR(200) NOT NULL,
    description LONGTEXT,
    start_date DATE,
    end_date DATE,
    status ENUM('planning', 'active', 'completed', 'cancelled') DEFAULT 'planning',
    priority TINYINT DEFAULT 1,
    estimated_hours SMALLINT,
    actual_hours MEDIUMINT DEFAULT 0
);

-- Junction table for many-to-many relationship
CREATE TABLE employee_projects (
    employee_id BIGINT,
    project_id BIGINT,
    role VARCHAR(50),
    allocation_percentage DECIMAL(5,2) DEFAULT 100.00,
    start_date DATE,
    end_date DATE,
    PRIMARY KEY (employee_id, project_id),
    FOREIGN KEY (employee_id) REFERENCES employees(id) ON DELETE CASCADE,
    FOREIGN KEY (project_id) REFERENCES projects(id) ON DELETE CASCADE
);

-- View creation
CREATE VIEW active_employees AS
SELECT 
    e.id,
    CONCAT(e.first_name, ' ', e.last_name) AS full_name,
    e.email,
    d.name AS department_name,
    e.salary,
    CASE 
        WHEN e.salary > 100000 THEN 'Senior'
        WHEN e.salary > 60000 THEN 'Mid-level'
        ELSE 'Junior'
    END AS level,
    TIMESTAMPDIFF(YEAR, e.hire_date, CURRENT_DATE) AS years_employed
FROM employees e
LEFT JOIN departments d ON e.department_id = d.id
WHERE e.is_active = TRUE;

-- Stored procedure
DELIMITER //
CREATE PROCEDURE GetEmployeesByDepartment(
    IN dept_name VARCHAR(100),
    OUT total_count INT
)
BEGIN
    DECLARE done INT DEFAULT FALSE;
    DECLARE emp_name VARCHAR(151);
    DECLARE cur CURSOR FOR 
        SELECT CONCAT(first_name, ' ', last_name)
        FROM employees e
        JOIN departments d ON e.department_id = d.id
        WHERE d.name = dept_name AND e.is_active = TRUE;
    DECLARE CONTINUE HANDLER FOR NOT FOUND SET done = TRUE;
    
    SELECT COUNT(*) INTO total_count
    FROM employees e
    JOIN departments d ON e.department_id = d.id
    WHERE d.name = dept_name AND e.is_active = TRUE;
    
    OPEN cur;
    read_loop: LOOP
        FETCH cur INTO emp_name;
        IF done THEN
            LEAVE read_loop;
        END IF;
        SELECT emp_name;
    END LOOP;
    CLOSE cur;
END //
DELIMITER ;

-- Function
DELIMITER //
CREATE FUNCTION CalculateBonus(emp_salary DECIMAL(10,2), performance_rating DECIMAL(3,2))
RETURNS DECIMAL(10,2)
READS SQL DATA
DETERMINISTIC
BEGIN
    DECLARE bonus_amount DECIMAL(10,2) DEFAULT 0;
    
    IF performance_rating >= 4.5 THEN
        SET bonus_amount = emp_salary * 0.15;
    ELSEIF performance_rating >= 4.0 THEN
        SET bonus_amount = emp_salary * 0.10;
    ELSEIF performance_rating >= 3.5 THEN
        SET bonus_amount = emp_salary * 0.05;
    END IF;
    
    RETURN bonus_amount;
END //
DELIMITER ;

-- Trigger
DELIMITER //
CREATE TRIGGER employee_audit_trigger
    AFTER UPDATE ON employees
    FOR EACH ROW
BEGIN
    IF OLD.salary != NEW.salary THEN
        INSERT INTO audit_log (table_name, record_id, field_name, old_value, new_value, change_date)
        VALUES ('employees', NEW.id, 'salary', OLD.salary, NEW.salary, NOW());
    END IF;
END //
DELIMITER ;

-- Data Manipulation Language (DML)
-- INSERT statements with various syntaxes
INSERT INTO departments (name, budget, location) VALUES
('Engineering', 2500000.00, 'San Francisco'),
('Marketing', 800000.00, 'New York'),
('Sales', 1200000.00, 'Chicago'),
('HR', 400000.00, 'Austin');

INSERT INTO employees 
SET first_name = 'John',
    last_name = 'Doe',
    employee_code = 'EMP001',
    email = 'john.doe@company.com',
    salary = 95000.00,
    department_id = (SELECT id FROM departments WHERE name = 'Engineering'),
    hire_date = '2020-01-15';

-- Complex INSERT with subquery
INSERT INTO projects (id, name, description, start_date, status, priority)
SELECT 
    ROW_NUMBER() OVER() + 1000,
    CONCAT('Project ', UPPER(d.name)),
    CONCAT('Strategic initiative for ', d.name, ' department'),
    DATE_ADD(CURRENT_DATE, INTERVAL 30 DAY),
    'planning',
    CASE 
        WHEN d.budget > 1000000 THEN 1
        ELSE 2
    END
FROM departments d
WHERE d.budget IS NOT NULL;

-- UPDATE statements
UPDATE employees 
SET salary = salary * 1.05 
WHERE hire_date < DATE_SUB(CURRENT_DATE, INTERVAL 2 YEAR)
  AND is_active = TRUE;

UPDATE employees e1
JOIN (
    SELECT department_id, AVG(salary) as avg_salary
    FROM employees
    WHERE is_active = TRUE
    GROUP BY department_id
) e2 ON e1.department_id = e2.department_id
SET e1.bonus = 
    CASE 
        WHEN e1.salary > e2.avg_salary * 1.2 THEN e1.salary * 0.10
        WHEN e1.salary > e2.avg_salary THEN e1.salary * 0.05
        ELSE 0
    END;

-- DELETE statements
DELETE FROM employees 
WHERE is_active = FALSE 
  AND updated_at < DATE_SUB(CURRENT_DATE, INTERVAL 1 YEAR);

-- Complex SELECT queries
-- Basic SELECT with various clauses
SELECT 
    e.employee_code,
    UPPER(e.first_name) AS first_name,
    LOWER(e.last_name) AS last_name,
    e.email,
    d.name AS department,
    e.salary,
    e.hire_date,
    DATEDIFF(CURRENT_DATE, e.hire_date) AS days_employed,
    CASE 
        WHEN e.salary >= 100000 THEN 'Executive'
        WHEN e.salary >= 75000 THEN 'Senior'
        WHEN e.salary >= 50000 THEN 'Mid-Level'
        ELSE 'Entry-Level'
    END AS salary_grade
FROM employees e
INNER JOIN departments d ON e.department_id = d.id
WHERE e.is_active = TRUE
  AND e.hire_date BETWEEN '2020-01-01' AND '2023-12-31'
  AND e.salary > 50000
ORDER BY d.name ASC, e.salary DESC
LIMIT 10 OFFSET 5;

-- Aggregate functions and GROUP BY
SELECT 
    d.name AS department,
    COUNT(*) AS employee_count,
    AVG(e.salary) AS avg_salary,
    MIN(e.salary) AS min_salary,
    MAX(e.salary) AS max_salary,
    SUM(e.salary) AS total_payroll,
    STDDEV(e.salary) AS salary_stddev,
    GROUP_CONCAT(DISTINCT e.first_name ORDER BY e.first_name SEPARATOR ', ') AS employee_names
FROM employees e
JOIN departments d ON e.department_id = d.id
WHERE e.is_active = TRUE
GROUP BY d.id, d.name
HAVING COUNT(*) >= 2 AND AVG(e.salary) > 60000
ORDER BY avg_salary DESC;

-- Window functions
SELECT 
    employee_code,
    first_name,
    last_name,
    salary,
    department_id,
    ROW_NUMBER() OVER (ORDER BY salary DESC) as salary_rank,
    RANK() OVER (PARTITION BY department_id ORDER BY salary DESC) as dept_salary_rank,
    DENSE_RANK() OVER (PARTITION BY department_id ORDER BY hire_date) as seniority_rank,
    LAG(salary, 1, 0) OVER (ORDER BY hire_date) as prev_hire_salary,
    LEAD(salary, 1) OVER (ORDER BY hire_date) as next_hire_salary,
    FIRST_VALUE(salary) OVER (PARTITION BY department_id ORDER BY salary DESC) as highest_dept_salary,
    LAST_VALUE(salary) OVER (PARTITION BY department_id ORDER BY salary DESC ROWS BETWEEN UNBOUNDED PRECEDING AND UNBOUNDED FOLLOWING) as lowest_dept_salary,
    NTILE(4) OVER (ORDER BY salary) as salary_quartile,
    PERCENT_RANK() OVER (ORDER BY salary) as salary_percentile,
    salary - AVG(salary) OVER (PARTITION BY department_id) as salary_diff_from_dept_avg
FROM employees
WHERE is_active = TRUE;

-- Complex subqueries
SELECT e1.*
FROM employees e1
WHERE e1.salary > (
    SELECT AVG(e2.salary)
    FROM employees e2
    WHERE e2.department_id = e1.department_id
      AND e2.is_active = TRUE
)
AND EXISTS (
    SELECT 1
    FROM employee_projects ep
    WHERE ep.employee_id = e1.id
      AND ep.end_date IS NULL
)
AND e1.id NOT IN (
    SELECT DISTINCT manager_id
    FROM employees
    WHERE manager_id IS NOT NULL
);

-- Common Table Expressions (CTE)
WITH RECURSIVE employee_hierarchy AS (
    -- Base case: top-level managers
    SELECT 
        id, 
        first_name, 
        last_name, 
        manager_id, 
        0 as level,
        CAST(CONCAT(last_name, ', ', first_name) AS CHAR(1000)) as path
    FROM employees 
    WHERE manager_id IS NULL
    
    UNION ALL
    
    -- Recursive case: employees with managers
    SELECT 
        e.id,
        e.first_name,
        e.last_name,
        e.manager_id,
        eh.level + 1,
        CAST(CONCAT(eh.path, ' -> ', e.last_name, ', ', e.first_name) AS CHAR(1000))
    FROM employees e
    INNER JOIN employee_hierarchy eh ON e.manager_id = eh.id
    WHERE eh.level < 5  -- Prevent infinite recursion
),
department_stats AS (
    SELECT 
        department_id,
        COUNT(*) as emp_count,
        AVG(salary) as avg_salary,
        MAX(salary) as max_salary
    FROM employees
    WHERE is_active = TRUE
    GROUP BY department_id
)
SELECT 
    eh.level,
    eh.first_name,
    eh.last_name,
    eh.path,
    e.salary,
    ds.avg_salary as dept_avg_salary,
    e.salary - ds.avg_salary as salary_variance
FROM employee_hierarchy eh
JOIN employees e ON eh.id = e.id
JOIN department_stats ds ON e.department_id = ds.department_id
ORDER BY eh.level, eh.last_name;

-- UNION operations
SELECT 'Employee' as type, first_name as name, email, hire_date as date_added
FROM employees
WHERE is_active = TRUE
UNION ALL
SELECT 'Department' as type, name, NULL, created_date
FROM departments
UNION ALL
SELECT 'Project' as type, name, NULL, start_date
FROM projects
WHERE status IN ('active', 'planning')
ORDER BY date_added DESC, name;

-- Complex JOINs
SELECT 
    e.first_name + ' ' + e.last_name AS employee_name,
    d.name AS department,
    p.name AS project_name,
    ep.role AS project_role,
    ep.allocation_percentage,
    COALESCE(m.first_name + ' ' + m.last_name, 'No Manager') AS manager_name
FROM employees e
LEFT JOIN departments d ON e.database_id = d.id
FULL OUTER JOIN employee_projects ep ON e.id = ep.employee_id
LEFT JOIN projects p ON ep.project_id = p.id
LEFT JOIN employees m ON e.manager_id = m.id
WHERE e.is_active = TRUE
   OR ep.end_date IS NULL;

-- Date and time functions
SELECT 
    employee_code,
    hire_date,
    YEAR(hire_date) as hire_year,
    MONTH(hire_date) as hire_month,
    DAY(hire_date) as hire_day,
    DAYNAME(hire_date) as hire_day_name,
    QUARTER(hire_date) as hire_quarter,
    WEEK(hire_date) as hire_week,
    DATE_FORMAT(hire_date, '%Y-%m-%d') as formatted_date,
    TIMESTAMPDIFF(YEAR, hire_date, CURDATE()) as years_employed,
    TIMESTAMPDIFF(MONTH, hire_date, NOW()) as months_employed,
    DATE_ADD(hire_date, INTERVAL 90 DAY) as probation_end,
    LAST_DAY(hire_date) as end_of_hire_month
FROM employees
WHERE hire_date IS NOT NULL;

-- String functions
SELECT 
    employee_code,
    CONCAT(first_name, ' ', COALESCE(middle_name, ''), ' ', last_name) as full_name,
    LENGTH(first_name) as name_length,
    SUBSTRING(first_name, 1, 1) as first_initial,
    UPPER(last_name) as last_name_upper,
    LOWER(email) as email_lower,
    REPLACE(phone_number, '-', '.') as phone_formatted,
    TRIM(BOTH ' ' FROM CONCAT('  ', first_name, '  ')) as trimmed_name,
    LEFT(employee_code, 3) as code_prefix,
    RIGHT(employee_code, 3) as code_suffix,
    LOCATE('@', email) as at_position,
    REVERSE(last_name) as reversed_name
FROM employees;

-- Mathematical functions
SELECT 
    employee_code,
    salary,
    ROUND(salary * 1.05, 2) as projected_salary,
    FLOOR(salary / 12) as monthly_salary_floor,
    CEILING(salary / 12) as monthly_salary_ceiling,
    ABS(salary - 75000) as salary_difference,
    POWER(salary / 1000, 2) as salary_squared,
    SQRT(salary) as salary_sqrt,
    MOD(salary, 1000) as salary_remainder,
    GREATEST(salary, bonus, 50000) as highest_amount,
    LEAST(salary, 200000) as capped_salary
FROM employees
WHERE salary IS NOT NULL;

-- Conditional logic
SELECT 
    employee_code,
    first_name,
    last_name,
    salary,
    CASE 
        WHEN salary IS NULL THEN 'No salary data'
        WHEN salary > 120000 THEN 'Executive Level'
        WHEN salary BETWEEN 80000 AND 120000 THEN 'Senior Level'
        WHEN salary BETWEEN 50000 AND 79999 THEN 'Mid Level'
        ELSE 'Entry Level'
    END as salary_band,
    IF(is_active = TRUE, 'Active', 'Inactive') as status,
    NULLIF(bonus, 0) as non_zero_bonus,
    IFNULL(phone_number, 'No phone') as contact_phone
FROM employees;

-- Set operations and advanced queries
(SELECT department_id, AVG(salary) as avg_salary 
 FROM employees 
 WHERE hire_date >= '2023-01-01' 
 GROUP BY department_id)
INTERSECT
(SELECT department_id, AVG(salary) as avg_salary 
 FROM employees 
 WHERE is_active = TRUE 
 GROUP BY department_id 
 HAVING COUNT(*) > 5);

-- Transaction control
START TRANSACTION;

SAVEPOINT before_updates;

UPDATE employees SET salary = salary * 1.1 WHERE department_id = 1;

-- Rollback to savepoint if needed
-- ROLLBACK TO SAVEPOINT before_updates;

COMMIT;

-- Data Control Language (DCL)
-- Grant permissions
GRANT SELECT, INSERT, UPDATE ON employees TO 'hr_user'@'localhost';
GRANT ALL PRIVILEGES ON sample_schema.* TO 'admin_user'@'%';
REVOKE DELETE ON employees FROM 'hr_user'@'localhost';

-- Index management
CREATE INDEX idx_employees_salary ON employees(salary DESC);
CREATE UNIQUE INDEX idx_employee_code_unique ON employees(employee_code);
CREATE COMPOSITE INDEX idx_emp_dept_salary ON employees(department_id, salary);
DROP INDEX idx_email ON employees;

-- Alter table operations
ALTER TABLE employees 
ADD COLUMN middle_name VARCHAR(50) AFTER first_name,
ADD COLUMN emergency_contact VARCHAR(200),
MODIFY COLUMN phone_number VARCHAR(20),
DROP COLUMN profile_image;

ALTER TABLE departments 
ADD CONSTRAINT chk_budget CHECK (budget >= 0);

-- Drop operations
DROP VIEW IF EXISTS active_employees;
DROP PROCEDURE IF EXISTS GetEmployeesByDepartment;
DROP FUNCTION IF EXISTS CalculateBonus;
DROP TRIGGER IF EXISTS employee_audit_trigger;
DROP TABLE IF EXISTS employee_projects;
DROP TABLE IF EXISTS projects;
DROP TABLE IF EXISTS employees;
DROP TABLE IF EXISTS departments;
DROP SCHEMA IF EXISTS sample_schema;