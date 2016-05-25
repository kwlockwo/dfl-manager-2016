package net.dflmngr.model.dao.impl;

import java.util.List;

import javax.persistence.criteria.Predicate;

import net.dflmngr.model.dao.DflPlayerPredictedScoresDao;
import net.dflmngr.model.entity.DflPlayerPredictedScores;
import net.dflmngr.model.entity.DflPlayerPredictedScores_;
import net.dflmngr.model.entity.keys.DflPlayerPredictedScoresPK;

public class DflPlayerPredictedScoresDaoImpl extends GenericDaoImpl<DflPlayerPredictedScores, DflPlayerPredictedScoresPK> implements DflPlayerPredictedScoresDao {
	
	public List<DflPlayerPredictedScores> findForRound(int round) {
		criteriaBuilder = entityManager.getCriteriaBuilder();
		criteriaQuery = criteriaBuilder.createQuery(entityClass);
		entity = criteriaQuery.from(entityClass);
		
		Predicate equals = criteriaBuilder.equal(entity.get(DflPlayerPredictedScores_.round), round);
		
		criteriaQuery.where(equals);
		List<DflPlayerPredictedScores> entitys = entityManager.createQuery(criteriaQuery).getResultList();
		
		return entitys;
	}
}
