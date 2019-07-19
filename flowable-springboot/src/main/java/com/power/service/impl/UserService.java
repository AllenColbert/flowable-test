package com.power.service.impl;

import com.power.entity.User;
import com.power.mapper.UserMapper;
import com.power.service.UserIService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;


//@ComponentScan({"com.power.mapper"})
@Service
public class UserService implements UserIService {

	@Resource
	private UserMapper userMapper;

	@Override
	public void insert(User user) {
		userMapper.insert(user);
	}

	@Override
	public List<User> findList() {
		return userMapper.findList();
	}

	public void update(User user) {
		userMapper.update(user);
	}

	public User find(int id) {
		return userMapper.find(id);
	}

	public void delete(int id){
		userMapper.delete(id);
	}

}
