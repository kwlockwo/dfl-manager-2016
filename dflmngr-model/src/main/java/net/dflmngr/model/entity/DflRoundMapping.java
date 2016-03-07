package net.dflmngr.model.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "dfl_round_mapping")
public class DflRoundMapping {

	@Id @GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="id")
	private int id;
	
	@Column(name="round")
	private int round;
	
	@Column(name="afl_round")
	private int aflRound;
	
	@Column(name="afl_game")
	private int aflGame;
	
	@Column(name="afl_team")
	private String aflTeam;

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

	public String getAflTeam() {
		return aflTeam;
	}

	public void setAflTeam(String aflTeam) {
		this.aflTeam = aflTeam;
	}

	@Override
	public String toString() {
		return "DflRoundMapping [id=" + id + ", round=" + round + ", aflRound=" + aflRound + ", aflGame=" + aflGame
				+ ", aflTeam=" + aflTeam + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + aflGame;
		result = prime * result + aflRound;
		result = prime * result + ((aflTeam == null) ? 0 : aflTeam.hashCode());
		result = prime * result + id;
		result = prime * result + round;
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
		DflRoundMapping other = (DflRoundMapping) obj;
		if (aflGame != other.aflGame)
			return false;
		if (aflRound != other.aflRound)
			return false;
		if (aflTeam == null) {
			if (other.aflTeam != null)
				return false;
		} else if (!aflTeam.equals(other.aflTeam))
			return false;
		if (id != other.id)
			return false;
		if (round != other.round)
			return false;
		return true;
	}
}
