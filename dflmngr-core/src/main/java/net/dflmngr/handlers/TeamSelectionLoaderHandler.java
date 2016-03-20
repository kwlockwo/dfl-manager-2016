package net.dflmngr.handlers;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import net.dflmngr.model.DomainDecodes;
import net.dflmngr.model.entity.InsAndOuts;
import net.dflmngr.model.service.InsAndOutsService;
import net.dflmngr.model.service.impl.InsAndOutsServiceImpl;

public class TeamSelectionLoaderHandler {
	private Logger logger;
	
	InsAndOutsService insAndOutsService;
	
	public TeamSelectionLoaderHandler() throws Exception {
		
		MDC.put("online.name", "Selections");
		logger = LoggerFactory.getLogger("online-logger");
		
		insAndOutsService = new InsAndOutsServiceImpl();
	}
	
	public void execute(String teamCode, int round, List<Integer> ins, List<Integer> outs) {
		
		try {
			List<InsAndOuts> insAndOuts = new ArrayList<>();
			
			logger.info("Processing ins and out selections for: teamCode={}; round={}; ins={}; outs={};", teamCode, round, ins, outs);
			
			for(Integer i : ins) {
				InsAndOuts in = new InsAndOuts();
				in.setRound(round);
				in.setTeamCode(teamCode);
				in.setTeamPlayerId(i);
				in.setInOrOut(DomainDecodes.INS_AND_OUTS.IN_OR_OUT.IN);
				
				insAndOuts.add(in);
			}
			
			for(Integer o : outs) {
				InsAndOuts out = new InsAndOuts();
				out.setRound(round);
				out.setTeamCode(teamCode);
				out.setTeamPlayerId(o);
				out.setInOrOut(DomainDecodes.INS_AND_OUTS.IN_OR_OUT.OUT);
				
				insAndOuts.add(out);
			}
			
			logger.info("Saving ins and outs to database: ", insAndOuts);
			insAndOutsService.saveTeamInsAndOuts(insAndOuts);
			logger.info("Ins and outs saved");
			
			insAndOutsService.close();
			
		} catch (Exception ex) {
			logger.error("Error in ... ", ex);
		} finally {
			MDC.remove("online.name");
		}	
	}
}