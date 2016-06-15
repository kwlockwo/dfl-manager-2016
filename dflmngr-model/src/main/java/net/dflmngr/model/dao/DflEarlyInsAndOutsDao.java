package net.dflmngr.model.dao;

import java.util.List;

import net.dflmngr.model.entity.DflEarlyInsAndOuts;
import net.dflmngr.model.entity.keys.DflEarlyInsAndOutsPK;

public interface DflEarlyInsAndOutsDao extends GenericDao<DflEarlyInsAndOuts, DflEarlyInsAndOutsPK> {
	public List<DflEarlyInsAndOuts> findByTeamAndRound(int round, String teamCode);
}
