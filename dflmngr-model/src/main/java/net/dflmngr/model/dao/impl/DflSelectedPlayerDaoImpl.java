package net.dflmngr.model.dao.impl;

import java.util.List;

import javax.persistence.criteria.Predicate;

import net.dflmngr.model.dao.DflSelectedPlayerDao;
import net.dflmngr.model.entity.DflSelectedPlayer;
import net.dflmngr.model.entity.DflSelectedPlayer_;
import net.dflmngr.model.entity.keys.DflSelectedPlayerPK;

public class DflSelectedPlayerDaoImpl extends GenericDaoImpl<DflSelectedPlayer, DflSelectedPlayerPK> implements DflSelectedPlayerDao {
	
	
	public List<DflSelectedPlayer> findSelectedTeamForRound(int round, String teamCode) {
		
		criteriaBuilder = entityManager.getCriteriaBuilder();
		criteriaQuery = criteriaBuilder.createQuery(entityClass);
		entity = criteriaQuery.from(entityClass);
		
		Predicate roundEquals = criteriaBuilder.equal(entity.get(DflSelectedPlayer_.round), round);
		Predicate temaCodeEquals = criteriaBuilder.equal(entity.get(DflSelectedPlayer_.teamCode), teamCode);
		
		criteriaQuery.where(criteriaBuilder.and(roundEquals, temaCodeEquals));
		List<DflSelectedPlayer> entitys = entityManager.createQuery(criteriaQuery).getResultList();
		
		return entitys;
	}
	
	public List<DflSelectedPlayer> findAllForRound(int round) {
		
		criteriaBuilder = entityManager.getCriteriaBuilder();
		criteriaQuery = criteriaBuilder.createQuery(entityClass);
		entity = criteriaQuery.from(entityClass);
		
		Predicate equals = criteriaBuilder.equal(entity.get(DflSelectedPlayer_.round), round);
		
		criteriaQuery.where(criteriaBuilder.and(equals));
		List<DflSelectedPlayer> entitys = entityManager.createQuery(criteriaQuery).getResultList();
		
		return entitys;
	}
}
