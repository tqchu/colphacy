CREATE TYPE branch_status AS ENUM ('OPEN', 'CLOSED', 'MAINTENANCE');
CREATE TYPE gender AS ENUM ('MALE', 'FEMALE', 'OTHER');
CREATE TYPE role_name AS ENUM ('STAFF', 'ADMIN');
CREATE TABLE branch (
                        id SERIAL PRIMARY KEY,
                        streetAddress VARCHAR(255) NOT NULL,
                        ward VARCHAR(255) NOT NULL,
                        district VARCHAR(255) NOT NULL,
                        province VARCHAR(255) NOT NULL,
                        closingHour VARCHAR(10) NOT NULL,
                        openingHour VARCHAR(10) NOT NULL,
                        phoneNumber VARCHAR(10) NOT NULL,
                        status branch_status NOT NULL DEFAULT 'OPEN'
);

CREATE INDEX idx_branch_status ON Branch(status);

CREATE TABLE employee (
                          id SERIAL PRIMARY KEY,
                          fullName VARCHAR(50) NOT NULL,
                          username VARCHAR(50) NOT NULL,
                          password VARCHAR(60) NOT NULL,
                          phone VARCHAR(10) NOT NULL,
                          isActive BOOLEAN NOT NULL DEFAULT TRUE,
                          gender gender NOT NULL,
                          branch_id INTEGER,
                          role_id INTEGER NOT NULL,
                          FOREIGN KEY (branch_id) REFERENCES Branch(id),
                          FOREIGN KEY (role_id) REFERENCES Role(id)
);

CREATE INDEX idx_employee_active ON Employee(isActive);
CREATE INDEX idx_employee_gender ON Employee(gender);



-- Branch status

ALTER TABLE branch
ALTER COLUMN status DROP DEFAULT;
ALTER TABLE branch
ALTER COLUMN status TYPE branch_status USING status::branch_status;
ALTER TABLE branch
ALTER COLUMN status SET DEFAULT 'OPEN';

-- Customer gender

ALTER TABLE customer
ALTER COLUMN gender TYPE gender USING gender::gender;

-- Employee gender
ALTER TABLE employee
ALTER COLUMN gender TYPE gender USING gender::gender;

-- Employee role

ALTER TABLE role
ALTER COLUMN role_name TYPE role_name USING role_name::role_name;
