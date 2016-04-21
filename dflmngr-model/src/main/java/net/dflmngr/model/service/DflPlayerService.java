package net.dflmngr.model.service;

import java.util.List;

import net.dflmngr.model.entity.DflPlayer;

public interface DflPlayerService extends GenericService<DflPlayer, Integer> {
	public DflPlayer getByAflPlayerId(String aflPlayerId);
	public List<DflPlayer> getAdamGoodesEligible();
}
