package net.dflmngr.model.dao;

import java.util.List;

import net.dflmngr.model.entity.DflPlayerScores;
import net.dflmngr.model.entity.keys.DflPlayerScoresPK;

public interface DflPlayerScoresDao extends GenericDao<DflPlayerScores, DflPlayerScoresPK> {
	public List<DflPlayerScores> findForRound(int round);
	public List<DflPlayerScores> findForRoundAndTeam(int round, String teamCode);
}
