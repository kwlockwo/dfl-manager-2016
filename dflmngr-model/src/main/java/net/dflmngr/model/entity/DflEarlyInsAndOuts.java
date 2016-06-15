package net.dflmngr.model.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;

import net.dflmngr.model.entity.keys.DflEarlyInsAndOutsPK;

@Entity
@Table(name = "dfl_early_ins_and_outs")
@IdClass(DflEarlyInsAndOutsPK.class)

public class DflEarlyInsAndOuts {
	
	@Id @Column(name = "team_code")
	private String teamCode;
	
	@Id @Column(name = "round")
	private int round;
	
	@Id @Column(name = "team_player_id")
	private int teamPlayerId;
	
	@Column(name = "in_or_out")
	private String inOrOut;
	
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
	
	public int getTeamPlayerId() {
		return teamPlayerId;
	}
	
	public void setTeamPlayerId(int teamPlayerId) {
		this.teamPlayerId = teamPlayerId;
	}
	
	public String getInOrOut() {
		return inOrOut;
	}
	
	public void setInOrOut(String inOrOut) {
		this.inOrOut = inOrOut;
	}

	@Override
	public String toString() {
		return "DflEarlyInsAndOuts [teamCode=" + teamCode + ", round=" + round
				+ ", teamPlayerId=" + teamPlayerId + ", inOrOut=" + inOrOut
				+ "]";
	}

}
