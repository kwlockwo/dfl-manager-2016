package net.dflmngr.model.dao;

import java.util.List;

import net.dflmngr.model.entity.DflTeamScores;
import net.dflmngr.model.entity.keys.DflTeamScoresPK;

public interface DflTeamScoresDao extends GenericDao<DflTeamScores, DflTeamScoresPK> {
	public List<DflTeamScores> findForRound(int round);
}
