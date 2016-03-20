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

-- Dumping structure for table dflmngrdev.raw_player_stats
CREATE TABLE IF NOT EXISTS `raw_player_stats` (
  `round` int(2) NOT NULL,
  `name` varchar(50) NOT NULL,
  `team` varchar(4) NOT NULL,
  `kicks` int(2) DEFAULT NULL,
  `handballs` int(2) DEFAULT NULL,
  `disposals` int(2) DEFAULT NULL,
  `marks` int(2) DEFAULT NULL,
  `hitouts` int(2) DEFAULT NULL,
  `frees_for` int(2) DEFAULT NULL,
  `frees_against` int(2) DEFAULT NULL,
  `tackles` int(2) DEFAULT NULL,
  `goals` int(2) DEFAULT NULL,
  `behinds` int(2) DEFAULT NULL,
  PRIMARY KEY (`name`,`round`,`team`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- Data exporting was unselected.
/*!40101 SET SQL_MODE=IFNULL(@OLD_SQL_MODE, '') */;
/*!40014 SET FOREIGN_KEY_CHECKS=IF(@OLD_FOREIGN_KEY_CHECKS IS NULL, 1, @OLD_FOREIGN_KEY_CHECKS) */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
