package com.itheima.bos.dao.base.impl;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import javax.annotation.Resource;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;
import com.itheima.bos.dao.base.IBaseDao;

/**
 * 持久层通用实现
 * 
 * @author zhaoqx
 * 
 */
public class BaseDaoImpl<T> extends HibernateDaoSupport implements IBaseDao<T> {
	// 实体类型
	private Class<T> entityClass;

	// 使用注解方式进行依赖注入
	@Resource
	// @Autowired
	// @Qualifier(value="abc")
	public void setMySessionFactory(SessionFactory sessionFactory) {
		super.setSessionFactory(sessionFactory);
	}

	/**
	 * 在构造方法中动态获得操作的实体类型
	 */
	public BaseDaoImpl() {
		// 获得父类（BaseDaoImpl<T>）类型
		ParameterizedType genericSuperclass = (ParameterizedType) this
				.getClass().getGenericSuperclass();
		// 获得父类上的泛型数组
		Type[] actualTypeArguments = genericSuperclass.getActualTypeArguments();
		entityClass = (Class<T>) actualTypeArguments[0];
	}

	public void save(T entity) {
		this.getHibernateTemplate().save(entity);
	}

	public void delete(T entity) {
		this.getHibernateTemplate().delete(entity);
	}

	public void update(T entity) {
		this.getHibernateTemplate().update(entity);
	}

	public T findById(Serializable id) {
		return this.getHibernateTemplate().get(entityClass, id);
	}

	public List<T> findAll() {// FROM User
		String hql = "FROM  " + entityClass.getSimpleName();
		return this.getHibernateTemplate().find(hql);
	}

	/**
	 * 通用更新方法
	 */
	public void executeUpdate(String queryName, Object... objects) {
		Session session = this.getSession();// 从本地线程中获得session对象
		// 使用命名查询语句获得一个查询对象
		Query query = session.getNamedQuery(queryName);
		// 为HQL语句中的？赋值
		int i = 0;
		for (Object arg : objects) {
			query.setParameter(i++, arg);
		}
		query.executeUpdate();// 执行更新
	}
}
