package net.dflmngr.model.dao.impl;

import java.util.List;

import javax.persistence.criteria.Predicate;

import net.dflmngr.model.dao.AflFixtureDao;
import net.dflmngr.model.entity.AflFixture;
import net.dflmngr.model.entity.AflFixture_;
import net.dflmngr.model.entity.keys.AflFixturePK;

public class AflFixtureDaoImpl extends GenericDaoImpl<AflFixture, AflFixturePK>implements AflFixtureDao {
	
	public List<AflFixture> findAflFixturesForRound(int round) {
		
		criteriaBuilder = entityManager.getCriteriaBuilder();
		criteriaQuery = criteriaBuilder.createQuery(entityClass);
		entity = criteriaQuery.from(entityClass);
		
		Predicate roundEquals = criteriaBuilder.equal(entity.get(AflFixture_.round), round);
		
		criteriaQuery.where(criteriaBuilder.and(roundEquals));
		List<AflFixture> entitys = entityManager.createQuery(criteriaQuery).getResultList();
		
		return entitys;
	}
	
}
