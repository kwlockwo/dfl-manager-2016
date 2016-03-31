package net.dflmngr.model.dao.impl;

import java.util.List;

import javax.persistence.criteria.Predicate;

import net.dflmngr.model.dao.DflPlayerScoresDao;
import net.dflmngr.model.entity.DflPlayerScores;
import net.dflmngr.model.entity.DflPlayerScores_;
import net.dflmngr.model.entity.keys.DflPlayerScoresPK;

public final class DflPlayerScoresDaoImpl extends GenericDaoImpl<DflPlayerScores, DflPlayerScoresPK> implements DflPlayerScoresDao {
	
	public List<DflPlayerScores> findForRound(int round) {
		criteriaBuilder = entityManager.getCriteriaBuilder();
		criteriaQuery = criteriaBuilder.createQuery(entityClass);
		entity = criteriaQuery.from(entityClass);
		
		Predicate equals = criteriaBuilder.equal(entity.get(DflPlayerScores_.round), round);
		
		criteriaQuery.where(criteriaBuilder.and(equals));
		List<DflPlayerScores> entitys = entityManager.createQuery(criteriaQuery).getResultList();
		
		return entitys;
	}
}
