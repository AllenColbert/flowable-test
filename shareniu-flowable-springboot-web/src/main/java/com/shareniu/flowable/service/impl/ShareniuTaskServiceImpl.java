package com.shareniu.flowable.service.impl;

import com.shareniu.flowable.dao.TaskDao;
import com.shareniu.flowable.entity.TaskAPIData;
import com.shareniu.flowable.entity.TaskData;
import com.shareniu.flowable.service.ShareniuTaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class ShareniuTaskServiceImpl  implements ShareniuTaskService{
	@Autowired
	private TaskDao taskDao;
	@Override
	public List<TaskData> taskListPage(String userId) {
		return taskDao.queryByUserIdListPage(userId);
	}

	@Override
	public List<TaskAPIData> queryByUserIdPage(String userId) {
		return taskDao.queryByUserIdPage(userId);
	}

}
