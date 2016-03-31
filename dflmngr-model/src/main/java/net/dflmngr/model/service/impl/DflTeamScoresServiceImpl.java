package net.dflmngr.model.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.dflmngr.model.dao.DflTeamScoresDao;
import net.dflmngr.model.dao.impl.DflTeamScoresDaoImpl;
import net.dflmngr.model.entity.DflTeamScores;
import net.dflmngr.model.entity.keys.DflTeamScoresPK;
import net.dflmngr.model.service.DflTeamScoresService;

public class DflTeamScoresServiceImpl extends GenericServiceImpl<DflTeamScores, DflTeamScoresPK> implements DflTeamScoresService {
	private DflTeamScoresDao dao;
	
	public DflTeamScoresServiceImpl() {
		dao = new DflTeamScoresDaoImpl();
		super.setDao(dao);
	}
	
	public List<DflTeamScores> getForRound(int round) {
		List<DflTeamScores> teamScores = dao.findForRound(round);
		return teamScores;
	}
	
	public Map<String, DflTeamScores> getForRoundWithKey(int round) {
		Map<String, DflTeamScores> teamScoresWithKey = new HashMap<>();
		List<DflTeamScores> scores = getForRound(round);
		
		for(DflTeamScores teamScore : scores) {
			teamScoresWithKey.put(teamScore.getTeamCode(), teamScore);
		}
		
		return teamScoresWithKey;
	}
	
	public void replaceAllForRound(int round, List<DflTeamScores> teamScores) {
		
		dao.beginTransaction();
		
		List<DflTeamScores> existingScores = getForRound(round);
		for(DflTeamScores scores : existingScores) {
			delete(scores);
		}
		
		dao.flush();
		
		insertAll(teamScores, true);
		
		dao.commit();
	}
}
