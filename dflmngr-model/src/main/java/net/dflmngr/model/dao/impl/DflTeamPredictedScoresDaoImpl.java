package net.dflmngr.model.dao.impl;

import java.util.List;

import javax.persistence.criteria.Predicate;

import net.dflmngr.model.dao.DflTeamPredictedScoresDao;
import net.dflmngr.model.entity.DflTeamPredictedScores;
import net.dflmngr.model.entity.DflTeamPredictedScores_;
import net.dflmngr.model.entity.keys.DflTeamPredictedScoresPK;

public class DflTeamPredictedScoresDaoImpl extends GenericDaoImpl<DflTeamPredictedScores, DflTeamPredictedScoresPK>	implements DflTeamPredictedScoresDao {
	public List<DflTeamPredictedScores> findForRound(int round) {
		criteriaBuilder = entityManager.getCriteriaBuilder();
		criteriaQuery = criteriaBuilder.createQuery(entityClass);
		entity = criteriaQuery.from(entityClass);
		
		Predicate equals = criteriaBuilder.equal(entity.get(DflTeamPredictedScores_.round), round);
		
		criteriaQuery.where(criteriaBuilder.and(equals));
		List<DflTeamPredictedScores> entitys = entityManager.createQuery(criteriaQuery).getResultList();
		
		return entitys;
	}
	
	public List<DflTeamPredictedScores> findAllForRound(int round) {
		
		criteriaBuilder = entityManager.getCriteriaBuilder();
		criteriaQuery = criteriaBuilder.createQuery(entityClass);
		entity = criteriaQuery.from(entityClass);
		
		Predicate equals = criteriaBuilder.equal(entity.get(DflTeamPredictedScores_.round), round);
		
		criteriaQuery.where(criteriaBuilder.and(equals));
		List<DflTeamPredictedScores> entitys = entityManager.createQuery(criteriaQuery).getResultList();
		
		return entitys;
	}
}
