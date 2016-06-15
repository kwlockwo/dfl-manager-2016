package net.dflmngr.model.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.dflmngr.model.dao.DflSelectedPlayerDao;
import net.dflmngr.model.dao.impl.DflSelectedPlayerDaoImpl;
import net.dflmngr.model.entity.DflSelectedPlayer;
import net.dflmngr.model.entity.keys.DflSelectedPlayerPK;
import net.dflmngr.model.service.DflSelectedTeamService;

public class DflSelectedTeamServiceImpl extends GenericServiceImpl<DflSelectedPlayer, DflSelectedPlayerPK> implements DflSelectedTeamService {
	
	private DflSelectedPlayerDao dao;
	
	public DflSelectedTeamServiceImpl() {
		dao = new DflSelectedPlayerDaoImpl();
		super.setDao(dao);
	}
	
	public List<DflSelectedPlayer> getAllForRound(int round) {
		List<DflSelectedPlayer> selectedRound = dao.findAllForRound(round);
		return selectedRound;
	}
	
	public List<DflSelectedPlayer> getSelectedTeamForRound(int round, String teamCode) {
		List<DflSelectedPlayer> selectedTeam = dao.findSelectedTeamForRound(round, teamCode);
		return selectedTeam;
	}
	
	public void replaceAllForRound(int round, List<DflSelectedPlayer> selectedTeam) {
		
		List<DflSelectedPlayer> existingTeam = getAllForRound(round);
		
		dao.beginTransaction();
		
		for(DflSelectedPlayer stats : existingTeam) {
			delete(stats);
		}
		
		dao.flush();
		
		insertAll(selectedTeam, true);
		
		dao.commit();
	}
	
	public void replaceTeamForRound(int round, String teamCode, List<DflSelectedPlayer> selectedTeam) {
		
		List<DflSelectedPlayer> existingTeam = getSelectedTeamForRound(round, teamCode);
		
		dao.beginTransaction();
		
		for(DflSelectedPlayer stats : existingTeam) {
			delete(stats);
		}
		
		dao.flush();
		
		insertAll(selectedTeam, true);
		
		dao.commit();
	}
	
	public Map<Integer, DflSelectedPlayer> getForRoundWithKey(int round) {
		Map<Integer, DflSelectedPlayer> selectedPlayersWithKey = new HashMap<>();
		List<DflSelectedPlayer> selectedPlayers = getAllForRound(round);
		
		for(DflSelectedPlayer player : selectedPlayers) {
			selectedPlayersWithKey.put(player.getPlayerId(), player);
		}
		
		return selectedPlayersWithKey;
	}
}
