DROP DATABASE IF EXISTS `provisio`;

-- create db
CREATE DATABASE `provisio` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci */;

USE `provisio`;

-- create customers table
CREATE TABLE `users` (
  `user_id` varchar(36) UNIQUE NOT NULL, -- UUID
  `email` varchar(255) NOT NULL,
  `first_name` varchar(255) NOT NULL,
  `last_name` varchar(255) NOT NULL,
  `hashed_password` varchar(255) NOT NULL,
  PRIMARY KEY (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- create table of hotels, reduces repeated varchar strings in reservations table
CREATE TABLE `locations` (
  `location_id` tinyint UNIQUE NOT NULL,
  `location_name` varchar(255) NOT NULL,
  PRIMARY KEY (`location_id`)
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
  `location_id` tinyint NOT NULL,
  `check_in` date NOT NULL,
  `check_out` date NOT NULL,
  `room_size_id` tinyint NOT NULL,
  `wifi` tinyint DEFAULT NULL,
  `breakfast` tinyint DEFAULT NULL,
  `parking` tinyint DEFAULT NULL,
  PRIMARY KEY (`reservation_id`),
  FOREIGN KEY (`user_id`) REFERENCES users(`user_id`),
  FOREIGN KEY (`location_id`) REFERENCES locations(`location_id`),
  FOREIGN KEY (`room_size_id`) REFERENCES room_sizes(`room_size_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- the purpose of this function is to make it easier reservation information to query from Java
CREATE OR REPLACE VIEW `reservations_view` AS SELECT 
`reservation_id`,
`user_id`,
(SELECT `first_name` FROM `users` WHERE `reservations`.`user_id` = `users`.`user_id`) AS 'first_name',
(SELECT `last_name` FROM `users` WHERE `reservations`.`user_id` = `users`.`user_id`) AS 'last_name',
(SELECT `location_name` FROM `locations` WHERE `reservations`.`location_id` = `locations`.`location_id`) AS 'location_name',
`check_in`,
`check_out`,
(SELECT `room_size_name` FROM `room_sizes` WHERE `reservations`.`room_size_id` = `room_sizes`.`room_size_id`) AS 'room_size_name',
`wifi`,
`breakfast`,
`parking`,
(DATEDIFF(`check_out`, `check_in`) * 150) AS `points_earned` FROM `reservations`;
-- query for total points earned like this: SELECT SUM(`points_earned`)FROM `reservations_view` WHERE `user_id` = "f81c3fe5-cc03-11ed-a4d2-1735c641f2fc";
-- do guest count, guest names, and guest fees in Java because I don't know how to do them in SQL lol

CREATE TABLE `guests` (
  `guest_id` bigint NOT NULL AUTO_INCREMENT,
  `reservation_id` varchar(36) NOT NULL,  -- UUIDs
  `first_name` varchar(255) NOT NULL,
  `last_name` varchar(255) NOT NULL,
  PRIMARY KEY (`guest_id`),
  FOREIGN KEY (`reservation_id`) REFERENCES reservations(`reservation_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- insert hotel data
INSERT INTO `locations` (`location_id`, `location_name`) VALUES (1, "Hotel 1"); -- names need to be changed
INSERT INTO `locations` (`location_id`, `location_name`) VALUES (2, "Hotel 2");
INSERT INTO `locations` (`location_id`, `location_name`) VALUES (3, "Hotel 3");
INSERT INTO `locations` (`location_id`, `location_name`) VALUES (4, "Hotel 4");

-- insert room sizes talbes
INSERT INTO `room_sizes` (`room_size_id`, `room_size_name`) VALUES (1, "Double Full Beds");
INSERT INTO `room_sizes` (`room_size_id`, `room_size_name`) VALUES (2, "Single Queen Bed");
INSERT INTO `room_sizes` (`room_size_id`, `room_size_name`) VALUES (3, "Double Queen Beds");
INSERT INTO `room_sizes` (`room_size_id`, `room_size_name`) VALUES (4, "Single King Bed");

-- create test user
INSERT INTO users (user_id, email, first_name, last_name, hashed_password) VALUES('f81c3fe5-cc03-11ed-a4d2-1735c641f2fc', 'test@example.com', 'Test', 'User', '$2a$10$W9A1VVvL4OoqaTvH3dq6sOdq8D2xQl2NR4nsIrdiX24DhZNc38n7G');