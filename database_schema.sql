CREATE SCHEMA karya;
USE karya;

CREATE TABLE User(
	email VARCHAR(200) PRIMARY KEY,
    telegramId VARCHAR(20) UNIQUE,
    name VARCHAR(50) NOT NULL,
    surname VARCHAR(50) NOT NULL,
    password VARCHAR(255) NOT NULL,
    phone_number VARCHAR(50) NOT NULL,
    statements BOOLEAN NOT NULL);

CREATE TABLE Fire(
	id INTEGER auto_increment PRIMARY KEY,
    actual_location VARCHAR(100) NOT NULL,
    fire_location VARCHAR(100) NOT NULL,
    photo MEDIUMBLOB,
    industrial_area BOOLEAN NOT NULL,
    waste_dump BOOLEAN NOT NULL,
    city_center BOOLEAN NOT NULL,
    trusses BOOLEAN NOT NULL,
    gas_pipeline BOOLEAN NOT NULL,
    bell_tower BOOLEAN NOT NULL,
    more_info VARCHAR(255) NOT NULL,
    dateAndTime VARCHAR(30) NOT NULL,
    
    user_email VARCHAR(200),
    FOREIGN KEY (user_email) REFERENCES user(email) ON DELETE CASCADE ON UPDATE CASCADE
);