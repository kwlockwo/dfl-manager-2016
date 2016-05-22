package net.dflmngr.handlers;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxBinary;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;

import net.dflmngr.logging.LoggingUtils;
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
	private LoggingUtils loggerUtils;
	
	DflRoundInfoService dflRoundInfoService;
	AflFixtureService aflFixtureService;
	GlobalsService globalsService;
	RawPlayerStatsService rawPlayerStatsService;
	
	boolean isExecutable;
		
	String defaultMdcKey = "batch.name";
	String defaultLoggerName = "batch-logger";
	String defaultLogfile = "RawPlayerStatsHandler";
	
	String mdcKey;
	String loggerName;
	String logfile;
	
	public RawPlayerStatsHandler() {
		dflRoundInfoService = new DflRoundInfoServiceImpl();
		aflFixtureService = new AflFixtureServiceImpl();
		globalsService = new GlobalsServiceImpl();
		rawPlayerStatsService = new RawPlayerStatsServiceImpl();
		
		isExecutable = false;
	}
	
	public void configureLogging(String mdcKey, String loggerName, String logfile) {
		loggerUtils = new LoggingUtils(loggerName, mdcKey, logfile);
		this.mdcKey = mdcKey;
		this.loggerName = loggerName;
		this.logfile = logfile;
		isExecutable = true;
	}
	
	public boolean execute(int round) {
		
		boolean success = false;
		
		try {
			if(!isExecutable) {
				configureLogging(defaultMdcKey, defaultLoggerName, defaultLogfile);
				loggerUtils.log("info", "Default logging configured");
			}
			
			loggerUtils.log("info", "Downloading player stats for DFL round: ", round);
			
			DflRoundInfo dflRoundInfo = dflRoundInfoService.get(round);
			
			List<AflFixture> fixturesToProcess = new ArrayList<>();
			Set<String> teamsToProcess = new HashSet<>();
			
			loggerUtils.log("info", "Checking for AFL rounds to download");
			for(DflRoundMapping roundMapping : dflRoundInfo.getRoundMapping()) {
				int aflRound = roundMapping.getAflRound();
				
				loggerUtils.log("info", "DFL round includes AFL round={}", aflRound);
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
			
			loggerUtils.log("info", "AFL games to download stats from: {}", fixturesToProcess);
			loggerUtils.log("info", "Team to take stats from: {}", teamsToProcess);
			
			//List<RawPlayerStats> playerStats = processFixtures(round, fixturesToProcess, teamsToProcess);
			List<RawPlayerStats> playerStats = altProcessFixtures(round, fixturesToProcess, teamsToProcess);
			
			loggerUtils.log("info", "Saving player stats to database");
			
			rawPlayerStatsService.replaceAllForRound(round, playerStats);
			
			loggerUtils.log("info", "Player stats saved");
			
			dflRoundInfoService.close();
			aflFixtureService.close();
			globalsService.close();
			rawPlayerStatsService.close();
			
			success = true;
			
		} catch (Exception ex) {
			loggerUtils.log("error", "Error in ... ", ex);
		}
		
		return success;
	}
	
	private List<RawPlayerStats> processFixtures(int round, List<AflFixture> fixturesToProcess, Set<String> teamsToProcess) throws Exception {
		
		List<RawPlayerStats> playerStats = new ArrayList<>();
		
		String year = globalsService.getCurrentYear();
		String statsUrl = globalsService.getAflStatsUrl();
		
		for(AflFixture fixture : fixturesToProcess) {
			String homeTeam = fixture.getHomeTeam();
			String awayTeam = fixture.getAwayTeam();
			
			String fullStatsUrl =  statsUrl + "/" + year + "/" + round + "/" + homeTeam.toLowerCase() + "-v-" + awayTeam.toLowerCase();
			loggerUtils.log("info", "AFL stats URL: {}", fullStatsUrl);
			
					
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
	
	private List<RawPlayerStats> altProcessFixtures(int round, List<AflFixture> fixturesToProcess, Set<String> teamsToProcess) throws Exception {
		
		List<RawPlayerStats> playerStats = new ArrayList<>();
		
		String year = globalsService.getCurrentYear();
		String statsUrl = globalsService.getAflStatsUrl();
		
		String browserPath = globalsService.getBrowserPath();
		int webdriverWait = globalsService.getWebdriverWait();
		int webdriberTimeout = globalsService.getWebdriverTimeout();
		
		for(AflFixture fixture : fixturesToProcess) {
			String homeTeam = fixture.getHomeTeam();
			String awayTeam = fixture.getAwayTeam();
			
			String fullStatsUrl =  statsUrl + "/" + year + "/" + round + "/" + homeTeam.toLowerCase() + "-v-" + awayTeam.toLowerCase();
			loggerUtils.log("info", "AFL stats URL: {}", fullStatsUrl);

			File browserBinary = new File(browserPath);
			FirefoxBinary ffBinary = new FirefoxBinary(browserBinary);
			FirefoxProfile firefoxProfile = new FirefoxProfile();
			WebDriver driver = new FirefoxDriver(ffBinary, firefoxProfile);
			
			//driver.manage().timeouts().implicitlyWait(webdriverWait, TimeUnit.SECONDS);
			driver.manage().timeouts().pageLoadTimeout(webdriberTimeout, TimeUnit.SECONDS);
			//driver.manage().window().setSize(new Dimension(1024, 768));
			
			try {
				driver.get(fullStatsUrl);
			} catch (Exception ex) {
				if(driver.findElements(By.cssSelector("a[href='#full-time-stats']")).isEmpty()) {
					driver.quit();
					throw new Exception("Error Loading page, URL:"+webdriberTimeout, ex);
				}
			}
						
			//if(driver.findElement(By.id("homeTeam-advanced")).isDisplayed()) {
			//	System.out.println("Displayed");
			//}		
			
			if(teamsToProcess.contains(homeTeam)) {
				playerStats.addAll(altGetStats(round, homeTeam, "h", driver));
			}
			if(teamsToProcess.contains(awayTeam)) {
				playerStats.addAll(altGetStats(round, awayTeam, "a", driver));
			}
			
			driver.quit();
		}
		
		return playerStats;
	}
	
	private List<RawPlayerStats> getStats(int round, String aflTeam, String homeORaway, Document doc) throws Exception {
		
		Element teamStatsTable;
		List<RawPlayerStats> teamStats = new ArrayList<>();
		
		if(homeORaway.equals("h")) {
			teamStatsTable = doc.getElementById("homeTeam-advanced").getElementsByTag("tbody").get(0);
			loggerUtils.log("info", "Found home team stats for: round={}; aflTeam={}; ", round, aflTeam);
		} else {
			teamStatsTable = doc.getElementById("awayTeam-advanced").getElementsByTag("tbody").get(0);
			loggerUtils.log("info", "Found away team stats for: round={}; aflTeam={}; ", round, aflTeam);
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

			playerStats.setJumperNo(Integer.parseInt(stats.get(1).text()));
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
			
			loggerUtils.log("info", "Player stats: {}", playerStats);
			
			teamStats.add(playerStats);
		}
		
		return teamStats;
	}
	
	private List<RawPlayerStats> altGetStats(int round, String aflTeam, String homeORaway, WebDriver driver) throws Exception {
		
		driver.findElement(By.cssSelector("a[href='#full-time-stats']")).click();
		driver.findElement(By.cssSelector("a[href='#advanced-stats']")).click();
		
		List<WebElement> statsRecs;
		List<RawPlayerStats> teamStats = new ArrayList<>();
		
		if(homeORaway.equals("h")) {
			statsRecs = driver.findElement(By.id("homeTeam-advanced")).findElement(By.tagName("tbody")).findElements(By.tagName("tr"));
			loggerUtils.log("info", "Found home team stats for: round={}; aflTeam={}; ", round, aflTeam);
		} else {
			statsRecs = driver.findElement(By.id("awayTeam-advanced")).findElement(By.tagName("tbody")).findElements(By.tagName("tr"));
			loggerUtils.log("info", "Found away team stats for: round={}; aflTeam={}; ", round, aflTeam);
		}
		

		
		for(WebElement statsRec : statsRecs) {
			List<WebElement> stats = statsRec.findElements(By.tagName("td"));
			
			RawPlayerStats playerStats = new RawPlayerStats();
			playerStats.setRound(round);
						
			playerStats.setName(stats.get(0).findElements(By.tagName("span")).get(1).getText());
			
			playerStats.setTeam(aflTeam);
			
			playerStats.setJumperNo(Integer.parseInt(stats.get(1).getText()));
			playerStats.setKicks(Integer.parseInt(stats.get(2).getText()));
			playerStats.setHandballs(Integer.parseInt(stats.get(3).getText()));
			playerStats.setDisposals(Integer.parseInt(stats.get(4).getText()));
			playerStats.setMarks(Integer.parseInt(stats.get(9).getText()));
			playerStats.setHitouts(Integer.parseInt(stats.get(12).getText()));
			playerStats.setFreesFor(Integer.parseInt(stats.get(17).getText()));
			playerStats.setFreesAgainst(Integer.parseInt(stats.get(18).getText()));
			playerStats.setTackles(Integer.parseInt(stats.get(19).getText()));
			playerStats.setGoals(Integer.parseInt(stats.get(23).getText()));
			playerStats.setBehinds(Integer.parseInt(stats.get(24).getText()));
			
			loggerUtils.log("info", "Player stats: {}", playerStats);
			
			teamStats.add(playerStats);
		}
		
		return teamStats;
	}
	
	// For internal testing
	public static void main(String[] args) {
		RawPlayerStatsHandler testing = new RawPlayerStatsHandler();
		testing.configureLogging("batch.name", "batch-logger", "RawPlayerStatsHandlerTesting");
		testing.execute(1);
	}
}
