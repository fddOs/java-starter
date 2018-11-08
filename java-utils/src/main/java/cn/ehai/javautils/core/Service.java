package cn.ehai.javautils.core;

import java.util.List;

/**
 * Service 层 基础接口，其他Service 接口 请继承该接口
 */
public interface Service<T> {
	int save(T model);// 持久化

	int update(T model);// 更新

	T findById(Integer id);// 通过ID查找

	List<T> findAll();// 获取所有
}
