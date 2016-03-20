-- --------------------------------------------------------
-- Host:                         127.0.0.1
-- Server version:               10.1.11-MariaDB - mariadb.org binary distribution
-- Server OS:                    Win64
-- HeidiSQL Version:             9.1.0.4867
-- --------------------------------------------------------

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET NAMES utf8mb4 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;

-- Dumping structure for table dflmngrdev.afl_fixture
CREATE TABLE IF NOT EXISTS `afl_fixture` (
  `round` int(2) NOT NULL,
  `game` int(2) NOT NULL,
  `home_team` varchar(4) DEFAULT NULL,
  `away_team` varchar(4) DEFAULT NULL,
  `ground` varchar(3) NOT NULL,
  `start` datetime DEFAULT NULL,
  `timezone` varchar(50) NOT NULL,
  PRIMARY KEY (`round`,`game`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- Data exporting was unselected.
/*!40101 SET SQL_MODE=IFNULL(@OLD_SQL_MODE, '') */;
/*!40014 SET FOREIGN_KEY_CHECKS=IF(@OLD_FOREIGN_KEY_CHECKS IS NULL, 1, @OLD_FOREIGN_KEY_CHECKS) */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
