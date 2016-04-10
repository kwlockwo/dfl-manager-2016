package net.dflmngr.model.service.impl;

import net.dflmngr.model.dao.DflMatthewAllenDao;
import net.dflmngr.model.dao.impl.DflMatthewAllenDaoImpl;
import net.dflmngr.model.entity.DflMatthewAllen;
import net.dflmngr.model.service.DflMatthewAllenService;

public class DflMatthewAllenServiceImpl extends GenericServiceImpl<DflMatthewAllen, Integer> implements DflMatthewAllenService {
	private DflMatthewAllenDao dao;
	
	public DflMatthewAllenServiceImpl() {
		dao = new DflMatthewAllenDaoImpl();
		super.setDao(dao);
	}
}
