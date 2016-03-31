package net.dflmngr.model.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;

import net.dflmngr.model.entity.keys.DflSelectedPlayerPK;

@Entity
@Table(name="dfl_selected_player")
@IdClass(DflSelectedPlayerPK.class)
public class DflSelectedPlayer {
	
	@Id
	private int round;
	
	@Id
	@Column(name = "player_id")
	private int playerId;
	
	@Column(name = "team_player_id")
	private int teamPlayerId;
	
	@Column(name = "team_code")
	private String teamCode;
	
	public int getRound() {
		return round;
	}
	public void setRound(int round) {
		this.round = round;
	}
	public int getPlayerId() {
		return playerId;
	}
	public void setPlayerId(int playerId) {
		this.playerId = playerId;
	}
	public int getTeamPlayerId() {
		return teamPlayerId;
	}
	public void setTeamPlayerId(int teamPlayerId) {
		this.teamPlayerId = teamPlayerId;
	}
	public String getTeamCode() {
		return teamCode;
	}
	public void setTeamCode(String teamCode) {
		this.teamCode = teamCode;
	}
	
	@Override
	public String toString() {
		return "DflSelectedPlayer [round=" + round + ", playerId=" + playerId + ", teamPlayerId=" + teamPlayerId
				+ ", teamCode=" + teamCode + "]";
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + playerId;
		result = prime * result + round;
		result = prime * result + ((teamCode == null) ? 0 : teamCode.hashCode());
		result = prime * result + teamPlayerId;
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
		DflSelectedPlayer other = (DflSelectedPlayer) obj;
		if (playerId != other.playerId)
			return false;
		if (round != other.round)
			return false;
		if (teamCode == null) {
			if (other.teamCode != null)
				return false;
		} else if (!teamCode.equals(other.teamCode))
			return false;
		if (teamPlayerId != other.teamPlayerId)
			return false;
		return true;
	}
}
