package net.dflmngr.model.entity;

import java.util.Comparator;

public class DflCallumChambers implements Comparator<DflCallumChambers>, Comparable<DflCallumChambers> {
	
	private int round;
	private int playerId;
	private String teamCode;
	private int draftOrder;
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
	public int getDraftOrder() {
		return draftOrder;
	}
	public void setDraftOrder(int draftOrder) {
		this.draftOrder = draftOrder;
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
		return "DflCallumChambers [round=" + round + ", playerId=" + playerId + ", teamCode=" + teamCode
				+ ", draftOrder=" + draftOrder + ", teamPlayerId=" + teamPlayerId + ", totalScore=" + totalScore + "]";
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + draftOrder;
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
		DflCallumChambers other = (DflCallumChambers) obj;
		if (draftOrder != other.draftOrder)
			return false;
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
	public int compareTo(DflCallumChambers o) {
		int equal = 0;
		int less = -1;
		int greater = 1;
		
		int totalScore = this.getTotalScore();
		int oTotalScore = o.getTotalScore();
		
		if(totalScore < oTotalScore) {
			return greater;
		}
		if(totalScore > oTotalScore) {
			return less;
		}
		
		int teamPlayerId = this.getTeamPlayerId();
		int oTeamPlayerId = o.getTeamPlayerId();
		
		if(teamPlayerId < oTeamPlayerId) {
			return greater;
		}
		if(teamPlayerId > oTeamPlayerId) {
			return less;
		}
		
		int draftOrder = this.getDraftOrder();
		int oDraftOrder = o.getDraftOrder();
		
		if(draftOrder > oDraftOrder) {
			return greater;
		}
		if(draftOrder < oDraftOrder) {
			return less;
		}
		
		return equal;
	}
	
	@Override
	public int compare(DflCallumChambers o1, DflCallumChambers o2) {
		int equal = 0;
		int less = -1;
		int greater = 1;
		
		int o1TotalScore = o1.getTotalScore();
		int o2TotalScore = o2.getTotalScore();
		
		if(o1TotalScore < o2TotalScore) {
			return greater;
		}
		if(o1TotalScore > o2TotalScore) {
			return less;
		}
		
		int o1teamPlayerId = o1.getTeamPlayerId();
		int o2TeamPlayerId = o2.getTeamPlayerId();
		
		if(o1teamPlayerId < o2TeamPlayerId) {
			return greater;
		}
		if(o1teamPlayerId > o2TeamPlayerId) {
			return less;
		}
		
		int o1draftOrder = o1.getDraftOrder();
		int o2DraftOrder = o2.getDraftOrder();
		
		if(o1draftOrder > o2DraftOrder) {
			return greater;
		}
		if(o1draftOrder < o2DraftOrder) {
			return less;
		}
		
		return equal;
	}

}
