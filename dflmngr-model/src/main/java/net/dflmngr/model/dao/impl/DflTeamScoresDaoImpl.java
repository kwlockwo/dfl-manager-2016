package net.dflmngr.model.dao.impl;

import java.util.List;

import javax.persistence.criteria.Predicate;

import net.dflmngr.model.dao.DflTeamScoresDao;
import net.dflmngr.model.entity.DflTeamScores;
import net.dflmngr.model.entity.DflTeamScores_;
import net.dflmngr.model.entity.keys.DflTeamScoresPK;

public class DflTeamScoresDaoImpl extends GenericDaoImpl<DflTeamScores, DflTeamScoresPK> implements DflTeamScoresDao {
	public List<DflTeamScores> findForRound(int round) {
		criteriaBuilder = entityManager.getCriteriaBuilder();
		criteriaQuery = criteriaBuilder.createQuery(entityClass);
		entity = criteriaQuery.from(entityClass);
		
		Predicate equals = criteriaBuilder.equal(entity.get(DflTeamScores_.round), round);
		
		criteriaQuery.where(criteriaBuilder.and(equals));
		List<DflTeamScores> entitys = entityManager.createQuery(criteriaQuery).getResultList();
		
		return entitys;
	}
}
