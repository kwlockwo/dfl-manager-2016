package net.dflmngr.model.service.impl;

import java.util.List;

import net.dflmngr.model.dao.InsAndOutsDao;
import net.dflmngr.model.dao.impl.InsAndOutsDaoImpl;
import net.dflmngr.model.entity.InsAndOuts;
import net.dflmngr.model.entity.keys.InsAndOutsPK;
import net.dflmngr.model.service.InsAndOutsService;

public class InsAndOutsServiceImpl extends GenericServiceImpl<InsAndOuts, InsAndOutsPK> implements InsAndOutsService {
	
	InsAndOutsDao insAndOutsDao;
	
	public InsAndOutsServiceImpl() {
		insAndOutsDao = new InsAndOutsDaoImpl();
		super.setDao(insAndOutsDao);
	}
	
	public void saveTeamInsAndOuts(List<InsAndOuts> insAndOuts) {
		int round = insAndOuts.get(0).getRound();
		String teamCode = insAndOuts.get(0).getTeamCode();
		
		List<InsAndOuts> existingInsAndOuts = insAndOutsDao.findByTeamAndRound(round, teamCode);
		
		dao.beginTransaction();
		
		if(existingInsAndOuts.size() > 0) {
			for(InsAndOuts delete : existingInsAndOuts) {
				insAndOutsDao.remove(delete);
			}
		}
		
		dao.flush();
		
		for(InsAndOuts insert : insAndOuts) {
			insAndOutsDao.persist(insert);
		}
		
		dao.commit();
	}
	
	public List<InsAndOuts> getByTeamAndRound(int round, String teamCode) {
		
		List<InsAndOuts> insAndOuts = insAndOutsDao.findByTeamAndRound(round, teamCode);
		
		return insAndOuts;
	}
}
