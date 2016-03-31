package net.dflmngr.model.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
	
	public Map<String, RawPlayerStats> getForRoundWithKey(int round) {
		Map<String, RawPlayerStats> playerStatsWithKey = new HashMap<>();
		
		List<RawPlayerStats> playerStats = dao.findForRound(round);
		
		for(RawPlayerStats stats : playerStats) {
			String key = stats.getTeam() + stats.getJumperNo();
			playerStatsWithKey.put(key, stats);
		}
		
		return playerStatsWithKey;
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
