-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: Dec 19, 2024 at 04:11 PM
-- Server version: 10.4.32-MariaDB
-- PHP Version: 8.2.12

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `ikea`
--

-- --------------------------------------------------------

--
-- Table structure for table `admin`
--

CREATE TABLE `admin` (
  `user_id` varchar(50) NOT NULL,
  `name` varchar(100) NOT NULL,
  `password` varchar(100) NOT NULL,
  `email` varchar(100) NOT NULL,
  `phone_num` varchar(15) DEFAULT NULL,
  `income` int(10) UNSIGNED DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `admin`
--

INSERT INTO `admin` (`user_id`, `name`, `password`, `email`, `phone_num`, `income`) VALUES
('A001', 'John Smith', 'hash123', 'john.smith@email.com', '+1234567890', 60000),
('A002', 'Emma Wilson', 'hash456', 'emma.wilson@email.com', '+1234567891', 55000),
('A003', 'Michael Brown', 'hash789', 'michael.brown@email.com', '+1234567892', 58000);

-- --------------------------------------------------------

--
-- Table structure for table `cart`
--

CREATE TABLE `cart` (
  `cart_id` varchar(50) NOT NULL,
  `customer_id` varchar(50) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `cart`
--

INSERT INTO `cart` (`cart_id`, `customer_id`) VALUES
('CART001', 'C001'),
('CART002', 'C002'),
('CART003', 'C003');

-- --------------------------------------------------------

--
-- Table structure for table `cartitem`
--

CREATE TABLE `cartitem` (
  `cart_item_id` varchar(255) NOT NULL,
  `cart_id` varchar(255) NOT NULL,
  `product_id` varchar(255) NOT NULL,
  `quantity` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `cartitem`
--

INSERT INTO `cartitem` (`cart_item_id`, `cart_id`, `product_id`, `quantity`) VALUES
('CI001', 'CART001', 'P001', 2),
('CI002', 'CART001', 'P003', 1),
('CI003', 'CART002', 'P002', 1),
('CI004', 'CART003', 'P004', 3);

-- --------------------------------------------------------

--
-- Table structure for table `category`
--

CREATE TABLE `category` (
  `category_id` varchar(255) NOT NULL,
  `name` varchar(255) NOT NULL,
  `description` longtext DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `category`
--

INSERT INTO `category` (`category_id`, `name`, `description`) VALUES
('CAT001', 'Living Room', 'Furniture and accessories for your living room'),
('CAT002', 'Bedroom', 'Everything you need for a comfortable bedroom'),
('CAT003', 'Kitchen', 'Kitchen furniture and accessories'),
('CAT004', 'Office', 'Home office furniture and accessories');

-- --------------------------------------------------------

--
-- Table structure for table `customer`
--

CREATE TABLE `customer` (
  `user_id` varchar(50) NOT NULL,
  `name` varchar(100) NOT NULL,
  `password` varchar(100) NOT NULL,
  `email` varchar(100) NOT NULL,
  `phone_num` varchar(15) DEFAULT NULL,
  `joined_date` date DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `customer`
--

INSERT INTO `customer` (`user_id`, `name`, `password`, `email`, `phone_num`, `joined_date`) VALUES
('C001', 'Alice Johnson', 'pass123', 'alice.j@email.com', '+1987654321', '2023-01-15'),
('C002', 'Bob Wilson', 'pass456', 'bob.w@email.com', '+1987654322', '2023-02-20'),
('C003', 'Carol Martinez', 'pass789', 'carol.m@email.com', '+1987654323', '2023-03-25'),
('C004', 'David Lee', 'pass101', 'david.l@email.com', '+1987654324', '2023-04-10'),
('C005', 'Eva Chen', 'pass102', 'eva.c@email.com', '+1987654325', '2023-05-05');

-- --------------------------------------------------------

--
-- Table structure for table `notification`
--

CREATE TABLE `notification` (
  `notification_id` varchar(50) NOT NULL,
  `message` varchar(255) NOT NULL,
  `notif_date` date DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `notification`
--

INSERT INTO `notification` (`notification_id`, `message`, `notif_date`) VALUES
('N001', 'New Winter Collection Arrived!', '2024-01-01'),
('N002', 'Special Holiday Discount', '2024-01-15'),
('N003', 'Flash Sale This Weekend', '2024-02-01');

-- --------------------------------------------------------

--
-- Table structure for table `order`
--

CREATE TABLE `order` (
  `order_id` varchar(50) NOT NULL,
  `customer_id` varchar(50) NOT NULL,
  `address` varchar(255) DEFAULT NULL,
  `price` double DEFAULT NULL,
  `cancel_status` enum('cancelled','not_cancelled') DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `order`
--

INSERT INTO `order` (`order_id`, `customer_id`, `address`, `price`, `cancel_status`) VALUES
('O001', 'C001', '123 Main St, City, Country', 329.97, 'not_cancelled'),
('O002', 'C002', '456 Oak St, City, Country', 299.99, 'not_cancelled'),
('O003', 'C003', '789 Pine St, City, Country', 149.97, 'not_cancelled'),
('O004', 'C001', '123 Main St, City, Country', 199.99, 'cancelled');

-- --------------------------------------------------------

--
-- Table structure for table `product`
--

CREATE TABLE `product` (
  `product_id` varchar(255) NOT NULL,
  `name` varchar(255) NOT NULL,
  `price` double DEFAULT NULL,
  `stock` int(11) DEFAULT NULL,
  `category` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `product`
--

INSERT INTO `product` (`product_id`, `name`, `price`, `stock`, `category`) VALUES
('P001', 'BILLY Bookcase', 99.99, 50, 'CAT001'),
('P002', 'MALM Bed Frame', 299.99, 30, 'CAT002'),
('P003', 'POÄNG Armchair', 129.99, 40, 'CAT001'),
('P004', 'LINNMON Desk', 49.99, 60, 'CAT004'),
('P005', 'KALLAX Shelf Unit', 79.99, 45, 'CAT001'),
('P006', 'MARKUS Office Chair', 199.99, 25, 'CAT004');

-- --------------------------------------------------------

--
-- Table structure for table `promo`
--

CREATE TABLE `promo` (
  `promo_id` varchar(255) NOT NULL,
  `name` varchar(255) NOT NULL,
  `description` longtext DEFAULT NULL,
  `discount` float DEFAULT NULL,
  `expired_date` date DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `promo`
--

INSERT INTO `promo` (`promo_id`, `name`, `description`, `discount`, `expired_date`) VALUES
('PROMO001', 'New Year Sale', '20% off on all furniture', 0.2, '2024-01-31'),
('PROMO002', 'Spring Special', '15% off on bedroom items', 0.15, '2024-03-31'),
('PROMO003', 'Welcome Bonus', '10% off on first purchase', 0.1, '2024-12-31');

-- --------------------------------------------------------

--
-- Table structure for table `review`
--

CREATE TABLE `review` (
  `rating_id` varchar(255) NOT NULL,
  `product_id` varchar(255) NOT NULL,
  `customer_id` varchar(255) NOT NULL,
  `rating` float DEFAULT NULL,
  `feedback_text` longtext DEFAULT NULL,
  `review_date` date DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `review`
--

INSERT INTO `review` (`rating_id`, `product_id`, `customer_id`, `rating`, `feedback_text`, `review_date`) VALUES
('R001', 'P001', 'C001', 4.5, 'Great bookcase, easy to assemble!', '2024-01-20'),
('R002', 'P002', 'C002', 5, 'Very sturdy bed frame, excellent quality', '2024-01-25'),
('R003', 'P003', 'C003', 4, 'Comfortable chair, good value', '2024-02-01'),
('R004', 'P004', 'C004', 4.8, 'Perfect desk for my home office', '2024-02-05');

-- --------------------------------------------------------

--
-- Table structure for table `transaction`
--

CREATE TABLE `transaction` (
  `transaction_id` varchar(50) NOT NULL,
  `user_id` varchar(50) DEFAULT NULL,
  `order_id` varchar(50) DEFAULT NULL,
  `promo_id` varchar(50) DEFAULT NULL,
  `discount_earned` double DEFAULT NULL,
  `final_price` double DEFAULT NULL,
  `payment_method` varchar(50) DEFAULT NULL,
  `transaction_status` enum('pending','completed','failed') DEFAULT NULL,
  `transaction_date` date DEFAULT NULL,
  `return_status` enum('returned','not_returned') DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `transaction`
--

INSERT INTO `transaction` (`transaction_id`, `user_id`, `order_id`, `promo_id`, `discount_earned`, `final_price`, `payment_method`, `transaction_status`, `transaction_date`, `return_status`) VALUES
('T001', 'C001', 'O001', 'PROMO001', 65.99, 263.98, 'credit_card', 'completed', '2024-01-20', 'not_returned'),
('T002', 'C002', 'O002', 'PROMO002', 45, 254.99, 'debit_card', 'completed', '2024-01-25', 'not_returned'),
('T003', 'C003', 'O003', NULL, 0, 149.97, 'paypal', 'completed', '2024-02-01', 'not_returned'),
('T004', 'C001', 'O004', NULL, 0, 199.99, 'credit_card', 'failed', '2024-02-05', 'not_returned');

-- --------------------------------------------------------

--
-- Table structure for table `warehouse`
--

CREATE TABLE `warehouse` (
  `warehouse_id` varchar(255) NOT NULL,
  `warehouse_name` varchar(255) NOT NULL,
  `address` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `warehouse`
--

INSERT INTO `warehouse` (`warehouse_id`, `warehouse_name`, `address`) VALUES
('W001', 'North Distribution Center', '789 Industrial Park, North City'),
('W002', 'South Distribution Center', '456 Warehouse District, South City'),
('W003', 'East Distribution Center', '123 Storage Lane, East City');

-- --------------------------------------------------------

--
-- Table structure for table `wishlist`
--

CREATE TABLE `wishlist` (
  `wishlist_id` varchar(255) NOT NULL,
  `product_id` varchar(255) NOT NULL,
  `user_id` varchar(255) NOT NULL,
  `date_added` date DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `wishlist`
--

INSERT INTO `wishlist` (`wishlist_id`, `product_id`, `user_id`, `date_added`) VALUES
('WL001', 'P001', 'C001', '2024-01-10'),
('WL002', 'P003', 'C002', '2024-01-15'),
('WL003', 'P002', 'C003', '2024-01-20'),
('WL004', 'P004', 'C001', '2024-02-01');

--
-- Indexes for dumped tables
--

--
-- Indexes for table `admin`
--
ALTER TABLE `admin`
  ADD PRIMARY KEY (`user_id`);

--
-- Indexes for table `cart`
--
ALTER TABLE `cart`
  ADD PRIMARY KEY (`cart_id`),
  ADD KEY `customer_id` (`customer_id`);

--
-- Indexes for table `cartitem`
--
ALTER TABLE `cartitem`
  ADD PRIMARY KEY (`cart_item_id`),
  ADD KEY `cart_id` (`cart_id`),
  ADD KEY `product_id` (`product_id`);

--
-- Indexes for table `category`
--
ALTER TABLE `category`
  ADD PRIMARY KEY (`category_id`);

--
-- Indexes for table `customer`
--
ALTER TABLE `customer`
  ADD PRIMARY KEY (`user_id`);

--
-- Indexes for table `notification`
--
ALTER TABLE `notification`
  ADD PRIMARY KEY (`notification_id`);

--
-- Indexes for table `order`
--
ALTER TABLE `order`
  ADD PRIMARY KEY (`order_id`),
  ADD KEY `customer_id` (`customer_id`);

--
-- Indexes for table `product`
--
ALTER TABLE `product`
  ADD PRIMARY KEY (`product_id`);

--
-- Indexes for table `promo`
--
ALTER TABLE `promo`
  ADD PRIMARY KEY (`promo_id`);

--
-- Indexes for table `review`
--
ALTER TABLE `review`
  ADD PRIMARY KEY (`rating_id`),
  ADD KEY `product_id` (`product_id`),
  ADD KEY `customer_id` (`customer_id`);

--
-- Indexes for table `transaction`
--
ALTER TABLE `transaction`
  ADD PRIMARY KEY (`transaction_id`);

--
-- Indexes for table `warehouse`
--
ALTER TABLE `warehouse`
  ADD PRIMARY KEY (`warehouse_id`);

--
-- Indexes for table `wishlist`
--
ALTER TABLE `wishlist`
  ADD PRIMARY KEY (`wishlist_id`),
  ADD KEY `product_id` (`product_id`),
  ADD KEY `user_id` (`user_id`);

--
-- Constraints for dumped tables
--

--
-- Constraints for table `cart`
--
ALTER TABLE `cart`
  ADD CONSTRAINT `cart_ibfk_1` FOREIGN KEY (`customer_id`) REFERENCES `customer` (`user_id`);

--
-- Constraints for table `cartitem`
--
ALTER TABLE `cartitem`
  ADD CONSTRAINT `cartitem_ibfk_1` FOREIGN KEY (`cart_id`) REFERENCES `cart` (`cart_id`),
  ADD CONSTRAINT `cartitem_ibfk_2` FOREIGN KEY (`product_id`) REFERENCES `product` (`product_id`);

--
-- Constraints for table `order`
--
ALTER TABLE `order`
  ADD CONSTRAINT `order_ibfk_1` FOREIGN KEY (`customer_id`) REFERENCES `customer` (`user_id`);

--
-- Constraints for table `review`
--
ALTER TABLE `review`
  ADD CONSTRAINT `review_ibfk_1` FOREIGN KEY (`product_id`) REFERENCES `product` (`product_id`),
  ADD CONSTRAINT `review_ibfk_2` FOREIGN KEY (`customer_id`) REFERENCES `customer` (`user_id`);

--
-- Constraints for table `wishlist`
--
ALTER TABLE `wishlist`
  ADD CONSTRAINT `wishlist_ibfk_1` FOREIGN KEY (`product_id`) REFERENCES `product` (`product_id`),
  ADD CONSTRAINT `wishlist_ibfk_2` FOREIGN KEY (`user_id`) REFERENCES `customer` (`user_id`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
