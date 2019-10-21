-- phpMyAdmin SQL Dump
-- version 4.9.0.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Creato il: Ott 21, 2019 alle 21:47
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
-- Struttura della tabella `language_default`
--

CREATE TABLE `language_default` (
  `id_user` int(11) NOT NULL,
  `language` varchar(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Struttura della tabella `location`
--

CREATE TABLE `location` (
  `id_user` int(11) NOT NULL,
  `active` tinyint(1) NOT NULL DEFAULT 1
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Struttura della tabella `login`
--

CREATE TABLE `login` (
  `id_user` int(11) NOT NULL,
  `username` varchar(50) NOT NULL,
  `hash_password` varchar(100) NOT NULL,
  `salt` varchar(20) NOT NULL
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
  `energy` int(11) NOT NULL DEFAULT 1,
  `weight` int(11) NOT NULL DEFAULT 1,
  `distance` int(11) NOT NULL DEFAULT 1,
  `height` int(11) NOT NULL DEFAULT 1
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Struttura della tabella `unit_measure_distance`
--

CREATE TABLE `unit_measure_distance` (
  `id_unit_distance` int(11) NOT NULL,
  `unit` varchar(2) NOT NULL,
  `extended_name` varchar(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dump dei dati per la tabella `unit_measure_distance`
--

INSERT INTO `unit_measure_distance` (`id_unit_distance`, `unit`, `extended_name`) VALUES
(1, 'Km', 'Chilometro'),
(2, 'mi', 'Miglio');

-- --------------------------------------------------------

--
-- Struttura della tabella `unit_measure_energy`
--

CREATE TABLE `unit_measure_energy` (
  `id_unit_energy` int(11) NOT NULL,
  `unit` varchar(4) NOT NULL,
  `extended_name` varchar(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dump dei dati per la tabella `unit_measure_energy`
--

INSERT INTO `unit_measure_energy` (`id_unit_energy`, `unit`, `extended_name`) VALUES
(1, 'Kcal', 'Chilocaloria');

-- --------------------------------------------------------

--
-- Struttura della tabella `unit_measure_height`
--

CREATE TABLE `unit_measure_height` (
  `id_unit_height` int(11) NOT NULL,
  `unit` varchar(2) NOT NULL,
  `extended_name` varchar(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dump dei dati per la tabella `unit_measure_height`
--

INSERT INTO `unit_measure_height` (`id_unit_height`, `unit`, `extended_name`) VALUES
(1, 'm', 'Metro'),
(2, 'ft', 'Piede');

-- --------------------------------------------------------

--
-- Struttura della tabella `unit_measure_weight`
--

CREATE TABLE `unit_measure_weight` (
  `id_unit_weight` int(11) NOT NULL,
  `unit` varchar(2) NOT NULL,
  `extended_name` varchar(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dump dei dati per la tabella `unit_measure_weight`
--

INSERT INTO `unit_measure_weight` (`id_unit_weight`, `unit`, `extended_name`) VALUES
(1, 'Kg', 'Chilogrammo'),
(2, 'lb', 'Libbra');

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
  `city_country` varchar(30) NOT NULL,
  `phone` varchar(20) NOT NULL,
  `height` float NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Trigger `user`
--
DELIMITER $$
CREATE TRIGGER `after_insert_user` AFTER INSERT ON `user` FOR EACH ROW BEGIN

INSERT INTO location (id_user) VALUES(new.id_user);
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
  `map_route` linestring DEFAULT NULL,
  `date` datetime NOT NULL,
  `duration` int(10) UNSIGNED NOT NULL,
  `distance` float DEFAULT NULL,
  `calories` float DEFAULT NULL,
  `middle_speed` float DEFAULT NULL,
  `sport` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Indici per le tabelle scaricate
--

--
-- Indici per le tabelle `language_default`
--
ALTER TABLE `language_default`
  ADD PRIMARY KEY (`id_user`),
  ADD UNIQUE KEY `FKuse_IND` (`id_user`);

--
-- Indici per le tabelle `location`
--
ALTER TABLE `location`
  ADD PRIMARY KEY (`id_user`),
  ADD UNIQUE KEY `FKbe_IND` (`id_user`);

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
  ADD UNIQUE KEY `FKchoose_IND` (`id_user`),
  ADD KEY `FKR_6_IND` (`energy`),
  ADD KEY `FKR_5_IND` (`weight`),
  ADD KEY `FKR_4_IND` (`distance`),
  ADD KEY `FKR_3_IND` (`height`);

--
-- Indici per le tabelle `unit_measure_distance`
--
ALTER TABLE `unit_measure_distance`
  ADD PRIMARY KEY (`id_unit_distance`),
  ADD UNIQUE KEY `ID_Unit_Measure_Distance_IND` (`id_unit_distance`);

--
-- Indici per le tabelle `unit_measure_energy`
--
ALTER TABLE `unit_measure_energy`
  ADD PRIMARY KEY (`id_unit_energy`),
  ADD UNIQUE KEY `ID_Unit_Measure_Energy_IND` (`id_unit_energy`);

--
-- Indici per le tabelle `unit_measure_height`
--
ALTER TABLE `unit_measure_height`
  ADD PRIMARY KEY (`id_unit_height`),
  ADD UNIQUE KEY `ID_Unit_Measure_Height_IND` (`id_unit_height`);

--
-- Indici per le tabelle `unit_measure_weight`
--
ALTER TABLE `unit_measure_weight`
  ADD PRIMARY KEY (`id_unit_weight`),
  ADD UNIQUE KEY `ID_Unit_Measure_Weight_IND` (`id_unit_weight`);

--
-- Indici per le tabelle `user`
--
ALTER TABLE `user`
  ADD PRIMARY KEY (`id_user`),
  ADD UNIQUE KEY `SID_User_ID` (`name`,`last_name`),
  ADD UNIQUE KEY `ID_User_IND` (`id_user`),
  ADD UNIQUE KEY `SID_User_IND` (`name`,`last_name`);

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
  ADD KEY `FKabout_IND` (`sport`),
  ADD KEY `FKcarry_out_IND` (`id_user`) USING BTREE;

--
-- AUTO_INCREMENT per le tabelle scaricate
--

--
-- AUTO_INCREMENT per la tabella `sport`
--
ALTER TABLE `sport`
  MODIFY `id_sport` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT per la tabella `target`
--
ALTER TABLE `target`
  MODIFY `id_target` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT per la tabella `unit_measure_distance`
--
ALTER TABLE `unit_measure_distance`
  MODIFY `id_unit_distance` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT per la tabella `unit_measure_energy`
--
ALTER TABLE `unit_measure_energy`
  MODIFY `id_unit_energy` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT per la tabella `unit_measure_height`
--
ALTER TABLE `unit_measure_height`
  MODIFY `id_unit_height` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT per la tabella `unit_measure_weight`
--
ALTER TABLE `unit_measure_weight`
  MODIFY `id_unit_weight` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT per la tabella `user`
--
ALTER TABLE `user`
  MODIFY `id_user` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT per la tabella `workout`
--
ALTER TABLE `workout`
  MODIFY `id_workout` int(11) NOT NULL AUTO_INCREMENT;

--
-- Limiti per le tabelle scaricate
--

--
-- Limiti per la tabella `language_default`
--
ALTER TABLE `language_default`
  ADD CONSTRAINT `FKuse_FK` FOREIGN KEY (`id_user`) REFERENCES `user` (`id_user`) ON DELETE CASCADE;

--
-- Limiti per la tabella `location`
--
ALTER TABLE `location`
  ADD CONSTRAINT `FKbe_FK` FOREIGN KEY (`id_user`) REFERENCES `user` (`id_user`) ON DELETE CASCADE;

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
  ADD CONSTRAINT `FKR_3_FK` FOREIGN KEY (`height`) REFERENCES `unit_measure_height` (`id_unit_height`),
  ADD CONSTRAINT `FKR_4_FK` FOREIGN KEY (`distance`) REFERENCES `unit_measure_distance` (`id_unit_distance`),
  ADD CONSTRAINT `FKR_5_FK` FOREIGN KEY (`weight`) REFERENCES `unit_measure_weight` (`id_unit_weight`),
  ADD CONSTRAINT `FKR_6_FK` FOREIGN KEY (`energy`) REFERENCES `unit_measure_energy` (`id_unit_energy`),
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
  ADD CONSTRAINT `FKabout_FK` FOREIGN KEY (`sport`) REFERENCES `sport` (`id_sport`),
  ADD CONSTRAINT `FKcarry_out_FK` FOREIGN KEY (`id_user`) REFERENCES `user` (`id_user`) ON DELETE CASCADE;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
