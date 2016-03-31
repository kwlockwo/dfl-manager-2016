package net.dflmngr.model.dao;

import java.util.List;

import net.dflmngr.model.entity.DflFixture;
import net.dflmngr.model.entity.keys.DflFixturePK;

public interface DflFixtureDao extends GenericDao<DflFixture, DflFixturePK> {
	public List<DflFixture> findAflFixturesForRound(int round);
}
