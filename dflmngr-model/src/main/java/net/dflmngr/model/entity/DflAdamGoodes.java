package net.dflmngr.model.entity;

import java.util.Comparator;

public class DflAdamGoodes implements Comparator<DflAdamGoodes>, Comparable<DflAdamGoodes> {
	
	private int round;
	private int playerId;
	private String teamCode;
	private int teamPlayerId;
	private int totalScore;
	
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
	public String getTeamCode() {
		return teamCode;
	}
	public void setTeamCode(String teamCode) {
		this.teamCode = teamCode;
	}
	public int getTeamPlayerId() {
		return teamPlayerId;
	}
	public void setTeamPlayerId(int teamPlayerId) {
		this.teamPlayerId = teamPlayerId;
	}
	public int getTotalScore() {
		return totalScore;
	}
	public void setTotalScore(int totalScore) {
		this.totalScore = totalScore;
	}
	@Override
	public String toString() {
		return "DflAdamGoodes [round=" + round + ", playerId=" + playerId + ", teamCode=" + teamCode + ", teamPlayerId="
				+ teamPlayerId + ", totalScore=" + totalScore + "]";
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + playerId;
		result = prime * result + round;
		result = prime * result + ((teamCode == null) ? 0 : teamCode.hashCode());
		result = prime * result + teamPlayerId;
		result = prime * result + totalScore;
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
		DflAdamGoodes other = (DflAdamGoodes) obj;
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
		if (totalScore != other.totalScore)
			return false;
		return true;
	}
	
	@Override
	public int compareTo(DflAdamGoodes o) {
		return this.totalScore > o.totalScore ? 1 : (this.totalScore < o.totalScore ? -1 : 0);
	}
	
	@Override
	public int compare(DflAdamGoodes o1, DflAdamGoodes o2) {
		return o1.totalScore > o2.totalScore ? 1 : (o2.totalScore < o2.totalScore ? -1 : 0);
	}
}
