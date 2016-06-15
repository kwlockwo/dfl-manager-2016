package net.dflmngr.model.dao.impl;

import java.util.List;

import javax.persistence.criteria.Predicate;

import net.dflmngr.model.dao.DflEarlyInsAndOutsDao;
import net.dflmngr.model.entity.DflEarlyInsAndOuts;
import net.dflmngr.model.entity.DflEarlyInsAndOuts_;
import net.dflmngr.model.entity.keys.DflEarlyInsAndOutsPK;

public class DflEarlyInsAndOutsDaoImpl extends GenericDaoImpl<DflEarlyInsAndOuts, DflEarlyInsAndOutsPK>	implements DflEarlyInsAndOutsDao {
	
	public List<DflEarlyInsAndOuts> findByTeamAndRound(int round, String teamCode) {
		criteriaBuilder = entityManager.getCriteriaBuilder();
		criteriaQuery = criteriaBuilder.createQuery(entityClass);
		entity = criteriaQuery.from(entityClass);
		
		Predicate roundEquals = criteriaBuilder.equal(entity.get(DflEarlyInsAndOuts_.round), round);
		Predicate temaCodeEquals = criteriaBuilder.equal(entity.get(DflEarlyInsAndOuts_.teamCode), teamCode);
		
		criteriaQuery.where(criteriaBuilder.and(roundEquals, temaCodeEquals));
		List<DflEarlyInsAndOuts> entitys = entityManager.createQuery(criteriaQuery).getResultList();
		
		return entitys;
	}
}
