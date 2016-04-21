package net.dflmngr.model.dao.impl;

import java.util.List;

import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;
import javax.persistence.criteria.Expression;

import net.dflmngr.model.dao.DflMatthewAllenDao;
import net.dflmngr.model.entity.DflMatthewAllen;
import net.dflmngr.model.entity.DflMatthewAllen_;

public class DflMatthewAllenDaoImpl extends GenericDaoImpl<DflMatthewAllen, Integer> implements DflMatthewAllenDao {
	public List<DflMatthewAllen> findForRound(int round) {
		criteriaBuilder = entityManager.getCriteriaBuilder();
		criteriaQuery = criteriaBuilder.createQuery(entityClass);
		entity = criteriaQuery.from(entityClass);
		
		Predicate equals = criteriaBuilder.equal(entity.get(DflMatthewAllen_.round), round);
		
		criteriaQuery.where(equals);
		List<DflMatthewAllen> entitys = entityManager.createQuery(criteriaQuery).getResultList();
		
		return entitys;
	}
	
	public DflMatthewAllen findLastVotes(int playerId) {
		criteriaBuilder = entityManager.getCriteriaBuilder();
		criteriaQuery = criteriaBuilder.createQuery(entityClass);
		entity = criteriaQuery.from(entityClass);
		
		Subquery<Integer> subQuery = criteriaQuery.subquery(Integer.class);
		Root<DflMatthewAllen> subEntity = subQuery.from(entityClass);
		
		Predicate subEquals = criteriaBuilder.equal(subEntity.get(DflMatthewAllen_.playerId), entity.get(DflMatthewAllen_.playerId));
		Expression<Integer> max = criteriaBuilder.greatest(subEntity.get(DflMatthewAllen_.round));
		subQuery.select(max);
		subQuery.where(subEquals);
		
		Predicate playerIdEquals = criteriaBuilder.equal(entity.get(DflMatthewAllen_.playerId), playerId);
		Predicate subQureyEquals = criteriaBuilder.equal(entity.get(DflMatthewAllen_.round), subQuery);
		criteriaQuery.where(criteriaBuilder.and(playerIdEquals, subQureyEquals));
		
		DflMatthewAllen lastVotes = null;
		
		List<DflMatthewAllen> lastVotesResults = entityManager.createQuery(criteriaQuery).getResultList();
		
		if(lastVotesResults != null && !lastVotesResults.isEmpty()) {
			lastVotes = lastVotesResults.get(0); 
		}
		
		return lastVotes;
	}
	
	public void removeForRound(int round) {
		criteriaBuilder = entityManager.getCriteriaBuilder();
		criteriaDelete = criteriaBuilder.createCriteriaDelete(entityClass);
		entity = criteriaDelete.from(entityClass);
		
		Predicate equals = criteriaBuilder.equal(entity.get(DflMatthewAllen_.round), round);
		criteriaDelete.where(equals);
		
		entityManager.createQuery(criteriaDelete).executeUpdate();
	}
}
