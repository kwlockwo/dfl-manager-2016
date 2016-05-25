package net.dflmngr.model.service;

import java.util.List;
import java.util.Map;

import net.dflmngr.model.entity.DflPlayerPredictedScores;
import net.dflmngr.model.entity.keys.DflPlayerPredictedScoresPK;

public interface DflPlayerPredictedScoresService extends GenericService<DflPlayerPredictedScores, DflPlayerPredictedScoresPK> {
	public List<DflPlayerPredictedScores> getForRound(int round);
	public Map<Integer, DflPlayerPredictedScores> getForRoundWithKey(int round);
	public void replaceAllForRound(int round, List<DflPlayerPredictedScores> predictedScores);
}
