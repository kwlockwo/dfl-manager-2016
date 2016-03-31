package net.dflmngr.model.dao.impl;

import java.util.List;

import javax.persistence.criteria.Predicate;

import net.dflmngr.model.dao.DflFixtureDao;
import net.dflmngr.model.entity.DflFixture;
import net.dflmngr.model.entity.DflFixture_;
import net.dflmngr.model.entity.keys.DflFixturePK;

public class DflFixtureDaoImpl extends GenericDaoImpl<DflFixture, DflFixturePK> implements DflFixtureDao {
	
	public List<DflFixture> findAflFixturesForRound(int round) {
		
		criteriaBuilder = entityManager.getCriteriaBuilder();
		criteriaQuery = criteriaBuilder.createQuery(entityClass);
		entity = criteriaQuery.from(entityClass);
		
		Predicate equals = criteriaBuilder.equal(entity.get(DflFixture_.round), round);
		
		criteriaQuery.where(criteriaBuilder.and(equals));
		List<DflFixture> entitys = entityManager.createQuery(criteriaQuery).getResultList();
		
		return entitys;
	}
}
