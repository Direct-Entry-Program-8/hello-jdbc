CREATE DATABASE dep8_hello;
USE dep8_hello;
CREATE TABLE customer
(
    id VARCHAR(4) PRIMARY KEY,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    dob DATE NOT NULL,
    picture LONGBLOB NOT NULL
);
CREATE TABLE contact
(
    customer_id VARCHAR(4) NOT NULL,
    telephone VARCHAR(15) NOT NULL,
    CONSTRAINT pk_contact PRIMARY KEY (customer_id, telephone),
    CONSTRAINT fk_contact FOREIGN KEY (customer_id) REFERENCES customer(id)
);
CREATE TABLE user
(
    username VARCHAR(15) PRIMARY KEY,
    password VARCHAR(15) NOT NULL
);
INSERT INTO user (username, password) VALUES ('admin', 'admin123');
INSERT INTO user (username, password) VALUES ('ijse', 'ijse123');