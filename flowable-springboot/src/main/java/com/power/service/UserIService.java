package com.power.service;

import com.power.bean.User;

import java.util.List;


public interface UserIService {

	void insert(User user);

	List<User> findList();
}
