package net.dflmngr.model.entity;

import java.io.Serializable;
import javax.persistence.*;

import net.dflmngr.model.entity.keys.AflFixturePK;

import java.util.Comparator;
import java.util.Date;

@Entity
@Table(name="afl_fixture")
@IdClass(AflFixturePK.class)
public class AflFixture implements Comparator<AflFixture>, Comparable<AflFixture>, Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	private int round;
	
	@Id
	private int game;

	@Column(name="away_team")
	private String awayTeam;

	@Column(name="home_team")
	private String homeTeam;
	
	@Column
	private String ground;

	@Temporal(TemporalType.TIMESTAMP)
	private Date start;
	
	@Column
	private String timezone;

	public String getAwayTeam() {
		return this.awayTeam;
	}

	public void setAwayTeam(String awayTeam) {
		this.awayTeam = awayTeam;
	}

	public int getGame() {
		return this.game;
	}

	public void setGame(int game) {
		this.game = game;
	}

	public String getHomeTeam() {
		return this.homeTeam;
	}

	public void setHomeTeam(String homeTeam) {
		this.homeTeam = homeTeam;
	}

	public int getRound() {
		return this.round;
	}

	public void setRound(int round) {
		this.round = round;
	}

	public Date getStart() {
		return this.start;
	}

	public void setStart(Date start) {
		this.start = start;
	}

	public String getGround() {
		return ground;
	}

	public void setGround(String ground) {
		this.ground = ground;
	}

	public String getTimezone() {
		return timezone;
	}

	public void setTimezone(String timezone) {
		this.timezone = timezone;
	}

	@Override
	public String toString() {
		return "AflFixture [round=" + round + ", game=" + game + ", awayTeam=" + awayTeam + ", homeTeam=" + homeTeam
				+ ", ground=" + ground + ", start=" + start + ", timezone=" + timezone + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((awayTeam == null) ? 0 : awayTeam.hashCode());
		result = prime * result + game;
		result = prime * result + ((ground == null) ? 0 : ground.hashCode());
		result = prime * result + ((homeTeam == null) ? 0 : homeTeam.hashCode());
		result = prime * result + round;
		result = prime * result + ((start == null) ? 0 : start.hashCode());
		result = prime * result + ((timezone == null) ? 0 : timezone.hashCode());
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
		AflFixture other = (AflFixture) obj;
		if (awayTeam == null) {
			if (other.awayTeam != null)
				return false;
		} else if (!awayTeam.equals(other.awayTeam))
			return false;
		if (game != other.game)
			return false;
		if (ground == null) {
			if (other.ground != null)
				return false;
		} else if (!ground.equals(other.ground))
			return false;
		if (homeTeam == null) {
			if (other.homeTeam != null)
				return false;
		} else if (!homeTeam.equals(other.homeTeam))
			return false;
		if (round != other.round)
			return false;
		if (start == null) {
			if (other.start != null)
				return false;
		} else if (!start.equals(other.start))
			return false;
		if (timezone == null) {
			if (other.timezone != null)
				return false;
		} else if (!timezone.equals(other.timezone))
			return false;
		return true;
	}

	@Override
	public int compareTo(AflFixture in) {
		Integer gameRound = Integer.parseInt(Integer.toString(this.round) + Integer.toString(this.game));
		Integer inGameRound = Integer.parseInt(Integer.toString(in.round) + Integer.toString(in.game));
		
		return gameRound.compareTo(inGameRound);
	}

	@Override
	public int compare(AflFixture a1, AflFixture a2) {
		Integer a1gameRound = Integer.parseInt(Integer.toString(a1.round) + Integer.toString(a1.game));
		Integer a2gameRound = Integer.parseInt(Integer.toString(a2.round) + Integer.toString(a2.game));
		
		return a1gameRound - a2gameRound;
	}
}