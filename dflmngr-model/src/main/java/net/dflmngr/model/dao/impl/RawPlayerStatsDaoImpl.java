package net.dflmngr.model.dao.impl;

import java.util.List;

import javax.persistence.criteria.Predicate;

import net.dflmngr.model.dao.RawPlayerStatsDao;
import net.dflmngr.model.entity.RawPlayerStats;
import net.dflmngr.model.entity.RawPlayerStats_;
import net.dflmngr.model.entity.keys.RawPlayerStatsPK;

public class RawPlayerStatsDaoImpl extends GenericDaoImpl<RawPlayerStats, RawPlayerStatsPK> implements RawPlayerStatsDao {
	
	public List<RawPlayerStats> findForRound(int round) {
		criteriaBuilder = entityManager.getCriteriaBuilder();
		criteriaQuery = criteriaBuilder.createQuery(entityClass);
		entity = criteriaQuery.from(entityClass);
		
		Predicate roundEquals = criteriaBuilder.equal(entity.get(RawPlayerStats_.round), round);
		
		criteriaQuery.where(criteriaBuilder.and(roundEquals));
		List<RawPlayerStats> entitys = entityManager.createQuery(criteriaQuery).getResultList();
		
		return entitys;
	}
}
