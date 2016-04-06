package net.dflmngr.model.entity.keys;

import java.io.Serializable;

public class DflLadderPK implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private int round;
	private String teamCode;
	
	public int getRound() {
		return round;
	}
	public void setRound(int round) {
		this.round = round;
	}
	public String getTeamCode() {
		return teamCode;
	}
	public void setTeamCode(String teamCode) {
		this.teamCode = teamCode;
	}

	@Override
	public String toString() {
		return "DflLadderPK [round=" + round + ", teamCode=" + teamCode + "]";
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + round;
		result = prime * result + ((teamCode == null) ? 0 : teamCode.hashCode());
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
		DflLadderPK other = (DflLadderPK) obj;
		if (round != other.round)
			return false;
		if (teamCode == null) {
			if (other.teamCode != null)
				return false;
		} else if (!teamCode.equals(other.teamCode))
			return false;
		return true;
	}
}
