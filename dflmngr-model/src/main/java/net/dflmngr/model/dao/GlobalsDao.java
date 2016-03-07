package net.dflmngr.model.dao;

import java.util.List;

import net.dflmngr.model.entity.Globals;
import net.dflmngr.model.entity.keys.GlobalsPK;

public interface GlobalsDao extends GenericDao<Globals, GlobalsPK> {
	public List<Globals> findCodesForGroup(String groupCode);
}
