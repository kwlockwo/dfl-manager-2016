package net.dflmngr.model.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;

import net.dflmngr.model.entity.keys.DflTeamPredictedScoresPK;

@Entity
@Table(name="dfl_team_predicted_scores")
@IdClass(DflTeamPredictedScoresPK.class)
public class DflTeamPredictedScores {
	
	@Id
	@Column(name="team_code")
	private String teamCode;
	
	@Id
	private int round;
	
	@Column(name="predicted_score")
	private int predictedScore;
	
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
	public int getPredictedScore() {
		return predictedScore;
	}
	public void setPredictedScore(int predictedScore) {
		this.predictedScore = predictedScore;
	}
	
	@Override
	public String toString() {
		return "DflTeamScores [teamCode=" + teamCode + ", round=" + round + ", score=" + predictedScore + "]";
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + round;
		result = prime * result + predictedScore;
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
		DflTeamPredictedScores other = (DflTeamPredictedScores) obj;
		if (round != other.round)
			return false;
		if (predictedScore != other.predictedScore)
			return false;
		if (teamCode == null) {
			if (other.teamCode != null)
				return false;
		} else if (!teamCode.equals(other.teamCode))
			return false;
		return true;
	}
}
