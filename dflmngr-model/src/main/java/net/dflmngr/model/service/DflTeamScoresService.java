package net.dflmngr.model.service;

import java.util.List;
import java.util.Map;

import net.dflmngr.model.entity.DflTeamScores;
import net.dflmngr.model.entity.keys.DflTeamScoresPK;

public interface DflTeamScoresService extends GenericService<DflTeamScores, DflTeamScoresPK> {
	public List<DflTeamScores> getForRound(int round);
	public Map<String, DflTeamScores> getForRoundWithKey(int round);
	public void replaceAllForRound(int round, List<DflTeamScores> teamScores);
}
