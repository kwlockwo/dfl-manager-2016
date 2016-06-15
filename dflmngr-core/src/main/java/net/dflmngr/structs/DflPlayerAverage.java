package net.dflmngr.structs;

import java.util.Comparator;

public class DflPlayerAverage implements Comparator<DflPlayerAverage>, Comparable<DflPlayerAverage> {
	
	private int playerId;
	private int teamPlayerId;
	private String teamCode;
	private int average;
	
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
	public int getAverage() {
		return average;
	}
	public void setAverage(int average) {
		this.average = average;
	}
	
	@Override
	public String toString() {
		return "DflPlayerAverage [playerId=" + playerId + ", teamPlayerId=" + teamPlayerId + ", teamCode=" + teamCode
				+ ", average=" + average + "]";
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + average;
		result = prime * result + playerId;
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
		DflPlayerAverage other = (DflPlayerAverage) obj;
		if (average != other.average)
			return false;
		if (playerId != other.playerId)
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
	
	@Override
	public int compareTo(DflPlayerAverage o) {
		return this.average > o.average ? 1 : (this.average < o.average ? -1 : 0);
	}
	
	@Override
	public int compare(DflPlayerAverage o1, DflPlayerAverage o2) {
		return o1.average > o2.average ? 1 : (o2.average < o2.average ? -1 : 0);
	}

}
