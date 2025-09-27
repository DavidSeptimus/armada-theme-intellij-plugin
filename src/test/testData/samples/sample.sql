-- DDL section
create table crm.product (
                             id numeric primary key,
                             title varchar(255) character set utf8
);
-- DML section
insert into product
values (1, 'Product1');

select count(*) from crm.product;
select id as ProductID, title as ProductName
from crm.product where id = :id;

\set content `cat data.txt`
select a from b



                  -- Comprehensive SQLite Syntax Demonstration
-- This example showcases extensive SQL features and syntax

-- Enable extensions and settings
                  PRAGMA foreign_keys = ON;
PRAGMA journal_mode = WAL;
PRAGMA synchronous = NORMAL;

-- SQL Foreign Key Cascade Conditions Examples
-- This file demonstrates all major cascade options with practical examples

-- =============================================================================
-- Sample Base Tables
-- =============================================================================

-- Customers table (parent)
CREATE TABLE customers.ok (
                              id INT PRIMARY KEY AUTO_INCREMENT,
                              name VARCHAR(100) NOT NULL,
                              email VARCHAR(100) UNIQUE NOT NULL
);

-- Categories table (parent)
CREATE TABLE categories (
                            code VARCHAR(10) PRIMARY KEY,
                            name VARCHAR(50) NOT NULL
);

-- Users table (parent)
CREATE TABLE users (
                       id INT PRIMARY KEY AUTO_INCREMENT,
                       username VARCHAR(50) UNIQUE NOT NULL,
                       email VARCHAR(100) NOT NULL
);

-- =============================================================================
-- ON DELETE CASCADE Examples
-- =============================================================================

-- Example 1: Orders are deleted when customer is deleted
CREATE TABLE orders (
                        id INT PRIMARY KEY AUTO_INCREMENT,
                        customer_id INT NOT NULL,
                        order_date DATE DEFAULT CURRENT_DATE,
                        total DECIMAL(10,2),
                        FOREIGN KEY (customer_id) REFERENCES customers(id) ON DELETE CASCADE
);

-- Example 2: Order items are deleted when order is deleted
CREATE TABLE order_items (
                             id INT PRIMARY KEY AUTO_INCREMENT,
                             order_id INT NOT NULL,
                             product_name VARCHAR(100),
                             quantity INT,
                             price DECIMAL(10,2),
                             FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE
);

-- =============================================================================
-- ON DELETE SET NULL Examples
-- =============================================================================

-- Example 1: Employee hierarchy - when manager is deleted, subordinates lose manager reference
CREATE TABLE employees (
                           id INT PRIMARY KEY AUTO_INCREMENT,
                           name VARCHAR(100) NOT NULL,
                           manager_id INT,
                           department VARCHAR(50),
                           FOREIGN KEY (manager_id) REFERENCES employees(id) ON DELETE SET NULL
);

-- Example 2: Posts keep existing when author is deleted, but author reference is removed
CREATE TABLE posts (
                       id INT PRIMARY KEY AUTO_INCREMENT,
                       title VARCHAR(200) NOT NULL,
                       content TEXT,
                       author_id INT,
                       created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                       FOREIGN KEY (author_id) REFERENCES users(id) ON DELETE SET NULL
);

-- =============================================================================
-- ON DELETE RESTRICT Examples
-- =============================================================================

-- Example 1: Cannot delete category if products exist in that category
CREATE TABLE products (
                          id INT PRIMARY KEY AUTO_INCREMENT,
                          name VARCHAR(100) NOT NULL,
                          category_code VARCHAR(10) NOT NULL,
                          price DECIMAL(10,2),
                          FOREIGN KEY (category_code) REFERENCES categories(code) ON DELETE RESTRICT
);

-- Example 2: Cannot delete user if they have active sessions
CREATE TABLE user_sessions (
                               id INT PRIMARY KEY AUTO_INCREMENT,
                               user_id INT NOT NULL,
                               session_token VARCHAR(255) UNIQUE,
                               expires_at TIMESTAMP,
                               FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE RESTRICT
);

-- =============================================================================
-- ON UPDATE CASCADE Examples
-- =============================================================================

-- Example 1: Product category updates automatically when category code changes
CREATE TABLE inventory (
                           id INT PRIMARY KEY AUTO_INCREMENT,
                           product_id INT,
                           category_code VARCHAR(10),
                           stock_quantity INT DEFAULT 0,
                           warehouse_location VARCHAR(50),
                           FOREIGN KEY (category_code) REFERENCES categories(code) ON UPDATE CASCADE
);

-- =============================================================================
-- ON UPDATE SET NULL Examples
-- =============================================================================

-- Example 1: Reference becomes NULL when parent key changes
CREATE TABLE reviews (
                         id INT PRIMARY KEY AUTO_INCREMENT,
                         product_id INT,
                         user_id INT,
                         rating INT CHECK (rating BETWEEN 1 AND 5),
                         comment TEXT,
                         FOREIGN KEY (product_id) REFERENCES products(id) ON UPDATE SET NULL,
                         FOREIGN KEY (user_id) REFERENCES users(id) ON UPDATE SET NULL
);

-- =============================================================================
-- ON DELETE SET DEFAULT Examples
-- =============================================================================

-- Example 1: User assignments default to a "system" user when original user is deleted
CREATE TABLE default_users (
                               id INT PRIMARY KEY AUTO_INCREMENT,
                               username VARCHAR(50) DEFAULT 'system_user'
);

-- Insert default system user
INSERT INTO default_users (id, username) VALUES (0, 'system_user');

CREATE TABLE tickets (
                         id INT PRIMARY KEY AUTO_INCREMENT,
                         title VARCHAR(200) NOT NULL,
                         assigned_user_id INT DEFAULT 0,
                         status VARCHAR(20) DEFAULT 'open',
                         created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                         FOREIGN KEY (assigned_user_id) REFERENCES users(id) ON DELETE SET DEFAULT
);

-- Example 2: Order status defaults to 'pending' when status record is deleted
CREATE TABLE order_statuses (
                                id INT PRIMARY KEY AUTO_INCREMENT,
                                status_name VARCHAR(50) NOT NULL
);

INSERT INTO order_statuses (id, status_name) VALUES
                                                 (1, 'pending'),
                                                 (2, 'processing'),
                                                 (3, 'shipped'),
                                                 (4, 'delivered');

CREATE TABLE customer_orders (
                                 id INT PRIMARY KEY AUTO_INCREMENT,
                                 customer_id INT NOT NULL,
                                 status_id INT DEFAULT 1,
                                 order_date DATE DEFAULT CURRENT_DATE,
                                 FOREIGN KEY (customer_id) REFERENCES customers(id) ON DELETE CASCADE,
                                 FOREIGN KEY (status_id) REFERENCES order_statuses(id) ON DELETE SET DEFAULT
);

-- =============================================================================
-- ON UPDATE SET DEFAULT Examples
-- =============================================================================

-- Example 1: Product prices default to base price when category pricing changes
CREATE TABLE price_categories (
                                  id INT PRIMARY KEY AUTO_INCREMENT,
                                  category_name VARCHAR(50),
                                  price_multiplier DECIMAL(3,2) DEFAULT 1.00
);

INSERT INTO price_categories (id, category_name, price_multiplier) VALUES
                                                                       (1, 'standard', 1.00),
                                                                       (2, 'premium', 1.50),
                                                                       (3, 'luxury', 2.00);

CREATE TABLE product_pricing (
                                 id INT PRIMARY KEY AUTO_INCREMENT,
                                 product_id INT,
                                 price_category_id INT DEFAULT 1,
                                 base_price DECIMAL(10,2),
                                 FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE CASCADE,
                                 FOREIGN KEY (price_category_id) REFERENCES price_categories(id) ON UPDATE SET DEFAULT
);

-- =============================================================================
-- Combined CASCADE Actions (Different for UPDATE and DELETE)
-- =============================================================================

-- Example 1: Comments are deleted with posts, but preserved if post ID changes
CREATE TABLE comments (
                          id INT PRIMARY KEY AUTO_INCREMENT,
                          post_id INT NOT NULL,
                          user_id INT,
                          content TEXT NOT NULL,
                          created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                          FOREIGN KEY (post_id) REFERENCES posts(id)
                              ON DELETE CASCADE
                              ON UPDATE RESTRICT,
                          FOREIGN KEY (user_id) REFERENCES users(id)  ON DELETE CASCADE
);

-- Example 2: Shopping cart items with mixed cascade behavior
CREATE TABLE cart_items (
                            id INT PRIMARY KEY AUTO_INCREMENT,
                            customer_id INT NOT NULL,
                            product_id INT,
                            quantity INT DEFAULT 1,
                            added_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                            FOREIGN KEY (customer_id) REFERENCES customers(id)
                                ON DELETE CASCADE
                                ON UPDATE CASCADE,
                            FOREIGN KEY (product_id) REFERENCES products(id)
                                ON DELETE SET NULL
                                ON UPDATE RESTRICT
);




-- SQL Foreign Key Cascade Conditions Examples
-- This file demonstrates all major cascade options with practical examples

-- =============================================================================
-- Sample Base Tables
-- =============================================================================

-- Customers table (parent)
CREATE TABLE customers (
                           id INT PRIMARY KEY AUTO_INCREMENT,
                           name VARCHAR(100) NOT NULL,
                           email VARCHAR(100) UNIQUE NOT NULL
);

-- Categories table (parent)
CREATE TABLE categories (
                            code VARCHAR(10) PRIMARY KEY,
                            name VARCHAR(50) NOT NULL
);

-- Users table (parent)
CREATE TABLE users (
                       id INT PRIMARY KEY AUTO_INCREMENT,
                       username VARCHAR(50) UNIQUE NOT NULL,
                       email VARCHAR(100) NOT NULL
);

-- =============================================================================
-- ON DELETE CASCADE Examples
-- =============================================================================

-- Example 1: Orders are deleted when customer is deleted
CREATE TABLE orders (
                        id INT PRIMARY KEY AUTO_INCREMENT,
                        customer_id INT NOT NULL,
                        order_date DATE DEFAULT CURRENT_DATE,
                        total DECIMAL(10,2),
                        FOREIGN KEY (customer_id) REFERENCES customers(id) ON DELETE CASCADE
);

-- Example 2: Order items are deleted when order is deleted
CREATE TABLE order_items (
                             id INT PRIMARY KEY AUTO_INCREMENT,
                             order_id INT NOT NULL,
                             product_name VARCHAR(100),
                             quantity INT,
                             price DECIMAL(10,2),
                             FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE
);

-- =============================================================================
-- ON DELETE SET NULL Examples
-- =============================================================================

-- Example 1: Employee hierarchy - when manager is deleted, subordinates lose manager reference
CREATE TABLE employees (
                           id INT PRIMARY KEY AUTO_INCREMENT,
                           name VARCHAR(100) NOT NULL,
                           manager_id INT,
                           department VARCHAR(50),
                           FOREIGN KEY (manager_id) REFERENCES employees(id) ON DELETE SET NULL
);

-- Example 2: Posts keep existing when author is deleted, but author reference is removed
CREATE TABLE posts (
                       id INT PRIMARY KEY AUTO_INCREMENT,
                       title VARCHAR(200) NOT NULL,
                       content TEXT,
                       author_id INT,
                       created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                       FOREIGN KEY (author_id) REFERENCES users(id) ON DELETE SET NULL
);

-- =============================================================================
-- ON DELETE RESTRICT Examples
-- =============================================================================

-- Example 1: Cannot delete category if products exist in that category
CREATE TABLE products (
                          id INT PRIMARY KEY AUTO_INCREMENT,
                          name VARCHAR(100) NOT NULL,
                          category_code VARCHAR(10) NOT NULL,
                          price DECIMAL(10,2),
                          FOREIGN KEY (category_code) REFERENCES categories(code) ON DELETE RESTRICT
);

-- Example 2: Cannot delete user if they have active sessions
CREATE TABLE user_sessions (
                               id INT PRIMARY KEY AUTO_INCREMENT,
                               user_id INT NOT NULL,
                               session_token VARCHAR(255) UNIQUE,
                               expires_at TIMESTAMP,
                               FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE RESTRICT
);

-- =============================================================================
-- ON UPDATE CASCADE Examples
-- =============================================================================

-- Example 1: Product category updates automatically when category code changes
CREATE TABLE inventory (
                           id INT PRIMARY KEY AUTO_INCREMENT,
                           product_id INT,
                           category_code VARCHAR(10),
                           stock_quantity INT DEFAULT 0,
                           warehouse_location VARCHAR(50),
                           FOREIGN KEY (category_code) REFERENCES categories(code) ON UPDATE CASCADE
);

-- =============================================================================
-- ON UPDATE SET NULL Examples
-- =============================================================================

-- Example 1: Reference becomes NULL when parent key changes
CREATE TABLE reviews (
                         id INT PRIMARY KEY AUTO_INCREMENT,
                         product_id INT,
                         user_id INT,
                         rating INT CHECK (rating BETWEEN 1 AND 5),
                         comment TEXT,
                         FOREIGN KEY (product_id) REFERENCES products(id) ON UPDATE SET NULL,
                         FOREIGN KEY (user_id) REFERENCES users(id) ON UPDATE SET NULL
);

-- =============================================================================
-- Combined CASCADE Actions (Different for UPDATE and DELETE)
-- =============================================================================

-- Example 1: Comments are deleted with posts, but preserved if post ID changes
CREATE TABLE comments (
                          id INT PRIMARY KEY AUTO_INCREMENT,
                          post_id INT NOT NULL,
                          user_id INT,
                          content TEXT NOT NULL,
                          created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                          FOREIGN KEY (post_id) REFERENCES posts(id)
                              ON DELETE CASCADE
                              ON UPDATE RESTRICT,
                          FOREIGN KEY (user_id) REFERENCES users(id)
                              ON DELETE SET NULL
                              ON UPDATE CASCADE
);

-- Example 2: Shopping cart items with mixed cascade behavior
CREATE TABLE cart_items (
                            id INT PRIMARY KEY AUTO_INCREMENT,
                            customer_id INT NOT NULL,
                            product_id INT,
                            quantity INT DEFAULT 1,
                            added_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                            FOREIGN KEY (customer_id) REFERENCES customers(id)
                                ON DELETE CASCADE
                                ON UPDATE CASCADE,
                            FOREIGN KEY (product_id) REFERENCES products(id)
                                ON DELETE SET NULL
                                ON UPDATE RESTRICT
);

-- =============================================================================
-- Sample Data for Testing
-- =============================================================================

-- Insert sample customers
INSERT INTO customers (name, email) VALUES
                                        ('John Doe', 'john@example.com'),
                                        ('Jane Smith', 'jane@example.com'),
                                        ('Bob Wilson', 'bob@example.com');

-- Insert sample categories
INSERT INTO categories (code, name) VALUES
                                        ('ELEC', 'Electronics'),
                                        ('BOOK', 'Books'),
                                        ('CLTH', 'Clothing');

-- Insert sample users
INSERT INTO users (username, email) VALUES
                                        ('johndoe', 'john@example.com'),
                                        ('janesmith', 'jane@example.com'),
                                        ('bobwilson', 'bob@example.com');

-- Insert sample orders
INSERT INTO orders (customer_id, total) VALUES
                                            (1, 299.99),
                                            (2, 89.50),
                                            (1, 149.99);

-- =============================================================================
-- Test Queries to Demonstrate Cascade Behavior
-- =============================================================================

-- Test ON DELETE CASCADE: This will delete the customer AND all their orders
-- DELETE FROM customers WHERE id = 1;

-- Test ON DELETE SET NULL: This will set manager_id to NULL for subordinates
-- INSERT INTO employees (name, manager_id) VALUES ('Manager', NULL), ('Employee', 1);
-- DELETE FROM employees WHERE id = 1;

-- Test ON DELETE RESTRICT: This will fail if products exist in the category
-- INSERT INTO products (name, category_code, price) VALUES ('Laptop', 'ELEC', 999.99);
-- DELETE FROM categories WHERE code = 'ELEC'; -- This will fail

-- Test ON UPDATE CASCADE: This will update all related records
-- UPDATE categories SET code = 'ELECTRONICS' WHERE code = 'ELEC';

-- ============================================================================
-- TABLE CREATION WITH ADVANCED FEATURES
-- ============================================================================

-- Create tables with various column types and constraints
CREATE TABLE departments (
                             id INTEGER PRIMARY KEY AUTOINCREMENT,
                             name TEXT NOT NULL UNIQUE CHECK (length(name) > 0),
                             budget REAL DEFAULT 0.0 CHECK (budget >= 0),
                             location TEXT DEFAULT 'Unknown',
                             created_at TEXT DEFAULT (datetime('now', 'localtime')),
                             is_active BOOLEAN DEFAULT TRUE,
                             metadata JSON -- SQLite 3.38+ supports JSON
);

CREATE TABLE employees (
                           id INTEGER PRIMARY KEY,
                           employee_code TEXT GENERATED ALWAYS AS ('EMP-' || printf('%06d', id)) STORED,
                           first_name TEXT NOT NULL COLLATE NOCASE,
                           last_name TEXT NOT NULL COLLATE NOCASE,
                           full_name TEXT GENERATED ALWAYS AS (first_name || ' ' || last_name) VIRTUAL,
                           email TEXT UNIQUE NOT NULL CHECK (email LIKE '%@%.%'),
                           phone TEXT CHECK (length(phone) >= 10),
                           hire_date DATE NOT NULL DEFAULT (date('now')),
                           salary DECIMAL(10,2) CHECK (salary > 0),
                           department_id INTEGER,
                           manager_id INTEGER,
                           status TEXT DEFAULT 'active' CHECK (status IN ('active', 'inactive', 'terminated')),
                           birth_date DATE,
                           notes TEXT,
                           FOREIGN KEY (department_id) REFERENCES departments(id) ON DELETE SET NULL,
                           FOREIGN KEY (manager_id) REFERENCES employees(id) ON DELETE SET NULL
);

CREATE TABLE projects (
                          id INTEGER PRIMARY KEY AUTOINCREMENT,
                          name TEXT NOT NULL,
                          description TEXT,
                          start_date DATE NOT NULL,
                          end_date DATE,
                          budget DECIMAL(12,2) DEFAULT 0,
                          status TEXT DEFAULT 'planning' CHECK (status IN ('planning', 'active', 'completed', 'cancelled')),
                          priority INTEGER DEFAULT 3 CHECK (priority BETWEEN 1 AND 5),
                          department_id INTEGER,
                          created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                          updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                          FOREIGN KEY (department_id) REFERENCES departments(id),
                          CHECK (end_date IS NULL OR end_date >= start_date)
);

CREATE TABLE employee_projects (
                                   id INTEGER PRIMARY KEY AUTOINCREMENT,
                                   employee_id INTEGER NOT NULL,
                                   project_id INTEGER NOT NULL,
                                   role TEXT DEFAULT 'contributor',
                                   allocation_percentage INTEGER DEFAULT 100 CHECK (allocation_percentage BETWEEN 1 AND 100),
                                   start_date DATE DEFAULT (date('now')),
                                   end_date DATE,
                                   hourly_rate DECIMAL(6,2),
                                   FOREIGN KEY (employee_id) REFERENCES employees(id) ON DELETE CASCADE,
                                   FOREIGN KEY (project_id) REFERENCES projects(id) ON DELETE CASCADE,
                                   UNIQUE (employee_id, project_id)
);

CREATE TABLE time_entries (
                              id INTEGER PRIMARY KEY AUTOINCREMENT,
                              employee_id INTEGER NOT NULL,
                              project_id INTEGER NOT NULL,
                              entry_date DATE NOT NULL,
                              hours_worked DECIMAL(4,2) CHECK (hours_worked > 0 AND hours_worked <= 24),
                              description TEXT,
                              billable BOOLEAN DEFAULT TRUE,
                              created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                              FOREIGN KEY (employee_id) REFERENCES employees(id),
                              FOREIGN KEY (project_id) REFERENCES projects(id)
);

-- Temporary table for demonstration
CREATE TEMPORARY TABLE temp_calculations (
                                             id INTEGER PRIMARY KEY,
                                             value REAL,
                                             category TEXT
);

-- ============================================================================
-- INDEXES
-- ============================================================================

CREATE UNIQUE INDEX idx_employees_email ON employees(email);
CREATE INDEX idx_employees_dept ON employees(department_id);
CREATE INDEX idx_employees_manager ON employees(manager_id);
CREATE INDEX idx_employees_hire_date ON employees(hire_date);
CREATE INDEX idx_employees_salary ON employees(salary DESC);
CREATE INDEX idx_employees_name ON employees(last_name, first_name);
CREATE INDEX idx_projects_dates ON projects(start_date, end_date);
CREATE INDEX idx_projects_status ON projects(status) WHERE status != 'completed';
CREATE INDEX idx_time_entries_date ON time_entries(entry_date);
CREATE PARTIAL INDEX idx_active_employees ON employees(id) WHERE status = 'active';

-- ============================================================================
-- VIEWS
-- ============================================================================

-- Simple view
CREATE VIEW active_employees AS
SELECT
    id,
    employee_code,
    full_name,
    email,
    department_id,
    salary,
    hire_date
FROM employees
WHERE status = 'active';

-- Complex view with joins and calculations
CREATE VIEW employee_summary AS
SELECT
    e.id,
    e.full_name,
    e.email,
    d.name AS department_name,
    e.salary,
    m.full_name AS manager_name,
    julianday('now') - julianday(e.hire_date) AS days_employed,
    CASE
        WHEN e.salary > 100000 THEN 'Senior'
        WHEN e.salary > 70000 THEN 'Mid-Level'
        ELSE 'Junior'
        END AS level,
    COUNT(ep.project_id) AS active_projects
FROM employees e
         LEFT JOIN departments d ON e.department_id = d.id
         LEFT JOIN employees m ON e.manager_id = m.id
         LEFT JOIN employee_projects ep ON e.id = ep.employee_id
    AND ep.end_date IS NULL
WHERE e.status = 'active'
GROUP BY e.id, e.full_name, e.email, d.name, e.salary, m.full_name, e.hire_date;

-- Updatable view
CREATE VIEW department_stats AS
SELECT
    d.id,
    d.name,
    d.budget,
    COUNT(e.id) AS employee_count,
    AVG(e.salary) AS avg_salary,
    SUM(e.salary) AS total_salary
FROM departments d
         LEFT JOIN employees e ON d.id = e.department_id AND e.status = 'active'
GROUP BY d.id, d.name, d.budget;

-- ============================================================================
-- TRIGGERS
-- ============================================================================

-- Update timestamp trigger
CREATE TRIGGER update_project_timestamp
    AFTER UPDATE ON projects
    FOR EACH ROW
    WHEN NEW.updated_at = OLD.updated_at
BEGIN
    UPDATE projects
    SET updated_at = CURRENT_TIMESTAMP
    WHERE id = NEW.id;
END;

-- Validation trigger
CREATE TRIGGER validate_employee_salary
    BEFORE INSERT ON employees
    FOR EACH ROW
    WHEN NEW.salary < 30000
BEGIN
    SELECT RAISE(ABORT, 'Salary must be at least $30,000');
END;

-- Audit trigger
CREATE TABLE audit_log (
                           id INTEGER PRIMARY KEY AUTOINCREMENT,
                           table_name TEXT,
                           operation TEXT,
                           old_values TEXT,
                           new_values TEXT,
                           timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TRIGGER employee_audit_trigger
    AFTER UPDATE OF salary ON employees
    FOR EACH ROW
BEGIN
    INSERT INTO audit_log (table_name, operation, old_values, new_values)
    VALUES (
               'employees',
               'UPDATE',
               json_object('id', OLD.id, 'salary', OLD.salary),
               json_object('id', NEW.id, 'salary', NEW.salary)
           );
END;

-- ============================================================================
-- SAMPLE DATA INSERTION
-- ============================================================================

-- Insert departments with JSON metadata
INSERT INTO departments (name, budget, location, metadata) VALUES
                                                               ('Engineering', 1500000.00, 'Building A', json('{"floor": 3, "capacity": 50, "technologies": ["Python", "JavaScript", "Go"]}')),
                                                               ('Marketing', 800000.00, 'Building B', json('{"floor": 2, "capacity": 25, "focus": "digital"}')),
                                                               ('Sales', 1200000.00, 'Building A', json('{"floor": 1, "capacity": 40, "regions": ["North", "South", "West"]}')),
                                                               ('HR', 400000.00, 'Building C', json('{"floor": 1, "capacity": 10, "compliance": true}')),
                                                               ('Finance', 600000.00, 'Building B', json('{"floor": 4, "capacity": 15, "systems": ["SAP", "Oracle"]}'));

-- Insert employees with various data types
INSERT INTO employees (first_name, last_name, email, phone, hire_date, salary, department_id, manager_id, birth_date) VALUES
                                                                                                                          ('Alice', 'Johnson', 'alice.johnson@company.com', '555-0101', '2020-01-15', 120000.00, 1, NULL, '1985-03-20'),
                                                                                                                          ('Bob', 'Smith', 'bob.smith@company.com', '555-0102', '2020-03-01', 95000.00, 1, 1, '1990-07-15'),
                                                                                                                          ('Carol', 'Davis', 'carol.davis@company.com', '555-0103', '2019-06-10', 110000.00, 2, NULL, '1988-11-30'),
                                                                                                                          ('David', 'Wilson', 'david.wilson@company.com', '555-0104', '2021-02-20', 85000.00, 2, 3, '1992-01-05'),
                                                                                                                          ('Eve', 'Brown', 'eve.brown@company.com', '555-0105', '2020-09-15', 130000.00, 3, NULL, '1987-05-22'),
                                                                                                                          ('Frank', 'Miller', 'frank.miller@company.com', '555-0106', '2022-01-10', 75000.00, 3, 5, '1995-09-12'),
                                                                                                                          ('Grace', 'Taylor', 'grace.taylor@company.com', '555-0107', '2019-12-01', 105000.00, 4, NULL, '1989-12-18'),
                                                                                                                          ('Henry', 'Anderson', 'henry.anderson@company.com', '555-0108', '2021-08-15', 90000.00, 5, NULL, '1991-04-03'),
                                                                                                                          ('Ivy', 'Thomas', 'ivy.thomas@company.com', '555-0109', '2020-11-20', 80000.00, 1, 1, '1993-08-27'),
                                                                                                                          ('Jack', 'White', 'jack.white@company.com', '555-0110', '2023-03-01', 65000.00, 2, 3, '1996-06-14');

-- Insert projects
INSERT INTO projects (name, description, start_date, end_date, budget, status, priority, department_id) VALUES
                                                                                                            ('Website Redesign', 'Complete overhaul of company website', '2024-01-01', '2024-06-30', 250000.00, 'active', 1, 1),
                                                                                                            ('CRM Implementation', 'Deploy new customer relationship management system', '2024-02-15', '2024-12-31', 500000.00, 'active', 2, 1),
                                                                                                            ('Brand Campaign', 'Launch new brand awareness campaign', '2024-03-01', '2024-09-30', 300000.00, 'planning', 2, 2),
                                                                                                            ('Sales Training', 'Comprehensive sales team training program', '2024-01-15', '2024-04-30', 75000.00, 'completed', 3, 3),
                                                                                                            ('HR System Upgrade', 'Modernize HR information system', '2024-04-01', NULL, 150000.00, 'planning', 4, 4);

-- Insert project assignments using multiple techniques
INSERT INTO employee_projects (employee_id, project_id, role, allocation_percentage, hourly_rate)
VALUES
    (1, 1, 'Project Lead', 75, 120.00),
    (2, 1, 'Developer', 100, 95.00),
    (9, 1, 'Developer', 50, 80.00);

-- Use SELECT with INSERT
INSERT INTO employee_projects (employee_id, project_id, role, allocation_percentage)
SELECT e.id, 2, 'Contributor', 60
FROM employees e
WHERE e.department_id = 1 AND e.id IN (1, 2);

-- Insert time entries with date functions
INSERT INTO time_entries (employee_id, project_id, entry_date, hours_worked, description, billable) VALUES
                                                                                                        (1, 1, date('now', '-7 days'), 8.0, 'Project planning and architecture review', true),
                                                                                                        (1, 1, date('now', '-6 days'), 7.5, 'Database design and schema creation', true),
                                                                                                        (2, 1, date('now', '-7 days'), 8.0, 'Frontend component development', true),
                                                                                                        (2, 1, date('now', '-6 days'), 6.0, 'API integration work', true),
                                                                                                        (1, 2, date('now', '-5 days'), 4.0, 'CRM system analysis', true),
                                                                                                        (2, 2, date('now', '-4 days'), 8.0, 'Data migration scripting', true);

-- ============================================================================
-- COMPREHENSIVE QUERIES DEMONSTRATING VARIOUS SYNTAX
-- ============================================================================

-- Basic SELECT with multiple clauses
SELECT 'Basic Queries:' AS query_section;

SELECT DISTINCT
    e.id,
    e.full_name,
    e.salary,
    d.name AS department
FROM employees e
         INNER JOIN departments d ON e.department_id = d.id
WHERE e.salary > 80000
  AND e.status = 'active'
ORDER BY e.salary DESC, e.last_name ASC
LIMIT 5 OFFSET 0;

-- Aggregate functions and GROUP BY
SELECT 'Aggregate Functions:' AS query_section;

SELECT
    d.name AS department,
    COUNT(*) AS employee_count,
    AVG(e.salary) AS avg_salary,
    MIN(e.salary) AS min_salary,
    MAX(e.salary) AS max_salary,
    SUM(e.salary) AS total_salary,
    GROUP_CONCAT(e.full_name, '; ') AS employees
FROM departments d
         LEFT JOIN employees e ON d.id = e.department_id
WHERE e.status = 'active'
GROUP BY d.id, d.name
HAVING COUNT(*) > 1
ORDER BY avg_salary DESC;

-- CASE expressions and conditional logic
SELECT 'Case Expressions:' AS query_section;

SELECT
    e.full_name,
    e.salary,
    CASE
        WHEN e.salary >= 120000 THEN 'Executive'
        WHEN e.salary >= 100000 THEN 'Senior'
        WHEN e.salary >= 80000 THEN 'Mid-Level'
        ELSE 'Junior'
        END AS salary_grade,
    CASE
        WHEN date('now') - e.hire_date > 365 * 3 THEN 'Veteran'
        WHEN date('now') - e.hire_date > 365 THEN 'Experienced'
        ELSE 'New'
        END AS tenure_status,
    IIF(e.manager_id IS NULL, 'Manager', 'Individual Contributor') AS role_type
FROM employees e
WHERE e.status = 'active';

-- Subqueries (scalar, correlated, EXISTS)
SELECT 'Subqueries:' AS query_section;

-- Scalar subquery
SELECT
    d.name,
    d.budget,
    (SELECT AVG(salary) FROM employees WHERE department_id = d.id AND status = 'active') AS avg_dept_salary,
    d.budget - (SELECT COALESCE(SUM(salary), 0) FROM employees WHERE department_id = d.id AND status = 'active') AS remaining_budget
FROM departments d;

-- Correlated subquery
SELECT e1.full_name, e1.salary, e1.department_id
FROM employees e1
WHERE e1.salary > (
    SELECT AVG(e2.salary)
    FROM employees e2
    WHERE e2.department_id = e1.department_id AND e2.status = 'active'
)
  AND e1.status = 'active';

-- EXISTS subquery
SELECT d.name
FROM departments d
WHERE EXISTS (
    SELECT 1 FROM employees e
    WHERE e.department_id = d.id
      AND e.salary > 100000
      AND e.status = 'active'
);

-- WINDOW FUNCTIONS
SELECT 'Window Functions:' AS query_section;

SELECT
    e.full_name,
    e.department_id,
    e.salary,
    -- Ranking functions
    ROW_NUMBER() OVER (ORDER BY e.salary DESC) AS salary_rank,
    RANK() OVER (PARTITION BY e.department_id ORDER BY e.salary DESC) AS dept_salary_rank,
    DENSE_RANK() OVER (ORDER BY e.salary DESC) AS dense_salary_rank,
    PERCENT_RANK() OVER (ORDER BY e.salary) AS salary_percentile,
    NTILE(4) OVER (ORDER BY e.salary) AS salary_quartile,

    -- Aggregate window functions
    SUM(e.salary) OVER (PARTITION BY e.department_id) AS dept_total_salary,
    AVG(e.salary) OVER (PARTITION BY e.department_id) AS dept_avg_salary,
    COUNT(*) OVER (PARTITION BY e.department_id) AS dept_employee_count,

    -- Frame-based functions
    SUM(e.salary) OVER (
        ORDER BY e.hire_date
        ROWS BETWEEN UNBOUNDED PRECEDING AND CURRENT ROW
        ) AS running_salary_total,

    -- Lead/Lag functions
    LAG(e.salary, 1) OVER (ORDER BY e.hire_date) AS prev_hire_salary,
    LEAD(e.salary, 1) OVER (ORDER BY e.hire_date) AS next_hire_salary,

    -- First/Last value
    FIRST_VALUE(e.salary) OVER (
        PARTITION BY e.department_id
        ORDER BY e.salary DESC
        ROWS UNBOUNDED PRECEDING
        ) AS highest_dept_salary,

    LAST_VALUE(e.salary) OVER (
        PARTITION BY e.department_id
        ORDER BY e.salary DESC
        ROWS BETWEEN UNBOUNDED PRECEDING AND UNBOUNDED FOLLOWING
        ) AS lowest_dept_salary

FROM employees e
WHERE e.status = 'active'
ORDER BY e.department_id, e.salary DESC;

-- COMMON TABLE EXPRESSIONS (CTEs)
SELECT 'Common Table Expressions:' AS query_section;

-- Simple CTE
WITH high_earners AS (
    SELECT e.*, d.name AS dept_name
    FROM employees e
             JOIN departments d ON e.department_id = d.id
    WHERE e.salary > 100000 AND e.status = 'active'
)
SELECT dept_name, COUNT(*) AS high_earner_count, AVG(salary) AS avg_high_earner_salary
FROM high_earners
GROUP BY dept_name;

-- Recursive CTE - Employee hierarchy
WITH RECURSIVE employee_hierarchy AS (
    -- Base case: top-level managers
    SELECT
        id,
        full_name,
        manager_id,
        0 AS level,
        full_name AS path
    FROM employees
    WHERE manager_id IS NULL AND status = 'active'

    UNION ALL

    -- Recursive case: employees with managers
    SELECT
        e.id,
        e.full_name,
        e.manager_id,
        eh.level + 1,
        eh.path || ' -> ' || e.full_name
    FROM employees e
             JOIN employee_hierarchy eh ON e.manager_id = eh.id
    WHERE e.status = 'active'
)
SELECT
    level,
    full_name,
    path,
    CASE level
        WHEN 0 THEN 'CEO/Director'
        WHEN 1 THEN 'Manager'
        WHEN 2 THEN 'Senior'
        ELSE 'Junior'
        END AS hierarchy_level
FROM employee_hierarchy
ORDER BY level, full_name;

-- Multiple CTEs
WITH
    dept_stats AS (
        SELECT
            department_id,
            COUNT(*) AS emp_count,
            AVG(salary) AS avg_salary,
            SUM(salary) AS total_salary
        FROM employees
        WHERE status = 'active'
        GROUP BY department_id
    ),
    project_stats AS (
        SELECT
            p.department_id,
            COUNT(p.id) AS project_count,
            SUM(p.budget) AS total_budget,
            AVG(p.budget) AS avg_project_budget
        FROM projects p
        GROUP BY p.department_id
    )
SELECT
    d.name AS department,
    COALESCE(ds.emp_count, 0) AS employees,
    COALESCE(ds.avg_salary, 0) AS avg_salary,
    COALESCE(ps.project_count, 0) AS projects,
    COALESCE(ps.total_budget, 0) AS total_project_budget,
    COALESCE(ps.total_budget, 0) / NULLIF(ds.emp_count, 0) AS budget_per_employee
FROM departments d
         LEFT JOIN dept_stats ds ON d.id = ds.department_id
         LEFT JOIN project_stats ps ON d.id = ps.department_id
ORDER BY d.name;

-- JOINS - All types
SELECT 'Join Operations:' AS query_section;

-- CROSS JOIN for combinations
SELECT
    e.full_name AS employee,
    p.name AS project
FROM employees e
         CROSS JOIN projects p
WHERE e.department_id = 1 AND p.status = 'active'
LIMIT 10;

-- Self-join for hierarchical data
SELECT
    emp.full_name AS employee,
    mgr.full_name AS manager,
    emp.salary - mgr.salary AS salary_difference
FROM employees emp
         JOIN employees mgr ON emp.manager_id = mgr.id
WHERE emp.status = 'active' AND mgr.status = 'active';

-- Multiple joins with different types
SELECT
    e.full_name,
    d.name AS department,
    p.name AS project,
    ep.role,
    ep.allocation_percentage,
    COALESCE(te.total_hours, 0) AS hours_logged
FROM employees e
         LEFT JOIN departments d ON e.department_id = d.id
         LEFT JOIN employee_projects ep ON e.id = ep.employee_id
         LEFT JOIN projects p ON ep.project_id = p.id
         LEFT JOIN (
    SELECT
        employee_id,
        project_id,
        SUM(hours_worked) AS total_hours
    FROM time_entries
    GROUP BY employee_id, project_id
) te ON e.id = te.employee_id AND p.id = te.project_id
WHERE e.status = 'active'
ORDER BY e.full_name, p.name;

-- SET OPERATIONS
SELECT 'Set Operations:' AS query_section;

-- UNION
SELECT full_name, 'High Salary' AS category FROM employees WHERE salary > 100000
UNION
SELECT full_name, 'Recent Hire' AS category FROM employees WHERE hire_date > '2022-01-01'
ORDER BY full_name;

-- INTERSECT
SELECT email FROM employees WHERE salary > 90000
INTERSECT
SELECT email FROM employees WHERE department_id IN (1, 2);

-- EXCEPT
SELECT email FROM employees WHERE status = 'active'
EXCEPT
SELECT email FROM employees WHERE manager_id IS NULL;

-- DATE AND TIME FUNCTIONS
SELECT 'Date/Time Functions:' AS query_section;

SELECT
    e.full_name,
    e.hire_date,
    date('now') AS current_date,
    julianday('now') - julianday(e.hire_date) AS days_employed,
    CAST((julianday('now') - julianday(e.hire_date)) / 365.25 AS INTEGER) AS years_employed,
    strftime('%Y-%m', e.hire_date) AS hire_year_month,
    strftime('%w', e.hire_date) AS hire_day_of_week,
    date(e.hire_date, '+1 year') AS first_anniversary,
    datetime('now', 'localtime') AS local_time,
    date('now', 'start of year') AS year_start,
    date('now', 'start of month') AS month_start
FROM employees e
WHERE e.status = 'active'
ORDER BY days_employed DESC;

-- STRING FUNCTIONS
SELECT 'String Functions:' AS query_section;

SELECT
    e.full_name,
    upper(e.first_name) AS upper_first,
    lower(e.last_name) AS lower_last,
    length(e.full_name) AS name_length,
    substr(e.email, 1, instr(e.email, '@') - 1) AS username,
    replace(e.phone, '-', '.') AS phone_dots,
    trim('  ' || e.first_name || '  ') AS trimmed_name,
    printf('Employee: %s (ID: %06d)', e.full_name, e.id) AS formatted_info,
    instr(e.email, '@') AS at_position,
    ltrim(rtrim(e.first_name)) AS cleaned_name
FROM employees e
WHERE e.status = 'active'
LIMIT 5;

-- NUMERIC FUNCTIONS
SELECT 'Numeric Functions:' AS query_section;

SELECT
    e.full_name,
    e.salary,
    round(e.salary / 12, 2) AS monthly_salary,
    ceil(e.salary / 1000) AS salary_thousands_ceil,
    floor(e.salary / 1000) AS salary_thousands_floor,
    abs(e.salary - 100000) AS distance_from_100k,
    e.salary % 1000 AS salary_remainder,
    power(e.salary / 100000, 2) AS salary_power,
    sqrt(e.salary) AS salary_sqrt,
    random() AS random_number,
    hex(e.id) AS id_hex
FROM employees e
WHERE e.status = 'active'
LIMIT 5;

-- JSON FUNCTIONS (if supported)
SELECT 'JSON Functions:' AS query_section;

SELECT
    d.name,
    d.metadata,
    json_extract(d.metadata, '$.floor') AS floor_number,
    json_extract(d.metadata, '$.capacity') AS capacity,
    json_array_length(json_extract(d.metadata, '$.technologies')) AS tech_count
FROM departments d
WHERE d.metadata IS NOT NULL;

-- CONDITIONAL EXPRESSIONS
SELECT 'Conditional Expressions:' AS query_section;

SELECT
    e.full_name,
    e.salary,
    NULLIF(e.manager_id, 0) AS manager_id_clean,
    COALESCE(e.notes, 'No notes available') AS notes_with_default,
    IIF(e.birth_date IS NOT NULL,
        CAST((julianday('now') - julianday(e.birth_date)) / 365.25 AS INTEGER),
        NULL) AS age,
    e.salary *
    CASE e.department_id
        WHEN 1 THEN 1.1  -- Engineering bonus
        WHEN 2 THEN 1.05 -- Marketing bonus
        ELSE 1.0
        END AS adjusted_salary
FROM employees e
WHERE e.status = 'active';

-- ADVANCED ANALYTICAL QUERIES
SELECT 'Advanced Analytics:' AS query_section;

-- Pivot-like operation using CASE
SELECT
    strftime('%Y', e.hire_date) AS hire_year,
    SUM(CASE WHEN d.name = 'Engineering' THEN 1 ELSE 0 END) AS engineering_hires,
    SUM(CASE WHEN d.name = 'Marketing' THEN 1 ELSE 0 END) AS marketing_hires,
    SUM(CASE WHEN d.name = 'Sales' THEN 1 ELSE 0 END) AS sales_hires,
    SUM(CASE WHEN d.name = 'HR' THEN 1 ELSE 0 END) AS hr_hires,
    SUM(CASE WHEN d.name = 'Finance' THEN 1 ELSE 0 END) AS finance_hires,
    COUNT(*) AS total_hires
FROM employees e
         LEFT JOIN departments d ON e.department_id = d.id
WHERE e.hire_date >= '2019-01-01'
GROUP BY strftime('%Y', e.hire_date)
ORDER BY hire_year;

-- Complex analytical query with multiple window functions
WITH employee_metrics AS (
    SELECT
        e.id,
        e.full_name,
        e.salary,
        e.department_id,
        e.hire_date,

        -- Salary percentiles
        PERCENT_RANK() OVER (ORDER BY e.salary) AS salary_percentile_company,
        PERCENT_RANK() OVER (PARTITION BY e.department_id ORDER BY e.salary) AS salary_percentile_dept,

        -- Salary comparisons
        e.salary - AVG(e.salary) OVER () AS salary_vs_company_avg,
        e.salary - AVG(e.salary) OVER (PARTITION BY e.department_id) AS salary_vs_dept_avg,

        -- Tenure analysis
        RANK() OVER (ORDER BY e.hire_date) AS hire_order,
        RANK() OVER (PARTITION BY e.department_id ORDER BY e.hire_date) AS dept_hire_order,

        -- Running totals
        SUM(e.salary) OVER (
            ORDER BY e.hire_date
            ROWS UNBOUNDED PRECEDING
            ) AS cumulative_salary_cost

    FROM employees e
    WHERE e.status = 'active'
)
SELECT
    em.*,
    d.name AS department_name,
    CASE
        WHEN em.salary_percentile_company >= 0.9 THEN 'Top 10%'
        WHEN em.salary_percentile_company >= 0.75 THEN 'Top 25%'
        WHEN em.salary_percentile_company >= 0.5 THEN 'Top 50%'
        ELSE 'Bottom 50%'
        END AS company_salary_tier
FROM employee_metrics em
         LEFT JOIN departments d ON em.department_id = d.id
ORDER BY em.salary_percentile_company DESC;

-- UPSERT operations (INSERT OR REPLACE, INSERT OR IGNORE)
INSERT OR REPLACE INTO temp_calculations (id, value, category) VALUES
                                                                   (1, 100.5, 'Revenue'),
                                                                   (2, 250.75, 'Cost'),
                                                                   (3, 75.25, 'Profit');

INSERT OR IGNORE INTO temp_calculations (id, value, category) VALUES
                                                                  (1, 999.99, 'Should be ignored'),
                                                                  (4, 50.0, 'New value');

-- UPDATE with complex conditions and subqueries
UPDATE employees
SET salary = salary * 1.05
WHERE id IN (
    SELECT e.id
    FROM employees e
             JOIN (
        SELECT department_id, AVG(salary) as dept_avg
        FROM employees
        WHERE status = 'active'
        GROUP BY department_id
    ) dept_avgs ON e.department_id = dept_avgs.department_id
    WHERE e.salary < dept_avgs.dept_avg
      AND e.status = 'active'
      AND julianday('now') - julianday(e.hire_date) > 365
);

-- UPDATE with CASE expression
UPDATE projects
SET status = CASE
                 WHEN end_date < date('now') AND status != 'completed' THEN 'completed'
                 WHEN start_date <= date('now') AND status = 'planning' THEN 'active'
                 ELSE status
    END;

-- DELETE with complex WHERE clause
DELETE FROM time_entries
WHERE id IN (
    SELECT te.id
    FROM time_entries te
             LEFT JOIN employee_projects ep ON te.employee_id = ep.employee_id
        AND te.project_id = ep.project_id
    WHERE ep.id IS NULL  -- Time entries without valid project assignments
);

-- ============================================================================
-- ADVANCED WINDOW FUNCTION EXAMPLES
-- ============================================================================

SELECT 'Advanced Window Functions:' AS query_section;

-- Moving averages and cumulative calculations
SELECT
    te.entry_date,
    te.employee_id,
    te.hours_worked,

    -- Moving averages
    AVG(te.hours_worked) OVER (
        PARTITION BY te.employee_id
        ORDER BY te.entry_date
        ROWS BETWEEN 6 PRECEDING AND CURRENT ROW
        ) AS moving_avg_7_days,

    AVG(te.hours_worked) OVER (
        PARTITION BY te.employee_id
        ORDER BY te.entry_date
        RANGE BETWEEN INTERVAL 7 DAY PRECEDING AND CURRENT ROW
    ) AS range_avg_7_days,

    -- Cumulative sums
    SUM(te.hours_worked) OVER (
        PARTITION BY te.employee_id
        ORDER BY te.entry_date
        ROWS UNBOUNDED PRECEDING
        ) AS cumulative_hours,

    -- First and last values in partition
    FIRST_VALUE(te.hours_worked) OVER (
        PARTITION BY te.employee_id
        ORDER BY te.entry_date
        ROWS UNBOUNDED PRECEDING
        ) AS first_hours_entry,

    LAST_VALUE(te.hours_worked) OVER (
        PARTITION BY te.employee_id
        ORDER BY te.entry_date
        ROWS BETWEEN UNBOUNDED PRECEDING AND UNBOUNDED FOLLOWING
        ) AS last_hours_entry,

    -- NTH_VALUE function
    NTH_VALUE(te.hours_worked, 2) OVER (
        PARTITION BY te.employee_id
        ORDER BY te.entry_date
        ROWS UNBOUNDED PRECEDING
        ) AS second_hours_entry,

    -- Lead and lag with default values
    LAG(te.hours_worked, 1, 0) OVER (
        PARTITION BY te.employee_id
        ORDER BY te.entry_date
        ) AS previous_day_hours,

    LEAD(te.hours_worked, 1, 0) OVER (
        PARTITION BY te.employee_id
        ORDER BY te.entry_date
        ) AS next_day_hours,

    -- Calculate streaks of consecutive days
    te.entry_date - LAG(te.entry_date, 1, te.entry_date) OVER (
        PARTITION BY te.employee_id
        ORDER BY te.entry_date
        ) AS days_since_last_entry

FROM time_entries te
ORDER BY te.employee_id, te.entry_date;

-- ============================================================================
-- RECURSIVE CTE EXAMPLES
-- ============================================================================

SELECT 'Recursive CTE Examples:' AS query_section;

-- Generate date series
WITH RECURSIVE date_series(date_val) AS (
    SELECT date('2024-01-01') AS date_val
    UNION ALL
    SELECT date(date_val, '+1 day')
    FROM date_series
    WHERE date_val < date('2024-01-31')
)
SELECT
    ds.date_val,
    strftime('%w', ds.date_val) AS day_of_week,
    strftime('%W', ds.date_val) AS week_of_year,
    CASE strftime('%w', ds.date_val)
        WHEN '0' THEN 'Sunday'
        WHEN '1' THEN 'Monday'
        WHEN '2' THEN 'Tuesday'
        WHEN '3' THEN 'Wednesday'
        WHEN '4' THEN 'Thursday'
        WHEN '5' THEN 'Friday'
        WHEN '6' THEN 'Saturday'
        END AS day_name,
    COUNT(te.id) AS time_entries_count,
    COALESCE(SUM(te.hours_worked), 0) AS total_hours
FROM date_series ds
         LEFT JOIN time_entries te ON ds.date_val = te.entry_date
GROUP BY ds.date_val
ORDER BY ds.date_val;

-- Fibonacci sequence
WITH RECURSIVE fibonacci(n, fib_n, fib_n_plus_1) AS (
    SELECT 1, 0, 1
    UNION ALL
    SELECT n + 1, fib_n_plus_1, fib_n + fib_n_plus_1
    FROM fibonacci
    WHERE n < 20
)
SELECT n, fib_n AS fibonacci_number
FROM fibonacci;

-- Hierarchical path enumeration
WITH RECURSIVE org_paths AS (
    -- Start with root managers
    SELECT
        id,
        full_name,
        manager_id,
        full_name AS path,
        0 AS depth,
        CAST(id AS TEXT) AS sort_path
    FROM employees
    WHERE manager_id IS NULL AND status = 'active'

    UNION ALL

    -- Add subordinates
    SELECT
        e.id,
        e.full_name,
        e.manager_id,
        op.path || ' > ' || e.full_name,
        op.depth + 1,
        op.sort_path || '.' || printf('%03d', e.id)
    FROM employees e
             JOIN org_paths op ON e.manager_id = op.id
    WHERE e.status = 'active'
)
SELECT
    depth,
    printf('%*s%s', depth * 2, '', full_name) AS indented_name,
    path,
    sort_path
FROM org_paths
ORDER BY sort_path;

-- ============================================================================
-- COMPLEX ANALYTICAL FUNCTIONS
-- ============================================================================

SELECT 'Complex Analytics:' AS query_section;

-- Cohort analysis - employees by hire month
WITH hire_cohorts AS (
    SELECT
        strftime('%Y-%m', hire_date) AS cohort_month,
        COUNT(*) AS cohort_size,
        AVG(salary) AS avg_starting_salary
    FROM employees
    WHERE status = 'active'
    GROUP BY strftime('%Y-%m', hire_date)
),
     current_cohorts AS (
         SELECT
             strftime('%Y-%m', hire_date) AS cohort_month,
             COUNT(*) AS current_size,
             AVG(salary) AS current_avg_salary,
             SUM(CASE WHEN manager_id IS NULL THEN 1 ELSE 0 END) AS promoted_to_manager
         FROM employees
         WHERE status = 'active'
         GROUP BY strftime('%Y-%m', hire_date)
     )
SELECT
    hc.cohort_month,
    hc.cohort_size,
    cc.current_size,
    ROUND(CAST(cc.current_size AS FLOAT) / hc.cohort_size * 100, 1) AS retention_rate,
    hc.avg_starting_salary,
    cc.current_avg_salary,
    ROUND(cc.current_avg_salary - hc.avg_starting_salary, 2) AS salary_growth,
    cc.promoted_to_manager AS promotions
FROM hire_cohorts hc
         JOIN current_cohorts cc ON hc.cohort_month = cc.cohort_month
ORDER BY hc.cohort_month;

-- Employee performance scoring
WITH performance_metrics AS (
    SELECT
        e.id,
        e.full_name,
        e.salary,
        e.department_id,

        -- Tenure score (0-10)
        CASE
            WHEN julianday('now') - julianday(e.hire_date) > 365 * 5 THEN 10
            WHEN julianday('now') - julianday(e.hire_date) > 365 * 3 THEN 8
            WHEN julianday('now') - julianday(e.hire_date) > 365 * 2 THEN 6
            WHEN julianday('now') - julianday(e.hire_date) > 365 THEN 4
            ELSE 2
            END AS tenure_score,

        -- Salary percentile score (0-10)
        ROUND(PERCENT_RANK() OVER (ORDER BY e.salary) * 10) AS salary_score,

        -- Project involvement score
        COALESCE((
                     SELECT COUNT(*) * 2
                     FROM employee_projects ep
                     WHERE ep.employee_id = e.id
                       AND ep.end_date IS NULL
                 ), 0) AS project_score,

        -- Hours worked consistency (based on time entries)
        CASE
            WHEN (
                     SELECT COUNT(DISTINCT te.entry_date)
                     FROM time_entries te
                     WHERE te.employee_id = e.id
                       AND te.entry_date >= date('now', '-30 days')
                 ) >= 20 THEN 10
            WHEN (
                     SELECT COUNT(DISTINCT te.entry_date)
                     FROM time_entries te
                     WHERE te.employee_id = e.id
                       AND te.entry_date >= date('now', '-30 days')
                 ) >= 15 THEN 7
            WHEN (
                     SELECT COUNT(DISTINCT te.entry_date)
                     FROM time_entries te
                     WHERE te.employee_id = e.id
                       AND te.entry_date >= date('now', '-30 days')
                 ) >= 10 THEN 5
            ELSE 2
            END AS consistency_score

    FROM employees e
    WHERE e.status = 'active'
)
SELECT
    pm.*,
    (pm.tenure_score + pm.salary_score + pm.project_score + pm.consistency_score) AS total_score,
    CASE
        WHEN (pm.tenure_score + pm.salary_score + pm.project_score + pm.consistency_score) >= 30 THEN 'Excellent'
        WHEN (pm.tenure_score + pm.salary_score + pm.project_score + pm.consistency_score) >= 25 THEN 'Good'
        WHEN (pm.tenure_score + pm.salary_score + pm.project_score + pm.consistency_score) >= 20 THEN 'Average'
        ELSE 'Needs Improvement'
        END AS performance_rating,

    -- Ranking within department
    RANK() OVER (
        PARTITION BY pm.department_id
        ORDER BY (pm.tenure_score + pm.salary_score + pm.project_score + pm.consistency_score) DESC
        ) AS dept_rank

FROM performance_metrics pm
ORDER BY total_score DESC, pm.full_name;

-- ============================================================================
-- DATA MODIFICATION WITH COMPLEX LOGIC
-- ============================================================================

-- Bulk operations with CTEs
WITH salary_adjustments AS (
    SELECT
        e.id,
        e.salary,
        CASE
            WHEN e.department_id = 1 AND e.salary < 100000 THEN e.salary * 1.10  -- Engineering
            WHEN e.department_id = 2 AND e.salary < 80000 THEN e.salary * 1.08   -- Marketing
            WHEN e.department_id = 3 AND e.salary < 90000 THEN e.salary * 1.07   -- Sales
            WHEN julianday('now') - julianday(e.hire_date) > 365 * 2 THEN e.salary * 1.03
            ELSE e.salary
            END AS new_salary
    FROM employees e
    WHERE e.status = 'active'
)
UPDATE employees
SET
    salary = (SELECT new_salary FROM salary_adjustments WHERE id = employees.id),
    updated_at = CURRENT_TIMESTAMP
WHERE id IN (SELECT id FROM salary_adjustments WHERE new_salary != salary);

-- Conditional INSERT with complex logic
INSERT INTO time_entries (employee_id, project_id, entry_date, hours_worked, description, billable)
SELECT
    e.id,
    p.id,
    date('now', '-' || (abs(random()) % 7) || ' days') AS entry_date,
    (abs(random()) % 8) + 1 AS hours_worked,
    'Auto-generated time entry for project ' || p.name,
    CASE WHEN p.status = 'active' THEN 1 ELSE 0 END
FROM employees e
         CROSS JOIN projects p
WHERE e.status = 'active'
  AND p.status IN ('active', 'planning')
  AND EXISTS (
    SELECT 1 FROM employee_projects ep
    WHERE ep.employee_id = e.id AND ep.project_id = p.id
)
  AND NOT EXISTS (
    SELECT 1 FROM time_entries te
    WHERE te.employee_id = e.id
      AND te.project_id = p.id
      AND te.entry_date = date('now', '-' || (abs(random()) % 7) || ' days')
)
LIMIT 10;  -- Limit to prevent too many inserts

-- ============================================================================
-- UTILITY AND SYSTEM QUERIES
-- ============================================================================

SELECT 'System Information:' AS query_section;

-- Database schema information
SELECT
    name AS table_name,
    type,
    sql AS create_statement
FROM sqlite_master
WHERE type IN ('table', 'view', 'index', 'trigger')
ORDER BY type, name;

-- Table statistics
SELECT
    'Table Statistics:' AS info_type,
    'employees' AS table_name,
    COUNT(*) AS row_count,
    COUNT(DISTINCT department_id) AS unique_departments,
    MIN(salary) AS min_salary,
    MAX(salary) AS max_salary,
    AVG(salary) AS avg_salary
FROM employees
UNION ALL
SELECT
    'Table Statistics:',
    'projects',
    COUNT(*),
    COUNT(DISTINCT status),
    MIN(budget),
    MAX(budget),
    AVG(budget)
FROM projects
UNION ALL
SELECT
    'Table Statistics:',
    'time_entries',
    COUNT(*),
    COUNT(DISTINCT employee_id),
    MIN(hours_worked),
    MAX(hours_worked),
    AVG(hours_worked)
FROM time_entries;

-- Index usage and optimization suggestions
EXPLAIN QUERY PLAN
SELECT e.full_name, d.name, p.name
FROM employees e
         JOIN departments d ON e.department_id = d.id
         JOIN employee_projects ep ON e.id = ep.employee_id
         JOIN projects p ON ep.project_id = p.id
WHERE e.salary > 80000 AND p.status = 'active';

-- ============================================================================
-- CLEANUP AND FINAL OPERATIONS
-- ============================================================================

-- Clean up temporary data
DELETE FROM temp_calculations;

-- Final verification queries
SELECT 'Final Verification:' AS query_section;

-- Data integrity check
SELECT
    'Data Integrity' AS check_type,
    CASE
        WHEN COUNT(*) = 0 THEN 'PASS: No orphaned employee projects'
        ELSE 'FAIL: Found ' || COUNT(*) || ' orphaned employee projects'
        END AS result
FROM employee_projects ep
         LEFT JOIN employees e ON ep.employee_id = e.id
         LEFT JOIN projects p ON ep.project_id = p.id
WHERE e.id IS NULL OR p.id IS NULL
UNION ALL
SELECT
    'Data Integrity',
    CASE
        WHEN COUNT(*) = 0 THEN 'PASS: No employees with invalid departments'
        ELSE 'FAIL: Found ' || COUNT(*) || ' employees with invalid departments'
        END
FROM employees e
         LEFT JOIN departments d ON e.department_id = d.id
WHERE e.department_id IS NOT NULL AND d.id IS NULL
UNION ALL
SELECT
    'Data Integrity',
    CASE
        WHEN COUNT(*) = 0 THEN 'PASS: No circular manager relationships'
        ELSE 'FAIL: Found ' || COUNT(*) || ' circular manager relationships'
        END
FROM employees e1
         JOIN employees e2 ON e1.manager_id = e2.id
WHERE e2.manager_id = e1.id;

-- Performance summary
SELECT
    'Performance Summary' AS summary_type,
    d.name AS department,
    COUNT(e.id) AS employee_count,
    ROUND(AVG(e.salary), 2) AS avg_salary,
    COUNT(p.id) AS project_count,
    ROUND(SUM(p.budget), 2) AS total_budget,
    ROUND(
            COALESCE(SUM(te.hours_worked), 0) /
            NULLIF(COUNT(DISTINCT te.employee_id), 0), 2
    ) AS avg_hours_per_employee
FROM departments d
         LEFT JOIN employees e ON d.id = e.department_id AND e.status = 'active'
         LEFT JOIN projects p ON d.id = p.department_id
         LEFT JOIN time_entries te ON e.id = te.employee_id
GROUP BY d.id, d.name
ORDER BY employee_count DESC;

-- ============================================================================
-- SNOWFLAKE-SPECIFIC FEATURES: CROSS-DATABASE AND CROSS-SCHEMA OPERATIONS
-- ============================================================================
-- Note: The following examples are Snowflake-specific and demonstrate
-- multi-database/schema operations not available in SQLite


-- ============================================================================
-- SNOWFLAKE DATABASE AND SCHEMA SETUP
-- ============================================================================

-- Create multiple databases for cross-database operations
CREATE DATABASE hr_system;
CREATE DATABASE finance_system;
CREATE DATABASE sales_system;
CREATE DATABASE analytics_warehouse;

-- Create schemas within databases
CREATE SCHEMA hr_system.payroll;
CREATE SCHEMA hr_system.benefits;
CREATE SCHEMA hr_system.recruiting;

CREATE SCHEMA finance_system.accounting;
CREATE SCHEMA finance_system.budgeting;
CREATE SCHEMA finance_system.reporting;

CREATE SCHEMA sales_system.crm;
CREATE SCHEMA sales_system.orders;
CREATE SCHEMA sales_system.marketing;

CREATE SCHEMA analytics_warehouse.data_lake;
CREATE SCHEMA analytics_warehouse.marts;
CREATE SCHEMA analytics_warehouse.staging;

-- ============================================================================
-- CROSS-DATABASE TABLE CREATION
-- ============================================================================

-- HR System Tables
USE DATABASE hr_system.x;
USE SCHEMA payroll.x;

CREATE TABLE employees (
                           employee_id NUMBER(10,0) IDENTITY(1,1),
                           employee_code VARCHAR(20),
                           first_name VARCHAR(50) NOT NULL,
                           last_name VARCHAR(50) NOT NULL,
                           email VARCHAR(100) UNIQUE,
                           department_id NUMBER(10,0),
                           hire_date DATE,
                           salary NUMBER(10,2),
                           manager_id NUMBER(10,0),
                           status VARCHAR(20) DEFAULT 'ACTIVE',
                           created_at TIMESTAMP_NTZ DEFAULT CURRENT_TIMESTAMP(),
                           updated_at TIMESTAMP_NTZ DEFAULT CURRENT_TIMESTAMP()
);

CREATE TABLE salary_history (
                                id NUMBER(10,0) IDENTITY(1,1),
                                employee_id NUMBER(10,0),
                                effective_date DATE,
                                old_salary NUMBER(10,2),
                                new_salary NUMBER(10,2),
                                change_reason VARCHAR(100),
                                approved_by NUMBER(10,0)
);


CREATE TABLE benefit_enrollments (
                                     enrollment_id NUMBER(10,0) IDENTITY(1,1),
                                     employee_id NUMBER(10,0),
                                     benefit_type VARCHAR(50),
                                     plan_name VARCHAR(100),
                                     monthly_cost NUMBER(8,2),
                                     employee_contribution NUMBER(8,2),
                                     enrollment_date DATE,
                                     status VARCHAR(20) DEFAULT 'ACTIVE'
);

-- Finance System Tables
USE DATABASE finance_system;
USE SCHEMA accounting;

CREATE TABLE chart_of_accounts (
                                   account_id NUMBER(10,0) IDENTITY(1,1),
                                   account_code VARCHAR(20) UNIQUE,
                                   account_name VARCHAR(100),
                                   account_type VARCHAR(50),
                                   department_id NUMBER(10,0),
                                   is_active BOOLEAN DEFAULT TRUE
);

CREATE TABLE general_ledger (
                                transaction_id NUMBER(15,0) IDENTITY(1,1),
                                account_id NUMBER(10,0),
                                transaction_date DATE,
                                description VARCHAR(200),
                                debit_amount NUMBER(15,2) DEFAULT 0,
                                credit_amount NUMBER(15,2) DEFAULT 0,
                                employee_id NUMBER(10,0), -- Links to HR system
                                reference_number VARCHAR(50),
                                created_at TIMESTAMP_NTZ DEFAULT CURRENT_TIMESTAMP()
);

USE SCHEMA finance_system.budgeting;

CREATE TABLE department_budgets (
                                    budget_id NUMBER(10,0) IDENTITY(1,1),
                                    department_id NUMBER(10,0),
                                    fiscal_year NUMBER(4,0),
                                    budget_category VARCHAR(50),
                                    budgeted_amount NUMBER(12,2),
                                    spent_amount NUMBER(12,2) DEFAULT 0,
                                    remaining_amount NUMBER(12,2),
                                    last_updated TIMESTAMP_NTZ DEFAULT CURRENT_TIMESTAMP()
);

-- Sales System Tables
USE DATABASE sales_system;
USE SCHEMA crm;

CREATE TABLE customers (
                           customer_id NUMBER(10,0) IDENTITY(1,1),
                           company_name VARCHAR(100),
                           contact_first_name VARCHAR(50),
                           contact_last_name VARCHAR(50),
                           email VARCHAR(100),
                           phone VARCHAR(20),
                           industry VARCHAR(50),
                           annual_revenue NUMBER(15,2),
                           employee_count NUMBER(10,0),
                           assigned_sales_rep NUMBER(10,0), -- Links to HR system
                           created_date DATE DEFAULT CURRENT_DATE(),
                           status VARCHAR(20) DEFAULT 'ACTIVE'
);

USE SCHEMA sales_system.orders;

CREATE TABLE sales_orders (
                              order_id NUMBER(15,0) IDENTITY(1,1),
                              customer_id NUMBER(10,0),
                              sales_rep_id NUMBER(10,0), -- Links to HR system
                              order_date DATE,
                              total_amount NUMBER(12,2),
                              commission_rate NUMBER(5,4),
                              commission_amount NUMBER(10,2),
                              order_status VARCHAR(20),
                              payment_terms VARCHAR(50)
);

CREATE TABLE order_line_items (
                                  line_item_id NUMBER(15,0) IDENTITY(1,1),
                                  order_id NUMBER(15,0),
                                  product_code VARCHAR(50),
                                  product_name VARCHAR(100),
                                  quantity NUMBER(10,2),
                                  unit_price NUMBER(10,2),
                                  line_total NUMBER(12,2),
                                  discount_percentage NUMBER(5,2) DEFAULT 0
);

-- Analytics Warehouse Tables
USE DATABASE analytics_warehouse;
USE SCHEMA data_lake;

CREATE TABLE employee_performance_metrics (
                                              metric_id NUMBER(15,0) IDENTITY(1,1),
                                              employee_id NUMBER(10,0),
                                              metric_date DATE,
                                              sales_quota NUMBER(12,2),
                                              sales_actual NUMBER(12,2),
                                              calls_made NUMBER(6,0),
                                              meetings_held NUMBER(6,0),
                                              deals_closed NUMBER(4,0),
                                              customer_satisfaction_score NUMBER(3,1),
                                              created_at TIMESTAMP_NTZ DEFAULT CURRENT_TIMESTAMP()
);

USE SCHEMA analytics_warehouse.marts;

CREATE TABLE employee_360_view (
                                   view_id NUMBER(15,0) IDENTITY(1,1),
                                   employee_id NUMBER(10,0),
                                   full_name VARCHAR(101),
                                   department VARCHAR(50),
                                   current_salary NUMBER(10,2),
                                   total_benefit_cost NUMBER(8,2),
                                   ytd_sales NUMBER(12,2),
                                   ytd_commission NUMBER(10,2),
                                   performance_rating VARCHAR(20),
                                   last_refresh_date TIMESTAMP_NTZ DEFAULT CURRENT_TIMESTAMP()
);

-- ============================================================================
-- SAMPLE DATA FOR CROSS-DATABASE OPERATIONS
-- ============================================================================

-- Insert sample data into HR system
USE DATABASE hr_system;
USE SCHEMA payroll;

INSERT INTO employees (employee_code, first_name, last_name, email, department_id, hire_date, salary, manager_id)
VALUES
    ('EMP001', 'John', 'Smith', 'john.smith@company.com', 100, '2020-01-15', 85000, NULL),
    ('EMP002', 'Sarah', 'Johnson', 'sarah.johnson@company.com', 200, '2020-03-01', 92000, 1),
    ('EMP003', 'Mike', 'Davis', 'mike.davis@company.com', 100, '2021-05-10', 78000, 1),
    ('EMP004', 'Lisa', 'Wilson', 'lisa.wilson@company.com', 300, '2019-08-20', 95000, NULL),
    ('EMP005', 'David', 'Brown', 'david.brown@company.com', 300, '2021-11-01', 105000, 4);

-- Insert benefit data
USE SCHEMA hr_system.benefits;

INSERT INTO benefit_enrollments (employee_id, benefit_type, plan_name, monthly_cost, employee_contribution, enrollment_date)
VALUES
    (1, 'Health Insurance', 'Premium PPO', 850.00, 200.00, '2020-01-15'),
    (1, 'Dental Insurance', 'Standard Dental', 75.00, 25.00, '2020-01-15'),
    (2, 'Health Insurance', 'Standard HMO', 650.00, 150.00, '2020-03-01'),
    (3, 'Health Insurance', 'Premium PPO', 850.00, 200.00, '2021-05-10'),
    (4, 'Health Insurance', 'Premium PPO', 850.00, 200.00, '2019-08-20'),
    (5, 'Health Insurance', 'Standard HMO', 650.00, 150.00, '2021-11-01');

-- Insert finance data
USE DATABASE finance_system;
USE SCHEMA accounting;

INSERT INTO chart_of_accounts (account_code, account_name, account_type, department_id)
VALUES
    ('5000', 'Salaries and Wages', 'Expense', 100),
    ('5100', 'Employee Benefits', 'Expense', 100),
    ('6000', 'Sales Commissions', 'Expense', 300),
    ('4000', 'Sales Revenue', 'Revenue', 300),
    ('7000', 'Office Supplies', 'Expense', 200);

-- Insert sales data
USE DATABASE sales_system;
USE SCHEMA crm;

INSERT INTO customers (company_name, contact_first_name, contact_last_name, email, industry, annual_revenue, assigned_sales_rep)
VALUES
    ('Tech Corp', 'Alice', 'Johnson', 'alice@techcorp.com', 'Technology', 5000000, 2),
    ('Manufacturing Inc', 'Bob', 'Miller', 'bob@mfginc.com', 'Manufacturing', 12000000, 4),
    ('Retail Solutions', 'Carol', 'Davis', 'carol@retail.com', 'Retail', 3000000, 5);

USE SCHEMA sales_system.orders;

INSERT INTO sales_orders (customer_id, sales_rep_id, order_date, total_amount, commission_rate, commission_amount, order_status)
VALUES
    (1, 2, '2024-01-15', 125000.00, 0.05, 6250.00, 'COMPLETED'),
    (2, 4, '2024-02-01', 275000.00, 0.06, 16500.00, 'COMPLETED'),
    (3, 5, '2024-02-15', 85000.00, 0.04, 3400.00, 'PENDING'),
    (1, 2, '2024-03-01', 95000.00, 0.05, 4750.00, 'COMPLETED');

-- ============================================================================
-- CROSS-DATABASE AND CROSS-SCHEMA QUERIES
-- ============================================================================

-- Example 1: Simple cross-database join
-- Join employees from HR with their sales performance from Sales system
SELECT
    hr.first_name,
    hr.last_name,
    hr.email,
    hr.salary,
    COUNT(so.order_id) AS total_orders,
    SUM(so.total_amount) AS total_sales,
    SUM(so.commission_amount) AS total_commission
FROM hr_system.payroll.employees hr
         LEFT JOIN sales_system.orders.sales_orders so
                   ON hr.employee_id = so.sales_rep_id
WHERE hr.status = 'ACTIVE'
GROUP BY hr.employee_id, hr.first_name, hr.last_name, hr.email, hr.salary
ORDER BY total_sales DESC NULLS LAST;

-- Example 2: Complex multi-database analysis
-- Employee total compensation including salary, benefits, and commissions
WITH employee_base_comp AS (
    SELECT
        e.employee_id,
        e.first_name || ' ' || e.last_name AS full_name,
        e.salary AS annual_salary,
        e.department_id
    FROM hr_system.payroll.employees e
    WHERE e.status = 'ACTIVE'
),
     employee_benefits AS (
         SELECT
             be.employee_id,
             SUM(be.monthly_cost) AS monthly_benefit_cost,
             SUM(be.monthly_cost) * 12 AS annual_benefit_cost
         FROM hr_system.benefits.benefit_enrollments be
         WHERE be.status = 'ACTIVE'
         GROUP BY be.employee_id
     ),
     employee_commissions AS (
         SELECT
             so.sales_rep_id AS employee_id,
             SUM(so.commission_amount) AS ytd_commission,
             COUNT(so.order_id) AS deals_closed,
             AVG(so.total_amount) AS avg_deal_size
         FROM sales_system.orders.sales_orders so
         WHERE so.order_date >= DATE_TRUNC('YEAR', CURRENT_DATE())
           AND so.order_status = 'COMPLETED'
         GROUP BY so.sales_rep_id
     )
SELECT
    ebc.full_name,
    ebc.annual_salary,
    COALESCE(eb.annual_benefit_cost, 0) AS annual_benefits,
    COALESCE(ec.ytd_commission, 0) AS ytd_commission,
    ebc.annual_salary +
    COALESCE(eb.annual_benefit_cost, 0) +
    COALESCE(ec.ytd_commission, 0) AS total_compensation,
    COALESCE(ec.deals_closed, 0) AS deals_closed,
    COALESCE(ec.avg_deal_size, 0) AS avg_deal_size
FROM employee_base_comp ebc
         LEFT JOIN employee_benefits eb ON ebc.employee_id = eb.employee_id
         LEFT JOIN employee_commissions ec ON ebc.employee_id = ec.employee_id
ORDER BY total_compensation DESC;

-- Example 3: Cross-database financial analysis
-- Department budget vs actual spend (including salaries and benefits)
WITH dept_salary_costs AS (
    SELECT
        e.department_id,
        SUM(e.salary) AS annual_salary_cost,
        COUNT(e.employee_id) AS employee_count
    FROM hr_system.payroll.employees e
    WHERE e.status = 'ACTIVE'
    GROUP BY e.department_id
),
     dept_benefit_costs AS (
         SELECT
             e.department_id,
             SUM(be.monthly_cost * 12) AS annual_benefit_cost
         FROM hr_system.payroll.employees e
                  JOIN hr_system.benefits.benefit_enrollments be ON e.employee_id = be.employee_id
         WHERE e.status = 'ACTIVE' AND be.status = 'ACTIVE'
         GROUP BY e.department_id
     ),
     dept_other_expenses AS (
         SELECT
             coa.department_id,
             SUM(gl.debit_amount - gl.credit_amount) AS other_expenses
         FROM finance_system.accounting.general_ledger gl
                  JOIN finance_system.accounting.chart_of_accounts coa ON gl.account_id = coa.account_id
         WHERE coa.account_type = 'Expense'
           AND coa.account_code NOT IN ('5000', '5100') -- Exclude salary and benefits
           AND gl.transaction_date >= DATE_TRUNC('YEAR', CURRENT_DATE())
         GROUP BY coa.department_id
     )
SELECT
    db.department_id,
    db.fiscal_year,
    db.budgeted_amount,
    COALESCE(dsc.annual_salary_cost, 0) AS salary_costs,
    COALESCE(dbc.annual_benefit_cost, 0) AS benefit_costs,
    COALESCE(doe.other_expenses, 0) AS other_expenses,
    COALESCE(dsc.annual_salary_cost, 0) +
    COALESCE(dbc.annual_benefit_cost, 0) +
    COALESCE(doe.other_expenses, 0) AS total_actual_spend,
    db.budgeted_amount - (
        COALESCE(dsc.annual_salary_cost, 0) +
        COALESCE(dbc.annual_benefit_cost, 0) +
        COALESCE(doe.other_expenses, 0)
        ) AS budget_variance,
    ROUND(
            ((COALESCE(dsc.annual_salary_cost, 0) +
              COALESCE(dbc.annual_benefit_cost, 0) +
              COALESCE(doe.other_expenses, 0)) /
             NULLIF(db.budgeted_amount, 0)) * 100, 2
    ) AS budget_utilization_pct
FROM finance_system.budgeting.department_budgets db
         LEFT JOIN dept_salary_costs dsc ON db.department_id = dsc.department_id
         LEFT JOIN dept_benefit_costs dbc ON db.department_id = dbc.department_id
         LEFT JOIN dept_other_expenses doe ON db.department_id = doe.department_id
WHERE db.fiscal_year = YEAR(CURRENT_DATE())
ORDER BY budget_utilization_pct DESC;

-- Example 4: Customer profitability analysis across systems
-- Analyze customer value including sales rep costs
WITH customer_sales AS (
    SELECT
        c.customer_id,
        c.company_name,
        c.annual_revenue,
        c.assigned_sales_rep,
        SUM(so.total_amount) AS total_sales,
        COUNT(so.order_id) AS order_count,
        AVG(so.total_amount) AS avg_order_value,
        MAX(so.order_date) AS last_order_date
    FROM sales_system.crm.customers c
             LEFT JOIN sales_system.orders.sales_orders so ON c.customer_id = so.customer_id
    WHERE so.order_status = 'COMPLETED'
      AND so.order_date >= DATE_TRUNC('YEAR', CURRENT_DATE())
    GROUP BY c.customer_id, c.company_name, c.annual_revenue, c.assigned_sales_rep
),
     sales_rep_costs AS (
         SELECT
             e.employee_id,
             e.first_name || ' ' || e.last_name AS sales_rep_name,
             e.salary AS annual_salary,
             COALESCE(SUM(be.monthly_cost * 12), 0) AS annual_benefit_cost,
             e.salary + COALESCE(SUM(be.monthly_cost * 12), 0) AS total_annual_cost
         FROM hr_system.payroll.employees e
                  LEFT JOIN hr_system.benefits.benefit_enrollments be ON e.employee_id = be.employee_id
         WHERE e.status = 'ACTIVE' AND (be.status = 'ACTIVE' OR be.status IS NULL)
         GROUP BY e.employee_id, e.first_name, e.last_name, e.salary
     ),
     rep_customer_allocation AS (
         SELECT
             assigned_sales_rep,
             COUNT(*) AS customers_managed
         FROM sales_system.crm.customers
         WHERE assigned_sales_rep IS NOT NULL
         GROUP BY assigned_sales_rep
     )
SELECT
    cs.company_name,
    cs.annual_revenue,
    cs.total_sales,
    cs.order_count,
    cs.avg_order_value,
    src.sales_rep_name,
    src.total_annual_cost AS rep_annual_cost,
    ROUND(src.total_annual_cost / rca.customers_managed, 2) AS allocated_rep_cost,
    cs.total_sales - ROUND(src.total_annual_cost / rca.customers_managed, 2) AS customer_profit,
    ROUND(
            ((cs.total_sales - ROUND(src.total_annual_cost / rca.customers_managed, 2)) /
             NULLIF(cs.total_sales, 0)) * 100, 2
    ) AS profit_margin_pct,
    cs.last_order_date,
    DATEDIFF('day', cs.last_order_date, CURRENT_DATE()) AS days_since_last_order
FROM customer_sales cs
         LEFT JOIN sales_rep_costs src ON cs.assigned_sales_rep = src.employee_id
         LEFT JOIN rep_customer_allocation rca ON cs.assigned_sales_rep = rca.assigned_sales_rep
WHERE cs.total_sales > 0
ORDER BY customer_profit DESC;

-- Example 5: Data warehouse ETL example - Cross-database data consolidation
-- Populate analytics warehouse with consolidated employee data
USE DATABASE analytics_warehouse;
USE SCHEMA staging;

-- Create staging table for data transformation
CREATE OR REPLACE TABLE employee_staging AS
SELECT
    e.employee_id,
    e.first_name || ' ' || e.last_name AS full_name,
    e.email,
    e.department_id,
    e.salary,
    e.hire_date,
    e.status,

    -- Benefit costs
    COALESCE(SUM(be.monthly_cost * 12), 0) AS annual_benefit_cost,

    -- Sales performance
    COALESCE(sales_metrics.ytd_sales, 0) AS ytd_sales,
    COALESCE(sales_metrics.ytd_commission, 0) AS ytd_commission,
    COALESCE(sales_metrics.deals_closed, 0) AS deals_closed,

    -- Performance rating calculation
    CASE
        WHEN COALESCE(sales_metrics.ytd_sales, 0) > 500000 THEN 'Excellent'
        WHEN COALESCE(sales_metrics.ytd_sales, 0) > 200000 THEN 'Good'
        WHEN COALESCE(sales_metrics.ytd_sales, 0) > 50000 THEN 'Average'
        WHEN e.department_id != 300 THEN 'N/A - Non-Sales'
        ELSE 'Below Target'
        END AS performance_rating,

    CURRENT_TIMESTAMP() AS last_updated

FROM hr_system.payroll.employees e

         LEFT JOIN hr_system.benefits.benefit_enrollments be
                   ON e.employee_id = be.employee_id
                       AND be.status = 'ACTIVE'

         LEFT JOIN (
    SELECT
        so.sales_rep_id,
        SUM(so.total_amount) AS ytd_sales,
        SUM(so.commission_amount) AS ytd_commission,
        COUNT(so.order_id) AS deals_closed
    FROM sales_system.orders.sales_orders so
    WHERE so.order_date >= DATE_TRUNC('YEAR', CURRENT_DATE())
      AND so.order_status = 'COMPLETED'
    GROUP BY so.sales_rep_id
) sales_metrics ON e.employee_id = sales_metrics.sales_rep_id

WHERE e.status = 'ACTIVE'
GROUP BY
    e.employee_id, e.first_name, e.last_name, e.email, e.department_id,
    e.salary, e.hire_date, e.status, sales_metrics.ytd_sales,
    sales_metrics.ytd_commission, sales_metrics.deals_closed;

-- Update the mart table
USE SCHEMA analytics_warehouse.marts;

MERGE INTO employee_360_view AS target
    USING analytics_warehouse.staging.employee_staging AS source
    ON target.employee_id = source.employee_id
    WHEN MATCHED THEN UPDATE SET
                          full_name = source.full_name,
                          current_salary = source.salary,
                          total_benefit_cost = source.annual_benefit_cost,
                          ytd_sales = source.ytd_sales,
                          ytd_commission = source.ytd_commission,
                          performance_rating = source.performance_rating,
                          last_refresh_date = CURRENT_TIMESTAMP()
                          WHEN NOT MATCHED THEN INSERT (
                                  employee_id, full_name, current_salary, total_benefit_cost,
                                  ytd_sales, ytd_commission, performance_rating, last_refresh_date
        ) VALUES (
                                                                            source.employee_id, source.full_name, source.current_salary,
                                                                            source.annual_benefit_cost, source.ytd_sales, source.ytd_commission,
                                                                            source.performance_rating, CURRENT_TIMESTAMP()
                                                                        );

-- Example 6: Advanced cross-database window functions
-- Employee ranking across different metrics from multiple systems
SELECT
    e.employee_id,
    e.first_name || ' ' || e.last_name AS full_name,
    e.department_id,
    e.salary,

    -- Salary rankings across company
    RANK() OVER (ORDER BY e.salary DESC) AS company_salary_rank,
    RANK() OVER (PARTITION BY e.department_id ORDER BY e.salary DESC) AS dept_salary_rank,

    -- Benefit cost rankings
    COALESCE(benefit_totals.annual_benefits, 0) AS annual_benefits,
    RANK() OVER (ORDER BY COALESCE(benefit_totals.annual_benefits, 0) DESC) AS benefit_cost_rank,

    -- Sales performance rankings (for sales reps only)
    COALESCE(sales_totals.ytd_sales, 0) AS ytd_sales,
    CASE
        WHEN e.department_id = 300 THEN
            RANK() OVER (
                PARTITION BY CASE WHEN e.department_id = 300 THEN 1 ELSE 0 END
                ORDER BY COALESCE(sales_totals.ytd_sales, 0) DESC
                )
        ELSE NULL
        END AS sales_rank,

    -- Total compensation ranking
    e.salary + COALESCE(benefit_totals.annual_benefits, 0) + COALESCE(sales_totals.ytd_commission, 0) AS total_comp,
    RANK() OVER (
        ORDER BY e.salary + COALESCE(benefit_totals.annual_benefits, 0) + COALESCE(sales_totals.ytd_commission, 0) DESC
        ) AS total_comp_rank,

    -- Percentile calculations
    PERCENT_RANK() OVER (ORDER BY e.salary) AS salary_percentile,
    PERCENT_RANK() OVER (
        ORDER BY e.salary + COALESCE(benefit_totals.annual_benefits, 0) + COALESCE(sales_totals.ytd_commission, 0)
        ) AS total_comp_percentile

FROM hr_system.payroll.employees e

         LEFT JOIN (
    SELECT
        be.employee_id,
        SUM(be.monthly_cost * 12) AS annual_benefits
    FROM hr_system.benefits.benefit_enrollments be
    WHERE be.status = 'ACTIVE'
    GROUP BY be.employee_id
) benefit_totals ON e.employee_id = benefit_totals.employee_id

         LEFT JOIN (
    SELECT
        so.sales_rep_id,
        SUM(so.total_amount) AS ytd_sales,
        SUM(so.commission_amount) AS ytd_commission
    FROM sales_system.orders.sales_orders so
    WHERE so.order_date >= DATE_TRUNC('YEAR', CURRENT_DATE())
      AND so.order_status = 'COMPLETED'
    GROUP BY so.sales_rep_id
) sales_totals ON e.employee_id = sales_totals.sales_rep_id

WHERE e.status = 'ACTIVE'
ORDER BY total_comp_rank;

-- Example 7: Cross-database recursive CTE
-- Organization hierarchy with cross-system data enrichment
WITH RECURSIVE org_hierarchy AS (
    -- Base case: top-level managers
    SELECT
        e.employee_id,
        e.first_name || ' ' || e.last_name AS full_name,
        e.manager_id,
        e.salary,
        0 AS level,
        CAST(e.first_name || ' ' || e.last_name AS VARCHAR(1000)) AS hierarchy_path,
        CAST(LPAD(e.employee_id::VARCHAR, 10, '0') AS VARCHAR(1000)) AS sort_path
    FROM hr_system.payroll.employees e
    WHERE e.manager_id IS NULL AND e.status = 'ACTIVE'

    UNION ALL

    -- Recursive case: subordinates
    SELECT
        e.employee_id,
        e.first_name || ' ' || e.last_name AS full_name,
        e.manager_id,
        e.salary,
        oh.level + 1,
        oh.hierarchy_path || ' > ' || e.first_name || ' ' || e.last_name,
        oh.sort_path || '.' || LPAD(e.employee_id::VARCHAR, 10, '0')
    FROM hr_system.payroll.employees e
             JOIN org_hierarchy oh ON e.manager_id = oh.employee_id
    WHERE e.status = 'ACTIVE'
)
SELECT
    oh.level,
    REPEAT('  ', oh.level) || oh.full_name AS indented_name,
    oh.salary,
    COALESCE(benefit_costs.annual_benefits, 0) AS annual_benefits,
    COALESCE(sales_perf.ytd_sales, 0) AS ytd_sales,
    COUNT(subordinates.employee_id) AS direct_reports,
    oh.hierarchy_path
FROM org_hierarchy oh

         LEFT JOIN (
    SELECT
        be.employee_id,
        SUM(be.monthly_cost * 12) AS annual_benefits
    FROM hr_system.benefits.benefit_enrollments be
    WHERE be.status = 'ACTIVE'
    GROUP BY be.employee_id
) benefit_costs ON oh.employee_id = benefit_costs.employee_id

         LEFT JOIN (
    SELECT
        so.sales_rep_id,
        SUM(so.total_amount) AS ytd_sales
    FROM sales_system.orders.sales_orders so
    WHERE so.order_date >= DATE_TRUNC('YEAR', CURRENT_DATE())
      AND so.order_status = 'COMPLETED'
    GROUP BY so.sales_rep_id
) sales_perf ON oh.employee_id = sales_perf.sales_rep_id

         LEFT JOIN hr_system.payroll.employees subordinates
                   ON oh.employee_id = subordinates.manager_id
                       AND subordinates.status = 'ACTIVE'

GROUP BY
    oh.level, oh.full_name, oh.salary, benefit_costs.annual_benefits,
    sales_perf.ytd_sales, oh.hierarchy_path, oh.sort_path
ORDER BY oh.sort_path;

-- ============================================================================
-- CROSS-DATABASE SECURITY AND GOVERNANCE EXAMPLES
-- ============================================================================

-- Role-based access across databases
CREATE ROLE hr_analyst;
CREATE ROLE finance_analyst;
CREATE ROLE sales_manager;
CREATE ROLE executive;

-- Grant cross-database permissions
GRANT USAGE ON DATABASE hr_system TO ROLE hr_analyst;
GRANT USAGE ON SCHEMA hr_system.payroll TO ROLE hr_analyst;
GRANT USAGE ON SCHEMA hr_system.benefits TO ROLE hr_analyst;
GRANT SELECT ON ALL TABLES IN SCHEMA hr_system.payroll TO ROLE hr_analyst;
GRANT SELECT ON ALL TABLES IN SCHEMA hr_system.benefits TO ROLE hr_analyst;

-- Finance analyst needs read access to HR for budget analysis
GRANT USAGE ON DATABASE hr_system TO ROLE finance_analyst;
GRANT USAGE ON SCHEMA hr_system.payroll TO ROLE finance_analyst;
GRANT SELECT ON hr_system.payroll.employees TO ROLE finance_analyst;

-- Executive role gets access to analytics warehouse
GRANT USAGE ON DATABASE analytics_warehouse TO ROLE executive;
GRANT USAGE ON ALL SCHEMAS IN DATABASE analytics_warehouse TO ROLE executive;
GRANT SELECT ON ALL