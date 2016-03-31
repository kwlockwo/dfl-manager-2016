package net.dflmngr.model.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "dfl_team")
public class DflTeam {
	
	@Id
	@Column(name = "team_code")
	private String teamCode;
	private String name;
	
	@Column(name = "short_name")
	private String shortName;
	
	@Column(name = "coach_name")
	private String coachName;
	
	@Column(name = "home_ground")
	private String homeGround;
	
	private String colours;
	
	@Column(name = "coach_email")
	private String coachEmail;
	
	public String getTeamCode() {
		return teamCode;
	}
	public void setTeamCode(String teamCode) {
		this.teamCode = teamCode;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getShortName() {
		return shortName;
	}
	public void setShortName(String shortName) {
		this.shortName = shortName;
	}
	public String getCoachName() {
		return coachName;
	}
	public void setCoachName(String coachName) {
		this.coachName = coachName;
	}
	public String getHomeGround() {
		return homeGround;
	}
	public void setHomeGround(String homeGround) {
		this.homeGround = homeGround;
	}
	public String getColours() {
		return colours;
	}
	public void setColours(String colours) {
		this.colours = colours;
	}
	public String getCoachEmail() {
		return coachEmail;
	}
	public void setCoachEmail(String coachEmail) {
		this.coachEmail = coachEmail;
	}
	
	@Override
	public String toString() {
		return "DflTeam [teamCode=" + teamCode + ", name=" + name + ", shortName=" + shortName + ", coachName="
				+ coachName + ", homeGround=" + homeGround + ", colours=" + colours + ", coachEmail=" + coachEmail
				+ "]";
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((coachEmail == null) ? 0 : coachEmail.hashCode());
		result = prime * result + ((coachName == null) ? 0 : coachName.hashCode());
		result = prime * result + ((colours == null) ? 0 : colours.hashCode());
		result = prime * result + ((homeGround == null) ? 0 : homeGround.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((shortName == null) ? 0 : shortName.hashCode());
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
		DflTeam other = (DflTeam) obj;
		if (coachEmail == null) {
			if (other.coachEmail != null)
				return false;
		} else if (!coachEmail.equals(other.coachEmail))
			return false;
		if (coachName == null) {
			if (other.coachName != null)
				return false;
		} else if (!coachName.equals(other.coachName))
			return false;
		if (colours == null) {
			if (other.colours != null)
				return false;
		} else if (!colours.equals(other.colours))
			return false;
		if (homeGround == null) {
			if (other.homeGround != null)
				return false;
		} else if (!homeGround.equals(other.homeGround))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (shortName == null) {
			if (other.shortName != null)
				return false;
		} else if (!shortName.equals(other.shortName))
			return false;
		if (teamCode == null) {
			if (other.teamCode != null)
				return false;
		} else if (!teamCode.equals(other.teamCode))
			return false;
		return true;
	}
}
