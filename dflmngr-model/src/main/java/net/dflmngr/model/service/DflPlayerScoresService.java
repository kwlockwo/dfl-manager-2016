package net.dflmngr.model.service;

import java.util.List;
import java.util.Map;

import net.dflmngr.model.entity.DflPlayerScores;
import net.dflmngr.model.entity.keys.DflPlayerScoresPK;

public interface DflPlayerScoresService extends GenericService<DflPlayerScores, DflPlayerScoresPK> {
	public List<DflPlayerScores> getForRound(int round);
	public List<DflPlayerScores> getForRoundAndTeam(int round, String teamCode);
	public Map<Integer, DflPlayerScores> getForRoundWithKey(int round);
	public Map<Integer, DflPlayerScores> getForRoundAndTeamWithKey(int round, String teamCode);
	public void replaceAllForRound(int round, List<DflPlayerScores> playerScores);
	public Map<Integer, List<DflPlayerScores>> getAllWithKey();
	public Map<Integer, List<DflPlayerScores>> getUptoRoundWithKey(int round);
}
