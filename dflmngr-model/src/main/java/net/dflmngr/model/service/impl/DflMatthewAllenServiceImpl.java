package net.dflmngr.model.service.impl;

import java.util.List;

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
	
	public List<DflMatthewAllen> getForRound(int round) {
		List<DflMatthewAllen> standings = dao.findForRound(round);
		return standings;
	}
	
	public DflMatthewAllen getLastVotes(int playerId) {
		DflMatthewAllen lastVotes = dao.findLastVotes(playerId);
		return lastVotes;
	}
	
	public void replaceAllForRound(int round, List<DflMatthewAllen> votes) {
		
		dao.beginTransaction();
		
		List<DflMatthewAllen> existingVotes = getForRound(round);
		for(DflMatthewAllen vote : existingVotes) {
			delete(vote);
		}
		
		dao.flush();
		
		insertAll(votes, true);
		
		dao.commit();
	}
	
	public void deleteForRound(int round) {
		dao.beginTransaction();
		dao.removeForRound(round);
		dao.commit();
	}
}
