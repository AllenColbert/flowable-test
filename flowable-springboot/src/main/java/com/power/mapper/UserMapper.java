package com.power.mapper;

import com.power.entity.User;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface UserMapper {

	void insert(User user);

	void update(User user);

	void delete(int id);

	User find(int id);

	List<User> findList();
}
