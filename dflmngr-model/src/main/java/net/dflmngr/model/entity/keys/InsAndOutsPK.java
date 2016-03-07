package net.dflmngr.model.entity.keys;

import java.io.Serializable;

public class InsAndOutsPK implements Serializable {

	private static final long serialVersionUID = 1L;
	private String teamCode;
	private int round;
	private int teamPlayerId;
	
	public InsAndOutsPK() {}

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

	public int getTeamPlayerId() {
		return teamPlayerId;
	}

	public void setTeamPlayerId(int teamPlayerId) {
		this.teamPlayerId = teamPlayerId;
	}

	@Override
	public String toString() {
		return "InsAndOutsPK [teamCode=" + teamCode + ", round=" + round
				+ ", teamPlayerId=" + teamPlayerId + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + round;
		result = prime * result
				+ ((teamCode == null) ? 0 : teamCode.hashCode());
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
		InsAndOutsPK other = (InsAndOutsPK) obj;
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
