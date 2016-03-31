package net.dflmngr.model.service.impl;

import net.dflmngr.model.dao.DflTeamPlayerDao;
import net.dflmngr.model.dao.impl.DflTeamPlayerDaoImpl;
import net.dflmngr.model.entity.DflTeamPlayer;
import net.dflmngr.model.service.DflTeamPlayerService;

public class DflTeamPlayerServiceImpl extends GenericServiceImpl<DflTeamPlayer, Integer> implements DflTeamPlayerService {
	
	private DflTeamPlayerDao dao;
	
	public DflTeamPlayerServiceImpl() {
		dao = new DflTeamPlayerDaoImpl();
		super.setDao(dao);
	}
	
	public DflTeamPlayer getTeamPlayerForTeam(String teamCode, int teamPlayerId) {
		DflTeamPlayer teamPlayer = dao.findTeamPlayerForTeam(teamCode, teamPlayerId);
		return teamPlayer;
	}

}
