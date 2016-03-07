package net.dflmngr.model.dao.impl;

import java.util.List;

import javax.persistence.criteria.Predicate;

import net.dflmngr.model.dao.GlobalsDao;
import net.dflmngr.model.entity.Globals;
import net.dflmngr.model.entity.Globals_;
import net.dflmngr.model.entity.keys.GlobalsPK;

public class GlobalsDaoImpl extends GenericDaoImpl<Globals, GlobalsPK> implements GlobalsDao {
	
	public List<Globals> findCodesForGroup(String groupCode) {
		criteriaBuilder = entityManager.getCriteriaBuilder();
		criteriaQuery = criteriaBuilder.createQuery(entityClass);
		entity = criteriaQuery.from(entityClass);
		
		Predicate groupCodeEquals = criteriaBuilder.equal(entity.get(Globals_.groupCode), groupCode);
		
		criteriaQuery.where(criteriaBuilder.and(groupCodeEquals));
		List<Globals> entitys = entityManager.createQuery(criteriaQuery).getResultList();
		
		return entitys;
	}
	
}
