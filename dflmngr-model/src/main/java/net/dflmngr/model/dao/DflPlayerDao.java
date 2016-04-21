package net.dflmngr.model.dao;

import java.util.List;

import net.dflmngr.model.entity.DflPlayer;

public interface DflPlayerDao extends GenericDao<DflPlayer, Integer> {
	public DflPlayer findByAflPlayerId(String aflPlayerId);
	public List<DflPlayer> findAdamGoodesEligible();
}
