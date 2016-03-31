package net.dflmngr.model.dao;

import java.util.List;

import net.dflmngr.model.entity.DflSelectedPlayer;
import net.dflmngr.model.entity.keys.DflSelectedPlayerPK;

public interface DflSelectedPlayerDao extends GenericDao<DflSelectedPlayer, DflSelectedPlayerPK> {
	public List<DflSelectedPlayer> findSelectedTeamForRound(int round, String teamCode);
	public List<DflSelectedPlayer> findAllForRound(int round);
}
