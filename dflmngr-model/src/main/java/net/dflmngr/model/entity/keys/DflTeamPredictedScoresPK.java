package net.dflmngr.model.entity.keys;

import java.io.Serializable;

public class DflTeamPredictedScoresPK implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private String teamCode;
	private int round;
	
	public String getTeamCode() {
		return teamCode;
	}
	public void setTeamCode(String teamCode) {
		this.teamCode = teamCode;
	}
	public int getRound() {
		return round;
	}
	public void setRound(int round) {
		this.round = round;
	}
	
	@Override
	public String toString() {
		return "DflTeamPredictedScoresPK [teamCode=" + teamCode + ", round=" + round + "]";
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
		DflTeamPredictedScoresPK other = (DflTeamPredictedScoresPK) obj;
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
