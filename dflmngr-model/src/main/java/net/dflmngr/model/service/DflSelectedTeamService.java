package net.dflmngr.model.service;

import java.util.List;

import net.dflmngr.model.entity.DflSelectedPlayer;
import net.dflmngr.model.entity.keys.DflSelectedPlayerPK;

public interface DflSelectedTeamService extends GenericService<DflSelectedPlayer, DflSelectedPlayerPK> {
	public List<DflSelectedPlayer> getAllForRound(int round);
	public List<DflSelectedPlayer> getSelectedTeamForRound(int round, String teamCode);
	public void replaceAllForRound(int round, List<DflSelectedPlayer> selectedTeam);
	public void replaceTeamForRound(int round, String teamCode, List<DflSelectedPlayer> selectedTeam);
}
