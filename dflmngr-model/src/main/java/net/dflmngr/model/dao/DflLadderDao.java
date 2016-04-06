package net.dflmngr.model.dao;

import java.util.List;

import net.dflmngr.model.entity.DflLadder;
import net.dflmngr.model.entity.keys.DflLadderPK;

public interface DflLadderDao extends GenericDao<DflLadder, DflLadderPK> {
	public List<DflLadder> findLadderForRound(int round);
}