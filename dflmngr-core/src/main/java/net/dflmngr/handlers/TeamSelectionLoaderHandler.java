package net.dflmngr.handlers;

import java.util.ArrayList;
import java.util.List;

import net.dflmngr.model.DomainDecodes;
import net.dflmngr.model.entity.InsAndOuts;
import net.dflmngr.model.service.InsAndOutsService;
import net.dflmngr.model.service.impl.InsAndOutsServiceImpl;

public class TeamSelectionLoaderHandler {
	
	InsAndOutsService insAndOutsService;
	
	public TeamSelectionLoaderHandler() throws Exception {
		insAndOutsService = new InsAndOutsServiceImpl();
	}
	
	public void execute(String teamCode, int round, List<Integer> ins, List<Integer> outs) throws Exception {
			
		List<InsAndOuts> insAndOuts = new ArrayList<>();
		
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
		
		insAndOutsService.saveTeamInsAndOuts(insAndOuts);
		
		insAndOutsService.close();
	}
}