-- phpMyAdmin SQL Dump
-- version 4.9.0.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Creato il: Dic 02, 2019 alle 19:43
-- Versione del server: 10.4.6-MariaDB
-- Versione PHP: 7.3.9

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET AUTOCOMMIT = 0;
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `run_walk_tracking`
--
CREATE DATABASE IF NOT EXISTS `run_walk_tracking` DEFAULT CHARACTER SET latin1 COLLATE latin1_swedish_ci;
USE `run_walk_tracking`;

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
-- Struttura della tabella `language_default`
--

CREATE TABLE `language_default` (
  `id_user` int(11) NOT NULL,
  `language` char(2) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Struttura della tabella `login`
--

CREATE TABLE `login` (
  `id_user` int(11) NOT NULL,
  `username` varchar(50) NOT NULL,
  `hash_password` char(60) NOT NULL,
  `date` datetime DEFAULT NULL
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
-- Struttura della tabella `sport`
--

CREATE TABLE `sport` (
  `id_sport` int(11) NOT NULL,
  `name` varchar(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dump dei dati per la tabella `sport`
--

INSERT INTO `sport` (`id_sport`, `name`) VALUES
(1, 'RUN'),
(2, 'WALK');

-- --------------------------------------------------------

--
-- Struttura della tabella `sport_default`
--

CREATE TABLE `sport_default` (
  `id_user` int(11) NOT NULL,
  `sport` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Struttura della tabella `target`
--

CREATE TABLE `target` (
  `id_target` int(11) NOT NULL,
  `name` varchar(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dump dei dati per la tabella `target`
--

INSERT INTO `target` (`id_target`, `name`) VALUES
(1, 'MARATHON'),
(2, 'LOSE_WEIGHT');

-- --------------------------------------------------------

--
-- Struttura della tabella `target_default`
--

CREATE TABLE `target_default` (
  `id_user` int(11) NOT NULL,
  `target` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Trigger `target_default`
--
DELIMITER $$
CREATE TRIGGER `after_insert_target_default` AFTER INSERT ON `target_default` FOR EACH ROW BEGIN
DECLARE run_sport int DEFAULT 1;
DECLARE walk_sport int DEFAULT 2;

if new.target=1
THEN

	INSERT INTO sport_default (id_user, sport) 
    VALUES(new.id_user, run_sport);

ELSEIF new.target=2
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
  `energy` varchar(4) NOT NULL DEFAULT 'Kcal',
  `weight` varchar(4) NOT NULL DEFAULT 'Kg',
  `distance` varchar(4) NOT NULL DEFAULT 'Km',
  `height` varchar(4) NOT NULL DEFAULT 'm'
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
  `date` datetime NOT NULL,
  `duration` int(10) UNSIGNED NOT NULL,
  `distance` float NOT NULL DEFAULT 0,
  `calories` float NOT NULL DEFAULT 0,
  `id_sport` int(11) NOT NULL
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
-- Indici per le tabelle `language_default`
--
ALTER TABLE `language_default`
  ADD PRIMARY KEY (`id_user`),
  ADD UNIQUE KEY `FKuse_IND` (`id_user`);

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
-- Indici per le tabelle `sport`
--
ALTER TABLE `sport`
  ADD PRIMARY KEY (`id_sport`),
  ADD UNIQUE KEY `ID_Sport_IND` (`id_sport`);

--
-- Indici per le tabelle `sport_default`
--
ALTER TABLE `sport_default`
  ADD PRIMARY KEY (`id_user`),
  ADD UNIQUE KEY `FKchange_IND` (`id_user`),
  ADD KEY `FKR_1_IND` (`sport`);

--
-- Indici per le tabelle `target`
--
ALTER TABLE `target`
  ADD PRIMARY KEY (`id_target`),
  ADD UNIQUE KEY `ID_Target_IND` (`id_target`);

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
  ADD KEY `FKabout_IND` (`id_sport`),
  ADD KEY `FKcarry_out_IND` (`id_user`) USING BTREE;

--
-- AUTO_INCREMENT per le tabelle scaricate
--

--
-- AUTO_INCREMENT per la tabella `sport`
--
ALTER TABLE `sport`
  MODIFY `id_sport` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=3;

--
-- AUTO_INCREMENT per la tabella `target`
--
ALTER TABLE `target`
  MODIFY `id_target` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=3;

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
-- Limiti per la tabella `language_default`
--
ALTER TABLE `language_default`
  ADD CONSTRAINT `FKuse_FK` FOREIGN KEY (`id_user`) REFERENCES `user` (`id_user`) ON DELETE CASCADE;

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
  ADD CONSTRAINT `FKR_1_FK` FOREIGN KEY (`sport`) REFERENCES `sport` (`id_sport`),
  ADD CONSTRAINT `FKchange_FK` FOREIGN KEY (`id_user`) REFERENCES `user` (`id_user`) ON DELETE CASCADE;

--
-- Limiti per la tabella `target_default`
--
ALTER TABLE `target_default`
  ADD CONSTRAINT `FKR_FK` FOREIGN KEY (`target`) REFERENCES `target` (`id_target`),
  ADD CONSTRAINT `FKpossess_FK` FOREIGN KEY (`id_user`) REFERENCES `user` (`id_user`) ON DELETE CASCADE;

--
-- Limiti per la tabella `unit_measure_default`
--
ALTER TABLE `unit_measure_default`
  ADD CONSTRAINT `FKchoose_1_FK` FOREIGN KEY (`id_user`) REFERENCES `user` (`id_user`) ON DELETE CASCADE;

--
-- Limiti per la tabella `weight`
--
ALTER TABLE `weight`
  ADD CONSTRAINT `FKadd` FOREIGN KEY (`id_user`) REFERENCES `user` (`id_user`) ON DELETE CASCADE;

--
-- Limiti per la tabella `workout`
--
ALTER TABLE `workout`
  ADD CONSTRAINT `FKabout_FK` FOREIGN KEY (`id_sport`) REFERENCES `sport` (`id_sport`),
  ADD CONSTRAINT `FKcarry_out_FK` FOREIGN KEY (`id_user`) REFERENCES `user` (`id_user`) ON DELETE CASCADE;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
