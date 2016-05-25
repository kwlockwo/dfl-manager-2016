package net.dflmngr.model.entity;

import java.util.Comparator;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;

import net.dflmngr.model.entity.keys.DflPlayerPredictedScoresPK;

@Entity
@Table(name="dfl_player_predicted_scores")
@IdClass(DflPlayerPredictedScoresPK.class)
public class DflPlayerPredictedScores implements Comparator<DflPlayerPredictedScores>, Comparable<DflPlayerPredictedScores> {
	
	@Id
	@Column(name="player_id")
	private int playerId;
	
	@Id
	private int round;
	
	@Column(name="afl_player_id")
	private String aflPlayerId;
	
	@Column(name="team_code")
	private String teamCode;
	
	@Column(name="team_player_id")
	private int teamPlayerId;
	
	@Column(name="predicted_score")
	private int predictedScore;
	
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
	public String getAflPlayerId() {
		return aflPlayerId;
	}
	public void setAflPlayerId(String aflPlayerId) {
		this.aflPlayerId = aflPlayerId;
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
	public int getPredictedScore() {
		return predictedScore;
	}
	public void setPredictedScore(int predictedScore) {
		this.predictedScore = predictedScore;
	}
	
	@Override
	public String toString() {
		return "DflPlayerPredictedScores [playerId=" + playerId + ", round=" + round + ", aflPlayerId=" + aflPlayerId
				+ ", teamCode=" + teamCode + ", teamPlayerId=" + teamPlayerId + ", predictedScore=" + predictedScore
				+ "]";
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((aflPlayerId == null) ? 0 : aflPlayerId.hashCode());
		result = prime * result + playerId;
		result = prime * result + predictedScore;
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
		DflPlayerPredictedScores other = (DflPlayerPredictedScores) obj;
		if (aflPlayerId == null) {
			if (other.aflPlayerId != null)
				return false;
		} else if (!aflPlayerId.equals(other.aflPlayerId))
			return false;
		if (playerId != other.playerId)
			return false;
		if (predictedScore != other.predictedScore)
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
	
	@Override
	public int compareTo(DflPlayerPredictedScores o) {
		return this.predictedScore > o.predictedScore ? 1 : (this.predictedScore < o.predictedScore ? -1 : 0);
	}
	
	@Override
	public int compare(DflPlayerPredictedScores o1, DflPlayerPredictedScores o2) {
		return o1.predictedScore > o2.predictedScore ? 1 : (o2.predictedScore < o2.predictedScore ? -1 : 0);
	}
}
