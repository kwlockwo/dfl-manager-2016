package net.dflmngr.model.service;

import net.dflmngr.model.entity.DflTeamPlayer;

public interface DflTeamPlayerService extends GenericService<DflTeamPlayer, Integer> {
	public DflTeamPlayer getTeamPlayerForTeam(String teamCode, int teamPlayerId);
}
