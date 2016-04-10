package net.dflmngr.model.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "dfl_player")
public class DflPlayer {
	
	@Id
	@Column(name = "player_id")
	private int playerId;
	
	@Column(name = "first_name")
	private String firstName;
	
	@Column(name = "last_name")
	private String lastName;
	
	private String inital;
	private String status;
	
	@Column(name = "afl_club")
	private String aflClub;
	private String position;
	
	@Column(name = "afl_player_id")
	private String aflPlayerId;
	
	@Column(name = "is_first_year")
	private boolean isFirstYear;

	public int getPlayerId() {
		return playerId;
	}

	public void setPlayerId(int playerId) {
		this.playerId = playerId;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getInital() {
		return inital;
	}

	public void setInital(String inital) {
		this.inital = inital;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getAflClub() {
		return aflClub;
	}

	public void setAflClub(String aflClub) {
		this.aflClub = aflClub;
	}

	public String getPosition() {
		return position;
	}

	public void setPosition(String position) {
		this.position = position;
	}

	public String getAflPlayerId() {
		return aflPlayerId;
	}

	public void setAflPlayerId(String aflPlayerId) {
		this.aflPlayerId = aflPlayerId;
	}
	public boolean isFirstYear() {
		return isFirstYear;
	}

	public void setFirstYear(boolean isFirstYear) {
		this.isFirstYear = isFirstYear;
	}

	@Override
	public String toString() {
		return "DflPlayer [playerId=" + playerId + ", firstName=" + firstName + ", lastName=" + lastName + ", inital="
				+ inital + ", status=" + status + ", aflClub=" + aflClub + ", position=" + position + ", aflPlayerId="
				+ aflPlayerId + ", isFirstYear=" + isFirstYear + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((aflClub == null) ? 0 : aflClub.hashCode());
		result = prime * result + ((aflPlayerId == null) ? 0 : aflPlayerId.hashCode());
		result = prime * result + ((firstName == null) ? 0 : firstName.hashCode());
		result = prime * result + ((inital == null) ? 0 : inital.hashCode());
		result = prime * result + (isFirstYear ? 1231 : 1237);
		result = prime * result + ((lastName == null) ? 0 : lastName.hashCode());
		result = prime * result + playerId;
		result = prime * result + ((position == null) ? 0 : position.hashCode());
		result = prime * result + ((status == null) ? 0 : status.hashCode());
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
		DflPlayer other = (DflPlayer) obj;
		if (aflClub == null) {
			if (other.aflClub != null)
				return false;
		} else if (!aflClub.equals(other.aflClub))
			return false;
		if (aflPlayerId == null) {
			if (other.aflPlayerId != null)
				return false;
		} else if (!aflPlayerId.equals(other.aflPlayerId))
			return false;
		if (firstName == null) {
			if (other.firstName != null)
				return false;
		} else if (!firstName.equals(other.firstName))
			return false;
		if (inital == null) {
			if (other.inital != null)
				return false;
		} else if (!inital.equals(other.inital))
			return false;
		if (isFirstYear != other.isFirstYear)
			return false;
		if (lastName == null) {
			if (other.lastName != null)
				return false;
		} else if (!lastName.equals(other.lastName))
			return false;
		if (playerId != other.playerId)
			return false;
		if (position == null) {
			if (other.position != null)
				return false;
		} else if (!position.equals(other.position))
			return false;
		if (status == null) {
			if (other.status != null)
				return false;
		} else if (!status.equals(other.status))
			return false;
		return true;
	}
}
