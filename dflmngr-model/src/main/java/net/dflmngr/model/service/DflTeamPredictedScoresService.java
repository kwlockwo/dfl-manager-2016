package net.dflmngr.model.service;

import java.util.List;
import java.util.Map;

import net.dflmngr.model.entity.DflTeamPredictedScores;
import net.dflmngr.model.entity.keys.DflTeamPredictedScoresPK;

public interface DflTeamPredictedScoresService extends GenericService<DflTeamPredictedScores, DflTeamPredictedScoresPK> {
	public DflTeamPredictedScores getTeamPredictedScoreForRound(String teamCode, int round);
	public List<DflTeamPredictedScores> getForRound(int round);
	public Map<String, DflTeamPredictedScores> getForRoundWithKey(int round);
	List<DflTeamPredictedScores> getAllForRound(int round);
	public void replaceAllForRound(int round, List<DflTeamPredictedScores> predictedScores);
}
