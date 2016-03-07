package net.dflmngr.model.service;

import java.util.List;

public interface GenericService<E, K> {
	
	public E get(K id);
	public List<E> findAll();
	public void insert(E entity);
	public void insertAll(List<E> entitys, boolean inTx);
	public void delete(E entity);
	public void replaceAll(List<E> entitys);
	public void close();

}
