package net.dflmngr.model.entity;

import java.util.Comparator;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "dfl_matthew_allen")
public class DflMatthewAllen implements Comparator<DflMatthewAllen>, Comparable<DflMatthewAllen> {
	
	@Id @GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="id")
	private int id;
	
	private int round;
	private int game;
	
	@Column(name="player_id")
	private int playerId;
	
	private int votes;
	private int total;
	
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
	public int getPlayerId() {
		return playerId;
	}
	public void setPlayerId(int playerId) {
		this.playerId = playerId;
	}
	public int getVotes() {
		return votes;
	}
	public void setVotes(int votes) {
		this.votes = votes;
	}
	public int getTotal() {
		return total;
	}
	public void setTotal(int total) {
		this.total = total;
	}
	
	@Override
	public String toString() {
		return "DflMatthewAllen [id=" + id + ", round=" + round + ", game=" + game + ", playerId=" + playerId
				+ ", votes=" + votes + ", total=" + total + "]";
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + game;
		result = prime * result + id;
		result = prime * result + playerId;
		result = prime * result + round;
		result = prime * result + total;
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
		if (playerId != other.playerId)
			return false;
		if (round != other.round)
			return false;
		if (total != other.total)
			return false;
		if (votes != other.votes)
			return false;
		return true;
	}
	
	@Override
	public int compareTo(DflMatthewAllen o) {
		return this.getTotal() > o.getTotal() ? 1 : (this.getTotal() < o.getTotal() ? -1 : 0);
	}
	
	@Override
	public int compare(DflMatthewAllen o1, DflMatthewAllen o2) {
		return o1.getTotal() > o2.getTotal() ? 1 : (o2.getTotal() < o2.getTotal() ? -1 : 0);
	}
}
