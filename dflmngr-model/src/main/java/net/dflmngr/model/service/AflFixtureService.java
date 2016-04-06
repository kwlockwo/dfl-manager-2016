package net.dflmngr.model.service;

import java.util.List;
import java.util.Map;

import net.dflmngr.model.entity.AflFixture;
import net.dflmngr.model.entity.keys.AflFixturePK;

public interface AflFixtureService extends GenericService<AflFixture, AflFixturePK> {
	
	public List<AflFixture> getAflFixturesForRound(int round);
	public Map<Integer, List<AflFixture>> getAflFixturneRoundBlocks();
	public List<AflFixture> getAflFixturesPlayedForRound(int round);
	public AflFixture getPlayedGame(int round, int game);
	public List<String> getAflTeamsPlayedForRound(int round);
}
