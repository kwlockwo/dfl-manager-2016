package net.dflmngr.model.service.impl;

import java.util.List;

import net.dflmngr.model.dao.DflPlayerDao;
import net.dflmngr.model.dao.impl.DflPlayerDaoImpl;
import net.dflmngr.model.entity.DflPlayer;
import net.dflmngr.model.service.DflPlayerService;

public class DflPlayerServiceImpl extends GenericServiceImpl<DflPlayer, Integer> implements DflPlayerService {
	
	private DflPlayerDao dao;
	
	public DflPlayerServiceImpl() {
		dao = new DflPlayerDaoImpl();
		super.setDao(dao);
	}
	
	public DflPlayer getByAflPlayerId(String aflPlayerId) {
		DflPlayer dflPlayer = null;
		dflPlayer = dao.findByAflPlayerId(aflPlayerId);
		return dflPlayer;
	}
	
	public List<DflPlayer> getAdamGoodesEligible() {
		List<DflPlayer> adamGoodesEligible = null;
		adamGoodesEligible = dao.findAdamGoodesEligible();
		return adamGoodesEligible;
	}
}
