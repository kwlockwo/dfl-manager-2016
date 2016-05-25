package net.dflmngr.model.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.dflmngr.model.dao.DflPlayerScoresDao;
import net.dflmngr.model.dao.impl.DflPlayerScoresDaoImpl;
import net.dflmngr.model.entity.DflPlayerScores;
import net.dflmngr.model.entity.keys.DflPlayerScoresPK;
import net.dflmngr.model.service.DflPlayerScoresService;

public class DflPlayerScoresServiceImpl extends GenericServiceImpl<DflPlayerScores, DflPlayerScoresPK> implements DflPlayerScoresService {
	private DflPlayerScoresDao dao;
	
	public DflPlayerScoresServiceImpl() {
		dao = new DflPlayerScoresDaoImpl();
		super.setDao(dao);
	}
	
	public List<DflPlayerScores> getForRound(int round) {
		List<DflPlayerScores> playerScores = dao.findForRound(round);
		return playerScores;
	}
	
	public List<DflPlayerScores> getForRoundAndTeam(int round, String teamCode) {
		List<DflPlayerScores> playerScores = dao.findForRoundAndTeam(round, teamCode);
		return playerScores;
	}
	
	public Map<Integer, DflPlayerScores> getForRoundWithKey(int round) {
		Map<Integer, DflPlayerScores> playerScoresWithKey = new HashMap<>();
		List<DflPlayerScores> scores = getForRound(round);
		
		for(DflPlayerScores playerScore : scores) {
			playerScoresWithKey.put(playerScore.getPlayerId(), playerScore);
		}
		
		return playerScoresWithKey;
	}
	
	public Map<Integer, DflPlayerScores> getForRoundAndTeamWithKey(int round, String teamCode) {
		Map<Integer, DflPlayerScores> playerScoresWithKey = new HashMap<>();
		List<DflPlayerScores> scores = getForRoundAndTeam(round, teamCode);
		
		for(DflPlayerScores playerScore : scores) {
			playerScoresWithKey.put(playerScore.getPlayerId(), playerScore);
		}
		
		return playerScoresWithKey;
	}
	
	public void replaceAllForRound(int round, List<DflPlayerScores> playerScores) {
		
		dao.beginTransaction();
		
		List<DflPlayerScores> existingScores = getForRound(round);
		for(DflPlayerScores scores : existingScores) {
			delete(scores);
		}
		
		dao.flush();
		
		insertAll(playerScores, true);
		
		dao.commit();
	}
	
	public Map<Integer, List<DflPlayerScores>> getAllWithKey() {
		Map<Integer, List<DflPlayerScores>> playerScoresWithKey = new HashMap<>();
		List<DflPlayerScores> scores = dao.findAll();
		
		for(DflPlayerScores playerScore : scores) {
			List<DflPlayerScores> playerScores = null;
			if(playerScoresWithKey.containsKey(playerScore.getPlayerId())) {
				playerScores = playerScoresWithKey.get(playerScore.getPlayerId());
			} else {
				playerScores = new ArrayList<>();
			}
			
			playerScores.add(playerScore);
			playerScoresWithKey.put(playerScore.getPlayerId(), playerScores);
		}
		
		return playerScoresWithKey;
	}
	
	public Map<Integer, List<DflPlayerScores>> getUptoRoundWithKey(int round) {
		Map<Integer, List<DflPlayerScores>> playerScoresWithKey = new HashMap<>();
		
		for(int i = 1; i < round; i++) {
			List<DflPlayerScores> scores = getForRound(i);
			
			for(DflPlayerScores playerScore : scores) {
				List<DflPlayerScores> playerScores = null;
				if(playerScoresWithKey.containsKey(playerScore.getPlayerId())) {
					playerScores = playerScoresWithKey.get(playerScore.getPlayerId());
				} else {
					playerScores = new ArrayList<>();
				}
				
				playerScores.add(playerScore);
				playerScoresWithKey.put(playerScore.getPlayerId(), playerScores);
			}
		}
		
		return playerScoresWithKey;
	}
}
