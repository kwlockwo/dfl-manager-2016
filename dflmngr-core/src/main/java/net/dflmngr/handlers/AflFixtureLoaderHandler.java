package net.dflmngr.handlers;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.TimeZone;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import net.dflmngr.model.entity.AflFixture;
import net.dflmngr.model.service.AflFixtureService;
import net.dflmngr.model.service.GlobalsService;
import net.dflmngr.model.service.impl.AflFixtureServiceImpl;
import net.dflmngr.model.service.impl.GlobalsServiceImpl;

public class AflFixtureLoaderHandler {
	private Logger logger;
	
	SimpleDateFormat formatter = new SimpleDateFormat("EEEE, MMMM dd h:mma yyyy");
	
	GlobalsService globalsService;
	AflFixtureService aflFixtureService;
	
	public AflFixtureLoaderHandler() {
		
		MDC.put("batch.name", "AflFixtureLoader");
		logger = LoggerFactory.getLogger("batch-logger");
		
		globalsService = new GlobalsServiceImpl();
		aflFixtureService = new AflFixtureServiceImpl();
	}
	
	public void execute(List<Integer> aflRounds) throws Exception {
		
		try {
			logger.info("Executing AflFixtureLoader for rounds: {}", aflRounds);
			
			List<AflFixture> allGames = new ArrayList<AflFixture>();
			String currentYear = globalsService.getCurrentYear();
			
			logger.info("Current year: {}", currentYear);
			
			for(Integer aflRound : aflRounds) {
				allGames.addAll(getAflRoundFixture(currentYear, aflRound));
			}
			
			logger.info("Saveing data to DB");
			
			aflFixtureService.insertAll(allGames, false);
			
			logger.info("AflFixtureLoader Complete");
		} catch (Exception ex) {
			logger.error("Error in ... ", ex);
		} finally {
			MDC.remove("batch.name");
		}
	}
	
	private List<AflFixture> getAflRoundFixture(String currentYear, Integer aflRound) throws Exception {
		
		List<AflFixture> games = new ArrayList<AflFixture>();
		String aflRoundStr = aflRound.toString();
		String paddedRoundNo = "";
		
		if(aflRoundStr.length() < 2) {
			paddedRoundNo = "0" + aflRoundStr;
		} else {
			paddedRoundNo = aflRoundStr;
		}
		
		List<String> aflFixtureUrlParts = globalsService.getAflFixtureUrl();
		String aflFixtureUrl = aflFixtureUrlParts.get(0) + aflFixtureUrlParts.get(1) + currentYear + aflFixtureUrlParts.get(2) + paddedRoundNo + aflFixtureUrlParts.get(3);
		
		logger.info("AFL fixture URL: {}", aflFixtureUrl);
		
		Document doc = Jsoup.parse(new URL(aflFixtureUrl).openStream(), "UTF-8", aflFixtureUrl);
		
		Elements fixtureRows = doc.getElementById("tround").getElementsByTag("tbody").select("tr");
		Iterator<Element> fixtureRowsElements = fixtureRows.listIterator();
		
		if(fixtureRowsElements.hasNext()) {
			
			Element fixtureRow = fixtureRowsElements.next();
			
			int gameCount = 1;
			
			while(fixtureRowsElements.hasNext()) {
				String dateStr = "";
				
				if(fixtureRow.child(0).tagName().equalsIgnoreCase("th")) {
					dateStr = fixtureRow.child(0).text();
				} 
				
				if(fixtureRowsElements.hasNext()) {
					fixtureRow = fixtureRowsElements.next();
					while(!fixtureRow.child(0).tagName().equalsIgnoreCase("th")) {
						//fixtureRow = fixtureRowsElements.next();
						
						AflFixture fixture = new AflFixture();
						
						fixture.setRound(aflRound);
						fixture.setGame(gameCount);
						
						Elements teamNames = fixtureRow.select(".team-logos");
						fixture.setHomeTeam(teamNames.select(".home").text());
						fixture.setAwayTeam(teamNames.select(".away").text());
						
						String ground = fixtureRow.select(".venue").text();
						fixture.setGround(ground);
						String timezone = globalsService.getGroundTimeZone(ground);
						String defaultTimezone = globalsService.getGroundTimeZone("default");
						
						formatter.setTimeZone(TimeZone.getTimeZone(timezone));
						String timeStr = fixtureRow.select(".time").text();
						Date localDate = formatter.parse(dateStr + " " + timeStr + " " + currentYear);
						
						if(timezone.equals(defaultTimezone)) {
							fixture.setStart(localDate);
						} else {
							formatter.setTimeZone(TimeZone.getTimeZone(defaultTimezone));
							String defaultDateStr = formatter.format(localDate);
							Date defaultDate = formatter.parse(defaultDateStr);
							fixture.setStart(defaultDate);
						}
						
						fixture.setTimezone(timezone);
						
						logger.info("Scraped fixture data: {}", fixture);
												
						games.add(fixture);
						gameCount++;
						
						if(fixtureRowsElements.hasNext()) {
							fixtureRow = fixtureRowsElements.next();
						} else {
							break;
						}
					} 
				}	
			}
		}
				
		return games;
	}
	
	// For internal testing
	public static void main(String[] args) {
		
		AflFixtureLoaderHandler testing = new AflFixtureLoaderHandler();

		try {
			List<Integer> testRounds = new ArrayList<>();
			
			for(int i = 1; i < 24; i++) {
				testRounds.add(i);
			}
			
			testing.execute(testRounds);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
