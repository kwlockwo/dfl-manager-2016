package net.dflmngr.model.dao;

import java.util.List;

public interface GenericDao<E, K> {
	void persist(E entity);
	void remove(E enitiy);
	E findById(K id);
	public List<E> findAll();
	public void commit();
	public void beginTransaction();
	public void flush();
	public void close();
}
