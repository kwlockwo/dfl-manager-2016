package net.dflmngr.model.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "dfl_round_early_games")
public class DflRoundEarlyGames {
	
	@Id @GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="id")
	private int id;
	
	@Column(name="round")
	private int round;
	
	@Column(name="afl_round")
	private int aflRound;
	
	@Column(name="afl_game")
	private int aflGame;
	
	@Column(name="start_time")
	private Date startTime;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getRound() {
		return round;
	}

	public void setRound(int round) {
		this.round = round;
	}

	public int getAflRound() {
		return aflRound;
	}

	public void setAflRound(int aflRound) {
		this.aflRound = aflRound;
	}

	public int getAflGame() {
		return aflGame;
	}

	public void setAflGame(int aflGame) {
		this.aflGame = aflGame;
	}

	public Date getStartTime() {
		return startTime;
	}

	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}

	@Override
	public String toString() {
		return "DflRoundEarlyGames [id=" + id + ", round=" + round + ", aflRound=" + aflRound + ", aflGame="
				+ aflGame + ", startTime=" + startTime + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + aflGame;
		result = prime * result + aflRound;
		result = prime * result + id;
		result = prime * result + round;
		result = prime * result + ((startTime == null) ? 0 : startTime.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		DflRoundEarlyGames other = (DflRoundEarlyGames) obj;
		if (aflGame != other.aflGame)
			return false;
		if (aflRound != other.aflRound)
			return false;
		if (id != other.id)
			return false;
		if (round != other.round)
			return false;
		if (startTime == null) {
			if (other.startTime != null)
				return false;
		} else if (!startTime.equals(other.startTime))
			return false;
		return true;
	}
}
