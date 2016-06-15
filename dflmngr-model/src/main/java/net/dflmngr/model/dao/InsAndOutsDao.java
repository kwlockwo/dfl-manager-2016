package net.dflmngr.model.dao;

import java.util.List;

import net.dflmngr.model.entity.InsAndOuts;
import net.dflmngr.model.entity.keys.InsAndOutsPK;

public interface InsAndOutsDao extends GenericDao<InsAndOuts, InsAndOutsPK> {

	public List<InsAndOuts> findByTeamAndRound(int round, String teamCode);
	public List<InsAndOuts> findByRound(int round);
}
