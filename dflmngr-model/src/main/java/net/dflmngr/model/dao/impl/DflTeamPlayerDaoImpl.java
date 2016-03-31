package net.dflmngr.model.dao.impl;

import java.util.List;

import javax.persistence.criteria.Predicate;

import net.dflmngr.model.dao.DflTeamPlayerDao;
import net.dflmngr.model.entity.DflTeamPlayer;
import net.dflmngr.model.entity.DflTeamPlayer_;

public class DflTeamPlayerDaoImpl extends GenericDaoImpl<DflTeamPlayer, Integer> implements DflTeamPlayerDao {
	
	
	public DflTeamPlayer findTeamPlayerForTeam(String teamCode, int teamPlayerId) {
		
		DflTeamPlayer teamPlayer = null;
		
		criteriaBuilder = entityManager.getCriteriaBuilder();
		criteriaQuery = criteriaBuilder.createQuery(entityClass);
		entity = criteriaQuery.from(entityClass);
		
		Predicate roundEquals = criteriaBuilder.equal(entity.get(DflTeamPlayer_.teamCode), teamCode);
		Predicate temaCodeEquals = criteriaBuilder.equal(entity.get(DflTeamPlayer_.teamPlayerId), teamPlayerId);
		
		criteriaQuery.where(criteriaBuilder.and(roundEquals, temaCodeEquals));
		List<DflTeamPlayer> entitys = entityManager.createQuery(criteriaQuery).getResultList();
		
		if(!entitys.isEmpty()) {
			teamPlayer = entitys.get(0);
		}
		
		return teamPlayer;
	}
	
}
