package net.dflmngr.model.dao;

import net.dflmngr.model.entity.DflPlayer;

public interface DflPlayerDao extends GenericDao<DflPlayer, Integer> {
	public DflPlayer findByAflPlayerId(String aflPlayerId);
}
