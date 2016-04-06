package net.dflmngr.model.service;

import java.util.List;
import java.util.Map;

import net.dflmngr.model.entity.DflLadder;
import net.dflmngr.model.entity.keys.DflLadderPK;

public interface DflLadderService extends GenericService<DflLadder, DflLadderPK> {
	public List<DflLadder> getLadderForRound(int round);
	public List<DflLadder> getPreviousRoundLadder(int round);
	public Map<String, DflLadder> getForRoundWithKey(int round);
	public Map<String, DflLadder> getForPeviousRoundWithKey(int round);
	public void replaceAllForRound(int round, List<DflLadder> ladder);
}
