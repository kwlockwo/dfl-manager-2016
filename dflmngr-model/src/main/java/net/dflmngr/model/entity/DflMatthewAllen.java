package net.dflmngr.model.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "dfl_matthew_allen")
public class DflMatthewAllen {
	
	@Id @GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="id")
	private int id;
	
	private int round;
	private int game;
	
	@Column(name="player_id")
	private int playderId;
	
	private int votes;
	
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
	public int getGame() {
		return game;
	}
	public void setGame(int game) {
		this.game = game;
	}
	public int getPlayderId() {
		return playderId;
	}
	public void setPlayderId(int playderId) {
		this.playderId = playderId;
	}
	public int getVotes() {
		return votes;
	}
	public void setVotes(int votes) {
		this.votes = votes;
	}
	
	@Override
	public String toString() {
		return "DflMatthewAllen [id=" + id + ", round=" + round + ", game=" + game + ", playderId=" + playderId
				+ ", votes=" + votes + "]";
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + game;
		result = prime * result + id;
		result = prime * result + playderId;
		result = prime * result + round;
		result = prime * result + votes;
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
		DflMatthewAllen other = (DflMatthewAllen) obj;
		if (game != other.game)
			return false;
		if (id != other.id)
			return false;
		if (playderId != other.playderId)
			return false;
		if (round != other.round)
			return false;
		if (votes != other.votes)
			return false;
		return true;
	}
}
