-- Kendrick Baker
-- Julia Delightly
-- Nathan Mausbach
-- Jessica Phan
-- Taylor Reid
-- 3/29/2023 
-- Module 5.1

-- removes DB if it already exists and will create a clean DB
DROP DATABASE IF EXISTS `provisio`;

-- create db
CREATE DATABASE `provisio` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci */;

-- select which DB to use
USE `provisio`;

-- create customers table
CREATE TABLE `users` (
  `user_id` varchar(36) UNIQUE NOT NULL, -- UUID
  `email` varchar(255) NOT NULL,
  `user_first_name` varchar(255) NOT NULL,
  `user_last_name` varchar(255) NOT NULL,
  `hashed_password` varchar(255) NOT NULL,
  PRIMARY KEY (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- create table of hotels, reduces repeated varchar strings in reservations table
CREATE TABLE `hotels` (
  `hotel_id` tinyint UNIQUE NOT NULL AUTO_INCREMENT,
  `hotel_name` varchar(255) NOT NULL,
  PRIMARY KEY (`hotel_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

CREATE TABLE `prices` (
	`item_id` bigint UNIQUE NOT NULL AUTO_INCREMENT,
	`item_name` varchar(255) UNIQUE NOT NULL,
	`item_price` decimal(13,2) NOT NULL,
	`item_holiday_price` decimal(13,2) DEFAULT NULL,
	PRIMARY KEY (`item_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

CREATE TABLE `holidays` (
  `holiday_name` varchar(255) NOT NULL UNIQUE,
  `holiday_date` date NOT NULL,
  PRIMARY KEY (`holiday_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- create reservations table
CREATE TABLE `reservations` (
  `reservation_id` varchar(36) UNIQUE NOT NULL, -- UUID
  `user_id` varchar(36) NOT NULL, -- UUID
  `hotel_id` tinyint NOT NULL,
  `check_in` date NOT NULL,
  `check_out` date NOT NULL,
  `room_size_id` bigint NOT NULL,
  `wifi` boolean DEFAULT NULL,
  `breakfast` boolean DEFAULT NULL,
  `parking` boolean DEFAULT NULL,
  PRIMARY KEY (`reservation_id`),
  FOREIGN KEY (`user_id`) REFERENCES users(`user_id`) ON DELETE CASCADE,
  FOREIGN KEY (`hotel_id`) REFERENCES hotels(`hotel_id`) ON DELETE CASCADE,
  FOREIGN KEY (`room_size_id`) REFERENCES prices(`item_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

CREATE TABLE `guests` (
  `guest_id` bigint UNIQUE NOT NULL AUTO_INCREMENT,
  `reservation_id` varchar(36) NOT NULL,  -- UUIDs
  `guest_first_name` varchar(255) NOT NULL,
  `guest_last_name` varchar(255) NOT NULL,
  PRIMARY KEY (`guest_id`),
  FOREIGN KEY (`reservation_id`) REFERENCES reservations(`reservation_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

CREATE TABLE `charges` (
	`charge_id` bigint UNIQUE NOT NULL AUTO_INCREMENT,
	`item_id` bigint NOT NULL,
	`reservation_id` varchar(255) NOT NULL,
	PRIMARY KEY (`charge_id`),
	FOREIGN KEY (`item_id`) REFERENCES prices(`item_id`) ON DELETE CASCADE,
	FOREIGN KEY (`reservation_id`) REFERENCES reservations(`reservation_id`) ON DELETE CASCADE	
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- the purpose of this view is to make it easier to query from Java
CREATE OR REPLACE VIEW `reservations_view` AS SELECT 
`reservation_id`,
`user_id`,
(SELECT `user_first_name` FROM `users` WHERE `reservations`.`user_id` = `users`.`user_id`) AS 'user_first_name',
(SELECT `user_last_name` FROM `users` WHERE `reservations`.`user_id` = `users`.`user_id`) AS 'user_last_name',
(SELECT `hotel_name` FROM `hotels` WHERE `reservations`.`hotel_id` = `hotels`.`hotel_id`) AS 'hotel_name',
`check_in`,
`check_out`,
(SELECT `item_name` FROM `prices` WHERE `reservations`.`room_size_id` = `prices`.`item_id`) AS 'room_size_name',
`wifi`,
`breakfast`,
`parking`,
(DATEDIFF(`check_out`, `check_in`) * 150) AS `points_earned` FROM `reservations`;
-- query for total points earned like this: SELECT SUM(`points_earned`)FROM `reservations_view` WHERE `user_id` = "f81c3fe5-cc03-11ed-a4d2-1735c641f2fc";

CREATE OR REPLACE VIEW `charges_view` AS SELECT
	`charge_id`,
	`item_id`,
	`reservation_id`,
	(SELECT `item_name` FROM `prices` WHERE `charges`.`item_id` = `prices`.`item_id`) AS 'item_name',
	(SELECT `item_price` FROM `prices` WHERE `charges`.`item_id` = `prices`.`item_id`) AS 'item_price'
	FROM `charges`;
	

-- insert necessary hotel data
INSERT INTO `hotels` (`hotel_name`) VALUES ("The Midnight Resort"); -- subject to change
INSERT INTO `hotels` (`hotel_name`) VALUES ("The Palatial Hotel");
INSERT INTO `hotels` (`hotel_name`) VALUES ("Alpine Haven Ski Lodge");
INSERT INTO `hotels` (`hotel_name`) VALUES ("The Serene Harbor Hotel & Yacht Club");

-- insert necessary room sizes table
INSERT INTO `prices` (`item_name`, `item_price`) VALUES ("Double Full Beds", 115.50);
INSERT INTO `prices` (`item_name`, `item_price`) VALUES ("Single Queen Bed", 131.25);
INSERT INTO `prices` (`item_name`, `item_price`) VALUES ("Double Queen Beds", 157.50);
INSERT INTO `prices` (`item_name`, `item_price`) VALUES ("Single King Bed", 173.25);
INSERT INTO `prices` (`item_name`, `item_price`) VALUES ("wifi", 12.99);
INSERT INTO `prices` (`item_name`, `item_price`) VALUES ("breakfast", 8.99);
INSERT INTO `prices` (`item_name`, `item_price`) VALUES ("parking", 19.99);

-- insert holidays
DELIMITER //

CREATE PROCEDURE insert_holidays()
BEGIN
    DECLARE x INT DEFAULT 2023;
    DECLARE i INT DEFAULT 1;

    WHILE i <= 20 DO
    	INSERT INTO `holidays` (`holiday_name`, `holiday_date`) VALUES (CONCAT("Fourth of July ", x), CONCAT(x, '-07-04')); -- Fourth of July
        INSERT INTO `holidays` (`holiday_name`, `holiday_date`) VALUES (CONCAT("Christmas Eve ", x), CONCAT(x, '-12-24')); -- Christmas Eve
        INSERT INTO `holidays` (`holiday_name`, `holiday_date`) VALUES (CONCAT("New Years Eve ", x), CONCAT(x, '-12-31')); -- New Years Eve
        SET x = x + 1;
        SET i = i + 1;
    END WHILE;
END //

DELIMITER ;

CALL insert_holidays();