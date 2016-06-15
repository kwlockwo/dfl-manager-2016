package net.dflmngr.model.service;

import java.util.List;

import net.dflmngr.model.entity.DflEarlyInsAndOuts;
import net.dflmngr.model.entity.keys.DflEarlyInsAndOutsPK;

public interface DflEarlyInsAndOutsService extends GenericService<DflEarlyInsAndOuts, DflEarlyInsAndOutsPK> {
	public List<DflEarlyInsAndOuts> getByTeamAndRound(int round, String teamCode);
	public void saveTeamInsAndOuts(List<DflEarlyInsAndOuts> insAndOuts);
}
