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


-- Dumping structure for table dflmngrdev.afl_player
CREATE TABLE IF NOT EXISTS `afl_player` (
  `player_id` varchar(7) NOT NULL,
  `jumper_no` int(2) DEFAULT NULL,
  `first_name` varchar(25) DEFAULT NULL,
  `second_name` varchar(25) DEFAULT NULL,
  `team_id` varchar(5) DEFAULT NULL,
  `height` int(3) DEFAULT NULL,
  `weight` varchar(3) DEFAULT NULL,
  `dob` date DEFAULT NULL,
  `dfl_player_id` int(9) DEFAULT NULL,
  PRIMARY KEY (`player_id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- Data exporting was unselected.


-- Dumping structure for table dflmngrdev.afl_team
CREATE TABLE IF NOT EXISTS `afl_team` (
  `team_id` varchar(5) NOT NULL,
  `name` varchar(30) DEFAULT NULL,
  `nickname` varchar(20) DEFAULT NULL,
  `website` varchar(50) DEFAULT NULL,
  `senior_uri` varchar(50) DEFAULT NULL,
  `rookie_uri` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`team_id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- Data exporting was unselected.


-- Dumping structure for table dflmngrdev.dfl_afl_player
CREATE TABLE IF NOT EXISTS `dfl_afl_player` (
  `dfl_player_id` int(9) DEFAULT NULL,
  `afl_player_id` varchar(7) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- Data exporting was unselected.


-- Dumping structure for table dflmngrdev.dfl_early_ins_and_outs
CREATE TABLE IF NOT EXISTS `dfl_early_ins_and_outs` (
  `team_code` varchar(5) NOT NULL,
  `round` int(2) NOT NULL,
  `team_player_id` int(2) NOT NULL,
  `in_or_out` varchar(1) DEFAULT NULL,
  PRIMARY KEY (`team_code`,`round`,`team_player_id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- Data exporting was unselected.


-- Dumping structure for table dflmngrdev.dfl_fixture
CREATE TABLE IF NOT EXISTS `dfl_fixture` (
  `round` int(2) NOT NULL,
  `game` int(2) NOT NULL,
  `home_team` varchar(5) DEFAULT NULL,
  `away_team` varchar(5) DEFAULT NULL,
  PRIMARY KEY (`round`,`game`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- Data exporting was unselected.


-- Dumping structure for table dflmngrdev.dfl_ladder
CREATE TABLE IF NOT EXISTS `dfl_ladder` (
  `round` int(2) NOT NULL,
  `team_code` varchar(5) NOT NULL,
  `wins` int(2) DEFAULT NULL,
  `losses` int(2) DEFAULT NULL,
  `draws` int(2) DEFAULT NULL,
  `points_for` int(5) DEFAULT NULL,
  `points_against` int(5) DEFAULT NULL,
  `average_for` float DEFAULT NULL,
  `average_against` float DEFAULT NULL,
  `pts` int(2) DEFAULT NULL,
  `percentage` float DEFAULT NULL,
  PRIMARY KEY (`round`,`team_code`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- Data exporting was unselected.


-- Dumping structure for table dflmngrdev.dfl_matthew_allen
CREATE TABLE IF NOT EXISTS `dfl_matthew_allen` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `round` int(2) DEFAULT '0',
  `game` int(2) DEFAULT '0',
  `player_id` int(9) DEFAULT '0',
  `votes` int(1) DEFAULT '0',
  `total` int(2) DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- Data exporting was unselected.


-- Dumping structure for table dflmngrdev.dfl_player
CREATE TABLE IF NOT EXISTS `dfl_player` (
  `player_id` int(9) NOT NULL,
  `first_name` varchar(25) DEFAULT NULL,
  `last_name` varchar(25) DEFAULT NULL,
  `inital` varchar(1) DEFAULT NULL,
  `status` varchar(6) DEFAULT NULL,
  `afl_club` varchar(5) DEFAULT NULL,
  `position` varchar(3) DEFAULT NULL,
  `afl_player_id` varchar(7) DEFAULT NULL,
  `is_first_year` tinyint(4) DEFAULT NULL,
  PRIMARY KEY (`player_id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- Data exporting was unselected.


-- Dumping structure for table dflmngrdev.dfl_player_predicted_scores
CREATE TABLE IF NOT EXISTS `dfl_player_predicted_scores` (
  `player_id` int(9) NOT NULL,
  `round` int(2) NOT NULL,
  `afl_player_id` varchar(7) DEFAULT NULL,
  `team_code` varchar(5) DEFAULT NULL,
  `team_player_id` int(2) DEFAULT NULL,
  `predicted_score` int(3) DEFAULT NULL,
  PRIMARY KEY (`player_id`,`round`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- Data exporting was unselected.


-- Dumping structure for table dflmngrdev.dfl_player_scores
CREATE TABLE IF NOT EXISTS `dfl_player_scores` (
  `player_id` int(9) NOT NULL,
  `round` int(2) NOT NULL,
  `afl_player_id` varchar(7) DEFAULT NULL,
  `team_code` varchar(5) DEFAULT NULL,
  `team_player_id` int(2) DEFAULT NULL,
  `score` int(3) DEFAULT NULL,
  PRIMARY KEY (`player_id`,`round`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- Data exporting was unselected.


-- Dumping structure for table dflmngrdev.dfl_round_early_games
CREATE TABLE IF NOT EXISTS `dfl_round_early_games` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `round` int(2) DEFAULT NULL,
  `afl_round` int(2) DEFAULT NULL,
  `afl_game` int(2) DEFAULT NULL,
  `start_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- Data exporting was unselected.


-- Dumping structure for table dflmngrdev.dfl_round_info
CREATE TABLE IF NOT EXISTS `dfl_round_info` (
  `round` int(2) NOT NULL,
  `hard_lockout` datetime DEFAULT NULL,
  `split_round` varchar(1) DEFAULT NULL,
  PRIMARY KEY (`round`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- Data exporting was unselected.


-- Dumping structure for table dflmngrdev.dfl_round_mapping
CREATE TABLE IF NOT EXISTS `dfl_round_mapping` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `round` int(2) DEFAULT NULL,
  `afl_round` int(2) DEFAULT NULL,
  `afl_game` int(2) DEFAULT NULL,
  `afl_team` varchar(4) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- Data exporting was unselected.


-- Dumping structure for table dflmngrdev.dfl_selected_player
CREATE TABLE IF NOT EXISTS `dfl_selected_player` (
  `round` int(2) NOT NULL,
  `player_id` int(9) NOT NULL,
  `team_player_id` int(2) DEFAULT NULL,
  `team_code` varchar(5) DEFAULT NULL,
  PRIMARY KEY (`round`,`player_id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- Data exporting was unselected.


-- Dumping structure for table dflmngrdev.dfl_team
CREATE TABLE IF NOT EXISTS `dfl_team` (
  `team_code` varchar(5) NOT NULL,
  `name` varchar(50) DEFAULT NULL,
  `short_name` varchar(10) DEFAULT NULL,
  `coach_name` varchar(50) DEFAULT NULL,
  `home_ground` varchar(50) DEFAULT NULL,
  `colours` varchar(50) DEFAULT NULL,
  `coach_email` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`team_code`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- Data exporting was unselected.


-- Dumping structure for table dflmngrdev.dfl_team_player
CREATE TABLE IF NOT EXISTS `dfl_team_player` (
  `player_id` int(9) NOT NULL,
  `team_code` varchar(5) DEFAULT NULL,
  `team_player_id` int(2) DEFAULT NULL,
  PRIMARY KEY (`player_id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- Data exporting was unselected.


-- Dumping structure for table dflmngrdev.dfl_team_predicted_scores
CREATE TABLE IF NOT EXISTS `dfl_team_predicted_scores` (
  `team_code` varchar(5) NOT NULL,
  `round` int(2) NOT NULL,
  `predicted_score` int(3) DEFAULT NULL,
  PRIMARY KEY (`team_code`,`round`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- Data exporting was unselected.


-- Dumping structure for table dflmngrdev.dfl_team_scores
CREATE TABLE IF NOT EXISTS `dfl_team_scores` (
  `team_code` varchar(5) NOT NULL,
  `round` int(2) NOT NULL,
  `score` int(3) DEFAULT NULL,
  PRIMARY KEY (`team_code`,`round`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- Data exporting was unselected.


-- Dumping structure for table dflmngrdev.globals
CREATE TABLE IF NOT EXISTS `globals` (
  `code` varchar(50) NOT NULL,
  `group_code` varchar(50) NOT NULL,
  `params` varchar(100) DEFAULT NULL,
  `value` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`code`,`group_code`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- Data exporting was unselected.


-- Dumping structure for table dflmngrdev.ins_and_outs
CREATE TABLE IF NOT EXISTS `ins_and_outs` (
  `team_code` varchar(5) NOT NULL,
  `round` int(2) NOT NULL,
  `team_player_id` int(2) NOT NULL,
  `in_or_out` varchar(1) DEFAULT NULL,
  PRIMARY KEY (`team_code`,`round`,`team_player_id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- Data exporting was unselected.


-- Dumping structure for table dflmngrdev.raw_player_stats
CREATE TABLE IF NOT EXISTS `raw_player_stats` (
  `round` int(2) NOT NULL,
  `name` varchar(50) NOT NULL,
  `team` varchar(4) NOT NULL,
  `jumper_no` int(2) DEFAULT NULL,
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


-- Dumping structure for table dflmngrdev.scheduler_blob_triggers
CREATE TABLE IF NOT EXISTS `scheduler_blob_triggers` (
  `SCHED_NAME` varchar(120) NOT NULL,
  `TRIGGER_NAME` varchar(200) NOT NULL,
  `TRIGGER_GROUP` varchar(200) NOT NULL,
  `BLOB_DATA` blob,
  PRIMARY KEY (`SCHED_NAME`,`TRIGGER_NAME`,`TRIGGER_GROUP`),
  KEY `SCHED_NAME` (`SCHED_NAME`,`TRIGGER_NAME`,`TRIGGER_GROUP`),
  CONSTRAINT `scheduler_blob_triggers_ibfk_1` FOREIGN KEY (`SCHED_NAME`, `TRIGGER_NAME`, `TRIGGER_GROUP`) REFERENCES `scheduler_triggers` (`SCHED_NAME`, `TRIGGER_NAME`, `TRIGGER_GROUP`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- Data exporting was unselected.


-- Dumping structure for table dflmngrdev.scheduler_calendars
CREATE TABLE IF NOT EXISTS `scheduler_calendars` (
  `SCHED_NAME` varchar(120) NOT NULL,
  `CALENDAR_NAME` varchar(200) NOT NULL,
  `CALENDAR` blob NOT NULL,
  PRIMARY KEY (`SCHED_NAME`,`CALENDAR_NAME`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- Data exporting was unselected.


-- Dumping structure for table dflmngrdev.scheduler_cron_triggers
CREATE TABLE IF NOT EXISTS `scheduler_cron_triggers` (
  `SCHED_NAME` varchar(120) NOT NULL,
  `TRIGGER_NAME` varchar(200) NOT NULL,
  `TRIGGER_GROUP` varchar(200) NOT NULL,
  `CRON_EXPRESSION` varchar(120) NOT NULL,
  `TIME_ZONE_ID` varchar(80) DEFAULT NULL,
  PRIMARY KEY (`SCHED_NAME`,`TRIGGER_NAME`,`TRIGGER_GROUP`),
  CONSTRAINT `scheduler_cron_triggers_ibfk_1` FOREIGN KEY (`SCHED_NAME`, `TRIGGER_NAME`, `TRIGGER_GROUP`) REFERENCES `scheduler_triggers` (`SCHED_NAME`, `TRIGGER_NAME`, `TRIGGER_GROUP`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- Data exporting was unselected.


-- Dumping structure for table dflmngrdev.scheduler_fired_triggers
CREATE TABLE IF NOT EXISTS `scheduler_fired_triggers` (
  `SCHED_NAME` varchar(120) NOT NULL,
  `ENTRY_ID` varchar(95) NOT NULL,
  `TRIGGER_NAME` varchar(200) NOT NULL,
  `TRIGGER_GROUP` varchar(200) NOT NULL,
  `INSTANCE_NAME` varchar(200) NOT NULL,
  `FIRED_TIME` bigint(13) NOT NULL,
  `SCHED_TIME` bigint(13) NOT NULL,
  `PRIORITY` int(11) NOT NULL,
  `STATE` varchar(16) NOT NULL,
  `JOB_NAME` varchar(200) DEFAULT NULL,
  `JOB_GROUP` varchar(200) DEFAULT NULL,
  `IS_NONCONCURRENT` varchar(1) DEFAULT NULL,
  `REQUESTS_RECOVERY` varchar(1) DEFAULT NULL,
  PRIMARY KEY (`SCHED_NAME`,`ENTRY_ID`),
  KEY `IDX_SCHEDULER_FT_TRIG_INST_NAME` (`SCHED_NAME`,`INSTANCE_NAME`),
  KEY `IDX_SCHEDULER_FT_INST_JOB_REQ_RCVRY` (`SCHED_NAME`,`INSTANCE_NAME`,`REQUESTS_RECOVERY`),
  KEY `IDX_SCHEDULER_FT_J_G` (`SCHED_NAME`,`JOB_NAME`,`JOB_GROUP`),
  KEY `IDX_SCHEDULER_FT_JG` (`SCHED_NAME`,`JOB_GROUP`),
  KEY `IDX_SCHEDULER_FT_T_G` (`SCHED_NAME`,`TRIGGER_NAME`,`TRIGGER_GROUP`),
  KEY `IDX_SCHEDULER_FT_TG` (`SCHED_NAME`,`TRIGGER_GROUP`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- Data exporting was unselected.


-- Dumping structure for table dflmngrdev.scheduler_job_details
CREATE TABLE IF NOT EXISTS `scheduler_job_details` (
  `SCHED_NAME` varchar(120) NOT NULL,
  `JOB_NAME` varchar(200) NOT NULL,
  `JOB_GROUP` varchar(200) NOT NULL,
  `DESCRIPTION` varchar(250) DEFAULT NULL,
  `JOB_CLASS_NAME` varchar(250) NOT NULL,
  `IS_DURABLE` varchar(1) NOT NULL,
  `IS_NONCONCURRENT` varchar(1) NOT NULL,
  `IS_UPDATE_DATA` varchar(1) NOT NULL,
  `REQUESTS_RECOVERY` varchar(1) NOT NULL,
  `JOB_DATA` blob,
  PRIMARY KEY (`SCHED_NAME`,`JOB_NAME`,`JOB_GROUP`),
  KEY `IDX_SCHEDULER_J_REQ_RECOVERY` (`SCHED_NAME`,`REQUESTS_RECOVERY`),
  KEY `IDX_SCHEDULER_J_GRP` (`SCHED_NAME`,`JOB_GROUP`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- Data exporting was unselected.


-- Dumping structure for table dflmngrdev.scheduler_locks
CREATE TABLE IF NOT EXISTS `scheduler_locks` (
  `SCHED_NAME` varchar(120) NOT NULL,
  `LOCK_NAME` varchar(40) NOT NULL,
  PRIMARY KEY (`SCHED_NAME`,`LOCK_NAME`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- Data exporting was unselected.


-- Dumping structure for table dflmngrdev.scheduler_paused_trigger_grps
CREATE TABLE IF NOT EXISTS `scheduler_paused_trigger_grps` (
  `SCHED_NAME` varchar(120) NOT NULL,
  `TRIGGER_GROUP` varchar(200) NOT NULL,
  PRIMARY KEY (`SCHED_NAME`,`TRIGGER_GROUP`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- Data exporting was unselected.


-- Dumping structure for table dflmngrdev.scheduler_scheduler_state
CREATE TABLE IF NOT EXISTS `scheduler_scheduler_state` (
  `SCHED_NAME` varchar(120) NOT NULL,
  `INSTANCE_NAME` varchar(200) NOT NULL,
  `LAST_CHECKIN_TIME` bigint(13) NOT NULL,
  `CHECKIN_INTERVAL` bigint(13) NOT NULL,
  PRIMARY KEY (`SCHED_NAME`,`INSTANCE_NAME`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- Data exporting was unselected.


-- Dumping structure for table dflmngrdev.scheduler_simple_triggers
CREATE TABLE IF NOT EXISTS `scheduler_simple_triggers` (
  `SCHED_NAME` varchar(120) NOT NULL,
  `TRIGGER_NAME` varchar(200) NOT NULL,
  `TRIGGER_GROUP` varchar(200) NOT NULL,
  `REPEAT_COUNT` bigint(7) NOT NULL,
  `REPEAT_INTERVAL` bigint(12) NOT NULL,
  `TIMES_TRIGGERED` bigint(10) NOT NULL,
  PRIMARY KEY (`SCHED_NAME`,`TRIGGER_NAME`,`TRIGGER_GROUP`),
  CONSTRAINT `scheduler_simple_triggers_ibfk_1` FOREIGN KEY (`SCHED_NAME`, `TRIGGER_NAME`, `TRIGGER_GROUP`) REFERENCES `scheduler_triggers` (`SCHED_NAME`, `TRIGGER_NAME`, `TRIGGER_GROUP`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- Data exporting was unselected.


-- Dumping structure for table dflmngrdev.scheduler_simprop_triggers
CREATE TABLE IF NOT EXISTS `scheduler_simprop_triggers` (
  `SCHED_NAME` varchar(120) NOT NULL,
  `TRIGGER_NAME` varchar(200) NOT NULL,
  `TRIGGER_GROUP` varchar(200) NOT NULL,
  `STR_PROP_1` varchar(512) DEFAULT NULL,
  `STR_PROP_2` varchar(512) DEFAULT NULL,
  `STR_PROP_3` varchar(512) DEFAULT NULL,
  `INT_PROP_1` int(11) DEFAULT NULL,
  `INT_PROP_2` int(11) DEFAULT NULL,
  `LONG_PROP_1` bigint(20) DEFAULT NULL,
  `LONG_PROP_2` bigint(20) DEFAULT NULL,
  `DEC_PROP_1` decimal(13,4) DEFAULT NULL,
  `DEC_PROP_2` decimal(13,4) DEFAULT NULL,
  `BOOL_PROP_1` varchar(1) DEFAULT NULL,
  `BOOL_PROP_2` varchar(1) DEFAULT NULL,
  PRIMARY KEY (`SCHED_NAME`,`TRIGGER_NAME`,`TRIGGER_GROUP`),
  CONSTRAINT `scheduler_simprop_triggers_ibfk_1` FOREIGN KEY (`SCHED_NAME`, `TRIGGER_NAME`, `TRIGGER_GROUP`) REFERENCES `scheduler_triggers` (`SCHED_NAME`, `TRIGGER_NAME`, `TRIGGER_GROUP`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- Data exporting was unselected.


-- Dumping structure for table dflmngrdev.scheduler_triggers
CREATE TABLE IF NOT EXISTS `scheduler_triggers` (
  `SCHED_NAME` varchar(120) NOT NULL,
  `TRIGGER_NAME` varchar(200) NOT NULL,
  `TRIGGER_GROUP` varchar(200) NOT NULL,
  `JOB_NAME` varchar(200) NOT NULL,
  `JOB_GROUP` varchar(200) NOT NULL,
  `DESCRIPTION` varchar(250) DEFAULT NULL,
  `NEXT_FIRE_TIME` bigint(13) DEFAULT NULL,
  `PREV_FIRE_TIME` bigint(13) DEFAULT NULL,
  `PRIORITY` int(11) DEFAULT NULL,
  `TRIGGER_STATE` varchar(16) NOT NULL,
  `TRIGGER_TYPE` varchar(8) NOT NULL,
  `START_TIME` bigint(13) NOT NULL,
  `END_TIME` bigint(13) DEFAULT NULL,
  `CALENDAR_NAME` varchar(200) DEFAULT NULL,
  `MISFIRE_INSTR` smallint(2) DEFAULT NULL,
  `JOB_DATA` blob,
  PRIMARY KEY (`SCHED_NAME`,`TRIGGER_NAME`,`TRIGGER_GROUP`),
  KEY `IDX_SCHEDULER_T_J` (`SCHED_NAME`,`JOB_NAME`,`JOB_GROUP`),
  KEY `IDX_SCHEDULER_T_JG` (`SCHED_NAME`,`JOB_GROUP`),
  KEY `IDX_SCHEDULER_T_C` (`SCHED_NAME`,`CALENDAR_NAME`),
  KEY `IDX_SCHEDULER_T_G` (`SCHED_NAME`,`TRIGGER_GROUP`),
  KEY `IDX_SCHEDULER_T_STATE` (`SCHED_NAME`,`TRIGGER_STATE`),
  KEY `IDX_SCHEDULER_T_N_STATE` (`SCHED_NAME`,`TRIGGER_NAME`,`TRIGGER_GROUP`,`TRIGGER_STATE`),
  KEY `IDX_SCHEDULER_T_N_G_STATE` (`SCHED_NAME`,`TRIGGER_GROUP`,`TRIGGER_STATE`),
  KEY `IDX_SCHEDULER_T_NEXT_FIRE_TIME` (`SCHED_NAME`,`NEXT_FIRE_TIME`),
  KEY `IDX_SCHEDULER_T_NFT_ST` (`SCHED_NAME`,`TRIGGER_STATE`,`NEXT_FIRE_TIME`),
  KEY `IDX_SCHEDULER_T_NFT_MISFIRE` (`SCHED_NAME`,`MISFIRE_INSTR`,`NEXT_FIRE_TIME`),
  KEY `IDX_SCHEDULER_T_NFT_ST_MISFIRE` (`SCHED_NAME`,`MISFIRE_INSTR`,`NEXT_FIRE_TIME`,`TRIGGER_STATE`),
  KEY `IDX_SCHEDULER_T_NFT_ST_MISFIRE_GRP` (`SCHED_NAME`,`MISFIRE_INSTR`,`NEXT_FIRE_TIME`,`TRIGGER_GROUP`,`TRIGGER_STATE`),
  CONSTRAINT `scheduler_triggers_ibfk_1` FOREIGN KEY (`SCHED_NAME`, `JOB_NAME`, `JOB_GROUP`) REFERENCES `scheduler_job_details` (`SCHED_NAME`, `JOB_NAME`, `JOB_GROUP`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- Data exporting was unselected.
/*!40101 SET SQL_MODE=IFNULL(@OLD_SQL_MODE, '') */;
/*!40014 SET FOREIGN_KEY_CHECKS=IF(@OLD_FOREIGN_KEY_CHECKS IS NULL, 1, @OLD_FOREIGN_KEY_CHECKS) */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
