-- phpMyAdmin SQL Dump
-- version 4.9.4
-- https://www.phpmyadmin.net/
--
-- Host: localhost:3306
-- Creato il: Feb 18, 2020 alle 15:09
-- Versione del server: 10.3.16-MariaDB
-- Versione PHP: 7.3.12

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
-- Struttura della tabella `login`
--

CREATE TABLE `login` (
  `id_user` int(11) NOT NULL,
  `username` varchar(50) NOT NULL,
  `hash_password` char(60) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Struttura della tabella `profile_image`
--

CREATE TABLE `profile_image` (
  `id_user` int(11) NOT NULL,
  `content` longblob NOT NULL,
  `name` char(24) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Struttura della tabella `request_forgot_password`
--

CREATE TABLE `request_forgot_password` (
  `email` varchar(50) NOT NULL,
  `c_key` char(10) NOT NULL,
  `end_validity` datetime NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Struttura della tabella `session`
--

CREATE TABLE `session` (
  `id_user` int(11) NOT NULL,
  `device` char(32) DEFAULT NULL,
  `token` char(100) NOT NULL,
  `last_update` bigint(20) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Struttura della tabella `signup`
--

CREATE TABLE `signup` (
  `id_user` int(11) NOT NULL,
  `token` char(10) NOT NULL,
  `date` bigint(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Struttura della tabella `sport_default`
--

CREATE TABLE `sport_default` (
  `id_user` int(11) NOT NULL,
  `sport` set('WALK','RUN') NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Struttura della tabella `target_default`
--

CREATE TABLE `target_default` (
  `id_user` int(11) NOT NULL,
  `target` set('MARATHON','LOSE_WEIGHT') NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Trigger `target_default`
--
DELIMITER $$
CREATE TRIGGER `after_insert_target` AFTER INSERT ON `target_default` FOR EACH ROW BEGIN
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
  `energy` enum('KILO_CALORIES') NOT NULL DEFAULT 'KILO_CALORIES',
  `weight` set('KILOGRAM','POUND') NOT NULL DEFAULT 'KILOGRAM',
  `distance` set('KILOMETER','MILE') NOT NULL DEFAULT 'KILOMETER',
  `height` set('METER','FEET') NOT NULL DEFAULT 'METER'
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
  `phone` varchar(20) NOT NULL,
  `city` varchar(30) NOT NULL,
  `height` float NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Trigger `user`
--
DELIMITER $$
CREATE TRIGGER `after_insert_user` AFTER INSERT ON `user` FOR EACH ROW BEGIN
INSERT INTO unit_measure_default(id_user) VALUES(new.id_user);
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
  `date` date NOT NULL DEFAULT current_timestamp(),
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
  `duration` float NOT NULL,
  `distance` float DEFAULT NULL,
  `calories` float DEFAULT NULL,
  `sport` varchar(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Indici per le tabelle scaricate
--

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
  ADD PRIMARY KEY (`email`),
  ADD UNIQUE KEY `ID_Request_Forgot_Password_IND` (`email`);

--
-- Indici per le tabelle `session`
--
ALTER TABLE `session`
  ADD PRIMARY KEY (`id_user`),
  ADD UNIQUE KEY `FKreport_IND` (`id_user`),
  ADD UNIQUE KEY `token` (`token`);

--
-- Indici per le tabelle `signup`
--
ALTER TABLE `signup`
  ADD PRIMARY KEY (`id_user`),
  ADD UNIQUE KEY `FKachieve_IND` (`id_user`);

--
-- Indici per le tabelle `sport_default`
--
ALTER TABLE `sport_default`
  ADD PRIMARY KEY (`id_user`),
  ADD UNIQUE KEY `FKchange_IND` (`id_user`);

--
-- Indici per le tabelle `target_default`
--
ALTER TABLE `target_default`
  ADD PRIMARY KEY (`id_user`),
  ADD UNIQUE KEY `FKpossess_IND` (`id_user`);

--
-- Indici per le tabelle `unit_measure_default`
--
ALTER TABLE `unit_measure_default`
  ADD PRIMARY KEY (`id_user`),
  ADD UNIQUE KEY `FKchoose_IND` (`id_user`);

--
-- Indici per le tabelle `user`
--
ALTER TABLE `user`
  ADD PRIMARY KEY (`id_user`),
  ADD UNIQUE KEY `ID_User_IND` (`id_user`),
  ADD UNIQUE KEY `SID_User_ID` (`name`,`last_name`) USING BTREE,
  ADD KEY `SID_User_IND` (`name`,`last_name`) USING BTREE;

--
-- Indici per le tabelle `weight`
--
ALTER TABLE `weight`
  ADD PRIMARY KEY (`id_weight`,`id_user`),
  ADD UNIQUE KEY `SID_Weight_ID` (`id_user`,`date`),
  ADD UNIQUE KEY `ID_Weight_IND` (`id_weight`),
  ADD UNIQUE KEY `SID_Weight_IND` (`id_user`,`date`);

--
-- Indici per le tabelle `workout`
--
ALTER TABLE `workout`
  ADD PRIMARY KEY (`id_workout`,`id_user`),
  ADD KEY `FKcarry_out_IND` (`id_user`),
  ADD KEY `ID_Workout_IND` (`id_workout`) USING BTREE;

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
-- Limiti per la tabella `session`
--
ALTER TABLE `session`
  ADD CONSTRAINT `FKreport_FK` FOREIGN KEY (`id_user`) REFERENCES `user` (`id_user`) ON DELETE CASCADE;

--
-- Limiti per la tabella `signup`
--
ALTER TABLE `signup`
  ADD CONSTRAINT `FKachieve_FK` FOREIGN KEY (`id_user`) REFERENCES `user` (`id_user`) ON DELETE CASCADE;

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
  ADD CONSTRAINT `FKchoose_FK` FOREIGN KEY (`id_user`) REFERENCES `user` (`id_user`) ON DELETE CASCADE;

--
-- Limiti per la tabella `weight`
--
ALTER TABLE `weight`
  ADD CONSTRAINT `FKadd` FOREIGN KEY (`id_user`) REFERENCES `user` (`id_user`) ON DELETE CASCADE;

--
-- Limiti per la tabella `workout`
--
ALTER TABLE `workout`
  ADD CONSTRAINT `FKcarry_out_FK` FOREIGN KEY (`id_user`) REFERENCES `user` (`id_user`) ON DELETE CASCADE;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
