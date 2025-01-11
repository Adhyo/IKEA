-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: Jan 11, 2025 at 04:02 AM
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
-- Table structure for table `carts`
--

CREATE TABLE `carts` (
  `cart_id` int(11) NOT NULL,
  `user_id` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `carts`
--

INSERT INTO `carts` (`cart_id`, `user_id`) VALUES
(3, 2),
(1, 3),
(4, 3),
(5, 3),
(6, 3),
(7, 3),
(8, 3),
(2, 5),
(9, 10),
(10, 10),
(11, 10),
(12, 10),
(13, 10),
(14, 10),
(15, 10),
(17, 11),
(18, 11);

-- --------------------------------------------------------

--
-- Table structure for table `cart_products`
--

CREATE TABLE `cart_products` (
  `cart_id` int(11) NOT NULL,
  `product_id` int(11) NOT NULL,
  `quantity` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `cart_products`
--

INSERT INTO `cart_products` (`cart_id`, `product_id`, `quantity`) VALUES
(1, 8, 4),
(2, 9, 4),
(3, 7, 3),
(4, 7, 2),
(5, 7, 2),
(6, 8, 2),
(7, 7, 3),
(8, 7, 3),
(9, 7, 1),
(10, 7, 1),
(11, 7, 1),
(11, 8, 1),
(12, 7, 4),
(13, 7, 1),
(14, 8, 1),
(15, 7, 1),
(17, 8, 2),
(18, 9, 1);

-- --------------------------------------------------------

--
-- Table structure for table `categories`
--

CREATE TABLE `categories` (
  `category_id` int(11) NOT NULL,
  `category_name` varchar(50) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `categories`
--

INSERT INTO `categories` (`category_id`, `category_name`) VALUES
(1, 'Furniture'),
(2, 'Storage & Organization'),
(3, 'Beds & Mattresses'),
(4, 'Kitchen & Appliances'),
(5, 'Working from Home'),
(6, 'Decoration'),
(7, 'Lighting'),
(8, 'Textiles'),
(9, 'Bathroom Products');

-- --------------------------------------------------------

--
-- Table structure for table `notifications`
--

CREATE TABLE `notifications` (
  `notification_id` int(11) NOT NULL,
  `user_id` int(11) NOT NULL,
  `message` text NOT NULL,
  `created_at` timestamp NOT NULL DEFAULT current_timestamp(),
  `is_read` tinyint(1) NOT NULL DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `notifications`
--

INSERT INTO `notifications` (`notification_id`, `user_id`, `message`, `created_at`, `is_read`) VALUES
(1, 10, 'testing maseh', '2025-01-10 18:12:01', 1),
(2, 10, 'hello', '2025-01-10 18:45:03', 1),
(3, 11, 'judgement day', '2025-01-11 02:39:06', 1);

-- --------------------------------------------------------

--
-- Table structure for table `orders`
--

CREATE TABLE `orders` (
  `order_id` int(11) NOT NULL,
  `cart_id` int(11) NOT NULL,
  `address` text DEFAULT NULL,
  `price` double NOT NULL,
  `status` enum('UNPAID','PAID','CANCELLED') DEFAULT NULL,
  `promo_applied` varchar(50) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `orders`
--

INSERT INTO `orders` (`order_id`, `cart_id`, `address`, `price`, `status`, `promo_applied`) VALUES
(1, 1, 'Kocak gaming', 180, 'UNPAID', NULL),
(2, 4, 'du', 41, 'UNPAID', NULL),
(3, 5, 'sekeloah', 41, 'UNPAID', NULL),
(4, 6, 'anjay', 90, 'UNPAID', NULL),
(5, 7, 'rumah tata', 49.2, 'UNPAID', 'FLASH_DEAL'),
(6, 8, 'asik', 49.2, 'UNPAID', 'FLASH_DEAL'),
(7, 9, 'bandung', 20.5, 'UNPAID', 'No Promo'),
(8, 10, 'bandung', 20.5, 'UNPAID', 'No Promo'),
(9, 11, 'testing', 65.5, 'UNPAID', 'No Promo'),
(10, 12, 'testing history', 82, 'UNPAID', 'No Promo'),
(11, 13, 'tst', 20.5, 'UNPAID', 'No Promo'),
(12, 14, 'helo', 45, 'UNPAID', 'No Promo'),
(13, 15, 'helo', 18.45, 'UNPAID', 'WELCOME_BONUS'),
(16, 17, 'bandung', 72, 'UNPAID', 'FLASH_DEAL'),
(17, 18, 'bandung', 11.25, 'PAID', 'NEW_YEAR_2025');

-- --------------------------------------------------------

--
-- Table structure for table `products`
--

CREATE TABLE `products` (
  `product_id` int(11) NOT NULL,
  `name` varchar(100) NOT NULL,
  `description` text DEFAULT NULL,
  `price` double NOT NULL,
  `stock_quantity` int(11) NOT NULL,
  `category_id` int(11) NOT NULL,
  `discount_price` double DEFAULT 0,
  `image_url` varchar(255) NOT NULL,
  `warehouse_id` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `products`
--

INSERT INTO `products` (`product_id`, `name`, `description`, `price`, `stock_quantity`, `category_id`, `discount_price`, `image_url`, `warehouse_id`) VALUES
(7, 'Chair', 'Comfortable wooden chair', 20.5, 100, 4, 0, '', 1),
(8, 'Table', 'Wooden dining table', 45, 50, 4, 0, '', 1),
(9, 'Lamp', 'Stylish desk lamp', 15, 200, 7, 0, '', 2),
(16, 'final', 'final test', 100, 1000, 1, 0, '86dc8e1a-5a15-43dc-be6f-e5584bc8d4c5.png', NULL);

-- --------------------------------------------------------

--
-- Table structure for table `promos`
--

CREATE TABLE `promos` (
  `promo_id` int(11) NOT NULL,
  `promo_name` varchar(50) DEFAULT NULL,
  `description` text DEFAULT NULL,
  `discount` double NOT NULL,
  `expiration_date` date DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `promos`
--

INSERT INTO `promos` (`promo_id`, `promo_name`, `description`, `discount`, `expiration_date`) VALUES
(1, 'NEW_YEAR_2025', 'Special New Year Discount 25% Off', 25, '2025-01-31'),
(2, 'WELCOME_BONUS', 'Welcome Discount 10% for New Users', 10, '2025-12-31'),
(3, 'SUMMER_SALE', 'Summer Season Sale 15% Off', 15, '2025-06-30'),
(4, 'FLASH_DEAL', 'Flash Deal 20% Off for Limited Time', 20, '2025-01-15');

-- --------------------------------------------------------

--
-- Table structure for table `return_requests`
--

CREATE TABLE `return_requests` (
  `id` int(11) NOT NULL,
  `transaction_id` int(11) NOT NULL,
  `user_id` int(11) NOT NULL,
  `request_date` date NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `return_requests`
--

INSERT INTO `return_requests` (`id`, `transaction_id`, `user_id`, `request_date`) VALUES
(1, 13, 10, '2025-01-11'),
(2, 13, 10, '2025-01-11'),
(3, 12, 10, '2025-01-11'),
(4, 16, 11, '2025-01-11');

-- --------------------------------------------------------

--
-- Table structure for table `reviews`
--

CREATE TABLE `reviews` (
  `id` int(11) NOT NULL,
  `user_id` int(11) NOT NULL,
  `transaction_id` int(11) NOT NULL,
  `rating` int(11) NOT NULL CHECK (`rating` >= 1 and `rating` <= 5),
  `review_text` text DEFAULT NULL,
  `review_date` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `reviews`
--

INSERT INTO `reviews` (`id`, `user_id`, `transaction_id`, `rating`, `review_text`, `review_date`) VALUES
(1, 10, 13, 4, 'helo', '2025-01-11 01:55:51'),
(2, 11, 17, 5, 'bagus banget', '2025-01-11 02:40:02');

-- --------------------------------------------------------

--
-- Table structure for table `transactions`
--

CREATE TABLE `transactions` (
  `transaction_id` int(11) NOT NULL,
  `cart_id` int(11) NOT NULL,
  `promo_id` int(11) DEFAULT NULL,
  `sub_total` double NOT NULL,
  `final_amount` double NOT NULL,
  `transaction_date` date NOT NULL,
  `discount_amount` double DEFAULT 0,
  `user_id` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `transactions`
--

INSERT INTO `transactions` (`transaction_id`, `cart_id`, `promo_id`, `sub_total`, `final_amount`, `transaction_date`, `discount_amount`, `user_id`) VALUES
(1, 1, NULL, 180, 180, '2025-01-06', 0, NULL),
(2, 4, NULL, 41, 41, '2025-01-06', 0, NULL),
(3, 5, NULL, 41, 41, '2025-01-09', 0, NULL),
(4, 6, NULL, 90, 90, '2025-01-09', 0, NULL),
(5, 7, NULL, 61.5, 49.2, '2025-01-09', 12.3, NULL),
(6, 8, NULL, 61.5, 49.2, '2025-01-10', 12.3, NULL),
(7, 9, NULL, 20.5, 20.5, '2025-01-11', 0, NULL),
(8, 10, NULL, 20.5, 20.5, '2025-01-11', 0, NULL),
(9, 11, NULL, 65.5, 65.5, '2025-01-11', 0, 10),
(10, 12, NULL, 82, 82, '2025-01-11', 0, 10),
(11, 13, NULL, 20.5, 20.5, '2025-01-11', 0, 10),
(12, 14, NULL, 45, 45, '2025-01-11', 0, 10),
(13, 15, NULL, 20.5, 18.45, '2025-01-11', 2.0500000000000003, 10),
(16, 17, NULL, 90, 72, '2025-01-11', 18, 11),
(17, 18, NULL, 15, 11.25, '2025-01-11', 3.75, 11);

-- --------------------------------------------------------

--
-- Table structure for table `users`
--

CREATE TABLE `users` (
  `user_id` int(11) NOT NULL,
  `username` varchar(50) NOT NULL,
  `password` varchar(255) NOT NULL,
  `email` varchar(100) NOT NULL,
  `user_type` enum('ADMIN','CUSTOMER') NOT NULL,
  `name` varchar(100) DEFAULT NULL,
  `phone` varchar(15) DEFAULT NULL,
  `address` text DEFAULT NULL,
  `income` double DEFAULT 0,
  `is_active` int(1) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `users`
--

INSERT INTO `users` (`user_id`, `username`, `password`, `email`, `user_type`, `name`, `phone`, `address`, `income`, `is_active`) VALUES
(1, 'admin', 'admin123', 'admin@ikea.com', 'ADMIN', NULL, NULL, NULL, 50000, 0),
(2, 'customer1', 'cust123', 'customer1@ikea.com', 'CUSTOMER', 'John Doe', '123456789', NULL, 0, 1),
(3, 'rudicc', 'rudicc123', 'rudicc@gmail.com', 'CUSTOMER', 'rudiccon', '08172128382', NULL, 0, 0),
(4, 'tata', 'tata@gmail.com', '111', 'CUSTOMER', 'tatababiwoi', '1234567890', NULL, 0, 0),
(5, 'ko dion', 'kodion123', 'kodion@gmail.com', 'CUSTOMER', 'kodionif', '123456789', NULL, 0, 0),
(6, 'tatalagi2', 'tataa2@gmail.com', '1234', 'CUSTOMER', 'Prabs', '08776', NULL, 0, 0),
(7, 'testing', 'testing@gmail.com', '112233', 'CUSTOMER', 'testingOnly', '085', NULL, 0, 1),
(8, 'haiz', 'hais@gmail.com', '556', 'CUSTOMER', 'hhhh', '088', NULL, 0, 0),
(9, 'hahaha', 'hah@gmail.com', 'heh', 'CUSTOMER', 'lmao', '09', NULL, 0, 1),
(10, 'finn', '12332', 'fin@gmail.com', 'CUSTOMER', 'finish', '099', NULL, 0, 0),
(11, 'finaltest', '333', 'finaltest@gmail.com', 'CUSTOMER', 'final testing', '37', NULL, 0, 0);

-- --------------------------------------------------------

--
-- Table structure for table `warehouses`
--

CREATE TABLE `warehouses` (
  `warehouse_id` int(11) NOT NULL,
  `name` varchar(100) NOT NULL,
  `address` text NOT NULL,
  `phone` varchar(15) DEFAULT NULL,
  `operational_status` enum('ACTIVE','INACTIVE') DEFAULT 'ACTIVE'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `warehouses`
--

INSERT INTO `warehouses` (`warehouse_id`, `name`, `address`, `phone`, `operational_status`) VALUES
(1, 'IKEA North Jakarta', 'Jl. Boulevard Kelapa Gading, North Jakarta', '021-45678901', 'ACTIVE'),
(2, 'IKEA South Jakarta', 'Jl. TB Simatupang, South Jakarta', '021-78901234', 'ACTIVE'),
(3, 'IKEA Tangerang', 'Jl. BSD Raya Utama, Tangerang', '021-89012345', 'ACTIVE');

-- --------------------------------------------------------

--
-- Table structure for table `wishlists`
--

CREATE TABLE `wishlists` (
  `wishlist_id` int(11) NOT NULL,
  `user_id` int(11) NOT NULL,
  `product_id` int(11) NOT NULL,
  `date_added` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Indexes for dumped tables
--

--
-- Indexes for table `carts`
--
ALTER TABLE `carts`
  ADD PRIMARY KEY (`cart_id`),
  ADD KEY `user_id` (`user_id`);

--
-- Indexes for table `cart_products`
--
ALTER TABLE `cart_products`
  ADD PRIMARY KEY (`cart_id`,`product_id`),
  ADD KEY `product_id` (`product_id`);

--
-- Indexes for table `categories`
--
ALTER TABLE `categories`
  ADD PRIMARY KEY (`category_id`);

--
-- Indexes for table `notifications`
--
ALTER TABLE `notifications`
  ADD PRIMARY KEY (`notification_id`),
  ADD KEY `user_id` (`user_id`);

--
-- Indexes for table `orders`
--
ALTER TABLE `orders`
  ADD PRIMARY KEY (`order_id`),
  ADD KEY `cart_id` (`cart_id`);

--
-- Indexes for table `products`
--
ALTER TABLE `products`
  ADD PRIMARY KEY (`product_id`),
  ADD KEY `category_id` (`category_id`),
  ADD KEY `products_warehouse_fk` (`warehouse_id`);

--
-- Indexes for table `promos`
--
ALTER TABLE `promos`
  ADD PRIMARY KEY (`promo_id`);

--
-- Indexes for table `return_requests`
--
ALTER TABLE `return_requests`
  ADD PRIMARY KEY (`id`),
  ADD KEY `transaction_id` (`transaction_id`),
  ADD KEY `user_id` (`user_id`);

--
-- Indexes for table `reviews`
--
ALTER TABLE `reviews`
  ADD PRIMARY KEY (`id`),
  ADD KEY `user_id` (`user_id`),
  ADD KEY `transaction_id` (`transaction_id`);

--
-- Indexes for table `transactions`
--
ALTER TABLE `transactions`
  ADD PRIMARY KEY (`transaction_id`),
  ADD KEY `cart_id` (`cart_id`),
  ADD KEY `transactions_promo_id_fk` (`promo_id`),
  ADD KEY `fk_user_id` (`user_id`);

--
-- Indexes for table `users`
--
ALTER TABLE `users`
  ADD PRIMARY KEY (`user_id`),
  ADD UNIQUE KEY `username` (`username`);

--
-- Indexes for table `warehouses`
--
ALTER TABLE `warehouses`
  ADD PRIMARY KEY (`warehouse_id`);

--
-- Indexes for table `wishlists`
--
ALTER TABLE `wishlists`
  ADD PRIMARY KEY (`wishlist_id`),
  ADD UNIQUE KEY `unique_user_product` (`user_id`,`product_id`),
  ADD KEY `product_id` (`product_id`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `carts`
--
ALTER TABLE `carts`
  MODIFY `cart_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=19;

--
-- AUTO_INCREMENT for table `categories`
--
ALTER TABLE `categories`
  MODIFY `category_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=10;

--
-- AUTO_INCREMENT for table `notifications`
--
ALTER TABLE `notifications`
  MODIFY `notification_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=4;

--
-- AUTO_INCREMENT for table `orders`
--
ALTER TABLE `orders`
  MODIFY `order_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=18;

--
-- AUTO_INCREMENT for table `products`
--
ALTER TABLE `products`
  MODIFY `product_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=17;

--
-- AUTO_INCREMENT for table `promos`
--
ALTER TABLE `promos`
  MODIFY `promo_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=6;

--
-- AUTO_INCREMENT for table `return_requests`
--
ALTER TABLE `return_requests`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=5;

--
-- AUTO_INCREMENT for table `reviews`
--
ALTER TABLE `reviews`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=3;

--
-- AUTO_INCREMENT for table `transactions`
--
ALTER TABLE `transactions`
  MODIFY `transaction_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=18;

--
-- AUTO_INCREMENT for table `users`
--
ALTER TABLE `users`
  MODIFY `user_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=12;

--
-- AUTO_INCREMENT for table `warehouses`
--
ALTER TABLE `warehouses`
  MODIFY `warehouse_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=4;

--
-- AUTO_INCREMENT for table `wishlists`
--
ALTER TABLE `wishlists`
  MODIFY `wishlist_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=2;

--
-- Constraints for dumped tables
--

--
-- Constraints for table `carts`
--
ALTER TABLE `carts`
  ADD CONSTRAINT `carts_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`);

--
-- Constraints for table `cart_products`
--
ALTER TABLE `cart_products`
  ADD CONSTRAINT `cart_products_ibfk_1` FOREIGN KEY (`cart_id`) REFERENCES `carts` (`cart_id`),
  ADD CONSTRAINT `cart_products_ibfk_2` FOREIGN KEY (`product_id`) REFERENCES `products` (`product_id`);

--
-- Constraints for table `notifications`
--
ALTER TABLE `notifications`
  ADD CONSTRAINT `notifications_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`);

--
-- Constraints for table `orders`
--
ALTER TABLE `orders`
  ADD CONSTRAINT `orders_ibfk_1` FOREIGN KEY (`cart_id`) REFERENCES `carts` (`cart_id`);

--
-- Constraints for table `products`
--
ALTER TABLE `products`
  ADD CONSTRAINT `products_warehouse_fk` FOREIGN KEY (`warehouse_id`) REFERENCES `warehouses` (`warehouse_id`) ON DELETE SET NULL;

--
-- Constraints for table `return_requests`
--
ALTER TABLE `return_requests`
  ADD CONSTRAINT `return_requests_ibfk_1` FOREIGN KEY (`transaction_id`) REFERENCES `transactions` (`transaction_id`),
  ADD CONSTRAINT `return_requests_ibfk_2` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`);

--
-- Constraints for table `reviews`
--
ALTER TABLE `reviews`
  ADD CONSTRAINT `reviews_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`),
  ADD CONSTRAINT `reviews_ibfk_2` FOREIGN KEY (`transaction_id`) REFERENCES `transactions` (`transaction_id`);

--
-- Constraints for table `transactions`
--
ALTER TABLE `transactions`
  ADD CONSTRAINT `fk_user_id` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`);

--
-- Constraints for table `wishlists`
--
ALTER TABLE `wishlists`
  ADD CONSTRAINT `wishlists_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`),
  ADD CONSTRAINT `wishlists_ibfk_2` FOREIGN KEY (`product_id`) REFERENCES `products` (`product_id`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
