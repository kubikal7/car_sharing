-- MySQL dump 10.13  Distrib 8.0.40, for Win64 (x86_64)
--
-- Host: localhost    Database: car_sharing
-- ------------------------------------------------------
-- Server version	8.0.40

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `car_service_history`
--

DROP TABLE IF EXISTS `car_service_history`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `car_service_history` (
  `id` int NOT NULL AUTO_INCREMENT,
  `car_id` varchar(16) DEFAULT NULL,
  `start_date` datetime DEFAULT NULL,
  `end_date` datetime DEFAULT NULL,
  `problem` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_service_cars_idx` (`car_id`),
  CONSTRAINT `fk_service_cars` FOREIGN KEY (`car_id`) REFERENCES `cars` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `car_service_history`
--

LOCK TABLES `car_service_history` WRITE;
/*!40000 ALTER TABLE `car_service_history` DISABLE KEYS */;
INSERT INTO `car_service_history` VALUES (1,'DW123456','2024-12-24 10:00:00','2024-12-24 12:00:00','Brakes not working');
/*!40000 ALTER TABLE `car_service_history` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `car_type`
--

DROP TABLE IF EXISTS `car_type`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `car_type` (
  `id` int NOT NULL AUTO_INCREMENT,
  `brand` varchar(64) DEFAULT NULL,
  `model` varchar(64) DEFAULT NULL,
  `nr_of_seats` int DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `id_UNIQUE` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `car_type`
--

LOCK TABLES `car_type` WRITE;
/*!40000 ALTER TABLE `car_type` DISABLE KEYS */;
INSERT INTO `car_type` VALUES (1,'Honda','Civic',5),(2,'Toyota','RAV4',5),(4,'Opel','Astra',5);
/*!40000 ALTER TABLE `car_type` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `cars`
--

DROP TABLE IF EXISTS `cars`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `cars` (
  `id` varchar(16) NOT NULL,
  `car_type_id` int DEFAULT NULL,
  `year` int DEFAULT NULL,
  `color` varchar(32) DEFAULT NULL,
  `price_per_day` double DEFAULT NULL,
  `status` enum('rent','available','not') DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_cars_car_type_idx` (`car_type_id`),
  CONSTRAINT `fk_cars_car_type` FOREIGN KEY (`car_type_id`) REFERENCES `car_type` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `cars`
--

LOCK TABLES `cars` WRITE;
/*!40000 ALTER TABLE `cars` DISABLE KEYS */;
INSERT INTO `cars` VALUES ('DW123456',1,2002,'blue',1080,'not');
/*!40000 ALTER TABLE `cars` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `details_of_transaction`
--

DROP TABLE IF EXISTS `details_of_transaction`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `details_of_transaction` (
  `id` int NOT NULL AUTO_INCREMENT,
  `start_date` datetime DEFAULT NULL,
  `end_date` datetime DEFAULT NULL,
  `user_id` int DEFAULT NULL,
  `car_id` varchar(16) DEFAULT NULL,
  `price` double DEFAULT NULL,
  `status` enum('finished','canceled','during') DEFAULT NULL,
  `payment_id` int DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `payment_id_UNIQUE` (`payment_id`),
  KEY `fk_details_payments_idx` (`payment_id`),
  KEY `fk_details_cars_idx` (`car_id`),
  KEY `fk_details_users_idx` (`user_id`),
  CONSTRAINT `fk_details_cars` FOREIGN KEY (`car_id`) REFERENCES `cars` (`id`),
  CONSTRAINT `fk_details_payments` FOREIGN KEY (`payment_id`) REFERENCES `payment` (`id`),
  CONSTRAINT `fk_details_users` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `details_of_transaction`
--

LOCK TABLES `details_of_transaction` WRITE;
/*!40000 ALTER TABLE `details_of_transaction` DISABLE KEYS */;
/*!40000 ALTER TABLE `details_of_transaction` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `payment`
--

DROP TABLE IF EXISTS `payment`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `payment` (
  `id` int NOT NULL AUTO_INCREMENT,
  `datails_of_transaction_id` int DEFAULT NULL,
  `user_id` int DEFAULT NULL,
  `date` datetime DEFAULT NULL,
  `type` enum('online','card','cash') DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `datails_of_transaction_id_UNIQUE` (`datails_of_transaction_id`),
  KEY `fk_payment_users_idx` (`user_id`),
  CONSTRAINT `fk_payment_details` FOREIGN KEY (`datails_of_transaction_id`) REFERENCES `details_of_transaction` (`id`),
  CONSTRAINT `fk_payment_users` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `payment`
--

LOCK TABLES `payment` WRITE;
/*!40000 ALTER TABLE `payment` DISABLE KEYS */;
/*!40000 ALTER TABLE `payment` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `users`
--

DROP TABLE IF EXISTS `users`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `users` (
  `id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(64) DEFAULT NULL,
  `surname` varchar(64) DEFAULT NULL,
  `email` varchar(64) DEFAULT NULL,
  `password` varchar(255) DEFAULT NULL,
  `date_of_birth` date DEFAULT NULL,
  `country` varchar(64) DEFAULT NULL,
  `role` enum('admin','user') DEFAULT NULL,
  `token` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `users`
--

LOCK TABLES `users` WRITE;
/*!40000 ALTER TABLE `users` DISABLE KEYS */;
INSERT INTO `users` VALUES (2,'root','root','root','$2a$10$sx6D4p5DObRUAx7t.WWIQu6SiZokWjcbMkpsivmcaKm0ZJsNf82UW',NULL,NULL,'admin','417be1fc-60e2-49c4-96c8-47689ca3ebec'),(3,'root','root','rootTest','$2a$10$BRKzyEz1lv7X//pTSsJ4EeJWcQsQ4aUKLydAKy7LbCuTMXTt6xIqi',NULL,NULL,'admin',NULL),(4,'user','user','user','$2a$10$fR.ix92JOZg5jU8l9y5Hq.bQi4a1x1aEf6D2hryH6HxWukksKMEum',NULL,NULL,'user',NULL);
/*!40000 ALTER TABLE `users` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2024-12-28 11:29:59
