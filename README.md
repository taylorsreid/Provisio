# Welcome to Provisio!

## Who?
- Taylor Reid
- Julia Delightly
- Nathan Mausbach
- Jessica Phan
- Kendrick Baker

## Why?
This is a collaborative project created to fulfill the requirements of the capstone course for Bellevue University's Bachelor's of Science in Software Development.

## What?
Provisio is an interactive website for a chain of luxury hotels. From this website users are able to register for an account, login, logout, book reservations, lookup previous reservations, and view their loyalty program points.

## How?
The backend of Provisio is written as a REST API that returns JSON using Spring Boot with Java 17, and is built using Maven.  Authentication is accomplished using JWTs.  MySQL is used as the database engine, and the SQL is hand written and is connected to Spring Boot using JDBC, without the assistance of any ORM tools.
In the MySQL database you can find tables holding user account data (with hashed passwords), reservation info, prices, charges, etc.

The frontend is written in plain HTML, Javascript, and CSS. Connections to the backend are made using calls from Javascript's Fetch API.  All content on the demo site listed below is served securely over HTTPS using the free Let's Encrypt certbot tool.

## Where?
A live version of this website is available at https://taylorsreid.com/Provisio/
