package cn.ehai.log.dao;

import cn.ehai.log.entity.ActionLog;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface ActionLogMapper {
	int insert(ActionLog record);

	ActionLog selectByPrimaryKey(Integer id);

	List<ActionLog> selectAll();

	List<Map<String, String>> selectBySql(@Param("sql") String sql);

	int updateByPrimaryKey(ActionLog record);
}