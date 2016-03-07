package net.dflmngr.model.dao.impl;

import java.util.List;

import javax.persistence.criteria.Predicate;

import net.dflmngr.model.dao.InsAndOutsDao;
import net.dflmngr.model.entity.InsAndOuts;
import net.dflmngr.model.entity.InsAndOuts_;
import net.dflmngr.model.entity.keys.InsAndOutsPK;

public class InsAndOutsDaoImpl extends GenericDaoImpl<InsAndOuts, InsAndOutsPK>implements InsAndOutsDao {
	
	public List<InsAndOuts> findByTeamAndRound(int round, String teamCode) {
		criteriaBuilder = entityManager.getCriteriaBuilder();
		criteriaQuery = criteriaBuilder.createQuery(entityClass);
		entity = criteriaQuery.from(entityClass);
		
		Predicate roundEquals = criteriaBuilder.equal(entity.get(InsAndOuts_.round), round);
		Predicate temaCodeEquals = criteriaBuilder.equal(entity.get(InsAndOuts_.teamCode), teamCode);
		
		criteriaQuery.where(criteriaBuilder.and(roundEquals, temaCodeEquals));
		List<InsAndOuts> entitys = entityManager.createQuery(criteriaQuery).getResultList();
		
		return entitys;
	}
}
