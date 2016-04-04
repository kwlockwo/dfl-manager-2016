package net.dflmngr.model.entity.keys;

import java.io.Serializable;

public class DflSelectedPlayerPK implements Serializable {
	private static final long serialVersionUID = 1L;

	private int round;
	private int playerId;
	
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
	
	@Override
	public String toString() {
		return "DflSelectedTeamPK [round=" + round + ", playerId=" + playerId + "]";
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + playerId;
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
		DflSelectedPlayerPK other = (DflSelectedPlayerPK) obj;
		if (playerId != other.playerId)
			return false;
		if (round != other.round)
			return false;
		return true;
	}
}
