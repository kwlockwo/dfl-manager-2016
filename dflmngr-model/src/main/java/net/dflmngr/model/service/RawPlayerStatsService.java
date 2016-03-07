package net.dflmngr.model.service;

import java.util.List;

import net.dflmngr.model.entity.RawPlayerStats;
import net.dflmngr.model.entity.keys.RawPlayerStatsPK;

public interface RawPlayerStatsService extends GenericService<RawPlayerStats, RawPlayerStatsPK> {
	
	public List<RawPlayerStats> getForRound(int round);
	public void replaceAllForRound(int round, List<RawPlayerStats> playerStats);
}
