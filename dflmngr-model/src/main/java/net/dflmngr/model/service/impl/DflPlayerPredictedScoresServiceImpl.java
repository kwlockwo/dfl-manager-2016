package net.dflmngr.model.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.dflmngr.model.dao.DflPlayerPredictedScoresDao;
import net.dflmngr.model.dao.impl.DflPlayerPredictedScoresDaoImpl;
import net.dflmngr.model.entity.DflPlayerPredictedScores;
import net.dflmngr.model.entity.keys.DflPlayerPredictedScoresPK;
import net.dflmngr.model.service.DflPlayerPredictedScoresService;

public class DflPlayerPredictedScoresServiceImpl extends GenericServiceImpl<DflPlayerPredictedScores, DflPlayerPredictedScoresPK> implements DflPlayerPredictedScoresService {
	private DflPlayerPredictedScoresDao dao;
	
	public DflPlayerPredictedScoresServiceImpl() {
		dao = new DflPlayerPredictedScoresDaoImpl();
		super.setDao(dao);
	}
	
	public List<DflPlayerPredictedScores> getForRound(int round) {
		List<DflPlayerPredictedScores> playerPredictedScores = dao.findForRound(round);
		return playerPredictedScores;
	}
	
	public Map<Integer, DflPlayerPredictedScores> getForRoundWithKey(int round) {
		Map<Integer, DflPlayerPredictedScores> playerPredictedScoresWithKey = new HashMap<>();
		List<DflPlayerPredictedScores> scores = getForRound(round);
		
		for(DflPlayerPredictedScores playerScore : scores) {
			playerPredictedScoresWithKey.put(playerScore.getPlayerId(), playerScore);
		}
		
		return playerPredictedScoresWithKey;
	}
		
	public void replaceAllForRound(int round, List<DflPlayerPredictedScores> predictedScores) {
		dao.beginTransaction();
		
		List<DflPlayerPredictedScores> existingPredictions = getForRound(round);
		for(DflPlayerPredictedScores preidction : existingPredictions) {
			delete(preidction);
		}
		
		dao.flush();
		
		insertAll(predictedScores, true);
		
		dao.commit();
	}
}
