package net.dflmngr.model.service.impl;

import net.dflmngr.model.dao.AflPlayerDao;
import net.dflmngr.model.dao.impl.AflPlayerDaoImpl;
import net.dflmngr.model.entity.AflPlayer;
import net.dflmngr.model.service.AflPlayerService;

public class AflPlayerServiceImpl extends GenericServiceImpl<AflPlayer, String> implements AflPlayerService {
	
	private AflPlayerDao dao;
	
	public AflPlayerServiceImpl() {
		dao = new AflPlayerDaoImpl();
		super.setDao(dao);
	}

}
