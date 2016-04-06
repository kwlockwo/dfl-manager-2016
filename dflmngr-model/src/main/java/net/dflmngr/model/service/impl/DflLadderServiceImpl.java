package net.dflmngr.model.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.dflmngr.model.dao.DflLadderDao;
import net.dflmngr.model.dao.impl.DflLadderDaoImpl;
import net.dflmngr.model.entity.DflLadder;
import net.dflmngr.model.entity.keys.DflLadderPK;
import net.dflmngr.model.service.DflLadderService;

public class DflLadderServiceImpl extends GenericServiceImpl<DflLadder, DflLadderPK> implements DflLadderService {
	
	private DflLadderDao dao;
	
	public DflLadderServiceImpl() {
		dao = new DflLadderDaoImpl();
		setDao(dao);
	}
	
	public List<DflLadder> getLadderForRound(int round) {
		List<DflLadder> ladder = dao.findLadderForRound(round);
		return ladder;
	}
	
	public List<DflLadder> getPreviousRoundLadder(int round) {
		List<DflLadder> ladder = getLadderForRound(round - 1);
		return ladder;
	}
	
	public Map<String, DflLadder> getForRoundWithKey(int round) {
		Map<String, DflLadder> ladderMap = new HashMap<>();
		
		List<DflLadder> ladder = getLadderForRound(round);
		
		for(DflLadder team : ladder) {
			ladderMap.put(team.getTeamCode(), team);
		}
		
		return ladderMap;
	}
	
	public Map<String, DflLadder> getForPeviousRoundWithKey(int round) {
		Map<String, DflLadder> ladderMap = new HashMap<>();
		
		List<DflLadder> ladder = getPreviousRoundLadder(round);
		
		for(DflLadder team : ladder) {
			ladderMap.put(team.getTeamCode(), team);
		}
		
		return ladderMap;
	}
	
	public void replaceAllForRound(int round, List<DflLadder> ladder) {
		
		dao.beginTransaction();
		
		List<DflLadder> existingLadder = getLadderForRound(round);
		for(DflLadder team : existingLadder) {
			delete(team);
		}
		
		dao.flush();
		
		insertAll(ladder, true);
		
		dao.commit();
	}
}
