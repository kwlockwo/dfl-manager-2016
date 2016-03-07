package net.dflmngr.model.dao;

import java.util.List;

import net.dflmngr.model.entity.RawPlayerStats;
import net.dflmngr.model.entity.keys.RawPlayerStatsPK;

public interface RawPlayerStatsDao extends GenericDao<RawPlayerStats, RawPlayerStatsPK> {
	
	public List<RawPlayerStats> findForRound(int round);
	
}
