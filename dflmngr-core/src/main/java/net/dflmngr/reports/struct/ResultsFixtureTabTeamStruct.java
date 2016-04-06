package net.dflmngr.reports.struct;

import java.util.Comparator;

public class ResultsFixtureTabTeamStruct implements Comparator<ResultsFixtureTabTeamStruct>, Comparable<ResultsFixtureTabTeamStruct> {
	
	private int no;
	private String player;
	private String position;
	private int kicks;
	private int handballs;
	private int disposals;
	private int marks;
	private int hitouts;
	private int freesFor;
	private int freesAgainst;
	private int tackles;
	private int goals;
	private int behinds;
	private String score;
	
	public int getNo() {
		return no;
	}
	public void setNo(int no) {
		this.no = no;
	}
	public String getPlayer() {
		return player;
	}
	public void setPlayer(String player) {
		this.player = player;
	}
	public String getPosition() {
		return position;
	}
	public void setPosition(String position) {
		this.position = position;
	}
	public int getKicks() {
		return kicks;
	}
	public void setKicks(int kicks) {
		this.kicks = kicks;
	}
	public int getHandballs() {
		return handballs;
	}
	public void setHandballs(int handballs) {
		this.handballs = handballs;
	}
	public int getDisposals() {
		return disposals;
	}
	public void setDisposals(int disposals) {
		this.disposals = disposals;
	}
	public int getMarks() {
		return marks;
	}
	public void setMarks(int marks) {
		this.marks = marks;
	}
	public int getHitouts() {
		return hitouts;
	}
	public void setHitouts(int hitouts) {
		this.hitouts = hitouts;
	}
	public int getFreesFor() {
		return freesFor;
	}
	public void setFreesFor(int freesFor) {
		this.freesFor = freesFor;
	}
	public int getFreesAgainst() {
		return freesAgainst;
	}
	public void setFreesAgainst(int freesAgainst) {
		this.freesAgainst = freesAgainst;
	}
	public int getTackles() {
		return tackles;
	}
	public void setTackles(int tackles) {
		this.tackles = tackles;
	}
	public int getGoals() {
		return goals;
	}
	public void setGoals(int goals) {
		this.goals = goals;
	}
	public int getBehinds() {
		return behinds;
	}
	public void setBehinds(int behinds) {
		this.behinds = behinds;
	}
	public String getScore() {
		return score;
	}
	public int getScoreInt() {
		return Integer.parseInt(score);
	}
	public void setScore(String score) {
		this.score = score;
	}
	public void setScoreInt(int score) {
		this.score = Integer.toString(score);
	}
	
	@Override
	public int compareTo(ResultsFixtureTabTeamStruct o) {
		
		int equal = 0;
		int less = -1;
		int greater = 1;
		
		String position = this.getPosition();
		String oPosition = o.getPosition();
		
		if(position.compareTo(oPosition) < 0) {
			return less;
		}
		if(position.compareTo(oPosition) > 0) {
			return greater;
		}
		if(position.compareTo(oPosition) == 0) {
			if(this.getScore().equals("dnp")) {
				if(o.getScore().equals("dnp")) {
					return equal;
				} else {
					return greater;
				}
			}
			if(o.getScore().equals("dnp")) {
				return less;
			}
			
			int score = Integer.parseInt(this.getScore());
			int oScore = Integer.parseInt(o.getScore());
			
			if(score > oScore) {
				return less;
			}
			if(score < oScore) {
				return greater;
			}
			if(score == oScore) {
				return equal;
			}		
		}
		
		return 0;
	}
	
	@Override
	public int compare(ResultsFixtureTabTeamStruct o1, ResultsFixtureTabTeamStruct o2) {

		int equal = 0;
		int less = -1;
		int greater = 1;
		
		String o1Position = o2.getPosition();
		String o2Position = o2.getPosition();
		
		if(o1Position.compareTo(o2Position) < 0) {
			return less;
		}
		if(o1Position.compareTo(o2Position) > 0) {
			return greater;
		}
		if(o1Position.compareTo(o2Position) == 0) {
			if(o1.getScore().equals("dnp")) {
				if(o2.getScore().equals("dnp")) {
					return equal;
				} else {
					return less;
				}
			}
			if(o2.getScore().equals("dnp")) {
				return greater;
			}
			
			int o1Score = Integer.parseInt(o1.getScore());
			int o2Score = Integer.parseInt(o2.getScore());
			
			if(o1Score > o2Score) {
				return less;
			}
			if(o1Score < o2Score) {
				return greater;
			}
			if(o1Score == o2Score) {
				return equal;
			}		
		}
		
		return 0;
	}
}
