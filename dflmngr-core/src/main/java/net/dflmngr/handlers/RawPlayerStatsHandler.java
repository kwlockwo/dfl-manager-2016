package net.dflmngr.handlers;

import java.net.URL;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import net.dflmngr.model.entity.AflFixture;
import net.dflmngr.model.entity.DflRoundInfo;
import net.dflmngr.model.entity.DflRoundMapping;
import net.dflmngr.model.entity.RawPlayerStats;
import net.dflmngr.model.service.AflFixtureService;
import net.dflmngr.model.service.DflRoundInfoService;
import net.dflmngr.model.service.GlobalsService;
import net.dflmngr.model.service.RawPlayerStatsService;
import net.dflmngr.model.service.impl.AflFixtureServiceImpl;
import net.dflmngr.model.service.impl.DflRoundInfoServiceImpl;
import net.dflmngr.model.service.impl.GlobalsServiceImpl;
import net.dflmngr.model.service.impl.RawPlayerStatsServiceImpl;

public class RawPlayerStatsHandler {
	
	DflRoundInfoService dflRoundInfoService;
	AflFixtureService aflFixtureService;
	GlobalsService globalsService;
	RawPlayerStatsService rawPlayerStatsService;
	
	public RawPlayerStatsHandler() {
		dflRoundInfoService = new DflRoundInfoServiceImpl();
		aflFixtureService = new AflFixtureServiceImpl();
		globalsService = new GlobalsServiceImpl();
		rawPlayerStatsService = new RawPlayerStatsServiceImpl();
	}
	
	public void execute(int round) throws Exception {
		
		DflRoundInfo dflRoundInfo = dflRoundInfoService.get(round);
		
		List<AflFixture> fixturesToProcess = new ArrayList<>();
		Set<String> teamsToProcess = new HashSet<>();
		
		for(DflRoundMapping roundMapping : dflRoundInfo.getRoundMapping()) {
			int aflRound = roundMapping.getAflRound();
			
			if(roundMapping.getAflGame() == 0) {
				List<AflFixture> fixtures = aflFixtureService.getAflFixturesPlayedForRound(aflRound);
				fixturesToProcess.addAll(fixtures);
				for(AflFixture fixture : fixtures) {
					teamsToProcess.add(fixture.getHomeTeam());
					teamsToProcess.add(fixture.getAwayTeam());
				}
			} else {
				int aflGame = roundMapping.getAflGame();
				AflFixture fixture = aflFixtureService.getPlayedGame(aflRound, aflGame);
				
				if(fixture != null) {
					if(roundMapping.getAflTeam() == null || roundMapping.getAflTeam().equals("")) {
						teamsToProcess.add(fixture.getHomeTeam());
						teamsToProcess.add(fixture.getAwayTeam());
					} else {
						teamsToProcess.add(roundMapping.getAflTeam());
					}
				}
			}
		}
		
		List<RawPlayerStats> playerStats = processFixtures(round, fixturesToProcess, teamsToProcess);
		
		rawPlayerStatsService.replaceAllForRound(round, playerStats);
		
		dflRoundInfoService.close();
		aflFixtureService.close();
		globalsService.close();
		rawPlayerStatsService.close();
	}
	
	private List<RawPlayerStats> processFixtures(int round, List<AflFixture> fixturesToProcess, Set<String> teamsToProcess) throws Exception {
		
		List<RawPlayerStats> playerStats = new ArrayList<>();
		
		String year = globalsService.getCurrentYear();
		String statsUrl = globalsService.getAflStatsUrl();
		
		for(AflFixture fixture : fixturesToProcess) {
			String homeTeam = fixture.getHomeTeam();
			String awayTeam = fixture.getAwayTeam();
			
			String fullStatsUrl =  statsUrl + "/" + year + "/" + round + "/" + homeTeam.toLowerCase() + "-v-" + awayTeam.toLowerCase();
					
					
			//		MessageFormat.format(statsUrl, String.valueOf(year), round, homeTeam, awayTeam);
			
			Document doc = Jsoup.parse(new URL(fullStatsUrl).openStream(), "UTF-8", fullStatsUrl);
			
			if(teamsToProcess.contains(homeTeam)) {
				playerStats.addAll(getStats(round, homeTeam, "h", doc));
			}
			if(teamsToProcess.contains(awayTeam)) {
				playerStats.addAll(getStats(round, awayTeam, "a", doc));
			}
		}
		
		return playerStats;
	}
	
	private List<RawPlayerStats> getStats(int round, String aflTeam, String homeORaway, Document doc) throws Exception {
		
		Element teamStatsTable;
		List<RawPlayerStats> teamStats = new ArrayList<>();
		
		if(homeORaway.equals("h")) {
			teamStatsTable = doc.getElementById("homeTeam-advanced").getElementsByTag("tbody").get(0);
		} else {
			teamStatsTable = doc.getElementById("awayTeam-advanced").getElementsByTag("tbody").get(0);
		}
		
		Elements teamStatsRecs = teamStatsTable.getElementsByTag("tr");
		for(Element pStatRec : teamStatsRecs) {
			RawPlayerStats playerStats = new RawPlayerStats();
			Elements stats = pStatRec.getElementsByTag("td");
			String utfName = stats.get(0).getElementsByClass("full-name").get(0).text();
			String name = new String(utfName.getBytes("UTF-8")).replaceAll("\\s+", " ").trim().replaceAll("[^\\u0000-\\u007f]+", " ").trim();
			
			playerStats.setRound(round);
			playerStats.setName(name);
			
			playerStats.setTeam(aflTeam);

			playerStats.setKicks(Integer.parseInt(stats.get(2).text()));
			playerStats.setHandballs(Integer.parseInt(stats.get(3).text()));
			playerStats.setDisposals(Integer.parseInt(stats.get(4).text()));
			playerStats.setMarks(Integer.parseInt(stats.get(9).text()));
			playerStats.setHitouts(Integer.parseInt(stats.get(12).text()));
			playerStats.setFreesFor(Integer.parseInt(stats.get(17).text()));
			playerStats.setFreesAgainst(Integer.parseInt(stats.get(18).text()));
			playerStats.setTackles(Integer.parseInt(stats.get(19).text()));
			playerStats.setGoals(Integer.parseInt(stats.get(23).text()));
			playerStats.setBehinds(Integer.parseInt(stats.get(24).text()));
			teamStats.add(playerStats);
		}
		
		return teamStats;
	}
	
	// For internal testing
	public static void main(String[] args) {
		
		try {
			RawPlayerStatsHandler testing = new RawPlayerStatsHandler();
			testing.execute(1);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
