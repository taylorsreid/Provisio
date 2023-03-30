-- Kendrick Baker
-- Julia Delightly
-- Nathan Mausbach
-- Jessica Phan
-- Taylor Reid
-- 3/29/2023 
-- Module 5.1

USE `provisio`;

-- create dummy users
INSERT INTO `users` (user_id, email, first_name, last_name, hashed_password)
VALUES('f81c3fe5-cc03-11ed-a4d2-1735c641f2fc', 'testusersr@example.com', 'Test', 'User Sr.', '$2a$10$W9A1VVvL4OoqaTvH3dq6sOdq8D2xQl2NR4nsIrdiX24DhZNc38n7G');
INSERT INTO `users` (user_id, email, first_name, last_name, hashed_password) 
VALUES('67583904-cd22-11ed-a4d2-1735c641f2fc', 'testuserjr@example.com', 'Test', 'User Jr.', '$2a$10$W9A1VVvL4OoqaTvH3dq6sOdq8D2xQl2NR4nsIrdiX24DhZNc38n7G');
INSERT INTO `users` (user_id, email, first_name, last_name, hashed_password) 
VALUES('79657b19-cd22-11ed-a4d2-1735c641f2fc', 'testuseriii@example.com', 'Test', 'User III', '$2a$10$W9A1VVvL4OoqaTvH3dq6sOdq8D2xQl2NR4nsIrdiX24DhZNc38n7G');

-- create dummy reservations
INSERT INTO `reservations` (`reservation_id`, `user_id`, `location_id`, `check_in`, `check_out`, `room_size_id`, `wifi`, `breakfast`, `parking`)
VALUES('046be693-9145-4d45-a63d-034ae708725a', 'f81c3fe5-cc03-11ed-a4d2-1735c641f2fc', 1, '2024-04-01', '2024-04-08', 3, 1, 1, 1);
INSERT INTO `reservations` (`reservation_id`, `user_id`, `location_id`, `check_in`, `check_out`, `room_size_id`, `wifi`, `breakfast`, `parking`)
VALUES('179dc8db-a8ae-423e-9a74-e42a44900e65', '67583904-cd22-11ed-a4d2-1735c641f2fc', 2, '2024-04-02', '2024-04-09', 1, 1, 1, 0);
INSERT INTO `reservations` (`reservation_id`, `user_id`, `location_id`, `check_in`, `check_out`, `room_size_id`, `wifi`, `breakfast`, `parking`)
VALUES('25d3c9b6-f572-4dad-96e2-0b31317759f6', '79657b19-cd22-11ed-a4d2-1735c641f2fc', 3, '2024-04-03', '2024-04-10', 4, 1, 0, 0);

-- create dummy guests, grouped by reservation
INSERT INTO `guests` (guest_id, reservation_id, first_name, last_name)
VALUES(1, '046be693-9145-4d45-a63d-034ae708725a', 'Test', 'User Sr.');
INSERT INTO `guests` (guest_id, reservation_id, first_name, last_name)
VALUES(2, '046be693-9145-4d45-a63d-034ae708725a', 'Jane', 'User');

INSERT INTO `guests` (guest_id, reservation_id, first_name, last_name)
VALUES(3, '179dc8db-a8ae-423e-9a74-e42a44900e65', 'Test', 'User Jr.');
INSERT INTO `guests` (guest_id, reservation_id, first_name, last_name)
VALUES(4, '179dc8db-a8ae-423e-9a74-e42a44900e65', 'Jill', 'User');

INSERT INTO `guests` (guest_id, reservation_id, first_name, last_name)
VALUES(5, '25d3c9b6-f572-4dad-96e2-0b31317759f6', 'Mary', 'User');