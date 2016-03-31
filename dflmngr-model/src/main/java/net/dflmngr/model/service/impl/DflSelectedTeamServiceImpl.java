package net.dflmngr.model.service.impl;

import java.util.List;

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
		
		dao.beginTransaction();
		
		List<DflSelectedPlayer> existingTeam = getAllForRound(round);
		for(DflSelectedPlayer stats : existingTeam) {
			delete(stats);
		}
		
		dao.flush();
		
		insertAll(selectedTeam, true);
		
		dao.commit();
	}
	
	public void replaceTeamForRound(int round, String teamCode, List<DflSelectedPlayer> selectedTeam) {
		
		dao.beginTransaction();
		
		List<DflSelectedPlayer> existingTeam = getSelectedTeamForRound(round, teamCode);
		for(DflSelectedPlayer stats : existingTeam) {
			delete(stats);
		}
		
		dao.flush();
		
		insertAll(selectedTeam, true);
		
		dao.commit();
	}
}
