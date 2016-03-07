package net.dflmngr.model.service.impl;

import java.util.List;

import net.dflmngr.model.dao.RawPlayerStatsDao;
import net.dflmngr.model.dao.impl.RawPlayerStatsDaoImpl;
import net.dflmngr.model.entity.RawPlayerStats;
import net.dflmngr.model.entity.keys.RawPlayerStatsPK;
import net.dflmngr.model.service.RawPlayerStatsService;

public class RawPlayerStatsServiceImpl extends GenericServiceImpl<RawPlayerStats, RawPlayerStatsPK> implements RawPlayerStatsService {

	RawPlayerStatsDao dao;
	
	public RawPlayerStatsServiceImpl() {
		dao = new RawPlayerStatsDaoImpl();
		setDao(dao);
	}
	
	public List<RawPlayerStats> getForRound(int round) {
		List<RawPlayerStats> playerStats = dao.findForRound(round);
		return playerStats;
	}
	
	public void replaceAllForRound(int round, List<RawPlayerStats> playerStats) {
		
		dao.beginTransaction();
		
		List<RawPlayerStats> existingStats = getForRound(round);
		for(RawPlayerStats stats : existingStats) {
			delete(stats);
		}
		
		dao.flush();
		
		insertAll(playerStats, true);
		
		dao.commit();
	}

}
