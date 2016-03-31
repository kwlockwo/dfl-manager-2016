package net.dflmngr.model.service.impl;

import net.dflmngr.model.dao.AflTeamDao;
import net.dflmngr.model.dao.impl.AflTeamDaoImpl;
import net.dflmngr.model.entity.AflTeam;
import net.dflmngr.model.service.AflTeamService;

public class AflTeamServiceImpl extends GenericServiceImpl<AflTeam, String> implements AflTeamService {
	
	private AflTeamDao dao;
	
	public AflTeamServiceImpl() {
		dao = new AflTeamDaoImpl();
		super.setDao(dao);
	}
}
