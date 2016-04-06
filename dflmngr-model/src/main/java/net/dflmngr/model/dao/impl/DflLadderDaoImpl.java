package net.dflmngr.model.dao.impl;

import java.util.List;

import javax.persistence.criteria.Predicate;

import net.dflmngr.model.dao.DflLadderDao;
import net.dflmngr.model.entity.DflLadder;
import net.dflmngr.model.entity.DflLadder_;
import net.dflmngr.model.entity.keys.DflLadderPK;

public class DflLadderDaoImpl extends GenericDaoImpl<DflLadder, DflLadderPK> implements DflLadderDao {
	
	public List<DflLadder> findLadderForRound(int round) {
		criteriaBuilder = entityManager.getCriteriaBuilder();
		criteriaQuery = criteriaBuilder.createQuery(entityClass);
		entity = criteriaQuery.from(entityClass);
		
		Predicate equals = criteriaBuilder.equal(entity.get(DflLadder_.round), round);
		
		criteriaQuery.where(criteriaBuilder.and(equals));
		List<DflLadder> entitys = entityManager.createQuery(criteriaQuery).getResultList();
		
		return entitys;
	}
}

