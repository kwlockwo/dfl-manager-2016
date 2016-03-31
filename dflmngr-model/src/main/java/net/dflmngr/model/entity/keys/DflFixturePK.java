package net.dflmngr.model.entity.keys;

public class DflFixturePK {
	
	private int round;
	private int game;
	
	public int getRound() {
		return round;
	}
	public void setRound(int round) {
		this.round = round;
	}
	public int getGame() {
		return game;
	}
	public void setGame(int game) {
		this.game = game;
	}
	
	@Override
	public String toString() {
		return "DflFixturePK [round=" + round + ", game=" + game + "]";
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + game;
		result = prime * result + round;
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
		DflFixturePK other = (DflFixturePK) obj;
		if (game != other.game)
			return false;
		if (round != other.round)
			return false;
		return true;
	}
}
