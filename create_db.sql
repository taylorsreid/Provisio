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
  `creation_date` date NOT NULL DEFAULT NOW(),
  `total_points` bigint DEFAULT 0,
  PRIMARY KEY (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- create table of hotels, reduces repeated varchar strings in reservations table
CREATE TABLE `hotels` (
  `hotel_id` tinyint UNIQUE NOT NULL AUTO_INCREMENT,
  `hotel_name` varchar(255) NOT NULL,
  PRIMARY KEY (`hotel_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

CREATE TABLE `charge_names` (
	`charge_names_id` bigint UNIQUE NOT NULL AUTO_INCREMENT,
	`name` varchar(255) UNIQUE NOT NULL,
	`per_night` boolean DEFAULT true,
	PRIMARY KEY (`charge_names_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

CREATE TABLE `charge_prices` (
	`charge_prices_id` bigint UNIQUE NOT NULL AUTO_INCREMENT,
	`charge_names_id` bigint NOT NULL,
	`price` decimal(13,2) NOT NULL,
	`valid_from` date NOT NULL,
	`valid_until` date NOT NULL,
	PRIMARY KEY (`charge_prices_id`),
	FOREIGN KEY (`charge_names_id`) REFERENCES charge_names(`charge_names_id`) ON DELETE CASCADE
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
  `creation_date` date NOT NULL DEFAULT NOW(),
  PRIMARY KEY (`reservation_id`),
  FOREIGN KEY (`user_id`) REFERENCES users(`user_id`) ON DELETE CASCADE,
  FOREIGN KEY (`hotel_id`) REFERENCES hotels(`hotel_id`) ON DELETE CASCADE,
  FOREIGN KEY (`room_size_id`) REFERENCES charge_names(`charge_names_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

CREATE TABLE `guests` (
  `guest_id` bigint UNIQUE NOT NULL AUTO_INCREMENT,
  `reservation_id` varchar(36) NOT NULL,  -- UUIDs
  `guest_first_name` varchar(255) NOT NULL,
  `guest_last_name` varchar(255) NOT NULL,
  PRIMARY KEY (`guest_id`),
  FOREIGN KEY (`reservation_id`) REFERENCES reservations(`reservation_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

CREATE TABLE `room_charges` (
	`room_charge_id` bigint UNIQUE NOT NULL AUTO_INCREMENT,
	`charge_prices_id` bigint NOT NULL,
	`reservation_id` varchar(255) NOT NULL,
	PRIMARY KEY (`room_charge_id`),
	FOREIGN KEY (`charge_prices_id`) REFERENCES charge_prices(`charge_prices_id`) ON DELETE CASCADE,
	FOREIGN KEY (`reservation_id`) REFERENCES reservations(`reservation_id`) ON DELETE CASCADE	
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- insert necessary hotel data
INSERT INTO `hotels` (`hotel_name`) VALUES ("The Midnight Resort"); -- subject to change
INSERT INTO `hotels` (`hotel_name`) VALUES ("The Palatial Hotel");
INSERT INTO `hotels` (`hotel_name`) VALUES ("Alpine Haven Ski Lodge");
INSERT INTO `hotels` (`hotel_name`) VALUES ("The Serene Harbor Hotel & Yacht Club");

-- insert charge_names
INSERT INTO `charge_names` (`charge_names_id`, `name`, `per_night`) VALUES (1, 'wifi', false);
INSERT INTO `charge_names` (`charge_names_id`, `name`) VALUES (2, 'breakfast');
INSERT INTO `charge_names` (`charge_names_id`, `name`) VALUES (3, 'parking');
INSERT INTO `charge_names` (`charge_names_id`, `name`) VALUES (4, 'Double Full Beds');
INSERT INTO `charge_names` (`charge_names_id`, `name`) VALUES (5, 'Single Queen Bed');
INSERT INTO `charge_names` (`charge_names_id`, `name`) VALUES (6, 'Double Queen Beds');
INSERT INTO `charge_names` (`charge_names_id`, `name`) VALUES (7, 'Single King Bed');

-- insert charge_prices
DELIMITER //
CREATE PROCEDURE insert_chargeables_prices()
BEGIN
	
	DECLARE yearsAhead INT DEFAULT 20; -- how many years ahead to generate prices for
    DECLARE x INT DEFAULT 2023;
    DECLARE i INT DEFAULT 1;
   
	-- constant prices, set to minimum and maximum MySQL dates
	INSERT INTO `charge_prices` (`charge_names_id`, `price`, `valid_from`, `valid_until`) VALUES (1, 12.99, '1000-01-01', '9999-12-31'); -- wifi
	INSERT INTO `charge_prices` (`charge_names_id`, `price`, `valid_from`, `valid_until`) VALUES (2, 8.99, '1000-01-01', '9999-12-31'); -- breakfast
	INSERT INTO `charge_prices` (`charge_names_id`, `price`, `valid_from`, `valid_until`) VALUES (3, 19.99, '1000-01-01', '9999-12-31'); -- parking

    WHILE i <= yearsAhead DO
    
	    -- Double Full Beds
	    INSERT INTO `charge_prices` (`charge_names_id`, `price`, `valid_from`, `valid_until`) VALUES (4, 115.50, CONCAT(x, '-01-01'), CONCAT(x, '-07-03'));
	    INSERT INTO `charge_prices` (`charge_names_id`, `price`, `valid_from`, `valid_until`) VALUES (4, 121.28, CONCAT(x, '-07-04'), CONCAT(x, '-07-04')); -- July 4th
	    INSERT INTO `charge_prices` (`charge_names_id`, `price`, `valid_from`, `valid_until`) VALUES (4, 115.50, CONCAT(x, '-07-05'), CONCAT(x, '-12-23'));
	    INSERT INTO `charge_prices` (`charge_names_id`, `price`, `valid_from`, `valid_until`) VALUES (4, 121.28, CONCAT(x, '-12-24'), CONCAT(x, '-12-24')); -- Christmas Eve
	    INSERT INTO `charge_prices` (`charge_names_id`, `price`, `valid_from`, `valid_until`) VALUES (4, 115.50, CONCAT(x, '-12-25'), CONCAT(x, '-12-30'));
	    INSERT INTO `charge_prices` (`charge_names_id`, `price`, `valid_from`, `valid_until`) VALUES (4, 121.28, CONCAT(x, '-12-31'), CONCAT(x, '-12-31')); -- New Years Eve
	   
	    -- Single Queen Bed
	    INSERT INTO `charge_prices` (`charge_names_id`, `price`, `valid_from`, `valid_until`) VALUES (5, 131.25, CONCAT(x, '-01-01'), CONCAT(x, '-07-03'));
	    INSERT INTO `charge_prices` (`charge_names_id`, `price`, `valid_from`, `valid_until`) VALUES (5, 137.82, CONCAT(x, '-07-04'), CONCAT(x, '-07-04')); -- July 4th
	    INSERT INTO `charge_prices` (`charge_names_id`, `price`, `valid_from`, `valid_until`) VALUES (5, 131.25, CONCAT(x, '-07-05'), CONCAT(x, '-12-23'));
	    INSERT INTO `charge_prices` (`charge_names_id`, `price`, `valid_from`, `valid_until`) VALUES (5, 137.82, CONCAT(x, '-12-24'), CONCAT(x, '-12-24')); -- Christmas Eve
	    INSERT INTO `charge_prices` (`charge_names_id`, `price`, `valid_from`, `valid_until`) VALUES (5, 131.25, CONCAT(x, '-12-25'), CONCAT(x, '-12-30'));
	    INSERT INTO `charge_prices` (`charge_names_id`, `price`, `valid_from`, `valid_until`) VALUES (5, 137.82, CONCAT(x, '-12-31'), CONCAT(x, '-12-31')); -- New Years Eve
	   
	    -- Double Queen Beds
	    INSERT INTO `charge_prices` (`charge_names_id`, `price`, `valid_from`, `valid_until`) VALUES (6, 157.50, CONCAT(x, '-01-01'), CONCAT(x, '-07-03'));
	    INSERT INTO `charge_prices` (`charge_names_id`, `price`, `valid_from`, `valid_until`) VALUES (6, 165.80, CONCAT(x, '-07-04'), CONCAT(x, '-07-04')); -- July 4th
	    INSERT INTO `charge_prices` (`charge_names_id`, `price`, `valid_from`, `valid_until`) VALUES (6, 157.50, CONCAT(x, '-07-05'), CONCAT(x, '-12-23'));
	    INSERT INTO `charge_prices` (`charge_names_id`, `price`, `valid_from`, `valid_until`) VALUES (6, 165.80, CONCAT(x, '-12-24'), CONCAT(x, '-12-24')); -- Christmas Eve
	    INSERT INTO `charge_prices` (`charge_names_id`, `price`, `valid_from`, `valid_until`) VALUES (6, 157.50, CONCAT(x, '-12-25'), CONCAT(x, '-12-30'));
	    INSERT INTO `charge_prices` (`charge_names_id`, `price`, `valid_from`, `valid_until`) VALUES (6, 165.80, CONCAT(x, '-12-31'), CONCAT(x, '-12-31')); -- New Years Eve
	   
	    -- Single King Bed
	    INSERT INTO `charge_prices` (`charge_names_id`, `price`, `valid_from`, `valid_until`) VALUES (7, 173.25, CONCAT(x, '-01-01'), CONCAT(x, '-07-03'));
	    INSERT INTO `charge_prices` (`charge_names_id`, `price`, `valid_from`, `valid_until`) VALUES (7, 181.92, CONCAT(x, '-07-04'), CONCAT(x, '-07-04')); -- July 4th
	    INSERT INTO `charge_prices` (`charge_names_id`, `price`, `valid_from`, `valid_until`) VALUES (7, 173.25, CONCAT(x, '-07-05'), CONCAT(x, '-12-23'));
	    INSERT INTO `charge_prices` (`charge_names_id`, `price`, `valid_from`, `valid_until`) VALUES (7, 181.92, CONCAT(x, '-12-24'), CONCAT(x, '-12-24')); -- Christmas Eve
	    INSERT INTO `charge_prices` (`charge_names_id`, `price`, `valid_from`, `valid_until`) VALUES (7, 173.25, CONCAT(x, '-12-25'), CONCAT(x, '-12-30'));
	    INSERT INTO `charge_prices` (`charge_names_id`, `price`, `valid_from`, `valid_until`) VALUES (7, 181.92, CONCAT(x, '-12-31'), CONCAT(x, '-12-31')); -- New Years Eve
    
        SET x = x + 1;
        SET i = i + 1;
    END WHILE;
END //

DELIMITER ;

CALL insert_chargeables_prices();

-- create view that is easier to query from Java
CREATE OR REPLACE VIEW `reservations_view` AS SELECT 
`reservation_id`,
`user_id`,
(SELECT `user_first_name` FROM `users` WHERE `reservations`.`user_id` = `users`.`user_id`) AS 'user_first_name',
(SELECT `user_last_name` FROM `users` WHERE `reservations`.`user_id` = `users`.`user_id`) AS 'user_last_name',
(SELECT `hotel_name` FROM `hotels` WHERE `reservations`.`hotel_id` = `hotels`.`hotel_id`) AS 'hotel_name',
`check_in`,
`check_out`,
(SELECT `name` FROM `charge_names` WHERE `reservations`.`room_size_id` = `charge_names`.`charge_names_id`) AS 'room_size_name',
`wifi`,
`breakfast`,
`parking`,
(DATEDIFF(`check_out`, `check_in`) * 150) AS `points_earned` FROM `reservations`;