package com.power.service;

import com.power.entity.User;

import java.util.List;


public interface UserIService {

	void insert(User user);

	List<User> findList();
}
