package net.dflmngr.model.dao;

import java.util.List;

import net.dflmngr.model.entity.DflTeamPredictedScores;
import net.dflmngr.model.entity.keys.DflTeamPredictedScoresPK;

public interface DflTeamPredictedScoresDao extends GenericDao<DflTeamPredictedScores, DflTeamPredictedScoresPK> {
	public List<DflTeamPredictedScores> findForRound(int round);
	public List<DflTeamPredictedScores> findAllForRound(int round);
}
