package net.dflmngr.model.service.impl;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.dflmngr.model.dao.AflFixtureDao;
import net.dflmngr.model.dao.impl.AflFixtureDaoImpl;
import net.dflmngr.model.entity.AflFixture;
import net.dflmngr.model.entity.keys.AflFixturePK;
import net.dflmngr.model.service.AflFixtureService;

public class AflFixtureServiceImpl extends GenericServiceImpl<AflFixture, AflFixturePK> implements AflFixtureService {

	private AflFixtureDao dao;
	
	public AflFixtureServiceImpl() {
		dao = new AflFixtureDaoImpl();
		setDao(dao);
	}
	
	public List<AflFixture> getAflFixturesForRound(int round) {
		List<AflFixture> aflFixtures = dao.findAflFixturesForRound(round);
		return aflFixtures;
	}
	
	public Map<Integer, List<AflFixture>> getAflFixturneRoundBlocks() {
	
		Map<Integer, List<AflFixture>> fxitureRoundBlocks = new HashMap<>();
		
		List<AflFixture> fullFixture = dao.findAll();
		
		for(AflFixture fixture : fullFixture) {
			int roundKey = fixture.getRound();
			List<AflFixture> fixtureBlock = null;
			
			if(fxitureRoundBlocks.containsKey(roundKey)) {
				fixtureBlock = fxitureRoundBlocks.get(roundKey);
			} else {
				fixtureBlock = new ArrayList<>();
			}
			
			fixtureBlock.add(fixture);
			fxitureRoundBlocks.put(roundKey, fixtureBlock);
		}
		
		return fxitureRoundBlocks;
	}
	
	public List<AflFixture> getAflFixturesPlayedForRound(int round) {
		
		List<AflFixture> playedFixtures = new ArrayList<>();
		List<AflFixture> aflFixtures = dao.findAflFixturesForRound(round);
		
		Date now = new Date();
		Calendar nowCal = Calendar.getInstance();
		nowCal.setTime(now);
		
		for(AflFixture fixture : aflFixtures) {
			
			Calendar startCal = Calendar.getInstance();
			startCal.setTime(fixture.getStart());
			startCal.add(Calendar.HOUR_OF_DAY, 3);
						
			if(nowCal.after(startCal)) {
				playedFixtures.add(fixture);
			}
			
		}
		
		return playedFixtures;
	}
	
	public AflFixture getPlayedGame(int round, int game) {
		
		AflFixture playedFixture = null;
		
		AflFixturePK aflFixturePK = new AflFixturePK();
		aflFixturePK.setRound(round);
		aflFixturePK.setGame(game);
		
		AflFixture fixture = get(aflFixturePK);
		
		Date now = new Date();
		Calendar nowCal = Calendar.getInstance();
		nowCal.setTime(now);
		
		Calendar startCal = Calendar.getInstance();
		startCal.setTime(fixture.getStart());
		startCal.add(Calendar.HOUR_OF_DAY, 3);
		
		if(nowCal.after(startCal)) {
			playedFixture = fixture;
		}
		
		return playedFixture;
	}
}
