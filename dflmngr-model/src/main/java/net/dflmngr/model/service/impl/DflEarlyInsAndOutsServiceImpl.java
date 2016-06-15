package net.dflmngr.model.service.impl;

import java.util.List;

import net.dflmngr.model.dao.DflEarlyInsAndOutsDao;
import net.dflmngr.model.dao.impl.DflEarlyInsAndOutsDaoImpl;
import net.dflmngr.model.entity.DflEarlyInsAndOuts;
import net.dflmngr.model.entity.keys.DflEarlyInsAndOutsPK;
import net.dflmngr.model.service.DflEarlyInsAndOutsService;

public class DflEarlyInsAndOutsServiceImpl extends GenericServiceImpl<DflEarlyInsAndOuts, DflEarlyInsAndOutsPK> implements DflEarlyInsAndOutsService {
	private DflEarlyInsAndOutsDao dao;
	
	public DflEarlyInsAndOutsServiceImpl() {
		dao = new DflEarlyInsAndOutsDaoImpl();
		super.setDao(dao);
	}
	
	public List<DflEarlyInsAndOuts> getByTeamAndRound(int round, String teamCode) {
		List<DflEarlyInsAndOuts> earlyInsAndOuts = dao.findByTeamAndRound(round, teamCode);
		return earlyInsAndOuts;
	}
	
	public void saveTeamInsAndOuts(List<DflEarlyInsAndOuts> earlyInsAndOuts) {
		int round = earlyInsAndOuts.get(0).getRound();
		String teamCode = earlyInsAndOuts.get(0).getTeamCode();
		
		List<DflEarlyInsAndOuts> existingInsAndOuts = dao.findByTeamAndRound(round, teamCode);
		
		dao.beginTransaction();
		
		if(existingInsAndOuts.size() > 0) {
			for(DflEarlyInsAndOuts delete : existingInsAndOuts) {
				dao.remove(delete);
			}
		}
		
		dao.flush();
		
		for(DflEarlyInsAndOuts insert : earlyInsAndOuts) {
			dao.persist(insert);
		}
		
		dao.commit();
	}
}
