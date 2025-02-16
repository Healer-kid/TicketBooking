# Ticket Booking

A simple train ticket booking application built using **Java** with an embedded HTTP server and MySQL database.

## Features

- **Book Train Tickets**: Reserve a train ticket by providing train number, passenger name, and age.
- **Check PNR Status**: Retrieve ticket details using a PNR number.

## Project Structure

- **DatabaseHandler**: Handles database connections.
- **TrainBookingServer**: Starts an HTTP server using `com.sun.net.httpserver.HttpServer`.
- **BookingSystem**: Implements the backend logic for booking and PNR status checking.

## Prerequisites

- **Java 17+** (Make sure it's installed and set up in `PATH`)
- **MySQL Server** (Running and accessible)
- **MySQL JDBC Driver** (Download from [MySQL Connector/J](https://dev.mysql.com/downloads/connector/j/))

## Database Setup

1. Start MySQL and create the required database:

```sql
CREATE DATABASE TrainBookingDB;
USE TrainBookingDB;

 CREATE TABLE `ticket` (
  `pnr` int NOT NULL AUTO_INCREMENT,
  `train_number` int DEFAULT NULL,
  `passenger_name` varchar(50) DEFAULT NULL,
  `age` int DEFAULT NULL,
  `status` enum('CONFIRMED','WAITLISTED','CANCELLED') DEFAULT NULL,
  PRIMARY KEY (`pnr`),
  KEY `train_number` (`train_number`),
  CONSTRAINT `ticket_ibfk_1` FOREIGN KEY (`train_number`) REFERENCES `train` (`train_number`)
)

 CREATE TABLE `train` (
  `train_number` int NOT NULL,
  `train_name` varchar(50) DEFAULT NULL,
  `source` varchar(50) DEFAULT NULL,
  `destination` varchar(50) DEFAULT NULL,
  `available_seats` int DEFAULT NULL,
  PRIMARY KEY (`train_number`)
) 
```


## How to Run

1. **Compile the Java files**:

```sh
javac -cp "C:\Downloads\mysql-connector-j-9.2.0\mysql-connector-j-9.2.0.jar;." *.java
```

2. **Run the Server**:

```sh
java -cp "C:\Downloads\mysql-connector-j-9.2.0\mysql-connector-j-9.2.0.jar;." TrainBookingServer
```

3. **Test API Endpoints**:

- **Book a Ticket** (POST Request):

```sh
curl -X POST -d "trainNumber=101&name=John Doe&age=25" http://localhost:8080/book
```

- **Check PNR Status** (GET Request):

```sh
curl -X GET http://localhost:8080/pnr?pnr=1
```

## Future Enhancements

- Implement a frontend UI.
- Add authentication for secure booking.
- Improve error handling.


