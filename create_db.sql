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
  `hotel_id` tinyint UNIQUE NOT NULL,
  `hotel_name` varchar(255) NOT NULL,
  PRIMARY KEY (`hotel_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- create table of room types, reduces repeated varchar strings in reservations table
CREATE TABLE `room_sizes` (
  `room_size_id` tinyint UNIQUE NOT NULL,
  `room_size_name` varchar(255) NOT NULL,
  PRIMARY KEY (`room_size_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- create reservations table
CREATE TABLE `reservations` (
  `reservation_id` varchar(36) UNIQUE NOT NULL, -- UUID
  `user_id` varchar(36) NOT NULL, -- UUID
  `hotel_id` tinyint NOT NULL,
  `check_in` date NOT NULL,
  `check_out` date NOT NULL,
  `room_size_id` tinyint NOT NULL,
  `wifi` boolean DEFAULT NULL,
  `breakfast` boolean DEFAULT NULL,
  `parking` boolean DEFAULT NULL,
  PRIMARY KEY (`reservation_id`),
  FOREIGN KEY (`user_id`) REFERENCES users(`user_id`),
  FOREIGN KEY (`hotel_id`) REFERENCES hotels(`hotel_id`),
  FOREIGN KEY (`room_size_id`) REFERENCES room_sizes(`room_size_id`)
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
(SELECT `room_size_name` FROM `room_sizes` WHERE `reservations`.`room_size_id` = `room_sizes`.`room_size_id`) AS 'room_size_name',
`wifi`,
`breakfast`,
`parking`,
(DATEDIFF(`check_out`, `check_in`) * 150) AS `points_earned` FROM `reservations`;
-- query for total points earned like this: SELECT SUM(`points_earned`)FROM `reservations_view` WHERE `user_id` = "f81c3fe5-cc03-11ed-a4d2-1735c641f2fc";

CREATE TABLE `guests` (
  `guest_id` bigint NOT NULL AUTO_INCREMENT,
  `reservation_id` varchar(36) NOT NULL,  -- UUIDs
  `guest_first_name` varchar(255) NOT NULL,
  `guest_last_name` varchar(255) NOT NULL,
  PRIMARY KEY (`guest_id`),
  FOREIGN KEY (`reservation_id`) REFERENCES reservations(`reservation_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- dummy data is created in a separate file, everything in this file is necessary for the database and API to function

-- insert necessary hotel data
INSERT INTO `hotels` (`hotel_id`, `hotel_name`) VALUES (1, "Hotel 1"); -- names need to be changed
INSERT INTO `hotels` (`hotel_id`, `hotel_name`) VALUES (2, "Hotel 2");
INSERT INTO `hotels` (`hotel_id`, `hotel_name`) VALUES (3, "Hotel 3");
INSERT INTO `hotels` (`hotel_id`, `hotel_name`) VALUES (4, "Hotel 4");

-- insert necessary room sizes table
INSERT INTO `room_sizes` (`room_size_id`, `room_size_name`) VALUES (1, "Double Full Beds");
INSERT INTO `room_sizes` (`room_size_id`, `room_size_name`) VALUES (2, "Single Queen Bed");
INSERT INTO `room_sizes` (`room_size_id`, `room_size_name`) VALUES (3, "Double Queen Beds");
INSERT INTO `room_sizes` (`room_size_id`, `room_size_name`) VALUES (4, "Single King Bed");