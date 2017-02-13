package cn.newphy.commons.data.generator.argument.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.newphy.commons.data.generator.CRUDDao.ExistException;
import cn.newphy.commons.data.generator.CRUDDao.Existable;
import cn.newphy.commons.data.generator.argument.dao.ArgDao;
import cn.newphy.commons.data.generator.model.arg.Argument;

@Service
public class ArgService {

	@Autowired
	private ArgDao argDao;
	
	/**
	 * 获得DS
	 * @param argId
	 * @return
	 */
	public Argument getArgument(String argId) {
		return argDao.get(argId);
	}
	
	/**
	 * 获得DS
	 * @param argId
	 * @return
	 */
	public void deleteArgument(String id) {
		argDao.delete(id);
	}
	
	/**
	 * 查询所有Argument
	 * @return
	 */
	public List<Argument> queryArgument() {
		List<Argument> args = argDao.query();
		return args;
	}
	
	/**
	 * 增加Argument
	 * @param arg
	 * @throws ExistException 
	 */
	public void addArgument(final Argument arg) throws ExistException {
		argDao.save(arg, new Existable<Argument>() {
			@Override
			public boolean checkExist(Argument t) {
				return arg != null && t != null && arg.getName().equals(t.getName());
			}
		});
	}
	
	
	/**
	 * 修改Argument
	 * @param arg
	 */
	public void editArgument(Argument arg) {
		argDao.update(arg);
	}

}
