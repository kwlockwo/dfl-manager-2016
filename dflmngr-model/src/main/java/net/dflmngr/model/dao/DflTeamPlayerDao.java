package net.dflmngr.model.dao;

import net.dflmngr.model.entity.DflTeamPlayer;

public interface DflTeamPlayerDao extends GenericDao<DflTeamPlayer, Integer> {
	public DflTeamPlayer findTeamPlayerForTeam(String teamCode, int teamPlayerId);
}
