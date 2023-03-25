DROP DATABASE IF EXISTS `provisio`;

-- create db
CREATE DATABASE `provisio` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci */;

-- create customers table
CREATE TABLE `provisio`.`customers` (
  `customer_id` varchar(255) UNIQUE NOT NULL,
  `email` varchar(255) NOT NULL,
  `first_name` varchar(255) NOT NULL,
  `last_name` varchar(255) NOT NULL,
  `hashed_password` varchar(255) NOT NULL,
  PRIMARY KEY (`customer_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- create table of hotels, reduces repeated varchar strings in reservations table
CREATE TABLE `provisio`.`hotels` (
  `hotel_id` tinyint UNIQUE NOT NULL,
  `hotel_name` varchar(255) NOT NULL,
  PRIMARY KEY (`hotel_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- insert hotel data
INSERT INTO `provisio`.`hotels` (`hotel_id`, `hotel_name`) VALUES (1, "Hotel 1"); -- names need to be changed
INSERT INTO `provisio`.`hotels` (`hotel_id`, `hotel_name`) VALUES (2, "Hotel 2");
INSERT INTO `provisio`.`hotels` (`hotel_id`, `hotel_name`) VALUES (3, "Hotel 3");
INSERT INTO `provisio`.`hotels` (`hotel_id`, `hotel_name`) VALUES (4, "Hotel 4");

-- create table of room types, reduces repeated varchar strings in reservations table
CREATE TABLE `provisio`.`room_sizes` (
  `room_size_id` tinyint UNIQUE NOT NULL,
  `room_size_name` varchar(255) NOT NULL,
  PRIMARY KEY (`room_size_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

INSERT INTO `provisio`.`room_sizes` (`room_size_id`, `room_size_name`) VALUES (1, "Double Full Beds");
INSERT INTO `provisio`.`room_sizes` (`room_size_id`, `room_size_name`) VALUES (2, "Single Queen Bed");
INSERT INTO `provisio`.`room_sizes` (`room_size_id`, `room_size_name`) VALUES (3, "Double Queen Beds");
INSERT INTO `provisio`.`room_sizes` (`room_size_id`, `room_size_name`) VALUES (4, "Single King Bed");

-- create reservations table
CREATE TABLE `provisio`.`reservations` (
  `customer_id` varchar(255) NOT NULL,
  `reservation_id` varchar(255) UNIQUE NOT NULL,
  `hotel_id` tinyint NOT NULL,
  `check_in` date NOT NULL,
  `check_out` date NOT NULL,
  `room_size_id` tinyint NOT NULL,
  `wifi` tinyint DEFAULT NULL,
  `breakfast` tinyint DEFAULT NULL,
  `parking` tinyint DEFAULT NULL,
  `guests` tinyint NOT NULL,
  PRIMARY KEY (`reservation_id`),
  FOREIGN KEY (`customer_id`) REFERENCES customers(`customer_id`),
  FOREIGN KEY (`hotel_id`) REFERENCES hotels(`hotel_id`),
  FOREIGN KEY (`room_size_id`) REFERENCES room_sizes(`room_size_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;