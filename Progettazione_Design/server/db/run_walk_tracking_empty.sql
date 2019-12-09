-- phpMyAdmin SQL Dump
-- version 4.9.2
-- https://www.phpmyadmin.net/
--
-- Host: localhost:3306
-- Creato il: Dic 09, 2019 alle 20:09
-- Versione del server: 10.3.16-MariaDB
-- Versione PHP: 7.3.10

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET AUTOCOMMIT = 0;
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `id11865186_runwalktracking`
--
CREATE DATABASE IF NOT EXISTS `id11865186_runwalktracking` DEFAULT CHARACTER SET utf8 COLLATE utf8_unicode_ci;
USE `id11865186_runwalktracking`;

-- --------------------------------------------------------

--
-- Struttura della tabella `check_registration`
--

CREATE TABLE `check_registration` (
  `id_user` int(11) NOT NULL,
  `token` varchar(10) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Struttura della tabella `login`
--

CREATE TABLE `login` (
  `id_user` int(11) NOT NULL,
  `username` varchar(50) NOT NULL,
  `hash_password` char(60) NOT NULL,
  `date` datetime DEFAULT NULL,
  `id_phone` char(15) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Struttura della tabella `profile_image`
--

CREATE TABLE `profile_image` (
  `id_user` int(11) NOT NULL,
  `img_encode` longblob NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Struttura della tabella `request_forgot_password`
--

CREATE TABLE `request_forgot_password` (
  `email` varchar(30) NOT NULL,
  `c_key` text NOT NULL,
  `end_validity` datetime NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Struttura della tabella `sport_default`
--

CREATE TABLE `sport_default` (
  `id_user` int(11) NOT NULL,
  `sport` varchar(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Struttura della tabella `target_default`
--

CREATE TABLE `target_default` (
  `id_user` int(11) NOT NULL,
  `target` varchar(30) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Trigger `target_default`
--
DELIMITER $$
CREATE TRIGGER `after_insert_target_default` AFTER INSERT ON `target_default` FOR EACH ROW BEGIN
DECLARE run_sport varchar(20) DEFAULT "RUN";
DECLARE walk_sport varchar(20) DEFAULT "WALK";

if new.target="MARATHON"
THEN

	INSERT INTO sport_default (id_user, sport)
    VALUES(new.id_user, run_sport);

ELSEIF new.target="LOSE_WEIGHT"
THEN

	INSERT INTO sport_default (id_user, sport)
    VALUES(new.id_user, walk_sport);

end if;

END
$$
DELIMITER ;

-- --------------------------------------------------------

--
-- Struttura della tabella `unit_measure_default`
--

CREATE TABLE `unit_measure_default` (
  `id_user` int(11) NOT NULL,
  `energy` varchar(20) NOT NULL DEFAULT 'KILO_CALORIES',
  `weight` varchar(20) NOT NULL DEFAULT 'KILOGRAM',
  `distance` varchar(20) NOT NULL DEFAULT 'KILOMETER',
  `height` varchar(20) NOT NULL DEFAULT 'METER'
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Struttura della tabella `user`
--

CREATE TABLE `user` (
  `id_user` int(11) NOT NULL,
  `name` varchar(20) NOT NULL,
  `last_name` varchar(30) NOT NULL,
  `gender` varchar(10) NOT NULL,
  `birth_date` date NOT NULL,
  `email` varchar(50) NOT NULL,
  `city` varchar(30) NOT NULL,
  `phone` varchar(20) NOT NULL,
  `height` float NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Trigger `user`
--
DELIMITER $$
CREATE TRIGGER `after_insert_user` AFTER INSERT ON `user` FOR EACH ROW BEGIN

INSERT INTO unit_measure_default (id_user) VALUES(new.id_user);

END
$$
DELIMITER ;

-- --------------------------------------------------------

--
-- Struttura della tabella `weight`
--

CREATE TABLE `weight` (
  `id_weight` int(11) NOT NULL,
  `id_user` int(11) NOT NULL,
  `date` date NOT NULL,
  `value` float NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Struttura della tabella `workout`
--

CREATE TABLE `workout` (
  `id_workout` int(11) NOT NULL,
  `id_user` int(11) NOT NULL,
  `map_route` longblob DEFAULT NULL,
  `date` bigint(20) NOT NULL,
  `duration` int(10) UNSIGNED NOT NULL,
  `distance` float NOT NULL DEFAULT 0,
  `calories` float NOT NULL DEFAULT 0,
  `sport` varchar(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Indici per le tabelle scaricate
--

--
-- Indici per le tabelle `check_registration`
--
ALTER TABLE `check_registration`
  ADD PRIMARY KEY (`id_user`),
  ADD UNIQUE KEY `FKachieve_IND` (`id_user`);

--
-- Indici per le tabelle `login`
--
ALTER TABLE `login`
  ADD PRIMARY KEY (`id_user`),
  ADD UNIQUE KEY `SID_Login_ID` (`username`,`hash_password`),
  ADD UNIQUE KEY `SID_Login_IND` (`username`,`hash_password`),
  ADD UNIQUE KEY `FKmake_IND` (`id_user`);

--
-- Indici per le tabelle `profile_image`
--
ALTER TABLE `profile_image`
  ADD PRIMARY KEY (`id_user`),
  ADD UNIQUE KEY `FKhave_IND` (`id_user`);

--
-- Indici per le tabelle `request_forgot_password`
--
ALTER TABLE `request_forgot_password`
  ADD PRIMARY KEY (`email`) USING BTREE;

--
-- Indici per le tabelle `sport_default`
--
ALTER TABLE `sport_default`
  ADD PRIMARY KEY (`id_user`),
  ADD UNIQUE KEY `FKchange_IND` (`id_user`),
  ADD KEY `FKR_1_IND` (`sport`);

--
-- Indici per le tabelle `target_default`
--
ALTER TABLE `target_default`
  ADD PRIMARY KEY (`id_user`),
  ADD UNIQUE KEY `FKpossess_IND` (`id_user`),
  ADD KEY `FKR_IND` (`target`);

--
-- Indici per le tabelle `unit_measure_default`
--
ALTER TABLE `unit_measure_default`
  ADD PRIMARY KEY (`id_user`),
  ADD UNIQUE KEY `FKchoose_1_IND` (`id_user`) USING BTREE;

--
-- Indici per le tabelle `user`
--
ALTER TABLE `user`
  ADD PRIMARY KEY (`id_user`),
  ADD UNIQUE KEY `SID_User_ID` (`name`,`last_name`),
  ADD UNIQUE KEY `ID_User_IND` (`id_user`);

--
-- Indici per le tabelle `weight`
--
ALTER TABLE `weight`
  ADD PRIMARY KEY (`id_weight`) USING BTREE,
  ADD UNIQUE KEY `SID_Weight_IND` (`id_user`,`date`) USING BTREE,
  ADD UNIQUE KEY `ID_Weight_IND` (`id_weight`) USING BTREE;

--
-- Indici per le tabelle `workout`
--
ALTER TABLE `workout`
  ADD PRIMARY KEY (`id_workout`),
  ADD UNIQUE KEY `ID_Workout_IND` (`id_workout`),
  ADD KEY `FKcarry_out_IND` (`id_user`) USING BTREE;

--
-- AUTO_INCREMENT per le tabelle scaricate
--

--
-- AUTO_INCREMENT per la tabella `user`
--
ALTER TABLE `user`
  MODIFY `id_user` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT per la tabella `weight`
--
ALTER TABLE `weight`
  MODIFY `id_weight` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT per la tabella `workout`
--
ALTER TABLE `workout`
  MODIFY `id_workout` int(11) NOT NULL AUTO_INCREMENT;

--
-- Limiti per le tabelle scaricate
--

--
-- Limiti per la tabella `check_registration`
--
ALTER TABLE `check_registration`
  ADD CONSTRAINT `FKachieve_FK` FOREIGN KEY (`id_user`) REFERENCES `user` (`id_user`) ON DELETE CASCADE;

--
-- Limiti per la tabella `login`
--
ALTER TABLE `login`
  ADD CONSTRAINT `FKmake_FK` FOREIGN KEY (`id_user`) REFERENCES `user` (`id_user`) ON DELETE CASCADE;

--
-- Limiti per la tabella `profile_image`
--
ALTER TABLE `profile_image`
  ADD CONSTRAINT `FKhave_FK` FOREIGN KEY (`id_user`) REFERENCES `user` (`id_user`) ON DELETE CASCADE;

--
-- Limiti per la tabella `sport_default`
--
ALTER TABLE `sport_default`
  ADD CONSTRAINT `FKchange_FK` FOREIGN KEY (`id_user`) REFERENCES `user` (`id_user`) ON DELETE CASCADE;

--
-- Limiti per la tabella `target_default`
--
ALTER TABLE `target_default`
  ADD CONSTRAINT `FKpossess_FK` FOREIGN KEY (`id_user`) REFERENCES `user` (`id_user`) ON DELETE CASCADE;

--
-- Limiti per la tabella `unit_measure_default`
--
ALTER TABLE `unit_measure_default`
  ADD CONSTRAINT `FKchoose_1_FK` FOREIGN KEY (`id_user`) REFERENCES `user` (`id_user`) ON DELETE CASCADE;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
