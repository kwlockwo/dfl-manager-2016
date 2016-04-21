package net.dflmngr.model.service;

import java.util.List;

import net.dflmngr.model.entity.DflMatthewAllen;

public interface DflMatthewAllenService extends GenericService<DflMatthewAllen, Integer> {
	public List<DflMatthewAllen> getForRound(int round);
	public DflMatthewAllen getLastVotes(int playerId);
	public void replaceAllForRound(int round, List<DflMatthewAllen> votes);
	public void deleteForRound(int round);
}
