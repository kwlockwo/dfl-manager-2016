package net.dflmngr.reports.struct;

import java.util.Comparator;

public class ResultsFixtureTabTeamStruct implements Comparator<ResultsFixtureTabTeamStruct>, Comparable<ResultsFixtureTabTeamStruct> {
	
	private boolean played;
	private boolean dnp;
	private int no;
	private String player;
	private String position;
	private String kicks;
	private String handballs;
	private String disposals;
	private String marks;
	private String hitouts;
	private String freesFor;
	private String freesAgainst;
	private String tackles;
	private String goals;
	private String behinds;
	private String score;
	private int predicted;
	private String trend;
	
	
	public boolean hasPlayed() {
		return played;
	}
	public void setPlayed(boolean played) {
		this.played = played;
	}
	public boolean isDnp() {
		return dnp;
	}
	public void setDnp(boolean dnp) {
		this.dnp = dnp;
	}
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
	public String getKicks() {
		return kicks;
	}
	public void setKicks(String kicks) {
		this.kicks = kicks;
	}
	public String getHandballs() {
		return handballs;
	}
	public void setHandballs(String handballs) {
		this.handballs = handballs;
	}
	public String getDisposals() {
		return disposals;
	}
	public void setDisposals(String disposals) {
		this.disposals = disposals;
	}
	public String getMarks() {
		return marks;
	}
	public void setMarks(String marks) {
		this.marks = marks;
	}
	public String getHitouts() {
		return hitouts;
	}
	public void setHitouts(String hitouts) {
		this.hitouts = hitouts;
	}
	public String getFreesFor() {
		return freesFor;
	}
	public void setFreesFor(String freesFor) {
		this.freesFor = freesFor;
	}
	public String getFreesAgainst() {
		return freesAgainst;
	}
	public void setFreesAgainst(String freesAgainst) {
		this.freesAgainst = freesAgainst;
	}
	public String getTackles() {
		return tackles;
	}
	public void setTackles(String tackles) {
		this.tackles = tackles;
	}
	public String getGoals() {
		return goals;
	}
	public void setGoals(String goals) {
		this.goals = goals;
	}
	public String getBehinds() {
		return behinds;
	}
	public void setBehinds(String behinds) {
		this.behinds = behinds;
	}
	public String getScore() {
		return score;
	}
	public void setScore(String score) {
		this.score = score;
	}
	public int getPredicted() {
		return predicted;
	}
	public void setPredicted(int predicted) {
		this.predicted = predicted;
	}
	public String getTrend() {
		return trend;
	}
	public void setTrend(String trend) {
		this.trend = trend;
	}
	
	public int getKicksInt() {
		return Integer.parseInt(kicks);
	}
	public void setKicksInt(int kicks) {
		this.kicks = Integer.toString(kicks);
	}
	public int getHandballsInt() {
		return Integer.parseInt(handballs);
	}
	public void setHandballsInt(int handballs) {
		this.handballs = Integer.toString(handballs);
	}
	public int getDisposalsInt() {
		return Integer.parseInt(disposals);
	}
	public void setDisposalsInt(int disposals) {
		this.disposals = Integer.toString(disposals);
	}
	public int getMarksInt() {
		return Integer.parseInt(marks);
	}
	public void setMarksInt(int marks) {
		this.marks = Integer.toString(marks);
	}
	public int getHitoutsInt() {
		return Integer.parseInt(hitouts);
	}
	public void setHitoutsInt(int hitouts) {
		this.hitouts = Integer.toString(hitouts);
	}
	public int getFreesForInt() {
		return Integer.parseInt(freesFor);
	}
	public void setFreesForInt(int freesFor) {
		this.freesFor = Integer.toString(freesFor);
	}
	public int getFreesAgainstInt() {
		return Integer.parseInt(freesAgainst);
	}
	public void setFreesAgainstInt(int freesAgainst) {
		this.freesAgainst = Integer.toString(freesAgainst);
	}
	public int getTacklesInt() {
		return Integer.parseInt(tackles);
	}
	public void setTacklesInt(int tackles) {
		this.tackles = Integer.toString(tackles);
	}
	public int getGoalsInt() {
		return Integer.parseInt(goals);
	}
	public void setGoalsInt(int goals) {
		this.goals = Integer.toString(goals);
	}
	public int getBehindsInt() {
		return Integer.parseInt(behinds);
	}
	public void setBehindsInt(int behinds) {
		this.behinds = Integer.toString(behinds);
	}
	public int getScoreInt() {
		return Integer.parseInt(score);
	}
	public void setScoreInt(int score) {
		this.score = Integer.toString(score);
	}
	public int getTrendInt() {
		return Integer.parseInt(trend);
	}
	public void setTrendInt(int trend) {
		this.trend = Integer.toString(trend);
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
			
			int score = this.getScore().equals("") ? 0 : Integer.parseInt(this.getScore());
			int oScore = o.getScore().equals("") ? 0 : Integer.parseInt(o.getScore());
			
			if(score > oScore) {
				return less;
			}
			if(score < oScore) {
				return greater;
			}
			if(score == oScore) {
				
				int predicted = this.getPredicted();
				int oPredicted = o.getPredicted();
				
				if(predicted > oPredicted) {
					return less;
				}
				if(predicted < oPredicted) {
					return greater;
				}
				if(predicted == oPredicted) {
					return equal;
				}
			}		
		}
		
		return equal;
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
				
				int o1Predicted = o1.getPredicted();
				int o2Predicted = o2.getPredicted();
				
				if(o1Predicted > o2Predicted) {
					return less;
				}
				if(o1Predicted < o2Predicted) {
					return greater;
				}
				if(o1Predicted == o2Predicted) {
					return equal;
				}
			}		
		}
		
		return 0;
	}
}
