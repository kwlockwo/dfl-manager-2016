package net.dflmngr.model.entity;

import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table(name = "dfl_round_info")
public class DflRoundInfo {
	
	@Id
	@Column(name = "round")
	private int round;
	
	@Column(name = "hard_lockout")
	@Temporal(TemporalType.TIMESTAMP)
	private Date hardLockoutTime;
	
	@Column(name = "split_round")
	private String splitRound;
	
	@OneToMany
	@JoinColumn(name="round")
	private List<DflRoundEarlyGames> earlyGames;
	
	@OneToMany
	@JoinColumn(name="round")
	private List<DflRoundMapping> roundMapping;
	
	
	public int getRound() {
		return round;
	}
	
	public void setRound(int round) {
		this.round = round;
	}
	
	public Date getHardLockoutTime() {
		return hardLockoutTime;
	}
	
	public void setHardLockoutTime(Date hardLockoutTime) {
		this.hardLockoutTime = hardLockoutTime;
	}
	
	public String getSplitRound() {
		return splitRound;
	}
	
	public void setSplitRound(String splitRound) {
		this.splitRound = splitRound;
	}
	
	public List<DflRoundEarlyGames> getEarlyGames() {
		return earlyGames;
	}

	public void setEarlyGames(List<DflRoundEarlyGames> earlyGames) {
		this.earlyGames = earlyGames;
	}

	public List<DflRoundMapping> getRoundMapping() {
		return roundMapping;
	}

	public void setRoundMapping(List<DflRoundMapping> roundMapping) {
		this.roundMapping = roundMapping;
	}

	@Override
	public String toString() {
		return "DflRoundInfo [round=" + round + ", hardLockoutTime=" + hardLockoutTime + ", splitRound=" + splitRound
				+ ", earlyGames=" + earlyGames + ", roundMapping=" + roundMapping + "]";
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((earlyGames == null) ? 0 : earlyGames.hashCode());
		result = prime * result + ((hardLockoutTime == null) ? 0 : hardLockoutTime.hashCode());
		result = prime * result + round;
		result = prime * result + ((roundMapping == null) ? 0 : roundMapping.hashCode());
		result = prime * result + ((splitRound == null) ? 0 : splitRound.hashCode());
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
		DflRoundInfo other = (DflRoundInfo) obj;
		if (earlyGames == null) {
			if (other.earlyGames != null)
				return false;
		} else if (!earlyGames.equals(other.earlyGames))
			return false;
		if (hardLockoutTime == null) {
			if (other.hardLockoutTime != null)
				return false;
		} else if (!hardLockoutTime.equals(other.hardLockoutTime))
			return false;
		if (round != other.round)
			return false;
		if (roundMapping == null) {
			if (other.roundMapping != null)
				return false;
		} else if (!roundMapping.equals(other.roundMapping))
			return false;
		if (splitRound == null) {
			if (other.splitRound != null)
				return false;
		} else if (!splitRound.equals(other.splitRound))
			return false;
		return true;
	}
}
