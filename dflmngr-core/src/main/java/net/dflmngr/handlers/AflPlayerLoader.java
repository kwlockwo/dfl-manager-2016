package net.dflmngr.handlers;

import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import net.dflmngr.jndi.JndiProvider;
import net.dflmngr.logging.LoggingUtils;
import net.dflmngr.model.entity.AflPlayer;
import net.dflmngr.model.entity.AflTeam;
import net.dflmngr.model.service.AflPlayerService;
import net.dflmngr.model.service.AflTeamService;
import net.dflmngr.model.service.impl.AflPlayerServiceImpl;
import net.dflmngr.model.service.impl.AflTeamServiceImpl;

public class AflPlayerLoader {
	private LoggingUtils loggerUtils;
	
	private AflTeamService aflTeamService;
	private AflPlayerService aflPlayerService;
	
	private DateFormat df = new SimpleDateFormat("dd.MM.yy");
	
	public AflPlayerLoader() {
		
		loggerUtils = new LoggingUtils("batch-logger", "batch.name", "AflPlayerLoader");
		
		try {
			
			JndiProvider.bind();
			
			aflTeamService = new AflTeamServiceImpl();
			aflPlayerService = new AflPlayerServiceImpl();
			
		} catch (Exception ex) {
			loggerUtils.log("error", "Error in ... ", ex);
		}	
	}
	
	public void execute() {
		
		try {
			loggerUtils.log("info", "Executing AflPlayerLoader ...");
			
			List<AflTeam> aflTeams = aflTeamService.findAll();
			
			loggerUtils.log("info", "Processing teams: {}", aflTeams);
			
			processTeams(aflTeams);
			
			loggerUtils.log("info", "AflPlayerLoader Complete");
		} catch (Exception ex) {
			loggerUtils.log("error", "Error in ... ", ex);
		}
		
	}
	
	private void processTeams(List<AflTeam> aflTeams) throws Exception {
		
		List<AflPlayer> aflPlayers = new ArrayList<>();
		
		for(AflTeam team : aflTeams) {
			
			loggerUtils.log("info", "Working on team: {}", team.getTeamId());
			
			
			String teamListUrlS = team.getWebsite() + "/" + team.getSeniorUri();
			loggerUtils.log("info", "Senior list URL: {}", teamListUrlS);
			
			Document doc = Jsoup.parse(new URL(teamListUrlS).openStream(), "UTF-8", teamListUrlS);
			aflPlayers.addAll(extractPlayers(team.getTeamId(), doc));
			
			loggerUtils.log("info", "Seniors added to list");
			
			if(team.getRookieUri() != null && !team.getRookieUri().equals("")) {
				String teamListUrlR = team.getWebsite() + "/" + team.getRookieUri();
				loggerUtils.log("info", "Rookie list URL: {}", teamListUrlS);
				
				doc = Jsoup.parse(new URL(teamListUrlR).openStream(), "UTF-8", teamListUrlR);
				aflPlayers.addAll(extractPlayers(team.getTeamId(), doc));
				
				loggerUtils.log("info", "Rookies added to list");
			} else {
				loggerUtils.log("info", "No rookie list");
			}
		}
		
		loggerUtils.log("info", "Saving players to database ...");
		aflPlayerService.insertAll(aflPlayers, false);
	}
	
	private List<AflPlayer> extractPlayers(String teamId, Document doc) throws Exception {
		
		List<AflPlayer> aflPlayers = new ArrayList<>();
		
		Element playerListTable = doc.getElementById("tlist").getElementsByTag("tbody").get(0);
		
		Elements playerRecs = playerListTable.getElementsByTag("tr");
		for(Element playerRec : playerRecs) {
			
			AflPlayer aflPlayer = new AflPlayer();
			
			Elements playerData = playerRec.getElementsByTag("td");
			
			aflPlayer.setTeamId(teamId);
			aflPlayer.setJumperNo(Integer.parseInt(playerData.get(0).text()));
			aflPlayer.setPlayerId(aflPlayer.getTeamId()+aflPlayer.getJumperNo());
			
			String []name = playerData.get(1).text().split(" ");
			aflPlayer.setFirstName(name[0]);
			aflPlayer.setSecondName(name[1]);
			
			aflPlayer.setHeight(Integer.parseInt(playerData.get(2).text()));
			aflPlayer.setWeight(Integer.parseInt(playerData.get(3).text()));
			aflPlayer.setDob(df.parse(playerData.get(4).text()));
			
			loggerUtils.log("info", "Extraced player data: {}", aflPlayer);
			
			aflPlayers.add(aflPlayer);
		}
		
		return aflPlayers;
	}
	
	public static void main(String[] args) {
		
		AflPlayerLoader aflPlayerLoader = new AflPlayerLoader();
		aflPlayerLoader.execute();
		
	}
	

}
