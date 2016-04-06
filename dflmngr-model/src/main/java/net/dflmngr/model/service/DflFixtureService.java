package net.dflmngr.model.service;

import java.util.List;

import net.dflmngr.model.entity.DflFixture;
import net.dflmngr.model.entity.keys.DflFixturePK;

public interface DflFixtureService extends GenericService<DflFixture, DflFixturePK> {
	public List<DflFixture> getFixturesForRound(int round);
}
