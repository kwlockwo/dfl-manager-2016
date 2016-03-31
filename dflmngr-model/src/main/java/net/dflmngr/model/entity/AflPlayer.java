package net.dflmngr.model.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "afl_player")
public class AflPlayer {
	
	@Id
	@Column(name = "player_id")
	private String playerId;
	
	@Column(name = "jumper_no")
	private int jumperNo;
	
	@Column(name = "first_name")
	private String firstName;
	
	@Column(name = "second_name")
	private String secondName;
	
	@Column(name = "team_id")
	private String teamId;
	
	private int height;
	private int weight;
	private Date dob;
	
	@Column(name = "dfl_player_id")
	private int dflPlayerId;
	
	public String getPlayerId() {
		return playerId;
	}
	public void setPlayerId(String playerId) {
		this.playerId = playerId;
	}
	public int getJumperNo() {
		return jumperNo;
	}
	public void setJumperNo(int jumperNo) {
		this.jumperNo = jumperNo;
	}
	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	public String getSecondName() {
		return secondName;
	}
	public void setSecondName(String secondName) {
		this.secondName = secondName;
	}
	public String getTeamId() {
		return teamId;
	}
	public void setTeamId(String teamId) {
		this.teamId = teamId;
	}
	public int getHeight() {
		return height;
	}
	public void setHeight(int height) {
		this.height = height;
	}
	public int getWeight() {
		return weight;
	}
	public void setWeight(int weight) {
		this.weight = weight;
	}
	public Date getDob() {
		return dob;
	}
	public void setDob(Date dob) {
		this.dob = dob;
	}
	
	public int getDflPlayerId() {
		return dflPlayerId;
	}
	public void setDflPlayerId(int dflPlayerId) {
		this.dflPlayerId = dflPlayerId;
	}
	
	@Override
	public String toString() {
		return "AflPlayer [playerId=" + playerId + ", jumperNo=" + jumperNo + ", firstName=" + firstName
				+ ", secondName=" + secondName + ", teamId=" + teamId + ", height=" + height + ", weight=" + weight
				+ ", dob=" + dob + ", dflPlayerId=" + dflPlayerId + "]";
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + dflPlayerId;
		result = prime * result + ((dob == null) ? 0 : dob.hashCode());
		result = prime * result + ((firstName == null) ? 0 : firstName.hashCode());
		result = prime * result + height;
		result = prime * result + jumperNo;
		result = prime * result + ((playerId == null) ? 0 : playerId.hashCode());
		result = prime * result + ((secondName == null) ? 0 : secondName.hashCode());
		result = prime * result + ((teamId == null) ? 0 : teamId.hashCode());
		result = prime * result + weight;
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
		AflPlayer other = (AflPlayer) obj;
		if (dflPlayerId != other.dflPlayerId)
			return false;
		if (dob == null) {
			if (other.dob != null)
				return false;
		} else if (!dob.equals(other.dob))
			return false;
		if (firstName == null) {
			if (other.firstName != null)
				return false;
		} else if (!firstName.equals(other.firstName))
			return false;
		if (height != other.height)
			return false;
		if (jumperNo != other.jumperNo)
			return false;
		if (playerId == null) {
			if (other.playerId != null)
				return false;
		} else if (!playerId.equals(other.playerId))
			return false;
		if (secondName == null) {
			if (other.secondName != null)
				return false;
		} else if (!secondName.equals(other.secondName))
			return false;
		if (teamId == null) {
			if (other.teamId != null)
				return false;
		} else if (!teamId.equals(other.teamId))
			return false;
		if (weight != other.weight)
			return false;
		return true;
	}
}

