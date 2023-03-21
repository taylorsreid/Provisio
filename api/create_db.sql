DROP DATABASE IF EXISTS `provisio`;

-- create db
CREATE DATABASE `provisio` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci */;

-- provisio.users definition
CREATE TABLE `provisio`.`users` (
  `customer_id` varchar(255) NOT NULL,
  `email` varchar(255) NOT NULL,
  `first_name` varchar(255) NOT NULL,
  `last_name` varchar(255) NOT NULL,
  `hashed_password` varchar(255) NOT NULL,
  PRIMARY KEY (`customer_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- provisio.reservations definition

CREATE TABLE `provisio`.`reservations` (
  `customer_id` varchar(255) NOT NULL,
  `reservation_id` varchar(255) NOT NULL,
  `check_in` date NOT NULL,
  `check_out` date NOT NULL,
  `room_size` varchar(20) NOT NULL,
  `wifi` tinyint(1) DEFAULT NULL,
  `breakfast` tinyint(1) DEFAULT NULL,
  `parking` tinyint(1) DEFAULT NULL,
  `guests` tinyint(4) NOT NULL,
  PRIMARY KEY (`reservation_id`),
  FOREIGN KEY (`customer_id`) REFERENCES users(`customer_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;