package net.dflmngr.model.dao;

import java.util.List;

import net.dflmngr.model.entity.DflPlayerPredictedScores;
import net.dflmngr.model.entity.keys.DflPlayerPredictedScoresPK;

public interface DflPlayerPredictedScoresDao extends GenericDao<DflPlayerPredictedScores, DflPlayerPredictedScoresPK> {
	public List<DflPlayerPredictedScores> findForRound(int round);
}
