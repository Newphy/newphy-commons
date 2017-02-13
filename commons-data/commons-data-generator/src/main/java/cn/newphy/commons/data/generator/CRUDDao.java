package cn.newphy.commons.data.generator;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.mapdb.DB;

import cn.newphy.commons.data.generator.model.IdEntity;
import cn.newphy.commons.data.generator.utils.JsonUtils;
import cn.newphy.commons.data.generator.utils.ReflectUtils;

public abstract class CRUDDao<T extends IdEntity> extends MapDBDao {

	private final Class<T> entityClass;
	
	public CRUDDao() {
		this.entityClass = ReflectUtils.getSuperClassGenericType(this.getClass());
	}
	
	public  List<T> query() {
		DB db = getDB();
		try {
			String json = db.catGet(getKey());
			if(json != null) {
				try {
					List<T> entities = JsonUtils.readList(json, entityClass);
					return entities;
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			return null;
		} 
		finally {
			close(db);
		}
	}
	
	public T get(String id) {
		if(id == null || id.length() == 0) {
			return null;
		}
		List<T> entities = query();
		if(entities != null) {
			for (T entity : entities) {
				if(id.equals(entity.getId())) {
					return entity;
				}
			}
		}
		return null;
	}

	public void save(T entity, Existable<T> existable) throws ExistException {
		entity.setId(UUID.randomUUID().toString());
		List<T> entities = query();
		if(entities == null) {
			entities = new ArrayList<T>();
		}
		if(existable != null && entities != null) {
			for (T e : entities) {
				if(existable.checkExist(e)) {
					throw new ExistException();
				}
			}
		}
		entities.add(entity);
		doSave(entities);
	}
	
	public void save(T entity) {
		try {
			save(entity, null);
		} catch (ExistException e) {
		}
	}
	
	public void update(T entity) {
		if(entity == null) {
			return;
		}
		List<T> entities = query();
		if(entities != null) {
			for(int i = 0; i < entities.size(); i++) {
				T e = entities.get(i);
				if(e.getId().equals(entity.getId())) {
					entities.set(i, entity);
				}
			}
			doSave(entities);
		}
	}
	
	public void delete(String id) {
		if(id == null) {
			return;
		}
		List<T> entities = query();
		if(entities != null) {
			for(int i = 0; i < entities.size(); i++) {
				T e = entities.get(i);
				if(id.equals(e.getId())) {
					entities.remove(i);
					break;
				}
			}
			doSave(entities);
		}
	}

	
	
	private void doSave(List<T> entities) {
		DB db = null;
		try {
			db = getDB();
			db.catPut(getKey(), JsonUtils.toJSON(entities));
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			close(db);
		}
	}
	

	
	private String getKey() {
		return entityClass.getName();
	}


	
	public interface Existable<T>  {
		boolean checkExist(T t);
	}
	
	@SuppressWarnings("serial")
	public static class ExistException extends Exception {
		
	}
}
