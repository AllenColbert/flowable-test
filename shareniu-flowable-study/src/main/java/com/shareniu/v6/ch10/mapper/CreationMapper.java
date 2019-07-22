package com.shareniu.v6.ch10.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.shareniu.v6.ch10.model.ActCreation;

/**
 * private String processInstanceId; private String processText; private Integer
 * state;
 * 
 *
 */
@Mapper
public interface CreationMapper {
	@Select("select * from act_creation where STATE_=0 and PROCESS_INSTANCE_ID=#{processInstanceId}")
	@Results({ @Result(property = "processDefinitionId", column = "process_definition_id"),
			@Result(property = "id", column = "id"), @Result(property = "douUerId", column = "DOUSERID"),
			// @Result(property = "douUerId", column = "DOUSERID") ,
			@Result(property = "actId", column = "ACT_ID"),
			@Result(property = "processInstanceId", column = "PROCESS_INSTANCE_ID"),
			@Result(property = "processText", column = "PROPERTIES_TEXT"),
			@Result(property = "state", column = "STATE_")

	})
	public List<ActCreation> find(String processInstanceId);

	@Select("select * from act_creation where STATE_=1 and  PROCESS_INSTANCE_ID=#{processInstanceId} order by create_time asc")
	@Results({ @Result(property = "processDefinitionId", column = "process_definition_id"),
			@Result(property = "id", column = "id"), @Result(property = "douUerId", column = "DOUSERID"),
			// @Result(property = "douUerId", column = "DOUSERID") ,
			@Result(property = "actId", column = "ACT_ID"),
			@Result(property = "processInstanceId", column = "PROCESS_INSTANCE_ID"),
			@Result(property = "processText", column = "PROPERTIES_TEXT"),
			@Result(property = "state", column = "STATE_")

	})
	public List<ActCreation> findLastOne(String processInstanceId);

	@Insert("insert into act_creation(PROCESS_DEFINITION_ID, PROCESS_INSTANCE_ID,PROPERTIES_TEXT,create_time) values(#{processDefinitionId}, #{processInstanceId}, #{processText},now())")
	public void insert(ActCreation actCreation);

	@Update("update act_creation set STATE_ = 1 where STATE_=0 and  PROCESS_INSTANCE_ID=#{processInstanceId}")
	void updateState(String processInstanceId);
	
	
	
	
	@Select("select * from act_creation where STATE_=0 ")
	@Results({ @Result(property = "processDefinitionId", column = "process_definition_id"),
			@Result(property = "id", column = "id"), @Result(property = "douUerId", column = "DOUSERID"),
			// @Result(property = "douUerId", column = "DOUSERID") ,
			@Result(property = "actId", column = "ACT_ID"),
			@Result(property = "processInstanceId", column = "PROCESS_INSTANCE_ID"),
			@Result(property = "processText", column = "PROPERTIES_TEXT"),
			@Result(property = "state", column = "STATE_")

	})
	public List<ActCreation> findAll();

}
