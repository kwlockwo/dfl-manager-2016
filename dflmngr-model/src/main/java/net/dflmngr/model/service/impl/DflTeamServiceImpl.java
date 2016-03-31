package net.dflmngr.model.service.impl;

import net.dflmngr.model.dao.DflTeamDao;
import net.dflmngr.model.dao.impl.DflTeamDaoImpl;
import net.dflmngr.model.entity.DflTeam;
import net.dflmngr.model.service.DflTeamService;

public class DflTeamServiceImpl extends GenericServiceImpl<DflTeam, String> implements DflTeamService {
	
	private DflTeamDao dao;
	
	public DflTeamServiceImpl() {
		dao = new DflTeamDaoImpl();
		super.setDao(dao);
	}
}
