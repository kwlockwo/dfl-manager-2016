package net.dflmngr.model.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.dflmngr.model.dao.DflTeamPredictedScoresDao;
import net.dflmngr.model.dao.impl.DflTeamPredictedScoresDaoImpl;
import net.dflmngr.model.entity.DflTeamPredictedScores;
import net.dflmngr.model.entity.keys.DflTeamPredictedScoresPK;
import net.dflmngr.model.service.DflTeamPredictedScoresService;

public class DflTeamPredictedScoresServiceImpl extends GenericServiceImpl<DflTeamPredictedScores, DflTeamPredictedScoresPK> implements DflTeamPredictedScoresService {
	private DflTeamPredictedScoresDao dao;
	
	public DflTeamPredictedScoresServiceImpl() {
		dao = new DflTeamPredictedScoresDaoImpl();
		super.setDao(dao);
	}
	
	public DflTeamPredictedScores getTeamPredictedScoreForRound(String teamCode, int round) {
		DflTeamPredictedScoresPK pk = new DflTeamPredictedScoresPK();
		pk.setRound(round);
		pk.setTeamCode(teamCode);
		DflTeamPredictedScores teamPredictedScore = get(pk);
		return teamPredictedScore;
	}
	
	public List<DflTeamPredictedScores> getForRound(int round) {
		List<DflTeamPredictedScores> teamPredictedScores = dao.findForRound(round);
		return teamPredictedScores;
	}
	
	public Map<String, DflTeamPredictedScores> getForRoundWithKey(int round) {
		Map<String, DflTeamPredictedScores> teamPredictedScoresWithKey = new HashMap<>();
		List<DflTeamPredictedScores> predictedScores = getForRound(round);
		
		for(DflTeamPredictedScores teamScore : predictedScores) {
			teamPredictedScoresWithKey.put(teamScore.getTeamCode(), teamScore);
		}
		
		return teamPredictedScoresWithKey;
	}
	
	public List<DflTeamPredictedScores> getAllForRound(int round) {
		List<DflTeamPredictedScores> predictedScores = dao.findAllForRound(round);
		return predictedScores;
	}
	
	public void replaceAllForRound(int round, List<DflTeamPredictedScores> predictedScores) {
		dao.beginTransaction();
		
		List<DflTeamPredictedScores> existingPredictions = getAllForRound(round);
		for(DflTeamPredictedScores preidction : existingPredictions) {
			delete(preidction);
		}
		
		dao.flush();
		
		insertAll(predictedScores, true);
		
		dao.commit();
	}
}
