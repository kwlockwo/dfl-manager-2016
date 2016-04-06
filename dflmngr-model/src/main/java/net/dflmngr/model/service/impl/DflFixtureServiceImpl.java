package net.dflmngr.model.service.impl;

import java.util.List;

import net.dflmngr.model.dao.DflFixtureDao;
import net.dflmngr.model.dao.impl.DflFixtureDaoImpl;
import net.dflmngr.model.entity.DflFixture;
import net.dflmngr.model.entity.keys.DflFixturePK;
import net.dflmngr.model.service.DflFixtureService;

public class DflFixtureServiceImpl extends GenericServiceImpl<DflFixture, DflFixturePK> implements DflFixtureService {
	private DflFixtureDao dao;
	
	public DflFixtureServiceImpl() {
		dao = new DflFixtureDaoImpl();
		super.setDao(dao);
	}
	
	public List<DflFixture> getFixturesForRound(int round) {
		List<DflFixture> fixtures = dao.findAflFixturesForRound(round);
		return fixtures;
	}
}
