package net.dflmngr.model.service;

import net.dflmngr.model.entity.DflPlayer;

public interface DflPlayerService extends GenericService<DflPlayer, Integer> {
	public DflPlayer getByAflPlayerId(String aflPlayerId);
}
