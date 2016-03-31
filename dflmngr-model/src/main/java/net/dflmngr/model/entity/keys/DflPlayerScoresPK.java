package net.dflmngr.model.entity.keys;

public class DflPlayerScoresPK {
	
	private int playerId;
	private int round;
	
	public int getPlayerId() {
		return playerId;
	}
	public void setPlayerId(int playerId) {
		this.playerId = playerId;
	}
	public int getRound() {
		return round;
	}
	public void setRound(int round) {
		this.round = round;
	}
	
	@Override
	public String toString() {
		return "DflPlayerScoresPK [playerId=" + playerId + ", round=" + round + "]";
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
		DflPlayerScoresPK other = (DflPlayerScoresPK) obj;
		if (playerId != other.playerId)
			return false;
		if (round != other.round)
			return false;
		return true;
	}
}
