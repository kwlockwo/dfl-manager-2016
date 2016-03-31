package net.dflmngr.model.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;

import net.dflmngr.model.entity.keys.DflTeamScoresPK;

@Entity
@Table(name="dfl_team_scores")
@IdClass(DflTeamScoresPK.class)
public class DflTeamScores {
	
	@Id
	@Column(name="team_code")
	private String teamCode;
	
	@Id
	private int round;
	private int score;
	
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
	public int getScore() {
		return score;
	}
	public void setScore(int score) {
		this.score = score;
	}
	
	@Override
	public String toString() {
		return "DflTeamScores [teamCode=" + teamCode + ", round=" + round + ", score=" + score + "]";
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + round;
		result = prime * result + score;
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
		DflTeamScores other = (DflTeamScores) obj;
		if (round != other.round)
			return false;
		if (score != other.score)
			return false;
		if (teamCode == null) {
			if (other.teamCode != null)
				return false;
		} else if (!teamCode.equals(other.teamCode))
			return false;
		return true;
	}
}
