package net.dflmngr.model.dao;

import java.util.List;

import net.dflmngr.model.entity.DflMatthewAllen;

public interface DflMatthewAllenDao extends GenericDao<DflMatthewAllen, Integer> {
	List<DflMatthewAllen> findForRound(int round);
	public DflMatthewAllen findLastVotes(int playerId);
	public void removeForRound(int round);
}
